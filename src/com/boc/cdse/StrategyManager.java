package com.boc.cdse;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * <p>StrategyManager类管理策略库，负责在CDSE启动时加载策略库，在策略版本更新时
 * 及有新策略时更新策略库，并为授信决策及额度决策运行提供相应的策略。
 * </p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class StrategyManager {
    private java.util.ArrayList portfolioList = new java.util.ArrayList(); //产品线列表
    private RunTimeEnvironment runTimeEnvironment = null;
    private static StrategyManager _instance = null; //策略管理器类的实例
    private StrategyListener strategyListener = null;
    private long strategyListenerTime = 0;
    private long lastWatchTime = System.currentTimeMillis();
    /** 初始策略文件名数组 */
    private String[] strategyFiles = null;

    /**
     * getInstance()方法产生一个StrategyManager实例
     *
     * @return StrategyManager 策略管理器类的实例
     */

    static public StrategyManager getInstance() {
        if (null == _instance) {
            _instance = new StrategyManager();
        }
        return _instance;
    }

    /**
     * 策略管理器类的构造方法
     *  处理过程为：
     * 1.获取RunTimeEnvironment实例对象
     * 2.将所有生效的策略调入内存
     */
    protected StrategyManager() {
        runTimeEnvironment = RunTimeEnvironment.getInstance();
        if (runTimeEnvironment != null) {
            loadActiveStrategys();
            strategyListener = StrategyListener.getInstance(this);
        }
    }

    /**
     *  获取策略文件监听器
     *
     * @param  策略文件监听器
     */
    public StrategyListener getStrategyListener() {
        return this.strategyListener;
    }

    /**
     * loadActiveStrategys()方法负责将所有的生效的策略文件调入内存。
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
     * verifyStrategy方法校验strategyXML对象是否正确
     *
     * @param strategyXML 要校验的strategyXML对象
     * @return true       对象正确
     *         false      对象错误
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
                String errorString = " 政策项格式内容检查有误，请重新上传！";
                //  createErrorFile(errorString);
                // deleteFile(totalFileName);
                LogManager.getInstance().toAppLog(errorString);
                return false;
            }
        } catch (Exception e) {
            //errorCode = -1;
            String errorString = " 政策格式检查有误，请重新上传！";
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
                String errorString = " 策略内容检查有误，请重新上传！";
                //   createErrorFile(totalFileName + errorString);
                //   deleteFile(totalFileName);
                LogManager.getInstance().toAppLog(errorString);
                return false;
            }
        } catch (Exception ex1) {
            //errorCode = -1;
            String errorString = " 策略格式检查有误，请重新上传！";
            LogManager.getInstance().toAppLog(errorString);
            // createErrorFile(totalFileName + errorString);
            //  deleteFile(totalFileName);
            return false;
        }
        return true;
    }

    /**
     *findNewStrategyFile()方法在CDSE发现一个新的策略文件时被调用，负责读入策略文件、检验策略文件、更新策略库并更新配置文件中策略部分的信息。
     *处理过程为：
     * 调用loadNewStrategyFromFile（）方法读入策略文件、检验策略文件并更新策略库
     *
     * @param strategyFileName 策略文件名
     * @return true 策略文件加栽成功
     *         false 策略文件加栽失败
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
     *loadNewStrategyFromFile()方法在CDSE发现一个新的策略文件时被调用，负责读入策略文件、检验策略文件并更新策略库。
     *处理过程为：
     *1.根据策略文件名，读入策略数据
     *2.调用verifyStrategy()方法校验读入的策略数据
     *3.如果策略数据正确，调用findNewStrategy()方法更新策略库
     *
     * @param  strategyFileName 要加载的策略文件名
     * @return true             加载成功
     *         false            加载失败
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
     *getStrategyContent()方法在CDSE发现一个新的策略文件时被调用，负责读入策略文件。
     *处理过程为：
     *1.根据策略文件名，读入策略数据
     *
     * @param strategyFileName 策略文件名
     * @return 包含策略信息的DOM对象，如果读取失败，则返回空
     */
    private org.w3c.dom.Document getStrategyContent(String strategyFileName) {
        //----------------读取XML文件---------------------------------------------//
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
     * getActiveStrategy()方法根据指定的产品线、政策，获得一个当前生效的策略
	 *
     * @param portfolioId 产品线编号
     * @param policyId 政策编号
     * @return 生效的策略，如果没有，返回空
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
//			str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//			str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
			LogManager.getInstance().toCdsLog(str.toString()+";");
		}
		if (portfolioId != null && policyId != null) {
			portfolioBelongto = getPortfolioByID(portfolioId);
			if(portfolioBelongto==null){
//				str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//				str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
				LogManager.getInstance().toCdsLog(str.toString());
				LogManager.getInstance().toCdsLog("portfolioBelongto: is null from getActiveStrategy");
			}
			if (portfolioBelongto != null) {
				policyBelongto = portfolioBelongto.getPolicyById(policyId);
				if(policyBelongto==null){
//					str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//					str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
					LogManager.getInstance().toCdsLog(str.toString());
					LogManager.getInstance().toCdsLog("policyBelongto: is null from getActiveStrategy");
				}
				if (policyBelongto != null) {
					strategy = policyBelongto.getActiveStrategy();
					if(strategy==null){
//						str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//						str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
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
     *发现一个新策略文件，更新策略库
     *处理过程为：
     *1.从doc中读取产品线编号、政策编号、策略编号及策略版本信息
     *2.判断是否存在策略文件所属的产品线
     *   不存在： 分别构造一个新的策略文件所属的产品线对象、一个新策略文件所属的政策对象、新策略文件所属的对应策略对象添加策略库中并返回
     *   存在：进行下一步
     *3.如存在策略文件所属的产品线，判断是否存在策略文件所属的政策
     *   不存在：分别构造一个新策略文件所属的政策对象、新策略文件所属的对应策略对象添加策略库中并返回
     *   存在：进行下一步
     *4.如存在策略文件所属的政策，判断是否存在策略文件对应的策略
     *   不存在：分别构造一个新策略文件所属的对应策略对象添加策略库中并返回
     *   存在：进行下一步
     *5.如存在策略文件对应的策略，比较策略版本如策略文件版本比较新，更新相应的策略。
     *
     * @param doc 包含策略信息的DOM对象
     * @return true 策略更新成功
     *         false 策略更新失败
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
     *  更新配置文件中的策略版本部分
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
     * 更新策略库中相应的产品线
     *
     * @param portfolio 要更新的产品线
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
     * 通过指定的产品线编号从策略库中获得产品线
     *
     * @param id 产品线编号
     * @return 产品线
     */
    private synchronized Portfolio getPortfolioByID(String id) {
        Portfolio portfolio = null;
        CDSLog clog = new CDSLog();
        StringBuffer str = new StringBuffer();
        if (id == null) {
        	LogManager.getInstance().toCdsLog("id is null from getPortfolioByID");
        } else {
        	if(portfolioList.size()<0){
//        		str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//    			str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
    			str.append("portfolioList.size<0 from getPortfolioByID");
        		LogManager.getInstance().toCdsLog(str.toString());
        	}
        	for (int i = 0; i < portfolioList.size(); i++) {
                Portfolio currentPortfolio = (Portfolio) portfolioList.get(i);
                if(currentPortfolio==null){
//                	str.append("CDS接收信息：").append(clog.getReceivingCDSXML()).append(";");
//        			str.append("CDS返回信息：").append(clog.getReturnCDSXML()).append(";");
                	str.append("currentPortfolio：is null from getPortfolioByID");
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
