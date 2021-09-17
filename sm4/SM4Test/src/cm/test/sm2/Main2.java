package cm.test.sm2;

import java.io.File;
import java.nio.file.Files;

public class Main2 {

    public static void main(String[] args) {
        File origin = new File("file/123.txt");
        File result = new File("file/123.txt.sms4_en");
        SM4Utils sm4 = new SM4Utils();
        SM4_Context context = new SM4_Context();
        context.secretKey = "1234567890abcdef";
        context.hexString = false;

        try {
            System.out.println("ECB模式加密");
            byte[] result1 = sm4.encryptData_ECB(context, Files.readAllBytes(origin.toPath()));
            String cipherText = new String(result1, "UTF-8");
            System.out.println("密文: " + cipherText);
            System.out.println("");

            byte[] result2 = sm4.decryptData_ECB(context, result1);
            System.out.println("明文: " + new String(result2, "UTF-8"));

            System.out.println("CBC模式加密");
            context.iv = "0000000000000000";
            byte[] result3 = sm4.encryptData_CBC(context, Files.readAllBytes(origin.toPath()));
            System.out.println("密文: " + new String(result3, "UTF-8"));

            byte[] result4 = sm4.decryptData_CBC(context, Files.readAllBytes(result.toPath()));
            System.out.println("明文: " + new String(result4, "UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
