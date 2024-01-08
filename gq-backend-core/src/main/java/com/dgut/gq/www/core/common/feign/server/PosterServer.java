package com.dgut.gq.www.core.common.feign.server;

import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.core.common.model.dto.PosterTweetDto;
import com.dgut.gq.www.core.service.PosterTweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Feign讲座模块服务端
 */
@RequestMapping("/feign-poster")
@RestController
public class PosterServer {

    @Autowired
    private PosterTweetService posterTweetService;

    /**
     * 远程调用新增或者更新推文
     * @param posterTweetDto
     * @return
     */
    @PostMapping("/updatePosterTweet")
    public  SystemJsonResponse saveUpdatePosterTweet(@RequestBody PosterTweetDto posterTweetDto){
        return posterTweetService.updatePosterTweet(posterTweetDto);
    }
}
