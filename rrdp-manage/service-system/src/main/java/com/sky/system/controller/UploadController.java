package com.sky.system.controller;

import cn.hutool.core.util.StrUtil;
import com.sky.common.constant.SystemConstants;
import com.sky.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    @PostMapping("/icon")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile image) {
        try {
            // 获取原始文件名称
            String originalFilename = image.getOriginalFilename();
            // 保存文件
            image.transferTo(new File(SystemConstants.SHOP_TYPE_ICON_UPLOAD, originalFilename));
            String fileName = "/types/"+originalFilename;
            // 返回结果
            log.debug("文件上传成功，{}", fileName);
            return Result.ok(fileName);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

}
