package com.dgut.gq.www.common.db.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.Lecture;

import java.util.List;

/**
 * 讲座
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-8
 */
public interface GqLectureService extends IService<Lecture> {

    /**
     * 获取最近未开始的讲座
     * @return
     */
    Lecture getLatestUnStartLecture();


    /**
     * 获取讲座信息
     * @param page
     * @param pageSize
     * @param msg 模糊查询
     * @param type 0-回顾，1-预告
     * @return
     */
    Page<Lecture> getLectures(int page, int pageSize, String msg, int type);

    List<Lecture> getByIds(List<String> list);

    /**
     * 后台查询讲座
     * @param page
     * @param pageSize
     * @param msg 搜索条件
     * @return
     */
    Page<Lecture> getBackendLectures(int page, int pageSize, String msg);
}
