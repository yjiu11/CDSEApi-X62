package com.boc.cdse;

import org.w3c.dom.*;

/**
 * <p>Strategy�ౣ��CDSE����ʱ����Ҫ�Ĳ�����Ϣ</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   PersonalCreditCardHandler
 */

public class Strategy implements java.io.Serializable, java.lang.Cloneable {
    /** ���Ա�� */
    private String id = null;
    /** �������������߱�� */
    private String policyId = null;
    /** ���������Ĳ�Ʒ�߱�� */
    private String portfolioId = null;
    /** ������ϸ��Ϣ */
    private org.w3c.dom.Document strategy = null;
	//���Ե�������Ϣ
    private XMLPolicy xmlPolicy = null;
	/** ����XML���� */
	private XMLStrategy xmlStrategy=null;
	/** �����ڲ��������� */
	private XMLInternal xmlInternal=null;
	/** ������У������� */
	private XMLChkRule xmlChkRule=null;
    /** ��ǰ�Ĳ����Ƿ���Ч */
    private boolean isActive = false;
    /** ���԰汾 */
    private String version;

    /**
     * Strategyȱʡ������
     */
    public Strategy() {
    }

    /**
     * ͨ������������ϸ��Ϣ��Document������һ�����Զ���
     *
     * @param strategy ����������Ϣ��Document����
     */
    public Strategy(org.w3c.dom.Document strategy) {
        this.strategy = strategy;
        if (strategy == null) {
            return;
        }
        //�Ӳ��������ж�ȡ���Ա�š����߱�š���Ʒ�߱�š��汾����Ϣ
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
     * ��ȡ���Ա��
     *
     * @param ���Ա��
     */

    public String getId() {
        return id;
    }

    /**
     * ���ò��Ա��
     *
     * @param id ���Ա��
     */

    public void setId(String id) {
        this.id = id;
    }

    /**
     * ��ȡ������ϸ��Ϣ
     *
     * @return ������ϸ��Ϣ��DOM����
     */

    public org.w3c.dom.Document getStrategy() {
        return strategy;
    }

    /**
     * ���ò�����ϸ��Ϣ
     *
     * @param strategy ������ϸ��Ϣ��DOM����
     */

    public void setStrategy(org.w3c.dom.Document strategy) {
        this.strategy = strategy;
    }

    /**
     * ��ȡ��ǰ�Ĳ����Ƿ���Ч
     *
     * @return true ��ǰ������Ч
     *         false ��ǰ���Բ���Ч
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * ʹ��ǰ�Ĳ�����Ч
     */
    public void setActive() {
        this.isActive = true;
    }

    /**
     * ʹ��ǰ�Ĳ���ʧЧ
     */
    public void setInactive() {
        this.isActive = false;
    }

    /**
     * ��ȡ�������������ߵı��
     *
     * @return ���߱��
     */

    public String getPolicyId() {
        return policyId;
    }

    /**
     * ���ò������������ߵı��
     *
     * @param id ���߱��
     */

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    /**
     * ���ò��������Ĳ�Ʒ�ߵı��
     *
     * @param ��Ʒ�߱��
     */

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * ��ȡ���������Ĳ�Ʒ�ߵı��
     *
     * @return ��Ʒ�߱��
     */

    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * ��ȡ���԰汾
     *
     * @return ���԰汾
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
     * ���ò��԰汾
     *
     * @param version ���԰汾
     */

    public void setVersion(String version) {
        this.version = version;
    }
	/**
     * �жϲ�������һ�������Ƿ���ͬһ��Ĳ���
     *
     * @param strategy Ҫ�ȽϵĲ���
     * @return true ��ͬһ�����
     *         false ����ͬһ�����
     */
    public boolean isSimilarTo(Strategy strategy) {
        return this.id.equals(strategy.getId());
    }

    /**
     * �жϲ��԰汾�Ƿ������һ�����Եİ汾
     *
     * @param strategy Ҫ�ȽϵĲ���
     * @return true   ��ǰ�Ĳ��԰汾��
     *         false  Ҫ�ȽϵĲ��԰汾��
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
     * �����԰汾���ָ���ĳ��ȣ�������ǰ�油0
     *
     * @param ver ���԰汾
     * @param length ָ���ĳ���
     * @return  ���԰汾
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
