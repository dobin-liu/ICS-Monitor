package com.bi.common.dao.jdbc;

import org.springframework.stereotype.Repository;

import com.bi.base.database.Sql;
import com.bi.common.dao.AbstractBaseDao;
import com.bi.common.dao.TblSysApiLogsDao;
import com.bi.common.model.TblSysApiLogs;

@Repository
public class TblSysApiLogsDaoImpl extends AbstractBaseDao implements TblSysApiLogsDao {

    @Override
    public int getRowsCount() {
        final Sql sql = new Sql();
        sql.a("SELECT COUNT(*) NUM ");
        sql.a("FROM tblSysApiLogs ");
        return getSqlUtil().findCount(sql);
    }

    @Override
    public int insert(TblSysApiLogs entity) {
        return getSqlUtil().insert(entity);
    }
}
