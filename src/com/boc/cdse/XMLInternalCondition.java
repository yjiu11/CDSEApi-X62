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

public class XMLInternalCondition {
    public String VarName ="";
   public String  VType="";
   public String  CompLog ="";
   public String  CompOper ="";
   public String  CompVal ="";
   public String  CompAttr="";

   public ArrayList childConditions = new ArrayList();

    public XMLInternalCondition() {
    }

}
