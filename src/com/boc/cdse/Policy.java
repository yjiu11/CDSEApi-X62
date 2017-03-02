package com.boc.cdse;

/**
 * <p>Policy�����������Ϣ�����еĲ�����Ϣ</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class Policy
    implements java.io.Serializable {
    /** ���߱�� */
    private String id;
    /** �������� */
    private String name;
    /** ���ڴ����ߵĲ����б� */
    private java.util.ArrayList strategyList = new java.util.ArrayList();

    /**
     * ���Թ�����
     */
    public Policy() {
    }

    /**
     * ͨ��ָ���Ĳ��Թ���һ�����ߡ�
     *
     * @param strategy ������Ϣ
     */
    public Policy(Strategy strategy) {
        this.id = strategy.getPolicyId();
        strategyList.add(strategy);
    }

    /**
     * ������߱��
     *
     * @return ���߱��
     */

    public String getId() {
        return id;
    }

    /**
     * �������߱��
     *
     * @param id ���߱��
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * �����������
     *
     * @return ��������
     */

    public String getName() {
        return name;
    }

    /**
     * ������������
     *
     * @param name ��������
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * ������ڴ����ߵĲ����б�
     *
     * @return ���ڴ����ߵĲ����б�
     */

    public java.util.ArrayList getStrategyList() {
        return strategyList;
    }

    /**
     * �������ڴ����ߵĲ����б�
     *
     * @param strategyList ���ڴ����ߵĲ����б�
     */

    public void setStrategyList(java.util.ArrayList strategyList) {
        this.strategyList = strategyList;
    }

    /**
     * �������л�ȡ��Ч�Ĳ���
     *
     * @return ��Ч�Ĳ���
     */
    public Strategy getActiveStrategy() {
        Strategy activeStrategy = null;
        CDSLog clog = new CDSLog();
        if(strategyList.size()==0){
        	LogManager.getInstance().toCdsLog("strategyList: ֵ����0");
        }
        for (int i = 0; i < strategyList.size(); i++) {
            Strategy currentStrategy = (Strategy) strategyList.get(i);
            if(!currentStrategy.isActive()){
            	StringBuffer str = new StringBuffer();
            	str.append("portfolioId: ֵΪ"+activeStrategy.getPortfolioId()+";");
            	str.append("policyId: ֵΪ"+activeStrategy.getPolicyId()+";");
//            	str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//				str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
				str.append("����δ����");
            	LogManager.getInstance().toCdsLog(str.toString());
            }
            if (currentStrategy.isActive()) {
                activeStrategy = currentStrategy;
            }
        }
        return activeStrategy;
    }

    /**
     * ����������Ƿ����ָ���Ĳ���
     *
     * @param strategyId ���Ա��
     */
    public boolean isExistedStrategy(String strategyId) {
        boolean isExisted = false;
        if (strategyId != null) {
            Strategy strategy = null;
            for (int i = 0; i < strategyList.size(); i++) {
                Strategy currentStrategy = (Strategy) strategyList.get(i);
                if (strategyId.equals(currentStrategy.getId())) {
                    isExisted = true;
                    break;
                }
            }
        }
        return isExisted;
    }

    /**
     * ����һ���߰汾���ԣ����´˲��Եİ汾
     */
    public void findHigherVersion(Strategy strategy) {
        if (strategy != null) {
            for (int i = 0; i < strategyList.size(); i++) {
                Strategy currentStrategy = (Strategy) strategyList.get(i);
                if (currentStrategy.isSimilarTo(strategy)) {
                    currentStrategy.setInactive();
                    strategyList.set(i, currentStrategy);
                }
            }
            strategy.setActive();
            strategyList.add(strategy);
        }
    }

    /**
     * ���ָ���Ĳ������������Ƿ���һ���ߵİ汾
     *
     * @param strategy ָ���Ĳ���
     * @return true  ָ���Ĳ����ڴ���������һ�߰汾�Ĳ���
     *         false   ָ���Ĳ����ڴ������в���һ�߰汾�Ĳ���
     */
    public boolean isHigherVersion(Strategy strategy) {
        boolean isHigher = true;
        if (strategy != null) {
            for (int i = 0; i < strategyList.size(); i++) {
                Strategy currentStrategy = (Strategy) strategyList.get(i);
                if (currentStrategy.isSimilarTo(strategy) &&
                    currentStrategy.isHigherThan(strategy)) {
                    isHigher = false;
                    break;
                }
            }
        }
        else {
            isHigher = false;
        }
        return isHigher;
    }

    /**
     * ���һ���µĲ��Բ�ʹ�˲�����Ч
     *
     * @param strategy ������Ϣ
     */
    public void appendStrategy(Strategy strategy) {
        strategy.setActive();
        strategyList.add(strategy);
    }

    /**
     * ����һ�����Բ�ʹ�˲�����Ч
     *
     * @param strategy ������Ϣ
     */
    public void updateStrategy(Strategy strategy) {
        for (int i = 0; i < strategyList.size(); i++) {
            ( (Strategy) strategyList.get(i)).setInactive();
        }
        strategy.setActive();
        strategyList.add(strategy);
    }
}
