package fertilizerModule.file;


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
            case "add_fertilizer_record":{
                String fertilizer_name=req.getParameter("fertilizer_name");
                String greenhouse_id=req.getParameter("greenhouse_id");
                String fertilizer_number=req.getParameter("fertilizer_number");
                String fertilizer_use_datetime=req.getParameter("fertilizer_use_datetime");
                String fertilizer_status=req.getParameter("fertilizer_status");
                String fertilizer_remark=req.getParameter("fertilizer_remark");
                String fertilizer_datetime=req.getParameter("fertilizer_datetime");
                HashMap map=new HashMap();
                map.put("fertilizer_name",fertilizer_name);
                map.put("greenhouse_id",greenhouse_id);
                map.put("fertilizer_number",fertilizer_number);
                map.put("fertilizer_use_datetime",fertilizer_use_datetime);
                map.put("fertilizer_status",fertilizer_status);
                map.put("fertilizer_remark",fertilizer_remark);
                map.put("fertilizer_datetime",fertilizer_datetime);
                try {
                    sqlOp.AddFertilizerRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            // 此delete可以对不同的数据表进行适配----------------------------------------------------------------------------
            case "delete_fertilizer_record":{
                String id=req.getParameter("id");
                String dbName=req.getParameter("tableName");
                System.out.println("ID:"+id+"  Dbname:"+dbName);
                try {
                    sqlOp.DeleteFertilizerRecord(id,dbName);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //修改-------------------------------------------------------------------------------------------------------
            case "modify_fertilizer_record":{
                String id=req.getParameter("id");
                String fertilizer_name=req.getParameter("fertilizer_name");
                String fertilizer_number=req.getParameter("fertilizer_number");
                String fertilizer_use_datetime=req.getParameter("fertilizer_use_datetime");
                String fertilizer_status=req.getParameter("fertilizer_status");
                String fertilizer_remark=req.getParameter("fertilizer_remark");
                String fertilizer_datetime=req.getParameter("fertilizer_datetime");
                HashMap map=new HashMap();
                map.put("id",id);
                map.put("fertilizer_name",fertilizer_name);
                map.put("fertilizer_number",fertilizer_number);
                map.put("fertilizer_use_datetime",fertilizer_use_datetime);
                map.put("fertilizer_status",fertilizer_status);
                map.put("fertilizer_remark",fertilizer_remark);
                map.put("fertilizer_datetime",fertilizer_datetime);
                try {
                    sqlOp.ModifyFertilizerRecord(map);
                    jsonObject.put("ok",200);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;
            }

            //统计数据-----------------------------------------------------------------------------------------------
            case "statistics_fertilizer_record":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.StatisticsFertilizerRecord(value);
                    jsonObject.put("ok",200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            }

            // 获取信息，用于构成DataTable------------------------------------------------------------------------------
            case "getFertilizerRecordList":{
                String value=req.getParameter("value");
                System.out.println(value);
                try {
                    jsonObject=sqlOp.getFertilizerRecordList(value);
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
