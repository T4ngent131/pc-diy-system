package com.pcdiy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pcdiy.entity.InventoryRecord;
import java.util.List;

public interface InventoryRecordService extends IService<InventoryRecord> {
    void recordIn(String componentId, String componentName, int quantity, int beforeStock, int afterStock, String remark);
    void recordOut(String componentId, String componentName, int quantity, int beforeStock, int afterStock, String remark);
    List<InventoryRecord> getRecent(int limit);
}
