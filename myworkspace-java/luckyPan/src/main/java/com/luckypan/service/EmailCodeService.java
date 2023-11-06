package com.luckypan.service;

import com.luckypan.common.exception.BusinessException;
import com.luckypan.entity.EmailCode;

/**
 * <p>
 * 邮箱验证码 服务类
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
public interface EmailCodeService {

    void sendEmailCode(String email, Integer type) throws BusinessException;

    void checkCode(String email, String code);
}
