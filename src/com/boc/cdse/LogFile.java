package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>LogFile类是日志文件类，保存对应的日志文件信息以及相关的输出通道。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class LogFile
    implements Serializable {
    /** 日志文件对应的申请卡类型 */
    private String logTypeOfCreditCard = null;
    /** 日志句柄（因为构造器的问题目前不使用） */
    private File logFile = null;
    /** 日志文件完整路径 */
    private String filePath = null;
    /** 文件输出流 */
    private FileOutputStream out = null;
    /** 缓冲输出流 */
    private BufferedOutputStream bout = null;
    /** 打印输出流 */
    private PrintStream pout = null;
    /** 日志文件的相关参数 */
    private ArrayList parameters = null;

    /**
     * 获取申请卡的类型
     *
     * @return 申请卡的类型
     */
    public String getLogTypeOfCreditCard() {
        return this.logTypeOfCreditCard;
    }

    /**
     * 设置申请卡的类型
     *
     * @param logTypeOfCreditCard 申请卡的类型
     */
    public void setLogTypeOfCreditCard(String logTypeOfCreditCard) {
        this.logTypeOfCreditCard = logTypeOfCreditCard;
    }

    /**
     * 获得日志文件完整路径
     *
     * @return 日志文件完整路径
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * 设置日志文件完整路径
     *
     * @param filePath 设置日志文件完整路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获得日志文件输出流
     *
     * @return 日志文件输出流
     */

    public FileOutputStream getOut() {
        return this.out;
    }

    /**
     * 获得缓冲输出流
     *
     * @return 缓冲输出流
     */

    public BufferedOutputStream getBout() {
        return this.bout;
    }

    /**
     * 获得打印输出流
     *
     * @return 打印输出流
     */

    public PrintStream getPout() {
        return this.pout;
    }

    /**
     * 设置文件输出流
     *
     * @param out 文件输出流
     */
    public void setOut(FileOutputStream out) {
        this.out = out;
    }

    /**
     * 设置缓冲输出流
     *
     * @param bout 缓冲输出流
     */
    public void setBout(BufferedOutputStream bout) {
        this.bout = bout;
    }

    /**
     * 设置打印输出流
     *
     * @param pout 打印输出流
     */
    public void setPout(PrintStream pout) {
        this.pout = pout;
    }

    /**
     * 获得日志句柄
     *
     * @return 日志句柄
     */

    public File getLogFile() {
        return this.logFile;
    }

    /**
  * 设置日志句柄
  *
  * @param logFile 日志句柄
  */
    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    /**
     * 获取日志文件的相关参数列表
     *
     * @return 日志文件的相关参数列表
     */

    public ArrayList getParameters() {
        return this.parameters;
    }
    /**
  * 设置日志文件的相关参数列表
  *
  * @param parameters 日志文件的相关参数列表
  */

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    /**
     * 释放日志文件对应的相关输出流
     */

    public void reset() {
        try {
            this.pout.close();
            this.bout.close();
            this.out.close();
        }
        catch (Exception e) {
            System.out.println("Error in close File:" + e.getMessage());
        }
       // this.pout = null; // for no print in another day;
        this.bout = null;
        this.out = null;
        this.logFile = null;
    }

}
