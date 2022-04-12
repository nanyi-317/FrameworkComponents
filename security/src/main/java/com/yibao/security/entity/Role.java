package com.yibao.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @author yibao
 * @create 2022 -04 -11 -18:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@TableName(value = "role")
public class Role {
    @TableId(value = "id",type = IdType.INPUT)
    private Integer id;
    @TableField(value = "name")
    private String name;      // 角色的名称
    @TableField(value = "name_zh")
    private String nameZh;    // 角色的中文名称
}
