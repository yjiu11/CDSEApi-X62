package com.boc.cdse;

import java.util.ArrayList;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class XMLStrategyItem {
    public String NodeId ="";
    public String ParentId  ="";
    public String VarNo  ="";
    public String CmpOpe  ="";
    public String CmpVal ="";
    public String ActionId  ="";
    public String ReasonCode  ="";
    public String NodeLevel  ="";
    public String ItemDesc  ="";
    public String ReWrite  ="";
    public String ReasonDsc  ="";

    public ArrayList childItems =new ArrayList();
    public ArrayList parameters = new ArrayList();

    public XMLStrategyItem() {
    }

}
