package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>Log�ౣ����־��Ϣ��</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class Log
    implements Serializable {
    /** Ҫд����־���������б� */
    private ArrayList logElementName = new ArrayList();
    private ArrayList logElementValue = new ArrayList();
    /** ���뿨������ */
    private String logTypeOfCreditCard = null;

    public ArrayList getLogElementName(){
        return this.logElementName;
    }

    public ArrayList getLogElementValue(){
        return this.logElementValue;
    }

    /**
     *  ������־���ݵ��б���ȥ
     *
     * @param name  ������
     * @param value ����ֵ
     */

    public void appendParameter(String name, String value) {
        this.logElementName.add(name);
        this.logElementValue.add(value);
    }

    /** ��ȡ���뿨������
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

}
