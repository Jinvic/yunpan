package com.qst.yunpan.dao;

import com.qst.yunpan.pojo.Share;

import java.util.List;

public interface ShareDao {
    void shareFile(Share share) throws Exception;

    List<Share> findShare(Share share) throws Exception;
}
