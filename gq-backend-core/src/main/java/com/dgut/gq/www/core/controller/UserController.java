package com.dgut.gq.www.core.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.common.util.ParseToken;
import com.dgut.gq.www.core.mapper.UserMapper;
import com.dgut.gq.www.core.model.vo.MyLectureVo;
import com.dgut.gq.www.core.service.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;


/**
 * 微信端用户接口
 * @version 1.0
 * @author  hyj
 * @since  2022-9-16
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
@Slf4j
public class UserController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;




    /**
     * cas client 默认的session key
     */
    public final static String CAS = "_const_cas_assertion_";

    @Autowired
    private UserService userService;


    @Autowired
    private UserMapper userMapper;

    /**
     * 用户实现微信登录
     * 返回token
     */
    @PostMapping("/wxLogin/{code}")
    @ApiOperation(value = "微信登录")
    @ApiImplicitParam(value = "微信code",name = "code",required = true)
    public SystemJsonResponse wxlogin(@PathVariable String code){
        String token = userService.wxLogin(code);
        return SystemJsonResponse.success(token);
    }








    /**
     * 获取微信端用户个人信息
     * @param request
     * @return
     */
    @GetMapping("/me")
    @ApiOperation(value = "微信端获取个人信息")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public  SystemJsonResponse getMyMessage(HttpServletRequest request){
        String token =  request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return  userService.getMe(openid);
    }

    /**
     * 获取自己参加过哪些讲座
     * @return
     */
    @GetMapping("/myLecture")
    @ApiOperation(value ="获取自己参加过的讲座")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页数",name = "page",required = true),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = MyLectureVo.class)
    }
    )
    public  SystemJsonResponse getMyLecture(HttpServletRequest request,Integer page,Integer pageSize){
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return userService.getMyLecture(openid,page,pageSize);
    }


    /**
     * 是否抢到票
     * @param request
     * @return
     */
    @GetMapping("/isGrabTicket")
    @ApiOperation(value = "是否抢到票")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public  SystemJsonResponse isGrabTicket(HttpServletRequest request){
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return userService.isGrabTicket(openid);
    }


    /**
     * 中央认证
     * @param request
     * @return
     */
    // @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "回调接口")
    @RequestMapping(value = "/getUserInfo",
            produces = { "application/json" },
            method = RequestMethod.GET)

    public SystemJsonResponse getLoginUserInfo(HttpServletRequest request,String id){
        User user1 = userMapper.selectById(id);
        if(user1 == null || user1.getOpenid() == null)return SystemJsonResponse.fail();
        String openid = user1.getOpenid();
        Object object = request.getSession().getAttribute(CAS);
        if ( null == object ) {
            return SystemJsonResponse.fail("中央认证失败");
        }
        Assertion assertion = ( Assertion ) object;
        String userName = assertion.getPrincipal().getName();
        log.error("cas对接登录用户buildUserInfoByCas：" + userName);
        //获取属性值
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
        //更新用户信息
        User user = new User();
        user.setOpenid(openid);
        user.setName((String) attributes.get("cn"));
        user.setCollege((String) attributes.get("eduPersonOrgDN"));
        user.setStudentId((String) attributes.get("bindUserList"));
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenid,openid);
        int update = userMapper.update(user, lambdaQueryWrapper);
        if(update == 0)return SystemJsonResponse.fail(GlobalResponseCode.OPERATE_FAIL.getCode(),"该中央认证已经绑定过微信号,请联系管理员解绑");
        //删除redis
        stringRedisTemplate.delete(RedisGlobalKey.USER_MESSAGE + openid);
        //设置中央认证缓存
        stringRedisTemplate.opsForValue().set(RedisGlobalKey.DGUT_LOGIN + openid,openid);
        return SystemJsonResponse.success();
    }


    /***
     * 获取用户id
     * @param request
     * @return
     */
    @GetMapping("/getId")
    @ApiOperation("获取用户id")
    public  SystemJsonResponse getId(HttpServletRequest request){
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        LambdaQueryWrapper<User>lambdaQueryWrapper = new LambdaQueryWrapper<>();
        User user = new User();
        user.setOpenid(openid);
        lambdaQueryWrapper.eq(User::getOpenid,openid);
        String uuid = userMapper.selectOne(lambdaQueryWrapper).getUuid();
        return SystemJsonResponse.success(uuid);
    }



    /**
     * 获取个人二维码
     * @param request
     * @param response
     */
    @GetMapping("/myQRCode")
    @ApiOperation(value = "获取个人二维码")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public  void getMyCode(HttpServletRequest request, HttpServletResponse response){
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getOpenid,openid);
        try {
            int width = 300; // QR Code的宽度
            int height = 300; // QR Code的高度

            // 使用ZXing库生成QR Code
            BitMatrix bitMatrix = new MultiFormatWriter().encode(openid, BarcodeFormat.QR_CODE, width, height);
            // 将QR Code转换为BufferedImage
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 将QR Code转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();

            // 将QR Code的字节数组回显给前端
            response.setContentType("image/png");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new GlobalSystemException(GlobalResponseCode.OPERATE_FAIL.getCode(),"获取二维码失败");
        }
    }



    /**
     * 签到
     * @param lectureId
     * @param openid
     * @return
     */
    @PostMapping("/SignIn")
    @ApiOperation("签到")
    @PreAuthorize("hasAuthority('admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "讲座id",name = "lectureId",required = true),
            @ApiImplicitParam(value = "openid",name = "openid",required = true),
    })
    public  SystemJsonResponse signIn(String lectureId,String openid){
          return  userService.signIn(lectureId,openid);
    }





    /**
     * 是否在黑名单里面
     * @param
     * @param
     * @return
     */
    @GetMapping("/isBlack")
    @ApiOperation("判断是否在黑名单")
    public  SystemJsonResponse isBlack(HttpServletRequest request){
        String token =  request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return userService.isBlack(openid);
    }

}
