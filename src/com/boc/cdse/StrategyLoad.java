package com.boc.cdse;



public class StrategyLoad {
	
	private static StrategyLoad _instance = null;
	

	
	public static StrategyLoad getInstance() {
		if (null == _instance) {
			_instance = new StrategyLoad();
		}

		return _instance;
	}
	
//	重新加载策略
    public boolean loadActiveStrategys() {	

    boolean result = false ;
    
    try{	
        StrategyManager.getInstance().loadActiveStrategys();       
        result = true ;
       }catch(Exception e){
    	   e.printStackTrace();
       }
        return result  ;
    }
    


}
