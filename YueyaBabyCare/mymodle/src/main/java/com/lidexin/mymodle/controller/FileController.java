package com.lidexin.mymodle.controller;

import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.dto.FileUploadResult;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传控制器
 * 提供文件上传功能
 * 
 * @author 张三
 */
@Api(tags = "文件上传")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileUtil fileUtil;

    /**
     * 获取请求的基础URL
     */
    private String getBaseUrl(javax.servlet.http.HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        
        String baseUrl = scheme + "://" + serverName;
        
        // 只有在非标准端口时才添加端口号
        if (("http".equals(scheme) && serverPort != 80) || 
            ("https".equals(scheme) && serverPort != 443)) {
            baseUrl += ":" + serverPort;
        }
        
        return baseUrl;
    }

    /**
     * 单文件上传
     * 返回相对路径，前端显示时根据环境拼接完整URL
     */
    @ApiOperation("单文件上传")
    @PostMapping("/upload")
    public R<FileUploadResult> uploadFile(
            @ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file,
            javax.servlet.http.HttpServletRequest request) {
        try {
            // fileUtil.upload 返回相对路径，如 /upload/2025/12/23/xxx.jpg
            String fileUrl = fileUtil.upload(file);

            FileUploadResult result = new FileUploadResult();
            result.setFileName(file.getOriginalFilename());
            result.setFileUrl(fileUrl);  // 保存相对路径，便于数据库存储和环境迁移
            result.setFileSize(file.getSize());
            result.setFileType(getFileExtension(file.getOriginalFilename()));
            result.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            return R.success("文件上传成功", result);
        } catch (BusinessException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 多文件上传
     */
    @ApiOperation("多文件上传")
    @PostMapping("/upload/batch")
    public R<List<FileUploadResult>> uploadFiles(
            @ApiParam(value = "文件列表", required = true) @RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return R.fail("请选择要上传的文件");
        }

        List<FileUploadResult> results = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String fileUrl = fileUtil.upload(file);
                
                FileUploadResult result = new FileUploadResult();
                result.setFileName(file.getOriginalFilename());
                result.setFileUrl(fileUrl);
                result.setFileSize(file.getSize());
                result.setFileType(getFileExtension(file.getOriginalFilename()));
                result.setUploadTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                results.add(result);
            } catch (Exception e) {
                failedFiles.add(file.getOriginalFilename());
            }
        }

        if (!failedFiles.isEmpty()) {
            return R.success("部分文件上传失败：" + String.join(", ", failedFiles), results);
        }
        
        return R.success("所有文件上传成功", results);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}
