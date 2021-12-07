package salaryModule.file;

import DbOperator.getDbConnection;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class sqlOperator {

    private Statement statement;

    public sqlOperator(){
        getDbConnection dbConnection=new getDbConnection();
        this.statement=dbConnection.getConnection();
        System.out.println("准备数据库操作完毕!");
    }

    // 以下是相关数据库的操作，包括增删改查

    public JSONObject showSalaryTable(String isOrdered) throws SQLException, JSONException {
        String sql;
        System.out.println("sqlOperator中isOrdered="+isOrdered);
        if(isOrdered.equals("true")){
            System.out.println("按工资降序");
            sql="select * from salary_file order by salary_number desc ";
        }
        else{
            sql="select * from salary_file";
        }

        ArrayList jsonList=new ArrayList();
        ArrayList jsonName=new ArrayList();
        try {
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map map = new HashMap();
                for (int i = 0; i < fieldCount; i++) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }
                jsonList.add(map);
            }
            rs.close();
            //加表头信息
            for(int i=0;i<rsmd.getColumnCount();i++){
                String columLabel= rsmd.getColumnLabel(i+1);
                jsonName.add(columLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }
        JSONObject json=new JSONObject();
        json.put("aaFieldName",jsonName);
        json.put("aaData",jsonList);
        return json;
    }

    public Boolean addSalaryRecord(String employee_id,String employee_name,String employee_duty,String salary_number,String salary_remark,String salary_month) throws SQLException, JSONException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql="insert into salary_file(employee_id,employee_name,employee_duty,salary_number,salary_remark,salary_month,salary_datetime)";
        sql=sql+" values('"+employee_id+"'";
        sql=sql+" ,'"+employee_name+"'";
        sql=sql+" ,'"+employee_duty+"'";
        sql=sql+" ,'"+salary_number+"'";
        sql=sql+" ,'"+salary_remark+"'";
        sql=sql+" ,'"+salary_month+"'";
        sql=sql+" ,'"+ dateTime.format(formatter) +"')";
        try {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public Boolean deleteSalaryRecord(String salary_id) throws SQLException, JSONException {
        String sql="delete from salary_file where salary_id="+salary_id;

        try {
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public Boolean modifySalaryRecord(String salary_id,String employee_id,String employee_name,String employee_duty,String salary_number,String salary_remark,String salary_month) throws SQLException, JSONException {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String sql="update salary_file set employee_id='"+employee_id+"',employee_name='"+employee_name+"',employee_duty='"+employee_duty+"',salary_number='"+salary_number+"',salary_remark='"+salary_remark+"',salary_month='"+salary_month+"',salary_datetime='"+dateTime.format(formatter)+"'";
        sql+=" where salary_id="+salary_id+";";
        try {
            statement.executeUpdate(sql);
            System.out.println("成功执行sql语句："+sql);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
            return false;
        }
        return true;
    }

    public JSONObject querySalaryRecord(String employee_name,String salary_month) throws SQLException, JSONException {
        String sql="select * from salary_file where employee_name like '"+employee_name+"' or salary_month='"+salary_month+"';";
        ArrayList jsonList=new ArrayList();
        System.out.println("执行的sql语句为："+sql);

        try {
            ResultSet rs = statement.executeQuery(sql);
            ResultSetMetaData rsmd = rs.getMetaData();
            int fieldCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map map = new HashMap();
                for (int i = 0; i < fieldCount; i++) {
                    map.put(rsmd.getColumnName(i + 1), rs.getString(rsmd.getColumnName(i + 1)));
                }
                jsonList.add(map);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[queryRecord]查询数据库出现错误：" + sql);
        }

        JSONObject json=new JSONObject();
        json.put("ok",200);
        json.put("aaData",jsonList);
        return json;
    }

    public JSONObject statisticSalaryTable() throws SQLException, JSONException {
        String sql="select  count(*) as salary_1 from salary_file where salary_number < '3000'";
        JSONObject json=new JSONObject();
        System.out.println("[sqlOperator/StatisticsGreenhouseSizeRecord]将要执行:"+sql);
        ResultSet resultSet=this.statement.executeQuery(sql);
        resultSet.next();
        json.put("salary_1",resultSet.getInt("salary_1"));

        String sql1="select  count(*) as salary_2 from salary_file where salary_number >= '3000' and salary_number <'6000' ";
        resultSet=this.statement.executeQuery(sql1);
        resultSet.next();
        json.put("salary_2",resultSet.getInt("salary_2"));


        String sql2="select  count(*) as salary_3 from salary_file where salary_number >= '6000' and salary_number < '9000' ";
        resultSet=this.statement.executeQuery(sql2);
        resultSet.next();
        json.put("salary_3",resultSet.getInt("salary_3"));

        String sql3="select  count(*) as salary_4 from salary_file where salary_number >= '9000' and salary_number < '12000' ";
        resultSet=this.statement.executeQuery(sql3);
        resultSet.next();
        json.put("salary_4",resultSet.getInt("salary_4"));

        String sql4="select  count(*) as salary_5 from salary_file where salary_number >= '12000' ";
        resultSet=this.statement.executeQuery(sql4);
        resultSet.next();
        json.put("salary_5",resultSet.getInt("salary_5"));
        return json;
    }

    public void CloseCn() throws SQLException {
        this.statement.close();
        System.out.println("数据库链接关闭！");
    }

}
