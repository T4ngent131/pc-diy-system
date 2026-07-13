package com.pcdiy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pcdiy.entity.InventoryRecord;
import com.pcdiy.mapper.InventoryRecordMapper;
import com.pcdiy.service.InventoryRecordService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InventoryRecordServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements InventoryRecordService {

    @Override
    public void recordIn(String componentId, String componentName, int quantity, int beforeStock, int afterStock, String remark) {
        InventoryRecord record = new InventoryRecord();
        record.setComponentId(componentId);
        record.setComponentName(componentName);
        record.setType("in");
        record.setQuantity(quantity);
        record.setBeforeStock(beforeStock);
        record.setAfterStock(afterStock);
        record.setRemark(remark);
        record.setOperator("system");
        save(record);
    }

    @Override
    public void recordOut(String componentId, String componentName, int quantity, int beforeStock, int afterStock, String remark) {
        InventoryRecord record = new InventoryRecord();
        record.setComponentId(componentId);
        record.setComponentName(componentName);
        record.setType("out");
        record.setQuantity(quantity);
        record.setBeforeStock(beforeStock);
        record.setAfterStock(afterStock);
        record.setRemark(remark);
        record.setOperator("system");
        save(record);
    }

    @Override
    public List<InventoryRecord> getRecent(int limit) {
        return list(new LambdaQueryWrapper<InventoryRecord>()
                .orderByDesc(InventoryRecord::getCreatedAt)
                .last("LIMIT " + limit));
    }
}
