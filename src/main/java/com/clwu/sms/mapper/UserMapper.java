package com.clwu.sms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clwu.sms.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/23 11:29
 * @Description: 用户管理mapper
 **/
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
