package com.boc.cdse;

import java.io.Serializable;
import java.util.*;
import org.w3c.dom.Element;

/**
 * <P><Code>ActionResult</Code>��Ϊ���վ��߽ӿڷ��صĲ��Խ������ͨ�������Ե�set�����������
 * ���ߺͲ��Եı�ţ��������汾�ŵ���Ϣ�����صľ��߽���Լ����߹�����ʹ�õ����м������������ȡֵ��ͨ�������Ե�
 * get������ȡ��Ϣ��
 *
 * <P>Copyright:��Ȩ (c) 2003 </P>
 * <P>Company:�׺�����������޹�˾</P>
 * @author:
 * @version 1.0
 */

public final class ActionResult
    implements Serializable {

    public ActionResult() {
    }

    private String policeNo = " ";
    private String policeNa = " ";
    private String strategyNo = " ";
    private String strategyNa = " ";
    private String strategyVer = " ";
    private String actionId = " ";
    private String actionNa = " ";
    private String attributesString = " ";
    private String reasonCode=" ";
    private String reasonDescription=" ";
    private String warnDescStr="";
    private String initialLine="0";
    private Element allAttributes;
	private ArrayList allAttributesA;
    private int badNum;          //�����߼��ģ��������ı����ĸ���
    private String badString;    //�����߼��Ļ������ı���������ִ�����|��ָ�

    private ArrayList paraList = new ArrayList(); //�����б�

    public String getPoliceNo() {
        return this.policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }

    public String getPoliceNa() {
        return this.policeNa;
    }

    public void setPoliceNa(String policeNa) {
        this.policeNa = policeNa;
    }

    public String getStrategyNo() {
        return this.strategyNo;
    }

    public void setStrategyNo(String strategyNo) {
        this.strategyNo = strategyNo;
    }

    public String getStrategyNa() {
        return this.strategyNa;
    }

    public void setStrategyNa(String strategyNa) {
        this.strategyNa = strategyNa;
    }

    public String getStrategyVer() {
        return this.strategyVer;
    }

    public void setStrategyVer(String strategyVer) {
        this.strategyVer = strategyVer;
    }

    public String getActionId() {
        return this.actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionNa() {
        return this.actionNa;
    }

    public void setActionNa(String actionNa) {
        this.actionNa = actionNa;
    }

    public String getAttributesString() {
        return this.attributesString;
    }

    public void setAttributesString(String attributesString) {
        this.attributesString = attributesString;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

	public String getWarnDescStr() {
		return warnDescStr;
	}

	public void setWarnDescStr(String warnDescStr) {
		this.warnDescStr = warnDescStr;
    }

    public String getInitialLine() {
        return initialLine;
    }

    public void setInitialLine(String initialLine) {
        this.initialLine = initialLine;
    }

    public ArrayList getAllAttributesA() {
        return allAttributesA;
    }

    public void setAllAttributesA(ArrayList allAttributesA) {
        this.allAttributesA = allAttributesA;
    }

	public Element getAllAttributes() {
		return allAttributes;
	}

	public void setAllAttributes(Element allAttributes) {
		this.allAttributes = allAttributes;
    }

    public int getBadNum() {
        return badNum;
    }
    public void setBadNum(int badNum) {
        this.badNum = badNum;
    }
    public String getBadString() {
        return badString;
    }
    public void setBadString(String badString) {
        this.badString = badString;
    }

    public ArrayList getParaList() {
    return this.paraList;
   }

   public void setParaList(ArrayList paraList) {
       this.paraList = paraList;
   }

}
