package com.boc.cdse;

import java.text.NumberFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.File;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import java.lang.Math;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.DOMException;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

//import java.io.*;


public class ReportVariant {
	//����
	private long Year  = 0;
	private long Month = 0;
	//private long Week;
	private long Week = 0;
	//private long Date;
	private long Date = 0;
	//��-��׼���� 2
	private long GoldApprovedNum = 0;
	//��-��׼�� ƽ����� 3
	private double GoldAvgApprovedIL = 0;
	//��-�˹�������� 4
	private long GoldToManCheckNum = 0;
	//��-���Ծ��߾ܾ����� 5
	private long GoldRejectedNum = 0;
	//��-��������������ܾ����� 6
	private long GoldRejectedOINum = 0;
	//����-��׼���� 7
	private long SilverApprovedNum = 0;
	//����-��׼ƽ����� 8
	private double SilverAvgApprovedIL = 0.00;
	//����-�𿨽�����׼���� 9
	private long SilverApprovedDGNum = 0;
	//����-�𿨽�����׼��ƽ����� 10
	private double SilverAvgApprovedDGIL = 0.00;
	//��-�˹�������� 11
	private long SilverToManCheckNum = 0;
	//��-���Ծܾ����� 12
	private long SilverRejectedNum = 0;
	//��-��������������ܾ����� 13
	private long SilverRejectedOINum = 0;
	//��-��׼���� 14
	private long OlympicsApprovedNum = 0;
	//��-��׼�� ƽ����� 15
	private double OlympicsAvgApprovedIL = 0.00;
	//��-�˹�������� 16
	private long OlympicsToManCheckNum = 0;
	//��-���Ծ��߾ܾ����� 17
	private long OlympicsRejectedNum = 0;
	//��-��������������ܾ����� 18
	private long OlympicsRejectedOINum = 0;

   static private ReportVariant _instance = null;
	/*
	public ReportVariant() {
		super();
	} */
	/**
		 * ��ȡ������������Ψһʵ��
		 *
		 * @return ������������Ψһʵ��
		 */
		public static ReportVariant getInstance() {
			if (_instance == null) {
				//RunTimeEnvironment en = RunTimeEnvironment.getInstance();
				_instance = new ReportVariant();
			}
			return _instance;
		}

	//format float number
	public String FormatFloat(double dblNum){
		try {
			NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);
			return usFormat.format(Math.round(dblNum));
			//return Long.toString(Math.round(dblNum));
			/*
			   NumberFormat nf = NumberFormat.getInstance();
			   nf.setMinimumFractionDigits(4);
			   return (nf).format(dblNum);
			 */
		}catch (Exception ex) {
				 ex.printStackTrace();
				 return "0";
		}
	}
    ////////////////////////////
	//����XMLԪ��ָ������ֵΪ˫������
	 private double getElementDoubleData(Element Em,String attname){
		 try {
			 if (! (Em.getAttribute(attname).trim().equals(""))) {
				 return (new Double(Em.getAttribute(attname).replaceAll(",", "")).doubleValue());
			 } else {
				 //System.out.println(Em.getAttribute(attname));
				 return 0;
			 }
		 } catch (Exception ex) {
			 ex.printStackTrace();
			 return 0.0;
		 }
	}

	 //����XMLԪ��ָ������ֵΪ������
	 private long getElementLongData(Element Em,String attname){
		 try{
			 if (! (Em.getAttribute(attname).trim().equals(""))) {
				 return (new Long(Em.getAttribute(attname)).longValue());
			 } else {
				 System.out.println(Em.getAttribute(attname));
				 return 0;
			 }
		 }catch (Exception ex) {
			 ex.printStackTrace();
			 return 0;
		 }

	 }

	 //return average of approved
	 private long getAmount(Element report,String card){
		 try{
			 return getElementLongData(report, card) + 1;
		 }catch (Exception ex) {
			 ex.printStackTrace();
			 return 0;
		 }

	 }

	 //return amount of people
	 private double getAverage(Element report,String card,long amount,double RMB){
		 try{
			 return (getElementDoubleData(report, card) * (amount - 1) + RMB) / amount;
		 }	 catch (Exception ex) {
			 ex.printStackTrace();
			 return 0.0;
		 }

		 }

	 public Document ActionXMLData(Document doc, CDSEResult data,String CurrentFileName){
		 try{
			 // ��:1002/2002/3002; ����:1001/2001/3001/; ���˿�: 2o01 - 2o05/2L01
			 String goldStr = "1002,2002,3002,1R05";
			 String silverStr = "1001,2001,3001,1R06";
			 String OlyStr = "2O01,2O02,2O03,2O04,2O05,2L01,";

			 String pdCode = data.getProductCode();
			 //��׼���� A, �ܾ����� B, ����ܾ� C, �˹���� D

			 String RltCode = data.getPrincipalResultID();
			 String attAvg = "";
			 String attAmt = "";

			 //��������
			 int downsell = data.getHasDownSell();
			 //int RMB = data.getPrincipalCashAdvanceRMB();
			 int RMB = data.getPrincipalInitialLineRMB();

			 double average = 0;
			 long amount = 0;

			 // test  debug

			 /*
				 downsell = 1;
				 RltCode = "A";
				 pdCode = "1001";
				 RMB = 500;
			*/
			 Element report = (Element) doc.getDocumentElement().getElementsByTagName("report").item(0);

//��׼���� A,

			 if ( (RltCode.equals("A"))) {
				 //Gold Card
				 if (goldStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "GoldApprovedNum";
					 attAvg = "GoldAvgApprovedIL";

					 amount = getAmount(report, attAmt);
					 average = getAverage(report, attAvg, amount, RMB);
				 }
				 //Silver Card
				 if (silverStr.lastIndexOf(pdCode) != -1) {
					 //��������
					 if (downsell == 1) {
						 attAmt = "SilverApprovedDGNum";
						 attAvg = "SilverAvgApprovedDGIL";
						 amount = getAmount(report, attAmt);
						 average = getAverage(report, attAvg, amount, RMB);
					 } else {
						 attAmt = "SilverApprovedNum";
						 attAvg = "SilverAvgApprovedIL";

						 amount = getAmount(report, attAmt);
						 average = getAverage(report, attAvg, amount, RMB);
					 }
				 }
				 //Olympics Card
				 if (OlyStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "OlympicsApprovedNum";
					 attAvg = "OlympicsAvgApprovedIL";
					 amount = getAmount(report, attAmt);
					 average = getAverage(report, attAvg, amount, RMB);
				 }
			 }
//�ܾ����� B,
			 if ( (RltCode.equals("B"))) {
				 //Gold Card
				 if (goldStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "GoldRejectedNum";
					 amount = getAmount(report, attAmt);
				 }
				 //Silver Card
				 if (silverStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "SilverRejectedNum";
					 amount = getAmount(report, attAmt);
				 }
				 //Olympics Card
				 if (OlyStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "OlympicsRejectedNum";
					 amount = getAmount(report, attAmt);
				 }
			 }
//����ܾ� C,
			 if ( (RltCode.equals("C"))) {
				 //Gold Card
				 if (goldStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "GoldRejectedOINum";
					 amount = getAmount(report, attAmt);
				 }
				 //Silver Card
				 if (silverStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "SilverRejectedOINum";
					 amount = getAmount(report, attAmt);
				 }
				 //Olympics Card
				 if (OlyStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "OlympicsRejectedOINum";
					 amount = getAmount(report, attAmt);
				 }

			 }
			 //�˹���� D
			 if ( (RltCode.equals("D"))) {
				 //Gold Card
				 if (goldStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "GoldToManCheckNum";
					 amount = getAmount(report, attAmt);
				 }
				 //Silver Card
				 if (silverStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "SilverToManCheckNum";
					 amount = getAmount(report, attAmt);
				 }
				 //Olympics Card
				 if (OlyStr.lastIndexOf(pdCode) != -1) {
					 attAmt = "OlympicsToManCheckNum";
					 amount = getAmount(report, attAmt);
				 }
			 }
			 //reWrite to XML File

			 /*attAvg = "SilverToManCheckNum"; test  --ok
				 average  = 888.88;
			  */
			 if (! (attAvg.equals(""))) {
				 report.setAttribute(attAvg, FormatFloat(average));
			 }
			 /* test --ok
				 attAmt = "OlympicsToManCheckNum";
				 amount = 222;
			  */
			 if (! (attAmt.equals(""))) {
				 report.setAttribute(attAmt, Long.toString(amount));
			 }
			 //save to disk
			 outputFile(doc, CurrentFileName);
			 //return doc;
		 }catch (Exception ex) {
			 ex.printStackTrace();
		 }
		 return doc;
	 }

	public Document readXMLFile(String inFile)  {
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce); //���쳣ʱ����쳣��Ϣ��Ȼ���˳�����ͬ
			//System.exit(1);
		}
		Document doc = null;
		try {
			doc = db.parse(inFile);
		} catch (DOMException dom) {
			System.err.println(dom.getMessage());
			//System.exit(1);
		} catch (IOException ioe) {
			System.err.println(ioe);
			//System.exit(1);
		} catch (SAXException ex) {
			ex.printStackTrace();
			/** @todo Handle this exception */
		}

		//�����ǽ���XML��ȫ���̣��Ƚϼ򵥣���ȡ��Ԫ��
		//Element root = doc.getDocumentElement();
		return doc;
	}

	 public int outputFile(Document doc,String CurrentFileName) {
		 int success = 0;
		 TransformerFactory tFactory = TransformerFactory.newInstance();
		 Transformer transformer = null;
		 try {
			 transformer = tFactory.newTransformer();
			 DOMSource source = null;
			 source = new DOMSource(doc);
			 StreamResult result = null;
			 result = new StreamResult(new File(CurrentFileName));
			 transformer.transform(source, result);
		 }
		 catch (TransformerException ex1) {
			 success = -1;
			 //errorString = "���������ļ�����(Read Configure Step)";
		 }
		 return success;
	 }


	//��⵱ǰxml�ļ��Ƿ��Ѿ�����
	public boolean ExistReportFile(String filename){
		boolean bool = false;
		File tmpFile = null;
		try{
			tmpFile = new File(filename);
			if(tmpFile.exists()){
				bool =  true;
			}else{
				bool =  false ;
				}

		}catch(Exception e){
			e.printStackTrace();
			}
	  tmpFile = null;
	  return bool;

	}

	////////////////////////////


	public void setYear(long Year) {
		this.Year = Year;
	}

	public long getYear() {
		return Year;
	}

	public void setGoldApprovedNum(long GoldApprovedNum) {
		this.GoldApprovedNum = GoldApprovedNum;
	}

	public long getGoldApprovedNum() {
		return GoldApprovedNum;
	}

	public void setMonth(long Month) {
		this.Month = Month;
	}

	public long getMonth() {
		return Month;
	}

	public void setWeek(long Week) {
		this.Week = Week;
	}

	public long getWeek() {
		return Week;
	}

	public void setDate(long Date) {
		this.Date = Date;
	}

	public long getDate() {
		return Date;
	}

	public void setGoldAvgApprovedIL(double GoldAvgApprovedIL) {
		this.GoldAvgApprovedIL = GoldAvgApprovedIL;
	}

	public double getGoldAvgApprovedIL() {
		return GoldAvgApprovedIL;
	}

	public void setGoldToManCheckNum(long GoldToManCheckNum) {
		this.GoldToManCheckNum = GoldToManCheckNum;
	}

	public long getGoldToManCheckNum() {
		return GoldToManCheckNum;
	}

	public void setGoldRejectedNum(long GoldRejectedNum) {
		this.GoldRejectedNum = GoldRejectedNum;
	}

	public long getGoldRejectedNum() {
		return GoldRejectedNum;
	}

	public void setGoldRejectedOINum(long GoldRejectedOINum) {
		this.GoldRejectedOINum = GoldRejectedOINum;
	}

	public long getGoldRejectedOINum() {
		return GoldRejectedOINum;
	}

	public void setSilverApprovedNum(long SilverApprovedNum) {
		this.SilverApprovedNum = SilverApprovedNum;
	}

	public long getSilverApprovedNum() {
		return SilverApprovedNum;
	}

	public void setSilverAvgApprovedIL(double SilverAvgApprovedIL) {
		this.SilverAvgApprovedIL = SilverAvgApprovedIL;
	}

	public double getSilverAvgApprovedIL() {
		return SilverAvgApprovedIL;
	}

	public void setSilverApprovedDGNum(long SilverApprovedDGNum) {
		this.SilverApprovedDGNum = SilverApprovedDGNum;
	}

	public long getSilverApprovedDGNum() {
		return SilverApprovedDGNum;
	}

	public void setSilverAvgApprovedDGIL(double SilverAvgApprovedDGIL) {
		this.SilverAvgApprovedDGIL = SilverAvgApprovedDGIL;
	}

	public double getSilverAvgApprovedDGIL() {
		return SilverAvgApprovedDGIL;
	}

	public void setSilverToManCheckNum(long SilverToManCheckNum) {
		this.SilverToManCheckNum = SilverToManCheckNum;
	}

	public long getSilverToManCheckNum() {
		return SilverToManCheckNum;
	}

	public void setSilverRejectedNum(long SilverRejectedNum) {
		this.SilverRejectedNum = SilverRejectedNum;
	}

	public long getSilverRejectedNum() {
		return SilverRejectedNum;
	}

	public void setSilverRejectedOINum(long SilverRejectedOINum) {
		this.SilverRejectedOINum = SilverRejectedOINum;
	}

	public long getSilverRejectedOINum() {
		return SilverRejectedOINum;
	}

	public void setOlympicsApprovedNum(long OlympicsApprovedNum) {
		this.OlympicsApprovedNum = OlympicsApprovedNum;
	}

	public long getOlympicsApprovedNum() {
		return OlympicsApprovedNum;
	}

	public void setOlympicsAvgApprovedIL(double OlympicsAvgApprovedIL) {
		this.OlympicsAvgApprovedIL = OlympicsAvgApprovedIL;
	}

	public double getOlympicsAvgApprovedIL() {
		return OlympicsAvgApprovedIL;
	}

	public void setOlympicsToManCheckNum(long OlympicsToManCheckNum) {
		this.OlympicsToManCheckNum = OlympicsToManCheckNum;
	}

	public long getOlympicsToManCheckNum() {
		return OlympicsToManCheckNum;
	}

	public void setOlympicsRejectedNum(long OlympicsRejectedNum) {
		this.OlympicsRejectedNum = OlympicsRejectedNum;
	}

	public long getOlympicsRejectedNum() {
		return OlympicsRejectedNum;
	}

	public void setOlympicsRejectedOINum(long OlympicsRejectedOINum) {
		this.OlympicsRejectedOINum = OlympicsRejectedOINum;
	}

	public long getOlympicsRejectedOINum() {
		return OlympicsRejectedOINum;
	}
}
