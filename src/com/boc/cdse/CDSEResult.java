package com.boc.cdse;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import com.boc.cdse.CDSEUtil;
import com.boc.cdse.PersonalCreditCardHandler;
import com.boc.cdse.SupplResult;

/**
 * <p>CDSEResult�ౣ��CDSE�����ÿ������������ż���Ⱦ��߽��</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public final class CDSEResult implements Serializable {

	/** �����˵������ */
	private String applicationId = "";

	/** �����������ID  */
	private int applicantRandomDigitID = 0;

	/** ���ն�����ֵ  */
	private int riskScore = 0;
	/** ʹ������  */
	private int usageGrade = 0;

	/** ��������  */
	private int riskGrade = 0;
	/** ����*/
	private float exchangeRate = 0;

	/** ʹ�ö�����ֵ */
	private int usageScore = 0;

	/** ���ֿ��汾�� */
	private String scorecardID = "";

	/** �Ƿ񾭹����� */
	/** ����Ϊ��������־ 0-δ�������� 1-�Ѿ������� 2-�Ѿ������� */
	private int hasDownSell;

	/** ���ղ�Ʒ�Ĳ�Ʒ�� */
	private String productCode = "";

	/** �������Ž������ */
	private String principalResultID = "";

	/** �������Ž������ */
	private String principalResultDescription = "";

	/** ��������ԭ����� */
	private String underwritingReasonCode = "";

	/** ��������ԭ������ */
	private String underwritingReasonDescription = "";

	/** ��������Ҷ�� */
	private int principalInitialLineRMB = 0;

	/** ������Ҷ�� */
	private int principalInitialLineFRC = 0;

	/** ���������ȡ�ֶ�� */
	private int principalCashAdvanceRMB = 0;

	/** �������ȡ�ֶ�� */
	private int principalCashAdvanceFRC = 0;

	/** �������ԭ����� */
	private String initLineReasonCode = "";

	/** �������ԭ������ */
	private String initLineReasonDescription = "";

	/** ���������LIST*/
	private ArrayList supplResultList = null;

	/** ���߹��̼�¼ID */
	private String strategiesLogID = "";

	////////////������һ��������ʱû���ô���Ȩ�ұ���////////////////////////////////
	/** ���������Ž������ */
	private String supplResultID = "";
	/** ���������Ž������ */
	private String supplResultDescription = "";
	/** ����������ҳ�ʼ��� */
	private int supplInitialLineRMB = 0;
	/** ��������ҳ�ʼ��� */
	private int supplInitialLineFRC = 0;
	/** ������ȡ������Ҷ�� */
	private int supplCashAdvanceRMB = 0;
	/** ������ȡ����Ҷ�� */
	private int supplCashAdvanceFRC = 0;
	/** ����ȡ�ֱ��ʣ�����ң� */
	private float principalCashAdvanceRMBRate = 0;
	/** ����ȡ�ֱ��ʣ���Ԫ�� */
	private float principalCashAdvanceFRCRate = 0;
	/** ������ȡ�ֱ��ʣ�����ң� */
	private float supplCashAdvanceRMBRate = 0;
	/** ������ȡ���ʣ���Ԫ�� */
	private float supplCashAdvanceFRCRate = 0;
	/** ���õ��ֶ�1 */
	private String reservedField1 = "";
	/** ���õ��ֶ�2 */
	private String reservedField2 = "";
	/** ���õ��ֶ�3 */
	private String reservedField3 = "";
	/** ���õ��ֶ�4 */
	private String reservedField4 = "";
	/** ���õ��ֶ�5 */
	private String reservedField5 = "";
	private String creditScore = "";

	/** ���Ա�� */
	private String strategyID = "";
	/** ���Ų��Ա�� */
	private String acquStrategyID = "";
	/** ��Ȳ��Ա�� */
	private String amouStrategyID = "";
	/**�ж��Ƿ���ս��1�ھ�2��ս,������ڣ�2015��10��25��*/
	/**C1506��սȫϽ���У�C1507Ͷ��ʱ��ԭ��սתΪ�ھ�����*/
	private String isChallenge = "";
	private String decisionPoint = ""; 
	//��֤��������
	private String aprAnnIncome = "" ;
	//////////////////////////////////////////////////////////////////////////
    
	/**
	 * Ĭ�Ϲ��캯��
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
	 * ���캯��.���ݾ��߽��dom������ʼ�����߽��ʵ�����Ա���ʾ
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
//ȡ�ֱ���

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
//��֤���������
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

				///////// ȡ��������������///////////////////////////////////////////
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

				////////////////////  ���洦������ ////////////////////////////////////////////

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
	 * ��������˵������
	 * @return �����˵������
	 */
	public String getApplicationId() {
		return (this.applicationId);
	}

	/**
	 * ���������˵������
	 *
	 * @param applicationId �����˵������
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	/**
	 * ��������������ID
	 * @return �����������ID
	 */
	public int getApplicantRandomDigitID() {
		return (this.applicantRandomDigitID);
	}

	/**
	 * ���������������ID
	 *
	 * @param applicantRandomDigitID �����������ID
	 */
	public void setApplicantRandomDigitID(int applicantRandomDigitID) {
		this.applicantRandomDigitID = applicantRandomDigitID;
	}

	/**
	 * ��÷��ն�����ֵ
	 * @return ���ն�����ֵ
	 */

	/**
	 * ���÷��ն�����ֵ
	 *
	 * @param riskScore ���ն�����ֵ
	 */

	/**
	 * ���ʹ�ö�����ֵ
	 * @return ʹ�ö�����ֵ
	 */

	/**
	 * ����ʹ�ö�����ֵ
	 *
	 * @param usageScore ʹ�ö�����ֵ
	 */

	/**
	 * ������Ž������
	 * @return ���Ž������
	 */

	/**
	 * �������Ž������
	 *
	 * @param resultID ���Ž������
	 */

	/**
	 * ������Ž������
	 * @return ���Ž������
	 */

	/**
	 * �������Ž������
	 *
	 * @param resultDescription ���Ž������
	 */

	/**
	 * ���ϵͳ����ĳ�ʼ���
	 * @return ϵͳ����ĳ�ʼ���
	 */

	/**
	 * ����ϵͳ����ĳ�ʼ���
	 *
	 * @param initialLineRMB ϵͳ����ĳ�ʼ���
	 */

	/**
	 * ���˫���ֿ��е���Ԫ���
	 * @return ˫���ֿ��е���Ԫ���
	 */

	/**
	 * ����˫���ֿ��е���Ԫ���
	 *
	 * @param initialLineFRC ˫���ֿ��е���Ԫ���
	 */

	/**
	 * ��ø����ž��ߵ�ԭ�����
	 * @return �����ž��ߵ�ԭ�����
	 */
	public String getUnderwritingReasonCode() {

		return (this.underwritingReasonCode);

	}

	/**
	 * ���ø����ž��ߵ�ԭ�����
	 *
	 * @param underwritingReasonCode �����ž��ߵ�ԭ�����
	 */
	public void setUnderwritingReasonCode(String underwritingReasonCode) {

		this.underwritingReasonCode = underwritingReasonCode;
	}

	/**
	 * ��ø����ž��ߵ�ԭ������
	 * @return �����ž��ߵ�ԭ������
	 */
	public String getUnderwritingReasonDescription() {

		return (this.underwritingReasonDescription);

	}

	/**
	 * ���ø����ž��ߵ�ԭ������
	 *
	 * @param underwritingReasonDescription �����ž��ߵ�ԭ������
	 */
	public void setUnderwritingReasonDescription(String
												 underwritingReasonDescription) {

		this.underwritingReasonDescription = underwritingReasonDescription;
	}

	/**
	 * ��øó�ʼ��Ⱦ��ߵ�ԭ�����
	 * @return �ó�ʼ��Ⱦ��ߵ�ԭ�����
	 */
	public String getInitLineReasonCode() {

		return (this.initLineReasonCode);

	}

	/**
	 * ���øó�ʼ��Ⱦ��ߵ�ԭ�����
	 *
	 * @param initLineReasonCode �ó�ʼ��Ⱦ��ߵ�ԭ�����
	 */
	public void setInitLineReasonCode(String initLineReasonCode) {

		this.initLineReasonCode = initLineReasonCode;
	}

	/**
	 * ��þ���ʼ��Ⱦ��ߵ�ԭ������
	 * @return ����ʼ��Ⱦ��ߵ�ԭ������
	 */
	public String getInitLineReasonDescription() {

		return (this.initLineReasonDescription);

	}

	/**
	 * ���þ���ʼ��Ⱦ��ߵ�ԭ������
	 *
	 * @param initLineReasonDescription ����ʼ��Ⱦ��ߵ�ԭ������
	 */
	public void setInitLineReasonDescription(String initLineReasonDescription) {

		this.initLineReasonDescription = initLineReasonDescription;
	}

	/**
	 * ����Ƽ���������Ĳ�Ʒ����
	 * @return �Ƽ���������Ĳ�Ʒ����
	 */

	/**
	 * �����Ƽ���������Ĳ�Ʒ����
	 *
	 * @param downsellProductID �Ƽ���������Ĳ�Ʒ����
	 */

	/**
	 * ��ø����ž�����ʹ�õ�һ����������ID
	 * @return �����ž�����ʹ�õ�һ����������ID
	 */

	/**
	 * ���ø����ž�����ʹ�õ�һ����������ID
	 *
	 * @param underwritingStrategyIDs �����ž�����ʹ�õ�һ����������ID
	 */

	/**
	 * ��ø�ʼ��Ⱦ��߾�����ʹ�õ�һ����������ID
	 * @return ��ʼ��Ⱦ��߾�����ʹ�õ�һ����������ID
	 */

	/**
	 * ���ø�ʼ��Ⱦ��߾�����ʹ�õ�һ����������ID
	 *
	 * @param initLineStrategyIDs ��ʼ��Ⱦ��߾�����ʹ�õ�һ����������ID
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
	 * �����߽��ת��Ϊdom����
	 *
	 * @return ���߽��dom��
	 */
	public Document toXML() {

		Document doc = null;
		//Ϊ����XML��׼��������DocumentBuilderFactoryʵ��,ָ��DocumentBuilder
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

		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�����Ԫ��"xml"
		Element xml = doc.createElement("xml");
		doc.appendChild(xml);
		//�����ǽ���XML�ĵ����ݵĹ��̣��Ƚ�������Ԫ��"data"
		Element data = doc.createElement("data");
		//��Ԫ��������ĵ�

		xml.appendChild(data);

		//����rowԪ�أ���ӵ�data
		Element row = doc.createElement("result");

		//���������ֵ��ӵ�row
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
		/**C1507ԭ��սתΪ�ھ�*/
		//row.setAttribute("isChallenge", isChallenge);
		row.setAttribute("isChallenge", "C1601");
		row.setAttribute("decisionPoint", decisionPoint);
		data.appendChild(row);

		/////////////////////////  ���濪ʼ�������� ///////////////////////////////////////
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
