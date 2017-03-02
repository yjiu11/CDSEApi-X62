package com.boc.cdse;

import java.io.*;
import java.util.*;

import javax.naming.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Untitled1 {
    public Untitled1() {
    }

    public static void main(String[] args) {
        Untitled1 untitled11 = new Untitled1();

        String dataDir = "D:/BOCCDSURGENCY/SimpleAppl/DATA/";
        //String applNum = request.getParameter("ApplNum");
        String applNum = "ApplNum";
        if (applNum == null || applNum.length() != 16) {
        // response.encodeRedirectURL("QueryAgain.html");
        }
        String applResultFileName = applNum.substring(0, 8) + "/" + applNum +
            ".xml";
        File applFile = new File(dataDir + applResultFileName);
        if (!applFile.exists()) {
        // response.encodeRedirectURL("QueryAgain.html");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document doc = null;
        try {
            db = dbf.newDocumentBuilder();
            doc = db.parse(applFile);
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (org.xml.sax.SAXException saxe) {
            saxe.printStackTrace();
        } catch (DOMException dome) {
            dome.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        Element basic = doc.getDocumentElement();
        Node data = basic.getFirstChild();
        Element item = (Element) data.getFirstChild();
        NodeList applList = item.getElementsByTagName("row");
        Element appl= (Element)applList.item(1);

    }

}