package com.luckypan.common.lang;

public class Const {
    public static final String ChECK_CODE_KEY = "check_code_key";
    public static final String ChECK_CODE_KEY_EMAIL = "check_code_key_email";

    public static final Integer ZERO = 0;
    public static final Integer LENGTH_5 = 5;
    public static final Integer LENGTH_150 = 150;
    //
    public static final Integer EMAIL_CODE_TIME = 3;

    public static final String SEND_SUBJECT_EMAIL = "邮箱验证码";

    public static final String SEND_TEXT_EMAIL = "<h1>你的邮箱验证码为 %s,请在3分钟内完成验证</h1>";

    /**
     * redis key 相关
     */

    /**
     * 过期时间 1分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60;

    /**
     * 过期时间 1天
     */
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;

    public static final Integer REDIS_KEY_EXPIRES_ONE_HOUR = REDIS_KEY_EXPIRES_ONE_MIN * 60;

    /**
     * 过期时间5分钟
     */
    public static final Integer REDIS_KEY_EXPIRES_FIVE_MIN = REDIS_KEY_EXPIRES_ONE_MIN * 5;

    public static final String REDIS_KEY_SYS_SETTING = "luckypan:syssetting:";

    public static final String REDIS_KEY_USER_SPACE_USE = "luckypan:user:spaceuse:";
    public static final String REDIS_KEY_USER_FILE_TEMP_SIZE = "luckypan:user:file:temp:";

    public static final Long MB = 1024 * 1024L;
    public static final String SESSION_KEY = "session_key";

    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_TEMP = "/temp/";

    public static final String FILE_FOLDER_AVATAR_NAME = "/avatar/";

    public static final String AVATAR_SUFFIX = ".jpg";
    public static final String IMAGE_PNG_SUFFIX = ".png";

    public static final String AVATAR_DEFAULT = "default_avatar.jpg";
    public static final String TS_NAME = "index.ts";
    public static final String M3U8_NAME = "index.m3u8";

}
