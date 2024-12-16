package com.dgut.gq.www.common.db.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;

import java.util.List;

public interface GqUserLectureInfoService extends IService<UserLectureInfo> {

    Page<UserLectureInfo> getByOpenid(String openid, Integer page, Integer pageSize);

    UserLectureInfo getByLectureAndOpenid(String lectureId, String openid);
}
