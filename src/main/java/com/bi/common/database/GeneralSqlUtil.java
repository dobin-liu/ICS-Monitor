package com.bi.common.database;

import com.bi.base.database.SqlUtil;
import com.bi.common.model.DateSernoEntity;

/**
 * 
 * @author Mingfu
 * @version
 *
 */
public interface GeneralSqlUtil extends SqlUtil {

    /**
     * insert時呼叫SP取得自動流水號編號 (若sp取得編碼的欄位為key值時使用)
     *
     * @param entity
     * @return 流水號
     *
     */
    public String insertWithAdmDateSerno(DateSernoEntity entity);

    /**
     * insert時呼叫SP取得自動流水號編號 (若sp取得編碼的欄位不是key值使用)
     *
     * @param colName
     * @return 流水號
     *
     */
    public String getDateSernoByColName(String colName);
    
    public void setPav(String usn, String pav);
}
