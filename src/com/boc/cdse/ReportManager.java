package com.boc.cdse;

import com.boc.cdse.MonthReport;
import com.boc.cdse.WeekReport;
import java.io.File;

public class ReportManager {

	//��ǰ������
	//private long curReportDate;
	//�Ƿ��Ѽ��ص�ǹ�����յ�XML����

	//��ǰ������
	//private long curReportWeek;

	//��ǰ������
	//private long curReportMonth;

   private boolean hasLoadDateXML = false;
   private String  ReportDir = null;
   private DailyReport DRpt = null;
   private WeekReport WRpt = null;
   private MonthReport MRpt = null;
	//CDSResult property

	/**
   * ��ȡ���ٱ�����������Ψһʵ��
   *
   * @return ���ٱ�����������Ψһʵ��
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
		//�ӻ��������л���ļ���·��
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
