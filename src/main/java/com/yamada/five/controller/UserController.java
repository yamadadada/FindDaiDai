package com.yamada.five.controller;

import com.alibaba.fastjson.JSONObject;
import com.yamada.five.bo.UserInfo;
import com.yamada.five.constant.SMSConstant;
import com.yamada.five.dto.UserDTO;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.enums.UserStatusEnum;
import com.yamada.five.exception.FiveException;
import com.yamada.five.pojo.User;
import com.yamada.five.service.UserService;
import com.yamada.five.utils.HttpClientUtils;
import com.yamada.five.utils.ResultVOUtil;
import com.yamada.five.utils.VerificationUtil;
import com.yamada.five.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/me")
@Slf4j
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

    /**
     * 前往身份认证页
     * @param map
     * @param request
     * @return
     */
    @GetMapping("/toVerification")
    public String toVerification(Map<String, Object> map, HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getById(userInfo.getUserId());
        if (!user.getUserStatus().equals(UserStatusEnum.NOT_CERTIFIED.getCode())) {
            throw new FiveException(ResultEnums.ALREADY_VERIFICATION);
        }

        // 获取cookie，存入session
        RestTemplate template = HttpClientUtils.getInstance(null, false);
        String url1 = "https://jxfw.gdut.edu.cn/";
        ResponseEntity<String> responseEntity = template.getForEntity(url1, String.class);
        List<String> cookies = responseEntity.getHeaders().get("Set-Cookie");
        if (cookies != null && cookies.size() > 0) {
            String cookie = cookies.get(0).split(";")[0].split("=")[1];
            request.getSession().setAttribute("cookie", cookie);
        }

        map.put("studentId", user.getStudentId());
        return "me/verification";
    }

    /**
     * 获取图形验证码（认证）
     * @param request
     * @param response
     */
    @GetMapping("/getImageCode")
    public void getImageCode(HttpServletRequest request, HttpServletResponse response) {
        String cookie = (String)request.getSession().getAttribute("cookie");
        if (StringUtils.isNotBlank(cookie)) {
            RestTemplate template = HttpClientUtils.getInstance(null, false);
            String url = "https://jxfw.gdut.edu.cn/yzm";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", "JSESSIONID=" + cookie);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity responseEntity = template.exchange(url, HttpMethod.GET, entity, byte[].class);
            byte[] bytes = (byte[]) responseEntity.getBody();
//            将请求的验证码图片用输出流方式输出
            OutputStream out = null;
            try {
                out = response.getOutputStream();
                response.setContentType("image/jpeg");
                response.setHeader("Content-Type","image/jpeg");
                if (bytes != null) {
                    out.write(bytes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @PostMapping("/verification")
    @ResponseBody
    public Object verification(@RequestParam("account") String account, @RequestParam("password") String password,
                               @RequestParam("verifycode") String verifycode, HttpServletRequest request) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!userInfo.getUsername().equals(account)) {
            throw new FiveException(ResultEnums.NOT_AUTHORITY);
        }
        User user = userService.getById(userInfo.getUserId());
        if (!user.getUserStatus().equals(UserStatusEnum.NOT_CERTIFIED.getCode())) {
            throw new FiveException(ResultEnums.ALREADY_VERIFICATION);
        }
        String cookie = (String)request.getSession().getAttribute("cookie");
        if (StringUtils.isNotBlank(cookie)) {
            RestTemplate template = HttpClientUtils.getInstance(null, false);
            String url = "https://jxfw.gdut.edu.cn/new/login";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", "JSESSIONID=" + cookie);
            headers.add("Accept", "application/json, text/javascript, */*; q=0.01");
            headers.add("Accept-Encoding", "gzip, deflate, br");
            headers.add("Accept-Language", "zh-CN,zh;q=0.9");
            headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers.add("Host", "jxfw.gdut.edu.cn");
            headers.add("Origin", "https://jxfw.gdut.edu.cn");
            headers.add("Referer", "https://jxfw.gdut.edu.cn/");
            headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/" +
                    "537.36 (KHTML, like Gecko) Chrome/74.0.3710.0 Safari/537.36");
            headers.add("X-Requested-With", "XMLHttpRequest");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("account", account);
            params.add("pwd", password);
            params.add("verifycode", verifycode);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<String> responseEntity = template.postForEntity(url, entity, String.class);
            JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
            log.info("【返回的数据：】" + jsonObject.toJSONString());
            if ((Integer)jsonObject.get("code") >= 0) {
                //认证成功，修改帐号状态
                user.setUserStatus(UserStatusEnum.NORMAL.getCode());
                userService.updateById(user);
                return ResultVOUtil.success(null);
            }
            ResultVO resultVO = new ResultVO(HttpStatus.UNAUTHORIZED.value(), (String)jsonObject.get("message"), null);
            return ResultVOUtil.error(resultVO);
        }
        ResultVO resultVO = new ResultVO(HttpStatus.UNAUTHORIZED.value(), "登录过期，请重新再试", null);
        return ResultVOUtil.error(resultVO);
    }
}
