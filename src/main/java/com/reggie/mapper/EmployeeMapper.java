package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

//简单的sql 利用 mybatis plus
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
