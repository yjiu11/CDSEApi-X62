package com.boc.cdse;

import com.boc.cdse.MonthReport;
import com.boc.cdse.WeekReport;
import java.io.File;

public class ReportManager {

	//当前报告日
	//private long curReportDate;
	//是否已加载当枪报告日的XML数据

	//当前报告周
	//private long curReportWeek;

	//当前报告月
	//private long curReportMonth;

   private boolean hasLoadDateXML = false;
   private String  ReportDir = null;
   private DailyReport DRpt = null;
   private WeekReport WRpt = null;
   private MonthReport MRpt = null;
	//CDSResult property

	/**
   * 获取跟踪报告管理器类的唯一实例
   *
   * @return 跟踪报告管理器类的唯一实例
   */

   static private ReportManager _instance = null;
	//private ReportVariant _Variant = new ReportVariant();
 		public static ReportManager getInstance() {
			if (_instance == null) {
				//RunTimeEnvironment en = RunTimeEnvironment.getInstance();
				_instance = new ReportManager();
			}


			return _instance;
    }
	/*
	public ReportManager() {
		super();
	}
	*/
   public synchronized void toAppTrackReport(CDSEResult cdseResult) {
	try {
		   //get report directory
		   if (ReportDir == null){
			   ReportDir = getCurrDateReportFile();
			   if (ReportDir.equals(""))
				   ReportDir = "d:/ftp/cds/reports"; //set default directory;
			   ReportDir = ReportDir + "/";

			   makeDir(ReportDir);
		   }
		   // initiance report object
		   if (!hasLoadDateXML){
			   if (DRpt == null) {
				   DRpt = new DailyReport();
				   DRpt.setReportDir(ReportDir);
			   }
			   if (WRpt == null){
				   WRpt = new WeekReport();
				   WRpt.setReportDir(ReportDir);
			   }
			   if (MRpt ==null){
				   MRpt = new MonthReport();
				   MRpt.setReportDir(ReportDir);
			   }
		   }
		   //call  report method
		   DRpt.toDaliyReport(cdseResult,hasLoadDateXML);
		   WRpt.toWeekReport(cdseResult,hasLoadDateXML);
		   MRpt.toMonthReport(cdseResult,hasLoadDateXML);

		   //set has called flag
		   hasLoadDateXML = true;

		} catch (Exception ex) {
			ex.printStackTrace();
			hasLoadDateXML = false;
		}
	}
	private String getCurrDateReportFile() {
		//从环境变量中获得文件的路径
		String ReportPhysicalFileName = null;
		RunTimeEnvironment en = RunTimeEnvironment.getInstance();
		String ReportDir = null;
		if (en == null) {
			ReportDir = "";
		} else {
			ReportDir = en.getReportDir();
		}
		return ReportDir;
	}

    private void makeDir(String Dir){
		File tmpFile = new File(Dir);
		if (!tmpFile.exists()) {
			try {
				tmpFile.mkdir();
			} catch (Exception ex) {
				 ex.printStackTrace();
			 }
		 }

    }
	//test
    /*
	public static void main(String args[]) throws Exception {
		   ReportManager _i = ReportManager.getInstance();
		   for(int I = 1 ; I<4 ;I++){
			   _i.toAppTrackReport(new CDSEResult());
		   }
  	 }
	   */

}
