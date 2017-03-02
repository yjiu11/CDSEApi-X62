package com.boc.cdse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * LogManager类管理所有的日志文件，包含根据配置文件建立日志文件以及日志的输出。
 * </p>
 * 
 * <p>
 * Copyright: 版权 (c) 2002
 * </p>
 * <p>
 * Company: 首航财务管理有限公司
 * </p>
 * 
 * @author: CDSE项目组
 * @version 1.0
 */

public class LogManager {
	/** 日志管理器的实例 */
	static private LogManager _instance = null;
	/** 日志文件列表 */
	private ArrayList logFiles = new ArrayList();
	/** 文件输出流 */
	private FileOutputStream out = null;
	/** 缓冲输出流 */
	private BufferedOutputStream bout = null;
	/** 打印输出流 */
	private PrintStream pout = null;
	/** 日志文件 */
	private File tmpFile = null;
	/** 当前日志文件的日期 */
	private String currLogDate = null;

	/**
	 * 获取日志管理器类的唯一实例
	 * 
	 * @return 日志管理器类的唯一实例
	 */
	public static LogManager getInstance() {
		if (_instance == null) {
			RunTimeEnvironment en = RunTimeEnvironment.getInstance();
			_instance = new LogManager();
		}
		return _instance;
	}

	/**
	 * 日志管理器类的构造器，在构造时根据配置文件建立日志文件
	 */
	protected LogManager() {
		getCurrDateLogFiles();
	}

	/**
	 * 获得指定信用卡类型的日志文件句柄
	 * 
	 * @param creditCardType
	 *            信用卡类型
	 * @return 对应的日志文件句柄
	 */
	private LogFile getLogFile(String creditCardType) {
		if (!logFiles.isEmpty()) {
			for (Iterator i = logFiles.iterator(); i.hasNext();) {
				LogFile currFile = (LogFile) i.next();
				if (currFile.getLogTypeOfCreditCard().equalsIgnoreCase(
						creditCardType.trim())) {
					return currFile;
				}
			}
		}
		return null;
	}

	/**
	 * 将日志信息写到相应的日志文件中
	 * 
	 * @param log
	 *            日志信息
	 */
	public synchronized void toLog(Log log) {
		// 判断是否是同一天
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// 获得对应产品线的日志文件句柄
		LogFile currFile = getLogFile(log.getLogTypeOfCreditCard());

		ArrayList logName = log.getLogElementName();
		ArrayList logValue = log.getLogElementValue();
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// 创建写文件的流
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			if (pout != null) {
				int logLength = logName.size();
				for (int i = 0; i < logLength; i++) {
					// 获得写入的数据
					// System.out.println("&&&&&&&&&PPPPPPPPPPPPPPPPPPP");
					key = (String) logName.get(i);
					value = (String) logValue.get(i);
					pout.print(key + "=" + value + "|");
					// pout.print(value + "|");
					pout.flush();
					bout.flush();
					out.flush();

				}
				pout.println("\n");

				pout.flush();

			}
			// pout.close();
			// bout.close();
			// out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void toCdsLog(String cdsLog) {
		// 判断是否是同一天
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// 获得对应产品线的日志文件句柄
		LogFile currFile = getLogFile("CdsLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// 创建写文件的流
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			currTime = CDSEUtil.getCurrentTime();

			pout.print("\n" + currTime + "=> " + cdsLog); // System.out.println(currTime
															// + "=> "+appLog);

			pout.flush();
			bout.flush();
			out.flush();
			// pout.close();
			// bout.close();
			// out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将申请报文信息写到相应的日志文件中
	 * 
	 * @param error
	 *            申请报文信息
	 */
	public synchronized void toCdsXmlLog(String cdsLog) {
		// 判断是否是同一天
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// 获得对应产品线的日志文件句柄
		LogFile currFile = getLogFile("CdsXmlLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// 创建写文件的流
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			//currTime = CDSEUtil.getCurrentTime();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");
			currTime = sf.format(new Date());
			pout.print("\n" + currTime + "=> " + cdsLog); // System.out.println(currTime
															// + "=> "+appLog);

			pout.flush();
			bout.flush();
			out.flush();
			// pout.close();
			// bout.close();
			// out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将错误日志信息写到相应的日志文件中
	 * 
	 * @param error
	 *            错误信息
	 */
	public synchronized void toErrorLog(String error) {
		// 判断是否是同一天
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// 获得对应产品线的日志文件句柄
		LogFile currFile = getLogFile("error");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// 创建写文件的流
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			currTime = CDSEUtil.getCurrentTime();
			pout.print("\n" + currTime + "=> " + error);
			pout.flush();
			bout.flush();
			out.flush();
			// pout.close();
			// bout.close();
			// out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将系统日志信息写到相应的日志文件中
	 * 
	 * @param error
	 *            错误信息
	 */
	public synchronized void toAppLog(String appLog) {
		// 判断是否是同一天
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// 获得对应产品线的日志文件句柄
		LogFile currFile = getLogFile("AppLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// 创建写文件的流
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			currTime = CDSEUtil.getCurrentTime();

			pout.print("\n" + currTime + "=> " + appLog);
			// System.out.println(currTime + "=> "+appLog);

			pout.flush();
			bout.flush();
			out.flush();
			// pout.close();
			// bout.close();
			// out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据配置信息产生日志文件列表，每一种信用卡类型每天有一个日志
	 */
	private void getCurrDateLogFiles() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);// 获取年份
		int month = ca.get(Calendar.MONTH) + 1;// 获取月份
		int day = ca.get(Calendar.DATE);// 获取日
		String date = "";
		date+=String.valueOf(year);
		if(month>=10){
			date+=String.valueOf(month);
		}else{
			date+="0"+String.valueOf(month);
		}
		if(day>=10){
			date+=String.valueOf(day);
		}else{
			date+="0"+String.valueOf(day);
		}
		//String date = String.valueOf(year) + String.valueOf(month)+ String.valueOf(day);
		// System.out.println("date:"+date);
		// 从环境变量中获得日志文件的路径和信用卡类型
		String logPhysicalFileName = null;
		RunTimeEnvironment en = RunTimeEnvironment.getInstance();
		String logDir = null;
		if (en == null) {
			logDir = "";
		} else {
			logDir = en.getLogDir();
		}
		logDir = checkDir(logDir);
		String typeOfCreditCard[] = en.getCardType();
		int count = typeOfCreditCard.length;
		currLogDate = CDSEUtil.getCurrentDate();
		// System.out.println("currLogDate:"+currLogDate);
		for (int i = 0; i < count; i++) {
			// 获得每个产品线的日志文件句柄
			try {
				logPhysicalFileName = logDir + "/" + typeOfCreditCard[i] + "_"
						+ date + ".log";
				tmpFile = new File(logPhysicalFileName);
				//System.out.println(typeOfCreditCard[i]);
				/** 每隔1个月删除一次CdsXmlLog文件 */
				if ("CdsXmlLog".equalsIgnoreCase(typeOfCreditCard[i].trim())) {
					/** 根据当前日期减去一个月，判断是否存在此月份，有则删除 */
					Calendar threeMonth = Calendar.getInstance();
					threeMonth.set(Calendar.YEAR, year);
					threeMonth.set(Calendar.MONTH, month);
					threeMonth.set(Calendar.DATE, day);
					/**按月删除日志*/
					threeMonth.add(Calendar.MONTH, -2);
					/***按天删除日志*/
					String newthreeMonth = threeMonth.get(Calendar.YEAR) + "";
					int threeMon = threeMonth.get(Calendar.MONTH)+1;
					if(threeMon>=10){
						newthreeMonth += threeMon;
					}else{
						newthreeMonth += "0"+threeMon;
					}
					System.out.println("减1后：" + newthreeMonth);
					File f = new File(logDir);
					File[] files = f.listFiles();
					for (File d : files) {
						if (d.getName().contains(typeOfCreditCard[i].trim() + "_"+ newthreeMonth)) {
							try {
								if(d.isFile()){
									if(d.delete()){
										//System.out.println("删除成功");
										System.out.println("删除成功的文件:"+d.getName());
									}else{
										//System.out.println("删除失败");
										System.out.println("失败的文件："+d.getName());
									}
								}
							} catch (Exception e) {

							}

						}
					}
				}
				if (!tmpFile.exists()) {
					try {
						tmpFile.createNewFile();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// 创建写文件的流
			try {
				out = new FileOutputStream(logPhysicalFileName, true);
				bout = new BufferedOutputStream(out);
				pout = new PrintStream(bout);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			LogFile logFile = new LogFile();
			logFile.setLogTypeOfCreditCard(typeOfCreditCard[i]);
			logFile.setLogFile(tmpFile);
			logFile.setFilePath(logPhysicalFileName);
			logFile.setOut(out);
			logFile.setBout(bout);
			logFile.setPout(pout);
			logFiles.add(logFile);
		}
	}
	/**
	 * 根据配置信息产生日志文件列表，每一种信用卡类型每天有一个日志 修改日期：2016年4月30日
	 */
	/*
	 * private void getCurrDateLogFiles_old() { Calendar ca =
	 * Calendar.getInstance(); int year = ca.get(Calendar.YEAR);//获取年份 int
	 * month=ca.get(Calendar.MONTH)+1;//获取月份 int day=ca.get(Calendar.DATE);//获取日
	 * String date =
	 * String.valueOf(year)+String.valueOf(month)+String.valueOf(day);
	 * //从环境变量中获得日志文件的路径和信用卡类型 String logPhysicalFileName = null;
	 * RunTimeEnvironment en = RunTimeEnvironment.getInstance(); String logDir =
	 * null; if (en == null) { logDir = ""; } else { logDir = en.getLogDir(); }
	 * logDir = checkDir(logDir); String typeOfCreditCard[] = en.getCardType();
	 * int count = typeOfCreditCard.length; currLogDate =
	 * CDSEUtil.getCurrentDate(); // System.out.println(currLogDate); for (int i
	 * = 0; i < count; i++) { //获得每个产品线的日志文件句柄 try { logPhysicalFileName =
	 * logDir + "/" + typeOfCreditCard[i] + "_" + date + ".log"; tmpFile = new
	 * File(logPhysicalFileName); if (!tmpFile.exists()) { try {
	 * tmpFile.createNewFile(); } catch (Exception e) { e.printStackTrace(); } }
	 * } catch (Exception ex) { ex.printStackTrace(); } //创建写文件的流 try { out =
	 * new FileOutputStream(logPhysicalFileName, true); bout = new
	 * BufferedOutputStream(out); pout = new PrintStream(bout); } catch
	 * (FileNotFoundException ex) { ex.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); } LogFile logFile = new LogFile();
	 * logFile.setLogTypeOfCreditCard(typeOfCreditCard[i]);
	 * logFile.setLogFile(tmpFile); logFile.setFilePath(logPhysicalFileName);
	 * logFile.setOut(out); logFile.setBout(bout); logFile.setPout(pout);
	 * logFiles.add(logFile); } }
	 */

	/**
	 * CDSE退出时关闭所有的日志文件
	 */
	private void closeLogFiles() {
		int count = logFiles.size();
		for (int i = 0; i < count; i++) {
			LogFile tmpFile = (LogFile) logFiles.get(i);
			tmpFile.reset();
		}
		logFiles = null;
		logFiles = new java.util.ArrayList();
	}

	private String checkDir(String dir) {
		String correctDir = null;
		if (dir == null || dir.trim().length() == 0) {
			correctDir = "c:/boccdse/log";
		} else {
			correctDir = dir;
		}
		File logPath = new File(correctDir);
		if (!(logPath.exists())) {
			logPath.mkdirs();
			if (!(logPath.exists())) {
				correctDir = "c:/boccdse/log";
				logPath = new File(correctDir);
				logPath.mkdirs();
			}
		}
		return correctDir;
	}
}
