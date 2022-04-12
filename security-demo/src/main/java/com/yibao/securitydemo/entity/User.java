package com.yibao.securitydemo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yibao
 * @create 2022 -04 -12 -17:45
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "user")
@Setter
@Getter
public class User implements UserDetails {
    @TableId(value = "id",type = IdType.INPUT)
    private Integer id;
    @TableField(value = "username")
    private String username;
    @TableField(value = "password")
    private String password;
    @TableField(value = "enabled")
    private Boolean isEnabled;  // 账号是否可用
    @TableField(value = "accountNonExpired")
    private Boolean isAccountNonExpired;    // 账号是否过期
    @TableField(value = "accountNonLocked")
    private Boolean isAccountNonLocked;     // 账号是否锁定
    @TableField(value = "credentialsNonExpired")
    private Boolean isCredentialsNonExpired;    // 凭证是否过期
    @TableField(value = "roles",exist = false)
    private List<Role> roles = new ArrayList<>();   // 用户角色集合

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        roles.forEach(role -> grantedAuthorities.add(new SimpleGrantedAuthority(role.getName())));
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
