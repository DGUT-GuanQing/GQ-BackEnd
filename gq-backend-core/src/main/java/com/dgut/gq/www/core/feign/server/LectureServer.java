package com.dgut.gq.www.core.feign.server;

import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.core.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign讲座模块服务端
 */
@RequestMapping("/feign-lecture")
@RestController
public class LectureServer {

    @Autowired
    private LectureService lectureService;

    /**
     * 远程调用获取参加讲座的人员
     * @param
     * @return
     */
    @GetMapping("/AttendLectureUser")
    SystemResultList getAttendLectureUser(@RequestParam int page,
                                          @RequestParam int pageSize,
                                          @RequestParam String id,
                                          @RequestParam Integer status){
        return lectureService.getAttendLectureUser(page,pageSize,id,status);
    }
}
