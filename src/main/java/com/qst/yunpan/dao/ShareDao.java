package com.qst.yunpan.dao;

import com.qst.yunpan.pojo.Share;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareDao {
    void shareFile(Share share) throws Exception;

    List<Share> findShare(Share share) throws Exception;

    List<Share> findShareByName(@Param("username") String username, @Param("status") int status);

    void cancelShare(@Param("url") String url, @Param("filePath") String filePath, @Param("status") int delete);
}
