package com.boc.cdse;

/**
 * <p>CDSEException�ฺ��CDSE�����⴦��</p>
 *
 * <p>Copyright: ��Ȩ (c) 2002</p>
 * <p>Company: �׺�����������޹�˾</p>
 * @author: CDSE��Ŀ��
 * @version 1.0
 * @see   Exception
 */

public class CDSEException
    extends Exception {

    private int errorCode;  //�������
    /**
     * ���ش����
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * ���ô����
     * @param errorCode   The new �����
     */
    public void setErrorCode(int newErrorCode) {
        this.errorCode = newErrorCode;
    }

    /**
     * ͨ�������Ź���һ��CDSE������
     *
     * @param   errorCode   ������
     */

    public CDSEException(int newErrorCode) {
        this.errorCode = newErrorCode;
        System.out.println(this.errorCode);
    }

    /**
     * ͨ��������Ϣ����һ��CDSE������
     *
     * @param   error   ������Ϣ
     */

    public CDSEException(String error) {
        super(error);
    }
}
