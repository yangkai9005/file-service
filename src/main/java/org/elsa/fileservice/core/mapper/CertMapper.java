package org.elsa.fileservice.core.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.elsa.fileservice.core.entity.UploadCert;
import org.springframework.stereotype.Repository;

/**
 * @author valor
 * @date 2019-03-27 02:19
 */
@Mapper
@Repository
public interface CertMapper {

    /**
     * 从数据库获取过期的cert
     */
    UploadCert getTimeoutCertOne(@Param("now") long time, @Param("timeout") Integer timeout);

}
