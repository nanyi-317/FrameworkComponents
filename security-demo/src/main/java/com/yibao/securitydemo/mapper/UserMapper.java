package com.yibao.securitydemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibao.securitydemo.entity.Role;
import com.yibao.securitydemo.entity.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author yibao
 * @create 2022 -04 -12 -17:47
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT\n" +
            "\tr.id,\n" +
            "\tr.NAME,\n" +
            "\tr.name_zh nameZh \n" +
            "FROM\n" +
            "\trole r,\n" +
            "\tuser_role ur \n" +
            "WHERE\n" +
            "\tr.id = ur.rid \n" +
            "\tAND ur.uid = #{uid}")
    List<Role> queryRolesByUid(Integer id);
}
