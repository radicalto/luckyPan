package com.luckypan.controller;

import com.luckypan.entity.EmailCode;
import com.luckypan.entity.Vo.ResponseVO;
import com.luckypan.mapper.EmailCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController extends BaseController{

    @Autowired
    EmailCodeMapper emailCodeMapper;

    private static final String UPLOAD_DIR = "E:/webser/wwwroot/myapps/luckypan/uploadTest";

    @PostMapping("/upload")
    public void uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // 保存文件到指定目录
        file.transferTo(new File(UPLOAD_DIR, file.getOriginalFilename()));
    }

    @PostMapping("/merge")
    public void mergeChunks(@RequestParam("fileName") String fileName,
                            @RequestParam("totalChunks") int totalChunks) throws IOException {
        // 合并分片为完整文件
        try (FileOutputStream fos = new FileOutputStream(new File(UPLOAD_DIR, fileName))) {
            for (int i = 0; i < totalChunks; i++) {
                FileInputStream fis = new FileInputStream(new File(UPLOAD_DIR, fileName + "_" + i));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fis.close();
            }
        }

        // 删除所有分片文件
//        for (int i = 0; i < totalChunks; i++) {
//            File chunkFile = new File(UPLOAD_DIR, fileName + "_" + i);
//            if (chunkFile.exists()) {
//                chunkFile.delete();
//            }
//        }
    }

    @GetMapping("test")
    public String test(HttpSession session) {
        session.setAttribute("checkCodeKey", "Welcome to LuckyPan!");
        String msg = (String) session.getAttribute("checkCodeKey");
        return msg;
    }

    @GetMapping("find")
    public ResponseVO findAll() {
        List<EmailCode> emailCodes = emailCodeMapper.selectAll();
        return getSuccessResponseVO(emailCodes);
    }

    @GetMapping("add")
    public String add() {
        EmailCode emailCode = new EmailCode();
        emailCode.setCode("123456");
        emailCode.setEmail("abcd@qq.com");
        emailCode.setStatus(0);
        emailCode.setCreateTime(new Date());
        emailCodeMapper.insert(emailCode);
        return "success";
    }
}
