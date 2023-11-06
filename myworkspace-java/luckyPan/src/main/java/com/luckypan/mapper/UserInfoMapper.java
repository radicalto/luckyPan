package com.luckypan.mapper;

import com.luckypan.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
@Mapper
public interface UserInfoMapper {

    UserInfo selectByEmail(@Param("email") String email);

    UserInfo selectByNickName(String nickname);

    void insert(UserInfo userInfo);

    void updateByUserId(UserInfo updateInfo, String userId);

    void updateByEmail(UserInfo updateInfo, String email);

    Integer updateUserSpace(@Param("userId") String userId, @Param("useSpace") Long useSpace, @Param("totalSpace") Long totalSpace);
}
