package com.clwu.sms.service;

import com.clwu.sms.entity.Physic;
import com.clwu.sms.enums.UnitEnum;

import java.util.List;

/**
 * @Author: wuchunlong
 * @Date: 2025/9/24 10:39
 * @Description:
 **/
public interface PhysicService {
    /**
     * 增加药品or耗材
     * @param physic
     */
    public void addPhysic(Physic physic);

    /**
     * 删除
     * @param physicId
     */

    public void delePhysic(Long physicId);

    /**
     * 修改药品or耗材
     * @param physic
     */

    public void updPhysic(Physic physic);

    /**
     * 基于ID查找
     * @param id 药品ID
     * @return
     */

    public Physic findPhysicById(Long pid);

    /**
     * 基于名称查找药物
     * @param name 药品名称
     * @return 满足条件列表
     */
    public List<Physic> findPhysicByName(String name);

    /**
     * 基于别名查找药物
     * @param alias 别名
     * @return 满足条件列表
     */
    public List<Physic> findPhysicByAlias(String alias);

    /**
     * 基于生产厂家查找
     * @param manufacturer
     * @return
     */
    public List<Physic> findPhysicByManufacturerString(String manufacturer);

    /**
     * 获取药品或者耗材单位
     * @return
     */
    public List<UnitEnum> getAllUnit();


}
