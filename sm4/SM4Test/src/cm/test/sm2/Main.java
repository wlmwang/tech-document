package cm.test.sm2;

public class Main {
    public static void main(String[] args) {
        String plainText = "123456qwerty今天天气不错";

        SM4Utils sm4 = new SM4Utils();
        // 密码操作配置，具体看属性注释
        SM4_Context context = new SM4_Context();
        // 设置对称密钥
        context.secretKey = "1234567890abcdef";
        // 是否对密钥做16进制转换
        context.hexString = false;

        try {
            System.out.println("原始文本:" + plainText);
            System.out.println("ECB模式加密");
            byte[] result1 = sm4.encryptData_ECB(context, plainText.getBytes("UTF-8"));
            String cipherText = new String(result1, "UTF-8");
            System.out.println("密文: " + cipherText);
            System.out.println("");

            byte[] result2 = sm4.decryptData_ECB(context, result1);
            System.out.println("明文: " + new String(result2, "UTF-8"));

            System.out.println("CBC模式加密");
            // 填充向量
            context.iv = "0000000000000000";
            byte[] result3 = sm4.encryptData_CBC(context, plainText.getBytes("UTF-8"));
            System.out.println("密文: " + new String(result3, "UTF-8"));
            byte[] result4 = sm4.decryptData_CBC(context, result3);
            System.out.println("明文: " + new String(result4, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
