package org.elsa.filemanager.common.utils;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author valor
 * @date 2018/11/21 18:05
 */
public class Files {

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
                throw new RuntimeException("Error reading inputSteam to bytes.");
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
