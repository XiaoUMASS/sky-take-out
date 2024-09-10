package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入数据
     * @param flavors
     */
//    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 删除菜品关联的口味数据
     * @param dishIds
     */
    void deleteRelatedFlavors(List<Long> dishIds);

    /**
     * 根据dishId查询关联口味
     * @param dishId
     * @return
     */
    List<DishFlavor> getById(Long dishId);

    /**
     * 更新
     * @param flavors
     */
//    void update(List<DishFlavor> flavors);
}
