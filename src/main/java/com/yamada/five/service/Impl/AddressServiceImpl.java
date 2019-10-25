package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.dto.AddressDTO;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveException;
import com.yamada.five.mapper.AddressDetailMapper;
import com.yamada.five.mapper.AddressMapper;
import com.yamada.five.mapper.UserMapper;
import com.yamada.five.pojo.Address;
import com.yamada.five.pojo.AddressDetail;
import com.yamada.five.service.AddressDetailService;
import com.yamada.five.service.AddressService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressDetailMapper addressDetailMapper;

    @Autowired
    private AddressDetailService addressDetailService;

    @Override
    public Address getById(Long addressId) {
        Address address = addressMapper.selectById(addressId);
        if (address == null) {
            throw new FiveException(ResultEnums.ADDRESS_NOT_EXIST);
        }
        return address;
    }

    @Override
    public List<Address> getByUserId(Long userId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return addressMapper.selectList(queryWrapper);
    }

    @Override
    public Long insert(Address address) {
        int result = addressMapper.insert(address);
        if (result == 0) {
            throw new FiveException(ResultEnums.ADDRESS_INSERT_ERROR, "/address/toAdd");
        }
        return address.getAddressId();
    }

    @Override
    public AddressDTO addressToAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        AddressDTO addressDTO = new AddressDTO();
        BeanUtils.copyProperties(address, addressDTO);
        addressDTO.setUser(userMapper.selectById(address.getUserId()));
        AddressDetail addressDetail = addressDetailMapper.selectById(address.getAddressDetailId());
        addressDTO.setAddressDetailDTO(addressDetailService.addressDetailToAddressDetailDTO(addressDetail));
        return addressDTO;
    }

    @Override
    public void update(Address address) {
        int result = addressMapper.updateById(address);
        if (result != 1) {
            throw new FiveException(ResultEnums.ADDRESS_UPDATE_ERROR);
        }
    }

    @Override
    public void deleteById(Long addressId) {
        int result = addressMapper.deleteById(addressId);
        if (result != 1) {
            throw new FiveException(ResultEnums.ADDRESS_DELETE_ERROR);
        }
    }

    @Override
    public List<AddressDTO> addressListToAddressDTOList(List<Address> addressList) {
        List<AddressDTO> addressDTOList = new ArrayList<>();
        for (Address address: addressList) {
            addressDTOList.add(addressToAddressDTO(address));
        }
        return addressDTOList;
    }
}
