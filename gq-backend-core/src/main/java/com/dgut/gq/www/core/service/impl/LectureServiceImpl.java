package com.dgut.gq.www.core.service.impl;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;
import com.dgut.gq.www.common.db.mapper.RecordRobTicketErrorMapper;
import com.dgut.gq.www.common.db.service.GqLectureService;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.core.common.config.RabbitmqConfig;
import com.dgut.gq.www.core.common.model.vo.LectureReviewVo;
import com.dgut.gq.www.core.common.model.vo.LectureTrailerVo;
import com.dgut.gq.www.core.common.model.vo.LectureVo;
import com.dgut.gq.www.core.common.mq.CustomCorrelationData;
import com.dgut.gq.www.core.common.util.RecordRobTicketErrorUtil;
import com.dgut.gq.www.core.service.LectureService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 讲座操作
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-16
 */
@Service
@Slf4j
public class LectureServiceImpl implements LectureService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RecordRobTicketErrorMapper recordRobTicketErrorMapper;

    @Autowired
    private GqLectureService gqLectureService;

    /**
     * 加载lua脚本代码
     *
     * @param voucherId
     * @return
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * 获取还没开始的讲座信息
     *
     * @return
     */
    @Override
    public SystemJsonResponse findUnStartLecture() {
        try {
            // 尝试从Redis获取讲座信息
            String key = RedisGlobalKey.UNSTART_LECTURE;
            String str = stringRedisTemplate.opsForValue().get(key);
            LectureVo lectureVo = Optional.ofNullable(str)
                    .map(s -> JSONUtil.toBean(s, LectureVo.class))
                    .orElseGet(() -> {
                        // 从数据库中查询最新未结束的讲座
                        Lecture lecture = gqLectureService.getLatestUnStartLecture();
                        // 检查是否查询到讲座
                        if (lecture == null || lecture.getLectureName() == null) {
                            throw new GlobalSystemException(999, "还没新的讲座");
                        }
                        // 转换为LectureVo并存入Redis
                        LectureVo newLectureVo = new LectureVo();
                        BeanUtils.copyProperties(lecture, newLectureVo);
                        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(newLectureVo));
                        stringRedisTemplate.opsForValue().set(RedisGlobalKey.TICKET_NUMBER, newLectureVo.getTicketNumber().toString());

                        return newLectureVo;
                    });

            // 获取票的数量
            String ticketNumberStr = Optional.ofNullable(stringRedisTemplate.opsForValue()
                            .get(RedisGlobalKey.TICKET_NUMBER))
                    .orElse(
                            String.valueOf(lectureVo.getTicketNumber())
                    );
            lectureVo.setTicketNumber(Integer.parseInt(ticketNumberStr));
            log.info("LectureServiceImpl findUnStartLecture data = {}", JSONUtil.toJsonStr(lectureVo));

            return SystemJsonResponse.success(lectureVo);
        } catch (Exception e) {
            log.error("LectureServiceImpl findUnStartLecture error", e);
            return SystemJsonResponse.fail();
        }
    }

    /**
     * 抢票
     *
     * @param lectureId
     * @param openid
     * @return SystemJsonResponse
     */
    @Override
    public SystemJsonResponse robTicket(String openid, String lectureId) {
        String key = RedisGlobalKey.UNSTART_LECTURE;
        String str = stringRedisTemplate.opsForValue().get(key);
        LocalDateTime grabTicketsStart = JSONUtil.toBean(str, LectureVo.class).getGrabTicketsStart();
        if (grabTicketsStart.isAfter(LocalDateTime.now())) {
            log.info("LectureServiceImpl robTicket 抢票时间未到， openid = {}, lectureId = {}", openid, lectureId);
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "抢票时间未到");
        }
        RLock lock = redissonClient.getLock(key + openid);
        try {
            boolean lockRes = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!lockRes) {
                log.info("LectureServiceImpl robTicket 抢票频繁 openid = {}, lectureId = {}", openid, lectureId);
                return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "抢票失败");
            }
        } catch (InterruptedException e) {
            log.error("LectureServiceImpl robTicket 获取锁异常 openid = {}, lectureId = {}", openid, lectureId, e);
            return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), "抢票失败");
        }
        try {
            //执行lua脚本
            Long execute = stringRedisTemplate.execute(SECKILL_SCRIPT, Collections.emptyList(), lectureId, openid);
            int re = execute.intValue();
            String msg = re == 1 ? "抢票成功" : "抢票失败";
            log.info("LectureServiceImpl robTicket openid = {}, lectureId = {}, msg = {}", openid, lectureId, msg);
            if (re != 0) {
                return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(), msg);
            }
            SendMsg(openid, lectureId);
        } catch (Exception e) {
            log.info("LectureServiceImpl robTicket 抢票失败 openid = {}, lectureId = {}", openid, lectureId, e);
        } finally {
            lock.unlock();
        }
        return SystemJsonResponse.success(GlobalResponseCode.OPERATE_SUCCESS.getCode(), "抢票成功");
    }

    @SuppressWarnings("all")
    private void SendMsg(String openid, String lectureId) {
        UserLectureInfo userLectureInfo = buildUserLectureInfo(openid, lectureId);
        CustomCorrelationData correlationData = new CustomCorrelationData(openid, lectureId);
        correlationData.getFuture().addCallback(new ListenableFutureCallback<CorrelationData.Confirm>() {
            @Override
            //springamqp内部处理future的异常（很少出现），不是投递失败
            public void onFailure(Throwable throwable) {
                log.error("发送消息失败！！", throwable);
            }

            @Override
            public void onSuccess(CorrelationData.Confirm result) {
                //Future接收到回执的处理逻辑，参数中的result就是回执内容
                if (result.isAck()) {
                    log.debug("LectureServiceImpl SendMsg 发送消息成功，收到 ack");
                } else { // result.getReason()，String类型，返回nack时的异常描述
                    log.error("LectureServiceImpl SendMsg 发送消息失败，收到 nack, reason = {}", result.getReason());
                    String openid;
                    String lectureId;
                    openid = correlationData.getOpenid();
                    lectureId = correlationData.getLectureId();
                    String key = RedisGlobalKey.SEND_EXCHANGE_FAIL + openid;
                    String str = stringRedisTemplate.opsForValue().get(key);
                    int value = 0;
                    if (str != null) {
                        value = Integer.parseInt(str);
                    }
                    // 重试了一次还失败就记录日志
                    if (value == 1) {
                        RecordRobTicketErrorUtil.recordError(recordRobTicketErrorMapper, openid, lectureId, 0);
                    } else {
                        stringRedisTemplate.opsForValue().set(key, String.valueOf(value + 1));
                        stringRedisTemplate.expire(key, 20, TimeUnit.MINUTES);
                        //重发消息
                        rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE, "gqyyds",
                                JSONUtil.toJsonStr(userLectureInfo), correlationData);
                    }
                }
            }
        });
        // 发送消息
        rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE, "gqyyds",
                JSONUtil.toJsonStr(userLectureInfo), correlationData);
    }

    private UserLectureInfo buildUserLectureInfo(String openid, String lectureId) {
        UserLectureInfo userLectureInfo = new UserLectureInfo();
        userLectureInfo.setUpdateTime(LocalDateTime.now());
        userLectureInfo.setCreateTime(LocalDateTime.now());
        userLectureInfo.setLectureId(lectureId);
        userLectureInfo.setOpenid(openid);
        return userLectureInfo;
    }

    /**
     * 获取讲座回顾
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public SystemJsonResponse getLectureReview(int page, int pageSize, String name) {
        try {
            Page<Lecture> pageInfo = gqLectureService.getLectures(page, pageSize, name, 0);
            log.info("LectureServiceImpl getLectureReview data = {}", JSONUtil.toJsonStr(pageInfo.getRecords()));
            List<LectureReviewVo> lectureVos = pageInfo.getRecords().stream()
                    .filter(record -> record.getReviewName() != null)
                    .map(record -> {
                        LectureReviewVo lectureVo = new LectureReviewVo();
                        BeanUtils.copyProperties(record, lectureVo);
                        return lectureVo;
                    })
                    .collect(Collectors.toList());
            return SystemJsonResponse.success(new SystemResultList<>(Collections.singletonList(lectureVos), (int) pageInfo.getTotal()));
        } catch (Exception e) {
            log.error("LectureServiceImpl getLectureReview error name = {}", name, e);
            return SystemJsonResponse.fail();
        }
    }

    /**
     * 讲座预告
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public SystemJsonResponse getLectureTrailer(int page, int pageSize, String name) {
        try {
            Page<Lecture> pageInfo = gqLectureService.getLectures(page, pageSize, name, 1);
            log.info("LectureServiceImpl getLectureTrailer data = {}", JSONUtil.toJsonStr(pageInfo.getRecords()));
            List<LectureTrailerVo> lectureVos = pageInfo.getRecords().stream()
                    .filter(record -> record.getLectureName() != null)
                    .map(record -> {
                        LectureTrailerVo lectureVo = new LectureTrailerVo();
                        BeanUtils.copyProperties(record, lectureVo);
                        return lectureVo;
                    })
                    .collect(Collectors.toList());
            return SystemJsonResponse.success(new SystemResultList<>(lectureVos, (int) pageInfo.getTotal()));
        } catch (Exception e) {
            log.error("LectureServiceImpl getLectureTrailer error name = {}", name, e);
            return SystemJsonResponse.fail();
        }
    }
}


