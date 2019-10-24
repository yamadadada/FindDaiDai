package com.yamada.five.utils;

import java.util.Random;

public class MathUtil {

    /**
     * 生成随机短信验证码
     * @return
     */
    public static String getRandomSMSCode() {
        Random random = new Random();
        int i = random.nextInt(900000) + 100000;
        return String.valueOf(i);
    }
}
