package com.luckypan.controller;


import cn.hutool.http.HttpStatus;
import com.google.code.kaptcha.Producer;
import com.luckypan.LuckyPanApplication;
import com.luckypan.common.annotation.GlobalInterceptor;
import com.luckypan.common.annotation.VerifyParam;
import com.luckypan.common.exception.BusinessException;
import com.luckypan.common.lang.Const;
import com.luckypan.common.lang.eumns.VerifyRegexEnum;
import com.luckypan.common.utils.StringTools;
import com.luckypan.component.RedisComponent;
import com.luckypan.config.AppConfig;
import com.luckypan.entity.Dto.SessionWebUserDto;
import com.luckypan.entity.Dto.UserSpaceDto;
import com.luckypan.entity.UserInfo;
import com.luckypan.entity.Vo.ResponseVO;
import com.luckypan.mapper.UserInfoMapper;
import com.luckypan.service.EmailCodeService;
import com.luckypan.service.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-06-24
 */
@RestController
public class UserInfoController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(LuckyPanApplication.class);
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

    @Autowired
    Producer producer;
    @Autowired
    EmailCodeService emailCodeService;
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    AppConfig appConfig;
    @Autowired
    RedisComponent redisComponent;

    @RequestMapping("checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        // 设置响应头信息，告诉浏览器返回的是图片类型
        response.setContentType("image/jpeg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码文本
        String captchaText = producer.createText();
        logger.info("============== 验证码 =============：" + captchaText);
        // 将验证码文本存入session中，用于后续验证
        if (type == null || type == 0) {
            session.setAttribute(Const.ChECK_CODE_KEY, captchaText);
        } else {
            session.setAttribute(Const.ChECK_CODE_KEY_EMAIL, captchaText);
        }
        // 生成验证码图片并输出到响应流中
        BufferedImage image = producer.createImage(captchaText);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "jpg", outputStream);

    }

    @RequestMapping("sendEmailCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO sendEmailCode(
            HttpSession session,
            @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
            @VerifyParam(required = true) String checkCode,
            @VerifyParam(required = true) Integer type) {
        try {
            String SessionCheckCode = (String) session.getAttribute(Const.ChECK_CODE_KEY_EMAIL);
            if (!checkCode.equalsIgnoreCase(SessionCheckCode)) {
                throw new BusinessException("图片验证码不正确");
            }
            emailCodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Const.ChECK_CODE_KEY_EMAIL);
        }
    }

    @RequestMapping("register")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO register(
            HttpSession session,
            @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
            @VerifyParam(required = true, max = 20) String nickName,
            @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, max = 18, min = 8) String password,
            @VerifyParam(required = true) String checkCode,
            @VerifyParam(required = true) String emailCode) {
        try {
            String SessionCheckCode = (String) session.getAttribute(Const.ChECK_CODE_KEY);
            if (!checkCode.equalsIgnoreCase(SessionCheckCode)) {
                throw new BusinessException("图片验证码不正确");
            }
            password = StringTools.encodeByMd5(password);
            this.userInfoService.register(email, nickName, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Const.ChECK_CODE_KEY);
        }
    }

    @RequestMapping("login")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO login(
            HttpSession session,
            @VerifyParam(required = true) String email,
            @VerifyParam(required = true) String password,
            @VerifyParam(required = true) String checkCode) {
        try {
            String SessionCheckCode = (String) session.getAttribute(Const.ChECK_CODE_KEY);
            if (!checkCode.equalsIgnoreCase(SessionCheckCode)) {
                throw new BusinessException("验证码不正确");
            }
            SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
            session.setAttribute(Const.SESSION_KEY, sessionWebUserDto);

            return getSuccessResponseVO(sessionWebUserDto);
        } finally {
            session.removeAttribute(Const.ChECK_CODE_KEY);
        }
    }

    @RequestMapping("resetPwd")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO resetPwd(
            HttpSession session,
            @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL) String email,
            @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password,
            @VerifyParam(required = true) String checkCode,
            @VerifyParam(required = true) String emailCode) {
        try {
            String SessionCheckCode = (String) session.getAttribute(Const.ChECK_CODE_KEY);

            if (!checkCode.equalsIgnoreCase(SessionCheckCode)) {
                throw new BusinessException("验证码不正确");
            }
            this.userInfoService.resetPwd(email, password, emailCode);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Const.ChECK_CODE_KEY);
        }
    }

    @RequestMapping("/getAvatar/{userId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void getAvatar(HttpServletResponse response, HttpSession session,
                          @VerifyParam(required = true) @PathVariable("userId") String userId) {
        // 头像文件
        String avatarFolderName = Const.FILE_FOLDER_FILE + Const.FILE_FOLDER_AVATAR_NAME;
        File folder = new File(appConfig.getProjectFolder() + avatarFolderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 获取最终头像文件路径
        String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Const.AVATAR_SUFFIX;
        File file = new File(avatarPath);
        // 检验头像是否存在
        // 获取默认头像文件路径
        File avatar_default = new File(appConfig.getProjectFolder() + avatarFolderName + Const.AVATAR_DEFAULT);
        if (!file.exists()) {
            if (!avatar_default.exists()) {
                printNoDefaultImage(response);
            }
            // 不存在自己的头像，则获取默认头像
            avatarPath = appConfig.getProjectFolder() + avatarFolderName + Const.AVATAR_DEFAULT;
        }
        response.setContentType("image/jpeg");
        readFile(response, avatarPath);
    }

//    @RequestMapping("/getUserInfo")
//    public ResponseVO getUserInfo(HttpSession session) {
//        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Const.SESSION_KEY);
//        return getSuccessResponseVO(sessionWebUserDto);
//    }

    @RequestMapping("/getUseSpace")
    @GlobalInterceptor
    public ResponseVO getUseSpace(HttpSession session) {
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Const.SESSION_KEY);
        UserSpaceDto userSpaceUse = redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId());
        return getSuccessResponseVO(userSpaceUse);
    }

    @RequestMapping("/logout")
    public ResponseVO logout(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/updateUserAvatar")
    @GlobalInterceptor
    public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Const.SESSION_KEY);
        // 获取头像文件夹，判断文件是否存在
        String baseFolder = appConfig.getProjectFolder() + Const.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Const.FILE_FOLDER_AVATAR_NAME);
        if (!targetFileFolder.exists()) {
            // 不存在 则创建文件夹
            targetFileFolder.mkdirs();
        }
        // 存在 获取文件
        File targetFile = new File(targetFileFolder.getPath() + "/" + sessionWebUserDto.getUserId() + Const.AVATAR_SUFFIX);
        try {
            avatar.transferTo(targetFile);
        } catch (IOException e) {
            logger.error("上传头像失败",e);
        }
        UserInfo userInfo = new UserInfo();
        // 为了避免设置头像和qq相冲，改变我们的头像不影响qq
        userInfo.setQqAvatar("");
        this.userInfoService.updateByUserId(userInfo, sessionWebUserDto.getUserId());
        sessionWebUserDto.setAvatar(null);
        session.setAttribute(Const.SESSION_KEY,sessionWebUserDto);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/updatePassword")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updatePassword(HttpSession session,
                                 @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD) String password) {
        SessionWebUserDto sessionWebUserDto = (SessionWebUserDto) session.getAttribute(Const.SESSION_KEY);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeByMd5(password));
        this.userInfoService.updateByUserId(userInfo,sessionWebUserDto.getUserId());
        return getSuccessResponseVO(null);
    }
    private void printNoDefaultImage(HttpServletResponse response) {
        response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        response.setStatus(HttpStatus.HTTP_OK);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print("请在头像目录下放在默认头像default_avatar.jpg");
            writer.close();
        } catch (Exception e) {
            logger.error("无默认图或输出默认图失败", e);
        } finally {
            writer.close();
        }
    }

}
