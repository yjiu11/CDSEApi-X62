package com.boc.cdse;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * <p>StrategyManager�������Կ⣬������CDSE����ʱ���ز��Կ⣬�ڲ��԰汾����ʱ
 * �����²���ʱ���²��Կ⣬��Ϊ���ž��߼���Ⱦ��������ṩ��Ӧ�Ĳ��ԡ�
 * </p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 */

public class StrategyManager {
    private java.util.ArrayList portfolioList = new java.util.ArrayList(); //��Ʒ���б�
    private RunTimeEnvironment runTimeEnvironment = null;
    private static StrategyManager _instance = null; //���Թ��������ʵ��
    private StrategyListener strategyListener = null;
    private long strategyListenerTime = 0;
    private long lastWatchTime = System.currentTimeMillis();
    /** ��ʼ�����ļ������� */
    private String[] strategyFiles = null;

    /**
     * getInstance()��������һ��StrategyManagerʵ��
     *
     * @return StrategyManager ���Թ��������ʵ��
     */

    static public StrategyManager getInstance() {
        if (null == _instance) {
            _instance = new StrategyManager();
        }
        return _instance;
    }

    /**
     * ���Թ�������Ĺ��췽��
     *  �������Ϊ��
     * 1.��ȡRunTimeEnvironmentʵ������
     * 2.��������Ч�Ĳ��Ե����ڴ�
     */
    protected StrategyManager() {
        runTimeEnvironment = RunTimeEnvironment.getInstance();
        if (runTimeEnvironment != null) {
            loadActiveStrategys();
            strategyListener = StrategyListener.getInstance(this);
        }
    }

    /**
     *  ��ȡ�����ļ�������
     *
     * @param  �����ļ�������
     */
    public StrategyListener getStrategyListener() {
        return this.strategyListener;
    }

    /**
     * loadActiveStrategys()�����������е���Ч�Ĳ����ļ������ڴ档
     */
    public void loadActiveStrategys() {
        String[] strategyFilses = CDSEUtil.initFiles(runTimeEnvironment.
            getDomainDir());
        if (strategyFilses != null) {
            for (int i = 0; i < strategyFilses.length; i++) {
                loadNewStrategyFromFile(runTimeEnvironment.
                                        getDomainDir() + "/" + strategyFilses[i]);
            }
        }
        releaseInactiveStrategys();
        displayStrategyListForTesting();
        LogManager.getInstance().toAppLog(
            "----------End to load the strategy files------");
    }

    private synchronized void releaseInactiveStrategys () {
        for (int i = 0; i < portfolioList.size(); i++) {
            Portfolio currPortfolio = (Portfolio) portfolioList.get(i);
            java.util.ArrayList policyList = currPortfolio.getPolicyList();
            for (int j = 0; j < policyList.size(); j++) {
                Policy currPolicy = (Policy) policyList.get(j);
                java.util.ArrayList strategyList = currPolicy.getStrategyList();
                for (int k = 0; k < strategyList.size(); k++) {
                    Strategy currStrategy = (Strategy) strategyList.get(k);
                    if (!currStrategy.isActive()) {
                        strategyList.remove(k);
                    }
                }
            }
        }

    }

    private synchronized void displayStrategyListForTesting() {
        if (portfolioList.size() == 0) {
            return;
        }
        for (int i = 0; i < portfolioList.size(); i++) {
            Portfolio currPortfolio = (Portfolio) portfolioList.get(i);
            LogManager.getInstance().toAppLog(currPortfolio.getId());
            java.util.ArrayList policyList = currPortfolio.getPolicyList();
            for (int j = 0; j < policyList.size(); j++) {
                Policy currPolicy = (Policy) policyList.get(j);
                LogManager.getInstance().toAppLog("-----" + currPolicy.getId());
                java.util.ArrayList strategyList = currPolicy.getStrategyList();
                for (int k = 0; k < strategyList.size(); k++) {
                    Strategy currStrategy = (Strategy) strategyList.get(k);
                    LogManager.getInstance().toAppLog("--------------" +
                        currStrategy.getId() +
                        "  version=" + currStrategy.getVersion() +
                        " active=" + currStrategy.isActive());

                }
            }
        }

    }

    /**
     * verifyStrategy����У��strategyXML�����Ƿ���ȷ
     *
     * @param strategyXML ҪУ���strategyXML����
     * @return true       ������ȷ
     *         false      �������
     */
    private boolean verifyStrategy(org.w3c.dom.Document strategyXML) {
        //   String totalFileName = dir + "/" + xmlName;
        Element basic = strategyXML.getDocumentElement();
        Element policyList = null;
        Element strategyList = null;
        Element policyItemsList = null;
        Element policyInclusionList = null;
        Element strategyItemsList = null;
        NodeList items = null;
        NodeList items1 = null;
        try {
            policyList = (Element) basic.getElementsByTagName("Policy").item(0);
            policyItemsList = (Element) policyList.getElementsByTagName("Items").
                item(
                0);
            items = policyItemsList.getElementsByTagName("Item");
            if (items.getLength() <= 0) {
                String errorString = " �������ʽ���ݼ�������������ϴ���";
                //  createErrorFile(errorString);
                // deleteFile(totalFileName);
                LogManager.getInstance().toAppLog(errorString);
                return false;
            }
        } catch (Exception e) {
            //errorCode = -1;
            String errorString = " ���߸�ʽ��������������ϴ���";
            LogManager.getInstance().toAppLog(errorString);
            //  createErrorFile(totalFileName + errorString);
            //  deleteFile(totalFileName);
            return false;
        }
        try {
            strategyList = (Element) basic.getElementsByTagName("Stratege").
                item(0);
            strategyItemsList = (Element) strategyList.getElementsByTagName(
                "Items").
                item(0);
            items1 = strategyItemsList.getElementsByTagName("Item");
            if (items1.getLength() <= 0) {
                String errorString = " �������ݼ�������������ϴ���";
                //   createErrorFile(totalFileName + errorString);
                //   deleteFile(totalFileName);
                LogManager.getInstance().toAppLog(errorString);
                return false;
            }
        } catch (Exception ex1) {
            //errorCode = -1;
            String errorString = " ���Ը�ʽ��������������ϴ���";
            LogManager.getInstance().toAppLog(errorString);
            // createErrorFile(totalFileName + errorString);
            //  deleteFile(totalFileName);
            return false;
        }
        return true;
    }

    /**
     *findNewStrategyFile()������CDSE����һ���µĲ����ļ�ʱ�����ã������������ļ�����������ļ������²��ԿⲢ���������ļ��в��Բ��ֵ���Ϣ��
     *�������Ϊ��
     * ����loadNewStrategyFromFile����������������ļ�����������ļ������²��Կ�
     *
     * @param strategyFileName �����ļ���
     * @return true �����ļ����Գɹ�
     *         false �����ļ�����ʧ��
     */

    public synchronized boolean findNewStrategyFile(String strategyFileName) {
        LogManager.getInstance().toAppLog(
            "----------Begin to load the new strategy files------");
        boolean result = true;
        result = loadNewStrategyFromFile(strategyFileName);
        displayStrategyListForTesting();
        LogManager.getInstance().toAppLog(
            "----------End to load the strategy files------");
        return result;
    }

    /**
     *loadNewStrategyFromFile()������CDSE����һ���µĲ����ļ�ʱ�����ã������������ļ�����������ļ������²��Կ⡣
     *�������Ϊ��
     *1.���ݲ����ļ����������������
     *2.����verifyStrategy()����У�����Ĳ�������
     *3.�������������ȷ������findNewStrategy()�������²��Կ�
     *
     * @param  strategyFileName Ҫ���صĲ����ļ���
     * @return true             ���سɹ�
     *         false            ����ʧ��
     */
    private synchronized boolean loadNewStrategyFromFile(String strategyFileName) {
        boolean result = true;
        Document doc = null;
        doc = getStrategyContent(strategyFileName);
        if (doc == null) {
            return false;
        }
        if (verifyStrategy(doc) == true) {
            result= this.findNewStrategy(doc);
            releaseInactiveStrategys();
            return result;
        } else {
            return false;
        }
    }

    /**
     *getStrategyContent()������CDSE����һ���µĲ����ļ�ʱ�����ã������������ļ���
     *�������Ϊ��
     *1.���ݲ����ļ����������������
     *
     * @param strategyFileName �����ļ���
     * @return ����������Ϣ��DOM���������ȡʧ�ܣ��򷵻ؿ�
     */
    private org.w3c.dom.Document getStrategyContent(String strategyFileName) {
        //----------------��ȡXML�ļ�---------------------------------------------//
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        File strategyfile = new File(strategyFileName);
        Document doc = null;
        try {
            doc = db.parse(strategyfile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        strategyfile = null;
        return doc;
    }

    /**
     * getActiveStrategy()��������ָ���Ĳ�Ʒ�ߡ����ߣ����һ����ǰ��Ч�Ĳ���
	 *
     * @param portfolioId ��Ʒ�߱��
     * @param policyId ���߱��
     * @return ��Ч�Ĳ��ԣ����û�У����ؿ�
     */
	public synchronized Strategy getActiveStrategy(String portfolioId,
		String policyId) {
		CDSLog clog = new CDSLog();
		Strategy strategy = null;
		Portfolio portfolioBelongto = null;
		Policy policyBelongto = null;
		StringBuffer str=new StringBuffer(); 
		str.append("portfolioId: ").append(portfolioId+";").append("policyId: ").append(policyId+";");
		str.append("Strategy is null").append("from getActiveStrategy");
		if(portfolioId==null || policyId==null){
//			String str1 = str.toString();
//			str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//			str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
			LogManager.getInstance().toCdsLog(str.toString()+";");
		}
		if (portfolioId != null && policyId != null) {
			portfolioBelongto = getPortfolioByID(portfolioId);
			if(portfolioBelongto==null){
//				str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//				str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
				LogManager.getInstance().toCdsLog(str.toString());
				LogManager.getInstance().toCdsLog("portfolioBelongto: is null from getActiveStrategy");
			}
			if (portfolioBelongto != null) {
				policyBelongto = portfolioBelongto.getPolicyById(policyId);
				if(policyBelongto==null){
//					str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//					str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
					LogManager.getInstance().toCdsLog(str.toString());
					LogManager.getInstance().toCdsLog("policyBelongto: is null from getActiveStrategy");
				}
				if (policyBelongto != null) {
					strategy = policyBelongto.getActiveStrategy();
					if(strategy==null){
//						str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//						str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
						LogManager.getInstance().toCdsLog(str.toString());
						LogManager.getInstance().toCdsLog("strategy : is null from getActiveStrategy");
					}
				}
			}
		}
		watchStrategyListener();
		return strategy;
    }

    /**
     *����һ���²����ļ������²��Կ�
     *�������Ϊ��
     *1.��doc�ж�ȡ��Ʒ�߱�š����߱�š����Ա�ż����԰汾��Ϣ
     *2.�ж��Ƿ���ڲ����ļ������Ĳ�Ʒ��
     *   �����ڣ� �ֱ���һ���µĲ����ļ������Ĳ�Ʒ�߶���һ���²����ļ����������߶����²����ļ������Ķ�Ӧ���Զ�����Ӳ��Կ��в�����
     *   ���ڣ�������һ��
     *3.����ڲ����ļ������Ĳ�Ʒ�ߣ��ж��Ƿ���ڲ����ļ�����������
     *   �����ڣ��ֱ���һ���²����ļ����������߶����²����ļ������Ķ�Ӧ���Զ�����Ӳ��Կ��в�����
     *   ���ڣ�������һ��
     *4.����ڲ����ļ����������ߣ��ж��Ƿ���ڲ����ļ���Ӧ�Ĳ���
     *   �����ڣ��ֱ���һ���²����ļ������Ķ�Ӧ���Զ�����Ӳ��Կ��в�����
     *   ���ڣ�������һ��
     *5.����ڲ����ļ���Ӧ�Ĳ��ԣ��Ƚϲ��԰汾������ļ��汾�Ƚ��£�������Ӧ�Ĳ��ԡ�
     *
     * @param doc ����������Ϣ��DOM����
     * @return true ���Ը��³ɹ�
     *         false ���Ը���ʧ��
     */
    private synchronized boolean findNewStrategy(org.w3c.dom.Document doc) {
        Strategy strategy = new Strategy(doc);
        strategy.setActive();
        boolean hasNewVersion = true;
        String portfolioId = strategy.getPortfolioId();
        String policyId = strategy.getPolicyId();
        String strategyId = strategy.getId();
        String strategyVersion = strategy.getVersion();

        Portfolio portfolio = getPortfolioByID(portfolioId);
        if (portfolio == null) {
            portfolioList.add(new Portfolio(strategy));
        } else {
            Policy policy = portfolio.getPolicyById(policyId);
            if (policy == null) {
                portfolio.appendPolicy(new Policy(strategy));
                updatePortfolio(portfolio);
            } else {
                if (policy.isExistedStrategy(strategyId) == false) {
                    policy.appendStrategy(strategy);
                    portfolio.updatePolicy(policy);
                    updatePortfolio(portfolio);
                } else {
                    if (policy.isHigherVersion(strategy) == true) {
                        policy.findHigherVersion(strategy);
                        portfolio.updatePolicy(policy);
                        updatePortfolio(portfolio);
                    } else {
                        hasNewVersion = false;
                    }
                }
            }
        }
        return hasNewVersion;
    }

    /**
     *  ���������ļ��еĲ��԰汾����
     */
    private boolean updateStrategyConfigFile() {
        java.util.ArrayList activeStrategyList = new java.util.ArrayList();
        for (int i = 0; i < portfolioList.size(); i++) {
            Portfolio currPortfolio = (Portfolio) portfolioList.get(i);
            java.util.ArrayList policyList = currPortfolio.getPolicyList();
            for (int j = 0; j < policyList.size(); j++) {
                Policy currPolicy = (Policy) policyList.get(j);
                java.util.ArrayList strategyList = currPolicy.getStrategyList();
                for (int k = 0; k < strategyList.size(); k++) {
                    Strategy currStrategy = (Strategy) strategyList.get(k);
                    if (currStrategy.isActive()) {
                        activeStrategyList.add(currStrategy);
                    }
                }
            }
        }
        runTimeEnvironment.setActiveStrategyList(activeStrategyList);
        return runTimeEnvironment.saveConfigureFile();
    }

    /**
     * ���²��Կ�����Ӧ�Ĳ�Ʒ��
     *
     * @param portfolio Ҫ���µĲ�Ʒ��
     */
    private synchronized void updatePortfolio(Portfolio portfolio) {
        if (portfolio != null) {
            for (int i = 0; i < portfolioList.size(); i++) {
                Portfolio currentPortfolio = (Portfolio) portfolioList.get(i);
                if (portfolio.getId().equals(currentPortfolio.getId())) {
                    portfolioList.set(i, portfolio);
                    break;
                }
            }
        }
    }

    /**
     * ͨ��ָ���Ĳ�Ʒ�߱�ŴӲ��Կ��л�ò�Ʒ��
     *
     * @param id ��Ʒ�߱��
     * @return ��Ʒ��
     */
    private synchronized Portfolio getPortfolioByID(String id) {
        Portfolio portfolio = null;
        CDSLog clog = new CDSLog();
        StringBuffer str = new StringBuffer();
        if (id == null) {
        	LogManager.getInstance().toCdsLog("id is null from getPortfolioByID");
        } else {
        	if(portfolioList.size()<0){
//        		str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//    			str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
    			str.append("portfolioList.size<0 from getPortfolioByID");
        		LogManager.getInstance().toCdsLog(str.toString());
        	}
        	for (int i = 0; i < portfolioList.size(); i++) {
                Portfolio currentPortfolio = (Portfolio) portfolioList.get(i);
                if(currentPortfolio==null){
//                	str.append("CDS������Ϣ��").append(clog.getReceivingCDSXML()).append(";");
//        			str.append("CDS������Ϣ��").append(clog.getReturnCDSXML()).append(";");
                	str.append("currentPortfolio��is null from getPortfolioByID");
        			LogManager.getInstance().toCdsLog(str.toString());
                }
                if (id.equals(currentPortfolio.getId())) {
                    portfolio = currentPortfolio;
                    break;
                }
            }
        }
        return portfolio;
    }

    private void loadStrategyListenerParameter() {
        if (runTimeEnvironment != null) {
            this.strategyListenerTime = runTimeEnvironment.getStrategyListenerTime();
        } else {
            this.strategyListenerTime = 60000;
        }
    }

    private void watchStrategyListener() {
        long currentTime = System.currentTimeMillis();
        if (currentTime > (this.lastWatchTime + this.strategyListenerTime * 10)) {
            if (strategyListener != null) {
                if (!strategyListener.isAlive()) {
                    LogManager.getInstance().toAppLog(CDSEUtil.getCurrentTime() + "StrategyListener is not active.The system tries to reset it.");
                    strategyListener = StrategyListener.reset(this);
                }
            } else {
                strategyListener = StrategyListener.reset(this);

            }
            this.lastWatchTime = currentTime;
        }
    }

    public synchronized String[] getStrategyFiles() {
        return this.strategyFiles;
    }

    public synchronized void setStrategyFiles(String[] strategyFiles) {
        this.strategyFiles = strategyFiles;
    }
}
