package com.lidexin.mymodle.controller;

import com.lidexin.mymodle.common.PageResult;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.dto.LogFileDTO;
import com.lidexin.mymodle.dto.LogInfoDTO;
import com.lidexin.mymodle.dto.LogQueryDTO;
import com.lidexin.mymodle.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 日志管理前端控制器
 *
 * @author 张三
 */
@Slf4j
@Api(tags = "日志管理")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private LogService logService;

    @ApiOperation("获取日志文件列表")
    @GetMapping("/files")
    public R<List<LogFileDTO>> getLogFiles() {
        List<LogFileDTO> files = logService.getLogFiles();
        return R.success(files);
    }

    @ApiOperation("分页查询日志内容")
    @GetMapping("/query")
    public R<PageResult<LogInfoDTO>> queryLogs(LogQueryDTO queryDTO) {
        PageResult<LogInfoDTO> result = logService.queryLogs(queryDTO);
        return R.success(result);
    }

    @ApiOperation("下载日志文件")
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLog(@RequestParam String fileName) {
        try {
            String filePath = logService.getLogFilePath(fileName);
            Resource resource = new FileSystemResource(filePath);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // URL 编码文件名，解决中文文件名乱码问题
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
                    
        } catch (UnsupportedEncodingException e) {
            log.error("文件名编码失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation("清空日志文件")
    @DeleteMapping("/clear")
    public R<String> clearLog(@RequestParam String fileName) {
        logService.clearLogFile(fileName);
        return R.success("日志文件已清空", null);
    }

    @ApiOperation("删除日志文件")
    @DeleteMapping("/delete")
    public R<String> deleteLog(@RequestParam String fileName) {
        logService.deleteLogFile(fileName);
        return R.success("日志文件已删除", null);
    }
}

