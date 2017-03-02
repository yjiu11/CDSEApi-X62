package com.boc.cdse;

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/**
 * <p>StrategyListener�߳�����CDSE��������ָ��Ŀ¼�²����ļ��Ľӿڣ����ڷ������µĲ����ļ��ϴ���Ὣ������ϵͳ��
 *</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   RunTimeEnvironment
 * @see   AppDataManager
 */

public class AppDataListener
    extends Thread {
    /** ��ʼ�����ļ������� */
    private String[] strategyf = null;
    /** �����ļ�·�� */
    private String path = null;

    /**��������ļ���*/
    private File outFile = null;

    /** ���ݼ��������ʵ�� */
    static private AppDataListener _instance = null;
    /** ����ϵͳ����ʱ�� */
    private long strategyListenerTime = 30000;
    private boolean runflag = true;
    /**������dom*/
    private Document outDom = null;
    private Element outData = null;

    /**
     * getInstance()��������һ��AppDataListenerʵ��
     *
     * @return StrategyListener ���������ʵ��
     */
    static public AppDataListener getInstance() {
        if (null == _instance) {

            RunTimeEnvironment en = RunTimeEnvironment.getInstance();
            if (en != null) {
                //deactiveThreads();
                _instance = new AppDataListener();
                _instance.start();
            }

        }
        return _instance;
    }

    /**
     * AppDataListener()��������һ����������ʵ��,��ȡ��ʼ�����ļ�
     *
     * @return void
     */

    protected AppDataListener() {
        RunTimeEnvironment en = RunTimeEnvironment.getInstance();
        //path = en.getDomainDir();
        path ="D:/BOCCDSURGENCY";
        //strategyListenerTime = en.getStrategyListenerTime();
        strategyf = initFiles(path+"/input");
        //AppDataManager = am;
    }

    /**
     * initFiles()������ȡָ��Ŀ¼���ƶ���׺�������������ļ�
     * @param dir   ָ��Ŀ¼
     * @return      �ļ������鼯
     */

    private String[] initFiles(String dir) {
        String initFiles[] = null;
        File dirFile = null;
        try {
            dirFile = new File(dir);
        }
        catch (Exception ex) {
            System.out.println("Error in getFile dirFile" + ex.getMessage());
        }
        if (dirFile.isDirectory()) {
            Filter filter = new Filter("xml");
            initFiles = dirFile.list(filter);
        }
        return initFiles;
    }

    /**
     * refreshFiles()������ȡָ��Ŀ¼���¿������������ļ�
     * @param dir   ָ��Ŀ¼
     * @param initFiles  ��ʼ�����ļ���
     * @return      �������ļ����б�
     */

    private ArrayList refreshFiles(String dir, String[] initFiles) {
        String tmpFiles[] = null;
        ArrayList refresh = new ArrayList();
        tmpFiles
            = CDSEUtil.initFiles(dir);

        int i = 0;
        int j = 0;
        int c = 0;
        int ii = 0;
        int jj = 0;
        if (tmpFiles == null) {
            return refresh;
        }
        if (initFiles == null) {
            for (ii = 0; ii < tmpFiles.length; ii++) {
                refresh.add(tmpFiles[ii]);
            }
            this.strategyf = tmpFiles;
            //  this.appDataManager.setStrategyFiles(this.strategyf);
            return refresh;
        }
        else {
            int count1 = initFiles.length;
            int count2 = tmpFiles.length;
            if (count1 < count2) {
                for (i = 0; i < count2; i++) {
                    c = 0;
                    for (j = 0; j < count1; j++) {
                        if (tmpFiles[i].equalsIgnoreCase(initFiles[j])) {
                            c = 1;
                            break;
                        }
                    }
                    if (c == 0) {
                        refresh.add(tmpFiles[i]);
                    }
                }
            }

            this.strategyf = tmpFiles;
            //this.appDataManager.setStrategyFiles(this.strategyf);
            return refresh;
        }
    }

    /**
     * run()���������߳���ϵͳ�趨�����Ƿ����µ������ļ�����
     *
     * @return void
     */

    public void run() {
        String fileName = null;
        boolean result;
        while (getrunflag()) {
            try {
                ArrayList newStrategyf = _instance.refreshFiles(_instance.path+"/input",
                    _instance.strategyf);
                int count = newStrategyf.size();
                if (count <= 0) {
                    System.out.println(CDSEUtil.getCurrentTime() + ":" +
                                       "δ�����µ������ļ�");
                    LogManager.getInstance().toAppLog(CDSEUtil.getCurrentTime() +
                        ":" +
                        "δ�����µ������ļ�");
                }
                else {
                    //System.out.println("�ѷ��� " + count + " ���µĲ����ļ�");
                    LogManager.getInstance().toAppLog("�ѷ��� " + count +
                        " ���µ������ļ�");
                    for (int i = 0; i < newStrategyf.size(); i++) {
                        fileName = (String) newStrategyf.get(i);
                        outFile = new File(_instance.path + "/output/"+fileName);
                        //���������ļ��е������¼�������ز��Խ��
                        dealWithFile(_instance.path+"/input/"+fileName);
                    }
                }
                this.currentThread().sleep(strategyListenerTime);
            }
            catch (InterruptedException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    /**
     * ���ѷ��ֵ����������ļ������������Ŵ�������������������ļ���
     * @param filePath
     */
    private void dealWithFile(String filePath) {
        Document entireDoc = null;
        Element data = null;
        NodeList rowList = null;
        Element row = null;
        Document oneDom = null;
        Document resultDom = null;

        //ȡ������dom
        entireDoc = getEntireDom(filePath);

        if (entireDoc != null) {
            //ȡ�ø�Ԫ��
            data = entireDoc.getDocumentElement();

            //ȡ��row
            rowList = data.getElementsByTagName("row");
            for (int i = 0; i < rowList.getLength(); i++) {
                row = (Element) rowList.item(i);

                //Ϊÿ�����ݹ���dom��
                oneDom = newDocument(row);

                //����Bean�������ž���
                CDSEEngineBean engineBean = CDSEEngineBean.getInstance();
                String result = "";
                result = engineBean.process(oneDom);

                resultDom = CDSEUtil.stringToXml(result);
                //������������ļ���
                outputResult(row, resultDom);

            }
        }
    }

    /**
     * Ϊÿ�����ݹ���dom��
     * @param oneRow һ������Ԫ��
     * @return
     */
    private Document newDocument(Element oneRow) {
        Document newDom = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            return null;
        }

        newDom = db.newDocument();

        //�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�����Ԫ��"Basic"
        Element basic = newDom.createElement("basic");
        Element data = newDom.createElement("data");
         Element newRow = newDom.createElement("row");
        NamedNodeMap att = oneRow.getAttributes();
        NamedNodeMap attValue = oneRow.getAttributes();

        for (int i=0;i<att.getLength();i++){
            String name = att.item(i).getNodeName();
            String value = oneRow.getAttribute(name);
            newRow.setAttribute(name,value);
        }
        data.appendChild(newRow);
        basic.appendChild(data);
        //��Ԫ��������ĵ�
        newDom.appendChild(basic);

        return newDom;

    }

    /**
     * ������������ļ���
     * @param outData
     */
    private int outputResult(Element row, Document result) {


        Element newRow = outDom.createElement("row");
       NamedNodeMap att = row.getAttributes();
       for (int i=0;i<att.getLength();i++){
           String name = att.item(i).getNodeName();
           String value = row.getAttribute(name);
           newRow.setAttribute(name,value);
       }

        outData.appendChild(newRow);
        //���Ž�����
        Element resultdata = (Element)result.getDocumentElement().getElementsByTagName("data").item(0);
        Element resultRow = (Element)resultdata.getElementsByTagName("row").item(0);

        Element newResult = outDom.createElement("result");


        NamedNodeMap attResult = resultRow.getAttributes();

        for (int i=0;i<attResult.getLength();i++){
            String name = attResult.item(i).getNodeName();
            String value = resultRow.getAttribute(name);
            newResult.setAttribute(name,value);
        }

        outData.appendChild(newResult);


        TransformerFactory tFactory = TransformerFactory.
            newInstance();
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer();
            DOMSource source = null;
            source = new DOMSource(outDom);
            StreamResult streamResult = null;
            streamResult = new StreamResult(outFile);
            transformer.transform(source, streamResult);
        }
        catch (TransformerException ex1) {
            return -1;
        }
        return 0;

    }

    /**
     * �õ�������������Dom��
     * @param filePath ���������ļ�
     * @return
     */
    private Document getEntireDom(String filePath) {

        Document entireDoc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            //����������dom
           outDom = db.newDocument();
           Element xml = outDom.createElement("xml");
           outDom.appendChild(xml);
           outData = outDom.createElement("data");
           xml.appendChild(outData);

            entireDoc = db.parse(filePath);
        }
        catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
            return null;
        }
        catch (DOMException e) {
            e.printStackTrace();
            return null;

        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return entireDoc;
    }

    public static void deactiveThreads() {
        Thread[] threads = null;
        System.out.println(
            "begin to try to stop the threads belongs to AppDataListener.");
        try {

            if (Thread.activeCount() != 0) {
                threads = new Thread[Thread.activeCount()];
            }
            int n = Thread.enumerate(threads);
            for (int i = 0; i < n; i++) {
                Thread currentThread = threads[i];
                System.out.println("---AppDataListener test() thread :" +
                                   currentThread.getClass().getName());
                if (currentThread.getClass().getName().equals(
                    "com.boc.cdse.AppDataListener")) {
                    // if (currentThread instanceof com.boc.cdse.StrategyListener) {
                    System.out.println("currentThread=" +
                                       currentThread.getClass().getName() +
                                       " stopped");
                    ( (com.boc.cdse.AppDataListener) currentThread).stopthread();
                    currentThread.interrupt();
                    currentThread = null;
                }
                else {
                    System.out.println("currentThread=" +
                                       currentThread.getClass().getName() +
                                       " is not strategylistener.");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopthread() {
        System.out.println(this +" will stop!");
        runflag = false;
    }

    public synchronized boolean getrunflag() {
        return runflag;
    }
}