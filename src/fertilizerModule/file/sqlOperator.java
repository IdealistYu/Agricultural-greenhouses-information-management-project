package fertilizerModule.file;

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
    public JSONObject getFertilizerRecordList(String Value) throws SQLException, JSONException {
        String sql="select * from fertilizer_file where greenhouse_id="+Value;
        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getFertilizerRecordList()]执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("fertilizer_id",resultSet.getString("fertilizer_id"));
            map.put("fertilizer_name",resultSet.getString("fertilizer_name"));
            map.put("fertilizer_number",resultSet.getString("fertilizer_number"));
            map.put("fertilizer_use_datetime",resultSet.getString("fertilizer_use_datetime"));
            map.put("fertilizer_status",resultSet.getString("fertilizer_status"));
            map.put("fertilizer_remark",resultSet.getString("fertilizer_remark"));
            map.put("fertilizer_datetime",resultSet.getString("fertilizer_datetime"));
            jsonList.add(map);
        }
        System.out.println(jsonList);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    //该函数用于添加数据------------------------------------------------------------------------------------------------
    public void AddFertilizerRecord(HashMap map) throws SQLException {
        // 指定属性插入
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into fertilizer_file(fertilizer_name,greenhouse_id,fertilizer_number,fertilizer_use_datetime,fertilizer_status,fertilizer_remark,fertilizer_datetime) values('"+map.get("fertilizer_name")+"','"+map.get("greenhouse_id")+"','"+map.get("fertilizer_number")+"','"+map.get("fertilizer_use_datetime")+"','"+map.get("fertilizer_status")+"','"+map.get("fertilizer_remark")+"','"+map.get("fertilizer_datetime")+"')";
        System.out.println("[sqlOperator/AddFertilizerRecord]将要执行:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 该函数可以用于记录删除，传入参数：表名；id-------------------------------------------------------------------------------
    public void DeleteFertilizerRecord(String id,String tableName) throws SQLException {
        String sql="delete from "+tableName+" where ";
        // 获取删除的sql
        switch (tableName){
            case "fertilizer_file":{
                sql+="fertilizer_id="+id;
                break;
            }
        }
        System.out.println("[sqlOperator/DeleteFertilizerRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }

    // 该函数用于普适性的获取记录信息，主要用于导出等情况------------------------------------------------------------------
    public JSONObject getRecord(String value) throws SQLException, JSONException {
        String sql="select * from fertilizer_file where greenhouse_id="+value;
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
    public void ModifyFertilizerRecord(HashMap map) throws SQLException {
        String sql="update fertilizer_file set fertilizer_name='"+map.get("fertilizer_name")+"',fertilizer_number='"+map.get("fertilizer_number")+"',fertilizer_use_datetime='"+map.get("fertilizer_use_datetime")+"',fertilizer_status='"+map.get("fertilizer_status")+"',fertilizer_remark='"+map.get("fertilizer_remark")+"',fertilizer_datetime='"+map.get("fertilizer_datetime")+"'";
        sql+=" where fertilizer_id="+map.get("id");
        System.out.println("[sqlOperator/ModifyFertilizerRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 统计信息-------------------------------------------------------------------------------------------------------
    public JSONObject StatisticsFertilizerRecord(String Value) throws SQLException, JSONException {
        System.out.println(Value);
        String sql="select  count(*) as sumNum from fertilizer_file where greenhouse_id="+Value;
        String sql1="select fertilizer_number,count(*) as levelNum  from fertilizer_file  where greenhouse_id="+Value+" group by fertilizer_number";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/StatisticsFertilizerRecord]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/StatisticsFertilizerRecord]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("Low_margin",0);
        json.put("Medium_margin",0);
        json.put("High_margin",0);
        json.put("Ultra_high_margin",0);
        while (resultSet.next()){
            if(resultSet.getInt("fertilizer_number")<=500){
                json.put("Low_margin",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("fertilizer_number")>500&&resultSet.getInt("fertilizer_number")<=1000){
                json.put("Medium_margin",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("fertilizer_number")>1000&&resultSet.getInt("fertilizer_number")<=2000){
                json.put("High_margin",resultSet.getInt("levelNum"));
            }
            else{
                json.put("Ultra_high_margin",resultSet.getInt("levelNum"));
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
