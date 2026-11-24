package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.NoticeDTO;
import com.lidexin.business.service.INoticeService;
import com.lidexin.business.vo.NoticeVO;
import com.lidexin.business.vo.UserInfoVO;
import com.lidexin.mymodle.common.R;
import com.lidexin.mymodle.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 系统公告表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "公告管理")
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Autowired
    private INoticeService noticeService;

    @Autowired
    private AuthService authService;

    /**
     * 新增公告（管理员）
     */
    @ApiOperation("新增公告（管理员）")
    @PostMapping
    public R<Long> addNotice(@Validated @RequestBody NoticeDTO dto, HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 验证是否为管理员
        if (userInfo.getUserType() == null || userInfo.getUserType() != 4) {
            return R.fail("无权限操作，仅管理员可发布公告");
        }

        Long noticeId = noticeService.addNotice(userInfo.getUserId(), dto);
        return R.success("新增成功", noticeId);
    }

    /**
     * 修改公告（管理员）
     */
    @ApiOperation("修改公告（管理员）")
    @PutMapping("/{id}")
    public R<Boolean> updateNotice(
            @ApiParam(value = "公告ID", required = true) @PathVariable Long id,
            @Validated @RequestBody NoticeDTO dto,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 验证是否为管理员
        if (userInfo.getUserType() == null || userInfo.getUserType() != 4) {
            return R.fail("无权限操作，仅管理员可修改公告");
        }

        boolean result = noticeService.updateNotice(id, dto);
        return result ? R.success("修改成功", true) : R.fail("修改失败，公告不存在");
    }

    /**
     * 删除公告（管理员）
     */
    @ApiOperation("删除公告（管理员）")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteNotice(
            @ApiParam(value = "公告ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 验证是否为管理员
        if (userInfo.getUserType() == null || userInfo.getUserType() != 4) {
            return R.fail("无权限操作，仅管理员可删除公告");
        }

        boolean result = noticeService.deleteNotice(id);
        return result ? R.success("删除成功", true) : R.fail("删除失败");
    }

    /**
     * 获取公告详情
     */
    @ApiOperation("获取公告详情")
    @GetMapping("/{id}")
    public R<NoticeVO> getNoticeById(
            @ApiParam(value = "公告ID", required = true) @PathVariable Long id) {
        NoticeVO notice = noticeService.getNoticeById(id);
        return notice != null ? R.success(notice) : R.fail("公告不存在");
    }

    /**
     * 获取公告列表（管理员）
     */
    @ApiOperation("获取公告列表（管理员）")
    @GetMapping("/list")
    public R<IPage<NoticeVO>> getNoticeList(
            @ApiParam(value = "公告类型（1-系统公告，2-活动通知，3-其他）") @RequestParam(required = false) Byte type,
            @ApiParam(value = "目标类型（0-全部，1-家长，2-保姆，3-机构）") @RequestParam(required = false) Byte targetType,
            @ApiParam(value = "状态（0-草稿，1-已发布，2-已下线）") @RequestParam(required = false) Byte status,
            @ApiParam(value = "关键词") @RequestParam(required = false) String keyword,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 验证是否为管理员
        if (userInfo.getUserType() == null || userInfo.getUserType() != 4) {
            return R.fail("无权限操作，仅管理员可查看公告列表");
        }

        IPage<NoticeVO> page = noticeService.getNoticeList(type, targetType, status, keyword, pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 获取已发布的公告列表（用户端）
     */
    @ApiOperation("获取已发布的公告列表（用户端）")
    @GetMapping("/published")
    public R<IPage<NoticeVO>> getPublishedNoticeList(
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        Byte targetType = null;
        if (userInfo != null && userInfo.getUserType() != null) {
            // 根据用户类型获取对应的公告
            targetType = userInfo.getUserType().byteValue();
        }

        IPage<NoticeVO> page = noticeService.getPublishedNoticeList(targetType, pageNum, pageSize);
        return R.success(page);
    }

    /**
     * 发布/下线公告（管理员）
     */
    @ApiOperation("发布/下线公告（管理员）")
    @PutMapping("/{id}/status")
    public R<Boolean> updateNoticeStatus(
            @ApiParam(value = "公告ID", required = true) @PathVariable Long id,
            @ApiParam(value = "状态（1-已发布，2-已下线）", required = true) @RequestParam Byte status,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 验证是否为管理员
        if (userInfo.getUserType() == null || userInfo.getUserType() != 4) {
            return R.fail("无权限操作，仅管理员可修改公告状态");
        }

        // 验证状态值
        if (status != 1 && status != 2) {
            return R.fail("状态值无效，只能为1（已发布）或2（已下线）");
        }

        boolean result = noticeService.updateNoticeStatus(id, status);
        return result ? R.success("操作成功", true) : R.fail("操作失败，公告不存在");
    }

    /**
     * 获取最新公告列表
     */
    @ApiOperation("获取最新公告列表")
    @GetMapping("/latest")
    public R<List<NoticeVO>> getLatestNotices(
            @ApiParam(value = "数量限制", defaultValue = "5") @RequestParam(defaultValue = "5") Integer limit,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        Byte targetType = null;
        if (userInfo != null && userInfo.getUserType() != null) {
            // 根据用户类型获取对应的公告
            targetType = userInfo.getUserType().byteValue();
        }

        List<NoticeVO> notices = noticeService.getLatestNotices(targetType, limit);
        return R.success(notices);
    }

    /**
     * 从请求中获取当前登录用户
     */
    private UserInfoVO getCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            return null;
        }
        return authService.getUserByToken(token, UserInfoVO.class);
    }
}
