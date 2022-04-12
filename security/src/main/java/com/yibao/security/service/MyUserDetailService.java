package com.yibao.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yibao.security.entity.Role;
import com.yibao.security.entity.User;
import com.yibao.security.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author yibao
 * @create 2022 -04 -12 -10:54
 */
@Component
public class MyUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public MyUserDetailService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1.查询用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("用户名不存在!");
        }
        // 2.查询权限信息
        List<Role> roles = userMapper.queryRolesById(user.getId());
        user.setRoles(roles);
        // 3.返回对象信息
        return user;
    }
}
