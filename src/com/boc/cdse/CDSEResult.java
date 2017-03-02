package com.boc.cdse;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.boc.cdse.CDSEUtil;
import com.boc.cdse.PersonalCreditCardHandler;
import com.boc.cdse.SupplResult;

/**
 * <p>CDSEResult类保存CDSE对信用卡申请数据授信及额度决策结果</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public final class CDSEResult implements Serializable {

	/** 申请人的申请号 */
	private String applicationId = "";

	/** 申请人随机数ID  */
	private int applicantRandomDigitID = 0;

	/** 风险度评估值  */
	private int riskScore = 0;
	/** 使用评分  */
	private int usageGrade = 0;

	/** 风险评分  */
	private int riskGrade = 0;
	/** 汇率*/
	private float exchangeRate = 0;

	/** 使用度评估值 */
	private int usageScore = 0;

	/** 评分卡版本号 */
	private String scorecardID = "";

	/** 是否经过降级 */
	/** 更新为升降级标志 0-未经过降级 1-已经过降级 2-已经过升级 */
	private int hasDownSell;

	/** 最终产品的产品号 */
	private String productCode = "";

	/** 主卡授信结果代码 */
	private String principalResultID = "";

	/** 主卡授信结果描述 */
	private String principalResultDescription = "";

	/** 主卡授信原因代码 */
	private String underwritingReasonCode = "";

	/** 主卡授信原因描述 */
	private String underwritingReasonDescription = "";

	/** 主卡人民币额度 */
	private int principalInitialLineRMB = 0;

	/** 主卡外币额度 */
	private int principalInitialLineFRC = 0;

	/** 主卡人民币取现额度 */
	private int principalCashAdvanceRMB = 0;

	/** 主卡外币取现额度 */
	private int principalCashAdvanceFRC = 0;

	/** 主卡额度原因代码 */
	private String initLineReasonCode = "";

	/** 主卡额度原因描述 */
	private String initLineReasonDescription = "";

	/** 附属卡结果LIST*/
	private ArrayList supplResultList = null;

	/** 决策过程记录ID */
	private String strategiesLogID = "";

	////////////以下这一窜属性暂时没有用处，权且保留////////////////////////////////
	/** 附属卡授信结果代码 */
	private String supplResultID = "";
	/** 附属卡授信结果描述 */
	private String supplResultDescription = "";
	/** 附属卡人民币初始额度 */
	private int supplInitialLineRMB = 0;
	/** 附属卡外币初始额度 */
	private int supplInitialLineFRC = 0;
	/** 附属卡取现人民币额度 */
	private int supplCashAdvanceRMB = 0;
	/** 附属卡取现外币额度 */
	private int supplCashAdvanceFRC = 0;
	/** 主卡取现比率（人民币） */
	private float principalCashAdvanceRMBRate = 0;
	/** 主卡取现比率（美元） */
	private float principalCashAdvanceFRCRate = 0;
	/** 附属卡取现比率（人民币） */
	private float supplCashAdvanceRMBRate = 0;
	/** 附属卡取比率（美元） */
	private float supplCashAdvanceFRCRate = 0;
	/** 备用的字段1 */
	private String reservedField1 = "";
	/** 备用的字段2 */
	private String reservedField2 = "";
	/** 备用的字段3 */
	private String reservedField3 = "";
	/** 备用的字段4 */
	private String reservedField4 = "";
	/** 备用的字段5 */
	private String reservedField5 = "";
	private String creditScore = "";

	/** 策略编号 */
	private String strategyID = "";
	/** 授信策略编号 */
	private String acquStrategyID = "";
	/** 额度策略编号 */
	private String amouStrategyID = "";
	/**判断是否挑战，1冠军2挑战,添加日期：2015年10月25日*/
	/**C1506挑战全辖分行，C1507投产时，原挑战转为冠军策略*/
	private String isChallenge = "";
	private String decisionPoint = ""; 
	//可证明年收入
	private String aprAnnIncome = "" ;
	//////////////////////////////////////////////////////////////////////////
    
	/**
	 * 默认构造函数
	 *
	 */
	public CDSEResult() {
		try {
			jbInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 构造函数.根据决策结果dom树，初始化决策结果实例，以便显示
	 *
	 */

	public CDSEResult(String cdseResult) {
		if (cdseResult != null) {
			try {
				this.applicationId = getValue(cdseResult, "applicationId");
				this.applicantRandomDigitID = Integer.parseInt(getValue(
					cdseResult, "applicantRandomDigitID"));
				String tempStr = getValue(cdseResult, "riskScore");
				if (! ("".equals(tempStr))) {
					this.riskScore = Integer.parseInt(tempStr);
				} else {
					this.riskScore = 0;
				}
				tempStr = getValue(cdseResult, "usageScore");
				if (! ("".equals(tempStr))) {
					this.usageScore = Integer.parseInt(tempStr);
				} else {
					this.usageScore = 0;
				}
				this.creditScore =
					getValue(cdseResult, "creditScore");
			    
				this.aprAnnIncome = getValue(cdseResult,"aprAnnIncome");
				
				this.scorecardID =
					getValue(cdseResult, "scorecardID");
				this.hasDownSell = Integer.parseInt(getValue(cdseResult,
					"hasDownSell"));
				this.productCode = getValue(cdseResult, "productCode");
				this.principalResultID = getValue(cdseResult,
												  "principalResultID");
				
				this.principalResultID = getValue(cdseResult,
												  "principalResultID");
				this.principalResultDescription = getValue(cdseResult,
					"principalResultDescription");
				tempStr = getValue(cdseResult, "principalInitialLineRMB");
				if (!tempStr.trim().equals("")) {
					this.principalInitialLineRMB = Integer.parseInt(tempStr);
				} else {
					this.principalInitialLineRMB = 0;
				}
				tempStr = getValue(cdseResult, "principalInitialLineFRC");
				if (!tempStr.trim().equals("")) {
					this.principalInitialLineFRC = Integer.parseInt(tempStr);
				} else {
					this.principalInitialLineFRC = 0;
				}
				this.supplResultID = getValue(cdseResult, "supplResultID");
				this.supplResultDescription = getValue(cdseResult,
					"supplResultDescription");
				tempStr = getValue(cdseResult, "supplInitialLineRMB");
				if (!tempStr.trim().equals("")) {
					this.supplInitialLineRMB = Integer.parseInt(tempStr);
				} else {
					this.supplInitialLineRMB = 0;
				}
				tempStr = getValue(cdseResult, "supplInitialLineFRC");
				if (!tempStr.trim().equals("")) {
					this.supplInitialLineFRC = Integer.parseInt(tempStr);
				} else {
					this.supplInitialLineFRC = 0;
				}
				this.underwritingReasonCode = getValue(
					cdseResult, "underwritingReasonCode");
				this.underwritingReasonDescription = getValue(cdseResult,
					"underwritingReasonDescription");

				this.initLineReasonCode = getValue(cdseResult,
					"initLineReasonCode");
				this.initLineReasonDescription = getValue(cdseResult,
					"initLineReasonDescription");

				this.strategiesLogID = getValue(cdseResult, "strategiesLogID");
				tempStr = getValue(cdseResult, "exchangeRate");
				if (!tempStr.trim().equals("")) {
					this.exchangeRate = Float.parseFloat(tempStr);
				} else {
					this.exchangeRate = 0;
				}
				
				tempStr = getValue(cdseResult, "principalCashAdvanceRMB");
				if (!tempStr.trim().equals("")) {
					this.principalCashAdvanceRMB = Integer.parseInt(tempStr);
				} else {
					this.principalCashAdvanceRMB = 0;
				}

				tempStr = getValue(cdseResult, "principalCashAdvanceFRC");
				if (!tempStr.trim().equals("")) {
					this.principalCashAdvanceFRC = Integer.parseInt(tempStr);
				} else {
					this.principalCashAdvanceFRC = 0;
				}
				tempStr = getValue(cdseResult, "supplCashAdvanceRMB");
				if (!tempStr.trim().equals("")) {
					this.supplCashAdvanceRMB = Integer.parseInt(tempStr);
				} else {
					this.supplCashAdvanceRMB = 0;
				}
				tempStr = getValue(cdseResult, "supplCashAdvanceFRC");
				if (!tempStr.trim().equals("")) {
					this.supplCashAdvanceFRC = Integer.parseInt(tempStr);
				} else {
					this.supplCashAdvanceFRC = 0;
				}
//取现比率

				tempStr = getValue(cdseResult, "principalCashAdvanceRMBRate");
				if (!tempStr.trim().equals("")) {
					this.principalCashAdvanceRMBRate = Float.parseFloat(tempStr);
				} else {
					this.principalCashAdvanceRMBRate = 0;
				}

				tempStr = getValue(cdseResult, "principalCashAdvanceFRCRate");
				if (!tempStr.trim().equals("")) {
					this.principalCashAdvanceFRCRate = Float.parseFloat(tempStr);
				} else {
					this.principalCashAdvanceFRCRate = 0;
				}
				tempStr = getValue(cdseResult, "supplCashAdvanceRMBRate");
				if (!tempStr.trim().equals("")) {
					this.supplCashAdvanceRMBRate = Float.parseFloat(tempStr);
				} else {
					this.supplCashAdvanceRMBRate = 0;
				}
				tempStr = getValue(cdseResult, "supplCashAdvanceFRCRate");
				if (!tempStr.trim().equals("")) {
					this.supplCashAdvanceFRCRate = Float.parseFloat(tempStr);
				} else {
					this.supplCashAdvanceFRCRate = 0;
				}
//可证明年收入额
				this.aprAnnIncome = getValue(cdseResult, "aprAnnIncome");
				this.reservedField1 = "";
				this.reservedField2 = "";
				this.reservedField3 = "";
				this.reservedField4 = "";
				this.reservedField5 = "";

				this.strategyID = getValue(cdseResult, "strategyID");
				this.acquStrategyID = getValue(cdseResult, "acquisitionStrategyID");
				System.out.println("acquStrategyID:"+acquStrategyID);
				this.amouStrategyID = getValue(cdseResult, "amountlandStrategyID");
				this.isChallenge = getValue(cdseResult, "isChallenge");
				this.decisionPoint = getValue(cdseResult, "decisionPoint");
			} catch (Exception e) {
				System.out.println("Initialize class error!");
				e.printStackTrace();
			}

		}
	}

	public CDSEResult(Document cdseResult) {
		if (cdseResult != null) {
			try {

				Element xml = (Element) cdseResult.getElementsByTagName("xml").
					item(0);
				Element data = (Element) xml.getElementsByTagName("data").item(
					0);

				///////// 取到主卡的数据行///////////////////////////////////////////
				Element row = (Element) xml.getElementsByTagName("result").item(
					0);
				///////////////////////////////////////////////////////////////////

				this.applicationId = row.getAttribute("applicationId");

				this.applicantRandomDigitID = Integer.parseInt(row.getAttribute(
					"applicantRandomDigitID"));

				String tempStr = row.getAttribute("riskScore").trim();
				if (! ("".equals(tempStr))) {
					this.riskScore = Integer.parseInt(tempStr);
				} else {
					this.riskScore = 0;
				}
				tempStr = row.getAttribute("usageScore").trim();
				if (! ("".equals(tempStr))) {
					this.usageScore = Integer.parseInt(tempStr);
				} else {
					this.usageScore = 0;
				}

				tempStr = row.getAttribute("exchangeRate").trim();
				if (! (tempStr.equals(""))) {
					this.exchangeRate = Float.parseFloat(tempStr);
				} else {
					this.exchangeRate = 0;
				}

				this.creditScore = row.getAttribute("creditScore");
				
				this.scorecardID = row.getAttribute("scorecardID");
				this.hasDownSell = Integer.parseInt(row.getAttribute(
					"hasDownSell"));

				this.productCode = row.getAttribute("productCode");

				this.principalResultID = row.getAttribute("principalResultID");

				this.principalResultDescription = row.getAttribute(
					"principalResultDescription");

				this.underwritingReasonCode = row.getAttribute(
					"underwritingReasonCode");

				this.underwritingReasonDescription = row.getAttribute(
					"underwritingReasonDescription");

				tempStr = row.getAttribute("principalInitialLineRMB").trim();
				if (! (tempStr.equals(""))) {
					this.principalInitialLineRMB = Integer.parseInt(tempStr);
				} else {
					this.principalInitialLineRMB = 0;
				}

				tempStr = row.getAttribute("principalInitialLineFRC").trim();
				if (! (tempStr.equals(""))) {
					this.principalInitialLineFRC = Integer.parseInt(tempStr);
				} else {
					this.principalInitialLineFRC = 0;
				}

				tempStr = row.getAttribute("principalCashAdvanceRMB").trim();
				if (! (tempStr.equals(""))) {
					this.principalCashAdvanceRMB = Integer.parseInt(tempStr);
				} else {
					this.principalCashAdvanceRMB = 0;
				}

				tempStr = row.getAttribute("principalCashAdvanceFRC").trim();
				if (! (tempStr.equals(""))) {
					this.principalCashAdvanceFRC = Integer.parseInt(tempStr);
				} else {
					this.principalCashAdvanceFRC = 0;
				}

				this.initLineReasonCode = row.getAttribute(
					"initLineReasonCode");

				this.initLineReasonDescription = row.getAttribute(
					"initLineReasonDescription");

				this.strategiesLogID = row.getAttribute("strategiesLogID");
				this.scorecardID = row.getAttribute("scorecardID");
				this.strategyID = row.getAttribute("strategyID");
				this.acquStrategyID = row.getAttribute("acquisitionStrategyID");
				this.amouStrategyID = row.getAttribute("amountlandStrategyID");
				this.isChallenge = row.getAttribute("isChallenge");
				this.decisionPoint = row.getAttribute("decisionPoint");
				this.aprAnnIncome = row.getAttribute("aprAnnIncome");

				////////////////////  下面处理附属卡 ////////////////////////////////////////////

				Node rowNode = (Node) row;
				Node supplNode = null;
				NodeList supplNodeList = rowNode.getChildNodes();
				SupplResult supplResult = null;
				ArrayList supplList = new ArrayList();

				for (int i = 0; i < supplNodeList.getLength(); i++) {
					supplNode = (Element) supplNodeList.item(i);

					supplResult = new SupplResult();

					tempStr = ( (Element) supplNode).getAttribute("supplID");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplID(tempStr);
					} else {
						supplResult.setSupplID("-1");
					}

					supplResult.setSupplResultID( ( (Element) supplNode).
												 getAttribute("supplResultID"));

					supplResult.setSupplResultDescription( ( (Element)
						supplNode).getAttribute("supplResultDescription"));

					tempStr = ( (Element) supplNode).getAttribute(
						"supplUwReasonCode");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplUwReasonCode(tempStr);
					} else {
						supplResult.setSupplUwReasonCode("-1");
					}

					supplResult.setSupplUwReasonDescription( ( (Element)
						supplNode).getAttribute("supplUwReasonDescription"));

					tempStr = ( (Element) supplNode).getAttribute(
						"supplInitialLineRMB");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplInitialLineRMB(Integer.parseInt(
							tempStr));
					} else {
						supplResult.setSupplInitialLineRMB(0);
					}

					tempStr = ( (Element) supplNode).getAttribute(
						"supplInitialLineFRC");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplInitialLineFRC(Integer.parseInt(
							tempStr));
					} else {
						supplResult.setSupplInitialLineFRC(0);
					}

					tempStr = ( (Element) supplNode).getAttribute(
						"supplCashAdvanceRMB");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplCashAdvanceRMB(Integer.parseInt(
							tempStr));
					} else {
						supplResult.setSupplCashAdvanceRMB(0);
					}

					tempStr = ( (Element) supplNode).getAttribute(
						"supplCashAdvanceFRC");
					if (! (tempStr.equals(""))) {
						supplResult.setSupplCashAdvanceFRC(Integer.parseInt(
							tempStr));
					} else {
						supplResult.setSupplCashAdvanceFRC(0);
					}

					supplList.add(supplResult);

					supplNode = supplNode.getNextSibling();
				}
				this.setSupplResultList(supplList);

				///////////////////////////////////////////////////////////////////////////////

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Initialize class error!");
			}

		}
	}

	/**
	 * 获得申请人的申请号
	 * @return 申请人的申请号
	 */
	public String getApplicationId() {
		return (this.applicationId);
	}

	/**
	 * 设置申请人的申请号
	 *
	 * @param applicationId 申请人的申请号
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * 获得申请人随机数ID
	 * @return 申请人随机数ID
	 */
	public int getApplicantRandomDigitID() {
		return (this.applicantRandomDigitID);
	}

	/**
	 * 设置申请人随机数ID
	 *
	 * @param applicantRandomDigitID 申请人随机数ID
	 */
	public void setApplicantRandomDigitID(int applicantRandomDigitID) {
		this.applicantRandomDigitID = applicantRandomDigitID;
	}

	/**
	 * 获得风险度评估值
	 * @return 风险度评估值
	 */

	/**
	 * 设置风险度评估值
	 *
	 * @param riskScore 风险度评估值
	 */

	/**
	 * 获得使用度评估值
	 * @return 使用度评估值
	 */

	/**
	 * 设置使用度评估值
	 *
	 * @param usageScore 使用度评估值
	 */

	/**
	 * 获得授信结果代码
	 * @return 授信结果代码
	 */

	/**
	 * 设置授信结果代码
	 *
	 * @param resultID 授信结果代码
	 */

	/**
	 * 获得授信结果描述
	 * @return 授信结果描述
	 */

	/**
	 * 设置授信结果描述
	 *
	 * @param resultDescription 授信结果描述
	 */

	/**
	 * 获得系统建议的初始额度
	 * @return 系统建议的初始额度
	 */

	/**
	 * 设置系统建议的初始额度
	 *
	 * @param initialLineRMB 系统建议的初始额度
	 */

	/**
	 * 获得双币种卡中的美元额度
	 * @return 双币种卡中的美元额度
	 */

	/**
	 * 设置双币种卡中的美元额度
	 *
	 * @param initialLineFRC 双币种卡中的美元额度
	 */

	/**
	 * 获得该授信决策的原因代码
	 * @return 该授信决策的原因代码
	 */
	public String getUnderwritingReasonCode() {

		return (this.underwritingReasonCode);

	}

	/**
	 * 设置该授信决策的原因代码
	 *
	 * @param underwritingReasonCode 该授信决策的原因代码
	 */
	public void setUnderwritingReasonCode(String underwritingReasonCode) {

		this.underwritingReasonCode = underwritingReasonCode;
	}

	/**
	 * 获得该授信决策的原因描述
	 * @return 该授信决策的原因描述
	 */
	public String getUnderwritingReasonDescription() {

		return (this.underwritingReasonDescription);

	}

	/**
	 * 设置该授信决策的原因描述
	 *
	 * @param underwritingReasonDescription 该授信决策的原因描述
	 */
	public void setUnderwritingReasonDescription(String
												 underwritingReasonDescription) {

		this.underwritingReasonDescription = underwritingReasonDescription;
	}

	/**
	 * 获得该初始额度决策的原因代码
	 * @return 该初始额度决策的原因代码
	 */
	public String getInitLineReasonCode() {

		return (this.initLineReasonCode);

	}

	/**
	 * 设置该初始额度决策的原因代码
	 *
	 * @param initLineReasonCode 该初始额度决策的原因代码
	 */
	public void setInitLineReasonCode(String initLineReasonCode) {

		this.initLineReasonCode = initLineReasonCode;
	}

	/**
	 * 获得决策始额度决策的原因描述
	 * @return 决策始额度决策的原因描述
	 */
	public String getInitLineReasonDescription() {

		return (this.initLineReasonDescription);

	}

	/**
	 * 设置决策始额度决策的原因描述
	 *
	 * @param initLineReasonDescription 决策始额度决策的原因描述
	 */
	public void setInitLineReasonDescription(String initLineReasonDescription) {

		this.initLineReasonDescription = initLineReasonDescription;
	}

	/**
	 * 获得推荐降级处理的产品代码
	 * @return 推荐降级处理的产品代码
	 */

	/**
	 * 设置推荐降级处理的产品代码
	 *
	 * @param downsellProductID 推荐降级处理的产品代码
	 */

	/**
	 * 获得该授信决策中使用的一个或多个策略ID
	 * @return 该授信决策中使用的一个或多个策略ID
	 */

	/**
	 * 设置该授信决策中使用的一个或多个策略ID
	 *
	 * @param underwritingStrategyIDs 该授信决策中使用的一个或多个策略ID
	 */

	/**
	 * 获得该始额度决策决策中使用的一个或多个策略ID
	 * @return 该始额度决策决策中使用的一个或多个策略ID
	 */

	/**
	 * 设置该始额度决策决策中使用的一个或多个策略ID
	 *
	 * @param initLineStrategyIDs 该始额度决策决策中使用的一个或多个策略ID
	 */

	public String toString() {
		String str = "applicationId =" + applicationId
			+ ",applicantRandomDigitID=" + applicantRandomDigitID
			+ ",riskScore=" + (riskScore == -1 ? "" : String.valueOf(riskScore))
			+ ",usageScore=" +
			(usageScore == -1 ? "" : String.valueOf(usageScore))
			+ ",creditScore=" + creditScore
			+",aprAnnIncome"+aprAnnIncome
			+ ",scorecardID=" + scorecardID + ",hasDownSell=" + hasDownSell
			+ ",productCode=" + productCode
			+ ",principalResultID=" + principalResultID
			+ ",principalResultDescription=" + principalResultDescription
			+ ",principalInitialLineRMB=" +
			(principalInitialLineRMB == -1 ? "" :
			 String.valueOf(principalInitialLineRMB))
			+ ",principalInitialLineFRC=" +
			(principalInitialLineFRC == -1 ? "" :
			 String.valueOf(principalInitialLineFRC))
			+ ",supplResultID=" + supplResultID
			+ ",supplResultDescription=" + supplResultDescription
			+ ",supplInitialLineRMB=" +
			(supplInitialLineRMB == -1 ? "" :
			 String.valueOf(supplInitialLineRMB))
			+ ",supplInitialLineFRC=" +
			(supplInitialLineFRC == -1 ? "" :
			 String.valueOf(supplInitialLineFRC))
			+ ",underwritingReasonCode=" + underwritingReasonCode
			+ ",underwritingReasonDescription=" + underwritingReasonDescription
			+ ",initLineReasonCode=" + initLineReasonCode
			+ ",initLineReasonDescription=" + initLineReasonDescription
			+ ",strategiesLogID=" + strategiesLogID
			+ ",strategyID=" + strategyID + ",acquisitionstrategyID=" + acquStrategyID + ",amountlandStrategyID=" + amouStrategyID + ",exchangeRate=" +
			(exchangeRate == -1 ? "" : String.valueOf(exchangeRate))
			+ ",principalCashAdvanceRMB=" +
			(principalCashAdvanceRMB == -1 ? "" :
			 String.valueOf(principalCashAdvanceRMB))
			+ ",principalCashAdvanceFRC=" +
			(this.principalCashAdvanceFRC == -1 ? "" :
			 String.valueOf(principalCashAdvanceFRC))
			+ ",supplCashAdvanceRMB=" +
			(supplCashAdvanceRMB == -1 ? "" :
			 String.valueOf(supplCashAdvanceRMB))
			+ ",supplCashAdvanceFRC=" +
			(supplCashAdvanceFRC == -1 ? "" :
			 String.valueOf(supplCashAdvanceFRC))
			+ ",principalCashAdvanceRMBRate=" +
			(principalCashAdvanceRMBRate == -1 ? "" :
			 String.valueOf(principalCashAdvanceRMBRate))
			+ ",principalCashAdvanceFRCRate=" +
			(this.principalCashAdvanceFRCRate == -1 ? "" :
			 String.valueOf(principalCashAdvanceFRCRate))
			+ ",supplCashAdvanceRMBRate=" +
			(supplCashAdvanceRMBRate == -1 ? "" :
			 String.valueOf(supplCashAdvanceRMBRate))
			+ ",supplCashAdvanceFRCRate=" +
			(supplCashAdvanceFRCRate == -1 ? "" :
			 String.valueOf(supplCashAdvanceFRCRate))

			+ ",reservedField1=" + reservedField1
			+ ",reservedField2=" + reservedField2
			+ ",reservedField3=" + reservedField3
			+ ",reservedField4=" + reservedField4
			+ ",reservedField5=" + reservedField5
			+ ",isChallenge=" + isChallenge
			+",decisionPoint=" + decisionPoint;
		return str;
	}

	public String toXMLString() {
		String str = CDSEUtil.xmlToString(this.toXML());
		return str;
	}

	/**
	 * 将决策结果转化为dom对象
	 *
	 * @return 决策结果dom树
	 */
	public Document toXML() {

		Document doc = null;
		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			return doc;
		}

		doc = db.newDocument();

		ProcessingInstruction pi = doc.createProcessingInstruction(
			"xml-stylesheet",
			"href=\"./DATA_RESULT_STRUCT.xsl\" type=\"text/xsl\"");
		doc.appendChild(pi);

		//下面是建立XML文档内容的过程，先建立根元素"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//下面是建立XML文档内容的过程，先建立二级元素"data"
		Element data = doc.createElement("data");
		//根元素添加上文档

		xml.appendChild(data);

		//建立row元素，添加到data
		Element row = doc.createElement("result");

		//将结果属性值添加到row
		row.setAttribute("applicationId", this.applicationId);
		row.setAttribute("applicantRandomDigitID",
						 String.valueOf(this.applicantRandomDigitID));
		row.setAttribute("riskScore",
						 (riskScore == -1 ? "" : String.valueOf(riskScore)));
		row.setAttribute("usageScore",
						 (usageScore == -1 ? "" : String.valueOf(usageScore)));
		row.setAttribute("usageGrade",
						 (usageGrade == -1 ? "" : String.valueOf(usageGrade)));
		row.setAttribute("riskGrade",
						 (riskGrade == -1 ? "" : String.valueOf(riskGrade)));
		row.setAttribute("exchangeRate",
						 this.exchangeRate == -1 ? "" :
						 String.valueOf(this.exchangeRate));
		row.setAttribute("hasDownSell", String.valueOf(this.hasDownSell));
		row.setAttribute("productCode", this.productCode);
		row.setAttribute("principalResultID", this.principalResultID);
		row.setAttribute("principalResultDescription",
						 this.principalResultDescription);
		row.setAttribute("underwritingReasonCode",
						 String.valueOf(this.underwritingReasonCode));
		row.setAttribute("aprAnnIncome",  String.valueOf(this.aprAnnIncome));
		row.setAttribute("underwritingReasonDescription",
						 this.underwritingReasonDescription);
		row.setAttribute("principalInitialLineRMB",
						 this.principalInitialLineRMB == -1 ? "" :
						 String.valueOf(this.principalInitialLineRMB));
		row.setAttribute("principalInitialLineFRC",
						 this.principalInitialLineFRC == -1 ? "" :
						 String.valueOf(this.principalInitialLineFRC));
		row.setAttribute("principalCashAdvanceRMB",
						 this.principalCashAdvanceRMB == -1 ? "" :
						 String.valueOf(this.principalCashAdvanceRMB));
		row.setAttribute("principalCashAdvanceFRC",
						 this.principalCashAdvanceFRC == -1 ? "" :
						 String.valueOf(this.principalCashAdvanceFRC));
		row.setAttribute("initLineReasonCode",
						 String.valueOf(this.initLineReasonCode));
		row.setAttribute("initLineReasonDescription",
						 this.initLineReasonDescription);
		row.setAttribute("strategiesLogID", this.strategiesLogID);
		row.setAttribute("creditScore", creditScore);
		row.setAttribute("scorecardID", scorecardID);
		//row.setAttribute("strategyID", strategiesLogID);
		row.setAttribute("strategyID", strategyID);
		row.setAttribute("acquisitionStrategyID", acquStrategyID);
		row.setAttribute("amountlandStrategyID", amouStrategyID);
		/**C1507原挑战转为冠军*/
		//row.setAttribute("isChallenge", isChallenge);
		row.setAttribute("isChallenge", "C1601");
		row.setAttribute("decisionPoint", decisionPoint);
		data.appendChild(row);

		/////////////////////////  下面开始处理附属卡 ///////////////////////////////////////
		ArrayList supplList = this.getSupplResultList();
		if (supplList == null) {
			return doc;
		}
		for (java.util.Iterator it = supplList.iterator(); it.hasNext(); ) {
			SupplResult supplResult = (SupplResult) it.next();
			Element supplData = doc.createElement("supplData");

			supplData.setAttribute("supplID",
								   "-1".equals(supplResult.getSupplID()) ? "" :
								   supplResult.getSupplID());
			supplData.setAttribute("supplResultID",
								   supplResult.getSupplResultID());
			supplData.setAttribute("supplResultDescription",
								   supplResult.getSupplResultDescription());
			supplData.setAttribute("supplUwReasonCode",
								   String.valueOf(supplResult.
												  getSupplUwReasonCode()));
			supplData.setAttribute("supplUwReasonDescription",
								   supplResult.getSupplUwReasonDescription());
			supplData.setAttribute("supplInitialLineRMB",
								   supplResult.getSupplInitialLineRMB() == -1 ?
								   "" :
								   String.valueOf(supplResult.getSupplInitialLineRMB()));
			supplData.setAttribute("supplInitialLineFRC",
								   supplResult.getSupplInitialLineFRC() == -1 ?
								   "" :
								   String.valueOf(supplResult.getSupplInitialLineFRC()));
			supplData.setAttribute("supplCashAdvanceRMB",
								   supplResult.getSupplCashAdvanceRMB() == -1 ?
								   "" :
								   String.valueOf(supplResult.getSupplCashAdvanceRMB()));
			supplData.setAttribute("supplCashAdvanceFRC",
								   supplResult.getSupplCashAdvanceFRC() == -1 ?
								   "" :
								   String.valueOf(supplResult.getSupplCashAdvanceFRC()));
			row.appendChild(supplData);
		}
		///////////////////////////////////////////////////////////////////////////////////

		return doc;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getPrincipalResultID() {
		return principalResultID;
	}

	public void setPrincipalResultID(String principalResultID) {
		this.principalResultID = principalResultID;
	}

	public String getPrincipalResultDescription() {
		return principalResultDescription;
	}

	public void setPrincipalResultDescription(String principalResultDescription) {
		this.principalResultDescription = principalResultDescription;
	}

	public int getPrincipalInitialLineRMB() {
		return principalInitialLineRMB;
	}

	public void setPrincipalInitialLineRMB(int principalInitialLineRMB) {
		this.principalInitialLineRMB = principalInitialLineRMB;
	}

	public int getPrincipalInitialLineFRC() {
		return principalInitialLineFRC;
	}

	public void setPrincipalInitialLineFRC(int principalInitialLineFRC) {
		this.principalInitialLineFRC = principalInitialLineFRC;
	}

	public String getSupplResultID() {
		return supplResultID;
	}

	public void setSupplResultID(String supplResultID) {
		this.supplResultID = supplResultID;
	}

	public String getSupplResultDescription() {
		return supplResultDescription;
	}

	public void setSupplResultDescription(String supplResultDescription) {
		this.supplResultDescription = supplResultDescription;
	}

	public int getSupplInitialLineRMB() {
		return supplInitialLineRMB;
	}

	public void setSupplInitialLineRMB(int supplInitialLineRMB) {
		this.supplInitialLineRMB = supplInitialLineRMB;
	}

	public int getSupplInitialLineFRC() {
		return supplInitialLineFRC;
	}

	public void setSupplInitialLineFRC(int supplInitialLineFRC) {
		this.supplInitialLineFRC = supplInitialLineFRC;
	}

	public String getStrategiesLogID() {
		return strategiesLogID;
	}

	public void setStrategiesLogID(String strategiesLogID) {
		this.strategiesLogID = strategiesLogID;
	}

	public String getReservedField1() {
		return reservedField1;
	}

	public void setReservedField1(String reservedField1) {
		this.reservedField1 = reservedField1;
	}

	public String getReservedField2() {
		return reservedField2;
	}

	public void setReservedField2(String reservedField2) {
		this.reservedField2 = reservedField2;
	}

	public String getReservedField3() {
		return reservedField3;
	}

	public void setReservedField3(String reservedField3) {
		this.reservedField3 = reservedField3;
	}

	public String getReservedField4() {
		return reservedField4;
	}

	public void setReservedField4(String reservedField4) {
		this.reservedField4 = reservedField4;
	}

	public String getReservedField5() {
		return reservedField5;
	}

	public void setReservedField5(String reservedField5) {
		this.reservedField5 = reservedField5;
	}

	public int getRiskScore() {
		return riskScore;
	}

	public void setRiskScore(int riskScore) {
		this.riskScore = riskScore;
	}

	public int getUsageScore() {
		return usageScore;
	}

	public void setUsageScore(int usageScore) {
		this.usageScore = usageScore;
	}

	public String getScorecardID() {
		return scorecardID;
	}

	public void setScorecardID(String scorecardID) {
		this.scorecardID = scorecardID;
	}

	public int getHasDownSell() {
		return hasDownSell;
	}

	public void setHasDownSell(int hasDownSell) {
		this.hasDownSell = hasDownSell;
	}

	public String getValue(String cdseResult, String name) {
		String result = "";
		int first = 0;
		int second = 0;
		if (cdseResult != null) {
			first = cdseResult.indexOf(name);
			first = cdseResult.indexOf("\"", first);
			second = cdseResult.indexOf("\"", first + 1);
			if (second <= (first + 1)) {
				result = "";
			} else {
				result = cdseResult.substring(first + 1, second);
			}
		}
		return result;
	}

	public float getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public int getPrincipalCashAdvanceRMB() {
		return principalCashAdvanceRMB;
	}

	public void setPrincipalCashAdvanceRMB(int principalCashAdvanceRMB) {
		this.principalCashAdvanceRMB = principalCashAdvanceRMB;
	}

	public int getPrincipalCashAdvanceFRC() {
		return principalCashAdvanceFRC;
	}

	public void setPrincipalCashAdvanceFRC(int principalCashAdvanceFRC) {
		this.principalCashAdvanceFRC = principalCashAdvanceFRC;
	}

	public int getSupplCashAdvanceRMB() {
		return supplCashAdvanceRMB;
	}

	public void setSupplCashAdvanceRMB(int supplCashAdvanceRMB) {
		this.supplCashAdvanceRMB = supplCashAdvanceRMB;
	}

	public int getSupplCashAdvanceFRC() {
		return supplCashAdvanceFRC;
	}

	public void setSupplCashAdvanceFRC(int supplCashAdvanceFRC) {
		this.supplCashAdvanceFRC = supplCashAdvanceFRC;
	}

	public float getSupplCashAdvanceRMBRate() {
		return supplCashAdvanceRMBRate;
	}

	public void setSupplCashAdvanceRMBRate(float supplCashAdvanceRMBRate) {
		this.supplCashAdvanceRMBRate = supplCashAdvanceRMBRate;
		
	}

	public void setSupplCashAdvanceFRCRate(float supplCashAdvanceFRCRate) {
		this.supplCashAdvanceFRCRate = supplCashAdvanceFRCRate;
	}

	public float getSupplCashAdvanceFRCRate() {
		return supplCashAdvanceFRCRate;
	}

	public float getPrincipalCashAdvanceFRCRate() {
		return principalCashAdvanceFRCRate;
	}

	public void setPrincipalCashAdvanceFRCRate(float
											   principalCashAdvanceFRCRate) {
		this.principalCashAdvanceFRCRate = principalCashAdvanceFRCRate;
	}

	public float getPrincipalCashAdvanceRMBRate() {
		return principalCashAdvanceRMBRate;
	}

	public void setPrincipalCashAdvanceRMBRate(float
											   principalCashAdvanceRMBRate) {
		this.principalCashAdvanceRMBRate = principalCashAdvanceRMBRate;
	}

	public String getCreditScore() {
		return creditScore;
	}

	public ArrayList getSupplResultList() {
		return supplResultList;
	}

	public void setCreditScore(String creditScore) {
		this.creditScore = creditScore;
	}

	public void setSupplResultList(ArrayList supplResultList) {
		this.supplResultList = supplResultList;
	}

	public String getStrategyID() {
		return strategyID;
	}

	public int getRiskGrade() {
		return riskGrade;
	}

	public int getUsageGrade() {
		return usageGrade;
	}

	public void setStrategyID(String strategyID) {
		this.strategyID = strategyID;
	}

	public void setAcquStrategyID(String acquStrategyID) {
		this.acquStrategyID = acquStrategyID;
	}

	public String getAcquStrategyID() {
		return acquStrategyID;
	}

	public void setAmouStrategyID(String amouStrategyID) {
		this.amouStrategyID = amouStrategyID;
	}

	public String getAmouStrategyID() {
		return amouStrategyID;
	}

	public void setRiskGrade(int riskGrade) {
		this.riskGrade = riskGrade;
	}

	public void setUsageGrade(int usageGrade) {
		this.usageGrade = usageGrade;
	}

	private void jbInit() throws Exception {
	}

	public String getAprAnnIncome() {
		return aprAnnIncome;
	}

	public void setAprAnnIncome(String aprAnnIncome) {
		this.aprAnnIncome = aprAnnIncome;
	}

	public String getIsChallenge() {
		return isChallenge;
	}

	public void setIsChallenge(String isChallenge) {
		this.isChallenge = isChallenge;
	}

	public String getDecisionPoint() {
		return decisionPoint;
	}

	public void setDecisionPoint(String decisionPoint) {
		this.decisionPoint = decisionPoint;
	}

	
}
