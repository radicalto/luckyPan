package com.luckypan.service;

import com.luckypan.entity.Dto.SessionWebUserDto;
import com.luckypan.entity.UserInfo;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
public interface UserInfoService {

    public void register(String email, String nickname, String password, String checkCode);

    public SessionWebUserDto login(String email, String password);

    void resetPwd(String email, String password, String emailCode);

    void updateByUserId(UserInfo userInfo, String userId);
}
