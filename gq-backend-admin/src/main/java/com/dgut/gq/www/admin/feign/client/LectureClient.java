package com.dgut.gq.www.admin.feign.client;

import com.dgut.gq.www.admin.model.dto.LectureDto;
import com.dgut.gq.www.common.common.SystemResultList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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
    SystemResultList getAttendLectureUser(@RequestParam int page,
                                          @RequestParam int pageSize,
                                          @RequestParam String id,
                                          @RequestParam Integer status);

    /**
     * 远程调用新增或者更新讲座
     * @param lectureDto
     * @return
     */
    @PostMapping("/updateSaveLecture")
    void  updateOrSaveLecture(@RequestBody LectureDto lectureDto);
}
