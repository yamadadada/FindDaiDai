package com.yamada.five.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.enums.UserStatusEnum;
import com.yamada.five.exception.FiveException;
import com.yamada.five.mapper.AddressMapper;
import com.yamada.five.mapper.OrderMapper;
import com.yamada.five.mapper.UserMapper;
import com.yamada.five.pojo.Address;
import com.yamada.five.pojo.Order;
import com.yamada.five.pojo.User;
import com.yamada.five.service.AddressService;
import com.yamada.five.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OrderMapper orderMapper;

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

    @Override
    public void judgeReceipt(Long userId) {
        User user = getById(userId);
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("receipt_user_id", userId);
        List<Order> orderList = orderMapper.selectList(wrapper);
        if (orderList.size() <= 5) {
            return;
        }
        int sum = 0;
        for (Order order: orderList) {
            sum += order.getPlaceRemark();
        }
        double avgRemark = sum / orderList.size();
        if (avgRemark < 50) {
            // 评价过低，无法接单
            user.setUserStatus(UserStatusEnum.BANNED.getCode());
            userMapper.updateById(user);
        }
    }
}
