package greenhouseModule.file;

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
    public JSONObject getGreenhouseRecordList(String sortWay) throws SQLException, JSONException {
        String sql="select * from greenhouse_file";
        // 定义升序或降序
        if(sortWay.equals("up")){
            sql+=" order by greenhouse_id";
        }else if(sortWay.equals("down")){
            sql+=" order by greenhouse_id desc";
        }

        ResultSet resultSet=this.statement.executeQuery(sql);
        System.out.println("[sqlOperator/getGreenhouseRecordList()]执行SQL："+sql);
        List jsonList=new ArrayList();
        while (resultSet.next()){
            Map map=new HashMap();
            map.put("greenhouse_id",resultSet.getString("greenhouse_id"));
            map.put("greenhouse_name",resultSet.getString("greenhouse_name"));
            map.put("greenhouse_status",resultSet.getString("greenhouse_status"));
            map.put("greenhouse_remark",resultSet.getString("greenhouse_remark"));
            map.put("greenhouse_datetime",resultSet.getString("greenhouse_datetime"));
            map.put("greenhouse_manager",resultSet.getString("greenhouse_manager"));
            map.put("greenhouse_size",resultSet.getString("greenhouse_size"));
            jsonList.add(map);
        }
        System.out.println(jsonList);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("aaData",jsonList);
        return jsonObject;
    }

    //该函数用于添加数据------------------------------------------------------------------------------------------------
    public void AddGreenhouseRecord(HashMap map) throws SQLException {
        // 指定属性插入
        String date=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String sql="insert into greenhouse_file(greenhouse_name,greenhouse_status,greenhouse_remark,greenhouse_datetime,greenhouse_manager,greenhouse_size) values('"+map.get("greenhouse_name")+"','"+map.get("greenhouse_status")+"','"+map.get("greenhouse_remark")+"','"+map.get("greenhouse_datetime")+"','"+map.get("greenhouse_manager")+"','"+map.get("greenhouse_size")+"')";
        System.out.println("[sqlOperator/AddGreenhouseRecord]将要执行:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 该函数可以用于记录删除，传入参数：表名；id-------------------------------------------------------------------------------
    public void DeleteGreenhouseRecord(String id,String tableName) throws SQLException {
        String sql="delete from "+tableName+" where ";
        // 获取删除的sql
        switch (tableName){
            case "greenhouse_file":{
                sql+="greenhouse_id="+id;
                break;
            }
        }
        System.out.println("[sqlOperator/DeleteGreenhouseRecord]将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);

    }

    // 该函数用于普适性的获取记录信息，主要用于导出，查询，等情况------------------------------------------------------------------
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

    // 修改信息-----------------------------------------------------------------------------------------------------------
    public void ModifyGreenhouse_Record(HashMap map) throws SQLException {
        String sql="update greenhouse_file set greenhouse_name='"+map.get("greenhouse_name")+"',greenhouse_status='"+map.get("greenhouse_status")+"',greenhouse_remark='"+map.get("greenhouse_remark")+"',greenhouse_datetime='"+map.get("greenhouse_datetime")+"',greenhouse_manager='"+map.get("greenhouse_manager")+"',greenhouse_size='"+map.get("greenhouse_size")+"'";
        sql+=" where greenhouse_id="+map.get("id");
        System.out.println("[sqlOperator/ModifyRecord]：将要执行SQL:"+sql);
        this.statement.executeUpdate(sql);
    }

    // 统计用户信息-------------------------------------------------------------------------------------------------------
    public JSONObject StatisticsGreenhouseSizeRecord() throws SQLException, JSONException {
        String sql="select  count(*) as sumNum from greenhouse_file";
        String sql1="select greenhouse_size,count(*) as levelNum  from greenhouse_file group by greenhouse_size";

        JSONObject json=new JSONObject();

        System.out.println("[sqlOperator/StatisticsGreenhouseSizeRecord]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("sumNum",resultSet.getInt("sumNum"));
        System.out.println(resultSet.getInt("sumNum"));

        System.out.println("[sqlOperator/StatisticsGreenhouseSizeRecord]将要执行:"+sql1);
        resultSet=this.statement.executeQuery(sql1);
        json.put("superGreenhouse",0);
        json.put("ordinaryGreenhouse",0);
        json.put("mediumGreenhouse",0);
        json.put("smallGreenhouse",0);
        while (resultSet.next()){
            if(resultSet.getInt("greenhouse_size")==0){
                json.put("superGreenhouse",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("greenhouse_size")==1){
                json.put("ordinaryGreenhouse",resultSet.getInt("levelNum"));
            }else if(resultSet.getInt("greenhouse_size")==2){
                json.put("mediumGreenhouse",resultSet.getInt("levelNum"));
            }
            else{
                json.put("smallGreenhouse",resultSet.getInt("levelNum"));
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
