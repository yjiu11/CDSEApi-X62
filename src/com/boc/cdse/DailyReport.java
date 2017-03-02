package com.boc.cdse;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;

public class DailyReport {

	/** �ձ����ļ� */
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

			//��ǰ����
			//String CurrentDate = CDSEUtil.getCurrentDate();
			Calendar cal = Calendar.getInstance();
			//get current week
			//int aa = cal.WEEK_OF_YEAR;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			//CurrentDate = formatter.format(cal.getTime());
			String tmpDate = formatter.format(cal.getTime());
			if (! tmpDate.equals(CurrentDate)){
				CurrentDate = tmpDate;
				//��ǰ�����ձ��ļ�����������·��
				CurrentDateFileName = ReportDir + "DailyRport" + CurrentDate + ".xml";

				//����XML����
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
			//����������ձ������ȴ������ձ��ļ�,��������ձ��ļ�
			if (! RV.ExistReportFile(CurrentDateFileName)) {
				doc = CreateXMLFile();
			}else{
				doc = readXMLFile(CurrentDateFileName);
			}
		    */
		   //���ݴ���
		   doc = RV.ActionXMLData(doc,data,CurrentDateFileName);
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
			"href=\"../reports/DailyTransform.xsl\" type=\"text/xsl\"");
        doc.appendChild(pi);

		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�����Ԫ��"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�������Ԫ��"report"
		Element report = doc.createElement("report");

	    //���෴�����ȡ�������������� û�в��Թ��Ƿ����!
        Class c =  ReportVariant.class;
		Field[] field = c.getDeclaredFields();

		for(int i = 0 ; i< field.length ;i++){
			String attname = (field[i].toString()).substring(field[i].toString().lastIndexOf(".")+1);
			report.setAttribute(attname,"0");
        }
	    //ɾ���������ձ�����
		report.removeAttribute("Year");
		report.removeAttribute("Month");
		report.removeAttribute("Week");
		report.removeAttribute("_instance");
		//��ʼ���ձ�����
		report.setAttribute("Date",CurrentDate);
		//��Ԫ��������ĵ�
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
