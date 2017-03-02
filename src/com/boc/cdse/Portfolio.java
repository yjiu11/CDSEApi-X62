package com.boc.cdse;

/**
 * <p>Portfolio类包含产品线信息及所有的政策信息、策略信息</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class Portfolio
    implements java.io.Serializable {
    /** 产品线编号 */
    private String id;
    /** 产品线名称 */
    private String name;
    /** 属于此产品线的政策列表 */
    private java.util.ArrayList policyList = new java.util.ArrayList();
    /**
     * 产品线构造器
     */
    public Portfolio() {
    }

    /**
     * 通过指定的策略构造一个产品线。
     *
     * @param strategy 策略信息
     */

    public Portfolio(Strategy strategy) {
        this.id = strategy.getPortfolioId();
        policyList.add(new Policy(strategy));
    }

    /**
     * 获得产品线编号
     *
     * @return 产品线编号
     */

    public String getId() {
        return id;
    }

    /**
     * 设置产品线编号
     *
     * @param id 产品线编号
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获得产品线名称
     *
     * @return 产品线名称
     */

    public String getName() {
        return name;
    }

    /**
     * 设置产品线名称
     *
     * @param name 产品线名称
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得属于此产品线的政策列表
     *
     * @return 属于此产品线的政策列表
     */
    public java.util.ArrayList getPolicyList() {
        return policyList;
    }

    /**
     * 设置属于此产品线的政策列表
     *
     * @param policyList 属于此产品线的政策列表
     */

    public void setPolicyList(java.util.ArrayList policyList) {
        this.policyList = policyList;
    }

    /**
     * 从产品线中获得指定的政策
     *
     * @param policyId 政策编号
     * @return 政策信息
     */
    public Policy getPolicyById(String policyId) {
        Policy policy = null;
        CDSLog clog = new CDSLog();
        Strategy activeStrategy = null;
        StringBuffer str = new StringBuffer();
        if(policyId==null){
        	str.append(policyId).append(policyId);
//        	str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//			str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
			str.append("policyList等于0");
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
//                	str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//        			str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
        			str.append("portfolioId: 值为").append(activeStrategy.getPortfolioId()+";").append("policyId: 值为").append(activeStrategy.getPolicyId()+";");
            		str.append("策略未激活");
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
     *   判断产品线是否存在指定的政策
     *
     * @param policyId 政策编号
     * @return true   存在
     *         false  不存在
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
     *   向产品线中添加一个新的政策
     *
     * @param policy 要添加的政策
     */
    public void appendPolicy(Policy policy) {
        policyList.add(policy);
    }

    /**
     *   更新产品线中一个政策
     *
     * @param 要更新的政策
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