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
 * <p>StrategyListener线程类是CDSE用来监听指定目录下策略文件的接口，它在发现有新的策略文件上传后会将其载入系统。
 *</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   RunTimeEnvironment
 * @see   AppDataManager
 */

public class AppDataListener
    extends Thread {
    /** 初始数据文件名数组 */
    private String[] strategyf = null;
    /** 数据文件路径 */
    private String path = null;

    /**输出数据文件名*/
    private File outFile = null;

    /** 数据监听器类的实例 */
    static private AppDataListener _instance = null;
    /** 设置系统监听时间 */
    private long strategyListenerTime = 30000;
    private boolean runflag = true;
    /**输出结果dom*/
    private Document outDom = null;
    private Element outData = null;

    /**
     * getInstance()方法产生一个AppDataListener实例
     *
     * @return StrategyListener 监听器类的实例
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
     * AppDataListener()方法产生一个环境变量实例,读取初始策略文件
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
     * initFiles()方法获取指定目录下制定后缀名的所有数据文件
     * @param dir   指定目录
     * @return      文件名数组集
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
     * refreshFiles()方法获取指定目录下新考入所有数据文件
     * @param dir   指定目录
     * @param initFiles  初始数据文件集
     * @return      新数据文件名列表集
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
     * run()方法启动线程在系统设定监听是否有新的数据文件存在
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
                                       "未发现新的数据文件");
                    LogManager.getInstance().toAppLog(CDSEUtil.getCurrentTime() +
                        ":" +
                        "未发现新的数据文件");
                }
                else {
                    //System.out.println("已发现 " + count + " 个新的策略文件");
                    LogManager.getInstance().toAppLog("已发现 " + count +
                        " 个新的数据文件");
                    for (int i = 0; i < newStrategyf.size(); i++) {
                        fileName = (String) newStrategyf.get(i);
                        outFile = new File(_instance.path + "/output/"+fileName);
                        //按条处理文件中的申请记录，并返回策略结果
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
     * 对已发现的申请数据文件按条进行授信处理，并将结果输出到结果文件中
     * @param filePath
     */
    private void dealWithFile(String filePath) {
        Document entireDoc = null;
        Element data = null;
        NodeList rowList = null;
        Element row = null;
        Document oneDom = null;
        Document resultDom = null;

        //取得整个dom
        entireDoc = getEntireDom(filePath);

        if (entireDoc != null) {
            //取得根元素
            data = entireDoc.getDocumentElement();

            //取得row
            rowList = data.getElementsByTagName("row");
            for (int i = 0; i < rowList.getLength(); i++) {
                row = (Element) rowList.item(i);

                //为每条数据构造dom树
                oneDom = newDocument(row);

                //调用Bean进行授信决策
                CDSEEngineBean engineBean = CDSEEngineBean.getInstance();
                String result = "";
                result = engineBean.process(oneDom);

                resultDom = CDSEUtil.stringToXml(result);
                //输出结果到输出文件中
                outputResult(row, resultDom);

            }
        }
    }

    /**
     * 为每条数据构造dom树
     * @param oneRow 一条数据元素
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

        //下面是建立XML文档内容的过程，先建立根元素"Basic"
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
        //根元素添加上文档
        newDom.appendChild(basic);

        return newDom;

    }

    /**
     * 输出结果到结果文件中
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
        //授信结果输出
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
     * 得到整个申请数据Dom树
     * @param filePath 申请数据文件
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
            //生成输出结果dom
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