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

public class XMLPolicy {
	public XMLPolicy() {
	}

	public String PlsNo = "";
	public String PlsVer = "";
	public String PlsNa = "";
	public String PlsKind = "";
	public String PlsSpe = "";
	public String PlsPri = "";
	public String ProdNo = "";
	public String IsEnable = "";
	public String Script = "";
	public String SrtDate = "";

	public ArrayList Items = new ArrayList();
	public ArrayList Inclusion = new ArrayList();

	public boolean parse(Document strategyDoc) {
		Node itemsNode = null;
		XMLPolicyItem item = null;
		Node inclusionNode = null;
		Node tmpNode = null;
		Element strategyBasic = null;
		Element policy = null;
		Element itemX = null;

		try {
			strategyBasic = strategyDoc.getDocumentElement();
			tmpNode = strategyBasic.getFirstChild();
			if (tmpNode == null) {
				return false;
			} while (! (tmpNode == null)) {
				if (tmpNode.getNodeName().equals("Policy")) {
					policy = (Element) tmpNode;
					break;
				}
				tmpNode = tmpNode.getNextSibling();
			}

			Element policyE;
			policyE = (Element) policy;
			PlsNo = policyE.getAttribute("PlsNo");
			PlsVer = policyE.getAttribute("PlsVer");
			PlsNa = policyE.getAttribute("PlsNa");
			PlsKind = policyE.getAttribute("PlsKind");
			PlsSpe = policyE.getAttribute("PlsSpe");
			PlsPri = policyE.getAttribute("PlsPri");
			ProdNo = policyE.getAttribute("ProdNo");
			IsEnable = policyE.getAttribute("IsEnable");
			Script = policyE.getAttribute("Script");
			SrtDate = policyE.getAttribute("Srt_date");

			NodeList tmpList = policyE.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				tmpNode = tmpList.item(i);
				if (tmpNode.getNodeName().equals("Items")) {
					itemsNode = tmpNode;
					break;
				}
			}

			for (int i = 0; i < tmpList.getLength(); i++) {
				tmpNode = tmpList.item(i);
				if (tmpNode.getNodeName().equals("Inclusion")) {
					inclusionNode = tmpNode;
					break;
				}
			}
			if (itemsNode == null) {
				return false;
			}

			tmpList = itemsNode.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				itemX = (Element) tmpList.item(i);
				item = new XMLPolicyItem();
				item.ItemNo = itemX.getAttribute("ItemNo");
				item.ItemNa = itemX.getAttribute("ItemNa");
				item.ReWrite = itemX.getAttribute("ReWrite");
				Items.add(item);
			}

			if (inclusionNode != null) {
				parseConditions( (Element) inclusionNode);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			itemX = null;
			policy = null;
			strategyBasic = null;
			tmpNode = null;
			inclusionNode = null;
			item = null;
			itemsNode = null;
		}

		return true;
	}

	private boolean parseConditions(Element conditionsDoc) {
		Node conditionNode = null;
		Element conditionXE = null;
		XMLPolicyCondition condition = null;

		try {

			NodeList tmpList = conditionsDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				conditionNode = tmpList.item(i);
				if (conditionNode.getNodeName().equals("Condition")) {
					conditionXE = (Element) conditionNode;
					condition = new XMLPolicyCondition();

					condition.VarNo = conditionXE.getAttribute("VarNo");
					condition.VType = conditionXE.getAttribute("vType");
					condition.LogOpe = conditionXE.getAttribute("LogOpe");
					condition.CmpOpe = conditionXE.getAttribute("CmpOpe");
					condition.CmpVal = conditionXE.getAttribute("CmpVal");
					condition.CompAttr = conditionXE.getAttribute("CompAttr");
					condition.CompAttr = conditionXE.getAttribute("Lnum");
					condition.CompAttr = conditionXE.getAttribute("Rnum");
					Inclusion.add(condition);
				} else if (conditionNode.getNodeName().equals("ConditionN")) {
					conditionXE = (Element) conditionNode;
					condition = new XMLPolicyCondition();
					parseConditionN(condition, conditionXE);
					Inclusion.add(condition);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			conditionNode = null;
			conditionXE = null;
			condition = null;
		}

		return true;

	}

	private boolean parseConditionN(XMLPolicyCondition condition, Element conditionsDoc) {
		Node conditionNode = null;
		Element conditionXE = null;
		XMLPolicyCondition childCondition = null;

		try {

			NodeList tmpList = conditionsDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				conditionNode = tmpList.item(i);
				if (conditionNode.getNodeName().equals("Condition")) {
					conditionXE = (Element) conditionNode;
					childCondition = new XMLPolicyCondition();

					childCondition.VarNo = conditionXE.getAttribute("VarNo");
					childCondition.VType = conditionXE.getAttribute("vType");
					childCondition.LogOpe = conditionXE.getAttribute("LogOpe");
					childCondition.CmpOpe = conditionXE.getAttribute("CmpOpe");
					childCondition.CmpVal = conditionXE.getAttribute("CmpVal");
					childCondition.CompAttr = conditionXE.getAttribute("CompAttr");
					childCondition.CompAttr = conditionXE.getAttribute("Lnum");
					childCondition.CompAttr = conditionXE.getAttribute("Rnum");
					condition.childConditions.add(childCondition);

				} else if (conditionNode.getNodeName().equals("ConditionN")) {
					conditionXE = (Element) conditionNode;
					childCondition = new XMLPolicyCondition();
					parseConditionN(childCondition, conditionXE);
					condition.childConditions.add(childCondition);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			conditionNode = null;
			conditionXE = null;
			childCondition = null;
		}

		return true;

	}

}
