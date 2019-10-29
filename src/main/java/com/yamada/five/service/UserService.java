package com.yamada.five.service;

import com.yamada.five.dto.UserDTO;
import com.yamada.five.pojo.User;

public interface UserService {

    User getById(Long userId);

    User registerAndLogin(String studentId);

    User studentIdIsRepeat(String studentId);

    Integer insert(User user);

    Integer updateById(User user);

    UserDTO userToUserDTO(User user);

    void judgeReceipt(Long userId);
}
