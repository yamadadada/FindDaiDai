package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.enums.UserStatusEnum;
import com.yamada.five.exception.FiveException;
import com.yamada.five.mapper.AddressMapper;
import com.yamada.five.mapper.UserMapper;
import com.yamada.five.pojo.Address;
import com.yamada.five.pojo.User;
import com.yamada.five.service.AddressService;
import com.yamada.five.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private AddressService addressService;

    @Override
    public User getById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new FiveException(ResultEnums.USER_NOT_EXIST);
        }
        return user;
    }

    /**
     * 注册时判重，登录时做验证
     * @param studentId
     * @return
     */
    @Override
    public User registerAndLogin(String studentId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return userMapper.selectOne(wrapper);
    }

    /**
     * 返回null表示没有重复，返回非null表示重复
     * @param studentId
     * @return
     */
    @Override
    public User studentIdIsRepeat(String studentId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public Integer insert(User user) {
        return userMapper.insert(user);
    }

    @Override
    public Integer updateById(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public UserDTO userToUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        Address address = addressMapper.selectById(user.getAddressId());
        userDTO.setAddressDTO(addressService.addressToAddressDTO(address));
        for (UserStatusEnum statusEnum: UserStatusEnum.values()) {
            if (statusEnum.getCode().equals(user.getUserStatus())) {
                userDTO.setUserStatusEnum(statusEnum);
                break;
            }
        }
        return userDTO;
    }
}
