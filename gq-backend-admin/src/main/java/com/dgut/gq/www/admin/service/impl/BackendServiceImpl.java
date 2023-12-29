package com.dgut.gq.www.admin.service.impl;

import com.dgut.gq.www.admin.service.BackendService;


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
}

