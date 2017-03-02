package com.boc.cdse;

/**
 * <p>CDSEBean类在接收到申请数据后，根据申请卡的类型调用相应的类进行处理。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public class CDSEBean
    implements java.io.Serializable {
    public CDSEBean() {
    }

    /**
     *process方法处理申请卡请求，获得并返回处理结果。
     * <blockquote><pre>
     * 处理过程为：
     *     1.获得申请卡的种类
     *     2.调用相应的卡处理接口，获得并返回处理结果
     * </pre></blockquote>
     *
     * @param appForm 包含申请原始数据的Document对象
     * @param log     保存日志数据的对象
     *
     * @return   CDSE处理结果
     */
    public CDSEResult process(org.w3c.dom.Document doc, Log log,String status) {
        log.setLogTypeOfCreditCard("PersonalCard");
        CreditCardHandler creditCardHandler = new PersonalCreditCardHandler();
        /*DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
               DocumentBuilder db = null;
               try {
          db = dbf.newDocumentBuilder();
               }
               catch (ParserConfigurationException pce) {
          System.err.println(pce);
               }
               File fileName = null;
               Document doc = null;
               fileName = new File("D://BOCCDSE//Strategy//input.xml");
               try {
          doc = db.parse(fileName);
               }
               catch (org.xml.sax.SAXException ex1) {
          System.err.println(ex1.getMessage());
               }
               catch (DOMException dom) {
          System.err.println(dom.getMessage());
               }
               catch (IOException ioe) {
          System.err.println(ioe);
               }
               return creditCardHandler.process(doc,log);
         */
        
        CDSEResult cdseReult = creditCardHandler.process(doc, log,status);
//        LogManager.getInstance().toCdsLog(str.toString());
        creditCardHandler = null;
        return cdseReult;
    }
}
