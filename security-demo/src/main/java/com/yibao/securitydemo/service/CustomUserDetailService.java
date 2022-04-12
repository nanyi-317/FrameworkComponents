package com.yibao.securitydemo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yibao.securitydemo.entity.Role;
import com.yibao.securitydemo.entity.User;
import com.yibao.securitydemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 自定义 UserDetailsService 实现
 * @author yibao
 * @create 2022 -04 -12 -17:38
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserMapper userMapper;

    @Autowired
    public CustomUserDetailService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1.查询用户信息
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        if (ObjectUtils.isEmpty(user)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        // 2.查询权限信息
        List<Role> roles = userMapper.queryRolesByUid(user.getId());
        user.setRoles(roles);
        // 3.返回对象
        return user;
    }
}
