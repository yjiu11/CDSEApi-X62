package com.boc.cdse;

import java.util.*;
import java.io.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.*;

//import org.apache.crimson.tree.XmlDocument;

/**
 * <P><Code>StrategyAnalyzer</Code>类为策略执行接口，通过读取策略指令文件结合传入的中间变量
 * 取得策略结果。<Code>getStrategyResult</Code>为决策结果的获取方法。
 *
 * <P>Copyright:版权 (c) 2004 </P>
 * <P>Company:首航财务顾问有限公司</P>
 * @author: Richard
 * @version 1.0
 * @see com.boc.cdse.Attributes
 * @see com.entity.ActionResult
 */

public class StrategyAnalyzerA implements java.io.Serializable {
	private String inclusionString;

	public void setInclusionString(String inclusionString) {
		this.inclusionString = inclusionString;
	}

	public String getInclusionString() {
		return inclusionString;
	}

	/**
	 * 获取决策结果
	 *
	 * @param attribute 通过数据处理过程产生的外部中间变量
	 * @param strategyFile 该次决策所遵循的策略规则
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
	public ActionResult getStrategyResultA(ArrayList
										   exVariables,
										   Strategy strategy, String flag) {
		//ActionResult actionResult = new ActionResult();
		ActionResult actionResultA = new ActionResult();
		XMLStrategy xmlStrategy = null;
		XMLInternal xmlInternal = null;
		XMLChkRule xmlChkRule = null;
		ArrayList allVariables = null;
		ArrayList usedExVariables=null;

		try {
			//----------------策略结果的初始化----------------------------------------//
			xmlStrategy = strategy.getXmlStrategy();
			xmlInternal = strategy.getXmlInternal();
			xmlChkRule = strategy.getXmlChkRule();
			//获取当前使用的外部变量
			 usedExVariables= getUseableVaribles(exVariables, flag);

			//生成执行策略所需的全部变量
			allVariables = getAllVariblesA(usedExVariables, xmlInternal);
//			System.out.println("========================All Variable=========================");
//			Enumeration aa=allVariables.elements();
//			while(aa.hasMoreElements())
//			{
//				VariableBean aaa=(VariableBean)aa.nextElement();
//				System.out.println("Name:"+aaa.getFldName()+",value="+aaa.getValue()+",desc:"+aaa.getFldDsc());
//			}
//			System.out.println("========================All Variable=========================");

			//获得决策结果相关的原因代码及描述
			getDecisionReasonList(allVariables, xmlChkRule, actionResultA);

			//    actionResultA = calculateStrategyResultA(allVariables, strategyDoc)

//			String actionId = "无";
//			String actionNa = "无";
//			String attributesString = "";
			String attributeString = "";

			//入组标准的判定和处理
			this.inclusionString = "";
			if (isInclusionA(xmlStrategy.policy, allVariables) == false) {
				actionResultA.setPoliceNo(xmlStrategy.PlsNo);
				actionResultA.setPoliceNa(xmlStrategy.policy.PlsNa);
				actionResultA.setStrategyNo(xmlStrategy.StgNo);
				actionResultA.setStrategyNa(xmlStrategy.StgNa);
				actionResultA.setStrategyVer(xmlStrategy.StgVer);
				actionResultA.setActionId("-888");
				actionResultA.setActionNa("不满足入组条件");
				actionResultA.setAttributesString(this.inclusionString);
				actionResultA.setReasonCode("-1");
				actionResultA.setReasonDescription("因入组条件不满足而拒绝");
				actionResultA.setInitialLine("-1");
				actionResultA.setAllAttributesA(allVariables);
				return actionResultA;
			}

			//通过策略树逻辑计算返回的对策对象
			XMLStrategyItem xmlStrategyItem = null;
			if (xmlStrategy.Items.size() > 0) {
				xmlStrategyItem = (XMLStrategyItem) xmlStrategy.Items.get(0);
			}
			XMLStrategyItem actionStrategyItem = null;
			if (xmlStrategyItem != null) {
//				System.out.println("Strategy No:" + xmlStrategy.StgNo);
				actionStrategyItem = getActionStrategyItemA(attributeString, xmlStrategyItem, allVariables);
			}
			if (actionStrategyItem != null) {
				//获取传入的中间变量的定义和数值
				actionResultA.setActionId(actionStrategyItem.ActionId);
				actionResultA.setActionNa(actionStrategyItem.ItemDesc);
				actionResultA.setPoliceNo(xmlStrategy.PlsNo);
				actionResultA.setPoliceNa(xmlStrategy.policy.PlsNa);
				actionResultA.setStrategyNo(xmlStrategy.StgNo);
				actionResultA.setStrategyNa(xmlStrategy.StgNa);
				actionResultA.setStrategyVer(xmlStrategy.StgVer);
				actionResultA.setAttributesString(attributeString);
				actionResultA.setReasonCode(actionStrategyItem.ReasonCode);
				actionResultA.setReasonDescription(actionStrategyItem.ReasonDsc);

				//取得参数节点
				ArrayList paraList = new ArrayList();
				StrategyParameter para = null;
				String paraValue = "";

				XMLStrategyParameter xmlStrategyParameter = null;

				for (int i = 0; i < actionStrategyItem.parameters.size(); i++) {
					xmlStrategyParameter = (XMLStrategyParameter) actionStrategyItem.parameters.get(i);
					para = new StrategyParameter();
					para.setName(xmlStrategyParameter.ParaName);
					para.setDesc(xmlStrategyParameter.ParaDesc);
					para.setSpec(xmlStrategyParameter.ParaSpec);
					para.setType(xmlStrategyParameter.ParaType);
					paraValue = xmlStrategyParameter.ParaValue;
					if ("V".equals(para.getSpec())) {
						paraValue = getVariableValueA(paraValue, allVariables);
					}
					para.setValue(paraValue);
					paraList.add(para);
				}
				actionResultA.setParaList(paraList);
			}

			//额度矩阵的非正规处理方法
			//Dim lineVarible As String = actionResult.ActionId
			//Dim initialLine As String = "0"
			//If lineVarible.IndexOf("-") > 0Then
			// lineVarible = lineVarible.Substring(lineVarible.IndexOf("-") + 1)
			// attrString = getVaribleString(lineVarible, allVariables)
			// initialLine = attrString.Substring(attrString.IndexOf("{", 0) + 1,
			// attrString.IndexOf("}", 0))
			// actionResult.InitialLine = initialLine
			//End If
			actionResultA.setAllAttributesA(allVariables);
//			System.out.println("原因代码："+actionResultA.getReasonCode()+"描述:"+actionResultA.getReasonDescription());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		xmlStrategy = null;
		xmlInternal = null;
		xmlChkRule = null;
		usedExVariables=null;
		allVariables = null;
		return actionResultA;
	}

	/**
	 * 获取决策结果相关的原因及其描述
	 * @param allVariables Hashtable
	 * @param xmlChkRule XMLChkRule
	 * @param actionResultA ActionResult
	 * @return boolean
	 */
	private void getDecisionReasonList(ArrayList allVariables,
									   XMLChkRule xmlChkRule,
									   ActionResult actionResultA) {
		XMLChkRuleItem ruleItem = null;
		String id = null;
		String varNo = null;
		String varValue = null;
		String cmpOpe = null;
		String cmpVal = null;
		String warnDesc = null;
		String warnDescStr = "";

		if (xmlChkRule != null && xmlChkRule.rules.size() > 0) {
			for (int i = 0; i < xmlChkRule.rules.size(); ++i) {
				ruleItem = (XMLChkRuleItem) xmlChkRule.rules.get(i);
				id = ruleItem.Id;
				varNo = ruleItem.varNo;
				cmpOpe = ruleItem.cmpOpe;
				cmpVal = ruleItem.cmpVal;
				warnDesc = ruleItem.warnDesc;
				//读取策略文件中的校验规则
//				System.out.println("varNo:"+varNo+",cmpOpe:"+cmpOpe+",cmpVal:"+cmpVal+",warnDesc:"+warnDesc);//测试用
				//变量所对应的值
				varValue = getVariableValueA(varNo, allVariables);
//				System.out.println("varValue:"+varValue);//测试用
				//比较变量值是否达到预期值
				if (compareNumVar(varValue, cmpVal, cmpOpe)) {
//					System.out.println("varNo"+varNo+",cmpVal:"+cmpVal);
//					if("AT4721".equals(varNo)){
//						System.out.println("varValue:"+varValue+",cmpVal:"+cmpVal);//测试用
//					}
					if ("".equals(warnDescStr)) {
						warnDescStr = warnDesc;
					} else {
						warnDescStr += "；" + warnDesc;
					}
				}
			}
		}
		actionResultA.setWarnDescStr(warnDescStr);
	}

	//获取最终采用的策略结点
	private XMLStrategyItem getActionStrategyItemA(String attributeString, XMLStrategyItem rootStrategyItem, ArrayList allVariables) {
		XMLStrategyItem actionStrategyItem = null;
		XMLStrategyItem strategyItem = null;

		String attrName = "";
		String cmpOpe = "";
		String cmpVal = "";
		String thisVal = "";
		String valType = "";
		String attrDesc = "";
		String attrString = "";

		String tmpStr = "";
		boolean isRight = false;

		if (rootStrategyItem == null) {
			return null;
		}
		if ("".equals(rootStrategyItem.ActionId) == false && rootStrategyItem.childItems.size() == 0) {
			return rootStrategyItem;
		}
		for (int i = 0; i < rootStrategyItem.childItems.size(); i++) {
			strategyItem = (XMLStrategyItem) rootStrategyItem.childItems.get(i);
			attrName = strategyItem.VarNo;
			//获取传入的中间变量的定义和数值
			attrString = getVariableStringA(attrName, allVariables);
			thisVal = attrString.substring(attrString.indexOf("{", 0) + 1,
										   attrString.indexOf("}", 0));
			tmpStr = attrString.substring(attrString.indexOf("}", 0) + 1);
			valType = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
									   tmpStr.indexOf("}", 0));
			tmpStr = tmpStr.substring(tmpStr.indexOf("}", 0) + 1);
			attrDesc = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
										tmpStr.indexOf("}", 0));

			//选择合适的策略树分支直到最终结果
			cmpOpe = strategyItem.CmpOpe;
			cmpVal = strategyItem.CmpVal;
			isRight = getCompareResult(thisVal, cmpVal, cmpOpe, valType);
//			System.out.println("Strategy Process Var,attrName:" + attrName + ",value=" + thisVal + ",cmpOpe:" + cmpOpe + ",cmpVal=" + cmpVal + ",isRight=" + isRight);
			if (isRight == true) {
				attributeString = attributeString + "{" + attrName + ":" + thisVal + " " + cmpOpe + " " + cmpVal + "}";
				if ("".equals(strategyItem.ActionId) == false && strategyItem.childItems.size() == 0) {
					return strategyItem;
				} else {
					return getActionStrategyItemA(attributeString, strategyItem, allVariables);
				}

			}
		}

		return null;

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


	private ArrayList getAllVariblesA(ArrayList exVariables,
									  XMLInternal inVariables) {
		//----------------变量全集初始化为外部变量集---------------------------

		ArrayList result = new ArrayList();
		VariableBean var = null;
		String valueString = "";

		try {
			result = (ArrayList)exVariables.clone();
			XMLInternalVariable variable = null;
			for (int i = 0; i < inVariables.varibles.size(); i++) {
				variable = (XMLInternalVariable) inVariables.varibles.get(i);
				valueString = getInternalVaribleValueA(result, variable);
				var = new VariableBean(variable.Name, valueString, variable.Type, "IN", variable.Desc);
				result.add(var);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
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

	private String getInternalVaribleValueA(ArrayList allVariables,
											XMLInternalVariable variable) {

		String result = "";
		XMLInternalValueGroup thisGroup = null;
		XMLInternalValueGroup valueGroup = null;
		for (int i = 0; i < variable.ValueGroups.size(); i++) {
			valueGroup = (XMLInternalValueGroup) variable.ValueGroups.get(i);
			if (isCalculateA(valueGroup, allVariables)) {
				thisGroup = valueGroup;
				break;
			}
		}

		ArrayList values = null;
		if (thisGroup != null) {
			values = thisGroup.Values;
		}

		if (values != null) {
			result = getVariableResultOfGroupA(values, allVariables);
		}
		return result;
	}

//////////// 一个递归的调用，用来处理包含左右括号的表达式定义 ////////////////////
	private String getVariableResultOfGroupA(ArrayList values, ArrayList allVariables) {
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
		XMLInternalValue value = null;
		boolean isFirst = true;

		for (int i = 0; i < values.size(); i++) {
			value = (XMLInternalValue) values.get(i);
			symble = value.Symble;

			if (value.ChildValues.size() > 0) {
				tsResult = getVariableResultOfGroupA(value.ChildValues, allVariables);
			} else {
				vName = value.VarName;
				vType = value.VType;
				coeffcient = value.Coeffcient;
				parameter = value.Parameter;
				param1 = value.Param1;
				param2 = value.Param2;
				param3 = value.Param3;
				function = value.FunctionA;

				if (vType.equals("V")) {
					tsResult = getVariableStringA(vName, allVariables);
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
		try {
			if ( (function.equals("X()")) || ("".equals(function))) {
				fResult = Double.parseDouble(Value);
			} else if (function.equals("ABS")) {
				fResult = Math.abs(Double.parseDouble(Value));
			} else if (function.equals("EXP")) {
				fResult = Math.exp(Double.parseDouble(Value));
			} else if (function.equals("LOG")) {
				fResult = Math.log(Double.parseDouble(Value));
			} else if (function.equals("POWER")) {
				fResult = Math.pow(Double.parseDouble(Value), Double.parseDouble(parameter));
			} else if (function.equals("RAND")) {
				fResult = Math.random(); //*Double.parseDouble(Value);
			} else if (function.equals("ROUND")) {
				fResult = Math.round(Math.pow(10f, Double.parseDouble(parameter)) *
									 Double.parseDouble(Value)) *
					Math.pow(10f, -Double.parseDouble(parameter));
			} else if (function.equals("SIGN")) {
				if (Math.abs(Double.parseDouble(Value)) < 0.000001f) {
					fResult = 0f;
				} else if (Double.parseDouble(Value) > 0f) {
					fResult = 1f;
				} else {
					fResult = -1f;
				}
			} else if (function.equals("SQUARE")) {
				fResult = (Double.parseDouble(Value)) * (Double.parseDouble(Value));
			} else if (function.equals("SQRT")) {
				fResult = Math.sqrt(Double.parseDouble(Value));
			}
		} catch (Exception ex) {
//            ex.printStackTrace();
			return result;
		}

		try {
			fResult = Double.parseDouble(coeffcient) * fResult;
		} catch (Exception ex) {
//            ex.printStackTrace();
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

	private boolean isCalculateA(XMLInternalValueGroup valueGroup,
								 ArrayList allVariables) {

		boolean result = true;
		if (valueGroup.Conditions.size() > 0) { //若为空则直接返回true
			result = getConditionResultA(valueGroup.Conditions, allVariables, "compLog", "VarName", "compOper", "vType", "compVal",
										 "CompAttr");
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

	private String getVariableValueA(String varName,
									 ArrayList allVariables) {

		String value = "";
		VariableBean var = null;
		if (varName != null && allVariables != null) {
			for(int i=0;i<allVariables.size();++i)
			{
				var=(VariableBean)allVariables.get(i);
				if(varName.equals(var.getFldName()))
				{
					value = var.getValue();
					break;
				}
			}
		}

		return value;
	}

	/**
	 * 在变量集基础上获取指定变量的值。
	 *
	 * @param varName 变量名称。
	 * @param allVariables 以XML结构定义的变量集。
	 * @return String 以字符串表示的{变量值}{变量类型}{变量描述}。
	 */

	private String getVariableStringA(String varName,
									  ArrayList allVariables) {
		String result = "{-1}{N}{None}";

		VariableBean var = null;
		if (varName != null && allVariables != null) {
			for(int i=0;i<allVariables.size();++i)
			{
				var=(VariableBean)allVariables.get(i);
				if(varName.equals(var.getFldName()))
				{
					result = "{" + var.getValue() + "}{" + var.getFldType() + "}{" + var.getFldDsc() + "}";
					break;
				}
			}
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
			try {
				nVal = Double.parseDouble(thisVal);
				nCmp = Double.parseDouble(cmpVal);
			} catch (Exception ex) {
//                ex.printStackTrace();
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
	 * 通过政策项目编号获取政策项目描述
	 *
	 * @param actionId  政策项目编号
	 * @param policy    XML结构中的政策节点
	 *
	 * @return 政策项目描述
	 */

	private static String getActionNa(String actionId, Node policy) {
		String actionNa = "无";
		//Node items = policy.getFirstChild();
		Element item = null;
		NodeList itemList = policy.getChildNodes();
		for (int i = 0; i < itemList.getLength(); i++) {
			item = (Element) itemList.item(i);
			if (item.getAttribute("PlsItem").equals(actionId)) {
				actionNa = item.getAttribute("ItemDesc");
				break;
			}
		}
		return actionNa;
	}

////////////////政策入组条件的另一种判别方法///////////////////////////////////
	private boolean isInclusionA(XMLPolicy policy, ArrayList allVariables) {
		boolean result = true;
		if (policy != null) {
			if (policy.Inclusion.size() > 0) {
				result = getConditionResultP(policy.Inclusion, allVariables, "LogOpe", "VarNo", "CmpOpe", "vType", "CmpVal",
											 "CompAttr");
			}
		}
		return result;
	}

////////////一个递归的调用，用来处理包含左右括号的条件定义////////////////////
	private boolean getConditionResultP(ArrayList con,
										ArrayList allVariables,
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
		int i = -1;

		XMLPolicyCondition con1 = null;
		for (int j = 0; j < con.size(); j++) {
			i = i + 1;
			con1 = (XMLPolicyCondition) con.get(j);
			compLog = con1.LogOpe;
			if ("".equals(compLog)) {
				compLog = "and";
			}
			if (con1.childConditions.size() > 0) {
				tmpResult = getConditionResultA(con1.childConditions, allVariables, logStr, nameStr, operStr, typeStr, valStr,
												attrStr);
			} else {
				varName = con1.VarNo;
				vType = con1.VType;
				compOper = con1.CmpOpe;
				compVal = con1.CmpVal;
				compAttr = con1.CompAttr;
				if ("".equals(compAttr) == false) {
					variableString = getVariableStringA(compAttr, allVariables);
					compVal = variableString.substring(variableString.indexOf(
						"{", 0) + 1, variableString.indexOf("}", 0));

				}
				variableString = getVariableStringA(varName, allVariables);
				valueString = variableString.substring
					(variableString.indexOf("{", 0) + 1,
					 variableString.indexOf("}", 0));
				tmpResult = getCompareResult(valueString, compVal, compOper,
											 vType);

				if ( (!tmpResult) && (nameStr.equalsIgnoreCase("VARNO"))) {
					String tmpString = variableString.substring(variableString.
						indexOf("}", 0) + 1);
					tmpString = tmpString.substring(tmpString.indexOf("}", 0) +
						1);

					this.setInclusionString(this.getInclusionString() +
											"{" + tmpString + compOper +
											compVal + "}");
				}
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
		}
		return result;
	}

////////////一个递归的调用，用来处理包含左右括号的条件定义////////////////////
	private boolean getConditionResultA(ArrayList con,
										ArrayList allVariables,
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
		int i = -1;

		XMLInternalCondition con1 = null;
		for (int j = 0; j < con.size(); j++) {
			i = i + 1;
			con1 = (XMLInternalCondition) con.get(j);
			compLog = con1.CompLog;
			if ("".equals(compLog)) {
				compLog = "and";
			}
			if (con1.childConditions.size() > 0) {
				tmpResult = getConditionResultA(con1.childConditions, allVariables, logStr, nameStr, operStr, typeStr, valStr,
												attrStr);
			} else {
				varName = con1.VarName;
				vType = con1.VType;
				compOper = con1.CompOper;
				compVal = con1.CompVal;
				compAttr = con1.CompAttr;
				if ("".equals(compAttr) == false) {
					variableString = getVariableStringA(compAttr, allVariables);
					compVal = variableString.substring(variableString.indexOf(
						"{", 0) + 1, variableString.indexOf("}", 0));

				}
				variableString = getVariableStringA(varName, allVariables);
				valueString = variableString.substring
					(variableString.indexOf("{", 0) + 1,
					 variableString.indexOf("}", 0));
				tmpResult = getCompareResult(valueString, compVal, compOper,
											 vType);

				if ( (!tmpResult) && (nameStr.equalsIgnoreCase("VARNO"))) {
					String tmpString = variableString.substring(variableString.
						indexOf("}", 0) + 1);
					tmpString = tmpString.substring(tmpString.indexOf("}", 0) +
						1);

					this.setInclusionString(this.getInclusionString() +
											"{" + tmpString + compOper +
											compVal + "}");
				}
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
		}
		return result;
	}

	/**
	 * 判断是否满足政策入组条件。
	 * @param policy XML结构中的政策节点。
	 * @param allVariables 以XML结构定义的变量集。
	 * @return true 满足入组条件；
	 * <p>false 不满足入组条件。
	 */

	private String getInclusion(Node policy, ArrayList allVariables) {
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
					varName = condition.getAttribute("AttrName");
					compLog = condition.getAttribute("LogOper");
					if (i == 0) {
						if (compLog.equalsIgnoreCase("and")) {
							result = true;
						} else {
							result = false;
						}
					}
					compOper = condition.getAttribute("CompOper");
					compVal = condition.getAttribute("CompVal");
					variableString = getVariableStringA(varName, allVariables);
					valueString = variableString.substring(variableString.
						indexOf("{",
								0) + 1, variableString.indexOf("}", 0));
					tmpResult = getCompareResult(valueString, compVal,
												 compOper,
												 "N");
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
				}
			}
		}

		if (!result) {
			resultString = "{0}" + resultString;
		} else {
			resultString = "{1}" + resultString;

		}
		return resultString;
	}

	/**
	 * 获取可用的外部变量集合－用于附属卡的处理（删除其它的附属卡信息）。
	 * @param srcVaribles 传入的外部变量全集（XML格式）
	 * @param flag 附属卡序号（若是主卡传入0)。
	 * @return Element 可用的外部变量集合（XML格式）。
	 */

	public ArrayList getUseableVaribles(ArrayList srcVaribles, String flag) {
		VariableBean variable = null;
		String fldSrc = null;
		ArrayList stdVariables=new ArrayList();

		if (srcVaribles != null) {
			for(int i=0;i<srcVaribles.size();++i)
			{
				variable = (VariableBean) srcVaribles.get(i);
				fldSrc = variable.getFldSrc();
				if (! ( (fldSrc.trim().equals("EX")) || (fldSrc.trim().equals(flag)))) {
					variable=new VariableBean("~!@#$%",variable.getValue(),variable.getFldType(),variable.getFldSrc(),variable.getFldDsc());
				}
				stdVariables.add(variable);
			}
		}
		return stdVariables;
	}
}
