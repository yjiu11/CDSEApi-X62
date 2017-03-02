package com.boc.cdse;

public class VariableBean {

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a new VariableBean with the specified values.
     *
     * @param fldName ��������
     * @param value The ����ֵ
     * @param fldType ��������
     * @param fldName ������Դ
     * @param fldName ����˵��
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
     * The fldName ��������.
     */
    private String fldName = "";
    /**
     * The fldType ��������.
     */

    private String fldType = "";
    /**
     * The fldSrc ������Դ.
     */

    private String fldSrc = "";
    /**
     * The value ����ֵ.
     */

    private String value = "";
    /**
     * The fldDsc ����˵��.
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
