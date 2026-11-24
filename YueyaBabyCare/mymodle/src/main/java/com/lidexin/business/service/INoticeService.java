package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.NoticeDTO;
import com.lidexin.business.po.Notice;
import com.lidexin.business.vo.NoticeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统公告表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public interface INoticeService extends IService<Notice> {

    /**
     * 新增公告（管理员）
     *
     * @param publisherId 发布人ID
     * @param dto         公告信息
     * @return 新增的公告ID
     */
    Long addNotice(Long publisherId, NoticeDTO dto);

    /**
     * 修改公告（管理员）
     *
     * @param id  公告ID
     * @param dto 公告信息
     * @return 是否成功
     */
    boolean updateNotice(Long id, NoticeDTO dto);

    /**
     * 删除公告（管理员）
     *
     * @param id 公告ID
     * @return 是否成功
     */
    boolean deleteNotice(Long id);

    /**
     * 获取公告详情
     *
     * @param id 公告ID
     * @return 公告详情
     */
    NoticeVO getNoticeById(Long id);

    /**
     * 获取公告列表（管理员）
     *
     * @param type       公告类型
     * @param targetType 目标类型
     * @param status     状态
     * @param keyword    关键词
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 公告分页列表
     */
    IPage<NoticeVO> getNoticeList(Byte type, Byte targetType, Byte status, String keyword, Integer pageNum, Integer pageSize);

    /**
     * 获取已发布的公告列表（用户端）
     *
     * @param targetType 目标类型（0-全部，1-家长，2-保姆，3-机构）
     * @param pageNum    页码
     * @param pageSize   每页数量
     * @return 公告分页列表
     */
    IPage<NoticeVO> getPublishedNoticeList(Byte targetType, Integer pageNum, Integer pageSize);

    /**
     * 发布/下线公告（管理员）
     *
     * @param id     公告ID
     * @param status 状态（1-已发布，2-已下线）
     * @return 是否成功
     */
    boolean updateNoticeStatus(Long id, Byte status);

    /**
     * 获取最新公告列表
     *
     * @param targetType 目标类型
     * @param limit      数量限制
     * @return 公告列表
     */
    List<NoticeVO> getLatestNotices(Byte targetType, Integer limit);
}
