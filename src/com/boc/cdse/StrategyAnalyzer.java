package com.boc.cdse;

import org.w3c.dom.*;
import java.util.*;

/**
 * <P><Code>StrategyAnalyzer</Code>��Ϊ����ִ�нӿڣ�ͨ����ȡ����ָ���ļ���ϴ�����м����
 * ȡ�ò��Խ����<Code>getStrategyResult</Code>Ϊ���߽���Ļ�ȡ������
 *
 * <P>Copyright:��Ȩ (c) 2004 </P>
 * <P>Company:�׺�����������޹�˾</P>
 * @author: Richard
 * @version 2.0
 * @see com.boc.cdse.Attributes
 * @see com.entity.ActionResult
 */

public class StrategyAnalyzer implements java.io.Serializable {
	private String inclusionString;
	/**
	 * ��ȡ���߽��
	 *
	 * @param attributes_0 ͨ�����ݴ�����̲������ⲿ�м����
	 * @param strategyFile �ôξ�������ѭ�Ĳ��Թ���
	 * @param flag ������Դ��־�����������������ض��ĸ�������
	 * @return actionResult ���߽������
	 * <p>������̣�
	 * <p>1.���Խ���ĳ�ʼ����
	 * <p>2.��ȡ�ⲿ�������ϡ�
	 * <p>3.��ȡ�ڲ��������ϼ����㷨��
	 * <P>4.���������ڲ����������ⲿ�����ϳɱ���ȫ����
	 * <P>5.���ڵ㣬���߽ڵ㣬���Խڵ����ȡ��
	 * <p>6.�������ڵ���ȡ���������ĳ�ʼ����
	 * <p>7.�����׼���ж��ʹ���
	 * <p>8.ͨ���������߼����㷵�صĶԲ߶���
	 * <p>&nbsp;&nbsp;8-1.��ȡ������м�����Ķ������ֵ��
	 * <p>&nbsp;&nbsp;8-2.ѡ����ʵĲ�������ֱ֧�����ս����
	 * <p>9.��Ⱦ�����
	 */

	public ActionResult getStrategyResult(PersonalCreditCardAttributes
										  attributes_0,
										  Strategy strategyFile,
										  String flag) {

		//----------------���Խ���ĳ�ʼ��----------------------------------------//
		ActionResult actionResult = new ActionResult();
		String actionId = "��";
		String actionNa = "��";
		String attributesString = "";

		Element basic = strategyFile.getStrategy().getDocumentElement();

		Node tmpNode = null;

		PersonalCreditCardAttributes attributes = (PersonalCreditCardAttributes) attributes_0.clone();

		//----------------��ȡ�ⲿ��������--------------------------------------------//

		Element exVariables = getUseableVaribles(attributes.getDocument().getDocumentElement(), flag);

		//----------------��ȡ�ڲ��������ϼ����㷨--------------------------------//
		Element inVariables = null;
		tmpNode = basic.getFirstChild();
		if (tmpNode == null) {
			System.out.println(" XXXXXXXXX Testing tmpNode is null");
		} while (! (tmpNode == null)) {
			if (tmpNode.getNodeName().equals("Internal")) {
				inVariables = (Element) tmpNode;
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}

		//----------------���������ڲ����������ⲿ�����ϳɱ���ȫ��-----------------//
		Element allVariables = getAllVaribles(exVariables, inVariables);

		//��ȡ������У�����ڵ�
		Element chkRules = null;
		tmpNode = basic.getFirstChild();
		while (! (tmpNode == null)) {
			if (tmpNode.getNodeName().equals("ChkRules")) {
				chkRules = (Element) tmpNode;
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}
		//��þ��߽����ص�ԭ����뼰����
		if(chkRules!=null)
		{
			getDecisionReasonList(allVariables, chkRules, actionResult);
        }

		// System.out.print(CDSEUtil.xmlToString(allVariables));

		actionResult = this.dealBadVariables(allVariables, inVariables,
											 actionResult);

		//-----------------���ڵ㣬���߽ڵ㣬���Խڵ����ȡ------------------------------

		tmpNode = basic.getFirstChild();
		Node policy = null;
		while (! (tmpNode == null)) {
			if (tmpNode.getNodeName().equals("Policy")) {
				policy = tmpNode;
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}

		Node strategy = null;
		tmpNode = basic.getFirstChild();
		while (! (tmpNode == null)) {
			if (tmpNode.getNodeName().equals("Stratege")) {
				strategy = tmpNode;
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}

		//------�������ڵ���ȡ���������ĳ�ʼ��-----------------------------------//
		Node itemsNode = strategy.getFirstChild();
		NodeList items = itemsNode.getChildNodes();
		Element item = (Element) items.item(0);
		String attrName = "";
		String cmpOpe = "";
		String cmpVal = "";
		String thisVal = "";
		String valType = "";
		String attrDesc = "";
		String attrString = "";
		String parentId = item.getAttribute("NodeId");
		String tmpStr = "";

		//----------------�����׼���ж��ʹ���-----------------------------------------------------
		this.setInclusionString("");
		if (!isInclusion(policy, allVariables)) {
			actionResult.setPoliceNo( ( (Element) policy).
									 getAttribute("PlsNo"));
			actionResult.setPoliceNa( ( (Element) policy).
									 getAttribute("PlsNa"));
			actionResult.setStrategyNo( ( (Element) strategy).
									   getAttribute("StgNo"));
			actionResult.setStrategyNa( ( (Element) strategy).
									   getAttribute("StgNa"));
			actionResult.setStrategyVer( ( (Element) strategy).
										getAttribute("StgVer"));
			actionResult.setActionId("-888");
			actionResult.setActionNa("��������������");

			actionResult.setAttributesString(this.getInclusionString());

			actionResult.setReasonCode("-1");
			actionResult.setReasonDescription("������������������ܾ�");

			actionResult.setInitialLine("-1");
			actionResult.setAllAttributes(allVariables);

			return actionResult;

		}

		//--------ͨ���������߼����㷵�صĶԲ߶���-----------------------------//
		boolean isRight = false;
		for (int i = 1; i < items.getLength(); i++) {
			item = (Element) items.item(i);
			if (item.getAttribute("ParentId").equals(parentId)) {
				attrName = item.getAttribute("VarNo");
				//-------��ȡ������м�����Ķ������ֵ-------------------------
				attrString = getVaribleString(attrName, allVariables);
				thisVal = attrString.substring(attrString.indexOf("{", 0) + 1,
											   attrString.indexOf("}", 0));
				tmpStr = attrString.substring(attrString.indexOf("}", 0) + 1);
				valType = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
										   tmpStr.indexOf("}", 0));
				tmpStr = tmpStr.substring(tmpStr.indexOf("}", 0) + 1);
				attrDesc = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
											tmpStr.indexOf("}", 0));
				//--------ѡ����ʵĲ�������ֱ֧�����ս��------------------------------------------------
				cmpOpe = item.getAttribute("CmpOpe");
				cmpVal = item.getAttribute("CmpVal");
				isRight = getCompareResult(thisVal, cmpVal, cmpOpe, valType);
				if (isRight) {
					actionId = item.getAttribute("ActionId");
					attributesString = attributesString + "{" + attrName + ": " +
						thisVal + " " + cmpOpe + " " + cmpVal + " }";
					if (actionId.length() > 0) {
						actionResult.setPoliceNo( ( (Element) policy).
												 getAttribute("PlsNo"));
						actionResult.setPoliceNa( ( (Element) policy).
												 getAttribute("PlsNa"));
						actionResult.setStrategyNo( ( (Element) strategy).
							getAttribute("StgNo"));
						actionResult.setStrategyNa( ( (Element) strategy).
							getAttribute("StgNa"));
						actionResult.setStrategyVer( ( (Element) strategy).
							getAttribute("StgVer"));
						actionResult.setActionId(actionId);
						actionResult.setActionNa(getActionNa(actionId, policy));
						actionResult.setAttributesString(attributesString);
						actionResult.setReasonCode(item.getAttribute(
							"ReasonCode"));
						actionResult.setReasonDescription(item.getAttribute(
							"ReasonDsc"));

						//ȡ�ò����ڵ�
						Node parameters = item.getFirstChild();
						ArrayList paraList = new ArrayList();
						StrategyParameter para = null;
						if (parameters != null) {
							NodeList paraItems = parameters.getChildNodes();
							String parameterString = ""; //��������ַ���
							String paraName = "";
							String paraDesc = "";
							String paraValue = "";
							String paraSpec = ""; //��������:����/����
							String paraType = "";
							for (int j = 0; j < paraItems.getLength(); j++) {
								para = new StrategyParameter();
								item = (Element) paraItems.item(j);
								paraName = item.getAttribute("ParaName");
								paraDesc = item.getAttribute("ParaDesc");
								paraSpec = item.getAttribute("ParaSpec");
								paraValue = item.getAttribute("ParaValue");
								paraType = item.getAttribute("ParaType");
								para.setDesc(paraDesc);
								para.setName(paraName);
								para.setType(paraType);
								if (paraSpec.equalsIgnoreCase("V")) {
									paraValue = getParameterValue(paraValue, allVariables);
								}
								para.setSpec(paraSpec);
								para.setValue(paraValue);
								paraList.add(para);
							}
							actionResult.setParaList(paraList);
						}

						break;
					} else {
						parentId = item.getAttribute("NodeId");
					}
				}
			}
		}

		//////////////// ��Ⱦ���ķ����洦����////////////////////////////////////////////////////
		String lineVarible = actionResult.getActionId();
		String initialLine = "0";
		if (lineVarible.indexOf("-") > 0) {
			lineVarible = lineVarible.substring(lineVarible.indexOf("-") + 1);
			attrString = this.getVaribleString(lineVarible, allVariables);
			initialLine = attrString.substring(attrString.indexOf("{", 0) + 1,
											   attrString.indexOf("}", 0));
		}
		actionResult.setInitialLine(initialLine);
		/////////////////////////////////////////////////////////////////////////////////////////////

		actionResult.setAllAttributes(allVariables);
		return actionResult;

	}

	/**
	 * ���������ڲ����������ⲿ�����ϳɱ���ȫ��
	 *
	 * @param exVariables �ⲿ�м��������
	 * @param inVariables �ڲ��������ϼ����㷨
	 * @return Element�� ��XML�ṹ����ı���ȫ��
	 * <p>������̣�
	 * <p>1.����ȫ����ʼ��Ϊ�ⲿ��������
	 * <p>2.��һ�����ڲ��������ŵ�����ȫ���С�
	 */

	private Element getAllVaribles(Element exVariables,
								   Element inVariables) {
		//----------------����ȫ����ʼ��Ϊ�ⲿ������---------------------------
		Element result = exVariables;
		Element variable = null;
		String valueString = "";
		String name = "";
		String type = "";
		String desc = "";
		Node varNode = inVariables.getFirstChild();
		Element attribute = null;

		//---------------��һ�����ڲ��������ŵ�����ȫ����-----------------------
		while ( (varNode != null) && (varNode instanceof Element)) {
			variable = (Element) varNode;
			name = variable.getAttribute("Name");
			type = variable.getAttribute("Type");
			desc = variable.getAttribute("Desc");
			valueString = getInternalVaribleValue(result, variable);

			attribute = result.getOwnerDocument().createElement("Variable");

			attribute.setAttribute("FldName", name);
			attribute.setAttribute("Value", valueString);
			attribute.setAttribute("FldType", type);
			attribute.setAttribute("FldSrc", "IN");
			attribute.setAttribute("FldDsc", desc);
			result.appendChild(attribute);

			varNode = varNode.getNextSibling();
		}

		return result;
	}

	/**
	 * ���ݱ����㷨�������ָ���ڲ�������ֵ
	 *
	 * @param allVariables ����˱�����ǰ���ɵı���ȫ����
	 * @param varible ��XML�ṹ������ڲ������������㷨����
	 * @return String ���ַ�����ʾ����ֵ��
	 */

	private String getInternalVaribleValue(Element allVariables,
										   Element variable) {
		String result = "";

		String varType = variable.getAttribute("Type");

		Element thisGroup = null;

		Node valueGroup = variable.getFirstChild();

		while (valueGroup != null) {
			if (isCalculate(valueGroup, allVariables)) {
				thisGroup = (Element) valueGroup;
				break;
			}
			valueGroup = valueGroup.getNextSibling();
		}

		Node values = null;

		if (thisGroup != null) {
			values = thisGroup.getLastChild();
			result = getVariableResultOfGroup(values, allVariables);
		}
		return result;
	}

	//////////// һ���ݹ�ĵ��ã�������������������ŵı��ʽ���� ////////////////////
	private String getVariableResultOfGroup(Node values, Element allVariables) {

		String result = "";

		String symble = "";
		String vName = "";
		String vType = "";
		String coeffcient = "";
		String parameter = "";
		String param1 = "";
		String param2 = "";
		String param3 = "";
		String function = "";
		double fResult = 0f;
		double tfResult = 0f;
		String tsResult = "";
		Node valueNode = null;
		Element value = null;
		boolean isFirst = true;

		valueNode = values.getFirstChild();

		while (valueNode != null) {

			value = (Element) valueNode;
			symble = value.getAttribute("Symble");

			if (valueNode.hasChildNodes()) {
				tsResult = getVariableResultOfGroup(valueNode, allVariables);
			} else {
				vName = value.getAttribute("VarName");
				vType = value.getAttribute("vType");
				coeffcient = value.getAttribute("Coeffcient");
				parameter = value.getAttribute("Parameter");
				param1 = value.getAttribute("Param1");
				param2 = value.getAttribute("Param2");
				param3 = value.getAttribute("Param3");
				function = value.getAttribute("Function");

				if (vType.equals("V")) {
					tsResult = getVaribleString(vName, allVariables);
					tsResult = tsResult.substring(tsResult.indexOf("{", 0) + 1,
												  tsResult.indexOf("}", 0));
				} else {
					tsResult = vName;
				}

				tsResult = getValueUnit(tsResult, vType, coeffcient,
										parameter, param1, param2, param3, function);
			}

			try {
				tfResult = Double.parseDouble(tsResult);
			} catch (Exception ex) {
				result = "-9991";
				return result; // �����ʽ����һ�������⣬������������-9991
			}

			if (isFirst) {
				fResult = tfResult;
			} else {
				if (symble.equals("+")) {
					fResult = fResult + tfResult;
				} else if (symble.equals("-")) {
					fResult = fResult - tfResult;
				} else if (symble.equals("*")) {
					fResult = fResult * tfResult;
				} else if (symble.equals("/")) {
					//�����ʽ����һ���б������������������-9992
					if ( ( -0.00001 < (tfResult - 0f)) && (0.00001 > (tfResult - 0f))) {
						result = "-9992";
						return result;
					}
					fResult = fResult / tfResult;
				}
			}
			isFirst = false;
			valueNode = valueNode.getNextSibling();
		}
		result = String.valueOf(fResult);
		return result;
	}

	/**
	 * ����ָ���ڲ�������һ�����ʽ��һ����ֵ��λ��ֵ
	 *
	 * @param Value �Ա������ַ�����
	 * @param vType �Ա������������͡�
	 * @param coeffcient ���ݵ�Ԫ��ϵ����
	 * @param function ���ݵ�Ԫ�ĺ�����
	 * @param parameter �����еĲ�����
	 * @return String ���ַ�����ʾ���������ֵ��
	 */
	private String getValueUnit(String Value, String vType,
								String coeffcient, String parameter,
								String param1, String param2, String param3,
								String function) {
		String result = "";
		double fResult = 0f;
		if ( (function.equals("X()")) || ("".equals(function))) {
			try {
				fResult = Double.parseDouble(Value);
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("ABS")) {
			try {
				fResult = Math.abs(Double.parseDouble(Value));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("EXP")) {
			try {
				fResult = Math.exp(Double.parseDouble(Value));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("LOG")) {
			try {
				fResult = Math.log(Double.parseDouble(Value));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("POWER")) {
			try {
				fResult = Math.pow(Double.parseDouble(Value), Double.parseDouble(parameter));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("RAND")) {
			try {
				fResult = Math.random(); //*Double.parseDouble(Value);
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("ROUND")) {
			try {
				fResult = Math.round(Math.pow(10f, Double.parseDouble(parameter)) *
									 Double.parseDouble(Value)) *
					Math.pow(10f, -Double.parseDouble(parameter));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("SIGN")) {
			try {
				if (Math.abs(Double.parseDouble(Value)) < 0.000001f) {
					fResult = 0f;
				} else if (Double.parseDouble(Value) > 0f) {
					fResult = 1f;
				} else {
					fResult = -1f;
				}

			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("SQUARE")) {
			try {
				fResult = (Double.parseDouble(Value)) * (Double.parseDouble(Value));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		} else if (function.equals("SQRT")) {
			try {
				fResult = Math.sqrt(Double.parseDouble(Value));
			} catch (Exception ex) {
				ex.printStackTrace();
				return result;
			}
		}
		try {
			fResult = Double.parseDouble(coeffcient) * fResult;
		} catch (Exception ex) {
			ex.printStackTrace();
			return result;
		}

		result = String.valueOf(fResult);

		return result;
	}

	/**
	 * �ж��ڲ������Ƿ���ĳһȡֵ�������㡣
	 *
	 * @param valueGroup ���жϵ�ȡֵ�顣
	 * @param varible ����˱�����ǰ���ɵı���ȫ����
	 * @return true �ñ����ô�ȡֵ�������㣻
	 * <p>false �ñ������ô�ȡֵ�������㡣
	 */


	////////// �����ǵ������ŵ�֧�֣��÷����ش��޸ġ�/////////////////////
	//////////  2006-3-13 richard //////////////////////////////////

	private boolean isCalculate(Node valueGroup,
								Element allVariables) {
		boolean result = true;
		Node conditions = valueGroup.getFirstChild();
		if (conditions.getNodeName().equals("Conditions")) { //��Ϊ����ֱ�ӷ���true
			result = getConditionResult(conditions, allVariables,
										"compLog", "VarName", "compOper", "vType", "compVal", "CompAttr");
		}
		return result;
	}

	////////////һ���ݹ�ĵ��ã�������������������ŵ���������////////////////////
	private boolean getConditionResult(Node con,
									   Element allVariables,
									   String logStr,
									   String nameStr,
									   String operStr,
									   String typeStr,
									   String valStr,
									   String attrStr) {
		boolean result = true;
		boolean tmpResult = false;

		String varName = "";
		String vType = "";
		String compLog = "";
		String compOper = "";
		String compVal = "";
		String compAttr = "";
		String variableString = "";
		String valueString = "";

		Element eCon = null;
		int i = -1;
		Node con1 = con.getFirstChild();
		while (con1 != null) { //��Ϊ����ֱ�ӷ���true
			i = i + 1;
			eCon = (Element) con1;
			compLog = eCon.getAttribute(logStr);
			if ("".equals(compLog)) {
				compLog = "and";
			}
			if (con1.hasChildNodes()) {
				tmpResult = getConditionResult(con1, allVariables, logStr, nameStr, operStr, typeStr, valStr, attrStr);
			} else {
				varName = eCon.getAttribute(nameStr);
				vType = eCon.getAttribute(typeStr);
				compOper = eCon.getAttribute(operStr);
				compVal = eCon.getAttribute(valStr);
				compAttr = eCon.getAttribute(attrStr);
				if (! ("".equals(compAttr))) {
					variableString = getVaribleString(compAttr, allVariables);
					compVal = variableString.substring(variableString.indexOf("{", 0) + 1, variableString.indexOf("}", 0));
				}
				variableString = getVaribleString(varName, allVariables);
				valueString = variableString.substring
					(variableString.indexOf("{", 0) + 1, variableString.indexOf("}", 0));
				tmpResult = getCompareResult(valueString, compVal, compOper, vType);

				////////////////�������׼�����Ĳ������г���ԭ��////////////////////////////
				if ( (!tmpResult) && (nameStr.equalsIgnoreCase("VARNO"))) {
					String tmpString = variableString.substring(variableString.
						indexOf("}", 0) + 1);
					tmpString = tmpString.substring(tmpString.indexOf("}", 0) + 1);

					this.setInclusionString(this.getInclusionString() +
											"{" + tmpString + compOper + compVal + "}");
				}
				///////////////////////////////////////////////////////////////////////
			}
			if (i == 0) {
				result = tmpResult;
			} else {
				if (compLog.equalsIgnoreCase("OR")) {
					result = result || tmpResult;
				} else {
					result = result && tmpResult;
				}
			}
			con1 = con1.getNextSibling();
		}
		return result;
	}

	/**
	 * �ڱ����������ϻ�ȡָ��������ֵ��
	 *
	 * @param varName �������ơ�
	 * @param allVariables ��XML�ṹ����ı�������
	 * @return String ���ַ�����ʾ��{����ֵ}{��������}{��������}��
	 */

	private String getVaribleString(String varName,
									Element allVariables) {
		String result = "{-1}{N}{None}";
		Node varNode = allVariables.getFirstChild();

		Element variable = null;

		String fldName = "";
		String value = "";
		String fldType = "";
		String fldSrc = "";
		String fldDsc = "";

		while (varNode != null) {
			if (varNode instanceof Element) {
				variable = (Element) varNode;
				fldName = variable.getAttribute("FldName");
				if (fldName.trim().equalsIgnoreCase(varName.trim())) {
					value = variable.getAttribute("Value");
					fldType = variable.getAttribute("FldType");
					fldSrc = variable.getAttribute("FldSrc");
					fldDsc = variable.getAttribute("FldDsc");
					result = "{" + value + "}{" + fldType + "}{" + fldDsc +
						"}";
					break;
				}
			}
			varNode = varNode.getNextSibling();
		}
		return result;
	}

	/**
	 * ��ȡ�����ȽϽ��
	 *
	 * @param thisVal   ����ֵ���ַ����
	 * @param cmpVal    �Ƚ�ֵ���ַ����
	 * @param cmpOpe    ����ֵ�ͱȽ�ֵ�ıȽϷ�
	 * @param valType   ����������
	 *
	 * @return true     ����ֵ�ͱȽ�ֵ����ָ���ıȽϹ�ϵ
	 *<p>         false    ����ֵ�ͱȽ�ֵ������ָ���ıȽϹ�ϵ
	 */
	private boolean getCompareResult(String thisVal, String cmpVal,
									 String cmpOpe, String valType) {
		String sVal = "";
		String sCmp = "";
		double nVal = 0f;
		double nCmp = 0f;

		if (valType.equalsIgnoreCase("S")) {
			sVal = thisVal;
			sCmp = cmpVal;

		} else {
			// Huang shaicheng changed 2004-4-9
			try {
				nVal = Double.parseDouble(thisVal);
				nCmp = Double.parseDouble(cmpVal);
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		if (cmpOpe.equalsIgnoreCase("<")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal < nCmp) {
					return true;
				} else {
					return false;
				}
			}
		}
		if (cmpOpe.equalsIgnoreCase("<=")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal <= nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else if (cmpOpe.equalsIgnoreCase(">=")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal >= nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else if (cmpOpe.equalsIgnoreCase(">")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal > nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else if (cmpOpe.equalsIgnoreCase("<>")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal != nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else if (cmpOpe.equalsIgnoreCase("==")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal == nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else if (cmpOpe.equalsIgnoreCase("=")) {
			if (valType.equalsIgnoreCase("S")) {
				return compareString(sVal, sCmp, cmpOpe);
			} else {
				if (nVal == nCmp) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private boolean compareString(String a, String b, String ope) {
		boolean result = true;
		char aChar = ' ';
		char bChar = ' ';
		int al = a.length();
		int bl = b.length();
		int l = Math.min(al, bl);
		for (int i = 1; i < l + 1; i++) {
			aChar = a.charAt(i);
			bChar = b.charAt(i);
			if (ope.equals("==")) {
				if (aChar != bChar) {
					result = false;
					break;
				}
			} else if (ope.equals(">")) {
				if (aChar <= bChar) {
					result = false;
					break;
				}
			} else if (ope.equals("<")) {
				if (aChar >= bChar) {
					result = false;
					break;
				}
			} else if (ope.equals("<=")) {
				if (aChar > bChar) {
					result = false;
					break;
				}
			} else if (ope.equals(">=")) {
				if (aChar < bChar) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * ͨ��������Ŀ��Ż�ȡ������Ŀ����
	 *
	 * @param actionId  ������Ŀ���
	 * @param policy    XML�ṹ�е����߽ڵ�
	 *
	 * @return ������Ŀ����
	 */

	private static String getActionNa(String actionId, Node policy) {
		String actionNa = "��";
		Node items = policy.getFirstChild();
		Element item = null;
		NodeList itemList = items.getChildNodes();
		for (int i = 0; i < itemList.getLength(); i++) {
			item = (Element) itemList.item(i);
			if (item.getAttribute("ItemNo").equals(actionId)) {
				actionNa = item.getAttribute("ItemNa");
				break;
			}
		}
		return actionNa;
	}

	////////////////����������������һ���б𷽷�///////////////////////////////////
	private boolean isInclusion(Node policy, Element allVariables) {
		boolean result = true;
		Node inclusion = null;
		Node tmpNode = null;
		NodeList tmpList = policy.getChildNodes();
		for (int i = 0; i < tmpList.getLength(); i++) {
			tmpNode = tmpList.item(i);
			if (tmpNode.getNodeName().equals("Inclusion")) {
				inclusion = tmpNode;
				break;
			}
		}
		if (inclusion != null) {
			result = getConditionResult(inclusion, allVariables,
										"LogOpe", "VarNo", "CmpOpe", "vType", "CmpVal", "CompAttr");
		}
		return result;
	}

	/**
	 * �ж��Ƿ�������������������
	 *
	 * @param policy XML�ṹ�е����߽ڵ㡣
	 * @param allVariables ��XML�ṹ����ı�������
	 * @return true ��������������
	 * <p>false ����������������
	 */

	private String getInclusion(Node policy, Element allVariables) {
		String resultString = "";
		String tmpString = "";
		boolean result = true;
		boolean tmpResult = false;
		Node inclusion = null;
		Node tmpNode = null;
		Element condition = null;

		String varName = "";
		String compLog = "";
		String compOper = "";
		String compVal = "";
		String variableString = "";
		String valueString = "";

		NodeList tmpList = policy.getChildNodes();
		for (int i = 0; i < tmpList.getLength(); i++) {
			tmpNode = tmpList.item(i);
			if (tmpNode.getNodeName().equals("Inclusion")) {
				inclusion = tmpNode;
				break;
			}
		}
		if (inclusion != null) {
			tmpList = inclusion.getChildNodes();
			for (int i = 0; i < tmpList.getLength(); i++) {
				tmpNode = tmpList.item(i);
				if (tmpNode.getNodeName().equals("Condition")) {
					condition = (Element) tmpNode;
					varName = condition.getAttribute("VarNo");
					compLog = condition.getAttribute("LogOpe");
					if (i == 0) {
						if (compLog.equalsIgnoreCase("and")) {
							result = true;
						} else {
							result = false;
						}
					}
					compOper = condition.getAttribute("CmpOpe");
					compVal = condition.getAttribute("CmpVal");
					variableString = getVaribleString(varName, allVariables);
					valueString = variableString.substring(variableString.
						indexOf("{",
								0) + 1, variableString.indexOf("}", 0));
					tmpResult = getCompareResult(valueString, compVal,
												 compOper,
												 "N");

					//////////////////  2004-4-1 richard  ////////////////////////
					if (!tmpResult) {
						tmpString = variableString.substring(variableString.
							indexOf("}", 0) + 1);
						tmpString = tmpString.substring(tmpString.indexOf("}",
							0) + 1);
						resultString = resultString + "{" +
							tmpString.substring(tmpString.
												indexOf("{", 0) + 1,
												tmpString.indexOf("}", 0))
							+ compOper + compVal + "}";

					}

					//////////////////////////////////////////////////////////////

					if (compLog.equalsIgnoreCase("OR")) {
						result = result || tmpResult;
					} else {
						result = result && tmpResult;
					}
					//if (!result) {
					//    break;
					//}
				}
			}
		}

		/////////////////////  2004-4-1 richard ////////////////////////////////
		if (!result) {
			resultString = "{0}" + resultString;
		} else {
			resultString = "{1}" + resultString;

		}
		////////////////////////////////////////////////////////////////////////

		return resultString;
	}

	/**
	 * �����������Թ����м�⵽������ì�ܱ������б�ǡ�
	 * @param allVariables ���б����ļ���
	 * @param inVariables �����ڲ������ļ���
	 * @param actionResult ����������
	 * @return actionResult ����������������Ǻ�Ĳ�����������
	 */
	private ActionResult dealBadVariables(Element allVariables,
										  Element inVariables,
										  ActionResult actionResult) {

		ActionResult result = actionResult;

		double fResult = 0f;

		String theVarName = "AT4200";

		String sResult = this.getVaribleString(theVarName, allVariables);

		sResult = sResult.substring(sResult.indexOf("{", 0) + 1,
									sResult.indexOf("}", 0));
		// Huang shaicheng changed 2004-4-9
		try {
			fResult = Double.parseDouble(sResult);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (fResult < 0.5f) {
			result.setBadNum(0);
			result.setBadString(" ");
			return result;
		}

		Element theVariable = null;
		Element tmpVariable = null;
		Element theGroup = null;

		Node values = null;
		Node valueNode = null;
		Element value = null;

		/////////////////////////////// �ҵ�ָ���ı���////////////////////////////////////
		Node varNode = inVariables.getFirstChild();

		while ( (varNode != null) && (varNode instanceof Element)) {
			tmpVariable = (Element) varNode;
			if (tmpVariable.getAttribute("Name").equals(theVarName)) {
				theVariable = tmpVariable;
				break;
			}
			varNode = varNode.getNextSibling();
		}

		if (theVariable == null) {
			result.setBadNum(0);
			result.setBadString(" ");
			return result;
		}
		///////////////////////////////// �ҵ�ָ������///////////////////////////////////////
		theGroup = (Element) theVariable.getFirstChild();
		if (theGroup == null) {
			result.setBadNum(0);
			result.setBadString(" ");
			return result;
		}
		//////////////////////////////// �ҵ�ָ����ȡֵ�� //////////////////////////////////

		values = theGroup.getLastChild();
		if (values == null) {
			result.setBadNum(0);
			result.setBadString(" ");
			return result;
		}

		valueNode = values.getFirstChild();
		String vName = "";
		String sV = "";
		double fV = 0f;
		int num = 0;
		sResult = "";
		while (valueNode != null) {
			value = (Element) valueNode;
			vName = value.getAttribute("VarName");

			sV = this.getVaribleString(vName, allVariables);
			vName = sV.substring(sV.indexOf("}", 0) + 1);
			sV = sV.substring(sV.indexOf("{", 0) + 1,
							  sV.indexOf("}", 0));
			vName = vName.substring(vName.indexOf("}", 0) + 1);
			vName = vName.substring(vName.indexOf("{", 0) + 1, vName.indexOf("}", 0));

			// Huang shaicheng changed 2004-4-9
			try {
				fV = Double.parseDouble(sV);
			} catch (Exception ex) {
				//fV = 0f;
				fV = 1f; //richard2004-4-9  ���쳣��ٶ��ñ����ǲ����߼���
			}
			if (fV > 0.5f) {
				num = num + 1;

				if (sResult.equals("")) {
					sResult = "{" + vName + "}";
				} else {
					sResult = sResult + "{" + vName + "}";
				}
			}
			valueNode = valueNode.getNextSibling();
		}
		result.setBadNum(num);
		result.setBadString(sResult);
		return result;
	}

	/**
	 * ��ȡ���߽����ص�ԭ��������
	 * @param allVariables Element
	 * @param chkRules Element
	 * @param actionResult ActionResult
	 * @return boolean
	 */
	private void getDecisionReasonList(Element allVariables,
									   Element chkRules,
									   ActionResult actionResult) {
		String id = null;
		String varNo = null;
		String varValue = null;
		String cmpOpe = null;
		String cmpVal = null;
		String warnDesc = null;
		String warnDescStr = "";
		Element ruleElement = null;
		Node ruleNode = chkRules.getFirstChild();

		while ( (ruleNode != null) && (ruleNode instanceof Element)) {
			ruleElement = (Element) ruleNode;
			id = ruleElement.getAttribute("Id");
			varNo = ruleElement.getAttribute("varNo");
			cmpOpe = ruleElement.getAttribute("cmpOpe");
			cmpVal = ruleElement.getAttribute("cmpVal");
			warnDesc = ruleElement.getAttribute("warnDesc");
			//��������Ӧ��ֵ
			varValue = getParameterValue(varNo, allVariables);
			//�Ƚϱ���ֵ�Ƿ�ﵽԤ��ֵ
			if (compareNumVar(varValue, cmpVal, cmpOpe)) {
				if ("".equals(warnDescStr)) {
					warnDescStr = warnDesc;
				} else {
					warnDescStr += "��" + warnDesc;
				}
			}

            ruleNode = ruleNode.getNextSibling();
		}

		actionResult.setWarnDescStr(warnDescStr);
	}

	/**
	 * �Ƚ��ַ�������ֵ
	 * @param varA String
	 * @param varB String
	 * @param ope String
	 * @return boolean
	 */
	private boolean compareNumVar(String varA, String varB, String ope) {
		boolean result = false;
		double dVarA = 0;
		double dVarB = 0;

		//������ֵת��Ϊ��ֵ
		try {
			dVarA = Double.parseDouble(varA);
			dVarB = Double.parseDouble(varB);
		} catch (Exception e) {
			return false;
		}

		if (">=".equals(ope)) {
			if (dVarA >= dVarB) {
				result = true;
			}
		} else if (">".equals(ope)) {
			if (dVarA > dVarB) {
				result = true;
			}
		} else if ("=".equals(ope)) {
			if (dVarA == dVarB) {
				result = true;
			}
		} else if ("<=".equals(ope)) {
			if (dVarA <= dVarB) {
				result = true;
			}
		} else if ("<".equals(ope)) {
			if (dVarA < dVarB) {
				result = true;
			}
		} else if ("<>".equals(ope)) {
			if (dVarA != dVarB) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * ��ȡ���õ��ⲿ�������ϣ����ڸ������Ĵ���ɾ�������ĸ�������Ϣ����
	 * @param srcVaribles ������ⲿ����ȫ����XML��ʽ��
	 * @param flag ��������ţ�������������0)��
	 * @return Element ���õ��ⲿ�������ϣ�XML��ʽ����
	 */

	public Element getUseableVaribles(Element srcVaribles, String flag) {
		Element result = srcVaribles;
		Node varNode = srcVaribles.getFirstChild();

		Element variable = null;

		String fldSrc = "";

		while (varNode != null) {
			if (varNode instanceof Element) {
				variable = (Element) varNode;
				fldSrc = variable.getAttribute("FldSrc");
				if (! ( (fldSrc.trim().equals("EX")) || (fldSrc.trim().equals(flag)))) {
					variable.setAttribute("FldName", "~!@#$%");
				}
			}
			varNode = varNode.getNextSibling();
		}

		return result;
	}

	private void jbInit() throws Exception {
	}

	public void setInclusionString(String inclusionString) {
		this.inclusionString = inclusionString;
	}

	public String getInclusionString() {
		return inclusionString;
	}

	private String getParameterValue(String varName,
									 Element allVariables) {

		Node varNode = allVariables.getFirstChild();

		Element variable = null;

		String fldName = "";
		String value = "";

		while (varNode != null) {
			if (varNode instanceof Element) {
				variable = (Element) varNode;
				fldName = variable.getAttribute("FldName");

				if (fldName.trim().equalsIgnoreCase(varName.trim())) {
					value = variable.getAttribute("Value");
					break;
				}
			}
			varNode = varNode.getNextSibling();
		}
		return value;
	}
}
