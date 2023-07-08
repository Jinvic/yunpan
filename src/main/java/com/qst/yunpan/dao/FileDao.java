package com.qst.yunpan.dao;

import org.apache.ibatis.annotations.Param;

public interface FileDao {
    void insertFiles(@Param("filePath") String filePath, @Param("userName") String userName) throws Exception;

}