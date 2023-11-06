package com.luckypan.mapper;

import com.luckypan.entity.EmailCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 邮箱验证码 Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
@Mapper
public interface EmailCodeMapper {

    List<EmailCode> selectAll();
    void insert(EmailCode emailCode);

    void disableEmailCode(String email);

    EmailCode selectByEmailAndCode(String email, String code);
}
