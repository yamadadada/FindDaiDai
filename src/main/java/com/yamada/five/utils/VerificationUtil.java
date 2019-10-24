package com.yamada.five.utils;

import java.util.regex.Pattern;

public class VerificationUtil {

    public static boolean isTel(String tel) {
        Pattern pattern = Pattern.compile("^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])[0-9]{8}$");
        return pattern.matcher(tel).matches();
    }

    public static boolean isStudentId(String studentId) {
        Pattern pattern = Pattern.compile("^[2|3][1|2]1[5|6|7|8]0[0-9]{5}$");
        return pattern.matcher(studentId).matches();
    }

    public static boolean isPassword(String password) {
        Pattern pattern = Pattern.compile("^\\w{6,18}$");
        return pattern.matcher(password).matches();
    }
}
