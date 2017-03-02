package com.boc.cdse;

import java.io.*;
import java.util.*;

/**
 * <p>StrategyListener�߳�����CDSE��������ָ��Ŀ¼�²����ļ��Ľӿڣ����ڷ������µĲ����ļ��ϴ���Ὣ������ϵͳ��
 *</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   RunTimeEnvironment
 * @see   StrategyManager
 */

public class StrategyListener extends Thread {
    /** ��ʼ�����ļ������� */
    private String[] strategyf = null;
    /** �����ļ�·�� */
    private String path = null;
    /** ���Թ���ʵ������� */
    private StrategyManager strategyManager = null;
    /** ���Լ��������ʵ�� */
    static private StrategyListener _instance = null;
    /** ����ϵͳ����ʱ�� */
    private long strategyListenerTime = 0;
    private boolean runflag = true;

    /**
     * getInstance()��������һ��StrategyListenerʵ��
     *
     * @param StrategyManager   ���Թ���ʵ�������
     * @return StrategyListener ���Լ��������ʵ��
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
     * StrategyListener()��������һ����������ʵ��,��ȡ��ʼ�����ļ�
     *
     * @param StrategyManager   ���Թ���ʵ�������
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
     * initFiles()������ȡָ��Ŀ¼���ƶ���׺�������в����ļ�
     * @param dir   ָ��Ŀ¼
     * @return      �ļ������鼯
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
     * refreshFiles()������ȡָ��Ŀ¼���¿������в����ļ�
     * @param dir   ָ��Ŀ¼
     * @param initFiles  ��ʼ�����ļ���
     * @return      �²����ļ����б�
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
     * run()���������߳���ϵͳ�趨�����Ƿ����µĲ����ļ�����
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
                                      "δ�����µĲ����ļ�");
                    LogManager.getInstance().toAppLog(CDSEUtil.getCurrentTime() +
                        ":" +
                        "δ�����µĲ����ļ�");
                } else {
                    System.out.println("�ѷ��� " + count + " ���µĲ����ļ�");
                    LogManager.getInstance().toAppLog("�ѷ��� " + count +
                        " ���µĲ����ļ�");
                    for (int i = 0; i < newStrategyf.size(); i++) {
                        fileName = (String) newStrategyf.get(i);
                        //System.out.println(_instance.path + "/" + fileName);
                        result = strategyManager.findNewStrategyFile(_instance.
                            path +
                            "/" + fileName);
                        if (result) {
                            System.out.println(CDSEUtil.getCurrentTime() + ":" +
                                              fileName + " �����ļ��ѳɹ�����");
                            LogManager.getInstance().toAppLog(CDSEUtil.
                                getCurrentTime() + ":" +
                                fileName + " �����ļ��ѳɹ�����");
                        } else {
                            LogManager.getInstance().toAppLog(CDSEUtil.
                                getCurrentTime() + ":" +
                                fileName + " �����ļ��Ѿ����ڻ��ļ�����ʧ��");
                            //System.out.println(CDSEUtil.getCurrentTime() + ":" +
                            //                   fileName + " �����ļ��Ѿ����ڻ��ļ�����ʧ��");
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
