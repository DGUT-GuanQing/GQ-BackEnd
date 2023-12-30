package com.dgut.gq.www.admin.feign.client;


import com.dgut.gq.www.admin.model.dto.PosterTweetDto;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign推文模块客户端
 */
@FeignClient(name = "gq-backend-core",contextId="poster")
public interface PosterClient  {

    /**
     * 远程调用新增或者更新推文信息
     * @param
     * @return
     */
     @PostMapping("/feign-poster/updatePosterTweet")
     SystemJsonResponse saveUpdatePosterTweet(@RequestBody PosterTweetDto posterTweetDto);

}
