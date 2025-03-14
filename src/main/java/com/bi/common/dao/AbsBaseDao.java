/*
 * @(#)AbstractBaseDao.java 2019年7月8日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.dao;

import java.util.Calendar;

import com.bi.util.DateUtil;
import com.bi.util.DateUtil.DateFormatter;

/**
 * 基本的AbstractBaseDao 提供sqlUtil供Dao使用
 *
 *
 */

public abstract class AbsBaseDao {

    private final DateFormatter YEAR_MONTH_FORMAT = DateUtil.createFormatter("yyyyMM");

    protected String getCurrentYear() {
        return String.valueOf(DateUtil.now().get(Calendar.YEAR));
    }

    protected String getCurrentToHour() {
        return DateUtil.format(DateUtil.getTimestamp(), "yyyyMMddHH");
    }

    protected DateFormatter getYearMonthFormatter() {
        return YEAR_MONTH_FORMAT;
    }
}
