package org.elsa.fileservice.advice.cache;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.elsa.fileservice.advice.BaseConfiguration;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author valor
 * @date 2019-03-27 15:29
 */
@Component
public class UploadCache extends BaseConfiguration {

    private volatile static DiskFileItemFactory factory = null;

    private volatile static UploadCache instance = null;

    private UploadCache() { }

    public static UploadCache getInstance() {
        if (null == instance) {
            synchronized (UploadCache.class) {
                if (null == instance) {
                    instance = new UploadCache();
                }
            }
        }
        return instance;
    }

    private FileItemFactory getFactory() {
        if (null == factory) {
            synchronized (UploadCache.class) {
                if (null == factory) {
                    File file = new File(super.uploadConf.getTmpDir());
                    factory = new DiskFileItemFactory(super.uploadConf.getUpsBuffer(), file);
                    factory.setDefaultCharset("utf-8");

                    file = null;
                }
            }
        }
        return factory;
    }

    public ServletFileUpload getUpload() {
        ServletFileUpload upload = new ServletFileUpload(this.getFactory());
        upload.setHeaderEncoding("utf-8");
        upload.setFileSizeMax(super.uploadConf.getFileMax());
        upload.setSizeMax(super.uploadConf.getSizeMax());
        return upload;
    }
}
