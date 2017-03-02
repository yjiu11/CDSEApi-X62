package com.boc.cdse;

import java.util.ArrayList;
import org.w3c.dom.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class XMLStrategy {
	public String StgNo = "";
	public String StgVer = "";
	public String StgNa = "";
	public String StgPri = "";
	public String PlsNo = "";
	public String PlsVer = "";
	public String Script = "";
	public String SrtDate = "";
	public String Bg = "";

	ArrayList Items = new ArrayList();
	ArrayList Parameters = new ArrayList();
	XMLPolicy policy = null;

	public XMLStrategy() {
	}

	public boolean parse(Document strategyDoc) {

		Node itemsNode = null;
		XMLStrategyItem item = null;
		Node parametersNode = null;
		XMLStrategyParameter parameter = null;
		Element parameterE = null;
		Node tmpNode = null;
		Element strategyBasic = null;
		Element strategy = null;

		try {
			strategyBasic = strategyDoc.getDocumentElement();
			tmpNode = strategyBasic.getFirstChild();
			if (tmpNode == null) {
				return false;
			} while (! (tmpNode == null)) {
				if (tmpNode.getNodeName().equals("Stratege")) {
					strategy = (Element) tmpNode;
					break;
				}
				tmpNode = tmpNode.getNextSibling();
			}

			Element strategyE;
			strategyE = (Element) strategy;
			StgNo = strategyE.getAttribute("StgNo");
			StgVer = strategyE.getAttribute("StgVer");
			StgNa = strategyE.getAttribute("StgNa");
			StgPri = strategyE.getAttribute("StgPri");
			PlsNo = strategyE.getAttribute("PlsNo");
			PlsVer = strategyE.getAttribute("PlsVer");
			Script = strategyE.getAttribute("Script");
			SrtDate = strategyE.getAttribute("SrtDate");
			Bg = strategyE.getAttribute("Bg");

			NodeList tmpList = strategyE.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				tmpNode = tmpList.item(i);
				if (tmpNode.getNodeName().equals("Items")) {
					itemsNode = tmpNode;
					break;
				}
			}

			Element itemE = null;
			tmpList = itemsNode.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				tmpNode = tmpList.item(i);
				itemE = (Element) tmpNode;
				if ("-1".equals(itemE.getAttribute("ParentId"))) {
					item = new XMLStrategyItem();
					item.NodeId = itemE.getAttribute("NodeId");
					item.ParentId = itemE.getAttribute("ParentId");
					item.VarNo = itemE.getAttribute("VarNo");
					item.CmpOpe = itemE.getAttribute("CmpOpe");
					item.CmpVal = itemE.getAttribute("CmpVal");
					item.ActionId = itemE.getAttribute("ActionId");
					item.ReasonCode = itemE.getAttribute("ReasonCode");
					item.NodeLevel = itemE.getAttribute("NodeLevel");
					item.ItemDesc = itemE.getAttribute("ItemDesc");
					item.ReWrite = itemE.getAttribute("ReWrite");
					item.ReasonDsc = itemE.getAttribute("ReasonDsc");
					if ("".equals(itemE.getAttribute("ActionId"))) {
						parseItem(item, (Element) itemsNode);
					}
					if (itemE.hasChildNodes()) {
						NodeList paramsList = itemE.getChildNodes();
						for (int j = 0; j < paramsList.getLength(); j++) {
							parametersNode = (Element) paramsList.item(j); ;
							break;
						}
						NodeList paramList = parametersNode.getChildNodes();
						for (int k = 0; k < paramList.getLength(); k++) {
							parameterE = (Element) paramList.item(k);
							parameter = new XMLStrategyParameter();

							parameter.ParaName = parameterE.getAttribute("ParaName");
							parameter.ParaDesc = parameterE.getAttribute("ParaDesc");
							parameter.ParaType = parameterE.getAttribute("ParaType");
							parameter.ParaSpec = parameterE.getAttribute("ParaSpec");
							parameter.ParaValue = parameterE.getAttribute("ParaValue");
							item.parameters.add(parameter);
						}

					}
					Items.add(item);
					break;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			itemsNode = null;
			item = null;
			parametersNode = null;
			parameter = null;
			parameterE = null;
			tmpNode = null;
			strategyBasic = null;
			strategy = null;
		}

		return true;
	}

	private boolean parseItem(XMLStrategyItem item, Element itemsDoc) {
		XMLStrategyItem childItem = null;
		Node parametersNode = null;
		XMLStrategyParameter parameter = null;
		Element parameterE = null;

		try {
			Element itemE = null;
			NodeList tmpList = itemsDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				itemE = (Element) tmpList.item(i);
				if (item.NodeId.equals(itemE.getAttribute("ParentId"))) {
					childItem = new XMLStrategyItem();
					childItem.NodeId = itemE.getAttribute("NodeId");
					childItem.ParentId = itemE.getAttribute("ParentId");
					childItem.VarNo = itemE.getAttribute("VarNo");
					childItem.CmpOpe = itemE.getAttribute("CmpOpe");
					childItem.CmpVal = itemE.getAttribute("CmpVal");
					childItem.ActionId = itemE.getAttribute("ActionId");
					childItem.ReasonCode = itemE.getAttribute("ReasonCode");
					childItem.NodeLevel = itemE.getAttribute("NodeLevel");
					childItem.ItemDesc = itemE.getAttribute("ItemDesc");
					childItem.ReWrite = itemE.getAttribute("ReWrite");
					childItem.ReasonDsc = itemE.getAttribute("ReasonDsc");
					if ("".equals(itemE.getAttribute("ActionId"))) {
						parseItem(childItem, itemsDoc);
					}
					if (itemE.hasChildNodes()) {
						NodeList paramsList = itemE.getChildNodes();
						for (int j = 0; j < paramsList.getLength(); j++) {
							parametersNode = (Element) paramsList.item(j); ;
							break;
						}
						NodeList paramList = parametersNode.getChildNodes();
						for (int k = 0; k < paramList.getLength(); k++) {
							parameterE = (Element) paramList.item(k);
							parameter = new XMLStrategyParameter();

							parameter.ParaName = parameterE.getAttribute("ParaName");
							parameter.ParaDesc = parameterE.getAttribute("ParaDesc");
							parameter.ParaType = parameterE.getAttribute("ParaType");
							parameter.ParaSpec = parameterE.getAttribute("ParaSpec");
							parameter.ParaValue = parameterE.getAttribute("ParaValue");
							childItem.parameters.add(parameter);
						}
					}
					item.childItems.add(childItem);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			childItem = null;
			parametersNode = null;
			parameter = null;
			parameterE = null;
		}

		return true;
	}

}
