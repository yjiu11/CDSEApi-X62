package com.boc.cdse;
import java.util.*;
import java.util.Date;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class WeekReport {

	/** 日报告文件 */
	ReportVariant RV = null;
	Document doc = null;

	private String ReportDir;// = "d:/ftp/cds/reports/";
	//==
	private String CurrentYear = "";
	private String CurrentWeek = "";
	private String CurrentFileName = "";

	public void toWeekReport(CDSEResult data,boolean hasLoadDateXML)  {
	try{
		//当前日期

	    Calendar c = new GregorianCalendar();
		int year = c.get(c.YEAR);
	    String tmpYear = Integer.toString(year);
		// GEDFWwaef
	    SimpleDateFormat  sd = new SimpleDateFormat("w");
		String tmpWeek =  sd.format(new Date());

		if (!tmpYear.equals(CurrentYear) || !tmpWeek.equals(CurrentWeek)){
			CurrentYear = tmpYear;
			CurrentWeek = tmpWeek;
			//当前文件名及其所在路径
			CurrentFileName = ReportDir + "WeeklyReport" + year + "-" + CurrentWeek + ".xml";
			//报表XML变量
			if (RV == null)
				RV = new ReportVariant();
			//XML操作

			if (! RV.ExistReportFile(CurrentFileName)){
				doc = CreateXMLFile();
			}
			else{
				doc = RV.readXMLFile(CurrentFileName);
				}
		} else
		if (! hasLoadDateXML)
			doc = RV.readXMLFile(CurrentFileName);

		//如果不存在周报或则进入了新的一周，则先创见该周报文件,否则导入该周报文件
		/*
		if (! RV.ExistReportFile(CurrentFileName))
		  {
			doc = CreateXMLFile();
		}else{
			doc = readXMLFile(CurrentFileName);
		}
		*/
	   //数据处理
	   doc = RV.ActionXMLData(doc,data,CurrentFileName);
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
			"href=\"../reports/WeeklyTransform.xsl\" type=\"text/xsl\"");
        doc.appendChild(pi);
		//下面是建立XML文档内容的过程，先建立根元素"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//下面是建立XML文档内容的过程，先建立二级元素"report"
		Element report = doc.createElement("report");
		//用类反射机制取得所有属性名称 没有测试过是否可行!
		Class c =  ReportVariant.class;
		Field[] field = c.getDeclaredFields();
		//Attr att = null;
		for(int i = 0 ; i< field.length ;i++){
			String attname = (field[i].toString()).substring(field[i].toString().lastIndexOf(".")+1);
			report.setAttribute(attname,"0");
		}
		//删除不属于week报属性
		//report.removeAttribute("Year");
		report.removeAttribute("Month");
		report.removeAttribute("Date");
		//report.removeAttribute("Week");
		report.removeAttribute("_instance");
		//初始化week report year & Week
		report.setAttribute("Year",CurrentYear);
		report.setAttribute("Week",CurrentWeek);

		//根元素添加上文档
		xml.appendChild(report);

		RV.outputFile(doc,CurrentFileName);

		return doc;

	}

	public void setReportDir(String ReportDir) {
		this.ReportDir = ReportDir;
	}

	public String getReportDir() {
		return ReportDir;
	}

}
