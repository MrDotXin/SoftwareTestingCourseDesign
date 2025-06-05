package com.mrdotxin.propsmart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 账单流水
 *
 */
@TableName(value ="energyconsumption")
@Data
public class EnergyConsumption implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房产ID
     */
    @TableField(value = "propertyId")
    private Long propertyId;

    /**
     * 能耗类型
     */
    @TableField(value = "energyType")
    private String energyType;

    /**
     * 能耗值
     */
    @TableField(value = "consumption")
    private Double consumption;

    /**
     * 能耗值
     */
    @TableField(value = "price")
    private Double price;

    /**
     * 测量时间
     */
    @TableField(value = "measureTime")
    private Date measureTime;

    /**
     * 创建时间
     */
    @TableField(value = "createTime")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        EnergyConsumption other = (EnergyConsumption) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPropertyId() == null ? other.getPropertyId() == null : this.getPropertyId().equals(other.getPropertyId()))
            && (this.getEnergyType() == null ? other.getEnergyType() == null : this.getEnergyType().equals(other.getEnergyType()))
            && (this.getMeasureTime() == null ? other.getMeasureTime() == null : this.getMeasureTime().equals(other.getMeasureTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPropertyId() == null) ? 0 : getPropertyId().hashCode());
        result = prime * result + ((getEnergyType() == null) ? 0 : getEnergyType().hashCode());
        result = prime * result + ((getMeasureTime() == null) ? 0 : getMeasureTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", propertyId=").append(propertyId);
        sb.append(", energyType=").append(energyType);
        sb.append(", measureTime=").append(measureTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}