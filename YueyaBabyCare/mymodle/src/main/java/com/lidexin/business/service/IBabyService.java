package com.lidexin.business.service;

import com.lidexin.business.dto.BabyDTO;
import com.lidexin.business.po.Baby;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.vo.BabyVO;

import java.util.List;

/**
 * <p>
 * 婴幼儿信息表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IBabyService extends IService<Baby> {

    /**
     * 新增婴幼儿
     *
     * @param dto      婴幼儿信息
     * @param parentId 家长ID
     * @return 是否成功
     */
    boolean addBaby(BabyDTO dto, Long parentId);

    /**
     * 获取婴幼儿信息
     *
     * @param id       婴幼儿ID
     * @param parentId 家长ID（用于权限校验，管理员可传null）
     * @return 婴幼儿信息
     */
    BabyVO getBabyById(Long id, Long parentId);

    /**
     * 修改婴幼儿信息
     *
     * @param dto      婴幼儿信息
     * @param parentId 家长ID（用于权限校验）
     * @return 是否成功
     */
    boolean updateBaby(BabyDTO dto, Long parentId);

    /**
     * 删除婴幼儿
     *
     * @param id       婴幼儿ID
     * @param parentId 家长ID（用于权限校验）
     * @return 是否成功
     */
    boolean deleteBaby(Long id, Long parentId);

    /**
     * 获取当前家长的婴幼儿列表
     *
     * @param parentId 家长ID
     * @return 婴幼儿列表
     */
    List<BabyVO> listByParentId(Long parentId);

    /**
     * 获取婴幼儿的保姆列表
     *
     * @param babyId   婴幼儿ID
     * @param parentId 家长ID（用于权限校验）
     * @return 保姆列表
     */
    List<?> getCaregivers(Long babyId, Long parentId);

    /**
     * 修改婴幼儿头像
     *
     * @param id        婴幼儿ID
     * @param avatarUrl 头像URL
     * @param parentId  家长ID（用于权限校验）
     * @return 是否成功
     */
    boolean updateAvatar(Long id, String avatarUrl, Long parentId);

    /**
     * 管理员根据家长ID查询婴幼儿列表
     *
     * @param parentId 家长ID
     * @return 婴幼儿列表
     */
    List<BabyVO> adminListByParentId(Long parentId);

    /**
     * 管理员获取所有婴幼儿列表
     *
     * @return 婴幼儿列表
     */
    List<BabyVO> adminListAll();
}
