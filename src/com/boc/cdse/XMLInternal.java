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

public class XMLInternal {
	public XMLInternal() {
	}

	public ArrayList varibles = new ArrayList();

	public boolean parse(Document inVariablesDoc) {
		Element variableXE = null;
		XMLInternalVariable variable = null;
		Node valueGroupNode = null;
		Node node = null;
		Element inVariables = null;
		Element internalBasic = null;

		try {
			Node tmpNode = null;
			internalBasic = inVariablesDoc.getDocumentElement();
			tmpNode = internalBasic.getFirstChild();
			if (tmpNode == null) {
				return false;
			} while (! (tmpNode == null)) {
				if (tmpNode.getNodeName().equals("Internal")) {
					inVariables = (Element) tmpNode;
					break;
				}
				tmpNode = tmpNode.getNextSibling();
			}

			NodeList tmpList = inVariables.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				variableXE = (Element) tmpList.item(i);
				variable = new XMLInternalVariable();
				variable.Name = variableXE.getAttribute("Name");
				variable.Desc = variableXE.getAttribute("Desc");
				variable.Type = variableXE.getAttribute("Type");
				variable.Length = variableXE.getAttribute("Length");
				variable.Dec = variableXE.getAttribute("Dec");
				variable.Oper = variableXE.getAttribute("Oper");
				variable.Enable = variableXE.getAttribute("Enabled");
				variable.FormatStr = variableXE.getAttribute("FormatStr");
				NodeList valueGroupList = variableXE.getChildNodes();
				for (int j = 0; j < valueGroupList.getLength(); j++) {
					valueGroupNode = valueGroupList.item(j);
					Element valueGroupXE = null;
					XMLInternalValueGroup valueGroup = new XMLInternalValueGroup();
					valueGroupXE = (Element) valueGroupNode;
					NodeList nodeList = valueGroupXE.getChildNodes();

					for (int k = 0; k < nodeList.getLength(); k++) {
						node = nodeList.item(k);
						if ("Conditions".equals(node.getNodeName())) {
							parseConditions(valueGroup, (Element) node);

						} else if ("Values".equals(node.getNodeName())) {
							parseValues(valueGroup, (Element) node);
						}

					}
					variable.ValueGroups.add(valueGroup);
				}
				varibles.add(variable);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			variableXE = null;
			variable = null;
			valueGroupNode = null;
			node = null;
			inVariables = null;
			internalBasic = null;
		}

		return true;
	}

	private boolean parseConditions(XMLInternalValueGroup valueGroup, Element conditionsDoc) {
		Node conditionNode = null;
		Element conditionE = null;
		XMLInternalCondition condition = null;
		try {
			NodeList tmpList = conditionsDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				conditionNode = tmpList.item(i);
				conditionE = (Element) conditionNode;
				if ("Condition".equals(conditionE.getNodeName())) {
					condition = new XMLInternalCondition();
					condition.VarName = conditionE.getAttribute("VarName");
					condition.VType = conditionE.getAttribute("vType");
					condition.CompLog = conditionE.getAttribute("compLog");
					condition.CompOper = conditionE.getAttribute("compOper");
					condition.CompVal = conditionE.getAttribute("compVal");
					condition.CompAttr = conditionE.getAttribute("CompAttr");
					valueGroup.Conditions.add(condition);
				} else if ("ConditionN".equals(conditionE.getNodeName())) {
					condition = new XMLInternalCondition();
					condition.CompLog = conditionE.getAttribute("compLog");
					parseConditionN(condition, conditionE);
					valueGroup.Conditions.add(condition);
				}
			}

		} catch (Exception ex) {
			return false;
		} finally {
			conditionNode = null;
			conditionE = null;
			condition = null;
		}

		return true;
	}

	private boolean parseConditionN(XMLInternalCondition condition, Element conditionsDoc) {
		Node conditionNode = null;
		Element conditionE = null;
		XMLInternalCondition childCondition = null;

		try {
			NodeList tmpList = conditionsDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				conditionNode = tmpList.item(i);
				conditionE = (Element) conditionNode;
				if ("Condition".equals(conditionE.getNodeName())) {
					childCondition = new XMLInternalCondition();
					childCondition.VarName = conditionE.getAttribute("VarName");
					childCondition.VType = conditionE.getAttribute("vType");
					childCondition.CompLog = conditionE.getAttribute("compLog");
					childCondition.CompOper = conditionE.getAttribute("compOper");
					childCondition.CompVal = conditionE.getAttribute("compVal");
					childCondition.CompAttr = conditionE.getAttribute("CompAttr");
					condition.childConditions.add(childCondition);
				} else if ("ConditionN".equals(conditionE.getNodeName())) {
					childCondition = new XMLInternalCondition();
					childCondition.CompLog = conditionE.getAttribute("compLog");
					parseConditionN(childCondition, conditionE);
					condition.childConditions.add(childCondition);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			conditionNode = null;
			conditionE = null;
			childCondition = null;
		}

		return true;
	}

	private boolean parseValues(XMLInternalValueGroup valueGroup, Element ValuesDoc) {
		Node valueNode = null;
		Element valueE = null;
		XMLInternalValue value = null;

		try {
			NodeList tmpList = ValuesDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				valueNode = tmpList.item(i);
				valueE = (Element) valueNode;
				if ("Value".equals(valueE.getNodeName())) {
					value = new XMLInternalValue();
					value.Symble = valueE.getAttribute("Symble");
					value.VType = valueE.getAttribute("vType");
					value.Coeffcient = valueE.getAttribute("Coeffcient");
					value.FunctionA = valueE.getAttribute("Function");
					value.VarName = valueE.getAttribute("VarName");
					value.Parameter = valueE.getAttribute("Parameter");
					value.Param1 = valueE.getAttribute("Param1");
					value.Param2 = valueE.getAttribute("Param2");
					value.Param3 = valueE.getAttribute("Param3");
					valueGroup.Values.add(value);
				} else if ("ValueN".equals(valueE.getNodeName())) {
					value = new XMLInternalValue();
					value.Symble = valueE.getAttribute("Symble");
					parseValuesN(value, valueE);
					valueGroup.Values.add(value);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			valueNode = null;
			valueE = null;
			value = null;
		}

		return true;
	}

	private boolean parseValuesN(XMLInternalValue value,
								 Element valueDoc) {
		Node valueNode = null;
		Element valueE = null;
		XMLInternalValue childValue = null;

		try {
			NodeList tmpList = valueDoc.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				valueNode = tmpList.item(i);
				valueE = (Element) valueNode;
				if ("Value".equals(valueE.getNodeName())) {
					childValue = new XMLInternalValue();
					childValue.Symble = valueE.getAttribute("Symble");
					childValue.VType = valueE.getAttribute("vType");
					childValue.Coeffcient = valueE.getAttribute("Coeffcient");
					childValue.FunctionA = valueE.getAttribute("Function");
					childValue.VarName = valueE.getAttribute("VarName");
					childValue.Parameter = valueE.getAttribute("Parameter");
					childValue.Param1 = valueE.getAttribute("Param1");
					childValue.Param2 = valueE.getAttribute("Param2");
					childValue.Param3 = valueE.getAttribute("Param3");
					value.ChildValues.add(childValue);
				} else if ("ValueN".equals(valueE.getNodeName())) {
					childValue = new XMLInternalValue();
					childValue.Symble = valueE.getAttribute("Symble");
					parseValuesN(childValue, valueE);
					value.ChildValues.add(childValue);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			valueNode = null;
			valueE = null;
			childValue = null;
		}

		return true;
	}
}
