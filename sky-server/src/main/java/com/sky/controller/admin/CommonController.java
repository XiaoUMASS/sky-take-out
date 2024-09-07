package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


/**
 * 通用接口
 */
@Api(tags = "通用接口")
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传");
        if (file.isEmpty()) {
            log.info("上传文件为空");
            return Result.error("文件不能为空");
        }
        UUID uuid = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename();
//        String[] subStrings = new String[0];
//        if (originalFilename != null) {
//            subStrings = originalFilename.split("\\.");
//        }
//        String suffix = subStrings[subStrings.length - 1];

        String[] subStrings = originalFilename != null ? originalFilename.split("\\.") : new String[0];
        if (subStrings.length == 0) {
            return Result.error("无效的文件格式！");
        }
        String suffix = subStrings[subStrings.length - 1];
        String filePath = aliOssUtil.upload(file.getBytes(), uuid.toString() + "." + suffix);
        return Result.success(filePath);
    }

}
