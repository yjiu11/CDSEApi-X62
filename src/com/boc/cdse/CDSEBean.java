package com.boc.cdse;

/**
 * <p>CDSEBean���ڽ��յ��������ݺ󣬸������뿨�����͵�����Ӧ������д���</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public class CDSEBean
    implements java.io.Serializable {
    public CDSEBean() {
    }

    /**
     *process�����������뿨���󣬻�ò����ش�������
     * <blockquote><pre>
     * �������Ϊ��
     *     1.������뿨������
     *     2.������Ӧ�Ŀ�����ӿڣ���ò����ش�����
     * </pre></blockquote>
     *
     * @param appForm ��������ԭʼ���ݵ�Document����
     * @param log     ������־���ݵĶ���
     *
     * @return   CDSE������
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
