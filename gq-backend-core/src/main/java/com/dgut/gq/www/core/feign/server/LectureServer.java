package com.dgut.gq.www.core.feign.server;

import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.core.model.dto.LectureDto;
import com.dgut.gq.www.core.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    SystemJsonResponse getAttendLectureUser(@RequestParam int page,
                                          @RequestParam int pageSize,
                                          @RequestParam String id,
                                          @RequestParam Integer status){
        return lectureService.getAttendLectureUser(page,pageSize,id,status);
    }

    /**
     * 远程调用新增或者更新讲座
     * @param lectureDto
     * @return
     */
    @PostMapping("/updateSaveLecture")
    SystemJsonResponse updateOrSaveLecture(@RequestBody LectureDto lectureDto){
        return lectureService.updateSaveLecture(lectureDto);
    }


    /**
     * 远程调用获取讲座
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/getLecture")
    SystemJsonResponse getLecture(@RequestParam int page ,
                                  @RequestParam int pageSize,
                                  @RequestParam String name){
        return lectureService.getLecture(page,pageSize,name);
    }


    /**
     * 导出参加讲座的用户
     * @param
     * @param
     * @return
     */
    @GetMapping("/exportAttendLectureUser")
    SystemJsonResponse exportAttendLectureUser(@RequestParam  String id, @RequestParam Integer status){
        return lectureService.exportAttendLectureUser(id,status);
    }

}
