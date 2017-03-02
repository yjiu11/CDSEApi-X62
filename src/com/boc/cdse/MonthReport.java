package com.boc.cdse;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.lang.reflect.Field;

public class MonthReport {

	/** �ձ����ļ� */
	ReportVariant RV = null;
	Document doc = null;
	//private String lastweekdate;

	private String CurrentMonth ;
    private String CurrentYear;


	// by ReportManager set
	private String ReportDir;// = "d:/ftp/cds/reports/";
	private String CurrentFileName;

	public void toMonthReport(CDSEResult data,boolean hasLoadDateXML)  {
	try{
		//��ǰ����

		//Calendar c = new GregorianCalendar();
		Calendar c = Calendar.getInstance();
		int Y = c.get(c.YEAR);
		//int M = c.get(c.MONTH);
		int M = c.get(Calendar.MONTH) + 1;

		String tmpMonth = Integer.toString(M);
		String tmpYear = Integer.toString(Y);

		if (!tmpYear.equals(CurrentYear) || !tmpMonth.equals(CurrentMonth)){
			CurrentYear = tmpYear;
			CurrentMonth = tmpMonth;
			//��ǰ�����ļ�����������·��
			CurrentFileName = ReportDir + "MonthlyReport" + CurrentYear + "-" + CurrentMonth + ".xml";
			//XML����
			//Document doc = null;

			//����XML����
			if (RV == null)
				RV = new ReportVariant();

			if (! RV.ExistReportFile(CurrentFileName)){
				doc = CreateXMLFile();
			}
			else{
				doc = RV.readXMLFile(CurrentFileName);
				}
/*
			//���������month������������µ�һmonth�����ȴ������ܱ��ļ�,�������month���ļ�
			if (!RV.ExistReportFile(CurrentFileName)) {
				doc = CreateXMLFile();
			} else {
				doc = readXMLFile(CurrentFileName);
			}*/
		}else
		if (! hasLoadDateXML)
			doc = RV.readXMLFile(CurrentFileName);
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
			"href=\"../reports/MonthlyTransform.xsl\" type=\"text/xsl\"");
        doc.appendChild(pi);
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�����Ԫ��"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�������Ԫ��"report"
		Element report = doc.createElement("report");
		//���෴�����ȡ�������������� û�в��Թ��Ƿ����!
		Class c =  ReportVariant.class;
		Field[] field = c.getDeclaredFields();
		Attr att = null;
		for(int i = 0 ; i< field.length ;i++){
			String attname = (field[i].toString()).substring(field[i].toString().lastIndexOf(".")+1);
			report.setAttribute(attname,"0");
		}

		//ɾ��������month������
		report.removeAttribute("Date");
		report.removeAttribute("Week");
		report.removeAttribute("_instance");
		//��ʼ��month report year & Week
		report.setAttribute("Year",CurrentYear);
		report.setAttribute("Month",CurrentMonth);

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
