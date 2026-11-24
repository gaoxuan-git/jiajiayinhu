package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.dto.MessageDTO;
import com.lidexin.business.mapper.MessageMapper;
import com.lidexin.business.mapper.NannyMapper;
import com.lidexin.business.mapper.ParentMapper;
import com.lidexin.business.mapper.InstitutionMapper;
import com.lidexin.business.po.Message;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.Parent;
import com.lidexin.business.po.Institution;
import com.lidexin.business.service.IMessageService;
import com.lidexin.business.vo.AvailableContactVO;
import com.lidexin.business.vo.ContactVO;
import com.lidexin.business.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 消息/留言表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ParentMapper parentMapper;

    @Autowired
    private NannyMapper nannyMapper;

    @Autowired
    private InstitutionMapper institutionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(Byte senderType, Long senderId, MessageDTO dto) {
        Message message = new Message();
        message.setSenderType(senderType);
        message.setSenderId(senderId);
        message.setReceiverType(dto.getReceiverType());
        message.setReceiverId(dto.getReceiverId());
        message.setMsgType(dto.getMsgType() != null ? dto.getMsgType() : (byte) 3); // 默认留言反馈
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setIsRead((byte) 0);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted((byte) 0);

        this.save(message);
        return message.getId();
    }

    @Override
    public IPage<MessageVO> getMessageList(Byte userType, Long userId, Byte msgType, Byte isRead, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getDeleted, (byte) 0)
                .eq(Message::getReceiverType, userType)
                .eq(Message::getReceiverId, userId);

        if (msgType != null) {
            wrapper.eq(Message::getMsgType, msgType);
        }
        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }

        wrapper.orderByDesc(Message::getCreateTime);

        Page<Message> page = new Page<>(pageNum, pageSize);
        IPage<Message> messagePage = this.page(page, wrapper);

        // 转换为VO
        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(convertToVOList(messagePage.getRecords(), userType, userId));
        return voPage;
    }

    @Override
    public IPage<MessageVO> getConversation(Byte userType, Long userId, Byte contactType, Long contactId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getDeleted, (byte) 0)
                .eq(Message::getMsgType, (byte) 3) // 只查询留言反馈类型的消息
                .and(w -> w
                        // 我发送给对方的
                        .nested(inner -> inner
                                .eq(Message::getSenderType, userType)
                                .eq(Message::getSenderId, userId)
                                .eq(Message::getReceiverType, contactType)
                                .eq(Message::getReceiverId, contactId))
                        .or()
                        // 对方发送给我的
                        .nested(inner -> inner
                                .eq(Message::getSenderType, contactType)
                                .eq(Message::getSenderId, contactId)
                                .eq(Message::getReceiverType, userType)
                                .eq(Message::getReceiverId, userId)));

        wrapper.orderByAsc(Message::getCreateTime);

        Page<Message> page = new Page<>(pageNum, pageSize);
        IPage<Message> messagePage = this.page(page, wrapper);

        // 打印原始消息数据
        System.out.println("=== 原始消息数据 ===");
        for (Message msg : messagePage.getRecords()) {
            System.out.println("消息ID: " + msg.getId() + ", 内容: [" + msg.getContent() + "], 发送人类型: " + msg.getSenderType() + ", 发送人ID: " + msg.getSenderId());
        }
        System.out.println("==================");

        // 转换为VO
        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        List<MessageVO> voList = convertToVOList(messagePage.getRecords(), userType, userId);
        
        // 打印转换后的VO数据
        System.out.println("=== 转换后的VO数据 ===");
        for (MessageVO vo : voList) {
            System.out.println("VO消息ID: " + vo.getId() + ", VO内容: [" + vo.getContent() + "], isSelf: " + vo.getIsSelf());
        }
        System.out.println("==================");
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<ContactVO> getContactList(Byte userType, Long userId) {
        List<ContactVO> contacts = messageMapper.selectContactList(userType, userId);

        // 填充联系人信息
        for (ContactVO contact : contacts) {
            fillContactInfo(contact);
            // 获取未读消息数
            Integer unreadCount = messageMapper.selectUnreadCountWithContact(
                    userType, userId, contact.getContactType(), contact.getContactId());
            contact.setUnreadCount(unreadCount != null ? unreadCount : 0);
        }

        return contacts;
    }

    @Override
    public Integer getUnreadCount(Byte userType, Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getDeleted, (byte) 0)
                .eq(Message::getReceiverType, userType)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, (byte) 0);

        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAsRead(Byte userType, Long userId, Long id) {
        Message message = this.getById(id);
        if (message == null || message.getDeleted() == 1) {
            return false;
        }

        // 验证是否是接收人
        if (!message.getReceiverType().equals(userType) || !message.getReceiverId().equals(userId)) {
            return false;
        }

        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getId, id)
                .set(Message::getIsRead, (byte) 1)
                .set(Message::getReadTime, LocalDateTime.now());

        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markAllAsRead(Byte userType, Long userId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverType, userType)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getIsRead, (byte) 0)
                .eq(Message::getDeleted, (byte) 0)
                .set(Message::getIsRead, (byte) 1)
                .set(Message::getReadTime, LocalDateTime.now());

        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMessage(Byte userType, Long userId, Long id) {
        Message message = this.getById(id);
        if (message == null || message.getDeleted() == 1) {
            return false;
        }

        // 验证是否是发送人或接收人
        boolean isSender = message.getSenderType().equals(userType) && message.getSenderId().equals(userId);
        boolean isReceiver = message.getReceiverType().equals(userType) && message.getReceiverId().equals(userId);

        if (!isSender && !isReceiver) {
            return false;
        }

        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getId, id)
                .set(Message::getDeleted, (byte) 1);

        return this.update(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markConversationAsRead(Byte userType, Long userId, Byte contactType, Long contactId) {
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getReceiverType, userType)
                .eq(Message::getReceiverId, userId)
                .eq(Message::getSenderType, contactType)
                .eq(Message::getSenderId, contactId)
                .eq(Message::getIsRead, (byte) 0)
                .eq(Message::getDeleted, (byte) 0)
                .set(Message::getIsRead, (byte) 1)
                .set(Message::getReadTime, LocalDateTime.now());

        return this.update(wrapper);
    }

    /**
     * 转换为VO列表
     */
    private List<MessageVO> convertToVOList(List<Message> messages, Byte currentUserType, Long currentUserId) {
        List<MessageVO> voList = new ArrayList<>();
        for (Message message : messages) {
            voList.add(convertToVO(message, currentUserType, currentUserId));
        }
        return voList;
    }

    /**
     * 转换为VO
     */
    private MessageVO convertToVO(Message message, Byte currentUserType, Long currentUserId) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setSenderType(message.getSenderType());
        vo.setSenderTypeName(getUserTypeName(message.getSenderType()));
        vo.setSenderId(message.getSenderId());
        vo.setReceiverType(message.getReceiverType());
        vo.setReceiverTypeName(getUserTypeName(message.getReceiverType()));
        vo.setReceiverId(message.getReceiverId());
        vo.setMsgType(message.getMsgType());
        vo.setMsgTypeName(getMsgTypeName(message.getMsgType()));
        vo.setTitle(message.getTitle());
        vo.setContent(message.getContent());
        vo.setIsRead(message.getIsRead());
        vo.setReadTime(message.getReadTime());
        vo.setCreateTime(message.getCreateTime());

        // 判断是否是自己发送的
        vo.setIsSelf(message.getSenderType().equals(currentUserType) && message.getSenderId().equals(currentUserId));

        // 填充发送人和接收人信息
        fillUserInfo(vo);

        return vo;
    }

    /**
     * 填充用户信息
     */
    private void fillUserInfo(MessageVO vo) {
        // 填充发送人信息
        String[] senderInfo = getUserInfo(vo.getSenderType(), vo.getSenderId());
        vo.setSenderName(senderInfo[0]);
        vo.setSenderAvatar(senderInfo[1]);

        // 填充接收人信息
        String[] receiverInfo = getUserInfo(vo.getReceiverType(), vo.getReceiverId());
        vo.setReceiverName(receiverInfo[0]);
        vo.setReceiverAvatar(receiverInfo[1]);
    }

    /**
     * 填充联系人信息
     */
    private void fillContactInfo(ContactVO contact) {
        String[] info = getUserInfo(contact.getContactType(), contact.getContactId());
        contact.setContactName(info[0]);
        contact.setContactAvatar(info[1]);
        contact.setContactTypeName(getUserTypeName(contact.getContactType()));
    }

    /**
     * 获取用户信息
     */
    private String[] getUserInfo(Byte userType, Long userId) {
        String name = "未知用户";
        String avatar = null;

        if (userType == null || userId == null) {
            return new String[]{name, avatar};
        }

        switch (userType) {
            case 1: // 家长
                Parent parent = parentMapper.selectById(userId);
                if (parent != null) {
                    name = parent.getRealName() != null ? parent.getRealName() : parent.getUsername();
                    avatar = parent.getAvatar();
                }
                break;
            case 2: // 保姆
                Nanny nanny = nannyMapper.selectById(userId);
                if (nanny != null) {
                    name = nanny.getRealName() != null ? nanny.getRealName() : nanny.getUsername();
                    avatar = nanny.getAvatar();
                }
                break;
            case 3: // 机构
                Institution institution = institutionMapper.selectById(userId);
                if (institution != null) {
                    name = institution.getName();
                    avatar = institution.getLogo();
                }
                break;
            case 4: // 管理员
                name = "系统管理员";
                break;
            default:
                break;
        }

        return new String[]{name, avatar};
    }

    /**
     * 获取用户类型名称
     */
    private String getUserTypeName(Byte userType) {
        if (userType == null) return "未知";
        switch (userType) {
            case 1: return "家长";
            case 2: return "保姆";
            case 3: return "机构";
            case 4: return "管理员";
            default: return "未知";
        }
    }

    /**
     * 获取消息类型名称
     */
    private String getMsgTypeName(Byte msgType) {
        if (msgType == null) return "未知";
        switch (msgType) {
            case 1: return "系统消息";
            case 2: return "护理提醒";
            case 3: return "留言反馈";
            default: return "未知";
        }
    }

    // ==================== 9.3 选择联系人功能实现 ====================

    @Override
    public List<AvailableContactVO> getInstitutionsForParent(Long parentId, String keyword) {
        List<AvailableContactVO> list = messageMapper.selectInstitutionsForParent(parentId, keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 3);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getNanniesForParent(Long parentId, String keyword) {
        List<AvailableContactVO> list = messageMapper.selectNanniesForParent(parentId, keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 2);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getParentsForNanny(Long nannyId, String keyword) {
        List<AvailableContactVO> list = messageMapper.selectParentsForNanny(nannyId, keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 1);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getInstitutionsForNanny(Long nannyId) {
        List<AvailableContactVO> list = new ArrayList<>();
        AvailableContactVO institution = messageMapper.selectInstitutionForNanny(nannyId);
        if (institution != null) {
            institution.setContactType((byte) 3);
            list.add(institution);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getParentsForInstitution(Long institutionId, String keyword) {
        List<AvailableContactVO> list = messageMapper.selectParentsForInstitution(institutionId, keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 1);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getAllParents(String keyword) {
        List<AvailableContactVO> list = messageMapper.selectAllParents(keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 1);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getAllNannies(String keyword) {
        List<AvailableContactVO> list = messageMapper.selectAllNannies(keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 2);
        }
        return list;
    }

    @Override
    public List<AvailableContactVO> getAllInstitutions(String keyword) {
        List<AvailableContactVO> list = messageMapper.selectAllInstitutions(keyword);
        // 设置联系人类型名称
        for (AvailableContactVO vo : list) {
            vo.setContactType((byte) 3);
        }
        return list;
    }
}
