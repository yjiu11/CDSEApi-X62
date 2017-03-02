package com.boc.cdse;

import org.w3c.dom.*;
import java.util.*;

/**
 * <P><Code>StrategyAnalyzer</Code>类为策略执行接口，通过读取策略指令文件结合传入的中间变量
 * 取得策略结果。<Code>getStrategyResult</Code>为决策结果的获取方法。
 *
 * <P>Copyright:版权 (c) 2004 </P>
 * <P>Company:首航财务顾问有限公司</P>
 * @author: Richard
 * @version 2.0
 * @see com.boc.cdse.Attributes
 * @see com.entity.ActionResult
 */

public class StrategyAnalyzer implements java.io.Serializable {
	private String inclusionString;
	/**
	 * 获取决策结果
	 *
	 * @param attributes_0 通过数据处理过程产生的外部中间变量
	 * @param strategyFile 该次决策所遵循的策略规则
	 * @param flag 变量来源标志（用于区分主卡或特定的附属卡）
	 * @return actionResult 决策结果对象
	 * <p>处理过程：
	 * <p>1.策略结果的初始化。
	 * <p>2.提取外部变量集合。
	 * <p>3.提取内部变量集合及其算法。
	 * <P>4.计算所有内部变量并与外部变量合成变量全集。
	 * <P>5.根节点，政策节点，策略节点的提取。
	 * <p>6.策略树节点提取，各参数的初始化。
	 * <p>7.入组标准的判定和处理。
	 * <p>8.通过策略树逻辑计算返回的对策对象。
	 * <p>&nbsp;&nbsp;8-1.获取传入的中间变量的定义和数值。
	 * <p>&nbsp;&nbsp;8-2.选择合适的策略树分支直到最终结果。
	 * <p>9.额度矩阵处理。
	 */

	public ActionResult getStrategyResult(PersonalCreditCardAttributes
										  attributes_0,
										  Strategy strategyFile,
										  String flag) {

		//----------------策略结果的初始化----------------------------------------//
		ActionResult actionResult = new ActionResult();
		String actionId = "无";
		String actionNa = "无";
		String attributesString = "";

		Element basic = strategyFile.getStrategy().getDocumentElement();

		Node tmpNode = null;

		PersonalCreditCardAttributes attributes = (PersonalCreditCardAttributes) attributes_0.clone();

		//----------------提取外部变量集合--------------------------------------------//

		Element exVariables = getUseableVaribles(attributes.getDocument().getDocumentElement(), flag);

		//----------------提取内部变量集合及其算法--------------------------------//
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

		//----------------计算所有内部变量并与外部变量合成变量全集-----------------//
		Element allVariables = getAllVaribles(exVariables, inVariables);

		//获取策略中校验规则节点
		Element chkRules = null;
		tmpNode = basic.getFirstChild();
		while (! (tmpNode == null)) {
			if (tmpNode.getNodeName().equals("ChkRules")) {
				chkRules = (Element) tmpNode;
				break;
			}
			tmpNode = tmpNode.getNextSibling();
		}
		//获得决策结果相关的原因代码及描述
		if(chkRules!=null)
		{
			getDecisionReasonList(allVariables, chkRules, actionResult);
        }

		// System.out.print(CDSEUtil.xmlToString(allVariables));

		actionResult = this.dealBadVariables(allVariables, inVariables,
											 actionResult);

		//-----------------根节点，政策节点，策略节点的提取------------------------------

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

		//------策略树节点提取，各参数的初始化-----------------------------------//
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

		//----------------入组标准的判定和处理-----------------------------------------------------
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
			actionResult.setActionNa("不满足入组条件");

			actionResult.setAttributesString(this.getInclusionString());

			actionResult.setReasonCode("-1");
			actionResult.setReasonDescription("因入组条件不满足而拒绝");

			actionResult.setInitialLine("-1");
			actionResult.setAllAttributes(allVariables);

			return actionResult;

		}

		//--------通过策略树逻辑计算返回的对策对象-----------------------------//
		boolean isRight = false;
		for (int i = 1; i < items.getLength(); i++) {
			item = (Element) items.item(i);
			if (item.getAttribute("ParentId").equals(parentId)) {
				attrName = item.getAttribute("VarNo");
				//-------获取传入的中间变量的定义和数值-------------------------
				attrString = getVaribleString(attrName, allVariables);
				thisVal = attrString.substring(attrString.indexOf("{", 0) + 1,
											   attrString.indexOf("}", 0));
				tmpStr = attrString.substring(attrString.indexOf("}", 0) + 1);
				valType = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
										   tmpStr.indexOf("}", 0));
				tmpStr = tmpStr.substring(tmpStr.indexOf("}", 0) + 1);
				attrDesc = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
											tmpStr.indexOf("}", 0));
				//--------选择合适的策略树分支直到最终结果------------------------------------------------
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

						//取得参数节点
						Node parameters = item.getFirstChild();
						ArrayList paraList = new ArrayList();
						StrategyParameter para = null;
						if (parameters != null) {
							NodeList paraItems = parameters.getChildNodes();
							String parameterString = ""; //结果参数字符串
							String paraName = "";
							String paraDesc = "";
							String paraValue = "";
							String paraSpec = ""; //参数类型:变量/常量
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

		//////////////// 额度矩阵的非正规处理方法////////////////////////////////////////////////////
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
	 * 计算所有内部变量并与外部变量合成变量全集
	 *
	 * @param exVariables 外部中间变量集合
	 * @param inVariables 内部变量集合及其算法
	 * @return Element： 以XML结构保存的变量全集
	 * <p>处理过程：
	 * <p>1.变量全集初始化为外部变量集。
	 * <p>2.逐一计算内部变量并放到变量全集中。
	 */

	private Element getAllVaribles(Element exVariables,
								   Element inVariables) {
		//----------------变量全集初始化为外部变量集---------------------------
		Element result = exVariables;
		Element variable = null;
		String valueString = "";
		String name = "";
		String type = "";
		String desc = "";
		Node varNode = inVariables.getFirstChild();
		Element attribute = null;

		//---------------逐一计算内部变量并放到变量全集中-----------------------
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
	 * 根据变量算法定义计算指定内部变量的值
	 *
	 * @param allVariables 计算此变量以前生成的变量全集。
	 * @param varible 以XML结构定义的内部变量（包括算法）。
	 * @return String 以字符串表示的数值。
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

	//////////// 一个递归的调用，用来处理包含左右括号的表达式定义 ////////////////////
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
				return result; // 若表达式中任一项有问题，则整体结果返回-9991
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
					//若表达式中任一项有被零除，则整体结果返回-9992
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
	 * 计算指定内部变量在一个表达式中一个数值单位的值
	 *
	 * @param Value 自变量的字符串表达。
	 * @param vType 自变量的数据类型。
	 * @param coeffcient 数据单元的系数。
	 * @param function 数据单元的函数。
	 * @param parameter 函数中的参数。
	 * @return String 以字符串表示的因变量数值。
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
	 * 判断内部变量是否用某一取值组来计算。
	 *
	 * @param valueGroup 需判断的取值组。
	 * @param varible 计算此变量以前生成的变量全集。
	 * @return true 该变量用此取值组来计算；
	 * <p>false 该变量不用此取值组来计算。
	 */


	////////// 　考虑到对括号的支持，该方法重大修改　/////////////////////
	//////////  2006-3-13 richard //////////////////////////////////

	private boolean isCalculate(Node valueGroup,
								Element allVariables) {
		boolean result = true;
		Node conditions = valueGroup.getFirstChild();
		if (conditions.getNodeName().equals("Conditions")) { //若为空则直接返回true
			result = getConditionResult(conditions, allVariables,
										"compLog", "VarName", "compOper", "vType", "compVal", "CompAttr");
		}
		return result;
	}

	////////////一个递归的调用，用来处理包含左右括号的条件定义////////////////////
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
		while (con1 != null) { //若为空则直接返回true
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

				////////////////对入组标准条件的不满足列出其原因////////////////////////////
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
	 * 在变量集基础上获取指定变量的值。
	 *
	 * @param varName 变量名称。
	 * @param allVariables 以XML结构定义的变量集。
	 * @return String 以字符串表示的{变量值}{变量类型}{变量描述}。
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
	 * 获取变量比较结果
	 *
	 * @param thisVal   变量值的字符表达
	 * @param cmpVal    比较值的字符表达
	 * @param cmpOpe    变量值和比较值的比较符
	 * @param valType   变量的类型
	 *
	 * @return true     变量值和比较值满足指定的比较关系
	 *<p>         false    变量值和比较值不满足指定的比较关系
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
	 * 通过政策项目编号获取政策项目描述
	 *
	 * @param actionId  政策项目编号
	 * @param policy    XML结构中的政策节点
	 *
	 * @return 政策项目描述
	 */

	private static String getActionNa(String actionId, Node policy) {
		String actionNa = "无";
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

	////////////////政策入组条件的另一种判别方法///////////////////////////////////
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
	 * 判断是否满足政策入组条件。
	 *
	 * @param policy XML结构中的政策节点。
	 * @param allVariables 以XML结构定义的变量集。
	 * @return true 满足入组条件；
	 * <p>false 不满足入组条件。
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
	 * 策略输出结果对过程中检测到的自相矛盾变量进行标记。
	 * @param allVariables 所有变量的集合
	 * @param inVariables 所有内部变量的集合
	 * @param actionResult 策略输出结果
	 * @return actionResult 经过“坏”变量标记后的策略输出结果。
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

		/////////////////////////////// 找到指定的变量////////////////////////////////////
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
		///////////////////////////////// 找到指定的组///////////////////////////////////////
		theGroup = (Element) theVariable.getFirstChild();
		if (theGroup == null) {
			result.setBadNum(0);
			result.setBadString(" ");
			return result;
		}
		//////////////////////////////// 找到指定的取值组 //////////////////////////////////

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
				fV = 1f; //richard2004-4-9  出异常则假定该变量是不合逻辑的
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
	 * 获取决策结果相关的原因及其描述
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
			//变量所对应的值
			varValue = getParameterValue(varNo, allVariables);
			//比较变量值是否达到预期值
			if (compareNumVar(varValue, cmpVal, cmpOpe)) {
				if ("".equals(warnDescStr)) {
					warnDescStr = warnDesc;
				} else {
					warnDescStr += "；" + warnDesc;
				}
			}

            ruleNode = ruleNode.getNextSibling();
		}

		actionResult.setWarnDescStr(warnDescStr);
	}

	/**
	 * 比较字符串的数值
	 * @param varA String
	 * @param varB String
	 * @param ope String
	 * @return boolean
	 */
	private boolean compareNumVar(String varA, String varB, String ope) {
		boolean result = false;
		double dVarA = 0;
		double dVarB = 0;

		//将变量值转化为数值
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
	 * 获取可用的外部变量集合－用于附属卡的处理（删除其它的附属卡信息）。
	 * @param srcVaribles 传入的外部变量全集（XML格式）
	 * @param flag 附属卡序号（若是主卡传入0)。
	 * @return Element 可用的外部变量集合（XML格式）。
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
