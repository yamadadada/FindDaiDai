package com.yamada.five.service;

import com.yamada.five.pojo.ImageCode;
import org.springframework.web.context.request.ServletWebRequest;

public interface ValidateCodeService {

    /**
     * 创建验证码
     */
    ImageCode createCode(ServletWebRequest request);
}
