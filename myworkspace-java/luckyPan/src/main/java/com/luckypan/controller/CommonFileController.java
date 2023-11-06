package com.luckypan.controller;

import com.luckypan.common.lang.Const;
import com.luckypan.common.lang.eumns.FileCategoryEnums;
import com.luckypan.common.lang.eumns.FileFolderTypeEnums;
import com.luckypan.common.lang.eumns.FileTypeEnums;
import com.luckypan.common.utils.CopyTools;
import com.luckypan.common.utils.StringTools;
import com.luckypan.component.RedisComponent;
import com.luckypan.config.AppConfig;
import com.luckypan.entity.FileInfo;
import com.luckypan.entity.Vo.ResponseVO;
import com.luckypan.entity.query.FileInfoQuery;
import com.luckypan.service.FileInfoService;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URLEncoder;
import java.util.List;

public class CommonFileController extends BaseController {

    @Resource
    protected FileInfoService fileInfoService;

    @Resource
    protected AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    public ResponseVO getFolderInfo(String path, String userId) {
        String[] pathArray = path.split("/");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        infoQuery.setFileIdArray(pathArray);
        String orderBy = "field(file_id,\"" + StringUtils.join(pathArray, "\",\"") + "\")";
        infoQuery.setOrderBy(orderBy);
        List<FileInfo> fileInfoList = fileInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(fileInfoList);
    }

    /**
     * 获取缩略图
     *
     * @param response    </br>
     * @param imageFolder </br>
     * @param imageName   </br>
     */
    public void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringUtils.isBlank(imageName)) {
            return;
        }
        String imageSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Const.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        imageSuffix = imageSuffix.replace(".", "");
        String contentType = "image/" + imageSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }

    /**
     * 获取视频 先获取索引文件 在获取切片的视频
     *
     * @param response </br>
     * @param fileId   </br>
     * @param userId   </br>
     */
    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;
        if (fileId.endsWith(".ts")) {
            String[] tsAarray = fileId.split("_");
            String realFileId = tsAarray[0];
            //根据原文件的id查询出一个文件集合
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                return;
            }
            String fileName = fileInfo.getFilePath();
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Const.FILE_FOLDER_FILE + fileName;

        } else {
            //根据原文件的id查询出一个文件集合
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (null == fileInfo) {
                return;
            }
            // 如果文件是视频类型，读取索引文件，获取视频总时长
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Const.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Const.M3U8_NAME;

            }
            else {
                filePath = appConfig.getProjectFolder() + Const.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
        }
        File file = new File(filePath);
        if (!file.exists()){
            return;
        }
        readFile(response, filePath);
    }
}
