package com.dgut.gq.www.common.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UsersDetails实现类
 * 权限校验和登入认证
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-7
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {
    /**
     * 用户
     */
    private User user;
    /**
     * 返回权限集合
     */
    private List<String> permission;

    public LoginUser(User user, List<String> permission) {
        this.user = user;
        this.permission = permission;
    }

    public LoginUser(User user) {
        this.user = user;
    }

    /**
     * 封装权限信息
     *
     * @return 权限
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //把permission中的string类型权限封装成SimpleGrantedAuthority
        List<GrantedAuthority> list = new ArrayList<>();
        if (permission == null) {
            return list;
        }
        for (String s : permission) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(s);
            list.add(simpleGrantedAuthority);
        }
        return list;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * @return 是否没过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return boolean
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
