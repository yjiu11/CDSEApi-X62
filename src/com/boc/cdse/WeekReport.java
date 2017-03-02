package com.boc.cdse;
import java.util.*;
import java.util.Date;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class WeekReport {

	/** �ձ����ļ� */
	ReportVariant RV = null;
	Document doc = null;

	private String ReportDir;// = "d:/ftp/cds/reports/";
	//==
	private String CurrentYear = "";
	private String CurrentWeek = "";
	private String CurrentFileName = "";

	public void toWeekReport(CDSEResult data,boolean hasLoadDateXML)  {
	try{
		//��ǰ����

	    Calendar c = new GregorianCalendar();
		int year = c.get(c.YEAR);
	    String tmpYear = Integer.toString(year);
		// GEDFWwaef
	    SimpleDateFormat  sd = new SimpleDateFormat("w");
		String tmpWeek =  sd.format(new Date());

		if (!tmpYear.equals(CurrentYear) || !tmpWeek.equals(CurrentWeek)){
			CurrentYear = tmpYear;
			CurrentWeek = tmpWeek;
			//��ǰ�ļ�����������·��
			CurrentFileName = ReportDir + "WeeklyReport" + year + "-" + CurrentWeek + ".xml";
			//����XML����
			if (RV == null)
				RV = new ReportVariant();
			//XML����

			if (! RV.ExistReportFile(CurrentFileName)){
				doc = CreateXMLFile();
			}
			else{
				doc = RV.readXMLFile(CurrentFileName);
				}
		} else
		if (! hasLoadDateXML)
			doc = RV.readXMLFile(CurrentFileName);

		//����������ܱ�����������µ�һ�ܣ����ȴ������ܱ��ļ�,��������ܱ��ļ�
		/*
		if (! RV.ExistReportFile(CurrentFileName))
		  {
			doc = CreateXMLFile();
		}else{
			doc = readXMLFile(CurrentFileName);
		}
		*/
	   //���ݴ���
	   doc = RV.ActionXMLData(doc,data,CurrentFileName);
	   //outputFile(doc);
	   //ActionXMLData(doc,data);
   } catch (Exception ex) {
	   ex.printStackTrace();
		}
	}

	/**
	 * ���ݱ��ύ���ݣ�����Dom��
	 *
	 * @param request ���ڴ����HTTP����
	 *
	 * @return Dom��
	 */
	private Document CreateXMLFile() {
		Document doc = null;
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder
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
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�����Ԫ��"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�������Ԫ��"report"
		Element report = doc.createElement("report");
		//���෴�����ȡ�������������� û�в��Թ��Ƿ����!
		Class c =  ReportVariant.class;
		Field[] field = c.getDeclaredFields();
		//Attr att = null;
		for(int i = 0 ; i< field.length ;i++){
			String attname = (field[i].toString()).substring(field[i].toString().lastIndexOf(".")+1);
			report.setAttribute(attname,"0");
		}
		//ɾ��������week������
		//report.removeAttribute("Year");
		report.removeAttribute("Month");
		report.removeAttribute("Date");
		//report.removeAttribute("Week");
		report.removeAttribute("_instance");
		//��ʼ��week report year & Week
		report.setAttribute("Year",CurrentYear);
		report.setAttribute("Week",CurrentWeek);

		//��Ԫ��������ĵ�
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
