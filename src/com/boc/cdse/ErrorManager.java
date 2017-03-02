package com.boc.cdse;

import java.io.*;
import java.util.Properties;

/**
 *<p>ErrorManager类是将指定目录下的错误代码和相应的值读入系统中。
 *</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   CDSEException
 */

public class ErrorManager {
    /** 类静态变量 */
    private static ErrorManager _instance = null;
    /** 配置文件相对路径 */
    private String propertiesFileName = "com/boc/cdse/configfiles/error.properties";
    Properties prop = new Properties();
    LogManager logManager = null;
    /**
     * 取得类实例。
     *
     * @throws CDSEException 解析配置文件异常时抛出
     */
    public static ErrorManager getInstance() {
        if (_instance == null) {
            _instance = new ErrorManager();
        }
        return _instance;
    }

    /**
     * 构造函数，调用配置文件，放入系统中。
     *
     * @throws CDSEException 解析配置文件异常时抛出
     */

    private ErrorManager() {
        FileInputStream fis = null;
        logManager = LogManager.getInstance();


        try {
            prop.load(getClass().getClassLoader().
                                    getResourceAsStream(propertiesFileName));
        }
        catch (Exception ex1) {
            System.out.println("装载文件错误！");
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
