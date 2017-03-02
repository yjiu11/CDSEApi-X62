package com.boc.cdse;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * LogManager��������е���־�ļ����������������ļ�������־�ļ��Լ���־�������
 * </p>
 * 
 * <p>
 * Copyright: ��Ȩ (c) 2002
 * </p>
 * <p>
 * Company: �׺�����������޹�˾
 * </p>
 * 
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class LogManager {
	/** ��־��������ʵ�� */
	static private LogManager _instance = null;
	/** ��־�ļ��б� */
	private ArrayList logFiles = new ArrayList();
	/** �ļ������ */
	private FileOutputStream out = null;
	/** ��������� */
	private BufferedOutputStream bout = null;
	/** ��ӡ����� */
	private PrintStream pout = null;
	/** ��־�ļ� */
	private File tmpFile = null;
	/** ��ǰ��־�ļ������� */
	private String currLogDate = null;

	/**
	 * ��ȡ��־���������Ψһʵ��
	 * 
	 * @return ��־���������Ψһʵ��
	 */
	public static LogManager getInstance() {
		if (_instance == null) {
			RunTimeEnvironment en = RunTimeEnvironment.getInstance();
			_instance = new LogManager();
		}
		return _instance;
	}

	/**
	 * ��־��������Ĺ��������ڹ���ʱ���������ļ�������־�ļ�
	 */
	protected LogManager() {
		getCurrDateLogFiles();
	}

	/**
	 * ���ָ�����ÿ����͵���־�ļ����
	 * 
	 * @param creditCardType
	 *            ���ÿ�����
	 * @return ��Ӧ����־�ļ����
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
	 * ����־��Ϣд����Ӧ����־�ļ���
	 * 
	 * @param log
	 *            ��־��Ϣ
	 */
	public synchronized void toLog(Log log) {
		// �ж��Ƿ���ͬһ��
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// ��ö�Ӧ��Ʒ�ߵ���־�ļ����
		LogFile currFile = getLogFile(log.getLogTypeOfCreditCard());

		ArrayList logName = log.getLogElementName();
		ArrayList logValue = log.getLogElementValue();
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// ����д�ļ�����
			// FileOutputStream out = new
			// FileOutputStream(currFile.getLogFile(), true);
			// out = new FileOutputStream(currFile.getFilePath(), true);
			// bout = new BufferedOutputStream(out);
			// pout = new PrintStream(bout);
			pout = currFile.getPout();
			if (pout != null) {
				int logLength = logName.size();
				for (int i = 0; i < logLength; i++) {
					// ���д�������
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
		// �ж��Ƿ���ͬһ��
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// ��ö�Ӧ��Ʒ�ߵ���־�ļ����
		LogFile currFile = getLogFile("CdsLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// ����д�ļ�����
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
	 * �����뱨����Ϣд����Ӧ����־�ļ���
	 * 
	 * @param error
	 *            ���뱨����Ϣ
	 */
	public synchronized void toCdsXmlLog(String cdsLog) {
		// �ж��Ƿ���ͬһ��
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// ��ö�Ӧ��Ʒ�ߵ���־�ļ����
		LogFile currFile = getLogFile("CdsXmlLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// ����д�ļ�����
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
	 * ��������־��Ϣд����Ӧ����־�ļ���
	 * 
	 * @param error
	 *            ������Ϣ
	 */
	public synchronized void toErrorLog(String error) {
		// �ж��Ƿ���ͬһ��
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// ��ö�Ӧ��Ʒ�ߵ���־�ļ����
		LogFile currFile = getLogFile("error");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// ����д�ļ�����
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
	 * ��ϵͳ��־��Ϣд����Ӧ����־�ļ���
	 * 
	 * @param error
	 *            ������Ϣ
	 */
	public synchronized void toAppLog(String appLog) {
		// �ж��Ƿ���ͬһ��
		String currDate = CDSEUtil.getCurrentDate();
		if (!currDate.equalsIgnoreCase(currLogDate)) {
			closeLogFiles();
			getCurrDateLogFiles();
		}
		// ��ö�Ӧ��Ʒ�ߵ���־�ļ����
		LogFile currFile = getLogFile("AppLog");
		String key = null;
		String value = null;
		String currTime = null;
		try {
			// ����д�ļ�����
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
	 * ����������Ϣ������־�ļ��б�ÿһ�����ÿ�����ÿ����һ����־
	 */
	private void getCurrDateLogFiles() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);// ��ȡ���
		int month = ca.get(Calendar.MONTH) + 1;// ��ȡ�·�
		int day = ca.get(Calendar.DATE);// ��ȡ��
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
		// �ӻ��������л����־�ļ���·�������ÿ�����
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
			// ���ÿ����Ʒ�ߵ���־�ļ����
			try {
				logPhysicalFileName = logDir + "/" + typeOfCreditCard[i] + "_"
						+ date + ".log";
				tmpFile = new File(logPhysicalFileName);
				//System.out.println(typeOfCreditCard[i]);
				/** ÿ��1����ɾ��һ��CdsXmlLog�ļ� */
				if ("CdsXmlLog".equalsIgnoreCase(typeOfCreditCard[i].trim())) {
					/** ���ݵ�ǰ���ڼ�ȥһ���£��ж��Ƿ���ڴ��·ݣ�����ɾ�� */
					Calendar threeMonth = Calendar.getInstance();
					threeMonth.set(Calendar.YEAR, year);
					threeMonth.set(Calendar.MONTH, month);
					threeMonth.set(Calendar.DATE, day);
					/**����ɾ����־*/
					threeMonth.add(Calendar.MONTH, -2);
					/***����ɾ����־*/
					String newthreeMonth = threeMonth.get(Calendar.YEAR) + "";
					int threeMon = threeMonth.get(Calendar.MONTH)+1;
					if(threeMon>=10){
						newthreeMonth += threeMon;
					}else{
						newthreeMonth += "0"+threeMon;
					}
					System.out.println("��1��" + newthreeMonth);
					File f = new File(logDir);
					File[] files = f.listFiles();
					for (File d : files) {
						if (d.getName().contains(typeOfCreditCard[i].trim() + "_"+ newthreeMonth)) {
							try {
								if(d.isFile()){
									if(d.delete()){
										//System.out.println("ɾ���ɹ�");
										System.out.println("ɾ���ɹ����ļ�:"+d.getName());
									}else{
										//System.out.println("ɾ��ʧ��");
										System.out.println("ʧ�ܵ��ļ���"+d.getName());
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
			// ����д�ļ�����
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
	 * ����������Ϣ������־�ļ��б�ÿһ�����ÿ�����ÿ����һ����־ �޸����ڣ�2016��4��30��
	 */
	/*
	 * private void getCurrDateLogFiles_old() { Calendar ca =
	 * Calendar.getInstance(); int year = ca.get(Calendar.YEAR);//��ȡ��� int
	 * month=ca.get(Calendar.MONTH)+1;//��ȡ�·� int day=ca.get(Calendar.DATE);//��ȡ��
	 * String date =
	 * String.valueOf(year)+String.valueOf(month)+String.valueOf(day);
	 * //�ӻ��������л����־�ļ���·�������ÿ����� String logPhysicalFileName = null;
	 * RunTimeEnvironment en = RunTimeEnvironment.getInstance(); String logDir =
	 * null; if (en == null) { logDir = ""; } else { logDir = en.getLogDir(); }
	 * logDir = checkDir(logDir); String typeOfCreditCard[] = en.getCardType();
	 * int count = typeOfCreditCard.length; currLogDate =
	 * CDSEUtil.getCurrentDate(); // System.out.println(currLogDate); for (int i
	 * = 0; i < count; i++) { //���ÿ����Ʒ�ߵ���־�ļ���� try { logPhysicalFileName =
	 * logDir + "/" + typeOfCreditCard[i] + "_" + date + ".log"; tmpFile = new
	 * File(logPhysicalFileName); if (!tmpFile.exists()) { try {
	 * tmpFile.createNewFile(); } catch (Exception e) { e.printStackTrace(); } }
	 * } catch (Exception ex) { ex.printStackTrace(); } //����д�ļ����� try { out =
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
	 * CDSE�˳�ʱ�ر����е���־�ļ�
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
