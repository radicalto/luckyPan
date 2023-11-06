package com.luckypan.common.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * @author rayfoo@qq.com
 * @version 1.0
 * <p>断言类</p>
 * @date 2020/8/7 9:43
 */
public class MyAssert {
    public static final Logger logger = LoggerFactory.getLogger(MyAssert.class);
    /**
     * 如果为空直接抛出异常 类似于断言的思想
     * @param status 当status为false 就会抛出异常 不继续执行后续语句
     * @param msg  异常描述
     */
    public static void assertMethod(boolean status, String msg) throws Exception {
        //为false抛出异常
        if (!status) {
            //记录错误信息
            logger.error(msg);
            //抛出异常
            throw new BusinessException("请求参数异常");
        }
    }

    /**
     * 如果为空直接抛出异常 类似于断言的思想
     * @param status 当status为false 就会抛出异常 不继续执行后续语句
     * @param code 状态码
     * @param msg  异常描述
     */
    public static void assertMethod(boolean status,Integer code, String msg) throws Exception {
        //为false抛出异常
        if (!status) {
            //记录错误信息
            logger.error(msg);
            //抛出异常
            throw new BusinessException("请求参数异常");
        }
    }

    /**
     * 如果为空直接抛出异常 类似于断言的思想
     * @param status 当status为false 就会抛出异常 不继续执行后续语句
     */
    public static void assertMethod(boolean status) throws Exception {
        //为false抛出异常
        if (!status) {
            //记录错误信息
            logger.error(HttpStatus.INTERNAL_SERVER_ERROR.name());
            //抛出异常
            throw new BusinessException("请求参数异常");
        }
    }
}

