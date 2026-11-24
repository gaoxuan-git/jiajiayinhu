package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.dto.NoticeDTO;
import com.lidexin.business.po.Notice;
import com.lidexin.business.mapper.NoticeMapper;
import com.lidexin.business.service.INoticeService;
import com.lidexin.business.vo.NoticeVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 系统公告表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addNotice(Long publisherId, NoticeDTO dto) {
        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setType(dto.getType() != null ? dto.getType() : (byte) 1);
        notice.setTargetType(dto.getTargetType() != null ? dto.getTargetType() : (byte) 0);
        notice.setPublisherId(publisherId);
        notice.setStatus(dto.getStatus() != null ? dto.getStatus() : (byte) 0); // 默认草稿
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        notice.setDeleted((byte) 0);

        // 如果状态为已发布，设置发布时间
        if (notice.getStatus() != null && notice.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }

        this.save(notice);
        return notice.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNotice(Long id, NoticeDTO dto) {
        Notice existingNotice = this.getById(id);
        if (existingNotice == null || existingNotice.getDeleted() == 1) {
            return false;
        }

        Notice notice = new Notice();
        notice.setId(id);
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setType(dto.getType());
        notice.setTargetType(dto.getTargetType());
        notice.setUpdateTime(LocalDateTime.now());

        // 如果状态变更为已发布且之前未发布，设置发布时间
        if (dto.getStatus() != null && dto.getStatus() == 1 && 
            (existingNotice.getStatus() == null || existingNotice.getStatus() != 1)) {
            notice.setPublishTime(LocalDateTime.now());
        }
        notice.setStatus(dto.getStatus());

        return this.updateById(notice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteNotice(Long id) {
        LambdaUpdateWrapper<Notice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notice::getId, id)
                .set(Notice::getDeleted, (byte) 1)
                .set(Notice::getUpdateTime, LocalDateTime.now());
        return this.update(wrapper);
    }

    @Override
    public NoticeVO getNoticeById(Long id) {
        Notice notice = this.getById(id);
        if (notice == null || notice.getDeleted() == 1) {
            return null;
        }
        return convertToVO(notice);
    }

    @Override
    public IPage<NoticeVO> getNoticeList(Byte type, Byte targetType, Byte status, String keyword, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getDeleted, (byte) 0);

        if (type != null) {
            wrapper.eq(Notice::getType, type);
        }
        if (targetType != null) {
            wrapper.eq(Notice::getTargetType, targetType);
        }
        if (status != null) {
            wrapper.eq(Notice::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword)
                    .or()
                    .like(Notice::getContent, keyword));
        }

        wrapper.orderByDesc(Notice::getCreateTime);

        Page<Notice> page = new Page<>(pageNum, pageSize);
        IPage<Notice> noticePage = this.page(page, wrapper);

        // 转换为VO
        Page<NoticeVO> voPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        voPage.setRecords(convertToVOList(noticePage.getRecords()));
        return voPage;
    }

    @Override
    public IPage<NoticeVO> getPublishedNoticeList(Byte targetType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getDeleted, (byte) 0)
                .eq(Notice::getStatus, (byte) 1); // 已发布

        // 目标类型：0-全部 或 指定类型
        if (targetType != null && targetType != 0) {
            wrapper.and(w -> w.eq(Notice::getTargetType, (byte) 0)
                    .or()
                    .eq(Notice::getTargetType, targetType));
        }

        wrapper.orderByDesc(Notice::getPublishTime);

        Page<Notice> page = new Page<>(pageNum, pageSize);
        IPage<Notice> noticePage = this.page(page, wrapper);

        // 转换为VO
        Page<NoticeVO> voPage = new Page<>(noticePage.getCurrent(), noticePage.getSize(), noticePage.getTotal());
        voPage.setRecords(convertToVOList(noticePage.getRecords()));
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateNoticeStatus(Long id, Byte status) {
        Notice existingNotice = this.getById(id);
        if (existingNotice == null || existingNotice.getDeleted() == 1) {
            return false;
        }

        LambdaUpdateWrapper<Notice> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Notice::getId, id)
                .set(Notice::getStatus, status)
                .set(Notice::getUpdateTime, LocalDateTime.now());

        // 如果状态变更为已发布且之前未发布，设置发布时间
        if (status == 1 && (existingNotice.getStatus() == null || existingNotice.getStatus() != 1)) {
            wrapper.set(Notice::getPublishTime, LocalDateTime.now());
        }

        return this.update(wrapper);
    }

    @Override
    public List<NoticeVO> getLatestNotices(Byte targetType, Integer limit) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getDeleted, (byte) 0)
                .eq(Notice::getStatus, (byte) 1); // 已发布

        // 目标类型：0-全部 或 指定类型
        if (targetType != null && targetType != 0) {
            wrapper.and(w -> w.eq(Notice::getTargetType, (byte) 0)
                    .or()
                    .eq(Notice::getTargetType, targetType));
        }

        wrapper.orderByDesc(Notice::getPublishTime)
                .last("LIMIT " + (limit != null ? limit : 5));

        List<Notice> notices = this.list(wrapper);
        return convertToVOList(notices);
    }

    /**
     * 转换为VO列表
     */
    private List<NoticeVO> convertToVOList(List<Notice> notices) {
        List<NoticeVO> voList = new ArrayList<>();
        for (Notice notice : notices) {
            voList.add(convertToVO(notice));
        }
        return voList;
    }

    /**
     * 转换为VO
     */
    private NoticeVO convertToVO(Notice notice) {
        NoticeVO vo = new NoticeVO();
        vo.setId(notice.getId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setType(notice.getType());
        vo.setTypeName(getTypeName(notice.getType()));
        vo.setTargetType(notice.getTargetType());
        vo.setTargetTypeName(getTargetTypeName(notice.getTargetType()));
        vo.setPublisherId(notice.getPublisherId());
        vo.setStatus(notice.getStatus());
        vo.setStatusName(getStatusName(notice.getStatus()));
        vo.setPublishTime(notice.getPublishTime());
        vo.setCreateTime(notice.getCreateTime());
        vo.setUpdateTime(notice.getUpdateTime());
        return vo;
    }

    /**
     * 获取公告类型名称
     */
    private String getTypeName(Byte type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "系统公告";
            case 2: return "活动通知";
            case 3: return "其他";
            default: return "未知";
        }
    }

    /**
     * 获取目标类型名称
     */
    private String getTargetTypeName(Byte targetType) {
        if (targetType == null) return "全部";
        switch (targetType) {
            case 0: return "全部";
            case 1: return "家长";
            case 2: return "保姆";
            case 3: return "机构";
            default: return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Byte status) {
        if (status == null) return "草稿";
        switch (status) {
            case 0: return "草稿";
            case 1: return "已发布";
            case 2: return "已下线";
            default: return "未知";
        }
    }
}
