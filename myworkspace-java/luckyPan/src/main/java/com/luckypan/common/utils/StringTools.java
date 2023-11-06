package com.luckypan.common.utils;

import cn.hutool.core.util.StrUtil;
import com.luckypan.common.lang.Const;
import com.luckypan.common.lang.RegexOption;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.DigestUtils;

import java.util.regex.Pattern;

public class StringTools {
    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(String.valueOf(RegexOption.EMAIL_REGEX));
        if (email == null)
            return false;
        return pattern.matcher(email).matches();
    }

    public static String encodeByMd5(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes());
    }

    public static boolean pathIsOk(String filePath) {
        if (StrUtil.isEmpty(filePath)) {
            return true;
        }
        if (filePath.contains("../") || filePath.contains("..\\")) {
            return false;
        }
        return true;

    }

    public static boolean isEmpty(String str) {

        if (null == str || "".equals(str) || "null".equals(str) || "\u0000".equals(str)) {
            return true;
        } else if ("".equals(str.trim())) {
            return true;
        }
        return false;
    }

    public static String getFileSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        String suffix = fileName.substring(index);
        return suffix;
    }


    public static String getFileNameNoSuffix(String fileName) {
        Integer index = fileName.lastIndexOf(".");
        if (index == -1) {
            return fileName;
        }
        fileName = fileName.substring(0, index);
        return fileName;
    }

    public static String rename(String fileName) {
        String fileNameReal = getFileNameNoSuffix(fileName);
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "_" + getRandomString(Const.LENGTH_5) + suffix;
    }

    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }
}
