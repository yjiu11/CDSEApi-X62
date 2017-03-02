package com.boc.cdse;

/**
 * <p>CreditCardHandler是信用卡处理接口。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public interface CreditCardHandler {
    public CDSEResult process(org.w3c.dom.Document appForm, Log log,String status);
}