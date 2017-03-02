package com.boc.cdse;

import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

/**
 * <p>解析配置文件,取得dom树;更新配置文件。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 *
 */
public class EnvironmentConfigureFileHandler {
    /** 类静态变量 */
    private static EnvironmentConfigureFileHandler configureFileHandle_Instance = null;
    /** dom树 */
    private Document configureDoc = null;
    /** 配置文件相对路径 */
    private String xmlName = "com/boc/cdse/configfiles/ConfigureFile.xml";
    /** 配置文件 */
    private File fileName = new File(getClass().getClassLoader().getResource(
        xmlName).toString().substring(6));
    /** 错误号 */
    private int errorCode = 0;
    /** 错误信息 */
    private String errorString = null;
    /** 日志信息 */
    private String logString = "";

    /**
     * 返回dom树
     */
    public Document getConfigureDoc() {
        return this.configureDoc;
    }

    /**
     * 设置dom树
     * @param configureDoc   dom树
     */
    public void setConfigureDoc(Document configureDoc) {
        this.configureDoc = configureDoc;
    }

    /**
     * 返回错误号
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * 设置错误号
     * @param errorCode   The new 错误号
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 返回错误信息
     */
    public String getErrorString() {
        return this.errorString;
    }

    /**
     * 设置错误信息
     * @param errorString   错误信息
     */
    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    /**
     * 返回日志信息
     */
    public String getLogString() {
        return this.logString;
    }

    /**
     * 设置日志信息
     * @param logString   日志信息
     */
    public void setLogString(String logString) {
        this.logString = logString;
    }

    /**
     * 构造函数，调用openConfigureFile()解析配置文件取得dom树。
     *
     * @throws CDSEException 解析配置文件异常时抛出
     */
    private EnvironmentConfigureFileHandler() throws CDSEException{

        logString = CDSEUtil.getCurrentTime();
        logString = logString +
            "\nStarting to open the environment configure file:\n" +
            xmlName + "\n";

        //解析配置文件取得dom树
        int openResult = openConfigureFile();

        //如果发生错误，抛出异常
        if (openResult != 1) {
            logString = logString + CDSEUtil.getCurrentTime() + errorString +
                "\n";
            //System.out.println(logString);
            throw new CDSEException(openResult);
        }
        else {
            logString = logString + CDSEUtil.getCurrentTime() +
                "\nThe configure file is opened" +
                "\n";
        }
       // System.out.println(logString);
    }

    /**
     * 取得类实例。
     *
     * @throws CDSEException 解析配置文件异常时抛出
     */
    public static EnvironmentConfigureFileHandler getInstance() throws CDSEException{
        if (configureFileHandle_Instance == null) {
            configureFileHandle_Instance = new EnvironmentConfigureFileHandler();
        }
        return configureFileHandle_Instance;
    }

    /**
     * 解析配置文件取得dom树。
     *
     * @return 解析结果
     */
    private int openConfigureFile() {
        errorCode = 1;
        if (configureDoc != null) {
            System.out.println(configureDoc != null);
            return errorCode;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            errorCode = 110;
            errorString = "创建配置DocumentBuilder错误！(Read Configure Step)";
            ex.printStackTrace();
            return errorCode;
        }

        try {
            configureDoc = db.parse(getClass().getClassLoader().
                                    getResourceAsStream(xmlName));
        }
        catch (org.xml.sax.SAXException e) {
            errorCode = 101;
            errorString = "配置文件Parse错误SAXException！(Read Configure Step)";
            e.printStackTrace();
            return errorCode;
        }
        catch (DOMException e) {
            errorCode = 102;
            errorString = "配置文件Parse错误！DOMException(Read Configure Step)";
            e.printStackTrace();
            return errorCode;

        }
        catch (IOException e) {
            errorCode = 103;
            errorString = "配置文件Parse错误！IOException(Read Configure Step)";
            e.printStackTrace();
            return errorCode;
        }
        return errorCode;
    }

    /**
     * 更新配置文件。
     *
     * @return 更新结果 0:正常
     *                  -1:异常
     */
    public int refreshConfigureFile() {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tFactory.newTransformer();
            DOMSource source = null;
            source = new DOMSource(configureDoc);
            StreamResult result = null;
            result = new StreamResult(fileName);
            transformer.transform(source, result);
        }
        catch (TransformerException ex1) {
            errorCode = -1;
            errorString = "更新配置文件错误！(Read Configure Step)";
        }
        return 0;
    }

    /**
     * 重置dom树为空。
     *
     */
    public void closeFileHandle() {
        configureDoc = null;
    }

}
