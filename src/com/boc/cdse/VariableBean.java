package com.boc.cdse;

public class VariableBean {

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new VariableBean with the specified values.
     *
     * @param fldName 变量名称
     * @param value The 变量值
     * @param fldType 变量类型
     * @param fldName 变量来源
     * @param fldName 变量说明
     */
    public VariableBean(String fldName, String value, String fldType,
                        String fldSrc, String fldDsc) {
        this.fldName = fldName;
        this.fldType = fldType;
        this.fldSrc = fldSrc;
        this.value = value;
        this.fldDsc = fldDsc;
    }

    // ------------------------------------------------------------- Properties

    /**
     * The fldName 变量名称.
     */
    private String fldName = "";
    /**
     * The fldType 变量类型.
     */

    private String fldType = "";
    /**
     * The fldSrc 变量来源.
     */

    private String fldSrc = "";
    /**
     * The value 变量值.
     */

    private String value = "";
    /**
     * The fldDsc 变量说明.
     */

    private String fldDsc = "";

    public String getFldName() {
        return (this.fldName);
    }

    public String getFldType() {
        return (this.fldType);
    }

    public String getFldSrc() {
        return (this.fldSrc);
    }

    public String getValue() {
        return (this.value);
    }

    public String getFldDsc() {
        return (this.fldDsc);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Return a string representation of this object.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("LabelValueBean[");
        sb.append(this.fldName);
        sb.append(", ");
        sb.append(this.value);
        sb.append("]");
        return (sb.toString());
    }

}
