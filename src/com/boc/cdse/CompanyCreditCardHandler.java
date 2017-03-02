package com.boc.cdse;

/**
 * <p>CompanyCreditCardHandler类负责公司卡的申请处理。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   CreditCardHandler
 */

public class CompanyCreditCardHandler
    implements CreditCardHandler {

    /*
     * 公司信用卡处理接口
     *
     * @param appForm 公司卡的申请数据
     * @param log     保存系统
     */
    public CDSEResult process(org.w3c.dom.Document appForm, Log log,String status) {
        return null;
    }
}