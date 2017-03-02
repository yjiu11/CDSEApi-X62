package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>LogFile������־�ļ��࣬�����Ӧ����־�ļ���Ϣ�Լ���ص����ͨ����</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class LogFile
    implements Serializable {
    /** ��־�ļ���Ӧ�����뿨���� */
    private String logTypeOfCreditCard = null;
    /** ��־�������Ϊ������������Ŀǰ��ʹ�ã� */
    private File logFile = null;
    /** ��־�ļ�����·�� */
    private String filePath = null;
    /** �ļ������ */
    private FileOutputStream out = null;
    /** ��������� */
    private BufferedOutputStream bout = null;
    /** ��ӡ����� */
    private PrintStream pout = null;
    /** ��־�ļ�����ز��� */
    private ArrayList parameters = null;

    /**
     * ��ȡ���뿨������
     *
     * @return ���뿨������
     */
    public String getLogTypeOfCreditCard() {
        return this.logTypeOfCreditCard;
    }

    /**
     * �������뿨������
     *
     * @param logTypeOfCreditCard ���뿨������
     */
    public void setLogTypeOfCreditCard(String logTypeOfCreditCard) {
        this.logTypeOfCreditCard = logTypeOfCreditCard;
    }

    /**
     * �����־�ļ�����·��
     *
     * @return ��־�ļ�����·��
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * ������־�ļ�����·��
     *
     * @param filePath ������־�ļ�����·��
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * �����־�ļ������
     *
     * @return ��־�ļ������
     */

    public FileOutputStream getOut() {
        return this.out;
    }

    /**
     * ��û��������
     *
     * @return ���������
     */

    public BufferedOutputStream getBout() {
        return this.bout;
    }

    /**
     * ��ô�ӡ�����
     *
     * @return ��ӡ�����
     */

    public PrintStream getPout() {
        return this.pout;
    }

    /**
     * �����ļ������
     *
     * @param out �ļ������
     */
    public void setOut(FileOutputStream out) {
        this.out = out;
    }

    /**
     * ���û��������
     *
     * @param bout ���������
     */
    public void setBout(BufferedOutputStream bout) {
        this.bout = bout;
    }

    /**
     * ���ô�ӡ�����
     *
     * @param pout ��ӡ�����
     */
    public void setPout(PrintStream pout) {
        this.pout = pout;
    }

    /**
     * �����־���
     *
     * @return ��־���
     */

    public File getLogFile() {
        return this.logFile;
    }

    /**
  * ������־���
  *
  * @param logFile ��־���
  */
    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    /**
     * ��ȡ��־�ļ�����ز����б�
     *
     * @return ��־�ļ�����ز����б�
     */

    public ArrayList getParameters() {
        return this.parameters;
    }
    /**
  * ������־�ļ�����ز����б�
  *
  * @param parameters ��־�ļ�����ز����б�
  */

    public void setParameters(ArrayList parameters) {
        this.parameters = parameters;
    }

    /**
     * �ͷ���־�ļ���Ӧ����������
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
