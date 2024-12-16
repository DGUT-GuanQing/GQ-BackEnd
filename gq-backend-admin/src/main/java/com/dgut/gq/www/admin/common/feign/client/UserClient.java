package com.dgut.gq.www.admin.common.feign.client;

import com.dgut.gq.www.common.db.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign用户模块客户端
 */
@FeignClient(name = "gq-backend-core", contextId = "user")
public interface UserClient {

    /**
     * 远程调用根据用户名获取用户信息
     *
     * @return
     */
    @GetMapping("/feign-user/getUserByUserName")
    User getUserByUserName(@RequestParam String userName);


}
