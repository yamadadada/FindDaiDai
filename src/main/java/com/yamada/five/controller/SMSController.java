package com.yamada.five.controller;

import com.yamada.five.enums.ResultEnums;
import com.yamada.five.pojo.User;
import com.yamada.five.service.SMSService;
import com.yamada.five.service.UserService;
import com.yamada.five.utils.ResultVOUtil;
import com.yamada.five.utils.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SMSController {

    @Autowired
    private SMSService smsService;

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public Object register(@RequestParam("tel") String tel) {
        if (!VerificationUtil.isTel(tel)) {
            return ResultVOUtil.error(ResultEnums.PHONE_ERROR);
        }
        smsService.sendMsg(tel);
        return ResultVOUtil.success(null);
    }

    /**
     * 修改密码
     * @return
     */
    @GetMapping("/updatePassword")
    public Object updatePassword(@RequestParam("studentId") String studentId) {
        if (!VerificationUtil.isStudentId(studentId)) {
            return ResultVOUtil.error(ResultEnums.FORM_VERIFICATION_ERROR);
        }
        User user = userService.studentIdIsRepeat(studentId);
        if (user == null) {
            return ResultVOUtil.error(ResultEnums.USER_NOT_EXIST);
        }
        String tel = user.getPhone();
        smsService.sendMsg(tel);
        return ResultVOUtil.success(null);
    }
}
