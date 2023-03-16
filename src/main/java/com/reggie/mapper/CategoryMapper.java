package com.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.reggie.domain.Category;
import org.apache.ibatis.annotations.Mapper;

/*利用mybatis plus 进行简单的sql操作*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
