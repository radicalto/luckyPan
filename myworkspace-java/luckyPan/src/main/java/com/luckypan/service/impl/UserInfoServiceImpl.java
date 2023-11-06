package com.luckypan.service.impl;

import com.luckypan.common.exception.BusinessException;
import com.luckypan.common.lang.Const;
import com.luckypan.common.lang.eumns.UserStatusEnum;
import com.luckypan.common.utils.RedisIdWork;
import com.luckypan.common.utils.StringTools;
import com.luckypan.component.RedisComponent;
import com.luckypan.config.AppConfig;
import com.luckypan.entity.Dto.SessionWebUserDto;
import com.luckypan.entity.Dto.SysSettingsDto;
import com.luckypan.entity.Dto.UserSpaceDto;
import com.luckypan.entity.UserInfo;
import com.luckypan.mapper.FileInfoMapper;
import com.luckypan.mapper.UserInfoMapper;

import com.luckypan.service.EmailCodeService;
import com.luckypan.service.UserInfoService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Resource
    FileInfoMapper fileInfoMapper;
    @Autowired
    EmailCodeService emailCodeService;
    @Autowired
    RedisIdWork redisIdWork;
    @Autowired
    RedisComponent redisComponent;
    @Autowired
    AppConfig appConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)// 不然如果注册失败了但验证码还未失效
    public void register(String email, String nickname, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo != null) {
            throw new BusinessException("邮箱账号已存在");
        }
        UserInfo nickNameUser = this.userInfoMapper.selectByNickName(nickname);
        if (nickNameUser != null) {
            throw new BusinessException("昵称已存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(email,emailCode);

        String userId = String.valueOf(redisIdWork.nextId("userid"));
        userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(nickname);
        userInfo.setEmail(email);
        userInfo.setPassword(password);
        userInfo.setRegisterTime(new Date());
        userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
        userInfo.setUseSpace(0L);
        SysSettingsDto sysSettingDto = redisComponent.getSysSettingsDto();
        userInfo.setTotalSpace(sysSettingDto.getUserInitUseSpace()* Const.MB);
        this.userInfoMapper.insert(userInfo);
    }

    @Override
    public SessionWebUserDto login(String email, String password) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
//        password = StringTools.encodeByMd5(password);
        if (userInfo == null || !userInfo.getPassword().equals(password)) {
            throw new BusinessException("账户或密码不正确");
        }
        if(UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
            throw new BusinessException("账号已禁用");
        }
        // 修改登录时间
        UserInfo updateInfo = new UserInfo();
        updateInfo.setLastLoginTime(new Date());
        this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());
        this.userInfoMapper.selectByEmail(email);
        // 封装登入返回数据
        SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
        sessionWebUserDto.setNickName(userInfo.getNickName());
        sessionWebUserDto.setUserId(userInfo.getUserId());
        // 判断是否是admin用户
        if (ArrayUtils.contains(appConfig.getAdminEmails().split(","),email)) {
            sessionWebUserDto.setAdmin(true);
        } else {
             sessionWebUserDto.setAdmin(false);
        }
        // 用户空间
        UserSpaceDto userSpaceDto = new UserSpaceDto();
        Long useSpace = this.fileInfoMapper.selectUseSpace(userInfo.getUserId());
        userSpaceDto.setUseSpace(useSpace);
        userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
        redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);

        return sessionWebUserDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)// 不然如果注册失败了但验证码还未失效
    public void resetPwd(String email, String password, String emailCode) {
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        if (userInfo == null) {
            throw new BusinessException("邮箱账号不存在");
        }
        // 校验邮箱验证码
        emailCodeService.checkCode(email,emailCode);
        // 修改密码
        UserInfo updateInfo = new UserInfo();
        password = StringTools.encodeByMd5(password);
        updateInfo.setPassword(password);
        this.userInfoMapper.updateByEmail(updateInfo,email);

    }

    @Override
    public void updateByUserId(UserInfo userInfo, String userId) {
        this.userInfoMapper.updateByUserId(userInfo,userId);
    }
}
