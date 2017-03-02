package com.boc.cdse;

/**
 * <p>QuasiCreditCardHandler类负责准贷记卡的申请处理。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   CreditCardHandler
 */


public class QuasiCreditCardHandler
    implements CreditCardHandler {
    /*
     * 准贷记卡构造器
     */
    public QuasiCreditCardHandler() {
    }

    /*
     * 准贷记卡处理接口
     *
     * @param appForm 准贷记卡的申请数据
     * @param log     保存系统
     */

    public CDSEResult process(org.w3c.dom.Document appForm, Log log,String status) {
        return null;
    }

}