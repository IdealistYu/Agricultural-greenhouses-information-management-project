package login.file;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.Random;

// 该类专用于发送验证码
public class sendCode {

    private final String destAddress;
    private final String code;

    public sendCode(String destAddress){
        this.destAddress=destAddress;
        System.out.println("destAddress:"+destAddress);
        this.code=verifyCode(6);
    }


    public boolean Send() throws EmailException {
        try{
            Email email = new SimpleEmail();

            // 设置主机
            email.setHostName("smtp.qq.com");
            email.setAuthentication("3244745586@qq.com", "0028.YHHyhh");
            email.setSSLOnConnect(true);
            email.setFrom("3244745586@qq.com");
            // 发送内容
            email.setCharset("UTF-8");
            email.setSubject("农业大棚信息管理系统");
            email.setMsg("你好，您申请注册的验证码是："+code+"，时效60s，请勿将该验证码发送给他人，保护信息安全。"+"\n@农业大棚信息管理系统");
            // 接收方邮箱
            email.addTo(this.destAddress);
            email.send();
            return true;
        }catch (Exception e){
            System.out.println("邮件发送失败！");
            e.printStackTrace();
            return false;
        }
    }

    public void sendApplyResult(String result){
        try{
            Email email = new SimpleEmail();

            // 设置主机
            email.setHostName("smtp.qq.com");
            email.setAuthentication("3244745586@qq.com", "0028.YHHyhh");
            email.setSSLOnConnect(true);
            email.setFrom("3244745586@qq.com");
            // 发送内容
            email.setCharset("UTF-8");
            email.setSubject("农业大棚信息管理系统");
            email.setMsg("你好，您申请注册农业大棚，已经管理员审核："+result+"感谢您的支持!!!"+"\n@农业大棚信息管理系统");
            // 接收方邮箱
            email.addTo(this.destAddress);
            email.send();
        }catch (Exception e){
            System.out.println("邮件发送失败！");
            e.printStackTrace();
        }
    }

    // 生成验证码
   private static String verifyCode(int n) {
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

    public String getCode(){
        return code;
    }



}
