package com.lidexin.mymodle.util;

import cn.hutool.core.util.IdUtil;
import com.lidexin.mymodle.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件工具类
 * 用于文件上传、下载等操作
 * 
 * @author 张三
 */
@Component
public class FileUtil {

    @Value("${mymodle.upload.path:./upload}")
    private String uploadPath;

    @Value("${mymodle.upload.url:/upload}")
    private String uploadUrl;

    /**
     * 允许上传的文件类型
     */
    private static final String[] ALLOWED_EXTENSIONS = {
            "jpg", "jpeg", "png", "gif", "bmp",  // 图片
            "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",  // 文档
            "txt", "zip", "rar", "7z"  // 其他
    };

    /**
     * 最大文件大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件
     * 
     * @param file 文件
     * @return 文件访问路径
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过10MB");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("文件名不能为空");
        }

        // 检查文件扩展名
        String extension = cn.hutool.core.io.FileUtil.extName(originalFilename);
        if (!isAllowedExtension(extension)) {
            throw new BusinessException("不支持的文件类型：" + extension);
        }

        // 生成新文件名（使用UUID避免重名）
        String newFileName = IdUtil.simpleUUID() + "." + extension;

        // 生成日期目录（按年月日分类）
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String targetDir = uploadPath + File.separator + dateDir;

        // 创建目录
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File targetFile = new File(targetDir + File.separator + newFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }

        // 返回文件访问路径
        return uploadUrl + "/" + dateDir + "/" + newFileName;
    }

    /**
     * 检查文件扩展名是否允许
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    public boolean delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        // 去掉URL前缀，获取实际文件路径
        String realPath = filePath.replace(uploadUrl, uploadPath);
        File file = new File(realPath);
        
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 获取文件的实际存储路径
     */
    public String getRealPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return filePath.replace(uploadUrl, uploadPath);
    }
}

