package com.luckypan.common.exception;


import com.luckypan.entity.Vo.ResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 异步的异常管理
public class GlobalExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    protected static final String STATUC_SUCCESS = "success";

    protected <T> ResponseVO getErrorResponseVO(String msg) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(400);
        responseVO.setInfo(msg);
        responseVO.setData(null);
        return responseVO;
    }

    // 实体校验异常捕获
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseVO handler(MethodArgumentNotValidException e) {

        BindingResult result = e.getBindingResult();
        ObjectError objectError = result.getAllErrors().stream().findFirst().get();

        logger.error("实体校验异常：----------------{}", objectError.getDefaultMessage());
        return getErrorResponseVO(objectError.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class) // 非法参数异常
    public ResponseVO Handler(IllegalArgumentException e){
        logger.error("Assert异常：----------------{}", e);
        return getErrorResponseVO(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseVO Handler(RuntimeException e){
        logger.error("运行时异常：----------------{}", e);
        return getErrorResponseVO(e.getMessage());
    }
}
