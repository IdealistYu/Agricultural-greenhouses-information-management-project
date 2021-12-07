package maturecropModule.file;


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
//        super.doGet(req, resp);
        System.out.println("执行doGet!");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
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

            // 此增加功能进行数据的增加-------------------------------------------------------------------------------
            case "add_mature_crop_record":{
                String maturecrop_name=req.getParameter("maturecrop_name");
                String greenhouse_id=req.getParameter("greenhouse_id");
                String maturecrop_number=req.getParameter("maturecrop_number");
                String maturecrop_status=req.getParameter("maturecrop_status");
                String maturecrop_remark=req.getParameter("maturecrop_remark");
                String maturecrop_datetime=req.getParameter("maturecrop_datetime");
                HashMap map=new HashMap();
                map.put("maturecrop_name",maturecrop_name);
                map.put("greenhouse_id",greenhouse_id);
                map.put("maturecrop_number",maturecrop_number);
                map.put("maturecrop_status",maturecrop_status);
                map.put("maturecrop_remark",maturecrop_remark);
                map.put("maturecrop_datetime",maturecrop_datetime);
                try {
                    sqlOp.AddMatureCropRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 此delete可以对不同的数据表进行适配----------------------------------------------------------------------------
            case "delete_mature_crop_record":{
                String id=req.getParameter("id");
                String dbName=req.getParameter("tableName");
                System.out.println("ID:"+id+"  Dbname:"+dbName);
                try {
                    sqlOp.DeleteMatureCropRecord(id,dbName);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //修改-------------------------------------------------------------------------------------------------------
            case "modify_mature_crop_record":{
                String id=req.getParameter("id");
                String maturecrop_name=req.getParameter("maturecrop_name");
                String maturecrop_number=req.getParameter("maturecrop_number");
                String maturecrop_status=req.getParameter("maturecrop_status");
                String maturecrop_remark=req.getParameter("maturecrop_remark");
                String maturecrop_datetime=req.getParameter("maturecrop_datetime");
                HashMap map=new HashMap();
                map.put("id",id);
                map.put("maturecrop_name",maturecrop_name);
                map.put("maturecrop_number",maturecrop_number);
                map.put("maturecrop_status",maturecrop_status);
                map.put("maturecrop_remark",maturecrop_remark);
                map.put("maturecrop_datetime",maturecrop_datetime);
                try {
                    sqlOp.ModifyMatureCropRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //统计数据-----------------------------------------------------------------------------------------------
            case "statistics_mature_crop_record":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.StatisticsMatureCropRecord(value);
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取信息，用于构成DataTable------------------------------------------------------------------------------
            case "getMaturecropRecordList":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.getMaturecropRecordList(value);
                } catch (SQLException | JSONException e) {
                    System.out.println("获取数据失败!");
                    e.printStackTrace();
                }
                break;
            }

            // 导出文件--------------------------------------------------------------------------------------------------
            case "exportFile":{
                // 该json用于保存导出的数据
                JSONObject json=new JSONObject();
                System.out.println("正在导出");
                // 传入大棚ID
                String value=req.getParameter("value");
                System.out.println("传入大棚ID:"+value);
                try {
                    // 获取到查询结果的json
                    json=sqlOp.getRecord(value);

                    // JSON导出文件的工具类对象
                    JsonToFile jsonToFile=new JsonToFile();
                    jsonObject=jsonToFile.setJsonTOTxt(json);

                    // 转excel
                    jsonToFile.setJsonToExcel(json,jsonObject);
                    System.out.println(jsonObject);
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

    //回显---------------------------------------------------------------------------------------------------------------
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

        }
    }


}
