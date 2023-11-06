package com.luckypan.mapper;

import com.luckypan.entity.FileInfo;
import com.luckypan.entity.query.FileInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文件信息 Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-10-06
 */
public interface FileInfoMapper{
    /**
     * selectList:(根据参数查询集合). <br/>
     */
    List<FileInfo> selectList(@Param("query") FileInfoQuery p);

    /**
     * selectCount:(根据集合查询数量). <br/>
     */
    Integer selectCount(@Param("query") FileInfoQuery p);

    /**
     * 获取用户已使用空间
     * @param userId <br/>
     * @return <br/>
     */
    Long selectUseSpace(@Param("userId") String userId);

    void insert(@Param("fileInfo") FileInfo fileInfo);
    FileInfo selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);
    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId, @Param("updateInfo") FileInfo updateInfo,
                                       @Param("oldStatus") Integer oldStatus);

    void updateByFileIdAndUserId(FileInfo updateInfo, String fileId, String userId);
}
