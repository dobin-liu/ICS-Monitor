/*
 * @(#)AbstractBaseDao.java 2019年7月8日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.dao;

import javax.annotation.Resource;

import com.bi.common.database.GeneralSqlUtil;

/**
 * 基本的AbstractBaseDao 提供sqlUtil供Dao使用
 * 
 * @author Mingfu
 * @version
 *
 */
public abstract class AbstractBaseDao extends AbsBaseDao {

    private GeneralSqlUtil sqlUtil;

    protected GeneralSqlUtil getSqlUtil() {
        return sqlUtil;
    }

    @Resource(name = "base.projectcode.sqlUtil")
    public void setSqlUtil(GeneralSqlUtil sqlUtil) {
        this.sqlUtil = sqlUtil;
    }
    
    public void setPav(String usn, String pav) {
        sqlUtil.setPav(usn, pav);
    }

}
