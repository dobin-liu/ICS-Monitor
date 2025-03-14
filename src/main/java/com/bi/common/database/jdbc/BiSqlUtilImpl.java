/*
 * @(#)BiSqlUtilImpl.java 2019年7月3日
 *
 * Copyright (c) 2013 - Business Intelligence Info. Tech.
 * 3F., No.363, Sec. 2, Fuxing S. Rd., Da’an Dist.,
 * Taipei City 106, Taiwan (R.O.C.)
 * All rights reserved.
 */

package com.bi.common.database.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.bi.base.database.BiDataSourceProxy;
import com.bi.base.database.DatabaseType;
import com.bi.base.database.PageContainer;
import com.bi.base.database.Sql;
import com.bi.base.database.SqlHelper;
import com.bi.base.database.jdbc.SqlUtilWithSinglePaginateDbAwareImpl;
import com.bi.base.exception.BIRuntimeException;
import com.bi.common.database.GeneralSqlUtil;
import com.bi.common.database.jdbc.SqlContainer.SqlContaint;
import com.bi.common.model.DateSernoEntity;
import com.bi.util.FieldMethodUtil;
import com.bi.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mingfu
 * @version
 *
 */
@Slf4j
public class BiSqlUtilImpl extends SqlUtilWithSinglePaginateDbAwareImpl implements GeneralSqlUtil {

    private SimpleJdbcCall dataserno;
    private static final String INPUT_NAME = "I_DATESER_COLNAME";
    private static final String OUTPUT_NAME = "O_DATESER_CODE";

    private final DataSourceTransactionManager transactionManager;

    public BiSqlUtilImpl(JdbcTemplate jdbcTemplate, DatabaseType databaseType, String schemaName, QueryRunner queryRunner, SqlHelper sqlHelper,
            DataSourceTransactionManager dataSourceTransactionManager) throws IllegalArgumentException, NullPointerException {
        super(jdbcTemplate, databaseType, schemaName, queryRunner, sqlHelper);
        transactionManager = dataSourceTransactionManager;
        setupDataserno(jdbcTemplate, schemaName);
    }

    public BiSqlUtilImpl(JdbcTemplate jdbcTemplate, String databaseType, String schemaName, SqlHelper sqlHelper,
            DataSourceTransactionManager dataSourceTransactionManager, boolean setupDataserno) throws IllegalArgumentException, NullPointerException {
        super(jdbcTemplate, databaseType, schemaName, sqlHelper);
        transactionManager = dataSourceTransactionManager;
        
        if (setupDataserno) {
            setupDataserno(jdbcTemplate, schemaName);
        }
    }

    private void setupDataserno(JdbcTemplate jdbcTemplate, String schemaName) {

        dataserno = new SimpleJdbcCall(jdbcTemplate).withProcedureName("SP_GET_DATESER_CODE");
        if (!StrUtil.isEmpty(schemaName)) {
            dataserno.setSchemaName(schemaName);
        }
        dataserno.addDeclaredParameter(new SqlParameter(INPUT_NAME, Types.VARCHAR));
        dataserno.addDeclaredParameter(new SqlOutParameter(OUTPUT_NAME, Types.VARCHAR));
        dataserno.setAccessCallParameterMetaData(false);
        dataserno.compile();
    }
    
    @Override
    public void setPav(String usn, String pav) {
        final BiDataSourceProxy dsp = (BiDataSourceProxy)jdbcTemplate.getDataSource();
        final BasicDataSource  ds = (BasicDataSource)dsp.getTargetDataSource();
        ds.setUsername(usn);
        ds.setPassword(pav);
        
    }

    @Override
    public <E> List<E> findTop(String sql, int top, Class<? extends E> clazz, Object... params) {

        setSqlContaint(sql, params);
        return super.findTop(sql, top, clazz, params);
    }

    @Override
    public List<Map<String, Object>> findTopWithMap(String sql, int top, Object... params) {

        setSqlContaint(sql, params);
        return super.findTopWithMap(sql, top, params);
    }

    @Override
    public <E> PageContainer<E> findWithPagination(String sql, int page, int rowsPerPage, Class<? extends E> clazz, Object... params) {

        setSqlContaint(sql, params);
        return super.findWithPagination(sql, page, rowsPerPage, clazz, params);
    }

    @Override
    public PageContainer<Map<String, Object>> findWithPaginationMap(String sql, int page, int rowsPerPage, Object... params) {

        setSqlContaint(sql, params);
        return super.findWithPaginationMap(sql, page, rowsPerPage, params);
    }

    @Override
    public <E> List<E> find(String sql, Class<? extends E> clazz, Object... params) {

        setSqlContaint(sql, params);
        return super.find(sql, clazz, params);
    }

    @Override
    public List<Map<String, Object>> findWithMap(String sql, Object... params) {

        setSqlContaint(sql, params);
        return super.findWithMap(sql, params);
    }

    private void setSqlContaint(String sql, Object[] params) {

        /* // fix:弱掃修正, J2EE Bad Practices: Threads
        final SqlContaint sc = SqlContainer.getSqlContaint();
        // if(!sc.isLogged()) {
        // if(sc.getSql() == null) {
        sc.setSql(sql);
        sc.setParams(params);
        // sc.setLogged(true);
        // }
        */
    }

    /**
     * insert時呼叫SP取得自動流水號編號(sp取得編碼的欄位為key值)
     */
    @Override
    public String insertWithAdmDateSerno(DateSernoEntity entity) {
        final String serno = setDateSerno(entity);
        super.insert(entity);
        return serno;
    }

    /**
     * insert時呼叫SP取得自動流水號編號(sp取得編碼的欄位不是key值)
     */
    /*
     * @Override public String getDateSernoByColName(String colName) { final DefaultTransactionDefinition td = new
     * DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
     * td.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE); final TransactionStatus status = transactionManager.getTransaction(td);
     * Map<String, Object> data = new HashMap<String, Object>(1); data.put(INPUT_NAME, colName); // dtat is null data = dataserno.execute(data);
     * transactionManager.commit(status); final String serno = (String) data.get(OUTPUT_NAME); if (serno == null) { throw new
     * BIRuntimeException("Formated SERNO is not found for colname: " + colName); } return serno; }
     */
    /**
     * 因為不能使用sp，取號改作法
     */
    @Override
    @Transactional
    public String getDateSernoByColName(String colName) {
        final Sql sql = new Sql();
        sql.a("UPDATE AD SET SERDATE = GETDATE(), ");
        sql.a("SERNO = ");
        sql.a("CASE WHEN SERDATE_TYPE  IN ('A','F') THEN "); // 系統取號，增加類型D(只有數字滾號)和F(月日+數字滾號)
        sql.a("CASE WHEN CONVERT(VARCHAR(8),SERDATE,112) = CONVERT(VARCHAR(8),GETDATE(),112) THEN  SERNO+1 ELSE '1' END ");
        sql.a("WHEN SERDATE_TYPE = 'E' THEN ");
        sql.a("CASE WHEN YEAR(SERDATE) = YEAR(GETDATE()) THEN  SERNO+1 ELSE '1' END ");
        sql.a("WHEN SERDATE_TYPE='D' THEN ");
        sql.a("CASE WHEN ");
        sql.a("SERNO <> RIGHT(REPLICATE('9',SERNO_LEN), SERNO_LEN) THEN SERNO+1 ");
        sql.a("ELSE '1' ");
        sql.a("END ");
        sql.a("ELSE ");
        sql.a("SERNO+1 ");
        sql.a("END ");
        sql.a("FROM ADM_DATESERNO AD ");
        sql.a("INNER JOIN ADM_DATESERNO_SETS ADS ON AD.DATESER_COLNAME=ADS.DATESER_COLNAME ");
        sql.a("WHERE AD.DATESER_COLNAME = ").pString(colName);
        final int i = update(sql);
        if (i <= 0) {
            throw new BIRuntimeException("Formated SERNO is not found for colname: " + colName);
        }
        final Sql sql2 = new Sql();
        sql2.a("SELECT CASE WHEN SERDATE_TYPE = 'A' THEN ");
        sql2.a("COALESCE(PREFIX_CODE,'') + CONVERT (VARCHAR(8),GETDATE(),112) + COALESCE(CONJ_SIGN,'') + RIGHT(REPLICATE('0',SERNO_LEN) + CAST(SERNO AS VARCHAR) ,SERNO_LEN) ");
        sql2.a("WHEN SERDATE_TYPE = 'E' THEN ");
        sql2.a("COALESCE(PREFIX_CODE,'') + CAST(YEAR(GETDATE())-1911 AS VARCHAR(3)) + RIGHT(REPLICATE('0',SERNO_LEN) + CAST(SERNO AS VARCHAR)  ,SERNO_LEN) ");
        sql2.a("WHEN SERDATE_TYPE = 'D' THEN ");
        sql2.a("COALESCE(PREFIX_CODE,'') + RIGHT(REPLICATE('0',SERNO_LEN) + CAST(SERNO AS VARCHAR)  ,SERNO_LEN) ");
        sql2.a("WHEN SERDATE_TYPE = 'F' THEN ");
        sql2.a("COALESCE(PREFIX_CODE, '')+ CAST(MONTH(GETDATE()) AS VARCHAR(2))+ CAST(DAY(GETDATE()) ");
        sql2.a("AS VARCHAR(2))+RIGHT(REPLICATE('0', SERNO_LEN)+CAST(SERNO AS VARCHAR), SERNO_LEN) ");
        sql2.a("END SER_CODE ");
        sql2.a("FROM ADM_DATESERNO AD ");
        sql2.a("INNER JOIN ADM_DATESERNO_SETS ADS ON AD.DATESER_COLNAME=ADS.DATESER_COLNAME ");
        sql2.a("WHERE AD.DATESER_COLNAME = ").pString(colName);

        final Map<String, Object> map = findOneWithMap(sql2);

        return (String) map.get("SER_CODE");
    }

    private String setDateSerno(DateSernoEntity entity) {

        // int cnt = addCount();
        final DefaultTransactionDefinition td = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        td.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);
        final TransactionStatus status = transactionManager.getTransaction(td);

        Map<String, Object> data = new HashMap<String, Object>(1);
        final String colName = getDateserColname(entity);
        data.put(INPUT_NAME, colName);

        try {
            data = dataserno.execute(data);
        }
        catch (InvalidDataAccessApiUsageException e) {
            log.error("dataserno.execute error");
        }
        finally {
            try {
                transactionManager.commit(status);
            } catch (TransactionException e) {
                log.error("transactionManager.commit error");
            }
        }
        final String serno = (String) data.get(OUTPUT_NAME);
        if (serno == null) {
            throw new BIRuntimeException("Formated SERNO is not found for colname: " + colName);
        }

        try {
            FieldMethodUtil.invokeSetter(entity, entity.getDateSernoFieldName(), serno);
            return serno;
        }
        catch (final Exception e) {
            throw new BIRuntimeException(e);
        }

    }

    private String getDateserColname(DateSernoEntity entity) {

        final String tableName = entity.getTableName();
        return (tableName + "." + entity.getDateSernoFieldName()).toUpperCase();
    }
}
