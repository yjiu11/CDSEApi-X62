package com.boc.cdse;

import java.util.ArrayList;

public class CDSLog {
	private String strategys = null;
	private String xmlStrings = null;
	private String cdseResults = null;
	private static ArrayList list = new ArrayList();
	private static ArrayList list1 = new ArrayList();
	private static ArrayList list2 = new ArrayList();
	public void cds(Strategy strategy){
		if(strategy !=null){
			strategys = "true";
		}
		list.add(strategys);
//		System.out.println("strategys:"+strategys);
	}
	public void cds(String xmlString){
		if(xmlString !=null){
			xmlStrings = xmlString;
		}
		list1.add(xmlStrings);
//		System.out.println("xmlString:"+xmlString);
	}
	public void cdsxml(String xmlString){
		LogManager.getInstance().toCdsXmlLog(xmlString.toString()+";");
	}
	public void cds(CDSEResult cdseResult){
		if(cdseResult !=null){
			cdseResults = cdseResult.toXMLString();
		}
		list2.add(cdseResults);
//		System.out.println("cdseResult:"+cdseResult);
	}
	public void cds(){
		boolean flag = false;
		String str = "";
		if(list.size()>0){
			str = (String) list.get(0);
		}
//			System.out.println(str);
			if(str!="true"||!str.equals("true")){
					String str1 = (String) list1.get(0);
//					System.out.println("str1"+str1);
					String str2 = (String) list2.get(0);
//					System.out.println("str2"+str2);
					LogManager.getInstance().toCdsLog(str1.toString()+";");
					LogManager.getInstance().toCdsLog(str2.toString()+";");
			}
	}
}
