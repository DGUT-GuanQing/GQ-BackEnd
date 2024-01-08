package com.dgut.gq.www.core.controller;


import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.util.ParseToken;
import com.dgut.gq.www.core.common.annotation.Limit;
import com.dgut.gq.www.core.common.model.vo.LectureReviewVo;
import com.dgut.gq.www.core.common.model.vo.LectureTrailerVo;
import com.dgut.gq.www.core.common.model.vo.LectureVo;
import com.dgut.gq.www.core.service.LectureService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 讲座接口
 * @author  hyj
 * @since  2022-10-10
 * @version  1.0
 */
@RequestMapping("/lecture")
@RestController
@Api(tags = "讲座模块")
public class LectureController {

    @Autowired
    private LectureService lectureService;



    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取讲座回顾信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/allLectureReview")
    @ApiOperation(value = "获取讲座回顾")
    //@PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "页数",name = "page",required = true),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",required = true),
            @ApiImplicitParam(value = "模糊查询字段",name = "name",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = LectureReviewVo.class)
        }
    )
    public SystemJsonResponse getLectureReview(int page , int pageSize, String name){
        return lectureService.getLectureReview(page,pageSize,name);
    }


    /**
     * 获取讲座预告信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/allLectureTrailer")
    @ApiOperation(value = "获取讲座预告")
   // @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiImplicitParams({@ApiImplicitParam(value = "页数",name = "page",required = true),
            @ApiImplicitParam(value = "每页数量",name = "pageSize",required = true),
            @ApiImplicitParam(value = "模糊查询字段",name = "name",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = LectureTrailerVo.class)
    }
    )
    public SystemJsonResponse getLectureTrailer(int page ,int pageSize,String name){
        return lectureService.getLectureTrailer(page,pageSize,name);
    }




    /**
     * 获取正在进行的讲座信息
     * @return
     */
    @GetMapping("/unStartLecture")
    @ApiOperation(value = "获取正在进行的讲座信息")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = LectureVo.class)
    }

    )
    public  SystemJsonResponse unstart(){
        return  lectureService.findUnStartLecture();
    }




    /**
     * 抢票
     * @param
     * @param
     * @return
     */
    @PostMapping("/robTicket/{id}")
    @ApiOperation(value = "抢票")
    @ApiImplicitParam(value = "讲座id",required = true)
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @Limit(key = "robTicketLimit", permitsPerSecond = 200, timeout = 500, timeunit = TimeUnit.MILLISECONDS)
    public  SystemJsonResponse robTicket(HttpServletRequest request,@PathVariable String id){
        String token = request.getHeader("token");
        String openid = ParseToken.getOpenid(token);
        return lectureService.robTicket(openid,id);
    }




    /**
     * 判断是否还有票
     * @return
     */
    @GetMapping("/isTicketAvailable")
    @ApiOperation(value = "判断当前讲座是否有票")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    public  SystemJsonResponse isTicketAvailable(){
        boolean flag = true;
        //获取票的数量
        String s = stringRedisTemplate.opsForValue().get(RedisGlobalKey.TICKET_NUMBER);
        if(s == null || s.equals(""))flag = false;
        else {
            int count  = Integer.parseInt(s);
            if(count <= 0)flag = false;
        }
        return SystemJsonResponse.success(flag);
    }







}
