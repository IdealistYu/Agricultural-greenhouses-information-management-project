package warningModule.file;

import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

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
            case "showWarningTable":
                String isOrdered=req.getParameter("isOrdered");
                System.out.println("isOrdered="+isOrdered);
                try {
                    responseBack(req,resp,sqlOp.showWarningTable(isOrdered));
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "addWarningRecord":
                String warningRecord=req.getParameter("warning_record");
                String greenhouseId=req.getParameter("greenhouse_id");

                try {
                    if(sqlOp.addWarningRecord(warningRecord,greenhouseId)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "deleteWarningRecord":
                String warningId=req.getParameter("id");

                try {
                    if(sqlOp.deleteWarningRecord(warningId)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "modifyWarningRecord":
                String modifyWarningRecord=req.getParameter("warning_record");
                int modifyWarningId=Integer.parseInt(req.getParameter("warning_id"));
                String modifyGreenhouseId=req.getParameter("greenhouse_id");

                try {
                    if(sqlOp.modifyWarningRecord(modifyWarningRecord,modifyGreenhouseId,modifyWarningId)){
                        jsonObject.put("ok",200);
                        System.out.println("成功修改");
                    }else{
                        System.out.println("修改失败");
                        jsonObject.put("ok",400);
                    }
                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "exportWarningRecord":
                try {
                    JSONObject json=sqlOp.showWarningTable("false");
//                    getExportWarningRecordToFile(json);
                    getExportWarningRecordToExcel(json);
                    json.put("ok",200);
                    responseBack(req,resp,json);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "statisticWarningRecord":
                try {
                    System.out.println("进入统计ServletAction");
                    responseBack(req,resp,sqlOp.statisticWarningTable());
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "queryWarningRecord":
                warningRecord=req.getParameter("warning_record");
                greenhouseId=req.getParameter("greenhouse_id");

                try {
                    responseBack(req,resp,sqlOp.queryWarningRecord(warningRecord,greenhouseId));
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;


            default:{
                break;
            }
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

    private void getExportWarningRecordToFile(JSONObject json) throws JSONException {
        String jsonStr = json.toString();
        File jsonFile = new File("D:\\test\\maintain\\device\\export_device.txt");
        json.put("download_url","/test/maintain/device/export_device.txt");
        try {
            // 文件不存在就创建文件
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(jsonStr);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getExportWarningRecordToExcel(JSONObject json) throws JSONException, IOException {
        MyExcel me=new MyExcel("D:\\test\\maintain\\device\\export_device.xls");
        json.put("download_url","/test/maintain/device/export_device.xls");
        json.put("file_path","D:\\test\\maintain\\device\\export_device.xls");
        me.exportData(json);
    }

}
