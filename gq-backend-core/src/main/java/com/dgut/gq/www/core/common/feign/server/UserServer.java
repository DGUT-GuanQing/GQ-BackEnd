package com.dgut.gq.www.core.common.feign.server;

import com.dgut.gq.www.common.common.SystemResultList;
import com.dgut.gq.www.common.model.entity.User;
import com.dgut.gq.www.core.service.LectureService;
import com.dgut.gq.www.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign用户模块服务端
 */
@RestController
@RequestMapping("/feign-user")
public class UserServer {

    @Autowired
    private UserService userService;


    /**
     * 远程调用根据用户名获取用户信息
     * @return
     */
    @GetMapping("/getUserByUserName")
    User getUserByUserName(@RequestParam String userName){
        return userService.getUserByUsername(userName);
    }


}
