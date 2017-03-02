package com.boc.cdse;



public class CDSThread extends Thread {
	
	private static CDSThread _instance = null;	
	
	public static  CDSThread getInstance(){
		
        if (null == _instance) {


          _instance = new CDSThread();
          _instance.start();
            

        }
        return _instance;
	}
	public void run(){
		
	
		
	
	}
}
