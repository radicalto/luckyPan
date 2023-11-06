package com.luckypan.interceptor;

import com.luckypan.common.exception.BusinessException;
import com.luckypan.common.lang.Const;
import com.luckypan.common.lang.eumns.ResponseCodeEnum;
import com.luckypan.entity.Dto.SessionWebUserDto;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class loginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Const.SESSION_KEY);
        if (null == sessionWebUserDto) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
        if (!sessionWebUserDto.getAdmin()){
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
