package com.yamada.five.controller;

import com.yamada.five.bo.UserInfo;
import com.yamada.five.constant.SMSConstant;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.OrderStatusEnum;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveApiException;
import com.yamada.five.pojo.ImageCode;
import com.yamada.five.pojo.Order;
import com.yamada.five.pojo.User;
import com.yamada.five.service.OrderService;
import com.yamada.five.service.UserService;
import com.yamada.five.service.ValidateCodeService;
import com.yamada.five.utils.ResultVOUtil;
import com.yamada.five.utils.VerificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
        return "login";
    }

    /**
     * 获取验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/imageCode")
    public void imageCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = validateCodeService.createCode(new ServletWebRequest(request));
//        将验证码放入session中
        request.getSession().setAttribute("imageCode", imageCode.getCode());
        ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
    }

    @GetMapping("/toRegistered")
    public String toRegistered() {
        return "registered";
    }

    /**
     * 注册帐号
     * @param studentId
     * @param password
     * @param phone
     * @param checkCode
     * @param session
     * @return
     */
    @PostMapping("/signup")
    @ResponseBody
    public Object register(@RequestParam("studentId") String studentId, @RequestParam("password") String password, @RequestParam("phone") String phone,
                           @RequestParam("checkCode") String checkCode, HttpSession session) {
        // 对表单进行校验
        if (!VerificationUtil.isStudentId(studentId) || !VerificationUtil.isPassword(password) || !VerificationUtil.isTel(phone)) {
            throw new FiveApiException(ResultEnums.FORM_VERIFICATION_ERROR);
        }
        // 验证手机验证码
        String code = redisTemplate.opsForValue().get(SMSConstant.CODE_PREFIX + phone);
        if (code == null || code.isEmpty() || !code.equals(checkCode)) {
            throw new FiveApiException(ResultEnums.MSM_VERIFICATION_ERROR);
        }
        // 验证该用户是否已被注册
        if (userService.studentIdIsRepeat(studentId) != null) {
            throw new FiveApiException(ResultEnums.USER_EXIST);
        }
        User newUser = new User();
        newUser.setStudentId(studentId);
        // 加密密码
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPhone(phone);
        Integer result = userService.insert(newUser);
        if (result == 0) {
            throw new FiveApiException(ResultEnums.REGISTER_ERROR);
        }
        session.setAttribute("studentId", studentId);
        return ResultVOUtil.success(null);
    }

    /**
     * 注销成功后跳转链接
     * @return
     */
    @GetMapping("/logoutSuccess")
    public String logoutSuccess(Map<String, Object> map) {
        map.put("msg", "注销成功！");
        map.put("url", "login");
        return "common/success";
    }
}
