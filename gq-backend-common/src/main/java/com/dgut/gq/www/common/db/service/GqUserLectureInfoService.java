package com.dgut.gq.www.common.db.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;

import java.util.List;

public interface GqUserLectureInfoService extends IService<UserLectureInfo> {

    Page<UserLectureInfo> getByOpenid(String openid, Integer page, Integer pageSize);

    UserLectureInfo getByLectureAndOpenid(String lectureId, String openid);

    /**
     * 查询报名和参加讲座的用户
     * @param page
     * @param pageSize
     * @param id
     * @param status 大于就是参加讲座的
     * @return
     */
    Page<UserLectureInfo> getByLectureId(int page, int pageSize, String id, Integer status);

    List<UserLectureInfo> getByLectureId(String id, Integer status);
}
