package com.lidexin.business.mapper;

import com.lidexin.business.po.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lidexin.business.vo.AvailableContactVO;
import com.lidexin.business.vo.ContactVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 消息/留言表 Mapper 接口
 * </p>
 *
 * @author 张三
 * @since 2025-12-24
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取联系人列表
     *
     * @param userType 当前用户类型
     * @param userId   当前用户ID
     * @return 联系人列表
     */
    List<ContactVO> selectContactList(@Param("userType") Byte userType, @Param("userId") Long userId);

    /**
     * 获取与某联系人的未读消息数
     *
     * @param receiverType 接收人类型
     * @param receiverId   接收人ID
     * @param senderType   发送人类型
     * @param senderId     发送人ID
     * @return 未读消息数
     */
    Integer selectUnreadCountWithContact(@Param("receiverType") Byte receiverType,
                                         @Param("receiverId") Long receiverId,
                                         @Param("senderType") Byte senderType,
                                         @Param("senderId") Long senderId);

    /**
     * 家长端：获取有业务关系的机构列表
     *
     * @param parentId 家长ID
     * @param keyword  搜索关键词（可选）
     * @return 机构列表
     */
    List<AvailableContactVO> selectInstitutionsForParent(@Param("parentId") Long parentId,
                                                          @Param("keyword") String keyword);

    /**
     * 家长端：获取服务过的保姆列表
     *
     * @param parentId 家长ID
     * @param keyword  搜索关键词（可选）
     * @return 保姆列表
     */
    List<AvailableContactVO> selectNanniesForParent(@Param("parentId") Long parentId,
                                                     @Param("keyword") String keyword);

    /**
     * 保姆端：获取服务过的家长列表
     *
     * @param nannyId 保姆ID
     * @param keyword 搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> selectParentsForNanny(@Param("nannyId") Long nannyId,
                                                    @Param("keyword") String keyword);

    /**
     * 保姆端：获取所属机构
     *
     * @param nannyId 保姆ID
     * @return 机构信息
     */
    AvailableContactVO selectInstitutionForNanny(@Param("nannyId") Long nannyId);

    /**
     * 机构端：获取有业务关系的家长列表
     *
     * @param institutionId 机构ID
     * @param keyword       搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> selectParentsForInstitution(@Param("institutionId") Long institutionId,
                                                          @Param("keyword") String keyword);

    /**
     * 管理员端：获取所有家长列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 家长列表
     */
    List<AvailableContactVO> selectAllParents(@Param("keyword") String keyword);

    /**
     * 管理员端：获取所有保姆列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 保姆列表
     */
    List<AvailableContactVO> selectAllNannies(@Param("keyword") String keyword);

    /**
     * 管理员端：获取所有机构列表
     *
     * @param keyword 搜索关键词（可选）
     * @return 机构列表
     */
    List<AvailableContactVO> selectAllInstitutions(@Param("keyword") String keyword);
}
