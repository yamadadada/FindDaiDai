package com.yamada.five.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class VerificationUtilTest {

    @Test
    public void verificationTel() {
        String tel = "13759845635";
        log.info("【验证手机号：】" + VerificationUtil.isTel(tel));
    }
}