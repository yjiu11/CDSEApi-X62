package com.boc.cdse;

/**
 * <p>CDSEException类负责CDSE的例外处理。</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 * @see   Exception
 */

public class CDSEException
    extends Exception {

    private int errorCode;  //错误编码
    /**
     * 返回错误号
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * 设置错误号
     * @param errorCode   The new 错误号
     */
    public void setErrorCode(int newErrorCode) {
        this.errorCode = newErrorCode;
    }

    /**
     * 通过错误编号构造一个CDSE的例外
     *
     * @param   errorCode   错误编号
     */

    public CDSEException(int newErrorCode) {
        this.errorCode = newErrorCode;
        System.out.println(this.errorCode);
    }

    /**
     * 通过错误信息构造一个CDSE的例外
     *
     * @param   error   错误信息
     */

    public CDSEException(String error) {
        super(error);
    }
}
