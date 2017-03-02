package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>Log类保存日志信息。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class Log
    implements Serializable {
    /** 要写入日志的数据项列表 */
    private ArrayList logElementName = new ArrayList();
    private ArrayList logElementValue = new ArrayList();
    /** 申请卡的类型 */
    private String logTypeOfCreditCard = null;

    public ArrayList getLogElementName(){
        return this.logElementName;
    }

    public ArrayList getLogElementValue(){
        return this.logElementValue;
    }

    /**
     *  增加日志数据到列表中去
     *
     * @param name  变量名
     * @param value 变量值
     */

    public void appendParameter(String name, String value) {
        this.logElementName.add(name);
        this.logElementValue.add(value);
    }

    /** 获取申请卡的类型
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

}
