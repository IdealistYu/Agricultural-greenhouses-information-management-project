package login.file;

import org.apache.commons.mail.EmailException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class ServletAction extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doGet!");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行doPost!");

    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("执行service!");
        req.setCharacterEncoding("UTF-8");
        // 返回给前端的json
        JSONObject jsonObject=new JSONObject();
        String action=req.getParameter("Action");
        sqlOperator sqlOp=new sqlOperator();
        switch (action){
            // 此增加功能只能进行用户表的增加
            case "addUserInfo":{
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String password=req.getParameter("password");
                String userLevel=req.getParameter("userLevel");
                String isWorker=req.getParameter("isWorker");
                HashMap map=new HashMap();
                map.put("account",account);
                map.put("mail",mail);
                map.put("PASSWORD",password);
                map.put("userLevel",userLevel);
                map.put("isWorker",isWorker);
                try {
                    sqlOp.AddRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 此delete可以对不同的数据表进行适配
            case "deleteInfo":{
                System.out.println("[Login_ServletAction]:deleteInfo");
                String id=req.getParameter("id");
                String dbName=req.getParameter("tableName");
                System.out.println("ID:"+id+"  Dbname:"+dbName);
                try {
                    sqlOp.DeleteRecord(id,dbName);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 对用户信息表进行modify
            case "modifyUserInfo":{
                String id=req.getParameter("id");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String userLevel=req.getParameter("userLevel");
                String userName=req.getParameter("userName");
                String userGender=req.getParameter("userGender");
                String isWorker=req.getParameter("isWorker");
                HashMap map=new HashMap();
                map.put("id",id);
                map.put("account",account);
                map.put("mail",mail);
                map.put("userLevel",Integer.parseInt(userLevel));
                map.put("userName",userName);
                map.put("userGender",userGender);
                map.put("isWorker",isWorker);
                try {
                    sqlOp.ModifyUserRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 统计数据
            case "statisticsUserInfo":{
                try {
                    jsonObject=sqlOp.StatisticsUserRecord();
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取用户信息，用于构成DataTable
            case "getUserRecord":{
                String sort=req.getParameter("sort");
                System.out.println(sort);
                try {
                    jsonObject=sqlOp.getUserRecord(sort);
                } catch (SQLException | JSONException e) {
                    System.out.println("获取数据失败!");
                    e.printStackTrace();
                }
                break;
            }
            // 登录
            case "login":{
                String account=req.getParameter("account");
                String password=req.getParameter("password");
                try {
                    int level= sqlOp.isExist(account,password);
                    if(level>=0){
                        jsonObject.put("level",level);
                        jsonObject.put("ok",200);
                        jsonObject.put("account",account);
                    }else{
                        jsonObject.put("ok",404);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    System.out.println("数据库查询出现异常！");
                    e.printStackTrace();
                }
                break;
            }

            // 注册
            case "register":{
                String registerAccount=req.getParameter("account");
                String password=req.getParameter("password");
                String mail=req.getParameter("mail");
                try {
                    if(sqlOp.Register(registerAccount,password,mail)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 判断注册信息是否合法
            case "isExisted":{
                String mail=req.getParameter("mail");
                String account=req.getParameter("account");
                System.out.println("mail:"+mail+",account:"+account);
                String content,key;
                if(mail == null){
                    content=account;
                    key="account";
                }else{
                    content=mail;
                    key="mail";
                }
                System.out.println("content:"+content+",key:"+key);
                try {
                    int status=sqlOp.isExisted(content,key);
                    if(status==-1){
                        jsonObject.put("ok",500);
                    }else if(status==-2) {
                        jsonObject.put("ok",400);
                    }else if(status==0){
                        jsonObject.put("ok",200);
                    }
//                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 发送验证码
            case "sendCode":{
                String mail=req.getParameter("mail");
                sendCode sendC=new sendCode(mail);
                try {
                    if(sendC.Send()){
                        jsonObject.put("ok",200);
                        System.out.println("验证码:"+sendC.getCode());
                        jsonObject.put("code",sendC.getCode());
                    }else{
                        jsonObject.put("ok",404);
                    }
                } catch (EmailException | JSONException e) {
                    e.printStackTrace();
                }
            }

            // 导出文件
            case "exportFile":{
                // 该json用于保存导出的数据
                JSONObject json=new JSONObject();
                System.out.println("正在导出");
                // 获取需要操作的表名
                String name=req.getParameter("tableName");
                System.out.println("将要操作的表:"+name);
                try {
                    json=sqlOp.getRecord(name);

                    // JSON导出文件的工具类对象
                    JsonToFile jsonToFile=new JsonToFile(req.getParameter("tag"));
                    jsonObject=jsonToFile.setJsonTOTxt(json);

                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    System.out.println(jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取设备信息
            case "getDeviceRecord":{
                try {
                    jsonObject=sqlOp.getDeviceRecord();
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 获取用户注册表信息
            case "getApplyRecord":{
                System.out.println("[Login_ServletAction]:getApplyRecord");
                String sort=req.getParameter("sort");
                System.out.println(sort);
                try {
                    jsonObject=sqlOp.getApplyRecord(sort);
                    System.out.println("jsonObject:"+jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 添加用户注册信息表
            case "addApplyInfo":{
                System.out.println("[Login_ServletAction]:addApplyInfo");
                String account=req.getParameter("account");
                String mail=req.getParameter("mail");
                String password=req.getParameter("password");
                HashMap map=new HashMap();
                map.put("account",account);
                map.put("mail",mail);
                map.put("password",password);
                try {
                    sqlOp.addApplyInfo(map);
                    jsonObject.put("ok",200);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            case "modifyApplyInfo":{
                String id=req.getParameter("id");
                HashMap map=new HashMap();
                map.put("account",req.getParameter("account"));
                map.put("mail",req.getParameter("mail"));
                map.put("password",req.getParameter("password"));
                try {
                    sqlOp.ModifyApplyRecord(Integer.parseInt(id),map);
                    jsonObject.put("ok",200);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 普通管理员审核用户注册信息
            case "refuseApply":{
                System.out.println("[Login_ServletAction]:refuseApply");
                String id=req.getParameter("id");
                // 是否全部拒绝？
                boolean refAll= id == null;
                try {
                    sqlOp.dealApply(id,refAll,"refuse");
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 普通管理员审核用户注册信息
            case "agreeApply":{
                System.out.println("[Login_ServletAction]:agreeApply");
                String id=req.getParameter("id");
                // 是否全部允许?
                boolean agrAll=false;
                String all=req.getHeader("all");
                if(all.equals("yes")){
                    agrAll=true;
                }
                try {
                    sqlOp.dealApply(id,agrAll,"agree");
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            default:{
                break;
            }
        }
        try {
            // 关闭数据库链接
            sqlOp.CloseCn();
            responseBack(req,resp,jsonObject);
        } catch (JSONException | SQLException e) {
            System.out.println("回调失败！|| 数据库链接关闭失败!");
            e.printStackTrace();
        }
    }

    private void responseBack(HttpServletRequest request, HttpServletResponse response, JSONObject json) throws JSONException {
        boolean isAjax=true;if (request.getHeader("x-requested-with") == null || request.getHeader("x-requested-with").equals("com.tencent.mm")){isAjax=false;}	//判断是异步请求还是同步请求，腾讯的特殊
        if(isAjax){
            response.setContentType("application/json; charset=UTF-8");
            try {
                response.getWriter().print(json);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("异步方式");
//            String action=json.getString("action");
//            String errorNo="0";
//            String errorMsg="ok";
//            String url = result.jsp?action="+action+"&result_code="+errorNo+ "&result_msg=" + errorMsg;
//            try {
//                response.sendRedirect(url);
//            } catch (IOException e) {
//                e.printStackTrace();
            }
        }


}
