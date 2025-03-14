package com.bi.common.dao;

import com.bi.common.model.TblSysApiLogs;

public interface TblSysApiLogsDao {

    /**
     * 取得資料表 筆數
     * 
     * @return
     *
     */
    public int getRowsCount();
    
    public int insert(TblSysApiLogs entity);
    
    public void setPav(String usn, String pav);
}
