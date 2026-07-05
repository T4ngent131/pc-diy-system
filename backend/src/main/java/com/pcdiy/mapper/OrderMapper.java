package com.pcdiy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcdiy.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {
    @Select("SELECT * FROM orders WHERE customer LIKE CONCAT('%', #{keyword}, '%') OR order_no LIKE CONCAT('%', #{keyword}, '%')")
    List<Order> search(@Param("keyword") String keyword);

    @Select("SELECT COUNT(*) FROM orders WHERE status != 'completed' AND status != 'cancelled'")
    Integer countPending();
}
