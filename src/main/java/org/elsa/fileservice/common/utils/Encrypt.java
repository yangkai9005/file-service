package org.elsa.fileservice.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.Security;

/**
 * @author valor
 * @date 2018/10/7 22:45
 */
public class Encrypt {

    /* aes算法名 */

    public static final String SHA1PRNG = "SHA1PRNG";

    /**
     * 微信小程序 getUserinfo
     */
    public static final String AES_128_CBC = "AES/CBC/PKCS7Padding";

    /**
     * 微信支付退款通知 解密算法
     */
    public static final String AES_256_ECB = "AES/ECB/PKCS7Padding";

    /* 类内变量及方法 */

    private static final String AES = "AES";

    private volatile static Encrypt instance = null;

    private Encrypt() { }

    private static Encrypt getInstance() {
        if (null == instance) {
            synchronized (Encrypt.class) {
                if (null == instance) {
                    instance = new Encrypt();
                }
            }
        }
        return instance;
    }

    /**
     * 转成Hex string
     */
    public static String toHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    /**
     * Hex 小写 md5 32位
     */
    public static String md5HexString(String content) {
        return DigestUtils.md5Hex(content);
    }

    /**
     * Hes 小写 md5 32位
     * 一般用于计算文件md5
     */
    public static String md5HexString(InputStream in) throws IOException {
        return DigestUtils.md5Hex(in);
    }

    /**
     * md5 + base64
     */
    public static String md5AndBase64(String content) {
        return base64EncodeToString(DigestUtils.md5(content));
    }

    /**
     * Hex 小写 sha-1
     */
    public static String sha1HexString(String content) {
        return DigestUtils.sha1Hex(content);
    }

    /**
     * sha-1 + base64
     */
    public static String sha1AndBase64(String content) {
        return base64EncodeToString(DigestUtils.sha1(content));
    }

    /**
     * Hex 小写 sha-256
     */
    public static String sha256HexString(String content) {
        return DigestUtils.sha256Hex(content);
    }

    /**
     * sha-256 + base64
     */
    public static String sha256AndBase64(String content) {
        return base64EncodeToString(DigestUtils.sha256(content));
    }

    /**
     * base64 加密
     *
     * @param content byte[]
     * @return String
     */
    public static String base64EncodeToString(byte[] content) {
        return Base64.encodeBase64String(content);
    }

    /**
     * base64 加密
     *
     * @param content String
     * @return String
     */
    public static String base64EncodeToString(String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return base64EncodeToString(bytes);
    }

    /**
     * base64 解密
     *
     * @param content String
     * @return String
     */
    public static String base64DecodeToString(String content) {
        byte[] bytes = base64DecodeToBytes(content);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * base64 解密
     *
     * @param content String
     * @return byte[]
     */
    public static byte[] base64DecodeToBytes(String content) {
        return Base64.decodeBase64(content);
    }

    /**
     * AES算法
     *
     * @param mode      模式 加密/解密
     * @param algorithm 算法填充
     * @param content   待解密的byte[]
     * @param keyByte   解密密钥
     * @param ivByte    算法向量
     * @return 解密后的byte[]
     * @throws Exception 抛出异常
     */
    private byte[] doAes(int mode, String algorithm, byte[] content, byte[] keyByte, byte[] ivByte) throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(algorithm);
        Key sKeySpec = new SecretKeySpec(keyByte, AES);

        if (null != ivByte && 0 != ivByte.length) {
            // CBC类型可以 填充iv向量
            AlgorithmParameters params = AlgorithmParameters.getInstance(AES);
            params.init(new IvParameterSpec(ivByte));
            // 初始化
            cipher.init(mode, sKeySpec, params);
        } else {
            // 初始化
            cipher.init(mode, sKeySpec);
        }

        return cipher.doFinal(content);
    }

    public static AesBuilder doAes() {
        return new AesBuilder();
    }

    public static class AesBuilder {

        private Encrypt encrypt;

        /**
         * 算法
         */
        private String algorithm;

        /**
         * 数据
         */
        private byte[] data;

        /**
         * 密钥
         */
        private byte[] key;

        /**
         * 填充向量
         */
        private byte[] iv;

        /**
         * 算法模式 加密/解密
         */
        private int mode;

        private AesBuilder() {
            this.encrypt = getInstance();
        }

        public AesBuilder setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public AesBuilder setData(String data) {
            this.data = base64DecodeToBytes(data);
            return this;
        }

        public AesBuilder setData(byte[] data) {
            this.data = data;
            return this;
        }

        public AesBuilder setKey(String key) {
            this.key = base64DecodeToBytes(key);
            return this;
        }

        public AesBuilder setKey(byte[] key) {
            this.key = key;
            return this;
        }

        public AesBuilder setIv(String iv) {
            this.iv = base64DecodeToBytes(iv);
            return this;
        }

        public AesBuilder setIv(byte[] iv) {
            this.iv = iv;
            return this;
        }

        public AesBuilder ofDecrypt() {
            this.mode = Cipher.DECRYPT_MODE;
            return this;
        }

        public AesBuilder ofEncrypt() {
            this.mode = Cipher.ENCRYPT_MODE;
            return this;
        }

        public byte[] toBytes() throws Exception {
            return encrypt.doAes(this.mode, this.algorithm, this.data, this.key, this.iv);
        }

        public String toText() throws Exception {
            return new String(this.toBytes(), StandardCharsets.UTF_8);
        }

    }

}
