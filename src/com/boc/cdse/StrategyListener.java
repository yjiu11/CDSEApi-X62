package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>StrategyListener线程类是CDSE用来监听指定目录下策略文件的接口，它在发现有新的策略文件上传后会将其载入系统。
 *</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   RunTimeEnvironment
 * @see   StrategyManager
 */

public class StrategyListener extends Thread {
    /** 初始策略文件名数组 */
    private String[] strategyf = null;
    /** 策略文件路径 */
    private String path = null;
    /** 策略管理实体类对象 */
    private StrategyManager strategyManager = null;
    /** 策略监听器类的实例 */
    static private StrategyListener _instance = null;
    /** 设置系统监听时间 */
    private long strategyListenerTime = 0;
    private boolean runflag = true;

    /**
     * getInstance()方法产生一个StrategyListener实例
     *
     * @param StrategyManager   策略管理实体类对象
     * @return StrategyListener 策略监听器类的实例
     */
    static public StrategyListener getInstance(StrategyManager sm) {
        if (null == _instance) {

            RunTimeEnvironment en = RunTimeEnvironment.getInstance();
            if (en != null) {
                //deactiveThreads();
                _instance = new StrategyListener(sm);
                _instance.start();
            }

        }
        return _instance;
    }

    /**
     * StrategyListener()方法产生一个环境变量实例,读取初始策略文件
     *
     * @param StrategyManager   策略管理实体类对象
     * @return void
     */

    protected StrategyListener(StrategyManager sm) {
        RunTimeEnvironment en = RunTimeEnvironment.getInstance();
        path = en.getDomainDir();
        strategyListenerTime = en.getStrategyListenerTime();
        strategyf = initFiles(path);
        strategyManager = sm;
    }

    /**
     * initFiles()方法获取指定目录下制定后缀名的所有策略文件
     * @param dir   指定目录
     * @return      文件名数组集
     */

    private String[] initFiles(String dir) {
        String initFiles[] = null;
        File dirFile = null;
        try {
            dirFile = new File(dir);
        } catch (Exception ex) {
            System.out.println("Error in getFile dirFile" + ex.getMessage());
        }
        if (dirFile.isDirectory()) {
            Filter filter = new Filter("xml");
            initFiles = dirFile.list(filter);
        }

        return initFiles;
    }

    /**
     * refreshFiles()方法获取指定目录下新考入所有策略文件
     * @param dir   指定目录
     * @param initFiles  初始策略文件集
     * @return      新策略文件名列表集
     */
    private ArrayList refreshFiles(String dir, String[] initFiles) {
        String tmpFiles[] = null;
        ArrayList refresh = new ArrayList();
        tmpFiles = CDSEUtil.initFiles(dir);

        int i = 0;
        int j = 0;
        int c = 0;
        int ii = 0;
        int jj = 0;
        if (tmpFiles == null) {
            return refresh;
        }
        if (initFiles == null) {
            for (ii = 0; ii < tmpFiles.length; ii++) {
                refresh.add(tmpFiles[ii]);
            }
            this.strategyf = tmpFiles;
            this.strategyManager.setStrategyFiles(this.strategyf);
            System.out.println("refresh:"+refresh);
            return refresh;
        } else {
            int count1 = initFiles.length;
            int count2 = tmpFiles.length;
            if (count1 <= count2) {
                for (i = 0; i < count2; i++) {
                    c = 0;
                    for (j = 0; j < count1; j++) {
                        if (tmpFiles[i].equalsIgnoreCase(initFiles[j])) {
                            c = 1;
                            break;
                        }
                    }
                    if (c == 0) {
                        refresh.add(tmpFiles[i]);
                    }
                }
            }

            this.strategyf = tmpFiles;
            this.strategyManager.setStrategyFiles(this.strategyf);
            return refresh;
        }
    }

    /**
     * run()方法启动线程在系统设定监听是否有新的策略文件存在
     *
     * @return void
     */
    public void run() {
        String fileName = null;
        boolean result;
        while (getrunflag()) {
            try {
                ArrayList newStrategyf = _instance.refreshFiles(_instance.path,
                    _instance.strategyf);
                int count = newStrategyf.size();
                if (count <= 0) {
                    System.out.println(CDSEUtil.getCurrentTime() + ":" +
                                      "未发现新的策略文件");
                    LogManager.getInstance().toAppLog(CDSEUtil.getCurrentTime() +
                        ":" +
                        "未发现新的策略文件");
                } else {
                    System.out.println("已发现 " + count + " 个新的策略文件");
                    LogManager.getInstance().toAppLog("已发现 " + count +
                        " 个新的策略文件");
                    for (int i = 0; i < newStrategyf.size(); i++) {
                        fileName = (String) newStrategyf.get(i);
                        //System.out.println(_instance.path + "/" + fileName);
                        result = strategyManager.findNewStrategyFile(_instance.
                            path +
                            "/" + fileName);
                        if (result) {
                            System.out.println(CDSEUtil.getCurrentTime() + ":" +
                                              fileName + " 策略文件已成功导入");
                            LogManager.getInstance().toAppLog(CDSEUtil.
                                getCurrentTime() + ":" +
                                fileName + " 策略文件已成功导入");
                        } else {
                            LogManager.getInstance().toAppLog(CDSEUtil.
                                getCurrentTime() + ":" +
                                fileName + " 策略文件已经存在或文件导入失败");
                            //System.out.println(CDSEUtil.getCurrentTime() + ":" +
                            //                   fileName + " 策略文件已经存在或文件导入失败");
                        }
                    }
                }
                this.currentThread().sleep(strategyListenerTime);
            } catch (InterruptedException ex2) {
                ex2.printStackTrace();
            }
        }
    }

    public static void deactiveThreads() {
        Thread[] threads = null;
        System.out.println(
            "begin to try to stop the threads belongs to StrategyListener.");
        try {

            if (Thread.activeCount() != 0) {
                threads = new Thread[Thread.activeCount()];
            }
            int n = Thread.enumerate(threads);
            for (int i = 0; i < n; i++) {
                Thread currentThread = threads[i];
                System.out.println("---StrategyListener test() thread :" +
                                   currentThread.getClass().getName());
                if (currentThread.getClass().getName().equals(
                    "com.boc.cdse.StrategyListener")) {
                    // if (currentThread instanceof com.boc.cdse.StrategyListener) {
                    System.out.println("currentThread=" +
                                       currentThread.getClass().getName() +
                                       " stopped");
                    ( (com.boc.cdse.StrategyListener) currentThread).stopthread();
                    currentThread.interrupt();
                    currentThread = null;
                } else {
                    System.out.println("currentThread=" +
                                       currentThread.getClass().getName() +
                                       " is not strategylistener.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stopthread(){
        System.out.println(this +" will stop!");
        runflag = false;
    }

    public synchronized boolean getrunflag(){
        return runflag;
    }

    public static StrategyListener reset(StrategyManager strategyManager) {
        System.out.println(CDSEUtil.getCurrentTime() + ":StrategyListener begin to reset.");
        _instance = null;
        StrategyListener strategyListener = getInstance(strategyManager);
        strategyListener.strategyf = strategyManager.getStrategyFiles();
        System.out.println(CDSEUtil.getCurrentTime() + ":StrategyListener has reset. Strategy files : ");
        for(int i=0;i<strategyListener.strategyf.length;i++){
             System.out.println(strategyListener.strategyf[i]);
        }
        return _instance;
    }

}
