package com.yamada.five.controller;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.constant.SMSConstant;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveException;
import com.yamada.five.pojo.User;
import com.yamada.five.service.UserService;
import com.yamada.five.utils.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/me")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/{userId}")
    public String userDetail(@PathVariable("userId") Long userId, Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userInfo.getUserId().equals(userId)) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        User user = userService.getById(userId);
        UserDTO userDTO = userService.userToUserDTO(user);
        map.put("userDTO", userDTO);
        return "me/detail";
    }

    @PostMapping("/{userId}")
    public String update(@PathVariable("userId") Long userId, @RequestParam("name") String name,
                         @RequestParam("phone") String phone, Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userInfo.getUserId().equals(userId)) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        User user = userService.getById(userId);
        user.setName(name);
        user.setPhone(phone);
        Integer result = userService.updateById(user);
        if (result != 1) {
            throw new FiveException(ResultEnums.USER_UPDATE_ERROR, "/me/" + userId);
        }
        map.put("msg", "修改成功！");
        map.put("url", "/me");
        return "common/success";
    }

    @GetMapping("/toUpdatePassword")
    public String toUpdatePassword(Map<String, Object> map) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String studentId = null;
        if (principal instanceof UserDetails) {
            // 已登录
            studentId = ((UserDetails) principal).getUsername();
        }
        map.put("studentId", studentId);
        return "me/updatePassword";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("studentId") String studentId, @RequestParam("new_password") String newPassword,
            @RequestParam("checkCode") String checkCode, Map<String, Object> map) {
        // 对表单进行校验
        if (!VerificationUtil.isStudentId(studentId) || !VerificationUtil.isPassword(newPassword)) {
            throw new FiveException(ResultEnums.FORM_VERIFICATION_ERROR, "/toUpdatePassword");
        }

        //验证手机短信
        User user = userService.studentIdIsRepeat(studentId);
        if (user == null) {
            throw new FiveException(ResultEnums.USER_NOT_EXIST, "/toUpdatePassword");
        }
        String code = redisTemplate.opsForValue().get(SMSConstant.CODE_PREFIX + user.getPhone());
        if (code == null || code.isEmpty() || !code.equals(checkCode)) {
            throw new FiveException(ResultEnums.MSM_VERIFICATION_ERROR, "/toUpdatePassword");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        Integer result = userService.updateById(user);
        if (result != 1) {
            throw new FiveException(ResultEnums.USER_UPDATE_ERROR, "/me");
        }
        map.put("msg", "修改成功！");
        map.put("url", "/me");
        return "common/success";
    }
}
