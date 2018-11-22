package org.elsa.filemanager.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author valor
 * @date 2018/11/21 18:05
 */
public class Files {

    public enum FileType {

        /**
         * 文件白名单
         */
        TXT("4a656e6b", "txt"),
        XML("3c3f786d", "xml");

        private String value;

        private String ext;

        FileType(String value, String ext) {
            this.value = value;
            this.ext = ext;
        }

        public String getValue() {
            return value;
        }

        public String getExt() {
            return ext;
        }

        public static FileType getType(String fileHeader) {
            if (null == fileHeader || 0 >= fileHeader.length()) {
                throw new RuntimeException("Blank string 'fileHeader'.");
            }

            for (FileType type : FileType.values()) {
                if (StringUtils.startsWith(fileHeader, type.value)) {
                    return type;
                }
            }

            throw new RuntimeException("Block this file.");
        }
    }

    /**
     * 根据文件路径 获取文件头
     */
    public static String getFileHeader(String filePath) {
        try {
            return getFileHeader(new FileInputStream(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据文件二进制流 获取文件头
     */
    public static String getFileHeader(InputStream inputStream) {
        if (null == inputStream) {
            throw new RuntimeException("No such file inputSteam.");
        }

        try {
            byte[] bytes = new byte[28];
            int i = inputStream.read(bytes, 0, 28);

            if (-1 == i) {
                throw new RuntimeException("Error read inputSteam to bytes.");
            }

            inputStream.close();
            return bytesToHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节数组转换成16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        if (0 >= bytes.length) {
            throw new RuntimeException("Empty bytes.");
        }

        StringBuilder stringBuilder = new StringBuilder();

        String hv;
        for (int i = 0; i < bytes.length; i++) {
            hv = Integer.toHexString(bytes[i] & 0xFF);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
