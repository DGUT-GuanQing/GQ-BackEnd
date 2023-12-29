package com.dgut.gq.www.admin.service.impl;

import com.dgut.gq.www.admin.model.dto.DepartmentDto;
import com.dgut.gq.www.admin.model.dto.LectureDto;
import com.dgut.gq.www.admin.model.dto.PositionDto;
import com.dgut.gq.www.admin.model.dto.PosterTweetDto;
import com.dgut.gq.www.admin.service.BackendService;


import com.dgut.gq.www.common.common.SystemJsonResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


;

/**
 * 后台管理
 * @since  2022-10-8
 * @author  hyj
 * @version  1.0
 */
@Service
public class BackendServiceImpl implements BackendService, UserDetailsService {


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public SystemJsonResponse login(String userName, String password) {
        return null;
    }

    @Override
    public void logout() {

    }

    @Override
    public SystemJsonResponse getAttendLectureUser(int page, int pageSize, String id, Integer status) {
        return null;
    }

    @Override
    public SystemJsonResponse updateOrSaveLecture(LectureDto lectureDto) {
        return null;
    }

    @Override
    public SystemJsonResponse saveUpdatePosterTweet(PosterTweetDto posterTweetDto) {
        return null;
    }

    @Override
    public SystemJsonResponse getLecture(int page, int pageSize, String name) {
        return null;
    }

    @Override
    public SystemJsonResponse exportUser(String id, Integer status) {
        return null;
    }

    @Override
    public SystemJsonResponse exportCurriculumVitae(String departmentId, Integer term) {
        return null;
    }

    @Override
    public SystemJsonResponse deleteLecture(String id) {
        return null;
    }

    @Override
    public SystemJsonResponse deleteDepartment(String id) {
        return null;
    }

    @Override
    public SystemJsonResponse deletePosition(String id) {
        return null;
    }

    @Override
    public SystemJsonResponse saveAndUpdateDep(DepartmentDto departmentDto) {
        return null;
    }

    @Override
    public SystemJsonResponse saveAndUpdatePos(PositionDto positionDto) {
        return null;
    }
}

