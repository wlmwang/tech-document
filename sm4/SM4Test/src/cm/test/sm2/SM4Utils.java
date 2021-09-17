package cm.test.sm2;

public class SM4Utils {
    public SM4Utils() {
    }

    /**
     * 
     * 功能描述: <br>
     * ECB 模式加密
     *
     * @param context
     * @param data
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public byte[] encryptData_ECB(SM4_Context context, byte[] data) {
        try {
            context.isPadding = true;
            context.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            if (context.hexString) {
                keyBytes = Util.hexStringToBytes(context.secretKey);
            } else {
                keyBytes = context.secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(context, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(context, data);
            byte[] result = encrypted;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * 功能描述: <br>
     * ECB 模式解密
     *
     * @param context
     * @param data
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public byte[] decryptData_ECB(SM4_Context context, byte[] data) {
        try {
            context.isPadding = true;
            context.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            if (context.hexString) {
                keyBytes = Util.hexStringToBytes(context.secretKey);
            } else {
                keyBytes = context.secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(context, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(context, data);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * 功能描述: <br>
     * CBC 模式加密
     *
     * @param context
     * @param data
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public byte[] encryptData_CBC(SM4_Context context, byte[] data) {
        try {
            context.isPadding = true;
            context.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (context.hexString) {
                keyBytes = Util.hexStringToBytes(context.secretKey);
                ivBytes = Util.hexStringToBytes(context.iv);
            } else {
                keyBytes = context.secretKey.getBytes();
                ivBytes = context.iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(context, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(context, ivBytes, data);
            byte[] result = encrypted;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 
     * 功能描述: <br>
     * CBC 模式解密
     *
     * @param context
     * @param data
     * @return
     * @see [相关类/方法](可选)
     * @since [产品/模块版本](可选)
     */
    public byte[] decryptData_CBC(SM4_Context context, byte[] data) {
        try {
            context.isPadding = true;
            context.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (context.hexString) {
                keyBytes = Util.hexStringToBytes(context.secretKey);
                ivBytes = Util.hexStringToBytes(context.iv);
            } else {
                keyBytes = context.secretKey.getBytes();
                ivBytes = context.iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(context, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_cbc(context, ivBytes, data);
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}