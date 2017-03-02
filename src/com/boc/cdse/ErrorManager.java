package com.boc.cdse;

import java.io.*;
import java.util.Properties;

/**
 *<p>ErrorManager���ǽ�ָ��Ŀ¼�µĴ���������Ӧ��ֵ����ϵͳ�С�
 *</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   CDSEException
 */

public class ErrorManager {
    /** �ྲ̬���� */
    private static ErrorManager _instance = null;
    /** �����ļ����·�� */
    private String propertiesFileName = "com/boc/cdse/configfiles/error.properties";
    Properties prop = new Properties();
    LogManager logManager = null;
    /**
     * ȡ����ʵ����
     *
     * @throws CDSEException ���������ļ��쳣ʱ�׳�
     */
    public static ErrorManager getInstance() {
        if (_instance == null) {
            _instance = new ErrorManager();
        }
        return _instance;
    }

    /**
     * ���캯�������������ļ�������ϵͳ�С�
     *
     * @throws CDSEException ���������ļ��쳣ʱ�׳�
     */

    private ErrorManager() {
        FileInputStream fis = null;
        logManager = LogManager.getInstance();


        try {
            prop.load(getClass().getClassLoader().
                                    getResourceAsStream(propertiesFileName));
        }
        catch (Exception ex1) {
            System.out.println("װ���ļ�����");
        }

    }

    public String getSystemError(int errorCode) {
          String description = null;
          String errorCodeStr = null;
          errorCodeStr = String.valueOf(errorCode);
          if (prop.containsKey(errorCodeStr)) {
              description = prop.getProperty(errorCodeStr);
              return CDSEUtil.getChineseStr(description);
          }
          else {
              return "";
          }

      }

    public String getSystemError(CDSEException ex) {
        String description = null;
        String errorCode = null;
        errorCode = String.valueOf(ex.getErrorCode());
        if (prop.containsKey(errorCode)) {
            description = prop.getProperty(errorCode);
            return CDSEUtil.getChineseStr(description);
        }
        else {
            return "";
        }

    }

    public void toAppErrorLog(String appId, CDSEException ex) {

        String description = new String();
        description = getSystemError(ex);
        if (logManager == null) {
            System.out.println(CDSEUtil.getCurrentTime() + " " + appId + " " +
                               description);
        }
        else {
            logManager.toErrorLog(description);
        }

    }

    public void toSystemErrorLog(CDSEException ex) {

        String description = new String();
        description = getSystemError(ex);
        if (logManager == null) {
            System.out.println(CDSEUtil.getCurrentTime() + "  " + description);
        }
        else {
            logManager.toErrorLog((description));
        }

    }

    public void toSystemErrorLog(String error) {

        if (logManager == null) {
            System.out.println(CDSEUtil.getCurrentTime() + "  " + error);
        }
        else {
            logManager.toErrorLog(error);
        }

    }

}
