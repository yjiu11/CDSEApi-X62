package com.boc.cdse;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import java.util.*;

/**
 * <p>解析配置文件，取得系统运行所需配置信息。提供getXXX()方法供其它类使用。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   EnvironmentConfigureFileHandler
 *
 */
public class RunTimeEnvironment {

	/** 类静态变量 */
	private static RunTimeEnvironment runTimeEnvironment = null;
	/** 用于解析配置文件，取得dom树 */
	private EnvironmentConfigureFileHandler enConHandle = null;
	/** 应用服务器用户名 */
	private String webUser = null;
	/** 应用服务器用户密码 */
	private String webPassword = null;
	/** 上下文URL */
	private String jndiDes = null;
	/** 主路径 */
	private String domainDir = null;
	/** 消息连接工厂 */
	private String queueConnectionFactory = null;
	/** 带URl的取得消息队列名 */
	private String recvQueueName = null;
	/** 带URl的发送消息的队列名 */
	private String sendQueueName = null;
	/** 取得消息队列名 */
	private String recvQueueNameOnly = null;
	/** 发送消息队列名 */
	private String sendQueueNameOnly = null;
	/** 消息驱动上下文工厂 */
	private String contextFactory = null;
	/** 日志文件路径 */
	private String logDir = null;
	/** 跟踪报告文件路径 */
	private String reportDir = null;
	/** 日志文件大小 */
	private String logSize = null;
	/** 日志文件当前点 */
	private String currentPoint = null;
	/** java消息URL */
	private String jmsUrl = null;
	/** 包含所有卡类型数组 */
	public String[] cardType = null;
	/** 包含所有卡的log文件名 */
	public String[] logFile = null;
	/** 根元素 */
	private Element basic = null;
	/** RunTimeEnvironment元素 */
	private Element environment = null;
	/** LogDefinition元素 */
	private Element logDefinition = null;
	/** Report元素 */
	private Element report = null;
	/** LogFileType元素 */
	private Element logFileType = null;
	/** 错误编码号 */
	private int errorCode = 0;
	/** 错误信息 */
	private String errorString = null;
	/** 日志信息 */
	private String logString = "";
	/** 配置文件读取结果标志 */
	private static boolean readSuccess = true;
	/** 配置文件dom树 */
	private Document configureDoc = null;
	private ArrayList activeStrategyList = new ArrayList();
	/** 监听时间间隔 */
	private long strategyListenerTime = 1000;

	/**
	 * 设置当前有效策略
	 * @param activeStrategyList   当前有效策略
	 */
	public void setActiveStrategyList(ArrayList activeStrategyList) {
		this.activeStrategyList = activeStrategyList;
	}

	/**
	 * 返回当前有效策略.
	 */
	public ArrayList getActiveStrategyList() {
		return this.activeStrategyList;
	}

	/**
	 * 设置监听时间间隔
	 * @param strategyListenerTime   监听时间间隔
	 */
	public void setStrategyListenerTime(long strategyListenerTime) {
		this.strategyListenerTime = strategyListenerTime;
	}

	/**
	 * 返回监听时间间隔.
	 */
	public long getStrategyListenerTime() {
		return this.strategyListenerTime;
	}

	/**
	 * 设置java消息URL
	 * @param jmsUrl   java消息URL
	 */
	public void setJmsUrl(String jmsUrl) {
		this.jmsUrl = jmsUrl;
	}

	/**
	 * 返回java消息URL.
	 */
	public String getJmsUrl() {
		return this.jmsUrl;
	}

	/**
	 * 设置应用服务器用户名
	 * @param webUser   应用服务器用户名
	 */
	public void setWebUser(String webUser) {
		this.webUser = webUser;
	}

	/**
	 * 返回应用服务器用户名.
	 */
	public String getWebUser() {
		return this.webUser;
	}

	/**
	 * 设置应用服务器用户密码
	 * @param webPassword   应用服务器用户密码
	 */
	public void setWebPassword(String webPassword) {
		this.webPassword = webPassword;
	}

	/**
	 * 返回应用服务器用户密码.
	 */
	public String getWebPassword() {
		return this.webPassword;
	}

	/**
	 * 设置上下文URL
	 * @param jndiDes   上下文URL
	 */
	public void setJndiDes(String jndiDes) {
		this.jndiDes = jndiDes;
	}

	/**
	 * 返回上下文URL.
	 */
	public String getJndiDes() {
		return this.jndiDes;
	}

	/**
	 * 设置主路径
	 * @param domainDir   主路径
	 */
	public void setDomainDir(String domainDir) {
		this.domainDir = domainDir;
	}

	/**
	 * 返回主路径.
	 */
	public String getDomainDir() {
		return this.domainDir;
	}

	/**
	 * 设置消息连接工厂
	 * @param queueConnectionFactory   消息连接工厂
	 */
	public void setQueueConnectionFactory(String queueConnectionFactory) {
		this.queueConnectionFactory = queueConnectionFactory;
	}

	/**
	 * 返回消息连接工厂.
	 */
	public String getQueueConnectionFactory() {
		return this.queueConnectionFactory;
	}

	/**
	 * 设置带URl的取得消息队列名
	 * @param recvQueueName   带URl的取得消息队列名
	 */
	public void setRecvQueueName(String recvQueueName) {
		this.recvQueueName = recvQueueName;
	}

	/**
	 * 返回带URl的取得消息队列名.
	 */
	public String getRecvQueueName() {
		return this.recvQueueName;
	}

	/**
	 * 设置带URl的发送消息的队列名
	 * @param sendQueueName   带URl的发送消息的队列名
	 */
	public void setSendQueueName(String sendQueueName) {
		this.sendQueueName = sendQueueName;
	}

	/**
	 * 返回带URl的发送消息的队列名.
	 */
	public String getSendQueueName() {
		return this.sendQueueName;
	}

	/**
	 * 设置消息驱动上下文工厂
	 * @param contextFactory   消息驱动上下文工厂
	 */
	public void setContextFactory(String contextFactory) {
		this.contextFactory = contextFactory;
	}

	/**
	 * 返回消息驱动上下文工厂.
	 */
	public String getContextFactory() {
		return this.contextFactory;
	}

	/**
	 * 设置日志文件路径
	 * @param logDir   日志文件路径
	 */
	public void setReportDir(String reportDir) {
		this.reportDir = reportDir;
	}

	/**
	 * 返回日志文件路径.
	 */
	public String getReportDir() {
		return this.reportDir;
	}

	/**
	 * 设置日志文件路径
	 * @param logDir   日志文件路径
	 */
	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	/**
	 * 返回日志文件路径.
	 */
	public String getLogDir() {
		return this.logDir;
	}

	/**
	 * 设置日志文件大小
	 * @param logSize   日志文件大小
	 */
	public void setLogSize(String logSize) {
		this.logSize = logSize;
	}

	/**
	 * 日志文件大小.
	 */
	public String getLogSize() {
		return this.logSize;
	}

	/**
	 * 设置日志文件当前点
	 * @param currentPoint   日志文件当前点
	 */
	public void setCurrentPoint(String currentPoint) {
		this.currentPoint = currentPoint;
	}

	/**
	 * 返回日志文件当前点.
	 */
	public String getCurrentPoint() {
		return this.currentPoint;
	}

	/**
	 * 设置包含所有卡类型数组
	 * @param cardType   包含所有卡类型数组
	 */
	public void setCardType(String[] cardType) {
		this.cardType = cardType;
	}

	/**
	 * 返回包含所有卡类型数组.
	 */
	public String[] getCardType() {
		return this.cardType;
	}

	/**
	 * 设置包含所有卡的log文件名
	 * @param logFile   包含所有卡的log文件名
	 */
	public void setLogFile(String[] logFile) {
		this.logFile = logFile;
	}

	/**
	 * 返回包含所有卡的log文件名.
	 */
	public String[] getLogFile() {
		return this.logFile;
	}

	/**
	 * 设置卡类型
	 * @param cardType   卡类型
	 */
	public void setCardTypeBySuffix(String cardType, int suffix) {
		this.cardType[suffix] = cardType;
	}

	/**
	 * 返回卡类型.
	 */
	public String getCardTypeBySuffix(int suffix) {
		return this.cardType[suffix];
	}

	/**
	 * 设置LogDefinition元素
	 * @param logDefinition   LogDefinition元素
	 */
	public void setLogDefinition(Element logDefinition) {
		this.logDefinition = logDefinition;
	}

	/**
	 * 返回LogDefinition元素.
	 */
	public Element getLogDefinition() {
		return this.logDefinition;
	}

	/**
	 * 设置Report元素
	 * @param Report   Report元素
	 */
	public void setReport(Element Report) {
		this.report = Report;
	}

	/**
	 * 返回LogDefinition元素.
	 */
	public Element getReport() {
		return this.report;
	}

	/**
	 * 设置根元素
	 * @param basic   根元素
	 */
	public void setBasic(Element basic) {
		this.basic = basic;
	}

	/**
	 * 返回根元素.
	 */
	public Element getBasic() {
		return this.basic;
	}

	/**
	 * 设置RunTimeEnvironment元素
	 * @param basic   RunTimeEnvironment元素
	 */
	public void setEnvironment(Element environment) {
		this.environment = environment;
	}

	/**
	 * 返回RunTimeEnvironment元素.
	 */
	public Element getEnvironment() {
		return this.environment;
	}

	/**
	 * 设置LogFileType元素
	 * @param basic   LogFileType元素
	 */
	public void setLogFileType(Element logFileType) {
		this.logFileType = logFileType;
	}

	/**
	 * 返回LogFileType元素.
	 */
	public Element getLogFileType() {
		return this.logFileType;
	}

	/**
	 * 设置错误编码
	 * @param errorCode   错误编码
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * 返回错误编码.
	 */
	public int getErrorCode() {
		return this.errorCode;
	}

	/**
	 * 设置错误信息
	 * @param errorString   错误信息
	 */
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}

	/**
	 * 返回错误信息.
	 */
	public String getErrorString() {
		return this.errorString;
	}

	/**
	 * 设置日志信息
	 * @param logString   日志信息
	 */
	public void setLogString(String logString) {
		this.logString = logString;
	}

	/**
	 * 返回日志信息.
	 */
	public String getLogString() {
		return this.logString;
	}

	/**
	 * 解析配置文件取得dom树，从dom树中取得各配置信息赋值给对应属性。
	 *
	 * @throws CDSEException 解析配置文件或从dom取得配置信息异常时抛出
	 */
	private RunTimeEnvironment() throws CDSEException {
		try {
			if (enConHandle == null) {
				enConHandle = EnvironmentConfigureFileHandler.getInstance();
				logString = enConHandle.getLogString();
				enConHandle.setLogString("");
			}
			logString = CDSEUtil.getCurrentTime() +
				"\nStarting to get the Element from configure file \n";

			//取得dom树并取得根元素
			basic = enConHandle.getConfigureDoc().getDocumentElement();
		} catch (Exception e) {
			errorCode = 100;
			readSuccess = false;
			System.out.println("Get the Element from configure file error!");
			throw new CDSEException(errorCode);
		}
		//从dom树取得配置信息值
		if (loadConfigureFile() < 0) {
			logString = logString +
				"Create the runtime environment Error! \n" +
				errorString;
			errorCode = 101;
			readSuccess = false;
			System.out.println(errorString);
			throw new CDSEException(errorCode);
		}
		errorString = null;
		readSuccess = true;
	}

	/**
	 * 取得RunTimeEnvironment实例。
	 *
	 * @return RunTimeEnvironment实例 异常时返回null
	 */
	public static RunTimeEnvironment getInstance() {
		if (runTimeEnvironment == null && readSuccess) {
			try {
				runTimeEnvironment = new RunTimeEnvironment();
			} catch (Exception e) {
				return null;
			}
		}
		return runTimeEnvironment;
	}

	/**
	 * 根据授信决策后的数据，生成新的配置文件
	 *
	 * @return 文件生成结果 true:生成成功
	 *                      false:失败
	 */
	public boolean saveConfigureFile() {

		boolean success = true;

		//为解析XML作准备，创建DocumentBuilderFactory实例,指定DocumentBuilder
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			success = false;
			return success;
		}

		Document doc = null;
		doc = db.newDocument();

		//下面是建立XML文档内容的过程，先建立根元素"Basic"
		Element basic = doc.createElement("Basic");
		//根元素添加上文档
		doc.appendChild(basic);

		//建立RunTimeEnvironment元素，添加到data
		Element runTimeEnvironment = doc.createElement("RunTimeEnvironment");
		runTimeEnvironment.setAttribute("DomainDir", this.domainDir);
		runTimeEnvironment.setAttribute("JNDIDES", this.jndiDes);
		runTimeEnvironment.setAttribute("QueueConnectionFactory",
										this.queueConnectionFactory);
		runTimeEnvironment.setAttribute("RecvQueueName", this.recvQueueNameOnly);
		runTimeEnvironment.setAttribute("SendQueueName", this.sendQueueNameOnly);
		runTimeEnvironment.setAttribute("WebPassword", this.webPassword);
		runTimeEnvironment.setAttribute("WebServerContextFactory",
										this.contextFactory);
		runTimeEnvironment.setAttribute("WebUser", this.webUser);
		runTimeEnvironment.setAttribute("JmsContextUrl", this.jmsUrl);
		runTimeEnvironment.setAttribute("StrategyListenerTime", String.valueOf(this.strategyListenerTime));

		basic.appendChild(runTimeEnvironment);

		//建立LogDefinition元素，添加到runTimeEnvironment
		Element logDefinition = doc.createElement("LogDefinition");
		logDefinition.setAttribute("CurrentPoint", this.currentPoint);
		logDefinition.setAttribute("LogDir", this.logDir);
		logDefinition.setAttribute("ReportDir", this.reportDir);
		logDefinition.setAttribute("Size", this.logSize);
		runTimeEnvironment.appendChild(logDefinition);
		//
		Element report = doc.createElement("Report");
		report.setAttribute("Dir", this.reportDir);
		runTimeEnvironment.appendChild(report);

		//建立LogFileType元素，添加到LogDefinition
		Element logFileType = doc.createElement("LogFileType");
		logDefinition.appendChild(logFileType);

		try {
			//建立type元素，添加到CreditCardType元素
			// System.out.println("cardType.length  ==========" + cardType.length);
			for (int i = 0; i < this.cardType.length; i++) {
				Element type = doc.createElement("type");
				type.setAttribute("Name", cardType[i]);
				type.setAttribute("fileName", logFile[i]);
				logFileType.appendChild(type);
			}
		} catch (Exception e) {
			success = false;
			return success;
		}
		enConHandle.setConfigureDoc(doc);
		int refresh = enConHandle.refreshConfigureFile();
		if (refresh < 0) {
			success = false;
		}

		return success;
	}

	/**
	 * 根据读取配置文件的Dom，初始化环境变量
	 *
	 * @return 初始化结果 1:成功
	 *                    -1:失败
	 */
	private int loadConfigureFile() {
//System.out.println("step 1 ============");
		int loadResult = 1;
		try {
			environment = (Element) basic.getElementsByTagName(
				"RunTimeEnvironment").item(0);
			logDefinition = (Element) environment.getElementsByTagName(
				"LogDefinition").item(0);
			report = (Element) environment.getElementsByTagName(
				"Report").item(0);
			logDir = logDefinition.getAttribute("LogDir");
			reportDir = report.getAttribute("Dir");
			logSize = logDefinition.getAttribute("Size");
			currentPoint = logDefinition.getAttribute("CurrentPoint");
			logFileType = (Element) logDefinition.getElementsByTagName(
				"LogFileType").item(0);
//System.out.println("step 2 ============");
		} catch (Exception e) {
			errorCode = 9001;
			errorString =
				"Read the RunTimeEnvironment and logDefinition Element from configure error!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		}
		//Get the Environment Information
		//webServer = environment.getAttribute("WebServer");
		try {
			strategyListenerTime = Long.parseLong(environment.getAttribute(
				"StrategyListenerTime"));
			if (strategyListenerTime < 1000) {
				errorCode = 9002;
				errorString = "StrategyListenerTime小于1000!";
				System.out.println(errorString);
				loadResult = -1;
				return loadResult;
			}
		} catch (NumberFormatException e) {
			errorCode = 9003;
			errorString = "Read StrategyListenerTime error!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		}

		jmsUrl = environment.getAttribute("JmsContextUrl");
		webUser = environment.getAttribute("WebUser");
		webPassword = environment.getAttribute("WebPassword");
		jndiDes = environment.getAttribute("JNDIDES");
		domainDir = environment.getAttribute("DomainDir");
		recvQueueNameOnly = environment.getAttribute(
			"RecvQueueName");
		sendQueueNameOnly = environment.getAttribute("SendQueueName");
		queueConnectionFactory
			= environment.getAttribute("QueueConnectionFactory");
		contextFactory = environment.getAttribute("WebServerContextFactory");

		//检查各配置值是否为空，为空则抛出例外
		if (jmsUrl.length() == 0) {
			errorCode = 9004;
			errorString = "JmsContextUrl element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
			// Huang Shaicheng 2004-2-14
			/*}else if(jndiDes.length() == 0){
				errorCode = 9005;
				errorString = "JNDIDES element value is empty!";
				System.out.println(errorString);
				loadResult = -1;
				return loadResult; */
		} else if (domainDir.length() == 0) {
			errorCode = 9006;
			errorString = "DomainDir element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		} else if (recvQueueNameOnly.length() == 0) {
			errorCode = 9007;
			errorString = "RecvQueueName element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		} else if (sendQueueNameOnly.length() == 0) {
			errorCode = 9008;
			errorString = "SendQueueName element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		} else if (queueConnectionFactory.length() == 0) {
			errorCode = 9009;
			errorString = "QueueConnectionFactory element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		} else if (contextFactory.length() == 0) {
			errorCode = 9010;
			errorString = "WebServerContextFactory element value is empty!";
			System.out.println(errorString);
			loadResult = -1;
			return loadResult;
		}
		//recvQueueName = jndiDes + "Queue/" + recvQueueNameOnly;
		recvQueueName = recvQueueNameOnly;
		//sendQueueName = jndiDes + "Queue/" + sendQueueNameOnly;
		sendQueueName = sendQueueNameOnly;
//System.out.println("step 3 ============");
		logString = logString +
			"The Configure Information read from file is :\n";
		logString = logString + "WebUser = " + webUser + "\n";
		logString = logString + "WebPassword = " + webPassword + "\n";
		logString = logString + "JndiDes = " + jndiDes + "\n";
		logString = logString + "DomainDir = " + domainDir + "\n";
		logString = logString + "QueueConnectionFactory = " +
			queueConnectionFactory + "\n";
		logString = logString + "RecvQueueName = " + recvQueueName + "\n";
		logString = logString + "SendQueueName = " + sendQueueName + "\n";
		logString = logString + "WebServerContextFactory = " + contextFactory +
			"\n";

		try {
			NodeList cardTypes = logFileType.getElementsByTagName("type");
			Element typeTag = null;
			cardType = new String[cardTypes.getLength()];
			logFile = new String[cardTypes.getLength()];
			//初始化卡信息
			for (int i = 0; i < cardTypes.getLength(); i++) {
				typeTag = (Element) cardTypes.item(i);
				String cardName = typeTag.getAttribute("Name");
				logFile[i] = typeTag.getAttribute("fileName");
				cardType[i] = cardName;
//System.out.println("step 6 ============");
			}
		} catch (Exception e) {
			errorCode = 9011;
			errorString = "Read the type element from configure Error!";
			loadResult = -1;
			return loadResult;
		}
		enConHandle.closeFileHandle();

		/*
		   System.out.println("webServer = " +webServer );
		   System.out.println("webUser = " +webUser );
		   System.out.println("webPassword = " +webPassword );
		   System.out.println("jndiDes = " +jndiDes );
			System.out.println("domainDir = " +domainDir );
			 System.out.println("queueConnectionFactory = " +queueConnectionFactory );
			System.out.println("recvQueueName = " +recvQueueName );
			System.out.println("sendQueueName = " +sendQueueName );
			System.out.println("logQueueName = " +logQueueName );
			System.out.println("contextFactory = " +contextFactory );
			System.out.println("serverPort = " +serverPort );
			System.out.println("subJndiDir = " +subJndiDir );
			System.out.println("subPhyDir = " +subPhyDir );
			System.out.println("logDir = " +logDir );
			System.out.println("logSize = " +logSize );
			System.out.println("currentPoint = " +currentPoint );
			System.out.println("strategyQueueName = " +strategyQueueName );
			System.out.println("personalCardLogFile = " +personalCardLogFile );
			System.out.println("companyCardLogFile = " +companyCardLogFile );
		 */
		return loadResult;
	}

	/*
	 public static void main(String args[])throws CDSEException{
		   RunTimeEnvironment handler = RunTimeEnvironment.getInstance();
		   handler.saveConfigureFile();
	 }
	 */
}
