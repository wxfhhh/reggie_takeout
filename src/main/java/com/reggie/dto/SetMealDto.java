package com.reggie.dto;

import com.reggie.domain.Setmeal;
import com.reggie.domain.SetmealDish;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetMealDto extends Setmeal implements Serializable {
    private List<SetmealDish> dish=new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
