package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.domain.Setmeal;
import com.reggie.dto.DishDto;
import com.reggie.dto.SetMealDto;

import java.util.LinkedList;
import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    public void saveWithDish(SetMealDto setMealDto);
    public void deleteWithDish(LinkedList<Long> ids);
    public void upData_s(int status, List<Long> ids);
}
