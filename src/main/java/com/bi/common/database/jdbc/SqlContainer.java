/*
 * @(#)SqlContainer.java 2019年7月8日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.database.jdbc;

/**
 * @author Steven
 * @version 1.0
 *
 */

public final class SqlContainer {

    /* // fix:弱掃修正, J2EE Bad Practices: Threads
    private static final ThreadLocal<SqlContaint> CONTAINT = new ThreadLocal<SqlContaint>() {

        @Override
        protected SqlContaint initialValue() {
            return new SqlContaint();
        }
    };
    */

    private SqlContainer() {};

    public static SqlContaint getSqlContaint() {
        // return CONTAINT.get();
        return null; // fix:弱掃修正, J2EE Bad Practices: Threads
    }

    public static class SqlContaint {

        // private boolean logged = false;
        private String sql;
        private Object[] params;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Object[] getParams() {
            return params;
        }

        public void setParams(Object[] params) {
            this.params = params;
        }

        public void clear() {
            sql = null;
            params = null;
        }
    }

}
