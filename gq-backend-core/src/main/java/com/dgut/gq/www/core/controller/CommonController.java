package com.dgut.gq.www.core.controller;


import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import com.dgut.gq.www.core.model.vo.PosterTweetVo;
import com.dgut.gq.www.core.service.PosterTweetService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.swagger.annotations.*;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 通用模块
 * @since  2023-3-14
 * @author  hyj
 * @version  1.0
 */
@RestController
@RequestMapping("/common")
@Api(tags = "通用模块")
public class CommonController {

    String url = "C:\\Users\\waili\\Desktop\\usual\\Javaopenfile\\ssmheima\\photo\\";

    String url2 = "/opt/image/";

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private PosterTweetService posterTweetService;

    /**
     * 获取推文
     * @param type
     * @return
     */
    @GetMapping("/posterTweet/{type}")
    @ApiOperation(value = "获取招新和活动推文")
    @ApiImplicitParam(value = "推文类型 0-活动，1-招新 ",name = "type")
    @ApiResponses({
            @ApiResponse(code = 200, message = "查询成功", response = PosterTweetVo.class)
    }
    )
    public SystemJsonResponse tweet(@PathVariable Integer type){
        return posterTweetService.getByType(type);
    }

    /**
     * 文件
     * @return
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "文件上传")
    public SystemJsonResponse upload(MultipartFile file){
        //原始文件名
        String filename = file.getOriginalFilename();
        //UUID随机生成名字，防止重复
        String s = UUID.randomUUID().toString();
        String extension = filename.substring(filename.lastIndexOf("."));
        s += extension;

        // 根据文件扩展名确定存储的子文件夹
        String subfolder = extension.equalsIgnoreCase(".pdf") ? "file" : "picture";

        try {
            // 上传文件到Minio
            ByteArrayInputStream bais = new ByteArrayInputStream(file.getBytes());
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("gqfile")
                            .object(subfolder + "/" + s)
                            .stream(bais, bais.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new GlobalSystemException(GlobalResponseCode.OPERATE_FAIL.getCode(),"上传文文件失败");
        }
        return SystemJsonResponse.success(s);
    }

    /**
     * 文件下载
     */
    @GetMapping("/download")
    @PreAuthorize("hasAnyAuthority('user', 'admin')")
    @ApiOperation(value = "文件下载")
    @ApiImplicitParams({@ApiImplicitParam(value = "文件类型 0-图片 1-文件",name = "type",required = true),
            @ApiImplicitParam(value = "文件名",name = "name",required = true)
    })
    public void download( HttpServletResponse response,
                         @RequestParam("type") Integer type,
                         @RequestParam("name") String name) throws IOException {

        // 判断是文件还是图片
        String str = "";
        if(type == 0)str = "image";
        else if(type == 1)str = "file";
        if ("file".equals(str)) {
            response.setContentType("application/octet-stream");
        } else if ("image".equals(str)) {
            response.setContentType("image/jpeg");
        } else {
            throw new IllegalArgumentException("Unsupported type: " + str);
        }

        String subfolder;
        if (type == 0) {
            subfolder = "picture";
        } else if (type == 1) {
            subfolder = "file";
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("gqfile")
                            .object(subfolder + "/" + name)
                            .build()
            );

            // Set the content type and attachment header.
            response.addHeader("Content-disposition", "attachment;filename=" + name);

            // Copy the stream to the response's output stream.
            IOUtils.copy(stream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalSystemException(GlobalResponseCode.OPERATE_FAIL.getCode(),"下载文文件失败");
        }
    }

}
