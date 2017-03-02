package com.boc.cdse;

import java.io.Serializable;
import java.util.*;
import org.w3c.dom.Element;

/**
 * <P><Code>ActionResult</Code>类为风险决策接口返回的策略结果对象，通过各属性的set方法储存相关
 * 政策和策略的编号，描述，版本号等信息、返回的决策结果以及决策过程中使用到的中间变量的描述和取值；通过各属性的
 * get方法提取信息。
 *
 * <P>Copyright:版权 (c) 2003 </P>
 * <P>Company:首航财务顾问有限公司</P>
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
    private int badNum;          //不合逻辑的，或不完整的变量的个数
    private String badString;    //不合逻辑的或不完整的变量的组合字串，以|相分隔

    private ArrayList paraList = new ArrayList(); //参数列表

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
