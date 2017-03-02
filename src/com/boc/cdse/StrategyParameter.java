package com.boc.cdse;
import java.io.Serializable;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class StrategyParameter implements Serializable{
    public StrategyParameter() {
    }

    private String name ="";
    private String desc ="";
    private String spec="";
    private String value="";
    private String type="";

    public void setName(String name) {
	this.name = name;
    }

    public String getName() {
	return this.name;
    }

    public void setDesc(String desc) {
	this.desc = desc;
    }

    public String getDesc() {
	return this.desc;
    }

    public void setSpec(String spec) {
	this.spec = spec;
    }

    public String getSpec() {
	return this.spec;
    }

    public void setValue(String value) {
	this.value = value;
    }

    public String getValue() {
	return this.value;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getType() {
	return this.type;
    }

}