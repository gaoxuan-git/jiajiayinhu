package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lidexin.business.po.Institution;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.vo.NannyPerformanceVO;

import java.util.List;

/**
 * <p>
 * 机构表 服务类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IInstitutionService extends IService<Institution> {
    
    /**
     * 获取所有机构列表（管理员使用）
     * 
     * @return 机构列表
     */
    List<Institution> getAllInstitutions();
    
    /**
     * 机构登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 根据 token 获取机构信息
     * 
     * @param token JWT token
     * @return 机构信息
     */
    Institution getInstitutionByToken(String token);
    
    /**
     * 退出登录
     * 
     * @param token JWT token
     */
    void logout(String token);
    
    /**
     * 更新机构信息
     * 
     * @param institution 机构信息
     * @return 是否成功
     */
    boolean updateProfile(Institution institution);
    
    /**
     * 获取机构下的保姆列表（分页）
     * 
     * @param institutionId 机构ID
     * @param status 审核状态（null-全部，0-待审核，1-已审核）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Nanny> getNannyList(Long institutionId, Byte status, Integer pageNum, Integer pageSize);
    
    /**
     * 审核保姆（修改状态）
     * 
     * @param institutionId 机构ID
     * @param nannyId 保姆ID
     * @param status 新状态（1-通过，2-拒绝）
     * @return 是否成功
     */
    boolean auditNanny(Long institutionId, Long nannyId, Byte status);
    
    /**
     * 获取机构员工数（保姆数量）
     * 
     * @param institutionId 机构ID
     * @return 员工数
     */
    Long getStaffCount(Long institutionId);
    
    /**
     * 获取机构服务订单数
     * 
     * @param institutionId 机构ID
     * @return 订单数
     */
    Long getServiceCount(Long institutionId);
    
    /**
     * 获取所有机构列表（管理员使用，分页）
     * 
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Institution> getInstitutionPage(Integer pageNum, Integer pageSize);
    
    /**
     * 审核机构（管理员）
     * 
     * @param institutionId 机构ID
     * @param status 审核状态（0-禁用，1-正常）
     * @return 是否成功
     */
    boolean auditInstitution(Long institutionId, Byte status);
    
    /**
     * 搜索机构（家长查看）
     * 
     * @param keyword 搜索关键词（机构名称）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    Page<Institution> searchInstitutions(String keyword, Integer pageNum, Integer pageSize);
    
    /**
     * 机构修改保姆信息
     * 
     * @param institutionId 机构ID
     * @param nannyId 保姆ID
     * @param nanny 保姆信息（可修改字段）
     * @return 是否成功
     */
    boolean updateNannyInfo(Long institutionId, Long nannyId, Nanny nanny);
    
    /**
     * 机构删除/解雇保姆
     * 
     * @param institutionId 机构ID
     * @param nannyId 保姆ID
     * @return 是否成功
     */
    boolean deleteNanny(Long institutionId, Long nannyId);
    
    /**
     * 获取保姆绩效数据
     * 
     * @param institutionId 机构ID
     * @param nannyId 保姆ID
     * @return 保姆绩效数据
     */
    NannyPerformanceVO getNannyPerformance(Long institutionId, Long nannyId);
}
