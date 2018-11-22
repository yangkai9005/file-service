package org.elsa.filemanager.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.elsa.filemanager.core.entity.FileSystem;
import org.springframework.stereotype.Repository;

/**
 * @author valord577
 * @date 18-8-1 下午3:45
 */
@Mapper
@Repository
public interface FileMapper {

    FileSystem queryByFilename(@Param("fileName") String fileName);
}
