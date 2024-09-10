package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     * @return
     */
    public List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 新增套餐绑定的菜品
     * @param setmealDishes
     */
    void insert(List<SetmealDish> setmealDishes);
}
