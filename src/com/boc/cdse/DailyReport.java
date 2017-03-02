package com.boc.cdse;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class DailyReport {

	/** 日报告文件 */
    ReportVariant RV = null;
	Document doc = null;

	// by ReportManager set
	private String ReportDir;// = "d:/ftp/cds/reports/";

	private String CurrentDate = "";
	private String CurrentDateFileName = "";



	public void toDaliyReport(CDSEResult data,boolean hasLoadDateXML)  {
		try{
			boolean newfile = false;
			//boolean loadfile = false;

			//当前日期
			//String CurrentDate = CDSEUtil.getCurrentDate();
			Calendar cal = Calendar.getInstance();
			//get current week
			//int aa = cal.WEEK_OF_YEAR;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			//CurrentDate = formatter.format(cal.getTime());
			String tmpDate = formatter.format(cal.getTime());
			if (! tmpDate.equals(CurrentDate)){
				CurrentDate = tmpDate;
				//当前日期日报文件名及其所在路径
				CurrentDateFileName = ReportDir + "DailyRport" + CurrentDate + ".xml";

				//报表XML变量
				if (RV == null)
    				RV = new ReportVariant();
				if (! RV.ExistReportFile(CurrentDateFileName)){
					doc = CreateXMLFile();
				}
				else{
					doc = RV.readXMLFile(CurrentDateFileName);
					}
			} else
			if (! hasLoadDateXML){
				doc = RV.readXMLFile(CurrentDateFileName);
			}
			/*
			//如果不存在日报，则先创见该日报文件,否则导入该日报文件
			if (! RV.ExistReportFile(CurrentDateFileName)) {
				doc = CreateXMLFile();
			}else{
				doc = readXMLFile(CurrentDateFileName);
			}
		    */
		   //数据处理
		   doc = RV.ActionXMLData(doc,data,CurrentDateFileName);
		   //outputFile(doc);

		   //ActionXMLData(doc,data);
       } catch (Exception ex) {
		    ex.printStackTrace();
		}
	}


	/**
	 * 根据表单提交数据，创建Dom树
	 *
	 * @param request 正在处理的HTTP请求
	 *
	 * @return Dom树
	 */
	private Document CreateXMLFile() {

		Document doc = null;
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return doc;
		}

		doc = db.newDocument();

		ProcessingInstruction pi = doc.createProcessingInstruction(
			"xml-stylesheet",
			"href=\"../reports/DailyTransform.xsl\" type=\"text/xsl\"");
        doc.appendChild(pi);

		//下面是建立XML文档内容的过程，先建立根元素"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//下面是建立XML文档内容的过程，先建立二级元素"report"
		Element report = doc.createElement("report");

	    //用类反射机制取得所有属性名称 没有测试过是否可行!
        Class c =  ReportVariant.class;
		Field[] field = c.getDeclaredFields();

		for(int i = 0 ; i< field.length ;i++){
			String attname = (field[i].toString()).substring(field[i].toString().lastIndexOf(".")+1);
			report.setAttribute(attname,"0");
        }
	    //删除不属于日报属性
		report.removeAttribute("Year");
		report.removeAttribute("Month");
		report.removeAttribute("Week");
		report.removeAttribute("_instance");
		//初始化日报日期
		report.setAttribute("Date",CurrentDate);
		//根元素添加上文档
		xml.appendChild(report);

	    //save  to disk
		//RV.outputFile(doc,CurrentDataFileName);
		RV.outputFile(doc,CurrentDateFileName);


		return doc;
	}

	public void setReportDir(String ReportDir) {
		this.ReportDir = ReportDir;

	}

	public String getReportDir() {
		return ReportDir;
	}

}
