package com.boc.cdse;

/**
 * <p>Portfolio�������Ʒ����Ϣ�����е�������Ϣ��������Ϣ</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class Portfolio
    implements java.io.Serializable {
    /** ��Ʒ�߱�� */
    private String id;
    /** ��Ʒ������ */
    private String name;
    /** ���ڴ˲�Ʒ�ߵ������б� */
    private java.util.ArrayList policyList = new java.util.ArrayList();
    /**
     * ��Ʒ�߹�����
     */
    public Portfolio() {
    }

    /**
     * ͨ��ָ���Ĳ��Թ���һ����Ʒ�ߡ�
     *
     * @param strategy ������Ϣ
     */

    public Portfolio(Strategy strategy) {
        this.id = strategy.getPortfolioId();
        policyList.add(new Policy(strategy));
    }

    /**
     * ��ò�Ʒ�߱��
     *
     * @return ��Ʒ�߱��
     */

    public String getId() {
        return id;
    }

    /**
     * ���ò�Ʒ�߱��
     *
     * @param id ��Ʒ�߱��
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * ��ò�Ʒ������
     *
     * @return ��Ʒ������
     */

    public String getName() {
        return name;
    }

    /**
     * ���ò�Ʒ������
     *
     * @param name ��Ʒ������
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * ������ڴ˲�Ʒ�ߵ������б�
     *
     * @return ���ڴ˲�Ʒ�ߵ������б�
     */
    public java.util.ArrayList getPolicyList() {
        return policyList;
    }

    /**
     * �������ڴ˲�Ʒ�ߵ������б�
     *
     * @param policyList ���ڴ˲�Ʒ�ߵ������б�
     */

    public void setPolicyList(java.util.ArrayList policyList) {
        this.policyList = policyList;
    }

    /**
     * �Ӳ�Ʒ���л��ָ��������
     *
     * @param policyId ���߱��
     * @return ������Ϣ
     */
    public Policy getPolicyById(String policyId) {
        Policy policy = null;
        CDSLog clog = new CDSLog();
        Strategy activeStrategy = null;
        StringBuffer str = new StringBuffer();
        if(policyId==null){
        	str.append(policyId).append(policyId);
//        	str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//			str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
			str.append("policyList����0");
        	LogManager.getInstance().toCdsLog(str.toString()+";");
        	if(policyList.size()==0){
        		LogManager.getInstance().toCdsLog(str.toString());
        	}
        }
        if (policyId != null) {
            for (int i = 0; i < policyList.size(); i++) {
                Policy currentPolicy = (Policy) policyList.get(i);
                if(currentPolicy==null){
                	str = null;
//                	str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//        			str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
        			str.append("portfolioId: ֵΪ").append(activeStrategy.getPortfolioId()+";").append("policyId: ֵΪ").append(activeStrategy.getPolicyId()+";");
            		str.append("����δ����");
                	LogManager.getInstance().toCdsLog(str.toString());
                }
                if (policyId.equals(currentPolicy.getId())) {
                    policy = currentPolicy;
                    break;
                }
            }
        }
        return policy;
    }

    /**
     *   �жϲ�Ʒ���Ƿ����ָ��������
     *
     * @param policyId ���߱��
     * @return true   ����
     *         false  ������
     */
    public boolean isExistedPolicy(String policyId) {
        boolean isExisted = false;
        if (policyId != null) {
            for (int i = 0; i < policyList.size(); i++) {
                Policy currentPolicy = (Policy) policyList.get(i);
                if (policyId.equals(currentPolicy.getId())) {
                    isExisted = true;
                    break;
                }
            }
        }
        return isExisted;
    }

    /**
     *   ���Ʒ�������һ���µ�����
     *
     * @param policy Ҫ��ӵ�����
     */
    public void appendPolicy(Policy policy) {
        policyList.add(policy);
    }

    /**
     *   ���²�Ʒ����һ������
     *
     * @param Ҫ���µ�����
     */
    public void updatePolicy(Policy policy) {
        if (policy != null) {
            for (int i = 0; i < policyList.size(); i++) {
                Policy currentPolicy = (Policy) policyList.get(i);
                if (policy.getId().equals(currentPolicy.getId())) {
                    policyList.set(i, policy);
                    break;
                }
            }
        }
    }

}