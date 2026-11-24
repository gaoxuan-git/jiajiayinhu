package com.lidexin.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lidexin.business.dto.MessageDTO;
import com.lidexin.business.service.IMessageService;
import com.lidexin.business.vo.AvailableContactVO;
import com.lidexin.business.vo.ContactVO;
import com.lidexin.business.vo.MessageVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 消息/留言表 前端控制器
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Api(tags = "消息管理")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private AuthService authService;

    /**
     * 发送消息
     */
    @ApiOperation("发送消息")
    @PostMapping("/send")
    public R<Long> sendMessage(@Validated @RequestBody MessageDTO dto, HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Long messageId = messageService.sendMessage(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                dto);
        return R.success("发送成功", messageId);
    }

    /**
     * 获取消息列表
     */
    @ApiOperation("获取消息列表")
    @GetMapping("/list")
    public R<IPage<MessageVO>> getMessageList(
            @ApiParam(value = "消息类型（1-系统消息，2-护理提醒，3-留言反馈）") @RequestParam(required = false) Byte msgType,
            @ApiParam(value = "是否已读（0-未读，1-已读）") @RequestParam(required = false) Byte isRead,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "10") @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        IPage<MessageVO> page = messageService.getMessageList(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                msgType,
                isRead,
                pageNum,
                pageSize);
        return R.success(page);
    }

    /**
     * 获取对话记录
     */
    @ApiOperation("获取对话记录")
    @GetMapping("/conversation")
    public R<IPage<MessageVO>> getConversation(
            @ApiParam(value = "联系人类型（1-家长，2-保姆，3-机构，4-管理员）", required = true) @RequestParam Byte contactType,
            @ApiParam(value = "联系人ID", required = true) @RequestParam Long contactId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页数量", defaultValue = "20") @RequestParam(defaultValue = "20") Integer pageSize,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        // 调试日志
        System.out.println("=== 获取对话记录 ===");
        System.out.println("当前用户类型: " + userInfo.getUserType());
        System.out.println("当前用户ID: " + userInfo.getUserId());
        System.out.println("联系人类型: " + contactType);
        System.out.println("联系人ID: " + contactId);

        IPage<MessageVO> page = messageService.getConversation(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                contactType,
                contactId,
                pageNum,
                pageSize);
        
        System.out.println("查询结果数量: " + page.getTotal());
        System.out.println("===================");
        
        return R.success(page);
    }

    /**
     * 获取联系人列表
     */
    @ApiOperation("获取联系人列表")
    @GetMapping("/contacts")
    public R<List<ContactVO>> getContactList(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        List<ContactVO> contacts = messageService.getContactList(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId());
        return R.success(contacts);
    }

    /**
     * 获取未读消息数
     */
    @ApiOperation("获取未读消息数")
    @GetMapping("/unread-count")
    public R<Integer> getUnreadCount(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        Integer count = messageService.getUnreadCount(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId());
        return R.success(count);
    }

    /**
     * 标记消息已读
     */
    @ApiOperation("标记消息已读")
    @PutMapping("/{id}/read")
    public R<Boolean> markAsRead(
            @ApiParam(value = "消息ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        boolean result = messageService.markAsRead(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                id);
        return result ? R.success("操作成功", true) : R.fail("操作失败");
    }

    /**
     * 标记所有消息已读
     */
    @ApiOperation("标记所有消息已读")
    @PutMapping("/read-all")
    public R<Boolean> markAllAsRead(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        boolean result = messageService.markAllAsRead(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId());
        return R.success("操作成功", result);
    }

    /**
     * 删除消息（撤回）
     */
    @ApiOperation("删除消息（撤回）")
    @DeleteMapping("/{id}")
    public R<Boolean> deleteMessage(
            @ApiParam(value = "消息ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        boolean result = messageService.deleteMessage(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                id);
        return result ? R.success("删除成功", true) : R.fail("删除失败");
    }

    /**
     * 标记与某联系人的所有消息已读
     */
    @ApiOperation("标记与某联系人的所有消息已读")
    @PutMapping("/conversation/read")
    public R<Boolean> markConversationAsRead(
            @ApiParam(value = "联系人类型（1-家长，2-保姆，3-机构，4-管理员）", required = true) @RequestParam Byte contactType,
            @ApiParam(value = "联系人ID", required = true) @RequestParam Long contactId,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }

        boolean result = messageService.markConversationAsRead(
                userInfo.getUserType().byteValue(),
                userInfo.getUserId(),
                contactType,
                contactId);
        return R.success("操作成功", result);
    }

    // ==================== 9.3 选择联系人功能 ====================

    /**
     * 家长端：获取有业务关系的机构列表
     */
    @ApiOperation("家长端：获取有业务关系的机构列表")
    @GetMapping("/parent/institutions")
    public R<List<AvailableContactVO>> getInstitutionsForParent(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 1) {
            return R.fail("仅家长可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getInstitutionsForParent(userInfo.getUserId(), keyword);
        return R.success(list);
    }

    /**
     * 家长端：获取服务过的保姆列表
     */
    @ApiOperation("家长端：获取服务过的保姆列表")
    @GetMapping("/parent/nannies")
    public R<List<AvailableContactVO>> getNanniesForParent(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 1) {
            return R.fail("仅家长可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getNanniesForParent(userInfo.getUserId(), keyword);
        return R.success(list);
    }

    /**
     * 保姆端：获取服务过的家长列表
     */
    @ApiOperation("保姆端：获取服务过的家长列表")
    @GetMapping("/nanny/parents")
    public R<List<AvailableContactVO>> getParentsForNanny(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 2) {
            return R.fail("仅保姆可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getParentsForNanny(userInfo.getUserId(), keyword);
        return R.success(list);
    }

    /**
     * 保姆端：获取所属机构
     */
    @ApiOperation("保姆端：获取所属机构")
    @GetMapping("/nanny/institutions")
    public R<List<AvailableContactVO>> getInstitutionsForNanny(HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 2) {
            return R.fail("仅保姆可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getInstitutionsForNanny(userInfo.getUserId());
        return R.success(list);
    }

    /**
     * 机构端：获取有业务关系的家长列表
     */
    @ApiOperation("机构端：获取有业务关系的家长列表")
    @GetMapping("/institution/parents")
    public R<List<AvailableContactVO>> getParentsForInstitution(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 3) {
            return R.fail("仅机构可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getParentsForInstitution(userInfo.getUserId(), keyword);
        return R.success(list);
    }

    /**
     * 管理员端：获取所有家长列表
     */
    @ApiOperation("管理员端：获取所有家长列表")
    @GetMapping("/admin/parents")
    public R<List<AvailableContactVO>> getAllParents(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 4) {
            return R.fail("仅管理员可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getAllParents(keyword);
        return R.success(list);
    }

    /**
     * 管理员端：获取所有保姆列表
     */
    @ApiOperation("管理员端：获取所有保姆列表")
    @GetMapping("/admin/nannies")
    public R<List<AvailableContactVO>> getAllNannies(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 4) {
            return R.fail("仅管理员可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getAllNannies(keyword);
        return R.success(list);
    }

    /**
     * 管理员端：获取所有机构列表
     */
    @ApiOperation("管理员端：获取所有机构列表")
    @GetMapping("/admin/institutions")
    public R<List<AvailableContactVO>> getAllInstitutions(
            @ApiParam(value = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        UserInfoVO userInfo = getCurrentUser(request);
        if (userInfo == null) {
            return R.fail("用户未登录");
        }
        if (userInfo.getUserType() != 4) {
            return R.fail("仅管理员可访问此接口");
        }
        List<AvailableContactVO> list = messageService.getAllInstitutions(keyword);
        return R.success(list);
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
