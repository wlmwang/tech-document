package cm.test.sm2;

public class SM4_Context {
    /**
     * SM4_ENCRYPT：加密<br>
     * SM4_DECRYPT：解密
     */
    public int mode;

    public long[] sk;
    /**
     * 填充向量
     */
    public String iv = "";
    /**
     * 是否填充
     */
    public boolean isPadding;
    /**
     * 密钥
     */
    public String secretKey;
    /**
     * 是否对密钥做16进制转换
     */
    public boolean hexString = false;

    public SM4_Context() {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }
}