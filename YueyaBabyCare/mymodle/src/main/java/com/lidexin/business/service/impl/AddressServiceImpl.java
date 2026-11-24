package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.lidexin.business.dto.AddressDTO;
import com.lidexin.business.po.Address;
import com.lidexin.business.mapper.AddressMapper;
import com.lidexin.business.service.IAddressService;
import com.lidexin.business.vo.AddressVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 地址表 服务实现类
 * </p>
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {

    @Override
    public List<AddressVO> getAddressList(Byte relationType, Long relationId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getRelationType, relationType)
                .eq(Address::getRelationId, relationId)
                .eq(Address::getDeleted, (byte) 0)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdateTime);

        List<Address> addresses = this.list(wrapper);
        return convertToVOList(addresses);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addAddress(Byte relationType, Long relationId, AddressDTO dto) {
        Address address = new Address();
        address.setRelationType(relationType);
        address.setRelationId(relationId);
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setLongitude(dto.getLongitude());
        address.setLatitude(dto.getLatitude());
        address.setContactName(dto.getContactName());
        address.setContactPhone(dto.getContactPhone());
        address.setCreateTime(LocalDateTime.now());
        address.setUpdateTime(LocalDateTime.now());
        address.setDeleted((byte) 0);

        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            cancelOtherDefaultAddress(relationType, relationId);
            address.setIsDefault((byte) 1);
        } else {
            // 如果是第一个地址，自动设为默认
            long count = this.count(new LambdaQueryWrapper<Address>()
                    .eq(Address::getRelationType, relationType)
                    .eq(Address::getRelationId, relationId)
                    .eq(Address::getDeleted, (byte) 0));
            address.setIsDefault(count == 0 ? (byte) 1 : (byte) 0);
        }

        this.save(address);
        return address.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAddress(Byte relationType, Long relationId, Long id, AddressDTO dto) {
        // 验证地址归属
        Address existingAddress = getAndValidateAddress(relationType, relationId, id);
        if (existingAddress == null) {
            return false;
        }

        Address address = new Address();
        address.setId(id);
        address.setProvince(dto.getProvince());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setDetailAddress(dto.getDetailAddress());
        address.setLongitude(dto.getLongitude());
        address.setLatitude(dto.getLatitude());
        address.setContactName(dto.getContactName());
        address.setContactPhone(dto.getContactPhone());
        address.setUpdateTime(LocalDateTime.now());

        // 如果设置为默认地址，先取消其他默认地址
        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            cancelOtherDefaultAddress(relationType, relationId);
            address.setIsDefault((byte) 1);
        }

        return this.updateById(address);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAddress(Byte relationType, Long relationId, Long id) {
        // 验证地址归属
        Address existingAddress = getAndValidateAddress(relationType, relationId, id);
        if (existingAddress == null) {
            return false;
        }

        // 逻辑删除
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getId, id)
                .set(Address::getDeleted, (byte) 1)
                .set(Address::getUpdateTime, LocalDateTime.now());

        boolean result = this.update(wrapper);

        // 如果删除的是默认地址，将最新的地址设为默认
        if (result && existingAddress.getIsDefault() != null && existingAddress.getIsDefault() == 1) {
            setFirstAsDefault(relationType, relationId);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Byte relationType, Long relationId, Long id) {
        // 验证地址归属
        Address existingAddress = getAndValidateAddress(relationType, relationId, id);
        if (existingAddress == null) {
            return false;
        }

        // 取消其他默认地址
        cancelOtherDefaultAddress(relationType, relationId);

        // 设置当前地址为默认
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getId, id)
                .set(Address::getIsDefault, (byte) 1)
                .set(Address::getUpdateTime, LocalDateTime.now());

        return this.update(wrapper);
    }

    @Override
    public AddressVO getAddressById(Byte relationType, Long relationId, Long id) {
        Address address = getAndValidateAddress(relationType, relationId, id);
        return address != null ? convertToVO(address) : null;
    }

    @Override
    public AddressVO getDefaultAddress(Byte relationType, Long relationId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getRelationType, relationType)
                .eq(Address::getRelationId, relationId)
                .eq(Address::getIsDefault, (byte) 1)
                .eq(Address::getDeleted, (byte) 0)
                .last("LIMIT 1");

        Address address = this.getOne(wrapper);
        return address != null ? convertToVO(address) : null;
    }

    /**
     * 验证地址归属并返回地址
     */
    private Address getAndValidateAddress(Byte relationType, Long relationId, Long id) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getId, id)
                .eq(Address::getRelationType, relationType)
                .eq(Address::getRelationId, relationId)
                .eq(Address::getDeleted, (byte) 0);
        return this.getOne(wrapper);
    }

    /**
     * 取消其他默认地址
     */
    private void cancelOtherDefaultAddress(Byte relationType, Long relationId) {
        LambdaUpdateWrapper<Address> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Address::getRelationType, relationType)
                .eq(Address::getRelationId, relationId)
                .eq(Address::getDeleted, (byte) 0)
                .set(Address::getIsDefault, (byte) 0);
        this.update(wrapper);
    }

    /**
     * 将第一个地址设为默认
     */
    private void setFirstAsDefault(Byte relationType, Long relationId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getRelationType, relationType)
                .eq(Address::getRelationId, relationId)
                .eq(Address::getDeleted, (byte) 0)
                .orderByDesc(Address::getUpdateTime)
                .last("LIMIT 1");

        Address firstAddress = this.getOne(wrapper);
        if (firstAddress != null) {
            LambdaUpdateWrapper<Address> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Address::getId, firstAddress.getId())
                    .set(Address::getIsDefault, (byte) 1);
            this.update(updateWrapper);
        }
    }

    /**
     * 转换为VO列表
     */
    private List<AddressVO> convertToVOList(List<Address> addresses) {
        List<AddressVO> voList = new ArrayList<>();
        for (Address address : addresses) {
            voList.add(convertToVO(address));
        }
        return voList;
    }

    /**
     * 转换为VO
     */
    private AddressVO convertToVO(Address address) {
        AddressVO vo = new AddressVO();
        vo.setId(address.getId());
        vo.setRelationType(address.getRelationType());
        vo.setRelationId(address.getRelationId());
        vo.setProvince(address.getProvince());
        vo.setCity(address.getCity());
        vo.setDistrict(address.getDistrict());
        vo.setDetailAddress(address.getDetailAddress());
        vo.setFullAddress(address.getProvince() + address.getCity() +
                address.getDistrict() + address.getDetailAddress());
        vo.setLongitude(address.getLongitude());
        vo.setLatitude(address.getLatitude());
        vo.setIsDefault(address.getIsDefault());
        vo.setContactName(address.getContactName());
        vo.setContactPhone(address.getContactPhone());
        vo.setCreateTime(address.getCreateTime());
        vo.setUpdateTime(address.getUpdateTime());
        return vo;
    }
}
