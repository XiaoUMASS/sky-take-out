package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细数据
     * @param details
     */
    void insertBatch(List<OrderDetail> details);

    /**
     * 根据OrderId查询订单详细信息
     * @param orderId
     * @return
     */
    List<OrderDetail> getByOrderId(Long orderId);
}
