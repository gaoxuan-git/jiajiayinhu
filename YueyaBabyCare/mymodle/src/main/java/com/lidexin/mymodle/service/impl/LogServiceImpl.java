package com.lidexin.mymodle.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.mymodle.common.PageResult;
import com.lidexin.mymodle.dto.LogFileDTO;
import com.lidexin.mymodle.dto.LogInfoDTO;
import com.lidexin.mymodle.dto.LogQueryDTO;
import com.lidexin.mymodle.exception.BusinessException;
import com.lidexin.mymodle.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 日志管理服务实现类
 *
 * @author 张三
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService {

    @Value("${logging.file.path:logs}")
    private String logPath;

    // 日志格式正则表达式（匹配 logback 配置的格式）
    // 实际格式：2025-11-05 14:59:35.634 [http-nio-8080-exec-2] INFO  com.lidexin.mymodle.interceptor.LogInterceptor - 日志内容
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+" +  // 时间
            "\\[([^\\]]+)\\]\\s+" +                                       // 线程
            "(TRACE|DEBUG|INFO|WARN|ERROR|FATAL)\\s+" +                   // 级别
            "([^-]+?)\\s*-\\s*" +                                         // Logger
            "(.+)"                                                        // 消息
    );

    @Override
    @Cacheable(value = "systemCache", key = "'logFiles'")
    public List<LogFileDTO> getLogFiles() {
        log.info("获取日志文件列表...");
        List<LogFileDTO> files = new ArrayList<>();
        
        File logDir = new File(logPath);
        if (!logDir.exists() || !logDir.isDirectory()) {
            log.warn("日志目录不存在: {}", logPath);
            return files;
        }
        
        File[] logFiles = logDir.listFiles((dir, name) -> name.endsWith(".log"));
        if (logFiles != null) {
            for (File file : logFiles) {
                try {
                    LogFileDTO dto = new LogFileDTO();
                    dto.setFileName(file.getName());
                    dto.setFilePath(file.getAbsolutePath());
                    dto.setFileSize(file.length());
                    dto.setFileSizeFormatted(formatFileSize(file.length()));
                    dto.setReadable(file.canRead());
                    
                    // 获取最后修改时间
                    Path path = file.toPath();
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dto.setLastModified(sdf.format(new Date(attrs.lastModifiedTime().toMillis())));
                    
                    files.add(dto);
                } catch (Exception e) {
                    log.error("读取文件信息失败: {}", file.getName(), e);
                }
            }
        }
        
        // 按最后修改时间倒序排列
        files.sort((a, b) -> b.getLastModified().compareTo(a.getLastModified()));
        
        return files;
    }

    @Override
    public PageResult<LogInfoDTO> queryLogs(LogQueryDTO queryDTO) {
        String fileName = StringUtils.isBlank(queryDTO.getFileName()) ? "app.log" : queryDTO.getFileName();
        File logFile = new File(logPath, fileName);
        
        if (!logFile.exists() || !logFile.canRead()) {
            throw new BusinessException("日志文件不存在或不可读: " + fileName);
        }
        
        try {
            // 读取所有行
            List<String> allLines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
            
            // 解析并过滤日志
            List<LogInfoDTO> filteredLogs = new ArrayList<>();
            long lineNumber = 1;
            
            for (String line : allLines) {
                LogInfoDTO logInfo = parseLogLine(line, lineNumber);
                
                // 应用过滤条件
                if (matchesFilter(logInfo, queryDTO)) {
                    filteredLogs.add(logInfo);
                }
                
                lineNumber++;
            }
            
            // 倒序排列（最新的在前面）
            Collections.reverse(filteredLogs);
            
            // 分页处理
            int current = queryDTO.getCurrent();
            int size = queryDTO.getSize();
            int total = filteredLogs.size();
            int fromIndex = (current - 1) * size;
            int toIndex = Math.min(fromIndex + size, total);
            
            List<LogInfoDTO> pageData = fromIndex < total ? 
                    filteredLogs.subList(fromIndex, toIndex) : 
                    Collections.emptyList();
            
            // 构建分页结果
            Page<LogInfoDTO> page = new Page<>(current, size, total);
            page.setRecords(pageData);
            
            return PageResult.of(page);
            
        } catch (IOException e) {
            log.error("读取日志文件失败: {}", fileName, e);
            throw new BusinessException("读取日志文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getLogFilePath(String fileName) {
        File logFile = new File(logPath, fileName);
        if (!logFile.exists()) {
            throw new BusinessException("日志文件不存在: " + fileName);
        }
        return logFile.getAbsolutePath();
    }

    @Override
    @CacheEvict(value = "systemCache", key = "'logFiles'")
    public void clearLogFile(String fileName) {
        File logFile = new File(logPath, fileName);
        if (!logFile.exists()) {
            throw new BusinessException("日志文件不存在: " + fileName);
        }
        
        try (FileWriter writer = new FileWriter(logFile, false)) {
            // 清空文件内容
            writer.write("");
            log.info("已清空日志文件: {}", fileName);
        } catch (IOException e) {
            log.error("清空日志文件失败: {}", fileName, e);
            throw new BusinessException("清空日志文件失败: " + e.getMessage());
        }
    }

    @Override
    @CacheEvict(value = "systemCache", key = "'logFiles'")
    public void deleteLogFile(String fileName) {
        // 不允许删除当前的主日志文件
        if ("app.log".equals(fileName)) {
            throw new BusinessException("不允许删除当前主日志文件");
        }
        
        File logFile = new File(logPath, fileName);
        if (!logFile.exists()) {
            throw new BusinessException("日志文件不存在: " + fileName);
        }
        
        if (logFile.delete()) {
            log.info("已删除日志文件: {}", fileName);
        } else {
            throw new BusinessException("删除日志文件失败: " + fileName);
        }
    }

    /**
     * 解析单行日志
     */
    private LogInfoDTO parseLogLine(String line, long lineNumber) {
        LogInfoDTO logInfo = new LogInfoDTO();
        logInfo.setId(lineNumber);
        logInfo.setRawLog(line);
        
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            logInfo.setTimestamp(matcher.group(1));      // 时间
            logInfo.setThread(matcher.group(2));         // 线程
            logInfo.setLevel(matcher.group(3).trim());   // 级别
            logInfo.setLogger(matcher.group(4).trim());  // Logger
            logInfo.setMessage(matcher.group(5).trim()); // 消息
        } else {
            // 如果不匹配格式，将整行作为消息
            logInfo.setLevel("UNKNOWN");
            logInfo.setMessage(line);
        }
        
        return logInfo;
    }

    /**
     * 检查日志是否匹配过滤条件
     */
    private boolean matchesFilter(LogInfoDTO logInfo, LogQueryDTO queryDTO) {
        // 级别过滤
        if (StringUtils.isNotBlank(queryDTO.getLevel()) && 
            !"ALL".equalsIgnoreCase(queryDTO.getLevel())) {
            if (!queryDTO.getLevel().equalsIgnoreCase(logInfo.getLevel())) {
                return false;
            }
        }
        
        // 关键词过滤
        if (StringUtils.isNotBlank(queryDTO.getKeyword())) {
            String keyword = queryDTO.getKeyword().toLowerCase();
            boolean found = false;
            if (logInfo.getMessage() != null && logInfo.getMessage().toLowerCase().contains(keyword)) {
                found = true;
            }
            if (logInfo.getLogger() != null && logInfo.getLogger().toLowerCase().contains(keyword)) {
                found = true;
            }
            if (!found) {
                return false;
            }
        }
        
        // 时间范围过滤
        if (StringUtils.isNotBlank(queryDTO.getStartTime()) && logInfo.getTimestamp() != null) {
            if (logInfo.getTimestamp().compareTo(queryDTO.getStartTime()) < 0) {
                return false;
            }
        }
        if (StringUtils.isNotBlank(queryDTO.getEndTime()) && logInfo.getTimestamp() != null) {
            if (logInfo.getTimestamp().compareTo(queryDTO.getEndTime()) > 0) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        DecimalFormat df = new DecimalFormat("#,##0.##");
        return df.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

