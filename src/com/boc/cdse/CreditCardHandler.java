package com.boc.cdse;

/**
 * <p>CreditCardHandler�����ÿ�����ӿڡ�</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public interface CreditCardHandler {
    public CDSEResult process(org.w3c.dom.Document appForm, Log log,String status);
}