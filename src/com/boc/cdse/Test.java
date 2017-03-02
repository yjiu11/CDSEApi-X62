package com.boc.cdse;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Test {
    public Test() {
    }
    public static void main(String []args){
    AppDataListener _instance = AppDataListener.getInstance()        ;
    _instance.run();

}

}
