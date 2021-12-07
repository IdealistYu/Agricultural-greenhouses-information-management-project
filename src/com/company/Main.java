package com.company;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.Random;

public class Main {

    public static void sendCode() throws EmailException {
        try {
            Email email = new SimpleEmail();

            // 设置主机
            email.setHostName("smtp.qq.com");
            email.setAuthentication("3244745586@qq.com", "0028.YHHyhh");
            email.setSSLOnConnect(true);
            email.setFrom("3244745586@qq.com");
            // 发送内容
            email.setCharset("UTF-8");
            email.setSubject("这是一封测试邮件");
            email.setMsg("你好，java发送邮件，验证码是：2220154");
            // 接收方邮箱
            email.addTo("1297737082@qq.com");
            email.send();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String verifyCode(int n) {
        StringBuilder strB = new StringBuilder();
        Random rand = new Random();
        for(int i = 0; i < n; i++) {
            int r1 = rand.nextInt(3);
            int r2 = 0;
            switch (r1) {  // r2为ascii码值
                case 0: // 数字
                    r2 = rand.nextInt(10) + 48;  // 数字：48-57的随机数
                    break;
                case 1:
                    r2 = rand.nextInt(26) + 65;  // 大写字母
                    break;
                case 2:
                    r2 = rand.nextInt(26) + 97;  // 小写字母
                    break;
                default:
                    break;
            }
            strB.append((char)r2);
        }
        return strB.toString();
    }

    public static void main(String[] args) throws EmailException {
	// write your code here
//        sendCode();
        System.out.println(verifyCode(6));
    }
}
