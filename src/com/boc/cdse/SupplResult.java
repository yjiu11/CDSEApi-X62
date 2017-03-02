package com.boc.cdse;

import java.io.*;

/**
 * <p>Title: CDSE</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: CF</p>
 *
 * @author CDSE项目组
 * @version 1.0
 */
public final class SupplResult implements Serializable {
    private String supplID="";
    private String supplResultID="";
    private String supplResultDescription="";
    private String supplUwReasonCode="";
    private String supplUwReasonDescription="";
    /**C1507修改*/
    private int supplInitialLineRMB=0;
    private int supplInitialLineFRC=0;
    private int supplCashAdvanceRMB=0;
    private int supplCashAdvanceFRC=0;

    /** 策略编号 */
    private String strategyNo = " ";
    /** 策略版本 */
    private String strategyVer = " ";

    public SupplResult() {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSupplID(String supplID) {
        this.supplID = supplID;
    }

    public void setSupplResultID(String supplResultID) {
        this.supplResultID = supplResultID;
    }

    public void setSupplResultDescription(String supplResultDescription) {
        this.supplResultDescription = supplResultDescription;
    }

    public void setSupplUwReasonCode(String supplUwReasonCode) {
        this.supplUwReasonCode = supplUwReasonCode;
    }

    public void setSupplUwReasonDescription(String supplUwReasonDescription) {
        this.supplUwReasonDescription = supplUwReasonDescription;
    }

    public void setSupplInitialLineRMB(int supplInitialLineRMB) {
        this.supplInitialLineRMB = supplInitialLineRMB;
    }

    public void setSupplInitialLineFRC(int supplInitialLineFRC) {
        this.supplInitialLineFRC = supplInitialLineFRC;
    }

    public void setSupplCashAdvanceRMB(int supplCashAdvanceRMB) {
        this.supplCashAdvanceRMB = supplCashAdvanceRMB;
    }

    public void setSupplCashAdvanceFRC(int supplCashAdvanceFRC) {
        this.supplCashAdvanceFRC = supplCashAdvanceFRC;
    }

    public void setStrategyNo(String strategyNo) {
        this.strategyNo = strategyNo;
    }

    public void setStrategyVer(String strategyVer) {
        this.strategyVer = strategyVer;
    }

    public String getSupplID() {
        return supplID;
    }

    public String getSupplResultID() {
        return supplResultID;
    }

    public String getSupplResultDescription() {
        return supplResultDescription;
    }

    public String getSupplUwReasonCode() {
        return supplUwReasonCode;
    }

    public String getSupplUwReasonDescription() {
        return supplUwReasonDescription;
    }

    public int getSupplInitialLineRMB() {
        return supplInitialLineRMB;
    }

    public int getSupplInitialLineFRC() {
        return supplInitialLineFRC;
    }

    public int getSupplCashAdvanceRMB() {
        return supplCashAdvanceRMB;
    }

    public int getSupplCashAdvanceFRC() {
        return supplCashAdvanceFRC;
    }

    public String getStrategyVer() {
        return strategyVer;
    }

    public String getStrategyNo() {
        return strategyNo;
    }

    private void jbInit() throws Exception {
    }
}
