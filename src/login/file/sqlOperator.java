package login.file;

import DbOperator.getDbConnection;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class sqlOperator {

    private Statement statement;

    public sqlOperator(){
        getDbConnection dbConnection=new getDbConnection();
        this.statement=dbConnection.getConnection();
        System.out.println("准备数据库操作完毕!");
    }

    // 以下是相关数据库的操作，包括增删改查
    // 判断用户是否可以登录成功
    public int isExist(String account, String password) throws SQLException {
        String sql="select * from accountInfo where account='"+account+"' and PASSWORD='"+password+"'";
        System.out.println("[sqlOperator/isExist()]将要执行sql:"+sql);
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
            return resultSet.getInt("userLevel");
        }else{
            sql="select * from accountInfo where mail='"+account+"' and PASSWORD='"+password+"'";
            System.out.println("SQL:"+sql);
            resultSet=statement.executeQuery(sql);
            if(resultSet.next()){
                return resultSet.getInt("userLevel");
            }else{
                return -1;
            }
        }
    }

    // 用户注册
    public Boolean Register(String account, String password, String mail) throws SQLException {
        System.out.println("注册!");
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(date);
        String sql="insert into applyInfo (account, PASSWORD, mail, applyDate) VALUES('"+account+"','"+password+"','"+mail+"','"+date+"')";
        System.out.println("[sqlOperator/Register()]将要执行sql:"+sql);
        if(statement.executeUpdate(sql)>0){
            return true;
        }else{
            return false;
        }

    }

    // 判断邮箱或用户名是否已注册或已申请注册
    public int isExisted(String content,String key) throws SQLException {
        String sql = null;
        String sql1= null;
        if(key.equals("mail")){
            sql="select * from accountInfo where mail='"+content+"'";
            sql1="select * from applyInfo where mail='"+content+"'";
        }else if(key.equals("account")){
            sql="select * from accountInfo where account='"+content+"'";
            sql1="select * from applyInfo where account='"+content+"'";
        }
        System.out.println("[sqlOperator/isExisted()]将要执行SQL:"+sql);
        ResultSet resultSet=statement.executeQuery(sql);
        if(resultSet.next()){
            // -1 说明用户账户或邮箱已经存在
            return -1;
        }else {
            System.out.println("SQL:"+sql1);
            resultSet=statement.executeQuery(sql1);
            if(resultSet.next()){
                // -2 说明用户账户或邮箱已经被申请注册，但尚未完成审核
                resultSet.close();
                return -2;
            }else{
                // 0表示用户账户或邮箱可以申请注册，合法
                resultSet.close();
                return 0;
            }
        }
    }

    // 后续可以优化
    public JSONObject getUserRecord(String sortWay) throws SQLException, JSONException {
        String sql="select * from accountInfo";
        // 定义升序或降序
        if(sortWay.equals("up")){
            sql+=" order by account";
        }else if(sortWay.equals("down")){
            sql+=" order by account desc";
        }

        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getUserRecord()]执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("id",resultSet.getInt("userId"));
            map.put("account",resultSet.getString("account"));
            map.put("mail",resultSet.getString("mail"));
//            map.put("password",resultSet.getString("PASSWORD"));
            map.put("userLevel",resultSet.getString("userLevel"));
            map.put("userName",resultSet.getString("userName"));
            map.put("userGender",resultSet.getString("userGender"));
            map.put("signUpDate",resultSet.getDate("signUpDate"));
            map.put("lastLoginDate",resultSet.getDate("lastLoginDate"));
            map.put("lastModifyDate",resultSet.getDate("lastModifyDate"));
            map.put("isWorker",resultSet.getInt("isWorker"));
            System.out.println(map);
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    // 获取注册申请
    public JSONObject getApplyRecord(String sortWay) throws SQLException, JSONException {
        JSONObject json=new JSONObject();
        String sql="select * from applyInfo ";
        System.out.println("[sqlOperator/getApplyRecord]:sql"+sql);
        if(sortWay.equals("up")){
            sql+=" order by account";
        }else if(sortWay.equals("down")){
            sql+=" order by account desc";
        }
        ResultSet resultSet=this.statement.executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        List jsonList=new ArrayList();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }
        json.put("aaData",jsonList);
        resultSet.close();
        System.out.println(json);
        return json;
    }

    public JSONObject getDeviceRecord() throws SQLException, JSONException {
        String sql="select * from deviceInfo";
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getDeviceRecord()]将要执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("deviceId",resultSet.getInt("deviceId"));
            map.put("deviceNum",resultSet.getString("deviceNum"));
            map.put("deviceName",resultSet.getString("deviceName"));
            map.put("devicePrice",resultSet.getString("devicePrice"));
            map.put("deviceCompany",resultSet.getString("deviceCompany"));
            map.put("deviceLocation",resultSet.getString("deviceLocation"));
            map.put("deviceStatus",resultSet.getString("deviceStatus"));
            map.put("deviceModifyTime",resultSet.getTimestamp("deviceModifyTime"));
            map.put("lastLocation",resultSet.getString("lastLocation"));
            jsonList.add(map);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    // 该函数用于普适性的获取记录信息，主要用于导出，查询，等情况
    public JSONObject getRecord(String name) throws SQLException, JSONException {
        String sql="select * from "+name;
        System.out.println("[sqlOperator/getRecord()]将要执行SQL："+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        // 获取查询到数据的列信息
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();

        // 获取数据库中的内容
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            jsonList.add(map);
        }

        // 获取数据库中的属性，以作为表头
        List jsonName=new ArrayList();
        for(int i=0;i<fieldCount;i++){
            jsonName.add(resultSetMetaData.getColumnName(i+1));
        }
        JSONObject json=new JSONObject();
        json.put("record",jsonList);
        json.put("title",jsonName);
        resultSet.close();
        return json;
    }

    // 该函数可以用于记录删除，传入参数：表名；id
    public void DeleteRecord(String id,String tableName) throws SQLException {
        String sql="delete from "+tableName+" where ";
        // 获取删除的sql
        switch (tableName){
            case "accountInfo":{
                sql+="userId="+id;
                break;
            }
            case "deviceInfo":{
                sql+="deviceId="+id;
                break;
            }
            case "applyInfo":{
                sql+="id="+Integer.parseInt(id);
            }
        }
        System.out.println("[sqlOperator/DeleteRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }

    // 添加用户记录
    public void AddRecord(HashMap map) throws SQLException {

        // 指定属性插入
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into accountInfo(account,mail,password,userLevel,signUpDate,lastModifyDate,isWorker) values('"+map.get("account")+"','"+map.get("mail")+"','"+map.get("PASSWORD")+"','"+map.get("userLevel")+"','"+date+"','"+date+"',"+map.get("isWorker")+")";
        System.out.println("[sqlOperator/AddRecord]将要执行:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 修改信息
    public void ModifyUserRecord(HashMap map) throws SQLException {
        String sql="update accountInfo set account='"+map.get("account")+"',mail='"+map.get("mail")+"',userLevel='"+map.get("userLevel")+"',userName='"+map.get("userName")+"',userGender='"+map.get("userGender")+"',isWorker='"+map.get("isWorker")+"'";
        sql+=" where userId="+map.get("id");
        System.out.println("[sqlOperator/ModifyRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 统计用户信息
    public JSONObject StatisticsUserRecord() throws SQLException, JSONException {
        String sql="select count(*) as sumNum from accountInfo";
        String sql1="select userLevel,count(*) as levelNum  from accountInfo group by userLevel";
        String sql2="select count(*) as applyNum from applyInfo";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/Static]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/Static]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("manager",0);
        json.put("generalManager",0);
        json.put("generalUser",0);
        while (resultSet.next()){
            if(resultSet.getInt("userLevel")==0){
                json.put("manager",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("userLevel")==1){
                json.put("generalManager",resultSet.getInt("levelNum"));
            }else{
                json.put("generalUser",resultSet.getInt("levelNum"));
            }

        }

        System.out.println("[sqlOperator/Static]将要执行:"+sql2);
        resultSet=this.statement.executeQuery(sql2);
        resultSet.next();
        json.put("applyNum",resultSet.getInt("applyNum"));
        System.out.println(resultSet.getString("applyNum"));
        return json;
    }

    public void addApplyInfo(HashMap map) throws SQLException {
        // 获取日期
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into applyInfo(account,mail,PASSWORD,applyDate) values('"+map.get("account")+"','"+map.get("mail")+"','"+map.get("password")+"','"+date+"')";
        System.out.println("[sqlOperator/addApplyInfo]:SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    public void dealApply(String id, boolean All,String operator) throws SQLException {
        String sql="select * from applyInfo";
        if(!All){
            sql+=" where id="+Integer.parseInt(id);
        }
        System.out.println("[sqlOperator/dealApply]:sql:"+sql);
        // 保存查询结果
        // 一个statement同时只可执行一个sql，一个resultSet没有处理完毕时，不可执行另外一个sql
        getDbConnection db=new getDbConnection();
        ResultSet resultSet=db.getConnection().executeQuery(sql);
        ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
        int fieldCount=resultSetMetaData.getColumnCount();
        while (resultSet.next()){
            HashMap map=new HashMap();
            for(int i=0;i<fieldCount;i++){
                map.put(resultSetMetaData.getColumnName(i+1),resultSet.getString(resultSetMetaData.getColumnName(i+1)));
            }
            // 判断是否添加到用户信息表
            if(operator.equals("agree")){
                map.put("userLevel",2);
                map.put("isWorker",0);
                AddRecord(map);
                sendCode send=new sendCode((String) map.get("mail"));
                send.sendApplyResult(operator);
            }else{
                sendCode send=new sendCode((String) map.get("mail"));
                send.sendApplyResult(operator);
            }
        }
        db.close();
        // 接下来删除applyInfo中的信息...
        sql="delete from applyInfo ";
        if(!All){
            sql+=" where id="+Integer.parseInt(id);
        }
        this.statement.executeUpdate(sql);
    }

    public void ModifyApplyRecord(int id,HashMap map) throws SQLException {
        String sql="update applyInfo set account='"+map.get("account")+"',mail='"+map.get("mail")+"',PASSWORD='"+map.get("password")+"'";
        sql+=" where Id="+id;
        System.out.println("[sqlOperator/ModifyApplyRecord]:sql"+sql);
        this.statement.executeUpdate(sql);
    }

    // 断开链接。
    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }
}
