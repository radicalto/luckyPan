package com.luckypan.service;

import com.luckypan.entity.Dto.SessionWebUserDto;
import com.luckypan.entity.Dto.UploadResultDto;
import com.luckypan.entity.FileInfo;
import com.luckypan.entity.Vo.PaginationResultVO;
import com.luckypan.entity.query.FileInfoQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 文件信息 服务类
 * </p>
 *
 * @author
 * @since 2023-10-06
 */
public interface FileInfoService{

    /**
     * 根据条件查询列表
     */
    List<FileInfo> findListByParam(FileInfoQuery param);

    /**
     * 根据条件查询列表
     */
    Integer findCountByParam(FileInfoQuery param);

    /**
     * 根据FileIdAndUserId查询对象
     */
    FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId);

    /**
     * 分页查询
     */
    PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

    UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

    FileInfo newFolder(String filePid, String userId, String fileName);

    FileInfo rename(String fileId, String userId, String fileName);
}
