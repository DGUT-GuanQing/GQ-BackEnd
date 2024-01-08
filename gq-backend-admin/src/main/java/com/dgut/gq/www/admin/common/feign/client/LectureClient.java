package com.dgut.gq.www.admin.common.feign.client;

import com.dgut.gq.www.admin.common.model.dto.LectureDto;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


/**
 * Feign讲座模块客户端
 */
@FeignClient(name = "gq-backend-core",contextId="lecture")
public interface LectureClient {

    /**
     * 远程调用获取参加讲座的人员
     * @param
     * @return
     */
    @GetMapping("/feign-lecture/AttendLectureUser")
    SystemJsonResponse getAttendLectureUser(@RequestParam int page,
                                            @RequestParam int pageSize,
                                            @RequestParam String id,
                                            @RequestParam Integer status);

    /**
     * 远程调用新增或者更新讲座
     * @param lectureDto
     * @return
     */
    @PostMapping("/feign-lecture/updateSaveLecture")
    SystemJsonResponse updateOrSaveLecture(@RequestBody LectureDto lectureDto);

    /**
     * 远程调用获取讲座
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/feign-lecture/getLecture")
    SystemJsonResponse getLecture(@RequestParam int page ,
                                  @RequestParam int pageSize,
                                  @RequestParam String name);

    /**
     * 远程调用导出参加讲座的用户
     * @param
     * @param
     * @return
     */
    @GetMapping("/feign-lecture/exportAttendLectureUser")
    SystemJsonResponse exportAttendLectureUser(@RequestParam  String id, @RequestParam Integer status);

    /**
     * 远程调用删除讲座
     * @param id
     * @return
     */
    @DeleteMapping("/feign-lecture/deleteLecture/{id}")
    SystemJsonResponse deleteLecture(@PathVariable String id);

}
