package com.luckypan;

import com.luckypan.common.utils.RedisIdWork;
import com.luckypan.common.utils.RedisUtils;
import com.luckypan.common.utils.StringTools;
import com.luckypan.entity.EmailCode;
import com.luckypan.mapper.EmailCodeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class TestDemo {
    @Autowired
    EmailCodeMapper emailCodeMapper;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    RedisIdWork redisIdWork;

    @Test
    public void md5(){
        String password = StringTools.encodeByMd5("000000");
        System.out.println("password: "+password);
    }

    @Test
    public void redisIdWork(){
        Long id = redisIdWork.nextId("fileId");
        System.out.println("id: "+id);
    }

    @Test
    public void set(){
        redisUtils.set("name","yn");
    }

    @Test
    public void selectAll(){
        List<EmailCode> emailCodes = emailCodeMapper.selectAll();
        System.out.println(emailCodes);
    }

    @Test
    public void add() {
        EmailCode emailCode = new EmailCode();
        emailCode.setCode("12345");
        emailCode.setEmail("abcd@qq.com");
        emailCode.setStatus(0);
        emailCode.setCreateTime(new Date());
        emailCodeMapper.insert(emailCode);
        System.out.println("success");
    }
}
