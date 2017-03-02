package com.boc.cdse;

import org.w3c.dom.*;

/**
 * <p>Strategy类保存CDSE运行时所需要的策略信息</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public class Strategy implements java.io.Serializable, java.lang.Cloneable {
    /** 策略编号 */
    private String id = null;
    /** 策略所属的政策编号 */
    private String policyId = null;
    /** 策略所属的产品线编号 */
    private String portfolioId = null;
    /** 策略详细信息 */
    private org.w3c.dom.Document strategy = null;
	//策略的政策信息
    private XMLPolicy xmlPolicy = null;
	/** 策略XML对象 */
	private XMLStrategy xmlStrategy=null;
	/** 策略内部变量对象 */
	private XMLInternal xmlInternal=null;
	/** 策略中校验规则项 */
	private XMLChkRule xmlChkRule=null;
    /** 当前的策略是否生效 */
    private boolean isActive = false;
    /** 策略版本 */
    private String version;

    /**
     * Strategy缺省构造器
     */
    public Strategy() {
    }

    /**
     * 通过包含策略详细信息的Document对象构造一个策略对象
     *
     * @param strategy 包含策略信息的Document对象
     */
    public Strategy(org.w3c.dom.Document strategy) {
        this.strategy = strategy;
        if (strategy == null) {
            return;
        }
        //从策略数据中读取策略编号、政策编号、产品线编号、版本等信息
        Element basic = strategy.getDocumentElement();
        Node tmpNode = null;

        tmpNode = basic.getFirstChild();
        Node policy = null;
        while (tmpNode != null) {
            if ("Policy".equals(tmpNode.getNodeName())) {
                policy = tmpNode;
                break;
            }
            tmpNode = tmpNode.getNextSibling();
        }
        if (policy != null) {
            policyId = ( (Element) policy).getAttribute("PlsNo");
            portfolioId = ( (Element) policy).getAttribute("ProdNo");
        }
        Node strategy1 = null;
        tmpNode = basic.getFirstChild();
        while (! (tmpNode == null)) {
            if ("Stratege".equals(tmpNode.getNodeName())) {
                strategy1 = tmpNode;
                break;
            }
            tmpNode = tmpNode.getNextSibling();
        }
        if (strategy1 != null) {
            id = ( (Element) strategy1).getAttribute("StgNo");
            version = converVersion( ( (Element) strategy1).getAttribute(
                "StgVer"), 4);
        }

	    xmlStrategy=new XMLStrategy();
		xmlInternal=new XMLInternal();
		xmlPolicy = new XMLPolicy();
		xmlChkRule=new XMLChkRule();
        xmlPolicy.parse(strategy);
		xmlStrategy.parse(strategy);
        xmlStrategy.policy = xmlPolicy;
		xmlInternal.parse(strategy);
		xmlChkRule.parse(strategy);
    }

    /**
     * 获取策略编号
     *
     * @param 策略编号
     */

    public String getId() {
        return id;
    }

    /**
     * 设置策略编号
     *
     * @param id 策略编号
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取策略详细信息
     *
     * @return 策略详细信息的DOM对象
     */

    public org.w3c.dom.Document getStrategy() {
        return strategy;
    }

    /**
     * 设置策略详细信息
     *
     * @param strategy 策略详细信息的DOM对象
     */

    public void setStrategy(org.w3c.dom.Document strategy) {
        this.strategy = strategy;
    }

    /**
     * 获取当前的策略是否生效
     *
     * @return true 当前策略生效
     *         false 当前策略不生效
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * 使当前的策略生效
     */
    public void setActive() {
        this.isActive = true;
    }

    /**
     * 使当前的策略失效
     */
    public void setInactive() {
        this.isActive = false;
    }

    /**
     * 获取策略所属的政策的编号
     *
     * @return 政策编号
     */

    public String getPolicyId() {
        return policyId;
    }

    /**
     * 设置策略所属的政策的编号
     *
     * @param id 政策编号
     */

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    /**
     * 设置策略所属的产品线的编号
     *
     * @param 产品线编号
     */

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取策略所属的产品线的编号
     *
     * @return 产品线编号
     */

    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 获取策略版本
     *
     * @return 策略版本
     */

    public String getVersion() {
        return version;
    }

	public XMLInternal getXmlInternal() {
		return xmlInternal;
	}

	public XMLStrategy getXmlStrategy() {
		return xmlStrategy;
	}

	public XMLChkRule getXmlChkRule() {
		return xmlChkRule;
	}

	/**
     * 设置策略版本
     *
     * @param version 策略版本
     */

    public void setVersion(String version) {
        this.version = version;
    }
	/**
     * 判断策略与另一个策略是否是同一类的策略
     *
     * @param strategy 要比较的策略
     * @return true 是同一类策略
     *         false 不是同一类策略
     */
    public boolean isSimilarTo(Strategy strategy) {
        return this.id.equals(strategy.getId());
    }

    /**
     * 判断策略版本是否高于另一个策略的版本
     *
     * @param strategy 要比较的策略
     * @return true   当前的策略版本高
     *         false  要比较的策略版本高
     */
    public boolean isHigherThan(Strategy strategy) {
        boolean isHigher = true;
        try {
            double version1 = Double.parseDouble(this.version);
            double verion2 = Double.parseDouble(strategy.getVersion());
            if (version1 <= verion2) {
                isHigher = false;
            }
        } catch (Exception e) {
            isHigher = false;
        }
        return isHigher;
    }

    /**
     * 将策略版本变成指定的长度，不足在前面补0
     *
     * @param ver 策略版本
     * @param length 指定的长度
     * @return  策略版本
     */
    public String converVersion(String ver, int length) {
        String newVer = ver;
        for (int i = 0; i < (length - ver.length()); i++) {
            newVer = "0" + newVer;
        }
        return newVer;
    }

    public Object clone() {
        try {
            Strategy clonedStrategy = new Strategy();
            clonedStrategy.setId(new String(this.id));
            clonedStrategy.setPortfolioId(new String(this.portfolioId));
            clonedStrategy.setPolicyId(new String(this.policyId));
            clonedStrategy.setStrategy( (Document) strategy.cloneNode(true));
            clonedStrategy.setVersion(new String(this.version));
            if (this.isActive == true) {
                clonedStrategy.setActive();
            } else {
                clonedStrategy.setInactive();
            }
            return clonedStrategy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
