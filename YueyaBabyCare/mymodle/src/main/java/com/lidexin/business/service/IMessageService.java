package com.lidexin.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.dto.MessageDTO;
import com.lidexin.business.po.Message;
import com.lidexin.business.vo.AvailableContactVO;
import com.lidexin.business.vo.ContactVO;
import com.lidexin.business.vo.MessageVO;

import java.util.List;

/**
 * <p>
 * 消息/留言表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public interface IMessageService extends IService<Message> {

    /**
     * 发送消息
     *
     * @param senderType 发送人类型
     * @param senderId   发送人ID
     * @param dto        消息信息
     * @return 新增的消息ID
     */
    Long sendMessage(Byte senderType, Long senderId, MessageDTO dto);

    /**
     * 获取消息列表
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @param msgType  消息类型
     * @param isRead   是否已读
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 消息分页列表
     */
    IPage<MessageVO> getMessageList(Byte userType, Long userId, Byte msgType, Byte isRead, Integer pageNum, Integer pageSize);

    /**
     * 获取对话记录
     *
     * @param userType    当前用户类型
     * @param userId      当前用户ID
     * @param contactType 联系人类型
     * @param contactId   联系人ID
     * @param pageNum     页码
     * @param pageSize    每页数量
     * @return 对话消息分页列表
     */
    IPage<MessageVO> getConversation(Byte userType, Long userId, Byte contactType, Long contactId, Integer pageNum, Integer pageSize);

    /**
     * 获取联系人列表
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @return 联系人列表
     */
    List<ContactVO> getContactList(Byte userType, Long userId);

    /**
     * 获取未读消息数
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @return 未读消息数
     */
    Integer getUnreadCount(Byte userType, Long userId);

    /**
     * 标记消息已读
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @param id       消息ID
     * @return 是否成功
     */
    boolean markAsRead(Byte userType, Long userId, Long id);

    /**
     * 标记所有消息已读
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @return 是否成功
     */
    boolean markAllAsRead(Byte userType, Long userId);

    /**
     * 删除消息（撤回）
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @param id       消息ID
     * @return 是否成功
     */
    boolean deleteMessage(Byte userType, Long userId, Long id);

    /**
     * 标记与某联系人的所有消息已读
     *
     * @param userType    当前用户类型
     * @param userId      当前用户ID
     * @param contactType 联系人类型
     * @param contactId   联系人ID
     * @return 是否成功
     */
    boolean markConversationAsRead(Byte userType, Long userId, Byte contactType, Long contactId);

    // ==================== 9.3 选择联系人功能 ====================

    /**
     * 家长端：获取有业务关系的机构列表
     *
     * @param parentId 家长ID
     * @param keyword  搜索关键词（可选）
     * @return 机构列表
     */
    List<AvailableContactVO> getInstitutionsForParent(Long parentId, String keyword);

    /**
     * 家长端：获取服务过的保姆列表
     *
     * @param parentId 家长ID
     * @param keyword  搜索关键词（可选）
     * @return 保姆列表
     */
    List<AvailableContactVO> getNanniesForParent(Long parentId, String keyword);

    /**
     * 保姆端：获取服务过的家长列表
     *
     * @param nannyId 保姆ID
     * @param keyword 搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> getParentsForNanny(Long nannyId, String keyword);

    /**
     * 保姆端：获取所属机构
     *
     * @param nannyId 保姆ID
     * @return 机构列表（可能为空）
     */
    List<AvailableContactVO> getInstitutionsForNanny(Long nannyId);

    /**
     * 机构端：获取有业务关系的家长列表
     *
     * @param institutionId 机构ID
     * @param keyword       搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> getParentsForInstitution(Long institutionId, String keyword);

    /**
     * 管理员端：获取所有家长列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> getAllParents(String keyword);

    /**
     * 管理员端：获取所有保姆列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 保姆列表
     */
    List<AvailableContactVO> getAllNannies(String keyword);

    /**
     * 管理员端：获取所有机构列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 机构列表
     */
    List<AvailableContactVO> getAllInstitutions(String keyword);
}
