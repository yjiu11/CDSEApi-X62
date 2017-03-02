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

public class CDSEMessage  {
    /** 消息驱动上下文 */
	private static CDSEMessage _instance = null;
	CDSLog clog = new CDSLog();
    /** runTimeEnvironment负责加载CDSE运行时所需要的配置信息 */
    private RunTimeEnvironment runTimeEnvironment = null;
    /** strategyManager负责管理CDSE中的策略文件 */
    private StrategyManager strategyManager = null;
    /** logManager负责管理CDSE日志数据的输出 */
    private LogManager logManager = null;
    private Strategy strategys = null;
    private com.boc.cdse.ReportManager reportManager;


	public static CDSEMessage getInstance() {
		if (null == _instance) {
			_instance = new CDSEMessage();
		}

		return _instance;
	}
	
	

//    public void ejbCreate() {
//        runTimeEnvironment = RunTimeEnvironment.getInstance();
//        messageQueueHandler = MessageQueueHandler.getInstance();
//        strategyManager = StrategyManager.getInstance();
//        logManager = LogManager.getInstance();
//		reportManager=ReportManager.getInstance();
//    }

    /**
     * CDSProcess（）方法在接收到信用卡申请数据消息被调用，负责处理信用卡申请数据，
     * 处理完成后向结果队列中发送申请结果并将相应的日志信息写到日志文件中。
     *
     * @param msg 信用卡申请数据对象
     */
	public String process(String xmlString){
		String status = "1";//审批
		CDSEResult cdseResult = null ;
		Document document = CDSEUtil.stringToXml(xmlString);
		clog.cdsxml(xmlString);
		cdseResult=process(document,status);
		clog.cdsxml(cdseResult.toXMLString());
		return cdseResult.toXMLString();
	}
	
	public String temporaryprocess(String xmlString){
		String status = "2";//临时
		CDSEResult cdseResult = null ;
		Document document = CDSEUtil.stringToXml(xmlString);
		clog.cdsxml(xmlString);
		cdseResult=process(document,status);
		clog.cdsxml(cdseResult.toXMLString());
		return cdseResult.toXMLString();
	}
	public String permanentprocess(String xmlString){
		String status = "3";//永久
		System.out.println("status"+status);
		CDSEResult cdseResult = null ;
		Document document = CDSEUtil.stringToXml(xmlString);
		clog.cdsxml(xmlString);
		cdseResult=process(document,status);
		clog.cdsxml(cdseResult.toXMLString());
		return cdseResult.toXMLString();
	}
	public String networkprocess(String xmlString){
		String status = "4";//网申
		CDSEResult cdseResult = null ;
		Document document = CDSEUtil.stringToXml(xmlString);
		clog.cdsxml(xmlString);
		cdseResult=process(document,status);
		clog.cdsxml(cdseResult.toXMLString());
		return cdseResult.toXMLString();
	}
	public String channelprocess(String xmlString){
		String status = "5";//网银调额、临增
		CDSEResult cdseResult = null ;
		Document document = CDSEUtil.stringToXml(xmlString);
		clog.cdsxml(xmlString);
		cdseResult=process(document,status);
		clog.cdsxml(cdseResult.toXMLString());
		return cdseResult.toXMLString();
	}
	public void card_refresh(){
		ProductCardInfoParameter pcp = new ProductCardInfoParameter();
		/**X64 2016年3月29日，原因：去除外卡码*/
		pcp.load_allProductCardMap();
		if(ProductCardInfoParameter.allProductCardMap==null){
			clog.cds("card is not find："+ProductCardInfoParameter.allProductCardMap);
		}
	}
	public void process(Strategy strategy){
		strategys = strategy;
		clog.cds(strategys);
	}
    /**
     * CDSProcess（）方法在接收到信用卡申请数据消息被调用，负责处理信用卡申请数据，
     * 处理完成后向结果队列中发送申请结果并将相应的日志信息写到日志文件中。
     *
     * @param msg 信用卡申请数据对象
     */
	
    private  CDSEResult process(org.w3c.dom.Document doc,String status) {
		
        runTimeEnvironment = RunTimeEnvironment.getInstance();
     
        strategyManager = StrategyManager.getInstance();
 //       logManager = LogManager.getInstance();
		reportManager=	ReportManager.getInstance();    
        String result = null;
        CDSEResult cdseResult = null ;
        try {
             
            if (doc != null) {
                Log log = new Log();
                CDSEBean cdseBean = new CDSEBean();
                cdseResult = cdseBean.process(doc, log,status);
                //System.out.println("cdsResult::::"+cdseResult);
                clog.cds(cdseResult);
                //clog.cds();
//                Strategy str = clog.getStrategy();
//                System.out.println(str);
//                System.out.println("返回结果:" + cdseResult.toXMLString());
				//跟踪报告
				reportManager.toAppTrackReport(cdseResult);
               
//                if (messageQueueHandler != null) {
//                    messageQueueHandler.resultSendText(cdseResult.toXMLString(), msg.getJMSCorrelationID());
//                }
			 
//				result  =  cdseResult.toXMLString();

                if (logManager != null) {
                    logManager.toLog(log);
                }

				
				cdseBean=null;
				log = null;
				doc=null;
                
            } else {	
                System.out.println("Message is a error doc！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdseResult;
		
    }



//    private org.w3c.dom.Document getdoc(String msg) {
//        if (msg == null) {
//            return null;
//        }
//        org.w3c.dom.Document appForm = null;
//        String msgStr = null;
//        try {
//            if (msg instanceof ObjectMessage) {
//                ObjectMessage objectMessage = (ObjectMessage) msg;
//                if ( (objectMessage.getObject() != null)) {
//                    if (objectMessage.getObject()instanceof java.lang.String) {
//                        msgStr = (String) objectMessage.getObject();
//                        appForm = CDSEUtil.stringToXml( (String) objectMessage.getObject());
//                    } else if (objectMessage.getObject()instanceof org.w3c.dom.Document) {
//                        msgStr = CDSEUtil.xmlToString( (org.w3c.dom.Document) objectMessage.getObject());
//                        appForm = (org.w3c.dom.Document) objectMessage.getObject();
//                    }
//                }
//            } else {
//                if (msg instanceof TextMessage) {
//                    TextMessage textMessage = (TextMessage) msg;
//                    msgStr = (String) textMessage.getText();
//                    appForm = CDSEUtil.stringToXml( (String) textMessage.getText());
//                } else {
//                    if (msg instanceof StreamMessage) {
//                        StreamMessage streamMessage = (StreamMessage) msg;
//                        String doc = new String();
//                        streamMessage.writeString(doc);
//                        msgStr = doc;
//                        appForm = CDSEUtil.stringToXml( (String) doc);
//                    }
//                }
//            }
//            //stringToFile("d:/FTP/CDS/result/" + CDSEUtil.getCurrentTime() + ".XML", msgStr);
//            //System.out.println("receive a message:" + msgStr);
//        } catch (Exception e) {
//            System.out.println("receive a message:" + msgStr);
//            //stringToFile("d:/FTP/CDS/result/" + CDSEUtil.getCurrentTime() + ".XML", msgStr);
//            if (logManager != null) {
//                logManager.toErrorLog(msgStr + e.getMessage());
//            }
//            e.printStackTrace();
//        }
//        return appForm;
//    }

//    private void stringToFile(String fileName, String xString) {
//
//        try {
//            FileOutputStream outStream = new FileOutputStream(fileName);
//            //OutputStreamWriter outWriter=new OutputStreamWriter(outStream);
//            //outWriter.write(xString);
//            outStream.write(xString.getBytes());
//            //outWriter.flush();
//            outStream.flush();
//            outStream.close();
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//
//    }

}
