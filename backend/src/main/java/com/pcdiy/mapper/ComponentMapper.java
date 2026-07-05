package com.pcdiy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pcdiy.entity.Component;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

public interface ComponentMapper extends BaseMapper<Component> {
    List<Component> selectByType(@Param("type") String type);

    @Select("SELECT DISTINCT brand FROM components WHERE type = #{type} ORDER BY brand")
    List<String> selectBrandsByType(@Param("type") String type);

    @Select("SELECT * FROM components WHERE stock <= 3 AND status = 1 ORDER BY stock ASC")
    List<Component> selectLowStock();
}
