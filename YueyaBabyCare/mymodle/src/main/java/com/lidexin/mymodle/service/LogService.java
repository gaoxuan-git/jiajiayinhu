package com.lidexin.mymodle.service;

import com.lidexin.mymodle.common.PageResult;
import com.lidexin.mymodle.dto.LogFileDTO;
import com.lidexin.mymodle.dto.LogInfoDTO;
import com.lidexin.mymodle.dto.LogQueryDTO;

import java.util.List;

/**
 * 日志管理服务接口
 *
 * @author 张三
 */
public interface LogService {

    /**
     * 获取所有日志文件列表
     *
     * @return 日志文件列表
     */
    List<LogFileDTO> getLogFiles();

    /**
     * 分页查询日志内容
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<LogInfoDTO> queryLogs(LogQueryDTO queryDTO);

    /**
     * 获取日志文件路径（用于下载）
     *
     * @param fileName 文件名
     * @return 文件绝对路径
     */
    String getLogFilePath(String fileName);

    /**
     * 清空指定日志文件
     *
     * @param fileName 文件名
     */
    void clearLogFile(String fileName);

    /**
     * 删除指定日志文件
     *
     * @param fileName 文件名
     */
    void deleteLogFile(String fileName);
}

