package com.luckypan.controller;


import com.luckypan.common.annotation.GlobalInterceptor;
import com.luckypan.common.annotation.VerifyParam;
import com.luckypan.common.lang.eumns.FileCategoryEnums;
import com.luckypan.common.lang.eumns.FileDelFlagEnums;
import com.luckypan.common.lang.eumns.FileFolderTypeEnums;
import com.luckypan.common.utils.StringTools;
import com.luckypan.entity.Dto.SessionWebUserDto;
import com.luckypan.entity.Dto.UploadResultDto;
import com.luckypan.entity.FileInfo;
import com.luckypan.entity.Vo.FileInfoVO;
import com.luckypan.entity.Vo.PaginationResultVO;
import com.luckypan.entity.Vo.ResponseVO;
import com.luckypan.entity.query.FileInfoQuery;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>
 * 文件信息 前端控制器
 * </p>
 *
 * @author
 * @since 2023-10-06
 */
@RestController
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {

    /**
     * 根据条件分页查询
     */
    @RequestMapping("/loadDataList")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
        // 转化 我们前端分类是路由区分 比如视频/video 而数据库是数值
        FileCategoryEnums categoryEnum = FileCategoryEnums.getByCode(category);
        if (null != categoryEnum) {
            query.setFileCategory(categoryEnum.getCategory());
        }
        query.setUserId(getUserInfoFromSession(session).getUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());// 获取正常的文件
        PaginationResultVO<FileInfo> result = fileInfoService.findListByPage(query);
        // 我们将结果返回数据优化，把一些需要的数据封装返回
        return getSuccessResponseVO(convert2PaginationVO(result, FileInfoVO.class));
    }

    @RequestMapping("/uploadFile")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO uploadFile(HttpSession session,
                                 String fileId,
                                 MultipartFile file,
                                 @VerifyParam(required = true) String fileName,
                                 @VerifyParam(required = true) String filePid,
                                 @VerifyParam(required = true) String fileMd5,
                                 @VerifyParam(required = true) Integer chunkIndex,
                                 @VerifyParam(required = true) Integer chunks) {

        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        UploadResultDto resultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
        return getSuccessResponseVO(resultDto);
    }

    @RequestMapping("/getImage/{imageFolder}/{imageName}")
    public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
        super.getImage(response, imageFolder, imageName);
    }

    @RequestMapping("/ts/getVideoInfo/{fileId}")
    public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    @RequestMapping("/getFile/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        super.getFile(response, fileId, webUserDto.getUserId());
    }

    @RequestMapping("/newFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO newFolder(HttpSession session,
                                @VerifyParam(required = true) String filePid,
                                @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(fileInfo);
    }

    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session, @VerifyParam(required = true) String path) {
        return super.getFolderInfo(path, getUserInfoFromSession(session).getUserId());
    }

    @RequestMapping("/rename")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO rename(
            HttpSession session,
            @VerifyParam(required = true) String fileId,
            @VerifyParam(required = true) String fileName) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
        return getSuccessResponseVO(fileInfo);
    }

    @RequestMapping("/loadAllFolder")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadAllFolder(
            HttpSession session,
            @VerifyParam(required = true) String filePid,
            String currentFileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFilePid(filePid);
        infoQuery.setUserId(webUserDto.getUserId());
        if (!StringTools.isEmpty(currentFileIds)) {
            infoQuery.setExcludeFileIdArray(currentFileIds.split(","));
        }
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setOrderBy("create_time desc");
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(fileInfoList);
    }
}
