package com.boc.cdse;


import org.w3c.dom.Document;


/**
 * <p>CDSEMessageBean����CDSE���ܵĽӿڣ����ڽ��յ����뿨����������������Ӧ����
 * ������������ž��ߺͶ�Ⱦ��ߣ�������ɺ����������з���������������Ӧ����־
 * ��Ϣд����־�ļ��С�</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   MessageQueueHandler
 * @see   RunTimeEnvironment
 * @see   StrategyManager
 * @see   LogManager
 *
 */

public class CDSEEngineBean {
    /** ��Ϣ���������� */
    //private MessageDrivenContext messageDrivenContext;

    /** strategyManager�������CDSE�еĲ����ļ� */
    //private StrategyManager strategyManager = null;
    /** logManager�������CDSE��־���ݵ���� */
    private LogManager logManager = null;
    /** �ྲ̬���� */
    private static CDSEEngineBean _instance = null;

    /**
     * ȡ����ʵ����
     *
     */
    public static CDSEEngineBean getInstance() {
        if (_instance == null) {
                _instance = new CDSEEngineBean();
        }
        return _instance;
    }


    /**
     * ejbCreate����������EJB����ʵ����CDSEMessageBeanʱ�����ã����ڳ�ʼ��
     * CDSEMessageBean����ʱ��Ҫ����Ϣ��
     *
     * @throws CreateException ���CDSEMessageBeanʵ������ʱ�쳣����
     */


    public String process(Document doc) {
        logManager = LogManager.getInstance();
        logManager.toCdsLog("start execute");
        logManager.toCdsLog(doc.toString());
        String aa = "";
        try {
            if (doc != null) {
                Log log = new Log();
                CDSEBean cdseBean = new CDSEBean();
                CDSEResult cdseResult = new CDSEResult();
                
                cdseResult = cdseBean.process(doc, log,"1");

                if (logManager != null) {
                    logManager.toLog(log);
                }
                log = null;
//                System.out.println("Result from cdse :" +cdseResult.toXMLString());
                aa = cdseResult.toXMLString();
                logManager.toCdsLog(aa);
                logManager.toCdsLog("end execute");
                
                
            } else {
                System.out.println("Message is a error doc��");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aa;
    }

}
