package com.sky.utils;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.util.UUID;


public class UploadUtils {
    /**
     * 创建文件路径和文件
     * @param prefix  blogs 或者  images 或者 avatar 或者 icons 或者 types
     * @param originalFilename
     * @return
     */
    public static String createNewFileName(String prefix,String originalFilename) {
        // 获取后缀
        String suffix = StrUtil.subAfter(originalFilename, ".", true);
        // 生成目录
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        // 判断目录是否存在
        File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/"+prefix+"/{}/{}", d1, d2));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 生成文件名
        return StrUtil.format("/"+prefix+"/{}/{}/{}.{}", d1, d2, name, suffix);
    }
}
