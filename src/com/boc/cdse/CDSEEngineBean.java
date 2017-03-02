package com.boc.cdse;


import org.w3c.dom.Document;


/**
 * <p>CDSEMessageBean类是CDSE的总的接口，它在接收到申请卡的数据请求后调用相应的类
 * 对申请进行授信决策和额度决策，处理完成后向结果队列中发送申请结果并将相应的日志
 * 信息写到日志文件中。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   MessageQueueHandler
 * @see   RunTimeEnvironment
 * @see   StrategyManager
 * @see   LogManager
 *
 */

public class CDSEEngineBean {
    /** 消息驱动上下文 */
    //private MessageDrivenContext messageDrivenContext;

    /** strategyManager负责管理CDSE中的策略文件 */
    //private StrategyManager strategyManager = null;
    /** logManager负责管理CDSE日志数据的输出 */
    private LogManager logManager = null;
    /** 类静态变量 */
    private static CDSEEngineBean _instance = null;

    /**
     * 取得类实例。
     *
     */
    public static CDSEEngineBean getInstance() {
        if (_instance == null) {
                _instance = new CDSEEngineBean();
        }
        return _instance;
    }


    /**
     * ejbCreate（）方法在EJB容器实例化CDSEMessageBean时被调用，用于初始化
     * CDSEMessageBean运行时需要的信息。
     *
     * @throws CreateException 如果CDSEMessageBean实例创建时异常出现
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
                System.out.println("Message is a error doc！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aa;
    }

}
