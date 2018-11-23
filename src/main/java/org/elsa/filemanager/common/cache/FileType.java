package org.elsa.filemanager.common.cache;

import com.thoughtworks.xstream.XStream;
import org.elsa.filemanager.common.utils.XStreams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * @author valor
 * @date 2018-11-22 18:48
 */
@Component
public class FileType {

    @Value("${config.xml-location}")
    private String xmlPath;

    /**
     * 文件白名单缓存 类似于枚举类 但是枚举类不可更新
     */
    private volatile static Map<String, String> cacheType = null;

    private volatile static FileType fileType = null;

    private FileType() { }

    public static FileType getInstance() {
        if (null == fileType) {
            synchronized (FileType.class) {
                if (null == fileType) {
                    fileType = new FileType();
                }
            }
        }
        return fileType;
    }

    public Map<String, String> getCacheType() {
        if (null == cacheType) {
            synchronized (FileType.class) {
                if (null == cacheType) {
                    flushCache();
                }
            }
        }
        return cacheType;
    }

    /**
     * 刷新缓存
     */
    @SuppressWarnings("unchecked")
    public void flushCache() {
        XStream magicApi = XStreams.getMagicApi();
        File file = new File(xmlPath);
        cacheType = (Map<String, String>) magicApi.fromXML(file);
    }

}
