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

public class XMLPolicyCondition {
  public XMLPolicyCondition() {
  }

  public String VarNo = "";
     public String  VType = "";
     public String  LogOpe = "";
     public String  CmpOpe = "";
     public String  CmpVal = "";
     public String  CompAttr = "";
     public String  Lnum = "";
     public String  Rnum= "";

     public ArrayList childConditions = new ArrayList();

}
