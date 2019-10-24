package com.yamada.five.service.Impl;

import com.yamada.five.constant.SMSConstant;
import com.yamada.five.enums.ResultEnums;
import com.yamada.five.exception.FiveApiException;
import com.yamada.five.service.SMSService;
import com.yamada.five.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SMSServiceImpl implements SMSService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void sendMsg(String tel) {
        String interval = redisTemplate.opsForValue().get(SMSConstant.INTERVAL_PREFIX + tel);
        if (interval != null) {
            throw new FiveApiException(ResultEnums.MSM_OFTEN);
        }
        String code = MathUtil.getRandomSMSCode();
        // TODO 调用短信服务发送短信
        redisTemplate.opsForValue().set(SMSConstant.CODE_PREFIX + tel, code, 10, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(SMSConstant.INTERVAL_PREFIX + tel, code, 60, TimeUnit.SECONDS);
    }
}
