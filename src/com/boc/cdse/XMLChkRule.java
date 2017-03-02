package com.boc.cdse;

import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLChkRule {
	public XMLChkRule() {
	}

	public ArrayList rules = new ArrayList();

	public boolean parse(Document chkRuleDoc) {
		Element chkRuleBasic = null;
		Element chkRuleElement = null;
		XMLChkRuleItem rule = null;
		Node tmpRuleNode = null;
		Element ruleElement=null;

		try {
			Node tmpNode = null;
			chkRuleBasic = chkRuleDoc.getDocumentElement();
			tmpNode = chkRuleBasic.getFirstChild();
			if (tmpNode == null) {
				return false;
			} while (! (tmpNode == null)) {
				if (tmpNode.getNodeName().equals("ChkRules")) {
					chkRuleElement = (Element) tmpNode;
					break;
				}
				tmpNode = tmpNode.getNextSibling();
			}

			if(chkRuleElement!=null)
			{
				NodeList tmpList = chkRuleElement.getChildNodes();
				for (int i = 0; i < tmpList.getLength(); i++) {
					tmpRuleNode = tmpList.item(i);
					if (tmpRuleNode instanceof Element) {
						ruleElement = (Element) tmpRuleNode;
						rule = new XMLChkRuleItem();
						rule.Id = ruleElement.getAttribute("Id");
						rule.varNo = ruleElement.getAttribute("varNo");
						rule.cmpOpe = ruleElement.getAttribute("cmpOpe");
						rule.cmpVal = ruleElement.getAttribute("cmpVal");
						rule.warnDesc = ruleElement.getAttribute("warnDesc");
						rules.add(rule);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}finally
		{
			ruleElement=null;
			tmpRuleNode=null;
			rule=null;
			chkRuleElement=null;
			chkRuleBasic=null;
		}

		return true;
	}
}
