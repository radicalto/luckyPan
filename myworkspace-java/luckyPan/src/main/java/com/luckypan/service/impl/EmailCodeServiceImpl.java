package com.luckypan.service.impl;

import com.luckypan.LuckyPanApplication;
import com.luckypan.common.exception.BusinessException;
import com.luckypan.common.lang.Const;
import com.luckypan.common.utils.StringTools;
import com.luckypan.component.RedisComponent;
import com.luckypan.config.AppConfig;
import com.luckypan.entity.Dto.SysSettingsDto;
import com.luckypan.entity.EmailCode;
import com.luckypan.entity.UserInfo;
import com.luckypan.mapper.EmailCodeMapper;
import com.luckypan.mapper.UserInfoMapper;
import com.luckypan.service.EmailCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * <p>
 * 邮箱验证码 服务实现类
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
@Service
public class EmailCodeServiceImpl implements EmailCodeService {
    private static final Logger logger = LoggerFactory.getLogger(EmailCodeServiceImpl.class);
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private EmailCodeMapper emailCodeMapper;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AppConfig appConfig;
    @Autowired
    RedisComponent redisComponent;
    @Override
    @Transactional(rollbackFor = Exception.class) // 防止注册失败，邮箱验证码还有用
    public void sendEmailCode(String email, Integer type) throws BusinessException {
        if (type == Const.ZERO) { // type==0 注册
            UserInfo userInfo = userInfoMapper.selectByEmail(email);
            if(userInfo != null) {
                throw new BusinessException("邮箱已经存在");
            }
        }
        // 发送的邮箱验证码
        String code = StringTools.getRandomNumber(Const.LENGTH_5);
        logger.info("========== 发送的邮箱验证码为：{} =========",code);
        // 发送验证码
        sendEmailCode(email,code);

        // 再次发送邮箱验证码时，前一个要失效 即status=1
        emailCodeMapper.disableEmailCode(email);

        EmailCode emailCode = new EmailCode();
        emailCode.setEmail(email);
        emailCode.setCode(code);
        emailCode.setStatus(Const.ZERO);
        emailCode.setCreateTime(new Date());
        emailCodeMapper.insert(emailCode);
    }

    @Override
    public void checkCode(String email, String code) {
        EmailCode emailCode = this.emailCodeMapper.selectByEmailAndCode(email, code);
        if (emailCode == null) {
            throw new BusinessException("邮箱验证码错误");
        }
        if (emailCode.getStatus()==1 ||
                System.currentTimeMillis() - emailCode.getCreateTime().getTime()>
                        Const.EMAIL_CODE_TIME*1000*60) {
            throw new BusinessException("邮箱验证码已失效");
        }
        emailCodeMapper.disableEmailCode(email);
    }

    private void sendEmailCode(String toEmail, String code) throws BusinessException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(appConfig.getSendUserName());
            helper.setTo(toEmail);
            SysSettingsDto sysSettingDto = redisComponent.getSysSettingsDto();
            helper.setSubject(sysSettingDto.getRegisterMailTitle());
            helper.setText(String.format(sysSettingDto.getRegisterEmailContent(),code));
            helper.setSentDate(new Date());
            javaMailSender.send(message);
        } catch (MessagingException e) {
            logger.error("邮箱发送失败",e);
            throw new BusinessException("邮箱发送失败");
        }
    }
}
