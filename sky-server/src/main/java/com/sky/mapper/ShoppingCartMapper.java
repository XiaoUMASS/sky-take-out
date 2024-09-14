package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新购物车商品
     * @param cart
     */
    void updateNumberById(ShoppingCart cart);

    /**
     * 插入菜品或套餐
     * @param shoppingCart
     */
    void insert(ShoppingCart shoppingCart);

    /**
     * 查询当前用户的购物车
     * @param currentId
     * @return
     */
//    List<ShoppingCart> getByUserId(Long currentId);
}
