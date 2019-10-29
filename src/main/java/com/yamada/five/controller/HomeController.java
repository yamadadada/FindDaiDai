package com.yamada.five.controller;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.constant.SMSConstant;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveException;
import com.yamada.five.pojo.ImageCode;
import com.yamada.five.pojo.Order;
import com.yamada.five.pojo.User;
import com.yamada.five.service.OrderService;
import com.yamada.five.service.UserService;
import com.yamada.five.service.ValidateCodeService;
import com.yamada.five.utils.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ValidateCodeService validateCodeService;

    @GetMapping("")
    public String empty(Map<String, Object> map) {
        return index(map);
    }

    @GetMapping("/index")
    public String index(Map<String, Object> map) {
        List<Order> orderList = orderService.getByOrderStatus(OrderStatusEnum.HAVE_PAY);
        // 筛选出未到期订单
        orderList = orderList.stream().filter(o -> o.getDeadline().getTime() > new Date().getTime()).collect(Collectors.toList());
        map.put("orderDTOList", orderService.addItemList(orderService.orderListToOrderDTO(orderList)));
        return "index";
    }

    @GetMapping("/me")
    public String me(Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userInfo.getUserId());
        UserDTO userDTO = userService.userToUserDTO(user);
        map.put("userDTO", userDTO);
        return "me/me";
    }

    @GetMapping("/login")
    public String userLogin() {
        return "signin";
    }

    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";
    }

    @GetMapping("/imageCode")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = validateCodeService.createCode(new ServletWebRequest(request));
        request.getSession().setAttribute("Image_Code", imageCode);
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
    }

    /**
     * 注册
     * @param studentId
     * @param password
     * @param phone
     * @param checkCode
     * @param session
     * @param map
     * @return
     */
    @PostMapping("/signup")
    public String register(@RequestParam("studentId") String studentId, @RequestParam("password") String password, @RequestParam("phone") String phone,
                           @RequestParam("checkCode") String checkCode, HttpSession session, Map<String, String> map) {
        // 对表单进行校验
        if (!VerificationUtil.isStudentId(studentId) || !VerificationUtil.isPassword(password) || !VerificationUtil.isTel(phone)) {
            // 返回登录页面时默认切换到注册界面
            session.setAttribute("loginFlag", "1");
            throw new FiveException(ResultEnums.FORM_VERIFICATION_ERROR, "/login");
        }

        // 验证手机验证码
        String code = redisTemplate.opsForValue().get(SMSConstant.CODE_PREFIX + phone);
        if (code == null || code.isEmpty() || !code.equals(checkCode)) {
            session.setAttribute("loginFlag", "1");
            throw new FiveException(ResultEnums.MSM_VERIFICATION_ERROR, "/login");
        }

        // 验证该用户是否已被注册
        if (userService.studentIdIsRepeat(studentId) != null) {
            session.setAttribute("loginFlag", "1");
            throw new FiveException(ResultEnums.USER_EXIST, "/login");
        }
        User newUser = new User();
        newUser.setStudentId(studentId);
        // 加密密码
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPhone(phone);
        Integer result = userService.insert(newUser);
        if (result == 0) {
            session.setAttribute("loginFlag", "1");
            throw new FiveException(ResultEnums.REGISTER_ERROR, "/login");
        }
        session.setAttribute("studentId", studentId);
        map.put("msg", "注册成功！");
        map.put("url", "/login");
        return "common/success";
    }

    /**
     * 注销成功后跳转链接
     * @return
     */
    @GetMapping("/logoutSuccess")
    public String logoutSuccess(Map<String, Object> map) {
        map.put("msg", "注销成功！");
        map.put("url", "/login");
        return "common/success";
    }
}
