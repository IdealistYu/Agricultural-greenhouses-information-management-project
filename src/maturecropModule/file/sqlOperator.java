package maturecropModule.file;

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

    //查询数据------------------------------------------------------------------------------------------------------------
    public JSONObject getMaturecropRecordList(String Value) throws SQLException, JSONException {
        String sql="select * from maturecrop_file where greenhouse_id="+Value;
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getMaturecropRecordList()]执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("maturecrop_id",resultSet.getString("maturecrop_id"));
            map.put("maturecrop_name",resultSet.getString("maturecrop_name"));
            map.put("maturecrop_number",resultSet.getString("maturecrop_number"));
            map.put("maturecrop_status",resultSet.getString("maturecrop_status"));
            map.put("maturecrop_remark",resultSet.getString("maturecrop_remark"));
            map.put("maturecrop_datetime",resultSet.getString("maturecrop_datetime"));
            jsonList.add(map);
        }
        System.out.println(jsonList);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    //该函数用于添加数据------------------------------------------------------------------------------------------------
    public void AddMatureCropRecord(HashMap map) throws SQLException {
        // 指定属性插入
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into maturecrop_file(maturecrop_name,greenhouse_id,maturecrop_number,maturecrop_status,maturecrop_remark,maturecrop_datetime) values('"+map.get("maturecrop_name")+"','"+map.get("greenhouse_id")+"','"+map.get("maturecrop_number")+"','"+map.get("maturecrop_status")+"','"+map.get("maturecrop_remark")+"','"+map.get("maturecrop_datetime")+"')";
        System.out.println("[sqlOperator/AddMatureCropRecord]将要执行:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 该函数可以用于记录删除，传入参数：表名；id-------------------------------------------------------------------------------
    public void DeleteMatureCropRecord(String id,String tableName) throws SQLException {
        String sql="delete from "+tableName+" where ";
        // 获取删除的sql
        switch (tableName){
            case "maturecrop_file":{
                sql+="maturecrop_id="+id;
                break;
            }
        }
        System.out.println("[sqlOperator/DeleteMatureCropRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }

    // 该函数用于普适性的获取记录信息，主要用于导出等情况------------------------------------------------------------------
    public JSONObject getRecord(String value) throws SQLException, JSONException {
        String sql="select * from maturecrop_file where greenhouse_id="+value;
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

    // 修改信息-----------------------------------------------------------------------------------------------------------
    public void ModifyMatureCropRecord(HashMap map) throws SQLException {
        String sql="update maturecrop_file set maturecrop_name='"+map.get("maturecrop_name")+"',maturecrop_number='"+map.get("maturecrop_number")+"',maturecrop_status='"+map.get("maturecrop_status")+"',maturecrop_remark='"+map.get("maturecrop_remark")+"',maturecrop_datetime='"+map.get("maturecrop_datetime")+"'";
        sql+=" where maturecrop_id="+map.get("id");
        System.out.println("[sqlOperator/ModifyMatureCropRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 统计信息-------------------------------------------------------------------------------------------------------
    public JSONObject StatisticsMatureCropRecord(String Value) throws SQLException, JSONException {
        System.out.println(Value);
        String sql="select  count(*) as sumNum from maturecrop_file where greenhouse_id="+Value;
        String sql1="select maturecrop_number,count(*) as levelNum  from maturecrop_file  where greenhouse_id="+Value+" group by maturecrop_number";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/StatisticsMatureCropRecord]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/StatisticsMatureCropRecord]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("Low_production",0);
        json.put("Medium_production",0);
        json.put("High_production",0);
        json.put("Ultra_high_production",0);
        while (resultSet.next()){
            if(resultSet.getInt("maturecrop_number")<=500){
                json.put("Low_production",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("maturecrop_number")>500&&resultSet.getInt("maturecrop_number")<=1000){
                json.put("Medium_production",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("maturecrop_number")>1000&&resultSet.getInt("maturecrop_number")<=2000){
                json.put("High_production",resultSet.getInt("levelNum"));
            }
            else{
                json.put("Ultra_high_production",resultSet.getInt("levelNum"));
            }

        }
        return json;
    }

    // 断开链接----------------------------------------------------------------------------------------------------------
    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }
}
