package com.boc.cdse;

/**
 * <p>QuasiCreditCardHandler�ฺ��׼���ǿ������봦��</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   CreditCardHandler
 */


public class QuasiCreditCardHandler
    implements CreditCardHandler {
    /*
     * ׼���ǿ�������
     */
    public QuasiCreditCardHandler() {
    }

    /*
     * ׼���ǿ�����ӿ�
     *
     * @param appForm ׼���ǿ�����������
     * @param log     ����ϵͳ
     */

    public CDSEResult process(org.w3c.dom.Document appForm, Log log,String status) {
        return null;
    }

}