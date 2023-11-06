package com.luckypan.component;

import cn.hutool.json.JSONUtil;
import com.luckypan.common.lang.Const;
import com.luckypan.common.utils.RedisUtils;
import com.luckypan.entity.Dto.SysSettingsDto;
import com.luckypan.entity.Dto.UserSpaceDto;
import com.luckypan.mapper.FileInfoMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisComponent {
    @Resource
    RedisUtils redisUtils;
    @Resource
    FileInfoMapper fileInfoMapper;

    public SysSettingsDto getSysSettingsDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Const.REDIS_KEY_SYS_SETTING);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Const.REDIS_KEY_SYS_SETTING, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    /**
     * 保存设置
     *
     * @param sysSettingsDto
     */
    public void saveSysSettingsDto(SysSettingsDto sysSettingsDto) {
        redisUtils.set(Const.REDIS_KEY_SYS_SETTING, sysSettingsDto);
    }

    /**
     * 保存已使用的空间
     *
     * @param userId
     */
    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Const.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Const.REDIS_KEY_EXPIRES_DAY);
    }


    /**
     * 获取用户使用的空间
     *
     * @param userId
     * @return
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Const.REDIS_KEY_USER_SPACE_USE + userId);
        if (null == spaceDto) {
            spaceDto = new UserSpaceDto();
            Long useSpace = this.fileInfoMapper.selectUseSpace(userId);
            spaceDto.setUseSpace(useSpace);
            spaceDto.setTotalSpace(getSysSettingsDto().getUserInitUseSpace() * Const.MB);
            redisUtils.setex(Const.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Const.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }

    //保存文件临时大小
    public void saveFileTempSize(String userId, String fileId, Long fileSize) {
        Long currentSize = getFileTempSize(userId, fileId);
        redisUtils.setex(Const.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, currentSize + fileSize, Const.REDIS_KEY_EXPIRES_ONE_HOUR);
    }

    public Long getFileTempSize(String userId, String fileId) {
        Long currentSize = getFileSizeFromRedis(Const.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
        return currentSize;
    }

    private Long getFileSizeFromRedis(String key) {
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }

        return 0L;
    }
}
