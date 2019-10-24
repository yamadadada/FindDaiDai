package com.yamada.five.pojo;

import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class ImageCode {

    private BufferedImage image;

    private String code;

    private Long expireTime;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        //当前时间  加上  设置过期的时间
        this.expireTime = System.currentTimeMillis() + expireIn;
    }

    public boolean isExpried() {
        //如果 过期时间 在 当前日期 之前，则验证码过期
        return System.currentTimeMillis() > expireTime;
    }
}
