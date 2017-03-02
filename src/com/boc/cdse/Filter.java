package com.boc.cdse;

import java.io.*;

/**
 * <p>Filter类实现了FilenameFilter接口类，用于指定文件后缀。
 *</p>
 *
 * <p>Copyright: 版权 (c) 2002</p>
 * <p>Company: 首航财务管理有限公司</p>
 * @author: CDSE项目组
 * @version 1.0
 */

public class Filter
    implements FilenameFilter {
    /**文件后缀*/
    String extent;

    /**
     * Filter 指定文件后缀
     *
     * @param extent   文件后缀
     */

    public Filter(String extent) {
        this.extent = extent.trim().toLowerCase();
    }

    /**
     * accept 实现接口方法
     *
     * @param dir   文件目录
     * @param name   文件名
     * @return boolean 是否接受指定文件后缀
     */

    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith("." + extent);
    }

}
