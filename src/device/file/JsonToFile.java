package device.file;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

public class JsonToFile {
    // 定义导出的路径信息
    final String txtPath="C:\\Users\\23632\\Desktop\\export\\export.txt";
    final String excelPath="C:\\Users\\23632\\Desktop\\export\\export.xls";


    // 将JSON导出成TXT文件
    public JSONObject setJsonTOTxt(JSONObject jsonObject) throws JSONException, IOException {
        // JSON转字符串
        String jsonstr=jsonObject.toString();

        File jsonFile=new File(this.txtPath);
        if(!jsonFile.exists()){
            jsonFile.createNewFile();
            System.out.println("创建文件成功!");
        }
        // 写入文件
        FileWriter fileWriter=new FileWriter(jsonFile.getAbsoluteFile());
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

        System.out.println("[sqlOperator/setJsonTOTxt]正在写入文件!");
        bufferedWriter.write(jsonstr);
        bufferedWriter.close();
        System.out.println("[sqlOperator/setJsonTOTxt]set JSON to Txt, success");
        // 使用JSON格式反馈下载路径
        JSONObject json=new JSONObject();
        json.put("txtDownloadPath","/upLoad/export.txt");
        return json;
    }


    // 将json导出成excel
    public void setJsonToExcel(JSONObject json,JSONObject jsonObject) throws IOException, JSONException {
        HSSFWorkbook workbook=new HSSFWorkbook();
        HSSFSheet sheet=workbook.createSheet("sheet0");

        // 获取表头
        JSONArray jsonTitle=json.getJSONArray("title");
        // 创建第一行
        HSSFRow rowTitle=sheet.createRow(0);
        // 设置表头
        for(int i=0;i<jsonTitle.length();i++){
            HSSFCell cell=rowTitle.createCell(i);
            cell.setCellValue((String) jsonTitle.get(i));
        }
        // 填充表内容
        JSONArray jsonArrayContent=json.getJSONArray("record");
        for(int i=0;i<jsonArrayContent.length();i++){
            HSSFRow row=sheet.createRow(i+1);

            // 获取内容
            int j=0;
            HashMap<String, String> record= (HashMap<String, String>) jsonArrayContent.get(i);
            for(int col=0;col<record.size();col++){
                HSSFCell cell=row.createCell(j);
                cell.setCellValue(record.get(jsonTitle.get(col)));
                j++;
            }
        }
        FileOutputStream outputStream=new FileOutputStream(this.excelPath);
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        jsonObject.put("excelDownloadPath","/upLoad/export.xls");
        jsonObject.put("ok",200);
        System.out.println("set JSON to excel, success");
    }

    // 将Json导出成一般的File
    public void setJsonToSimpleFile(JSONObject jsonObject){

        System.out.println("set JSON to simple File, success");
    }

}
