package salaryModule.file;

import org.json.JSONException;
import org.json.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

            case "showSalaryTable":
                String isOrdered=req.getParameter("isOrdered");
                System.out.println("isOrdered="+isOrdered);
                try {
                    responseBack(req,resp,sqlOp.showSalaryTable(isOrdered));
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "addSalaryRecord":
                String employee_id=req.getParameter("employee_id");
                String employee_name=req.getParameter("employee_name");
                String employee_duty=req.getParameter("employee_duty");
                String salary_number=req.getParameter("salary_number");
                String salary_remark=req.getParameter("salary_remark");
                String salary_month=req.getParameter("salary_month");

                try {
                    if(sqlOp.addSalaryRecord(employee_id,employee_name,employee_duty,salary_number,salary_remark,salary_month)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "deleteSalaryRecord":
                String salary_id=req.getParameter("salary_id");

                try {
                    if(sqlOp.deleteSalaryRecord(salary_id)){
                        jsonObject.put("ok",200);
                    }else{
                        jsonObject.put("ok",400);
                    }
                    responseBack(req,resp,jsonObject);
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "modifySalaryRecord":
                salary_id=req.getParameter("salary_id");
                employee_id=req.getParameter("employee_id");
                employee_name=req.getParameter("employee_name");
                employee_duty=req.getParameter("employee_duty");
                salary_number=req.getParameter("salary_number");
                salary_remark=req.getParameter("salary_remark");
                salary_month=req.getParameter("salary_month");

                try {
                    if(sqlOp.modifySalaryRecord(salary_id,employee_id,employee_name,employee_duty,salary_number,salary_remark,salary_month)){
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

            case "querySalaryRecord":
                String query_employee_name = req.getParameter("employee_name");
                String query_salary_month = req.getParameter("salary_month");

                try {
                    responseBack(req, resp, sqlOp.querySalaryRecord(query_employee_name, query_salary_month));
                } catch (SQLException | JSONException e) {
                    e.printStackTrace();
                }
                break;


            case "exportSalaryRecord":
                try {
                    JSONObject json=sqlOp.showSalaryTable("false");
//                    getExportWarningRecordToFile(json);
                    getExportSalaryRecordToExcel(json);
                    json.put("ok",200);
                    responseBack(req,resp,json);
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "statisticSalaryRecord": {
                try {
                    System.out.println("进入统计ServletAction!!!!");
                    jsonObject = sqlOp.statisticSalaryTable();
                    jsonObject.put("ok", 200);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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

    private void getExportSalaryRecordToExcel(JSONObject json) throws JSONException, IOException {
        MyExcel me=new MyExcel("C:\\upload\\maintain\\device\\export_salary.xls");
        json.put("download_url","/upload/maintain/device/export_salary.xls");
        json.put("file_path","C:\\upload\\maintain\\device\\export_salary.xls");
        me.exportData(json);
    }

}
