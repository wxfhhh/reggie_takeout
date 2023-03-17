package com.reggie.dto;
import com.reggie.domain.Dish;
import com.reggie.domain.DishFlavor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO，全称为Data Transfer Object, 即数据传输对象，一 般用于展示层与服务层之间的数据传输。
 */
@Data
public class DishDto extends Dish implements Serializable {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
