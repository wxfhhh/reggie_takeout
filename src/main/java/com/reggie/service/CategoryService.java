package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Category;

public interface CategoryService extends IService<Category> {
    public void removeById(Long id);
}
