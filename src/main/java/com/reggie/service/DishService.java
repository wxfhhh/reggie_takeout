package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Dish;
import com.reggie.dto.DishDto;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);
    public void deleteWithFlavor(List<Long> ids);
    public void upData_s(int status,List<Long> ids);
    public DishDto getWithFlavor(Long id);
}
