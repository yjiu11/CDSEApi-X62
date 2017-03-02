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
	//日期
	private long Year  = 0;
	private long Month = 0;
	//private long Week;
	private long Week = 0;
	//private long Date;
	private long Date = 0;
	//金卡-批准数量 2
	private long GoldApprovedNum = 0;
	//金卡-批准的 平均金额 3
	private double GoldAvgApprovedIL = 0;
	//金卡-人工审查数量 4
	private long GoldToManCheckNum = 0;
	//金卡-策略决策拒绝数量 5
	private long GoldRejectedNum = 0;
	//金卡-入组条件不满足拒绝数量 6
	private long GoldRejectedOINum = 0;
	//银卡-批准数量 7
	private long SilverApprovedNum = 0;
	//银卡-批准平均金额 8
	private double SilverAvgApprovedIL = 0.00;
	//银卡-金卡降级批准数量 9
	private long SilverApprovedDGNum = 0;
	//银卡-金卡降级批准得平均金额 10
	private double SilverAvgApprovedDGIL = 0.00;
	//金卡-人工审查数量 11
	private long SilverToManCheckNum = 0;
	//金卡-策略拒绝数量 12
	private long SilverRejectedNum = 0;
	//金卡-入组条件不满足拒绝数量 13
	private long SilverRejectedOINum = 0;
	//金卡-批准数量 14
	private long OlympicsApprovedNum = 0;
	//金卡-批准的 平均额度 15
	private double OlympicsAvgApprovedIL = 0.00;
	//金卡-人工审查数量 16
	private long OlympicsToManCheckNum = 0;
	//金卡-策略决策拒绝数量 17
	private long OlympicsRejectedNum = 0;
	//金卡-入组条件不满足拒绝数量 18
	private long OlympicsRejectedOINum = 0;

   static private ReportVariant _instance = null;
	/*
	public ReportVariant() {
		super();
	} */
	/**
		 * 获取报表管理器类的唯一实例
		 *
		 * @return 报表管理器类的唯一实例
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
	//返回XML元素指定属性值为双精度型
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

	 //返回XML元素指定属性值为长整型
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
			 // 金卡:1002/2002/3002; 银卡:1001/2001/3001/; 奥运卡: 2o01 - 2o05/2L01
			 String goldStr = "1002,2002,3002,1R05";
			 String silverStr = "1001,2001,3001,1R06";
			 String OlyStr = "2O01,2O02,2O03,2O04,2O05,2L01,";

			 String pdCode = data.getProductCode();
			 //批准授信 A, 拒绝授信 B, 入组拒绝 C, 人工审查 D

			 String RltCode = data.getPrincipalResultID();
			 String attAvg = "";
			 String attAmt = "";

			 //降级处理
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

//批准授信 A,

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
					 //降级处理
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
//拒绝授信 B,
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
//入组拒绝 C,
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
			 //人工审查 D
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
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce); //出异常时输出异常信息，然后退出，下同
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

		//下面是解析XML的全过程，比较简单，先取根元素
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
			 //errorString = "更新配置文件错误！(Read Configure Step)";
		 }
		 return success;
	 }


	//检测当前xml文件是否已经存在
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
