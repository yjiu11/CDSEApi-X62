package com.boc.cdse;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * <P>
 * <Code>process</Code>为控制流程的执行过程。
 * 
 * <P>
 * Copyright:版权 (c) 2004 - 2006
 * </P>
 * <P>
 * Company:首航财务顾问有限公司
 * </P>
 * <P>
 * Created:2004-01
 * <P>
 * Updated:2005-10
 * 
 * @author: CDSE项目组
 * @version 2.0
 */

public class PersonalCreditCardHandler implements CreditCardHandler,
		java.io.Serializable {
	private StrategyManager strategyManager = null;
	private int stepId = 0;
	private boolean hasVarLog = false;
	private float DM0210 = 0.3f; // 银卡主卡人民币取现比例
	private float DM0220 = 0.3f; // 银卡主卡外币取现比例
	private float DM0230 = 0.3f; // 银卡附属卡人民币取现比例
	private float DM0240 = 0.3f; // 银卡附属卡外币取现比例
	private float DM0250 = 0.4f; // 金卡主卡人民币取现比例
	private float DM0260 = 0.4f; // 金卡主卡外币取现比例
	private float DM0270 = 0.4f; // 金卡附属卡人民币取现比例
	private float DM0280 = 0.4f; // 金卡附属卡人民币取现比例
	private float DM0120 = 8.0f; // 美元兑人民币汇率
	private float DM0130 = 12.0f; // 日元兑人民币汇率
	private float DM0140 = 10.0f;
	private float DM0300 = 0.4f; // 公务卡取现额度参数
	private boolean isUsd = true;
	private String lastProductCode = ""; // 最终的产品号
	private String English_1 = "VY01";
	private String English_2 = "MY01";
	private String America_1 = "VM01";
	private String America_2 = "MM01";
	private String attrString = "";

	public PersonalCreditCardHandler() {
		strategyManager = StrategyManager.getInstance();

		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 个人卡控制流程执行过程
	 * 
	 * @param appForm
	 *            以xml规范提供的申请单对象
	 * @param log
	 *            日志文件
	 * @return cdesResult 最终输出结果
	 */
	public CDSEResult process(Document appForm, Log log, String status) {

		boolean createAttributesSucceed = true; // 是否成功获取外部变量

		CDSEResult cdseResult = new CDSEResult();
		PersonalCreditCardAttributes attributes = null;
		try {
			attributes = new PersonalCreditCardAttributes(appForm, log);
			if ("2".equals(status) || "3".equals(status) || "5".equals(status)) {
				attributes.caculateAllAttributs_amount(appForm, log, status);
			} else {
				attributes.caculateAllAttributs(appForm, log);
			}

			cdseResult.setApplicationId(attributes.getApplNum());
			/*cdseResult.setStrategiesLogID(cdseResult.getApplicationId()
					+ CDSEUtil.getTime());
			cdseResult.setStrategyID(cdseResult.getStrategiesLogID());*/
			//cdseResult.setStrategiesLogID("");111
			cdseResult.setStrategyID(cdseResult.getApplicationId()+ CDSEUtil.getTime());
			// System.out.println("getApplNum："+attributes.getApplNum()+"; getApplicationId："+cdseResult.getApplicationId()+"; getTime："+CDSEUtil.getTime()+"; getStrategiesLogID："+cdseResult.getStrategiesLogID());
		} catch (CDSEException ex) {
			createAttributesSucceed = false;
			System.out.println("ex.getErrorCode：" + ex.getErrorCode());
			if (8888 == ex.getErrorCode()) {
				cdseResult.setApplicationId(attributes.getApplNum());
				cdseResult.setStrategiesLogID(cdseResult.getApplicationId()
						+ CDSEUtil.getTime());
				//cdseResult.setStrategyID(cdseResult.getStrategiesLogID());
				cdseResult.setProductCode(attributes.getApplProductCd());
				cdseResult.setPrincipalResultID("D");
				cdseResult.setPrincipalResultDescription("人工审查;卡种未设置");
				StringBuffer str = new StringBuffer();
				str.append("卡种未设置:")
						.append(attributes.getApplNum())
						.append(cdseResult.getApplicationId()
								+ CDSEUtil.getTime())
						.append(cdseResult.getStrategiesLogID())
						.append(attributes.getApplProductCd());
				LogManager.getInstance().toCdsLog(str.toString());
			} else {
				String errorDesc = ex.getMessage();

				String underwritingReasonCode = "";
				if (errorDesc != null) {
					underwritingReasonCode = getNo(errorDesc, ":");
				}
				String underwritingReasonDescription = "";

				if (underwritingReasonCode.equals("")) {
					underwritingReasonCode = "DR299";
					underwritingReasonDescription = "原始数据出现未知异常，不予决策。";
				} else {
					underwritingReasonDescription = getDesc(errorDesc, ":");
				}

				cdseResult.setUnderwritingReasonCode(underwritingReasonCode);
				cdseResult
						.setUnderwritingReasonDescription(underwritingReasonDescription);
				cdseResult.setInitLineReasonCode("DR101");
				cdseResult.setInitLineReasonDescription("由于拒绝授信，初始额度策略不执行。");

				ErrorManager.getInstance().toSystemErrorLog(
						CDSEUtil.xmlToString(appForm) + " " + errorDesc);

				if (attributes != null) {
					if (!attributes.isSupplApplOnly()) {
						cdseResult.setPrincipalResultID("D");
						cdseResult.setPrincipalResultDescription("人工审查");
					}
					cdseResult.setApplicationId(attributes.getApplNum());
					cdseResult.setStrategiesLogID(cdseResult.getApplicationId()
							+ CDSEUtil.getTime());
					//cdseResult.setStrategyID(cdseResult.getStrategiesLogID());
					cdseResult.setProductCode(attributes.getApplProductCd());
					if (attributes.isSupplAppl()) {
						int supplNum = attributes.getSupplNum();
						SupplResult supplResult = new SupplResult();
						ArrayList supplResultList = new ArrayList();
						if (supplNum > 0) {
							for (int i = 0; i < supplNum; ++i) {
								String supplId = (String) attributes
										.getSupplData().get(i);
								supplResult = new SupplResult();
								supplResult.setSupplID(supplId);
								supplResult.setSupplResultID("D");
								supplResult.setSupplResultDescription("人工审查");
								supplResult.setSupplUwReasonCode("DR104");
								supplResult
										.setSupplUwReasonDescription("由于主卡申请数据出现异常，不执行附属卡的授信。");
								supplResultList.add(supplResult);
							}
							cdseResult.setSupplResultList(supplResultList);
						}
					}
				} else {
					cdseResult.setPrincipalResultID("D");
					cdseResult.setPrincipalResultDescription("人工审查");
				}
			}
		}

		if (createAttributesSucceed == false) {

			return cdseResult;
		}

		try {
			this.lastProductCode = attributes.getApplProductCd();
			// 填写最终结果中仅需要外部中间变量部分
			cdseResult.setApplicantRandomDigitID(attributes.getRandNum()); // 随机数
			// /////////////////////////////////////////////////////////////////////////////

			// 根据Qcc策略执行方式修改 2007-1-26 董春国
			if (attributes.isQccCardApp()) {
				if (attributes.isIsSupplApplOnly()) {
					// 纯粹附属卡申请
					cdseResult.setUnderwritingReasonCode("DR102");
					cdseResult
							.setUnderwritingReasonDescription("纯粹附属卡，不存在主卡的授信问题。");
					cdseResult.setInitLineReasonCode("DR102");
					cdseResult
							.setInitLineReasonDescription("纯粹附属卡，不存在主卡的额度问题。");
					cdseResult = this.getSupplResult("POD0000", "PLY0230",
							"无主卡", attributes, cdseResult, appForm, log);
				} else {
					// 有主卡的申请，在里面的程序考虑是否还有附属卡情况
					cdseResult = this.getQccEntireProcessResult(attributes,
							cdseResult, appForm, log, status);
				}
			} else {
				if (attributes.isIsSupplApplOnly()) {
					// 纯粹附属卡申请
					cdseResult.setUnderwritingReasonCode("DR102");
					cdseResult
							.setUnderwritingReasonDescription("纯粹附属卡，不存在主卡的授信问题。");
					cdseResult.setInitLineReasonCode("DR102");
					cdseResult
							.setInitLineReasonDescription("纯粹附属卡，不存在主卡的额度问题。");
					// cdseResult.SET
					cdseResult = this.getSupplResult("POD0000", "PLY0030",
							"无主卡", attributes, cdseResult, appForm, log);
				} else {
					// 有主卡的申请，在里面的程序考虑是否还有附属卡情况
					cdseResult = this.getEntireProcessResult(attributes,
							cdseResult, appForm, log, status);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			cdseResult.setUnderwritingReasonCode("DR999");
			cdseResult.setUnderwritingReasonDescription("系统出现未知错误，决策终止。");
			ErrorManager.getInstance().toSystemErrorLog(
					CDSEUtil.xmlToString(appForm) + "\n "
							+ CDSEUtil.xmlToString(attributes.getDocument())
							+ " "
							+ ErrorManager.getInstance().getSystemError(9007));
			ex.printStackTrace();

			if (attributes != null) {
				if (!attributes.isSupplApplOnly()) {
					cdseResult.setPrincipalResultID("D");
					cdseResult.setPrincipalResultDescription("人工审查");
				}
				cdseResult.setApplicationId(attributes.getApplNum());
				cdseResult.setStrategiesLogID(cdseResult.getApplicationId()+ CDSEUtil.getTime());
				cdseResult.setStrategyID(cdseResult.getApplicationId()+ CDSEUtil.getTime());
				cdseResult.setProductCode(attributes.getApplProductCd());
				if (attributes.isSupplAppl()) {
					int supplNum = attributes.getSupplNum();
					SupplResult supplResult = new SupplResult();
					ArrayList supplResultList = new ArrayList();
					if (supplNum > 0) {
						for (int i = 0; i < supplNum; ++i) {
							String supplId = (String) attributes.getSupplData()
									.get(i);
							supplResult = new SupplResult();
							supplResult.setSupplID(supplId);
							supplResult.setSupplResultID("D");
							supplResult.setSupplResultDescription("人工审查");
							supplResult.setSupplUwReasonCode("DR104");
							supplResult
									.setSupplUwReasonDescription("由于主卡申请数据出现异常，不执行附属卡的授信。");
							supplResultList.add(supplResult);
						}
						cdseResult.setSupplResultList(supplResultList);
					}
				}
			} else {
				cdseResult.setPrincipalResultID("D");
				cdseResult.setPrincipalResultDescription("人工审查");
			}
		}
		Document doc = appForm;
		Element basic = doc.getDocumentElement();
		Node data = basic.getFirstChild();
		Element variable = (Element) data.getFirstChild();

		attrString = variable.getAttribute("ProductCd").trim();
		Float num = ProductCardInfoParameter.exchangeRateCardMap
				.get(attrString);
		if (num != null) {
			if (isUsd) {
				cdseResult.setExchangeRate(num);
			}
		} else {
			if (isUsd) {
				cdseResult.setExchangeRate(this.DM0120);
			} else {
				cdseResult.setExchangeRate(this.DM0130);
			}
		}
		// if(attrString.equals(English_1)||attrString.equals(English_2)){
		// if (isUsd) {
		// cdseResult.setExchangeRate(this.DM0140);
		// }
		// }else if(attrString.equals(America_1)||attrString.equals(America_2)){
		// if (isUsd) {
		// cdseResult.setExchangeRate(this.DM0120);
		// }
		// }else{

		// }

		cdseResult.setProductCode(this.lastProductCode);
		// 根据是否零额度判断授信结果代码
		cdseResult.setStrategyID(cdseResult.getApplicationId()+ CDSEUtil.getTime());
		attributes = null;
		// zhushi
		// appendResultLog(log, cdseResult); //添加最终结果日志
		// if(attributes!=null && !"".equals(attributes.getWarningInfo())){
		// cdseResult.setPrincipalResultDescription(cdseResult.getPrincipalResultDescription()+";"+attributes.getWarningInfo());
		//
		// }
		// LogManager.getInstance().toCdsLog(appForm.toString());
		return cdseResult;
	}

	/**
	 * "主卡＋附属卡”控制流程执行过程
	 * 
	 * @param attributes
	 *            原始变量经过处理后形成的外部中间变量
	 * @param cdsResult
	 *            传入此前步骤处理后的输出结果
	 * @param log
	 *            日志文件
	 * @return cdesResult 经此步骤处理后的输出结果
	 */
	private CDSEResult getEntireProcessResult(
			PersonalCreditCardAttributes attributes, CDSEResult cdseResult,
			Document appForm, Log log, String status) {
		String portfolioId = "";
		String underwrittingPolicyId = "";
		String initialLinePolicyId = "";
		System.out.println("进入主附卡");
		// 2006-1-10 Huang shaicheng 根据中行的要求修改：所有的申请先走金卡再走银卡,
		// 2006-07-31 dongchg 根据中行的要求修改：不区分金卡银卡，按VIP来进行策略执行
		if (attributes.isIsPlatinaCardApp() == true) { // 白金卡策略
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0026";
			initialLinePolicyId = "PLY0126";
		} else if (attributes.isIsDutyCardApp() == false && "1".equals(status)) {
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0020";// 新版授信
			initialLinePolicyId = "PLY0120";
		} else if ("4".equals(status)) { // 网申
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0320";
			initialLinePolicyId = "PLY0350";
		} else if ("2".equals(status)) { // 临调
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0330";
			initialLinePolicyId = "PLY0360";
		} else if ("3".equals(status)) { // 永久
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0340";
			initialLinePolicyId = "PLY0370";
		} else if ("5".equals(status)) { // 网银
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0380";
			initialLinePolicyId = "PLY0390";
		} else {
			portfolioId = "POD0020"; // 公务卡
			underwrittingPolicyId = "PLY0300";
			initialLinePolicyId = "PLY0310";
		}

		System.out.println("underwrittingPolicyId" + underwrittingPolicyId);
		// 执行主卡决策
		// 修改23
		cdseResult = getPrincipalResult(portfolioId, underwrittingPolicyId,
				initialLinePolicyId, attributes, cdseResult, appForm, log,
				status);

		// 如果有附属卡，执行附属卡策略
		System.out.println("isSuppl:" + attributes.isSupplAppl());
		if (attributes.isSupplAppl()) {
			if (cdseResult.getPrincipalResultID().equals("B")
					|| cdseResult.getPrincipalResultID().equals("C")) {
				SupplResult supplResult = new SupplResult();
				ArrayList supplResultList = new ArrayList();
				int supplNum = attributes.getSupplNum();
				if (supplNum > 0) {
					for (int i = 0; i < supplNum; ++i) {
						String supplId = (String) attributes.getSupplData()
								.get(i);
						supplResult = new SupplResult();
						supplResult.setSupplID(supplId);
						supplResult.setSupplResultID("B");
						supplResult.setSupplResultDescription("拒绝授信");
						supplResult.setSupplUwReasonCode("DR105");
						supplResult
								.setSupplUwReasonDescription("由于主卡未获批准，不执行附属卡的授信。");
						supplResultList.add(supplResult);
					}
					cdseResult.setSupplResultList(supplResultList);
				}
			} else {
				cdseResult = getSupplResult("POD0000", "PLY0030",
						underwrittingPolicyId, attributes, cdseResult, appForm,
						log);
			}
		}

		return cdseResult;
	}

	/**
	 * 根据Qcc卡申请，修改Qcc执行策略
	 * 
	 * @param attributes
	 *            PersonalCreditCardAttributes
	 * @param cdseResult
	 *            CDSEResult
	 * @param log
	 *            Log
	 * @return CDSEResult
	 */
	private CDSEResult getQccEntireProcessResult(
			PersonalCreditCardAttributes attributes, CDSEResult cdseResult,
			Document appForm, Log log, String status) {
		System.out.println("进入Qcc卡申请");
		String portfolioId = "";
		String underwrittingPolicyId = "";
		String initialLinePolicyId = "";
		if (attributes.isIsPlatinaCardApp() == true) { // 白金卡策略
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0226";
			initialLinePolicyId = "PLY0236";
		} else if (attributes.getCardLevel() == 0) { // 非VIP授信策略
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0200";
			initialLinePolicyId = "PLY0220";
		} else if (attributes.getCardLevel() == 1) { // VIP授信策略
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0225";
			initialLinePolicyId = "PLY0235";
		} else if ("4".equals(status)) {
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0320";
			initialLinePolicyId = "PLY0350";
		} else if ("2".equals(status)) {
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0330";
			initialLinePolicyId = "PLY0360";
		} else if ("3".equals(status)) {
			portfolioId = "POD0020";
			underwrittingPolicyId = "PLY0340";
			initialLinePolicyId = "PLY0370";
		}

		// 执行主卡决策
		// 修改2323
		cdseResult = getPrincipalResult(portfolioId, underwrittingPolicyId,
				initialLinePolicyId, attributes, cdseResult, appForm, log,
				status);

		// 如果有附属卡，执行附属卡策略
		if (attributes.isSupplAppl()) {
			if (cdseResult.getPrincipalResultID().equals("B")
					|| cdseResult.getPrincipalResultID().equals("C")) {
				SupplResult supplResult = new SupplResult();
				ArrayList supplResultList = new ArrayList();
				int supplNum = attributes.getSupplNum();
				if (supplNum > 0) {
					for (int i = 0; i < supplNum; ++i) {
						String supplId = (String) attributes.getSupplData()
								.get(i);
						supplResult = new SupplResult();
						supplResult.setSupplID(supplId);
						supplResult.setSupplResultID("B");
						supplResult.setSupplResultDescription("拒绝授信");
						supplResult.setSupplUwReasonCode("DR105");
						supplResult
								.setSupplUwReasonDescription("由于主卡未获批准，不执行附属卡的授信。");
						supplResultList.add(supplResult);
					}
					cdseResult.setSupplResultList(supplResultList);
				}
			} else {
				cdseResult = getSupplResult("POD0000", "PLY0230",
						underwrittingPolicyId, attributes, cdseResult, appForm,
						log);
			}
		}

		return cdseResult;
	}

	/**
	 * 主卡控制流程执行过程
	 * 
	 * @param portfolioId
	 *            产品线代号
	 * @param underwrittingPolicyId
	 *            主卡授信政策编号
	 * @param initialLinePolicyId
	 *            主卡初始额度政策编号
	 * @param attributes
	 *            原始变量经过处理后形成的外部中间变量
	 * @param cdsResult
	 *            传入此前步骤处理后的输出结果
	 * @param log
	 *            日志文件
	 * @param status
	 * @return cdesResult 经此步骤处理后的输出结果
	 */
	// 修改2323
	private CDSEResult getPrincipalResult(String portfolioId,
			String underwrittingPolicyId, String initialLinePolicyId,
			PersonalCreditCardAttributes attributes, CDSEResult cdseResult,
			Document appForm, Log log, String status) {

		BigDecimal bd = null;
		String tmpStr = "";
		int tmpInt = -1;
		System.out.println("进入主卡");
		Strategy strategy = null;
		StrategyAnalyzerA strategyAnalyzerA = new StrategyAnalyzerA();
		ActionResult actionResult = null;
		strategy = strategyManager.getActiveStrategy(portfolioId,
				underwrittingPolicyId);
		CDSLog clog = new CDSLog();
		CDSEMessage CDSEM = new CDSEMessage();
		CDSEM.process(strategy);
		if (strategy == null) {
			cdseResult.setPrincipalResultID("D");
			cdseResult.setPrincipalResultDescription("人工审查");
			cdseResult.setUnderwritingReasonCode("DR904");
			cdseResult.setUnderwritingReasonDescription("没有发现指定的策略文件，决策终止");
			return cdseResult;
		}

		actionResult = strategyAnalyzerA.getStrategyResultA(
				attributes.getAttachVariableA(), strategy, "0");
		this.loadConstantVaribles(actionResult);

		if (!hasVarLog) { // 添加内部变量日志
			// Zhushi
			// appendVaribleLog(log, actionResult.getAllAttributesA());
			setHasVarLog(true);
		}
		// Zhushi
		// appendProcessLog(log, actionResult, portfolioId); //添加过程日志
		// 修改部分
		tmpStr = getAttrValue("CG0110", actionResult.getAllAttributesA());

		tmpInt = tmpStr.indexOf(".");
		if (tmpInt > 0) {
			tmpStr = tmpStr.substring(0, tmpInt);
		}
		try {
			cdseResult.setUsageGrade(Integer.parseInt(tmpStr));
		} catch (Exception ex2) {
			cdseResult.setUsageGrade(0); // 使用评分
		}

		tmpStr = getAttrValue("CG0010", actionResult.getAllAttributesA());
		tmpInt = tmpStr.indexOf(".");
		if (tmpInt > 0) {
			tmpStr = tmpStr.substring(0, tmpInt);
		}
		try {
			cdseResult.setRiskGrade(Integer.parseInt(tmpStr));
		} catch (Exception ex2) {
			cdseResult.setRiskGrade(0); // 风险评分
		}
		/** 调额 */
		String ZT0064 = null;
		String ZT0065 = null;
		String ZT0066 = null;
		String A01001 = null;
		String AT4410 = null;
		String AT4420 = null;
		String AT4421 = null;
		String AT4422 = null;
		String AT4430 = null;
		String AT4440 = null;
		String AT4450 = null;
		String AT4451 = null;
		String AT4460 = null;
		String AT4470 = null;
		String AT4480 = null;
		String AT4490 = null;
		String AT4530 = null;
		String AT4550 = null;
		String AT4590 = null;
		String AT4600 = null;
		String AT4610 = null;
		String AT4620 = null;
		String AT4630 = null;
		String AT4640 = null;
		String AT4650 = null;
		String AT4660 = null;
		String AT4670 = null;
		String AT4680 = null;
		String AT4690 = null;
		String AT4700 = null;
		String AT4710 = null;
		String AT4720 = null;
		String AT4721 = null;
		String AT4722 = null;
		String AT4723 = null;
		String AT4724 = null;
		String AT4730 = null;
		String AT4740 = null;
		String AT4750 = null;
		String AT4760 = null;
		String AT5600 = null;
		String AT5601 = null;
		String AT5602 = null;
		String AT5604 = null;
		String AT5605 = null;
		String AT5606 = null;
		String AT5607 = null;
		String AT5608 = null;
		String AT5609 = null;
		String AT5610 = null;
		String AT5611 = null;
		String AT5620 = null;
		String AT5630 = null;
		String AT5640 = null;
		String AT5650 = null;
		String AT6020 = null;
		String AT6030 = null;
		String AT6040 = null;
		String AT6050 = null;
		String AT6060 = null;
		String AT6070 = null;
		String AT6080 = null;
		String AT6090 = null;
		String AT9003 = null;
		String ZT0117 = null;
		String ZT0118 = null;
		String ZT0120 = null;
		String ZT0123 = null;
		String ZT0124 = null;
		String ZT0125 = null;
		String ZT0128 = null;
		String ZT0129 = null;
		String ZT0131 = null;
		String ZT0133 = null;
		String ZT0134 = null;
		String ZT0136 = null;
		String ZT0145 = null;
		String ZT0146 = null;
		String ZT0147 = null;
		String ZT0148 = null;
		String ZT0152 = null;
		String ZT0047 = null;
		String ZT0151 = null; // 普卡和金卡参数B
		String ZT0154 = null; // 是否大于10万
		String ZT0170 = null; // 小于2倍
		String ZT0177 = null; // 最终变量
		String ZT0168 = null; //
		String AT6100 = null; //
		String EE0087 = null;
		String ZT0206 = null;
		String ZT0207 = null;
		/** 以上是调额 **/
		/** 以下是网申 */
		String AT20001 = null;
		String AT20002 = null;
		String AT20004 = null;
		String AT20005 = null;
		String AT20006 = null;
		String AT20007 = null;
		String AT20008 = null;
		String AT20009 = null;
		String AT20010 = null;
		String AT20011 = null;
		String ZT0204 = null;
		String ZT0210 = null;
		String ZT0219 = null;
		String ZT0212 = null;
		String ZT0220 = null;
		String ZT0108 = null;
		String ZT0221 = null;
		String ZT0201 = null;
		String ZT0202 = null;
		String ZT0203 = null;
		/** 以上是网申 */
		String ZT0095 = null;
		String ZT0090 = null;
		String ZT0085 = null;
		String ZT0086 = null;
		String ZT0088 = null;
		String ZT0092 = null;
		String ZT0077 = null;
		String ZT0082 = null;
		String DD0030 = null;
		String ZT0076 = null;
		String ZT0079 = null;
		String ZT0080 = null;
		String ZT0078 = null;
		String ZT0081 = null;
		String ZT0074 = null;
		String ZT0053 = null;
		String ZT0055 = null;
		String ZT0068 = null;
		String ZT0070 = null;
		String ZT0075 = null;
		String KK0008 = null;
		String ZT0093 = null;
		String ZT0084 = null;
		String ZT0087 = null;
		String AT0720 = null;
		String AT0710 = null;
		String ZT0089 = null;
		String ZT0083 = null;
		String KK0009 = null;
		String DD0020 = null;
		String KK0001 = null;
		String AT3480 = null;
		String AT3500 = null;
		String AT3930 = null;
		String AT3970 = null;
		String AT3950 = null;
		String cards = null;
		String EE0045 = null;
		String HA0021 = null;
		String EE0034 = null;
		String EE0033 = null;
		String KK0004 = null;
		String EE0039 = null;
		String AT4210 = null;
		String AT0040 = null;
		String AT0041 = null;
		String AT3530 = null;
		String AT3270 = null;
		String AT0042 = null;
		String AT0070 = null;
		String DD0021 = null;
		String AT3110 = null;
		String KK0005 = null;
		String EE0032 = null;
		String DownLog = null;
		String AT2320 = null;
		String EE0035 = null;
		String EE0036 = null;
		String EE0037 = null;
		String ZT0180 = null;
		String ZT0181 = null;
		String ZT0182 = null;
		String ZT0183 = null;
		String ZT0184 = null;
		String ZT0185 = null;
		String ZT0186 = null;
		String ZT0187 = null;
		String ZT0188 = null;
		String ZT0189 = null;
		String ZT0190 = null;
		String ZT0191 = null;
		String ZT0192 = null;
		String ZT0193 = null;
		String ZT0258 = null;
		String HA0001, HA0003, HA0004, HA0005, HA0006, HA0007, HA0009, HA0010, HA0011, HA0019, HA0020;
		ZT0258 = getAttrValue("ZT0258", actionResult.getAllAttributesA());
		ZT0064 = getAttrValue("ZT0064", actionResult.getAllAttributesA());
		ZT0065 = getAttrValue("ZT0065", actionResult.getAllAttributesA());
		ZT0066 = getAttrValue("ZT0066", actionResult.getAllAttributesA());
		ZT0047 = getAttrValue("ZT0047", actionResult.getAllAttributesA());
		ZT0180 = getAttrValue("ZT0180", actionResult.getAllAttributesA());
		AT6100 = getAttrValue("AT6100", actionResult.getAllAttributesA());
		A01001 = getAttrValue("A01001", actionResult.getAllAttributesA());
		AT0720 = getAttrValue("AT0720", actionResult.getAllAttributesA());
		AT4410 = getAttrValue("AT4410", actionResult.getAllAttributesA());
		AT4420 = getAttrValue("AT4420", actionResult.getAllAttributesA());
		AT4421 = getAttrValue("AT4421", actionResult.getAllAttributesA());
		AT4422 = getAttrValue("AT4422", actionResult.getAllAttributesA());
		AT4430 = getAttrValue("AT4430", actionResult.getAllAttributesA());
		AT4440 = getAttrValue("AT4440", actionResult.getAllAttributesA());
		AT4450 = getAttrValue("AT4450", actionResult.getAllAttributesA());
		AT4451 = getAttrValue("AT4451", actionResult.getAllAttributesA());
		AT4460 = getAttrValue("AT4460", actionResult.getAllAttributesA());
		AT4470 = getAttrValue("AT4470", actionResult.getAllAttributesA());
		AT4480 = getAttrValue("AT4480", actionResult.getAllAttributesA());
		AT4490 = getAttrValue("AT4490", actionResult.getAllAttributesA());
		AT4530 = getAttrValue("AT4530", actionResult.getAllAttributesA());
		AT4550 = getAttrValue("AT4550", actionResult.getAllAttributesA());
		AT4590 = getAttrValue("AT4590", actionResult.getAllAttributesA());
		AT4600 = getAttrValue("AT4600", actionResult.getAllAttributesA());
		AT4610 = getAttrValue("AT4610", actionResult.getAllAttributesA());
		AT4620 = getAttrValue("AT4620", actionResult.getAllAttributesA());
		AT4630 = getAttrValue("AT4630", actionResult.getAllAttributesA());
		AT4640 = getAttrValue("AT4640", actionResult.getAllAttributesA());
		AT4650 = getAttrValue("AT4650", actionResult.getAllAttributesA());
		AT4660 = getAttrValue("AT4660", actionResult.getAllAttributesA());
		AT4670 = getAttrValue("AT4670", actionResult.getAllAttributesA());
		AT4680 = getAttrValue("AT4680", actionResult.getAllAttributesA());
		AT4690 = getAttrValue("AT4690", actionResult.getAllAttributesA());
		AT4700 = getAttrValue("AT4700", actionResult.getAllAttributesA());
		AT4710 = getAttrValue("AT4710", actionResult.getAllAttributesA());
		AT4720 = getAttrValue("AT4720", actionResult.getAllAttributesA());
		AT4721 = getAttrValue("AT4721", actionResult.getAllAttributesA());
		AT4722 = getAttrValue("AT4722", actionResult.getAllAttributesA());
		AT4723 = getAttrValue("AT4723", actionResult.getAllAttributesA());
		AT4724 = getAttrValue("AT4724", actionResult.getAllAttributesA());
		AT4730 = getAttrValue("AT4730", actionResult.getAllAttributesA());
		AT4740 = getAttrValue("AT4740", actionResult.getAllAttributesA());
		AT4750 = getAttrValue("AT4750", actionResult.getAllAttributesA());
		AT4760 = getAttrValue("AT4760", actionResult.getAllAttributesA());
		AT5600 = getAttrValue("AT5600", actionResult.getAllAttributesA());
		AT5601 = getAttrValue("AT5601", actionResult.getAllAttributesA());
		AT5602 = getAttrValue("AT5602", actionResult.getAllAttributesA());
		AT5604 = getAttrValue("AT5604", actionResult.getAllAttributesA());
		AT5605 = getAttrValue("AT5605", actionResult.getAllAttributesA());
		AT5606 = getAttrValue("AT5606", actionResult.getAllAttributesA());
		AT5607 = getAttrValue("AT5607", actionResult.getAllAttributesA());
		AT5608 = getAttrValue("AT5608", actionResult.getAllAttributesA());
		AT5609 = getAttrValue("AT5609", actionResult.getAllAttributesA());
		AT5610 = getAttrValue("AT5610", actionResult.getAllAttributesA());
		AT5611 = getAttrValue("AT5611", actionResult.getAllAttributesA());
		AT5620 = getAttrValue("AT5620", actionResult.getAllAttributesA());
		AT5630 = getAttrValue("AT5630", actionResult.getAllAttributesA());
		AT5640 = getAttrValue("AT5640", actionResult.getAllAttributesA());
		AT5650 = getAttrValue("AT5650", actionResult.getAllAttributesA());
		AT6020 = getAttrValue("AT6020", actionResult.getAllAttributesA());
		AT6030 = getAttrValue("AT6030", actionResult.getAllAttributesA());
		AT6040 = getAttrValue("AT6040", actionResult.getAllAttributesA());
		AT6050 = getAttrValue("AT6050", actionResult.getAllAttributesA());
		ZT0117 = getAttrValue("ZT0117", actionResult.getAllAttributesA());
		ZT0118 = getAttrValue("ZT0118", actionResult.getAllAttributesA());
		ZT0120 = getAttrValue("ZT0120", actionResult.getAllAttributesA());
		ZT0123 = getAttrValue("ZT0123", actionResult.getAllAttributesA());
		ZT0124 = getAttrValue("ZT0124", actionResult.getAllAttributesA());
		ZT0125 = getAttrValue("ZT0125", actionResult.getAllAttributesA());
		ZT0128 = getAttrValue("ZT0128", actionResult.getAllAttributesA());
		ZT0129 = getAttrValue("ZT0129", actionResult.getAllAttributesA());
		ZT0131 = getAttrValue("ZT0131", actionResult.getAllAttributesA());
		ZT0133 = getAttrValue("ZT0133", actionResult.getAllAttributesA());
		ZT0134 = getAttrValue("ZT0134", actionResult.getAllAttributesA());
		ZT0136 = getAttrValue("ZT0136", actionResult.getAllAttributesA());
		ZT0145 = getAttrValue("ZT0145", actionResult.getAllAttributesA());
		ZT0146 = getAttrValue("ZT0146", actionResult.getAllAttributesA());
		ZT0147 = getAttrValue("ZT0147", actionResult.getAllAttributesA());
		ZT0148 = getAttrValue("ZT0148", actionResult.getAllAttributesA());
		ZT0152 = getAttrValue("ZT0152", actionResult.getAllAttributesA());
		ZT0151 = getAttrValue("ZT0151", actionResult.getAllAttributesA());
		ZT0154 = getAttrValue("ZT0154", actionResult.getAllAttributesA());
		ZT0170 = getAttrValue("ZT0170", actionResult.getAllAttributesA());
		ZT0177 = getAttrValue("ZT0177", actionResult.getAllAttributesA());
		ZT0168 = getAttrValue("ZT0168", actionResult.getAllAttributesA());
		/** 以上是调额 **/
		/** 以下是网申 **/
		AT20001 = getAttrValue("AT20001", actionResult.getAllAttributesA());
		AT20002 = getAttrValue("AT20002", actionResult.getAllAttributesA());
		AT20004 = getAttrValue("AT20004", actionResult.getAllAttributesA());
		AT20005 = getAttrValue("AT20005", actionResult.getAllAttributesA());
		AT20006 = getAttrValue("AT20006", actionResult.getAllAttributesA());
		AT20007 = getAttrValue("AT20007", actionResult.getAllAttributesA());
		AT20008 = getAttrValue("AT20008", actionResult.getAllAttributesA());
		AT20009 = getAttrValue("AT20009", actionResult.getAllAttributesA());
		AT20010 = getAttrValue("AT20010", actionResult.getAllAttributesA());
		AT20011 = getAttrValue("AT20011", actionResult.getAllAttributesA());
		ZT0210 = getAttrValue("ZT0210", actionResult.getAllAttributesA());
		ZT0219 = getAttrValue("ZT0219", actionResult.getAllAttributesA());
		ZT0212 = getAttrValue("ZT0212", actionResult.getAllAttributesA());
		ZT0220 = getAttrValue("ZT0220", actionResult.getAllAttributesA());
		ZT0108 = getAttrValue("ZT0108", actionResult.getAllAttributesA());
		ZT0221 = getAttrValue("ZT0221", actionResult.getAllAttributesA());
		ZT0201 = getAttrValue("ZT0201", actionResult.getAllAttributesA());
		ZT0202 = getAttrValue("ZT0202", actionResult.getAllAttributesA());
		ZT0203 = getAttrValue("ZT0203", actionResult.getAllAttributesA());
		/** 以上是网申 **/
		AT3480 = getAttrValue("AT3480", actionResult.getAllAttributesA());
		AT3500 = getAttrValue("AT3500", actionResult.getAllAttributesA());
		AT3930 = getAttrValue("AT3930", actionResult.getAllAttributesA());
		AT3970 = getAttrValue("AT3970", actionResult.getAllAttributesA());
		AT3950 = getAttrValue("AT3950", actionResult.getAllAttributesA());
		ZT0095 = getAttrValue("ZT0095", actionResult.getAllAttributesA());
		ZT0090 = getAttrValue("ZT0090", actionResult.getAllAttributesA());
		ZT0085 = getAttrValue("ZT0085", actionResult.getAllAttributesA());
		ZT0086 = getAttrValue("ZT0086", actionResult.getAllAttributesA());
		ZT0088 = getAttrValue("ZT0088", actionResult.getAllAttributesA());
		ZT0092 = getAttrValue("ZT0092", actionResult.getAllAttributesA());
		ZT0077 = getAttrValue("ZT0077", actionResult.getAllAttributesA());
		ZT0082 = getAttrValue("ZT0082", actionResult.getAllAttributesA());
		DD0030 = getAttrValue("DD0030", actionResult.getAllAttributesA());
		ZT0076 = getAttrValue("ZT0076", actionResult.getAllAttributesA());
		ZT0079 = getAttrValue("ZT0079", actionResult.getAllAttributesA());
		ZT0080 = getAttrValue("ZT0080", actionResult.getAllAttributesA());
		ZT0078 = getAttrValue("ZT0078", actionResult.getAllAttributesA());
		ZT0081 = getAttrValue("ZT0081", actionResult.getAllAttributesA());
		ZT0053 = getAttrValue("ZT0053", actionResult.getAllAttributesA());
		ZT0055 = getAttrValue("ZT0055", actionResult.getAllAttributesA());
		ZT0068 = getAttrValue("ZT0068", actionResult.getAllAttributesA());
		ZT0070 = getAttrValue("ZT0070", actionResult.getAllAttributesA());
		ZT0075 = getAttrValue("ZT0075", actionResult.getAllAttributesA());
		KK0008 = getAttrValue("KK0008", actionResult.getAllAttributesA());
		ZT0093 = getAttrValue("ZT0093", actionResult.getAllAttributesA());
		ZT0084 = getAttrValue("ZT0084", actionResult.getAllAttributesA());
		ZT0087 = getAttrValue("ZT0087", actionResult.getAllAttributesA());
		ZT0074 = getAttrValue("ZT0074", actionResult.getAllAttributesA());
		ZT0089 = getAttrValue("ZT0089", actionResult.getAllAttributesA());
		ZT0083 = getAttrValue("ZT0083", actionResult.getAllAttributesA());
		KK0009 = getAttrValue("KK0009", actionResult.getAllAttributesA());
		ZT0204 = getAttrValue("ZT0204", actionResult.getAllAttributesA());
		ZT0207 = getAttrValue("ZT0207", actionResult.getAllAttributesA());
		ZT0206 = getAttrValue("ZT0206", actionResult.getAllAttributesA());
		EE0087 = getAttrValue("EE0087", actionResult.getAllAttributesA());
		AT0710 = getAttrValue("AT0710", actionResult.getAllAttributesA());
		ZT0181 = getAttrValue("ZT0181", actionResult.getAllAttributesA());
		ZT0182 = getAttrValue("ZT0182", actionResult.getAllAttributesA());
		ZT0183 = getAttrValue("ZT0183", actionResult.getAllAttributesA());
		ZT0184 = getAttrValue("ZT0184", actionResult.getAllAttributesA());
		ZT0185 = getAttrValue("ZT0185", actionResult.getAllAttributesA());
		ZT0186 = getAttrValue("ZT0186", actionResult.getAllAttributesA());
		ZT0187 = getAttrValue("ZT0187", actionResult.getAllAttributesA());
		ZT0188 = getAttrValue("ZT0188", actionResult.getAllAttributesA());
		ZT0189 = getAttrValue("ZT0189", actionResult.getAllAttributesA());
		ZT0190 = getAttrValue("ZT0190", actionResult.getAllAttributesA());
		ZT0191 = getAttrValue("ZT0191", actionResult.getAllAttributesA());
		ZT0192 = getAttrValue("ZT0192", actionResult.getAllAttributesA());
		ZT0193 = getAttrValue("ZT0193", actionResult.getAllAttributesA());
		String ZT0056 = null;
		String ZT0057 = null;
		String ZT0058 = null;
		String ZT0059 = null;
		String ZT0061 = null;
		String ZT0062 = null;
		ZT0056 = getAttrValue("ZT0056", actionResult.getAllAttributesA());
		ZT0057 = getAttrValue("ZT0057", actionResult.getAllAttributesA());
		ZT0058 = getAttrValue("ZT0058", actionResult.getAllAttributesA());
		ZT0059 = getAttrValue("ZT0059", actionResult.getAllAttributesA());
		ZT0061 = getAttrValue("ZT0061", actionResult.getAllAttributesA());
		ZT0062 = getAttrValue("ZT0062", actionResult.getAllAttributesA());
		System.out.println("ZT0056 " + ZT0056);
		System.out.println("ZT0258 " + ZT0258);
		System.out.println("ZT0057 " + ZT0057);
		System.out.println("ZT0058 " + ZT0058);
		System.out.println("ZT0059 " + ZT0059);
		System.out.println("ZT0061 " + ZT0061);
		System.out.println("ZT0062 " + ZT0062);
		System.out.println("ZT0064 " + ZT0064);
		System.out.println("ZT0065 " + ZT0065);
		System.out.println("ZT0066 " + ZT0066);
		System.out.println("A01001 " + A01001);
		System.out.println("AT6100 " + AT6100);
		System.out.println("AT0710 " + AT0710);
		System.out.println("AT0720 " + AT0720);
		System.out.println("AT3480 " + AT3480);
		System.out.println("AT3500 " + AT3500);
		System.out.println("AT3930 " + AT3930);
		System.out.println("AT3950 " + AT3950);
		System.out.println("AT3970 " + AT3970);
		System.out.println("ZT0095 " + ZT0095);
		System.out.println("ZT0090 " + ZT0090);
		System.out.println("ZT0047 " + ZT0047);
		System.out.println("ZT0081" + ZT0081);
		System.out.println("KK0008" + KK0008);
		System.out.println("ZT0093" + ZT0093);
		System.out.println("ZT0084" + ZT0084);
		System.out.println("ZT0087" + ZT0087);
		System.out.println("ZT0074" + ZT0074);
		System.out.println("ZT0089" + ZT0089);
		System.out.println("ZT0083" + ZT0083);
		System.out.println("KK0009" + KK0009);
		String A05002 = null;
		String A05004 = null;
		String A05008 = null;
		String A06002 = null;
		String A06003 = null;
		String A06009 = null;
		String A08004 = null;
		String A10004 = null;
		String ZT0099 = null;
		String DA0090_load = null;
		A05002 = getAttrValue("A05002", actionResult.getAllAttributesA());
		A05004 = getAttrValue("A05004", actionResult.getAllAttributesA());
		A05008 = getAttrValue("A05008", actionResult.getAllAttributesA());
		A06002 = getAttrValue("A06002", actionResult.getAllAttributesA());
		A06003 = getAttrValue("A06003", actionResult.getAllAttributesA());
		A06009 = getAttrValue("A06009", actionResult.getAllAttributesA());
		A08004 = getAttrValue("A08004", actionResult.getAllAttributesA());
		A10004 = getAttrValue("A10004", actionResult.getAllAttributesA());
		ZT0099 = getAttrValue("ZT0099", actionResult.getAllAttributesA());
		DA0090_load = getAttrValue("DA0090", actionResult.getAllAttributesA());

		System.out.println("AT4410----------------" + AT4410);
		System.out.println("AT4420----------------" + AT4420);
		System.out.println("AT4421----------------" + AT4421);
		System.out.println("AT4422----------------" + AT4422);
		System.out.println("AT4430----------------" + AT4430);
		System.out.println("AT4440----------------" + AT4440);
		System.out.println("AT4450----------------" + AT4450);
		System.out.println("AT4451----------------" + AT4451);
		System.out.println("AT4460----------------" + AT4460);
		System.out.println("AT4470----------------" + AT4470);
		System.out.println("AT4480----------------" + AT4480);
		System.out.println("AT4490----------------" + AT4490);
		System.out.println("AT4530----------------" + AT4530);
		System.out.println("AT4550----------------" + AT4550);
		System.out.println("AT4590----------------" + AT4590);
		System.out.println("AT4600----------------" + AT4600);
		System.out.println("AT4610----------------" + AT4610);
		System.out.println("AT4620----------------" + AT4620);
		System.out.println("AT4630----------------" + AT4630);
		System.out.println("AT4640----------------" + AT4640);
		System.out.println("AT4650----------------" + AT4650);
		System.out.println("AT4660----------------" + AT4660);
		System.out.println("AT4670----------------" + AT4670);
		System.out.println("AT4680----------------" + AT4680);
		System.out.println("AT4690----------------" + AT4690);
		System.out.println("AT4700----------------" + AT4700);
		System.out.println("AT4710----------------" + AT4710);
		System.out.println("AT4720----------------" + AT4720);
		System.out.println("AT4721----------------" + AT4721);
		System.out.println("AT4722----------------" + AT4722);
		System.out.println("AT4723----------------" + AT4723);
		System.out.println("AT4724----------------" + AT4724);
		System.out.println("AT4730----------------" + AT4730);
		System.out.println("AT4740----------------" + AT4740);
		System.out.println("AT4750----------------" + AT4750);
		System.out.println("AT4760----------------" + AT4760);
		System.out.println("AT5600----------------" + AT5600);
		System.out.println("AT5601----------------" + AT5601);
		System.out.println("AT5602----------------" + AT5602);
		System.out.println("AT5604----------------" + AT5604);
		System.out.println("AT5605----------------" + AT5605);
		System.out.println("AT5606----------------" + AT5606);
		System.out.println("AT5607----------------" + AT5607);
		System.out.println("AT5608----------------" + AT5608);
		System.out.println("AT5609----------------" + AT5609);
		System.out.println("AT5610----------------" + AT5610);
		System.out.println("AT5611----------------" + AT5611);
		System.out.println("AT5620----------------" + AT5620);
		System.out.println("AT5630----------------" + AT5630);
		System.out.println("AT5640----------------" + AT5640);
		System.out.println("AT5650----------------" + AT5650);
		System.out.println("AT6020----------------" + AT6020);
		System.out.println("AT6030----------------" + AT6030);
		System.out.println("AT6040----------------" + AT6040);
		System.out.println("AT6050----------------" + AT6050);
		System.out.println("AT6060----------------" + AT6060);
		System.out.println("AT6070----------------" + AT6070);
		System.out.println("AT6080----------------" + AT6080);
		System.out.println("AT6090----------------" + AT6090);
		System.out.println("AT9003----------------" + AT9003);
		System.out.println("ZT0180----------------" + ZT0180);
		System.out.println("DA0090_load----------------" + DA0090_load);
		System.out.println("ZT0099：" + ZT0099);
		System.out.println("ZT0210:" + ZT0210);
		System.out.println("ZT0212:" + ZT0212);
		System.out.println("ZT0219:" + ZT0219);
		System.out.println("ZT0220:" + ZT0220);
		System.out.println("ZT0221:" + ZT0221);
		System.out.println("ZT0201:" + ZT0201);
		System.out.println("ZT0202:" + ZT0202);
		System.out.println("ZT0203:" + ZT0203);
		System.out.println("ZT0117 " + ZT0117);
		System.out.println("ZT0118 " + ZT0118);
		System.out.println("ZT0120 " + ZT0120);
		System.out.println("ZT0123 " + ZT0123);
		System.out.println("ZT0124 " + ZT0124);
		System.out.println("ZT0125 " + ZT0125);
		System.out.println("ZT0128 " + ZT0128);
		System.out.println("ZT0129 " + ZT0129);
		System.out.println("ZT0131 " + ZT0131);
		System.out.println("ZT0133 " + ZT0133);
		System.out.println("ZT0134 " + ZT0134);
		System.out.println("ZT0136 " + ZT0136);
		System.out.println("ZT0145 " + ZT0145);
		System.out.println("ZT0146:" + ZT0146);
		System.out.println("ZT0147:" + ZT0147);
		System.out.println("ZT0148:" + ZT0148);
		System.out.println("ZT0152:" + ZT0152);
		System.out.println("ZT0151:" + ZT0151);
		System.out.println("ZT0154:" + ZT0154);
		System.out.println("ZT0170:" + ZT0170);
		System.out.println("ZT0177:" + ZT0177);
		System.out.println("ZT0168:" + ZT0168);
		/** 以上是调额 **/
		/** 以下是网申 **/
		System.out.println("AT20001:" + AT20001);
		System.out.println("AT20002:" + AT20002);
		System.out.println("AT20004:" + AT20004);
		System.out.println("AT20005:" + AT20005);
		System.out.println("AT20006:" + AT20006);
		System.out.println("AT20007:" + AT20007);
		System.out.println("AT20008:" + AT20008);
		System.out.println("AT20009:" + AT20009);
		System.out.println("AT20010:" + AT20010);
		System.out.println("AT20011:" + AT20011);
		System.out.println("ZT0181容忍度额度-------" + ZT0181);
		System.out.println("ZT0182----------------" + ZT0182);
		System.out.println("ZT0183----------------" + ZT0183);
		System.out.println("ZT0184----------------" + ZT0184);
		System.out.println("ZT0185预授信额度------" + ZT0185);
		System.out.println("ZT0186----------------" + ZT0186);
		System.out.println("ZT0187----------------" + ZT0187);
		System.out.println("ZT0188----------------" + ZT0188);
		System.out.println("ZT0189长增预授信额度--" + ZT0189);
		System.out.println("ZT0190临增预授信额度--" + ZT0190);
		System.out.println("ZT0191额度增幅政策----" + ZT0191);
		System.out.println("ZT0192----------------" + ZT0192);
		System.out.println("ZT0193触发期望与最大授信----------------" + ZT0193);

		/** 以上是网申 **/
		System.out.println("A05002----------------" + A05002);
		System.out.println("A05004----------------" + A05004);
		System.out.println("A05008----------------" + A05008);
		System.out.println("A06002----------------" + A06002);
		System.out.println("A06003----------------" + A06003);
		System.out.println("A06009----------------" + A06009);
		System.out.println("A08004----------------" + A08004);
		System.out.println("A10004----------------" + A10004);
		String FA0001 = null;
		String FA0002 = null;
		String FA0003 = null;
		String FA0005 = null;
		String FA0006 = null;
		String FA0007 = null;
		String FA0008 = null;
		String FA0009 = null;
		String FA0010 = null;
		String FA0011 = null;
		String FA0020 = null;
		String FA0021 = null;
		String FA0022 = null;
		// String ZT0108 = null;
		// String ZT0221 = null;
		// String ZT0201 = null;
		// String ZT0202 = null;
		// String ZT0203 = null;
		FA0001 = getAttrValue("FA0001", actionResult.getAllAttributesA());
		FA0002 = getAttrValue("FA0002", actionResult.getAllAttributesA());
		FA0003 = getAttrValue("FA0003", actionResult.getAllAttributesA());
		FA0005 = getAttrValue("FA0005", actionResult.getAllAttributesA());
		FA0006 = getAttrValue("FA0006", actionResult.getAllAttributesA());
		FA0007 = getAttrValue("FA0007", actionResult.getAllAttributesA());
		FA0008 = getAttrValue("FA0008", actionResult.getAllAttributesA());
		FA0009 = getAttrValue("FA0009", actionResult.getAllAttributesA());
		FA0010 = getAttrValue("FA0010", actionResult.getAllAttributesA());
		FA0011 = getAttrValue("FA0011", actionResult.getAllAttributesA());
		FA0020 = getAttrValue("FA0020", actionResult.getAllAttributesA());
		FA0021 = getAttrValue("FA0021", actionResult.getAllAttributesA());
		FA0022 = getAttrValue("FA0022", actionResult.getAllAttributesA());
		DownLog = getAttrValue("DA0050", actionResult.getAllAttributesA());
		System.out.println("DownLog>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + DownLog);
		EE0032 = getAttrValue("EE0032", actionResult.getAllAttributesA());
		AT0070 = getAttrValue("AT0070", actionResult.getAllAttributesA());
		AT0042 = getAttrValue("AT0041", actionResult.getAllAttributesA());
		DD0021 = getAttrValue("DD0021", actionResult.getAllAttributesA());
		DD0020 = getAttrValue("DD0020", actionResult.getAllAttributesA());
		DD0030 = getAttrValue("DD0030", actionResult.getAllAttributesA());
		KK0008 = getAttrValue("KK0008", actionResult.getAllAttributesA());
		AT3110 = getAttrValue("AT3110", actionResult.getAllAttributesA());
		KK0005 = getAttrValue("KK0005", actionResult.getAllAttributesA());
		KK0001 = getAttrValue("KK0001", actionResult.getAllAttributesA());
		KK0009 = getAttrValue("KK0009", actionResult.getAllAttributesA());
		EE0033 = getAttrValue("EE0033", actionResult.getAllAttributesA());
		EE0034 = getAttrValue("EE0034", actionResult.getAllAttributesA());
		EE0035 = getAttrValue("EE0035", actionResult.getAllAttributesA());
		EE0036 = getAttrValue("EE0036", actionResult.getAllAttributesA());
		EE0037 = getAttrValue("EE0037", actionResult.getAllAttributesA());
		EE0045 = getAttrValue("EE0045", actionResult.getAllAttributesA());
		HA0001 = getAttrValue("HA0001", actionResult.getAllAttributesA());
		HA0003 = getAttrValue("HA0003", actionResult.getAllAttributesA());
		HA0004 = getAttrValue("HA0004", actionResult.getAllAttributesA());
		HA0005 = getAttrValue("HA0005", actionResult.getAllAttributesA());
		HA0006 = getAttrValue("HA0006", actionResult.getAllAttributesA());
		HA0007 = getAttrValue("HA0007", actionResult.getAllAttributesA());
		HA0009 = getAttrValue("HA0009", actionResult.getAllAttributesA());
		HA0010 = getAttrValue("HA0010", actionResult.getAllAttributesA());
		HA0011 = getAttrValue("HA0011", actionResult.getAllAttributesA());
		HA0019 = getAttrValue("HA0019", actionResult.getAllAttributesA());
		HA0020 = getAttrValue("HA0020", actionResult.getAllAttributesA());
		HA0021 = getAttrValue("HA0021", actionResult.getAllAttributesA());
		EE0034 = getAttrValue("EE0034", actionResult.getAllAttributesA());
		EE0033 = getAttrValue("EE0033", actionResult.getAllAttributesA());
		KK0004 = getAttrValue("KK0004", actionResult.getAllAttributesA());
		EE0039 = getAttrValue("EE0039", actionResult.getAllAttributesA());
		AT4210 = getAttrValue("AT4210", actionResult.getAllAttributesA());
		AT0040 = getAttrValue("AT0040", actionResult.getAllAttributesA());
		AT0041 = getAttrValue("AT0041", actionResult.getAllAttributesA());
		AT3530 = getAttrValue("AT3530", actionResult.getAllAttributesA());
		AT3270 = getAttrValue("AT3270", actionResult.getAllAttributesA());
		AT2320 = getAttrValue("AT2320", actionResult.getAllAttributesA());
		ArrayList list = new ArrayList();
		list.add(getAttrValue("KK0010", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0011", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0012", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0013", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0014", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0015", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0016", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0017", actionResult.getAllAttributesA()));
		list.add(getAttrValue("KK0018", actionResult.getAllAttributesA()));
		System.out.println("AT2320:" + AT2320);
		for (int i = 0; i < list.size(); i++) {
			String lenthg = (String) list.get(i);
			if (lenthg.length() > 0) {
				cards = (String) list.get(i);
			}

		}

		String KK0010 = getAttrValue("KK0010", actionResult.getAllAttributesA());
		String KK0011 = getAttrValue("KK0011", actionResult.getAllAttributesA());
		String KK0012 = getAttrValue("KK0012", actionResult.getAllAttributesA());
		String KK0013 = getAttrValue("KK0013", actionResult.getAllAttributesA());
		String KK0014 = getAttrValue("KK0014", actionResult.getAllAttributesA());
		String KK0015 = getAttrValue("KK0015", actionResult.getAllAttributesA());
		String KK0016 = getAttrValue("KK0016", actionResult.getAllAttributesA());
		String KK0017 = getAttrValue("KK0017", actionResult.getAllAttributesA());
		String KK0018 = getAttrValue("KK0018", actionResult.getAllAttributesA());
		System.out.println("Cards " + cards);
		System.out.println("EE0033 " + EE0033);
		System.out.println("EE0034 " + EE0034);
		System.out.println("EE0035 " + EE0035);
		System.out.println("EE0036 " + EE0036);
		System.out.println("EE0037 " + EE0037);
		System.out.println("EE0045 " + EE0045);
		System.out.println("HA0001 " + HA0001);
		System.out.println("HA0003 " + HA0003);
		System.out.println("HA0004 " + HA0004);
		System.out.println("HA0005 " + HA0005);
		System.out.println("HA0006 " + HA0006);
		System.out.println("HA0007 " + HA0007);
		System.out.println("HA0009 " + HA0009);
		System.out.println("HA0010 " + HA0010);
		System.out.println("HA0011 " + HA0011);
		System.out.println("HA0019 " + HA0019);
		System.out.println("HA0020 " + HA0020);
		System.out.println("HA0021 " + HA0021);
		System.out.println("EE0034 " + EE0034);
		System.out.println("EE0033 " + EE0033);
		System.out.println("KK0004 " + KK0004);
		System.out.println("EE0039 " + EE0039);
		System.out.println("AT0041 " + AT0042);
		System.out.println("DD0021 " + DD0021);
		System.out.println("AT4210 " + AT4210);
		System.out.println("KK0005 " + KK0005);
		System.out.println("AT0070 " + AT0070);
		System.out.println("AT0040 " + AT0040);
		System.out.println("EE0032 " + EE0032);
		System.out.println("FA0001 " + FA0001);
		System.out.println("FA0002 " + FA0002);
		System.out.println("FA0003 " + FA0003);
		System.out.println("FA0005 " + FA0005);
		System.out.println("FA0006 " + FA0006);
		System.out.println("FA0007 " + FA0007);
		System.out.println("FA0008 " + FA0008);
		System.out.println("FA0009 " + FA0009);
		System.out.println("FA0010 " + FA0010);
		System.out.println("FA0011 " + FA0011);
		System.out.println("FA0020 " + FA0020);
		System.out.println("FA0021 " + FA0021);
		System.out.println("FA0022 " + FA0022);
		String AT3870 = null;
		String AT3890 = null;
		String AT3910 = null;
		AT3870 = getAttrValue("AT3870", actionResult.getAllAttributesA());
		AT3890 = getAttrValue("AT3890", actionResult.getAllAttributesA());
		AT3910 = getAttrValue("AT3910", actionResult.getAllAttributesA());
		System.out.println("AT3870" + AT3870);
		System.out.println("AT3890" + AT3890);
		System.out.println("AT3910" + AT3910);
		System.out.println("AT3930" + AT3930);
		System.out.println("AT3950" + AT3950);
		System.out.println("AT3970" + AT3970);
		
		/***
		 * 2015-11-14
		 * 批次：C1507添加
		 * @author 刘洋
		 * star
		 */
		String ZT01102 = null;//一次决策 额度算法判断
		String ZT0110 = null;//二次决策 额度算法判断
		String ZT0249 = null;//行内资产 一次决策ABD全局
		String ZT02491 = null;//行内资产二次决策ABD全局
		String ZT0250 = null;//普通客户ABD全局
		String ZT02501 = null;//行内房贷 一次决策ABD全局
		String ZT02503 = null;//行内房贷 二次决策ABD全局
		String ZT02502 = null;//网申矩阵ABD
		String ZT0251 = null;//全局ABD
		String ZT0269 = null;//房贷下限
		String ZT0263 = null;
		String ZT0262 = null;
		String ZT0265 = null;
		String ZT0266 = null;
		String ZT0267 = null;
		String ZT02671 = null;//取MAX（房贷，小额矩阵，人行收入）
		String ZT0268 = null;
		String ZT0109 = null;
		ZT02491 = getAttrValue("ZT02491", actionResult.getAllAttributesA());
		ZT0263 = getAttrValue("ZT0263", actionResult.getAllAttributesA());
		ZT0262 = getAttrValue("ZT0262", actionResult.getAllAttributesA());
		ZT0265 = getAttrValue("ZT0265", actionResult.getAllAttributesA());
		ZT0266 = getAttrValue("ZT0266", actionResult.getAllAttributesA());
		ZT0267 = getAttrValue("ZT0267", actionResult.getAllAttributesA());
		ZT0268 = getAttrValue("ZT0268", actionResult.getAllAttributesA());
		ZT01102 = getAttrValue("ZT01102", actionResult.getAllAttributesA());
		ZT0110 = getAttrValue("ZT0110", actionResult.getAllAttributesA());
		ZT0249 = getAttrValue("ZT0249", actionResult.getAllAttributesA());
		ZT0250 = getAttrValue("ZT0250", actionResult.getAllAttributesA());
		ZT02501 = getAttrValue("ZT02501", actionResult.getAllAttributesA());
		ZT02503 = getAttrValue("ZT02503", actionResult.getAllAttributesA());
		ZT02502 = getAttrValue("ZT02502", actionResult.getAllAttributesA());
		ZT0251 = getAttrValue("ZT0251", actionResult.getAllAttributesA());
		ZT0269 = getAttrValue("ZT0269", actionResult.getAllAttributesA());
		ZT0109 = getAttrValue("ZT0109", actionResult.getAllAttributesA());
		ZT02671 = getAttrValue("ZT02671", actionResult.getAllAttributesA());
		System.out.println("ZT0108 根据电话2012年平均工资:" + ZT0108);
		System.out.println("ZT0109 根据BH2012年平均工资:" + ZT0109);
		System.out.println("ZT0263  一次决策最终结果：" + ZT0263);
		System.out.println("ZT0265 取Max（房贷，收入）：" + ZT0265);
		System.out.println("ZT0266 取MAX（房贷，个人，行内资产）" + ZT0266);
		System.out.println("ZT0267 取MAX（房贷，小额矩阵）" + ZT0267);
		System.out.println("ZT02671 取MAX（房贷，小额矩阵,收入）" + ZT02671);
		System.out.println("ZT0268 取MAX（房贷，小额矩阵，行内资产，收入）" + ZT0268);
		System.out.println("ZT01102  一次决策 额度算法判断 " + ZT01102);
		System.out.println("ZT0110 二次决策 额度算法判断 " + ZT0110);
		System.out.println("ZT0249 行内资产 一次决策ABD全局 " + ZT0249);
		System.out.println("ZT02491 行内资产二次决策ABD全局 " + ZT02491);
		System.out.println("ZT0250 普通客户ABD全局 " + ZT0250);
		System.out.println("ZT02501 行内房贷 一次决策ABD全局 " + ZT02501);
		System.out.println("ZT02503 行内房贷 二次决策ABD全局  " + ZT02503);
		System.out.println("ZT02502 网申矩阵ABD " + ZT02502);
		System.out.println("ZT0251 全局ABD " + ZT0251);
		System.out.println("ZT0269 房贷下限 " + ZT0269);
		
		/**C1507 End*/
		/**C1601 解问题版本**/
		String KK00101 = null;
		String KK00111 = null;
		String KK00121 = null;
		String KK00131 = null;
		String KK00141 = null;
		String KK00151 = null;
		String KK00161 = null;
		String KK00171 = null;
		String KK00181 = null;
		String DD0031 = null;
		String DD0032 = null;
		String DD0033 = null;
		String DD0034 = null;
		String DD0035 = null;
		String ZT00921 = null;
		String ZT00923 = null;
		String WA0001 = null;
		String WA0002 = null;
		String WA0003 = null;
		String WA0004 = null;
		String WA0005 = null;
		String WA0006 = null;
		String WA0007 = null;
		String WA0008 = null;
		String WA0009 = null;
		String WA0010 = null;
		String WA0011 = null;
		String WA0012 = null;

		String YA0001 = null;
		String YA0002 = null;
		String YA0003 = null;
		String YA0004 = null;
		String YA0005 = null;
		String YA0006 = null;
		String YA0007 = null;
		String YA0008 = null;
		String YA0009 = null;
		String YA0010 = null;
		String YA0011 = null;
		String YA0012 = null;
		String YA0013 = null;
		String YA0014 = null;
		String YA0015 = null;
		String YA0017 = null;
		String YA0018 = null;
		String YA0019 = null;
		String KK0020 = null;
		String KK0021 = null;
		String KK0022 = null;
		String KK0023 = null;
		ZT00921 = getAttrValue("ZT00921", actionResult.getAllAttributesA());
		ZT00923 = getAttrValue("ZT00923", actionResult.getAllAttributesA());
		KK00101 = getAttrValue("KK00101", actionResult.getAllAttributesA());
		KK00111 = getAttrValue("KK00111", actionResult.getAllAttributesA());
		KK00121 = getAttrValue("KK00121", actionResult.getAllAttributesA());
		KK00131 = getAttrValue("KK00131", actionResult.getAllAttributesA());
		KK00141 = getAttrValue("KK00141", actionResult.getAllAttributesA());
		KK00151 = getAttrValue("KK00151", actionResult.getAllAttributesA());
		KK00161 = getAttrValue("KK00161", actionResult.getAllAttributesA());
		KK00171 = getAttrValue("KK00171", actionResult.getAllAttributesA());
		KK00181 = getAttrValue("KK00181", actionResult.getAllAttributesA());
		DD0031 = getAttrValue("DD0031", actionResult.getAllAttributesA());
		DD0032 = getAttrValue("DD0032", actionResult.getAllAttributesA());
		DD0033 = getAttrValue("DD0033", actionResult.getAllAttributesA());
		DD0034 = getAttrValue("DD0034", actionResult.getAllAttributesA());
		DD0035 = getAttrValue("DD0035", actionResult.getAllAttributesA());
		WA0001 = getAttrValue("WA0001", actionResult.getAllAttributesA());
		WA0002 = getAttrValue("WA0002", actionResult.getAllAttributesA());
		WA0003 = getAttrValue("WA0003", actionResult.getAllAttributesA());
		WA0004 = getAttrValue("WA0004", actionResult.getAllAttributesA());
		WA0005 = getAttrValue("WA0005", actionResult.getAllAttributesA());
		WA0006 = getAttrValue("WA0006", actionResult.getAllAttributesA());
		WA0007 = getAttrValue("WA0007", actionResult.getAllAttributesA());
		WA0008 = getAttrValue("WA0008", actionResult.getAllAttributesA());
		WA0009 = getAttrValue("WA0009", actionResult.getAllAttributesA());
		WA0010 = getAttrValue("WA0010", actionResult.getAllAttributesA());
		WA0011 = getAttrValue("WA0011", actionResult.getAllAttributesA());
		WA0012 = getAttrValue("WA0012", actionResult.getAllAttributesA());

		YA0001 = getAttrValue("YA0001", actionResult.getAllAttributesA());
		YA0002 = getAttrValue("YA0002", actionResult.getAllAttributesA());
		YA0003 = getAttrValue("YA0003", actionResult.getAllAttributesA());
		YA0004 = getAttrValue("YA0004", actionResult.getAllAttributesA());
		YA0005 = getAttrValue("YA0005", actionResult.getAllAttributesA());
		YA0006 = getAttrValue("YA0006", actionResult.getAllAttributesA());
		YA0007 = getAttrValue("YA0007", actionResult.getAllAttributesA());
		YA0008 = getAttrValue("YA0008", actionResult.getAllAttributesA());
		YA0009 = getAttrValue("YA0009", actionResult.getAllAttributesA());
		YA0010 = getAttrValue("YA0010", actionResult.getAllAttributesA());
		YA0011 = getAttrValue("YA0011", actionResult.getAllAttributesA());
		YA0012 = getAttrValue("YA0012", actionResult.getAllAttributesA());
		YA0013 = getAttrValue("YA0013", actionResult.getAllAttributesA());
		YA0014 = getAttrValue("YA0014", actionResult.getAllAttributesA());
		YA0015 = getAttrValue("YA0015", actionResult.getAllAttributesA());
		YA0017 = getAttrValue("YA0017", actionResult.getAllAttributesA());
		YA0018 = getAttrValue("YA0018", actionResult.getAllAttributesA());
		YA0019 = getAttrValue("YA0019", actionResult.getAllAttributesA());
		KK0020 = getAttrValue("KK0020", actionResult.getAllAttributesA());
		KK0021 = getAttrValue("KK0021", actionResult.getAllAttributesA());
		KK0022 = getAttrValue("KK0022", actionResult.getAllAttributesA());
		KK0023 = getAttrValue("KK0023", actionResult.getAllAttributesA());
		System.out.println("ZT0085：收入类" + ZT0085);
		System.out.println("ZT0204：行内资产" + ZT0204);
		System.out.println("ZT0207：网申矩阵" + ZT0207);
		System.out.println("ZT0206：老客户" + ZT0206);
		System.out.println("ZT0262  房贷额度：" + ZT0262);
		System.out.println("EE0087：冠军额度" + EE0087);
		System.out.println("ZT0088：他行客户" + ZT0088);
		System.out.println("ZT0074 A:" + ZT0074);
		System.out.println("ZT0053 B:" + ZT0053);
		System.out.println("ZT0055 C:" + ZT0055);
		System.out.println("ZT0068 D:" + ZT0068);
		System.out.println("ZT0070 E:" + ZT0070);
		System.out.println("DD0031：" + DD0031);
		System.out.println("DD0032：" + DD0032);
		System.out.println("DD0033：" + DD0033);
		System.out.println("DD0034：" + DD0034);
		System.out.println("DD0035：" + DD0035);
		System.out.println("ZT0075 :" + ZT0075);
		System.out.println("DD0030 :" + DD0030);
		System.out.println("ZT0086 :" + ZT0086);
		System.out.println("ZT0092 :" + ZT0092);
		System.out.println("ZT0082 :" + ZT0082);
		System.out.println("ZT0076 :" + ZT0076);
		System.out.println("ZT0077 :" + ZT0077);
		System.out.println("ZT0078 :" + ZT0078);
		System.out.println("ZT0079 :" + ZT0079);
		System.out.println("ZT0080 :" + ZT0080);
		System.out.println("ZT00921：" + ZT00921);
		System.out.println("ZT00923：" + ZT00923);
		System.out.println("*****无实质评分值****");
		System.out.println("KK0010:" + KK0010);
		System.out.println("KK0011:" + KK0011);
		System.out.println("KK0012:" + KK0012);
		System.out.println("KK0013:" + KK0013);
		System.out.println("KK0014:" + KK0014);
		System.out.println("KK0015:" + KK0015);
		System.out.println("KK0016:" + KK0016);
		System.out.println("KK0017:" + KK0017);
		System.out.println("KK0018:" + KK0018);
		System.out.println("*****有实质评分值****");
		System.out.println("KK00101：" + KK00101);
		System.out.println("KK00111：" + KK00111);
		System.out.println("KK00121：" + KK00121);
		System.out.println("KK00131：" + KK00131);
		System.out.println("KK00141：" + KK00141);
		System.out.println("KK00151：" + KK00151);
		System.out.println("KK00161：" + KK00161);
		System.out.println("KK00171：" + KK00171);
		System.out.println("KK00181：" + KK00181);
		System.out.println("*****无实质评分中间变量****");
		System.out.println("WA0001:" + WA0001);
		System.out.println("WA0002:" + WA0002);
		System.out.println("WA0003:" + WA0003);
		System.out.println("WA0004:" + WA0004);
		System.out.println("WA0005:" + WA0005);
		System.out.println("WA0006:" + WA0006);
		System.out.println("WA0007:" + WA0007);
		System.out.println("WA0008:" + WA0008);
		System.out.println("WA0009:" + WA0009);
		System.out.println("WA0010:" + WA0010);
		System.out.println("WA0011:" + WA0011);
		System.out.println("*****有实质评分中间变量****");
		System.out.println("YA0001:" + YA0001);
		System.out.println("YA0002:" + YA0002);
		System.out.println("YA0003:" + YA0003);
		System.out.println("YA0004:" + YA0004);
		System.out.println("YA0005:" + YA0005);
		System.out.println("YA0006:" + YA0006);
		System.out.println("YA0007:" + YA0007);
		System.out.println("YA0008:" + YA0008);
		System.out.println("YA0009:" + YA0009);
		System.out.println("YA0010:" + YA0010);
		System.out.println("YA0011:" + YA0011);
		System.out.println("YA0012:" + YA0012);
		System.out.println("YA0013:" + YA0013);
		System.out.println("YA0014:" + YA0014);
		System.out.println("YA0015:" + YA0015);
		System.out.println("YA0017:" + YA0017);
		System.out.println("YA0018:1有实质2无实质：" + YA0018);
		System.out.println("WA0012:无实质评分值 ：" + WA0012);
		System.out.println("YA0019:有实质评分值：" + YA0019);
		System.out.println("KK0020:无实质评分分组： " + KK0020);
		System.out.println("KK0021:有实质评分分组：" + KK0021);
		System.out.println("KK0022：" + KK0022);
		System.out.println("KK0023：" + KK0023);
		System.out.println("KK0001:" + KK0001);
		System.out.println("KK0008:" + KK0008);
		System.out.println("KK0009:" + KK0009);
		/**C1601 End*/
		double num1 = 0;

		// num1 =
		// Double.valueOf(FA0021)+Double.valueOf(FA0020)+Double.valueOf(FA0010)+Double.valueOf((FA0009))+Double.valueOf(FA0008)+Double.valueOf(FA0007)+Double.valueOf(FA0006)+Double.valueOf(FA0005)+Double.valueOf(FA0003)+Double.valueOf(FA0002)+Double.valueOf(FA0001);
		// String strs1 = String.valueOf(num1);
		// System.out.println("number "+strs1);
		if ((EE0045.equals("") || EE0045 == null || EE0045 == "")
				&& !AT3530.equals("8420") && AT3270.equals("1")) {
			if (AT4210.equals("1")) {
				LogManager.getInstance().toCdsLog("产品为零额度");
			}
			StringBuffer strs = new StringBuffer();
			// String str1 = null;
			if (!"".equals(AT0040) || AT0040 != null) {
				strs.append("额度值等于0;");
				strs.append("产品名称：" + AT0040 + ";");
				// str1 += "额度值等于0;"+"产品名称："+AT0040+";";

			} else {
				strs.append("额度值等于0;");
				strs.append("产品名称：null;");
				// str1 += "额度值等于0;"+"产品名称：null;";
			}
			if (!"".equals(AT0041) || AT0041 != null) {
				strs.append("卡片类型：" + AT0041 + ";");
				// str1 += "卡片类型："+AT0041+";";
			} else {
				strs.append("卡片类型：null;");
				// str1 += "卡片类型：null;";
			}
			if (!"".equals(AT3530) || AT3530 != null) {
				strs.append("客户类型：" + AT3530 + ";");
				// str1 += "客户类型："+AT3530+";";
			} else {
				strs.append("客户类型：null;");
				// str1 += "客户类型：null;";
			}
			if (!"".equals(KK0001) || KK0001 != null) {
				strs.append("评分值：" + KK0001 + ";");
				// str1 += "评分值："+KK0001+";";
			} else {
				strs.append("评分值：null;");
				// str1 += "评分值：null;";
			}
			if (!"".equals(cards) || cards != null) {
				strs.append("评分组：" + cards + ";");
				// str1 += "评分组："+cards+";";
			} else {
				strs.append("评分组：null;");
				// str1 += "评分组：null;";
			}
			if (!"".equals(EE0034) || EE0034 != null) {
				strs.append("中高收入判断：" + EE0034 + ";");
				// str1 += "中高收入判断："+EE0034+";";
			} else {
				strs.append("中高收入判断：null;");
				// str1 += "中高收入判断：null;";
			}
			if (!"".equals(EE0039) || EE0039 != null) {
				strs.append("特殊额度判断：" + EE0039 + "。");
				// str1 += "特殊额度判断："+EE0039+"。";
			} else {
				strs.append("特殊额度判断：null。");
				// str1 += "特殊额度判断：null。";
			}
			LogManager.getInstance().toCdsLog(strs.toString());
		}
		// 可证明年收入总额
		String tmpStr2 = null;
		tmpStr2 = getAttrValue("DD0010", actionResult.getAllAttributesA());
		tmpInt = tmpStr2.indexOf(".");
		if (tmpInt > 0) {
			tmpStr2 = tmpStr2.substring(0, tmpInt);
		}
		try {
			cdseResult.setAprAnnIncome(tmpStr2);
			// System.out.print("tmpStr2==========="+actionResult.getAllAttributesA()
			// );
		} catch (Exception ex2) {
			cdseResult.setAprAnnIncome(null); // 风险评分
		}
		/**C1601中添加*/
		StringBuffer output = new StringBuffer();
		output.append("YA0018="+getAttrValue("YA0018", actionResult.getAllAttributesA()));
		output.append(",KK0008="+getAttrValue("KK0008", actionResult.getAllAttributesA()));
		output.append(",KK0009="+getAttrValue("KK0009", actionResult.getAllAttributesA()));
		output.append(",ZT0074A="+getAttrValue("ZT0074", actionResult.getAllAttributesA()));
		output.append(",ZT0053B="+getAttrValue("ZT0053", actionResult.getAllAttributesA()));
		output.append(",ZT0055C="+getAttrValue("ZT0055", actionResult.getAllAttributesA()));
		output.append(",ZT0068D="+getAttrValue("ZT0068", actionResult.getAllAttributesA()));
		output.append(",ZT0070E="+getAttrValue("ZT0070", actionResult.getAllAttributesA()));
		output.append(",\r\nZT0075="+getAttrValue("ZT0075", actionResult.getAllAttributesA()));
		output.append(",DD0030="+getAttrValue("DD0030", actionResult.getAllAttributesA()));
		output.append(",ZT0077="+getAttrValue("ZT0077", actionResult.getAllAttributesA()));
		output.append(",ZT0082="+getAttrValue("ZT0082", actionResult.getAllAttributesA()));
		output.append(",ZT0092="+getAttrValue("ZT0092", actionResult.getAllAttributesA()));
		output.append(",ZT0085SR="+getAttrValue("ZT0085", actionResult.getAllAttributesA()));
		output.append(",\r\nZT0086ZC="+getAttrValue("ZT0086", actionResult.getAllAttributesA()));
		output.append(",ZT0262FD="+getAttrValue("ZT0262", actionResult.getAllAttributesA()));
		output.append(",ZT0205XE="+getAttrValue("ZT0205", actionResult.getAllAttributesA()));
		output.append(",ZT0201="+getAttrValue("ZT0201", actionResult.getAllAttributesA()));
		output.append(",ZT0202="+getAttrValue("ZT0202", actionResult.getAllAttributesA()));
		output.append(",ZT0108="+getAttrValue("ZT0108", actionResult.getAllAttributesA()));
		output.append(",ZT0109="+getAttrValue("ZT0109", actionResult.getAllAttributesA()));
		output.append(",ZT0033A1="+getAttrValue("ZT0033", actionResult.getAllAttributesA()));
		output.append(",\r\nZT0034A2="+getAttrValue("ZT0034", actionResult.getAllAttributesA()));
		output.append(",ZT0035A3="+getAttrValue("ZT0035", actionResult.getAllAttributesA()));
		
		output.append(",ZT0037B1="+getAttrValue("ZT0037", actionResult.getAllAttributesA()));
		output.append(",ZT0038B2="+getAttrValue("ZT0038", actionResult.getAllAttributesA()));
		output.append(",ZT0039B3="+getAttrValue("ZT0039", actionResult.getAllAttributesA()));
		output.append(",ZT0040B4="+getAttrValue("ZT0040", actionResult.getAllAttributesA()));
		output.append(",\r\nZT0041B5="+getAttrValue("ZT0041", actionResult.getAllAttributesA()));
		output.append(",ZT0042B6="+getAttrValue("ZT0042", actionResult.getAllAttributesA()));
		output.append(",ZT0043B7="+getAttrValue("ZT0043", actionResult.getAllAttributesA()));
		output.append(",ZT0054C1="+getAttrValue("ZT0054", actionResult.getAllAttributesA()));
		
		output.append(",ZT0056D1="+getAttrValue("ZT0056", actionResult.getAllAttributesA()));
		output.append(",ZT0057D2="+getAttrValue("ZT0057", actionResult.getAllAttributesA()));
		output.append(",\r\nZT0058D3="+getAttrValue("ZT0058", actionResult.getAllAttributesA()));
		output.append(",ZT0059D4="+getAttrValue("ZT0059", actionResult.getAllAttributesA()));
		output.append(",ZT0061D5="+getAttrValue("ZT0056", actionResult.getAllAttributesA()));
		output.append(",ZT0062D6="+getAttrValue("ZT0056", actionResult.getAllAttributesA()));//2016-2-18 getAttrValue("ZT0056")修改为正确的
		output.append(",ZT0076="+getAttrValue("ZT0076", actionResult.getAllAttributesA()));
		output.append(",ZT0099="+getAttrValue("ZT0099", actionResult.getAllAttributesA()));
		String strategyOutput = "";
		if(output.length()>=1000){
			strategyOutput = output.substring(0, 999);
		}else{
			strategyOutput = output.toString();
		}
		try {
			if("1".equals(status)){
				cdseResult.setStrategiesLogID(strategyOutput);				
			}else{
				cdseResult.setStrategiesLogID(cdseResult.getApplicationId()+ CDSEUtil.getTime());
			}
		} catch (Exception ex2) {
			cdseResult.setStrategiesLogID(null);// 输出项
		}
		// 2015-10-21
		String isChallenge = getAttrValue("ZT0047",
				actionResult.getAllAttributesA());
		try {
			cdseResult.setIsChallenge(isChallenge);
		} catch (Exception ex2) {
			cdseResult.setIsChallenge(null);// 是否冠军挑战，1冠军2挑战
		}
		// 2015-10-21
		String DecisionPoint = getAttrValue("AT20011",
				actionResult.getAllAttributesA());
		try {
			cdseResult.setDecisionPoint(DecisionPoint);
		} catch (Exception ex2) {
			cdseResult.setDecisionPoint(null);// 一次决策或二次决策，只有1和2
		}
		// ///////////////////////
		// 2004-3-24 根据Doctor Wang的需求将输出风险度评估值修改风险度评估10级分组值
		tmpStr = getAttrValue("CG0015", actionResult.getAllAttributesA());
		tmpInt = tmpStr.indexOf(".");
		if (tmpInt > 0) {
			tmpStr = tmpStr.substring(0, tmpInt);
		}
		try {
			cdseResult.setRiskScore(Integer.parseInt(tmpStr));
		} catch (Exception ex2) { // 风险度评估值
			cdseResult.setRiskScore(0);
		}

		// 2004-3-24 根据Doctor Wang的需求将输出使用度评估值修改为输出使用度评估10级分组值
		tmpStr = getAttrValue("CG0115", actionResult.getAllAttributesA());
		tmpInt = tmpStr.indexOf(".");
		if (tmpInt > 0) {
			tmpStr = tmpStr.substring(0, tmpInt);
		}
		try {
			cdseResult.setUsageScore(Integer.parseInt(tmpStr));
		} catch (Exception ex2) {
			cdseResult.setUsageScore(0); // 使用度评估值
		}

		if (actionResult.getActionId().equals("-888")) {
			actionResult.setActionId("C");
			actionResult.setActionNa("入组拒绝");
			actionResult.setReasonDescription("因入组条件不满足而拒绝");
			System.out.println("入组条件不满足而拒绝");
		}

		cdseResult.setPrincipalResultID(actionResult.getActionId());
		cdseResult.setPrincipalResultDescription(actionResult.getActionNa());

		// 设置授信策略编号版本
		// String str = cdseResult.getCreditScore();
		// System.out.println("str:"+str);
		cdseResult.setAcquStrategyID(actionResult.getStrategyNo() + "_"
				+ actionResult.getStrategyVer() + "_"
				+ actionResult.getActionId());

		// 修改部分 判断是否升降级 如果有加相应的策略编号和版本
		/*
		 * if (cdseResult.getHasDownSell() == 0) { cdseResult.setStrategyID(" |"
		 * + actionResult.getStrategyNo() + "_" + actionResult.getStrategyVer()
		 * + "|"); }
		 */

		try {
			cdseResult.setUnderwritingReasonCode(actionResult.getReasonCode()); // 授信原因代码
		} catch (Exception ex1) {
			cdseResult.setUnderwritingReasonCode("DR111");
			cdseResult.setStrategyID(actionResult.getStrategyNo() + "_"
					+ actionResult.getStrategyVer() + "| |");
		}
		if (cdseResult.getHasDownSell() == 1
				|| cdseResult.getHasDownSell() == 2) {
		}

		StringBuffer reasonDescription = new StringBuffer(
				actionResult.getReasonDescription());
		if ("C".equalsIgnoreCase(actionResult.getActionId())) {
			reasonDescription.append("；入组条件为：");
			reasonDescription.append(actionResult.getAttributesString());
		}
		if (actionResult.getBadNum() > 0) {
			reasonDescription.append("；决策过程提示：");
			reasonDescription.append(actionResult.getBadString());
		}
		if (!"".equals(actionResult.getWarnDescStr())) {
			reasonDescription.append("；");
			reasonDescription.append(actionResult.getWarnDescStr());
		}
		cdseResult.setUnderwritingReasonDescription(reasonDescription
				.toString()); // 授信原因描述
		cdseResult.setReservedField1(actionResult.getAttributesString());
		ArrayList paramList;
		StrategyParameter param = null;
		String creditScore = "0";
		String DA0040 = null;
		String DA0090Str = null;
		int DA0090 = 0;

		if (!"C".equalsIgnoreCase(actionResult.getActionId())) {
			paramList = actionResult.getParaList();
			for (int i = 0; i < paramList.size(); ++i) {
				param = (StrategyParameter) paramList.get(i);
				if ("SCORE".equalsIgnoreCase(param.getName())) {
					creditScore = param.getValue();
				}

				if ("DA0040".equals(param.getName())) {
					DA0040 = param.getValue();
				}
				if ("DA0090".equals(param.getName())) {
					DA0090Str = param.getValue();
					if (DA0090Str != null && !"".equals(DA0090Str.trim())) {
						bd = new BigDecimal(DA0090Str);
						DA0090 = bd.setScale(0, BigDecimal.ROUND_HALF_UP)
								.intValue();
					}
				}
				/*
				 * if("DD0080".equals(param.getName())){ DD0080 =
				 * param.getValue(); } cdseResult.setAprAnnIncome(DD0080);
				 * System.out.println("DD0080=========="+DD0080);
				 */
			}

			try {
				tmpInt = creditScore.indexOf(".");
				if (tmpInt > 0) {
					creditScore = creditScore.substring(0, tmpInt);
				}
				cdseResult.setCreditScore(creditScore != null ? creditScore
						: "0");
			} catch (Exception ex2) {
				cdseResult.setCreditScore("0"); // 使用度评估值
			}
			cdseResult.setScorecardID(DA0040 != null ? DA0040 : "");
		}

		if (DA0090 == 2) {
			cdseResult.setInitLineReasonCode("DR101");
			cdseResult.setInitLineReasonDescription("由于授信执行结果，初始额度策略不执行。");
			return cdseResult;
		}
		// if (! ("A".equalsIgnoreCase(cdseResult.getPrincipalResultID()) ||
		// "D".equalsIgnoreCase(cdseResult.getPrincipalResultID()))) {
		// cdseResult.setInitLineReasonCode("DR103");
		// cdseResult.setInitLineReasonDescription("。");
		// return cdseResult;
		// }
		if (cdseResult.getPrincipalResultID().equals("A")) {

		}
		if (attributes.isIsDutyCardApp() == true) {
			tmpStr = getAttrValue("AT0050", actionResult.getAllAttributesA());
			if ("3".equals(tmpStr)) {
				cdseResult.setInitLineReasonCode("DR106");
				cdseResult
						.setInitLineReasonDescription("公务卡申请且清偿方式为公司清偿，初始额度策略不执行。");
				return cdseResult;
			}
		}

		strategy = strategyManager.getActiveStrategy(portfolioId,
				initialLinePolicyId);
		System.out.println(portfolioId + ":" + initialLinePolicyId);
		System.out.println(strategy);
		if (strategy == null) {
			cdseResult.setInitLineReasonCode("DR904");
			cdseResult.setInitLineReasonDescription("没有发现指定的策略文件，决策终止");
			// cdseResult.setStrategyID(cdseResult.getStrategyID() + " |");
			return cdseResult;
		}

		actionResult = strategyAnalyzerA.getStrategyResultA(
				attributes.getAttachVariableA(), strategy, "0");
		loadConstantVaribles(actionResult);
		// 设置额度策略编号版本
		cdseResult.setAmouStrategyID(actionResult.getStrategyNo() + "_"
				+ actionResult.getStrategyVer() + "_"
				+ actionResult.getActionId());

		// cdseResult.setStrategyID(cdseResult.getStrategyID() +
		// actionResult.getStrategyNo() + "_" + actionResult.getStrategyVer() +
		// "|");
		// Zhushi
		// appendProcessLog(log, actionResult, portfolioId); //添加过程日志
		// System.out.println("actionResult======="+actionResult);
		// 由参数获得额度
		String l_old = "0";
		String DA0050 = null;
		String DA0100Str = null;
		int DA0100 = 0;
		paramList = actionResult.getParaList();
		for (int i = 0; i < paramList.size(); ++i) {
			param = (StrategyParameter) paramList.get(i);
			if ("L_OLD".equalsIgnoreCase(param.getName())) {
				l_old = param.getValue();
			}

			if ("DA0050".equals(param.getName())) {
				DA0050 = param.getValue();
				if (DA0050 != null && !"".equals(DA0050.trim())) {
					bd = new BigDecimal(DA0050);
					DA0050 = bd.setScale(0, BigDecimal.ROUND_HALF_UP)
							.toString();
				}

			}
			if ("DA0100".equals(param.getName())) {
				DA0100Str = param.getValue();
				if (DA0100Str != null && !"".equals(DA0100Str.trim())) {
					bd = new BigDecimal(DA0100Str);
					DA0100 = bd.setScale(0, BigDecimal.ROUND_HALF_UP)
							.intValue();
				}
			}
		}
		// if (DA0100 == 2) {
		// cdseResult.setPrincipalResultID("B");
		// cdseResult.setPrincipalResultDescription("拒绝授信");
		// } else if (DA0100 == 1) {
		// cdseResult.setPrincipalResultID("D");
		// cdseResult.setPrincipalResultDescription("人工审查");
		// }

		try { // 建议初始额度
			int initialLineRMB = 0;
			if (DA0100 != 2) {

				bd = new BigDecimal(l_old);
				initialLineRMB = bd.setScale(0, BigDecimal.ROUND_HALF_UP)
						.intValue();
			}
			// 修改
			if ("3".equals(status) || "2".equals(status)) {

			} else {
				if ((initialLineRMB % 100) >= 50) {
					initialLineRMB = ((int) (initialLineRMB / 100)) * 100 + 100;
				} else {
					initialLineRMB = ((int) (initialLineRMB / 100)) * 100;
				}
			}

			cdseResult.setPrincipalInitialLineRMB(initialLineRMB);
		} catch (Exception ex) {
			cdseResult.setPrincipalInitialLineRMB(0);
		}

		// 根据参数获得升级或降级的产品编号
		// 经过降级
		if ("1".equals(DA0050)) {
			cdseResult.setHasDownSell(1); // 已经过降级
			this.lastProductCode = getProductCodeAfterDownsale(lastProductCode);
		}
		if ("2".equals(DA0050)) { // 经过升级
			cdseResult.setHasDownSell(2); // 已经过升级
			this.lastProductCode = getProductCodeAfterUpsale(lastProductCode);
		}
		System.out.println("最终产品额度>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
				+ lastProductCode);
		tmpStr = getAttrValue("AT0060", actionResult.getAllAttributesA());
		String tmpStr1 = getAttrValue("AT0070",
				actionResult.getAllAttributesA());
		Document doc = appForm;
		Element basic = doc.getDocumentElement();
		Node data = basic.getFirstChild();
		Element variable = (Element) data.getFirstChild();
		attrString = variable.getAttribute("ProductCd").trim();

		Float number = ProductCardInfoParameter.exchangeRateCardMap
				.get(attrString);
		int num = 0;
		if ("1".equalsIgnoreCase(tmpStr) && number == null) {
			if (this.DM0120 == 0.0f) {
				this.DM0120 = 8.0f;
			}
			if (this.DM0130 == 0.0f) {
				this.DM0130 = 12f;
			}

			tmpStr = getAttrValue("AT0065", actionResult.getAllAttributesA());

			int FRC1 = 0;
			if ("2".equalsIgnoreCase(tmpStr)) {
				this.isUsd = false;
				FRC1 = (int) (cdseResult.getPrincipalInitialLineRMB() * this.DM0130);
			} else {
				this.isUsd = true;
				FRC1 = (int) (cdseResult.getPrincipalInitialLineRMB() / this.DM0120);
			}

			cdseResult.setPrincipalInitialLineFRC(FRC1);
		} else {
			cdseResult.setPrincipalInitialLineFRC(0);
		}
		if (number != null) {
			num = (int) (cdseResult.getPrincipalInitialLineRMB() / number);
			cdseResult.setPrincipalInitialLineFRC(num);

		}
		// if(attrString.equals(English_1)||attrString.equals(English_2)){
		// if (this.DM0140 == 0.0f) {
		// this.DM0140 = 10f;
		// }
		// num = (int) (cdseResult.getPrincipalInitialLineRMB() /
		// this.DM0140);
		// cdseResult.setPrincipalInitialLineFRC(num);
		// }else if
		// (attrString.equals(America_1)||attrString.equals(America_2)){
		// if (this.DM0120 == 0.0f) {
		// this.DM0120 = 8.0f;
		// }
		// num = (int) (cdseResult.getPrincipalInitialLineRMB() /
		// this.DM0120);
		// cdseResult.setPrincipalInitialLineFRC(num);
		// }
		try {
			cdseResult.setInitLineReasonCode(actionResult.getReasonCode()); // 额度原因代码
		} catch (Exception ex1) {
			cdseResult.setInitLineReasonCode("DR111");
		}

		cdseResult.setInitLineReasonDescription(actionResult
				.getReasonDescription()); // 额度原因描述

		String ATOO41 = getAttrValue("AT0041", actionResult.getAllAttributesA());
		if ((ATOO41.equals("2") && !"1".equals(DA0050))
				|| (ATOO41.equals("1") && "2".equals(DA0050))) {
			cdseResult.setPrincipalCashAdvanceRMBRate(this.DM0250);
			cdseResult.setPrincipalCashAdvanceFRCRate(this.DM0260);
		} else {
			cdseResult.setPrincipalCashAdvanceRMBRate(this.DM0210);
			cdseResult.setPrincipalCashAdvanceFRCRate(this.DM0220);
		}
		// 公务卡设置取现比率
		if (attributes.isIsDutyCardApp() == true) {
			cdseResult.setPrincipalCashAdvanceRMBRate(this.DM0300);
			cdseResult.setPrincipalCashAdvanceFRCRate(this.DM0300);
		}

		if (cdseResult.getPrincipalInitialLineRMB() != -1) {
			cdseResult.setPrincipalCashAdvanceRMB((int) (cdseResult
					.getPrincipalInitialLineRMB() * cdseResult
					.getPrincipalCashAdvanceRMBRate()));
		}
		if (cdseResult.getPrincipalInitialLineFRC() != -1) {
			cdseResult.setPrincipalCashAdvanceFRC((int) (cdseResult
					.getPrincipalInitialLineFRC() * cdseResult
					.getPrincipalCashAdvanceFRCRate()));
		}
		// 根据是否零额度判断授信结果代码
		// String AT4210 = getAttrValue("AT4210",
		// actionResult.getAllAttributesA());
		// if("1".equals(AT4210)){
		// cdseResult.setPrincipalResultID("A");
		// cdseResult.setPrincipalResultDescription("批准授信");
		// }
		System.out.println("主卡策略结束");
		return cdseResult;

	}

	/**
	 * 附属卡控制流程执行过程
	 * 
	 * @param portfolioId
	 *            产品线代号
	 * @param underwrittingPolicyId
	 *            附属卡授信政策编号
	 * @param initialLinePolicyId
	 *            附属卡初始额度政策编号
	 * @param attributes
	 *            原始变量经过处理后形成的外部中间变量
	 * @param cdsResult
	 *            传入此前步骤处理后的输出结果
	 * @param log
	 *            日志文件
	 * @return cdesResult 经此步骤处理后的输出结果
	 */
	private CDSEResult getSupplResult(String portfolioId,
			String underwrittingPolicyId, String stgno,
			PersonalCreditCardAttributes attributes, CDSEResult cdseResult,
			Document appForm, Log log) {
		String tmpStr = "";
		// 修改部分 添加一个字段
		String StrategyID = "";
		String StrategyID2 = "";
		// ////
		int tmpInt = -1;
		int supplNum = attributes.getSupplNum();

		if (supplNum <= 0) {
			return cdseResult;
		}
		SupplResult supplResult = null;
		ArrayList supplResultList = new ArrayList();
		Strategy strategy = null;
		StrategyAnalyzerA strategyAnalyzerA = new StrategyAnalyzerA();
		ActionResult actionResult = null;

		strategy = strategyManager.getActiveStrategy(portfolioId,
				underwrittingPolicyId);

		System.out.println(portfolioId + ":" + underwrittingPolicyId);
		System.out.println(strategy);
		if (strategy == null) {
			for (int i = 0; i < supplNum; i++) {
				supplResult = new SupplResult();

				String supplID = (String) attributes.getSupplData().get(i);
				supplResult.setSupplID(supplID);
				supplResult.setSupplResultID("D");
				supplResult.setSupplResultDescription("人工审查");
				supplResult.setSupplUwReasonCode("DR904");
				supplResult.setSupplUwReasonDescription("没有发现指定的策略文件，决策终止。");
				supplResultList.add(supplResult);
			}
			cdseResult.setSupplResultList(supplResultList);
			return cdseResult;
		}

		for (int i = 0; i < supplNum; i++) {
			System.out.println(i);
			supplResult = new SupplResult();
			try {
				String supplID = (String) attributes.getSupplData().get(i);
				supplResult.setSupplID(supplID);

				actionResult = strategyAnalyzerA.getStrategyResultA(
						attributes.getAttachVariableA(), strategy,
						Integer.toString((i + 1)));
				// if(){
				//
				// }
				loadConstantVaribles(actionResult);
				// Zhushi
				// appendProcessLog(log, actionResult, portfolioId); //添加过程日志
				// tmpStr = getAttrValue("CG0010",
				// actionResult.getAllAttributes());
				if (actionResult.getActionId().equals("-888")) {
					actionResult.setActionId("C");
					actionResult.setActionNa("入组拒绝");
					actionResult.setReasonDescription("因入组条件不满足而拒绝");
				}

				StringBuffer reasonDescription = new StringBuffer(
						actionResult.getReasonDescription());
				if ("C".equalsIgnoreCase(actionResult.getActionId())) {
					reasonDescription.append("；入组条件为：");
					reasonDescription
							.append(actionResult.getAttributesString());
				}
				actionResult.setReasonDescription(reasonDescription.toString()); // 授信原因描述
				// 修改部分 添加附属卡的授信策略编号和版本
				if (StrategyID.equalsIgnoreCase("")) {
					StrategyID = actionResult.getStrategyNo() + "_"
							+ actionResult.getStrategyVer() + "_"
							+ actionResult.getActionId();
				}

				// 如果主卡决策结果为人工审查，附属卡决策结果均为人工审查
				if ("D".equals(cdseResult.getPrincipalResultID())) {
					supplResult.setSupplResultID("D");
					supplResult.setSupplResultDescription("人工审查");
				} else {
					supplResult.setSupplResultID(actionResult.getActionId());
					supplResult.setSupplResultDescription(actionResult
							.getActionNa());
				}

				String tempStr = actionResult.getReasonCode();
				if (!(tempStr.equals(""))) {
					supplResult.setSupplUwReasonCode(tempStr);
				} else {
					supplResult.setSupplUwReasonCode("-1");
				}
				supplResult.setSupplUwReasonDescription(actionResult
						.getReasonDescription());

				if (!("A".equalsIgnoreCase(supplResult.getSupplResultID()) || "D"
						.equalsIgnoreCase(supplResult.getSupplResultID()))) {
					supplResultList.add(supplResult);
					continue;
				}

				double rmb1 = 0;
				double FRC1 = 0;
				double rmb3 = 0;
				// /////////////////// 取附属卡额度转换率
				// ///////////////////////////////////////////////////////////////
				double supplLtdRatio = 1.0f;
				tempStr = getAttrValue("AT1430",
						actionResult.getAllAttributesA());
				if (!(tempStr.equals(""))) {
					supplLtdRatio = Double.parseDouble(tempStr) / 100;
				}
				// ///////////////////////////////////////////////////////////////////////////////////////////////////
				if (!(attributes.isSupplApplOnly())) {
					rmb1 = cdseResult.getPrincipalInitialLineRMB()
							* supplLtdRatio;
					rmb3 = cdseResult.getPrincipalInitialLineFRC()
							* supplLtdRatio;
					System.out.println("rmb3: " + rmb3);
				} else {
					rmb1 = Double.parseDouble(getAttrValue("AT3020",
							actionResult.getAllAttributesA())) * supplLtdRatio;
				}

				tmpStr = getAttrValue("AT0060",
						actionResult.getAllAttributesA());
				Float number = ProductCardInfoParameter.exchangeRateCardMap
						.get(attrString);
				if ("1".equalsIgnoreCase(tmpStr) && number == null) {
					if (this.DM0120 == 0.0f) {
						this.DM0120 = 8.0f;
					}
					if (this.DM0130 == 0.0f) {
						this.DM0130 = 12f;
					}
					tmpStr = getAttrValue("AT0065",
							actionResult.getAllAttributesA());
					if ("2".equalsIgnoreCase(tmpStr)) {
						this.isUsd = false;
						FRC1 = rmb1 * this.DM0130;
					} else {
						this.isUsd = true;
						FRC1 = rmb1 / this.DM0120;
					}
				} else {
					FRC1 = 0;
				}
				Document doc = appForm;
				Element basic = doc.getDocumentElement();
				Node data = basic.getFirstChild();
				Element variable = (Element) data.getFirstChild();
				attrString = variable.getAttribute("ProductCd").trim();

				if (number != null) {
					FRC1 = rmb3;
				}

				int rmb2 = (int) (rmb1);
				int FRC2 = (int) (FRC1);

				supplResult.setSupplInitialLineRMB(rmb2);
				supplResult.setSupplInitialLineFRC(FRC2);

				String ATOO41 = getAttrValue("AT0041",
						actionResult.getAllAttributesA());
				if ((ATOO41.equals("2") && cdseResult.getHasDownSell() != 1)
						|| (ATOO41.equals("1") && cdseResult.getHasDownSell() == 2)) {
					cdseResult.setSupplCashAdvanceRMBRate(this.DM0270);
					cdseResult.setSupplCashAdvanceFRCRate(this.DM0280);
				} else {
					cdseResult.setSupplCashAdvanceRMBRate(this.DM0230);
					cdseResult.setSupplCashAdvanceFRCRate(this.DM0240);
				}

				if (supplResult.getSupplInitialLineRMB() != -1) {
					supplResult.setSupplCashAdvanceRMB((int) (supplResult
							.getSupplInitialLineRMB() * cdseResult
							.getSupplCashAdvanceRMBRate()));
				}
				if (supplResult.getSupplInitialLineFRC() != -1) {
					supplResult.setSupplCashAdvanceFRC((int) (supplResult
							.getSupplInitialLineFRC() * cdseResult
							.getSupplCashAdvanceFRCRate()));
				}
			} catch (Exception e) {
				supplResult.setSupplResultID("D");
				supplResult.setSupplResultDescription("人工审查");
				supplResult.setSupplUwReasonCode("DR905");
				supplResult.setSupplUwReasonDescription("附属卡决策过程中系统异常，决策终止。");
			}
			if (supplResult.getSupplInitialLineRMB() < 0) {
				supplResult.setSupplInitialLineRMB(0);
			}
			System.out.println("SupplInitialLineRMB:"
					+ supplResult.getSupplInitialLineRMB());
			if (supplResult.getSupplInitialLineFRC() < 0) {
				supplResult.setSupplInitialLineFRC(0);
			}
			if (supplResult.getSupplCashAdvanceRMB() < 0) {
				supplResult.setSupplCashAdvanceRMB(0);
			}
			System.out.println("SupplCashAdvanceRMB:"
					+ supplResult.getSupplCashAdvanceRMB());
			if (supplResult.getSupplCashAdvanceFRC() < 0) {
				supplResult.setSupplCashAdvanceFRC(0);
			}

			String str1 = supplResult.getSupplResultID();
			String str2 = cdseResult.getPrincipalResultID();
			System.out.println(str1 + " " + str2);
			// 主附统一(同批、同拒、同人工)
			// ************************2011-05-05 wanghy beign****************/
			// 主卡拒绝、副卡也拒
			if (cdseResult.getPrincipalResultID().equals("C")) {
				supplResult.setSupplResultID("C");
				supplResult.setSupplResultDescription("拒绝授信");
				supplResult.setSupplUwReasonCode("SS003");
				supplResult.setSupplUwReasonDescription("主附同申，附属卡随主卡转拒绝。");
			} else if (supplResult.getSupplResultID().equals("D")
					&& cdseResult.getPrincipalResultID().equals("A")) {
				// 副卡人工、主卡也得人工
				cdseResult.setPrincipalResultID("D");
				cdseResult.setPrincipalResultDescription("人工审查");
				cdseResult.setUnderwritingReasonCode("SS002");
				cdseResult.setUnderwritingReasonDescription("主附同申，附属卡转人工。");
			} else if ("D".equals(cdseResult.getPrincipalResultID())) {
				// 主卡人工、副卡也得人工
				supplResult.setSupplResultID("D");
				supplResult.setSupplResultDescription("人工审查");
				supplResult.setSupplUwReasonCode("AA022");
				supplResult.setSupplUwReasonDescription("主附同申，附属卡随主卡转人工审查。");
			}
			// ************************2011-05-05 wanghy end****************/

			supplResultList.add(supplResult);
		}
		// 修改部分 判断是否是纯附属卡申请
		// System.out.println("modify success!");
		if (attributes.isIsSupplApplOnly()) {
			cdseResult.setAcquStrategyID(StrategyID);
		} else {
			cdseResult.setAcquStrategyID(stgno + " " + StrategyID);
		}

		// ////////////
		cdseResult.setSupplResultList(supplResultList);
		return cdseResult;

	}

	/**
	 * 获取变量值
	 * 
	 * @param varName
	 *            变量名称
	 * @param allVariables
	 *            全部变量（外部和内部）的集合
	 * @return String 返回的变量值之字符串表示
	 */
	private String getAttrValue(String varName, ArrayList allVariablesA) {
		VariableBean var = null;
		String result = "-999";

		if (varName != null && allVariablesA != null) {
			for (int i = 0; i < allVariablesA.size(); ++i) {
				var = (VariableBean) allVariablesA.get(i);
				if (varName.equals(var.getFldName())) {
					result = var.getValue();
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 添加全部变量（外部和内部）日志
	 * 
	 * @param log
	 *            日志对象
	 * @param allVariables
	 *            全部变量（外部和内部）的集合
	 */

	private void appendVaribleLog(Log log, ArrayList allVariables) {

		VariableBean variable = null;
		String fldName = "";
		String value = "";

		if (allVariables != null) {
			for (int i = 0; i < allVariables.size(); ++i) {
				variable = (VariableBean) allVariables.get(i);
				if ("IN".equals(variable.getFldSrc())) {
					fldName = variable.getFldName();
					value = variable.getValue();
					log.appendParameter(fldName, value);
				}
			}
		}
	}

	/**
	 * 添加处理过程日志
	 * 
	 * @param log
	 *            日志对象
	 * @param actionResult
	 *            决策结果对象
	 * @param portfolioId
	 *            产品线代号
	 */
	private void appendProcessLog(Log log, ActionResult actionResult,
			String portfolioId) {
		int i = getStepId();
		String s = String.valueOf(i);
		log.appendParameter("portfolioId_" + s, portfolioId);
		log.appendParameter("policyId_" + s, actionResult.getPoliceNo());
		// log.appendParameter("policyDescriptionId_" + s,
		// actionResult.getPoliceNa());
		log.appendParameter("strategyId_" + s, actionResult.getStrategyNo());
		// log.appendParameter("strategyDescription_" + s,
		// actionResult.getStrategyNa());
		log.appendParameter("strategyVersion_" + s,
				actionResult.getStrategyVer());
		log.appendParameter("resultId_" + s, actionResult.getActionId());
		// log.appendParameter("resultDescription_" + s,
		// actionResult.getActionNa());
		log.appendParameter("initialLine_" + s, actionResult.getInitialLine());
		log.appendParameter("reasonCode_" + s, actionResult.getReasonCode());
		// log.appendParameter("reasonDescription_" + s,
		// actionResult.getReasonDescription());
		log.appendParameter("path_" + s, actionResult.getAttributesString());

	};

	/**
	 * 添加最终结果日志
	 * 
	 * @param log
	 *            日志对象
	 * @param cdseResult
	 *            最终结果对象
	 */
	private void appendResultLog(Log log, CDSEResult cdseResult) {
		log.appendParameter("applicantRandomDigitID",
				String.valueOf(cdseResult.getApplicantRandomDigitID()));
		log.appendParameter("applicationId", cdseResult.getApplicationId());
		log.appendParameter("exchangeRate",
				String.valueOf(cdseResult.getExchangeRate()));
		log.appendParameter("hasDownSell",
				String.valueOf(cdseResult.getHasDownSell()));
		log.appendParameter("initLineReasonCode",
				String.valueOf(cdseResult.getInitLineReasonCode()));
		log.appendParameter("initLineReasonDescription",
				cdseResult.getInitLineReasonDescription());
		log.appendParameter("principalCashAdvanceFRC",
				String.valueOf(cdseResult.getPrincipalCashAdvanceFRC()));
		log.appendParameter("principalCashAdvanceRMB",
				String.valueOf(cdseResult.getPrincipalCashAdvanceRMB()));
		log.appendParameter("principalInitialLineFRC",
				String.valueOf(cdseResult.getPrincipalInitialLineFRC()));
		log.appendParameter("principalInitialLineRMB",
				String.valueOf(cdseResult.getPrincipalInitialLineRMB()));
		log.appendParameter("principalResultDescription",
				cdseResult.getPrincipalResultDescription());
		log.appendParameter("principalResultID",
				cdseResult.getPrincipalResultID());
		log.appendParameter("productCode", cdseResult.getProductCode());

		log.appendParameter("riskScore",
				String.valueOf(cdseResult.getRiskScore()));
		log.appendParameter("strategiesLogID", cdseResult.getStrategiesLogID());
		log.appendParameter("underwritingReasonCode",
				String.valueOf(cdseResult.getUnderwritingReasonCode()));
		log.appendParameter("underwritingReasonDescription",
				cdseResult.getUnderwritingReasonDescription());
		log.appendParameter("usageScore",
				String.valueOf(cdseResult.getUsageScore()));
		log.appendParameter("creditScore",
				String.valueOf(cdseResult.getCreditScore()));
		log.appendParameter("riskGrade",
				String.valueOf(cdseResult.getRiskGrade()));
		log.appendParameter("usageGrade",
				String.valueOf(cdseResult.getUsageGrade()));
		log.appendParameter("creditScore",
				String.valueOf(cdseResult.getCreditScore()));
		log.appendParameter("scorecardID", cdseResult.getScorecardID());
		log.appendParameter("acquisitionStrategyID",
				cdseResult.getAcquStrategyID());
		log.appendParameter("amountlandStrategyID",
				cdseResult.getAmouStrategyID());

		ArrayList supplList = cdseResult.getSupplResultList();
		if (supplList != null) {
			int i = 0;
			for (java.util.Iterator it = supplList.iterator(); it.hasNext();) {
				SupplResult supplResult = (SupplResult) it.next();
				i = i + 1;
				log.appendParameter("supplResultID" + i,
						supplResult.getSupplResultID());
				log.appendParameter("supplResultDescription" + i,
						supplResult.getSupplResultDescription());
				log.appendParameter("supplUwReasonCode" + i,
						String.valueOf(supplResult.getSupplUwReasonCode()));
				log.appendParameter("supplInitialLineRMB" + i,
						String.valueOf(supplResult.getSupplInitialLineRMB()));
				log.appendParameter("supplInitialLineFRC" + i,
						String.valueOf(supplResult.getSupplInitialLineFRC()));
				log.appendParameter("supplCashAdvanceRMB" + i,
						String.valueOf(supplResult.getSupplCashAdvanceRMB()));
				log.appendParameter("supplCashAdvanceFRC" + i,
						String.valueOf(supplResult.getSupplCashAdvanceFRC()));
			}
		}

	}

	/**
	 * 系统常数载入（根据决策结果中附带的内部变量值更新汇率、取现比率等系统常数）
	 * 
	 * @param actionResult
	 *            决策结果对象
	 */
	private void loadConstantVaribles(ActionResult actionResult) {
		String tmpStr = new String();
		float tmpFloat = 0;
		try {
			tmpStr = getAttrValue("DM0210", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0210 = tmpFloat;
		} catch (Exception ex) {
			DM0210 = 0.3f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0220", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0220 = tmpFloat;
		} catch (Exception ex) {
			DM0220 = 0.3f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0230", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0230 = tmpFloat;
		} catch (Exception ex) {
			DM0230 = 0.3f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0240", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0240 = tmpFloat;
		} catch (Exception ex) {
			DM0240 = 0.3f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0250", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0250 = tmpFloat;
		} catch (Exception ex) {
			DM0250 = 0.4f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0260", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0260 = tmpFloat;
		} catch (Exception ex) {
			DM0260 = 0.4f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0270", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0270 = tmpFloat;
		} catch (Exception ex) {
			DM0270 = 0.4f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0280", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0280 = tmpFloat;
		} catch (Exception ex) {
			DM0280 = 0.4f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0120", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0120 = tmpFloat;
		} catch (Exception ex) {
			DM0120 = 8.0f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0130", actionResult.getAllAttributesA());
			tmpFloat = Float.parseFloat(tmpStr);
			DM0130 = tmpFloat;
		} catch (Exception ex) {
			DM0130 = 12f;
			ex.printStackTrace();
		}
		try {
			tmpStr = getAttrValue("DM0300", actionResult.getAllAttributesA());
			if (!tmpStr.equals("")) {
				tmpFloat = Float.parseFloat(tmpStr);
			}
			DM0300 = tmpFloat;
		} catch (Exception ex) {
			DM0300 = 0.4f;
			ex.printStackTrace();
		}
	}

	/**
	 * 获取降级后的产品号
	 * 
	 * @param productCode
	 *            降级前的产品号
	 * @return String 降级后的产品号
	 */
	private String getProductCodeAfterDownsale(String productCode) {
		String[] silverCardArray = { "1001", "1004", "1009", "1012", "2001",
				"2004", "2009", "2012", "4C02", "3002", "3004", "3DZ2", "1003",
				"1006", "4005", "4006", "1CT2", "4AW3", "1R06" };
		String[] goldenCardArray = { "1002", "1005", "1010", "1013", "2002",
				"2005", "2010", "2013", "4C01", "3001", "3003", "3DZ1", "1011",
				"1015", "4007", "4008", "1CT1", "4AW2", "1R05" };

		for (int i = 0; i < goldenCardArray.length; i++) {
			if (productCode.equalsIgnoreCase(goldenCardArray[i])) {
				productCode = silverCardArray[i];
				break;
			}
		}

		return productCode;
	}

	/**
	 * 获取升级后的产品号
	 * 
	 * @param productCode
	 *            升级前的产品号
	 * @return String 升级后的产品号
	 */
	private String getProductCodeAfterUpsale(String productCode) {
		String[] silverCardArray = { "1001", "1004", "1009", "1012", "2001",
				"2004", "2009", "2012", "4C02", "3002", "3004", "3DZ2", "1003",
				"1006", "4005", "4006", "1CT2", "4AW3" };
		String[] goldenCardArray = { "1002", "1005", "1010", "1013", "2002",
				"2005", "2010", "2013", "4C01", "3001", "3003", "3DZ1", "1011",
				"1015", "4007", "4008", "1CT1", "4AW2" };

		for (int i = 0; i < silverCardArray.length; i++) {
			if (productCode.equalsIgnoreCase(silverCardArray[i])) {
				productCode = goldenCardArray[i];
				break;
			}
		}

		return productCode;
	}

	/**
	 * 获取字符串指定字符前面的子字符串
	 * 
	 * @param srcString
	 *            源字符串
	 * @param divString
	 *            指定分界字符
	 * @return String 指定分界字符前面的子字符串
	 */
	public String getNo(String srcString, String divString) {
		String result = "";
		int pos = 0;
		if (srcString != null) {
			pos = srcString.indexOf(divString);
			if (pos != -1) {
				result = srcString.substring(0, pos);
			}
		}
		return result;
	}

	/**
	 * 获取字符串指定字符后面的子字符串
	 * 
	 * @param srcString
	 *            源字符串
	 * @param divString
	 *            指定分界字符
	 * @return String 指定分界字符后面的子字符串
	 */
	public String getDesc(String srcString, String divString) {
		String result = "";
		int pos = 0;
		if (srcString != null) {
			pos = srcString.indexOf(divString);
			if (pos != -1) {
				result = srcString.substring(pos + 1);
			}
		}
		return result;
	}

	public int getStepId() {
		setStepId(stepId + 1);
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}

	public boolean isHasVarLog() {
		return hasVarLog;
	}

	public void setHasVarLog(boolean hasVarLog) {
		this.hasVarLog = hasVarLog;
	}

	private void jbInit() throws Exception {
	}

}
