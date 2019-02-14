package org.elsa.filemanager.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.elsa.filemanager.core.entity.FileSystem;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author valord577
 * @date 18-8-1 下午3:45
 */
@Mapper
@Repository
public interface FileMapper {

    List<FileSystem> queryExpiredFile(@Param("service") Long service);

    void deleteById(@Param("id") Long id);

}
