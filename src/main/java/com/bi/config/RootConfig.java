/*
 * @(#)RootConfig.java 2019年7月8日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
// @ImportResource("classpath*:spring/ApplicationContext-BASE-Config.xml")
@ImportResource("classpath*:spring/ApplicationContext*.xml")
public class RootConfig {}
