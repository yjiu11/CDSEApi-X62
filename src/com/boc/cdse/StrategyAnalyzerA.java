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
 * <P><Code>StrategyAnalyzer</Code>��Ϊ����ִ�нӿڣ�ͨ����ȡ����ָ���ļ���ϴ�����м����
 * ȡ�ò��Խ����<Code>getStrategyResult</Code>Ϊ���߽���Ļ�ȡ������
 *
 * <P>Copyright:��Ȩ (c) 2004 </P>
 * <P>Company:�׺�����������޹�˾</P>
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
	 * ��ȡ���߽��
	 *
	 * @param attribute ͨ�����ݴ�����̲������ⲿ�м����
	 * @param strategyFile �ôξ�������ѭ�Ĳ��Թ���
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
			//----------------���Խ���ĳ�ʼ��----------------------------------------//
			xmlStrategy = strategy.getXmlStrategy();
			xmlInternal = strategy.getXmlInternal();
			xmlChkRule = strategy.getXmlChkRule();
			//��ȡ��ǰʹ�õ��ⲿ����
			 usedExVariables= getUseableVaribles(exVariables, flag);

			//����ִ�в��������ȫ������
			allVariables = getAllVariblesA(usedExVariables, xmlInternal);
//			System.out.println("========================All Variable=========================");
//			Enumeration aa=allVariables.elements();
//			while(aa.hasMoreElements())
//			{
//				VariableBean aaa=(VariableBean)aa.nextElement();
//				System.out.println("Name:"+aaa.getFldName()+",value="+aaa.getValue()+",desc:"+aaa.getFldDsc());
//			}
//			System.out.println("========================All Variable=========================");

			//��þ��߽����ص�ԭ����뼰����
			getDecisionReasonList(allVariables, xmlChkRule, actionResultA);

			//    actionResultA = calculateStrategyResultA(allVariables, strategyDoc)

//			String actionId = "��";
//			String actionNa = "��";
//			String attributesString = "";
			String attributeString = "";

			//�����׼���ж��ʹ���
			this.inclusionString = "";
			if (isInclusionA(xmlStrategy.policy, allVariables) == false) {
				actionResultA.setPoliceNo(xmlStrategy.PlsNo);
				actionResultA.setPoliceNa(xmlStrategy.policy.PlsNa);
				actionResultA.setStrategyNo(xmlStrategy.StgNo);
				actionResultA.setStrategyNa(xmlStrategy.StgNa);
				actionResultA.setStrategyVer(xmlStrategy.StgVer);
				actionResultA.setActionId("-888");
				actionResultA.setActionNa("��������������");
				actionResultA.setAttributesString(this.inclusionString);
				actionResultA.setReasonCode("-1");
				actionResultA.setReasonDescription("������������������ܾ�");
				actionResultA.setInitialLine("-1");
				actionResultA.setAllAttributesA(allVariables);
				return actionResultA;
			}

			//ͨ���������߼����㷵�صĶԲ߶���
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
				//��ȡ������м�����Ķ������ֵ
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

				//ȡ�ò����ڵ�
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

			//��Ⱦ���ķ����洦����
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
//			System.out.println("ԭ����룺"+actionResultA.getReasonCode()+"����:"+actionResultA.getReasonDescription());
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
	 * ��ȡ���߽����ص�ԭ��������
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
				//��ȡ�����ļ��е�У�����
//				System.out.println("varNo:"+varNo+",cmpOpe:"+cmpOpe+",cmpVal:"+cmpVal+",warnDesc:"+warnDesc);//������
				//��������Ӧ��ֵ
				varValue = getVariableValueA(varNo, allVariables);
//				System.out.println("varValue:"+varValue);//������
				//�Ƚϱ���ֵ�Ƿ�ﵽԤ��ֵ
				if (compareNumVar(varValue, cmpVal, cmpOpe)) {
//					System.out.println("varNo"+varNo+",cmpVal:"+cmpVal);
//					if("AT4721".equals(varNo)){
//						System.out.println("varValue:"+varValue+",cmpVal:"+cmpVal);//������
//					}
					if ("".equals(warnDescStr)) {
						warnDescStr = warnDesc;
					} else {
						warnDescStr += "��" + warnDesc;
					}
				}
			}
		}
		actionResultA.setWarnDescStr(warnDescStr);
	}

	//��ȡ���ղ��õĲ��Խ��
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
			//��ȡ������м�����Ķ������ֵ
			attrString = getVariableStringA(attrName, allVariables);
			thisVal = attrString.substring(attrString.indexOf("{", 0) + 1,
										   attrString.indexOf("}", 0));
			tmpStr = attrString.substring(attrString.indexOf("}", 0) + 1);
			valType = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
									   tmpStr.indexOf("}", 0));
			tmpStr = tmpStr.substring(tmpStr.indexOf("}", 0) + 1);
			attrDesc = tmpStr.substring(tmpStr.indexOf("{", 0) + 1,
										tmpStr.indexOf("}", 0));

			//ѡ����ʵĲ�������ֱ֧�����ս��
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
	 * ���������ڲ����������ⲿ�����ϳɱ���ȫ��
	 *
	 * @param exVariables �ⲿ�м��������
	 * @param inVariables �ڲ��������ϼ����㷨
	 * @return Element�� ��XML�ṹ����ı���ȫ��
	 * <p>������̣�
	 * <p>1.����ȫ����ʼ��Ϊ�ⲿ��������
	 * <p>2.��һ�����ڲ��������ŵ�����ȫ���С�
	 */


	private ArrayList getAllVariblesA(ArrayList exVariables,
									  XMLInternal inVariables) {
		//----------------����ȫ����ʼ��Ϊ�ⲿ������---------------------------

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
	 * ���ݱ����㷨�������ָ���ڲ�������ֵ
	 *
	 * @param allVariables ����˱�����ǰ���ɵı���ȫ����
	 * @param varible ��XML�ṹ������ڲ������������㷨����
	 * @return String ���ַ�����ʾ����ֵ��
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

//////////// һ���ݹ�ĵ��ã�������������������ŵı��ʽ���� ////////////////////
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
	 * �ж��ڲ������Ƿ���ĳһȡֵ�������㡣
	 *
	 * @param valueGroup ���жϵ�ȡֵ�顣
	 * @param varible ����˱�����ǰ���ɵı���ȫ����
	 * @return true �ñ����ô�ȡֵ�������㣻
	 * <p>false �ñ������ô�ȡֵ�������㡣
	 */

	private boolean isCalculateA(XMLInternalValueGroup valueGroup,
								 ArrayList allVariables) {

		boolean result = true;
		if (valueGroup.Conditions.size() > 0) { //��Ϊ����ֱ�ӷ���true
			result = getConditionResultA(valueGroup.Conditions, allVariables, "compLog", "VarName", "compOper", "vType", "compVal",
										 "CompAttr");
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
	 * �ڱ����������ϻ�ȡָ��������ֵ��
	 *
	 * @param varName �������ơ�
	 * @param allVariables ��XML�ṹ����ı�������
	 * @return String ���ַ�����ʾ��{����ֵ}{��������}{��������}��
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
	 * ͨ��������Ŀ��Ż�ȡ������Ŀ����
	 *
	 * @param actionId  ������Ŀ���
	 * @param policy    XML�ṹ�е����߽ڵ�
	 *
	 * @return ������Ŀ����
	 */

	private static String getActionNa(String actionId, Node policy) {
		String actionNa = "��";
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

////////////////����������������һ���б𷽷�///////////////////////////////////
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

////////////һ���ݹ�ĵ��ã�������������������ŵ���������////////////////////
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

////////////һ���ݹ�ĵ��ã�������������������ŵ���������////////////////////
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
	 * �ж��Ƿ�������������������
	 * @param policy XML�ṹ�е����߽ڵ㡣
	 * @param allVariables ��XML�ṹ����ı�������
	 * @return true ��������������
	 * <p>false ����������������
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
	 * ��ȡ���õ��ⲿ�������ϣ����ڸ������Ĵ���ɾ�������ĸ�������Ϣ����
	 * @param srcVaribles ������ⲿ����ȫ����XML��ʽ��
	 * @param flag ��������ţ�������������0)��
	 * @return Element ���õ��ⲿ�������ϣ�XML��ʽ����
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
