/*
 * @(#)McspWebServiceDaoImpl.java Oct 22, 2019
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.external.api.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface SampleWebServiceDao {
    public String doAction();
}
