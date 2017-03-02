package com.boc.cdse;

/**
 * <p>Policy类包含政策信息及所有的策略信息</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class Policy
    implements java.io.Serializable {
    /** 政策编号 */
    private String id;
    /** 政策名称 */
    private String name;
    /** 属于此政策的策略列表 */
    private java.util.ArrayList strategyList = new java.util.ArrayList();

    /**
     * 策略构造器
     */
    public Policy() {
    }

    /**
     * 通过指定的策略构造一个政策。
     *
     * @param strategy 策略信息
     */
    public Policy(Strategy strategy) {
        this.id = strategy.getPolicyId();
        strategyList.add(strategy);
    }

    /**
     * 获得政策编号
     *
     * @return 政策编号
     */

    public String getId() {
        return id;
    }

    /**
     * 设置政策编号
     *
     * @param id 政策编号
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获得政策名称
     *
     * @return 政策名称
     */

    public String getName() {
        return name;
    }

    /**
     * 设置政策名称
     *
     * @param name 政策名称
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获得属于此政策的策略列表
     *
     * @return 属于此政策的策略列表
     */

    public java.util.ArrayList getStrategyList() {
        return strategyList;
    }

    /**
     * 设置属于此政策的策略列表
     *
     * @param strategyList 属于此政策的策略列表
     */

    public void setStrategyList(java.util.ArrayList strategyList) {
        this.strategyList = strategyList;
    }

    /**
     * 从政策中获取生效的策略
     *
     * @return 生效的策略
     */
    public Strategy getActiveStrategy() {
        Strategy activeStrategy = null;
        CDSLog clog = new CDSLog();
        if(strategyList.size()==0){
        	LogManager.getInstance().toCdsLog("strategyList: 值等于0");
        }
        for (int i = 0; i < strategyList.size(); i++) {
            Strategy currentStrategy = (Strategy) strategyList.get(i);
            if(!currentStrategy.isActive()){
            	StringBuffer str = new StringBuffer();
            	str.append("portfolioId: 值为"+activeStrategy.getPortfolioId()+";");
            	str.append("policyId: 值为"+activeStrategy.getPolicyId()+";");
//            	str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//				str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
				str.append("策略未激活");
            	LogManager.getInstance().toCdsLog(str.toString());
            }
            if (currentStrategy.isActive()) {
                activeStrategy = currentStrategy;
            }
        }
        return activeStrategy;
    }

    /**
     * 检测政策中是否存在指定的策略
     *
     * @param strategyId 策略编号
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
     * 发现一个高版本策略，更新此策略的版本
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
     * 检测指定的策略在政策中是否是一个高的版本
     *
     * @param strategy 指定的策略
     * @return true  指定的策略在此政策中是一高版本的策略
     *         false   指定的策略在此政策中不是一高版本的策略
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
     * 添加一个新的策略并使此策略生效
     *
     * @param strategy 策略信息
     */
    public void appendStrategy(Strategy strategy) {
        strategy.setActive();
        strategyList.add(strategy);
    }

    /**
     * 更新一个策略并使此策略生效
     *
     * @param strategy 策略信息
     */
    public void updateStrategy(Strategy strategy) {
        for (int i = 0; i < strategyList.size(); i++) {
            ( (Strategy) strategyList.get(i)).setInactive();
        }
        strategy.setActive();
        strategyList.add(strategy);
    }
}
