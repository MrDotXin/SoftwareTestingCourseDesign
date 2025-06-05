package com.mrdotxin.propsmart.job;

import cn.hutool.core.collection.CollUtil;
import com.mrdotxin.propsmart.model.entity.Bill;
import com.mrdotxin.propsmart.model.entity.Property;
import com.mrdotxin.propsmart.model.entity.User;
import com.mrdotxin.propsmart.service.BillService;
import com.mrdotxin.propsmart.service.EnergyConsumptionService;
import com.mrdotxin.propsmart.service.PropertyService;
import com.mrdotxin.propsmart.service.UserService;
import com.mrdotxin.propsmart.websocket.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Configuration
public class MonthlyBillTask {

    @Resource
    private BillService billService;
    
    @Resource
    private EnergyConsumptionService energyConsumptionService;

    @Resource
    private UserService userService;
    
    @Resource
    private PropertyService propertyService;

    @Resource
    private NotificationService notificationService;

    // 每月1号凌晨2点生成账单
    @Scheduled(cron = "${Bill-Generate-Interval}")
    public void generateMonthlyBills() {
        log.info("开始生成月度账单...");
        
        // 获取上个月的第一天和最后一天
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = cal.getTime();
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date monthEnd = cal.getTime();
        
        List<Property> properties = propertyService.list();
        if (CollUtil.isEmpty(properties)) {
            log.info("没有房产数据，跳过账单生成");
            return;
        }
        
        int generatedCount = 0;
        int abnormalCount = 0;
        
        for (Property property : properties) {
            try {
                // 生成电费账单
                Bill electricityBill = generateUtilityBill(property, "electricity", monthStart, monthEnd);
                if (electricityBill != null) {
                    billService.save(electricityBill);
                    generatedCount++;
                    if (checkAbnormalBill(electricityBill)) {
                        abnormalCount++;
                        notifyAbnormalBill(electricityBill);
                    }
                }
                
                // 生成水费账单
                Bill waterBill = generateUtilityBill(property, "water", monthStart, monthEnd);
                if (waterBill != null) {
                    billService.save(waterBill);
                    generatedCount++;
                    if (checkAbnormalBill(waterBill)) {
                        abnormalCount++;
                        notifyAbnormalBill(waterBill);
                    }
                }
                
                // 生成物业费账单（固定费用）
                Bill propertyFeeBill = generatePropertyFeeBill(property);
                if (propertyFeeBill != null) {
                    billService.save(propertyFeeBill);
                    generatedCount++;
                }
                
            } catch (Exception e) {
                log.error("生成房产{}账单失败: {}", property.getId(), e.getMessage());
            }
        }
        
        log.info("月度账单生成完成，共生成{}条账单，其中异常账单{}条", generatedCount, abnormalCount);
    }

    private Bill generateUtilityBill(Property property, String utilityType, Date monthStart, Date monthEnd) {
        // 计算上个月的总消耗量和总费用
        Double totalConsumption = energyConsumptionService.getTotalConsumption(
            property.getId(), utilityType, monthStart, monthEnd);
        
        if (totalConsumption == null || totalConsumption == 0) {
            return null;
        }
        
        // 获取单价
        Double price = utilityType.equals("electricity") ? 0.8 : 3.5;
        Double amount = totalConsumption * price;
        
        Bill bill = new Bill();
        bill.setPropertyId(property.getId());
        bill.setType(utilityType);
        bill.setAmount(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP));
        bill.setDeadline(getBillDeadline());
        bill.setStatus("unpaid");
        
        return bill;
    }

    private Bill generatePropertyFeeBill(Property property) {
        // 物业费根据面积计算，假设每平米2元
        double feePerSquare = 2.0;
        double amount = property.getArea() * feePerSquare;
        
        Bill bill = new Bill();
        bill.setPropertyId(property.getId());
        bill.setType("property_fee");
        bill.setAmount(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP));
        bill.setDeadline(getBillDeadline());
        bill.setStatus("unpaid");
        
        return bill;
    }

    private Date getBillDeadline() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 15); // 15天后截止
        return cal.getTime();
    }

    private boolean checkAbnormalBill(Bill bill) {
        // 电费超过300元视为异常
        if ("electricity".equals(bill.getType())) {
            return bill.getAmount().compareTo(BigDecimal.valueOf(300)) > 0;
        }
        
        // 水费超过200元视为异常
        if ("water".equals(bill.getType())) {
            return bill.getAmount().compareTo(BigDecimal.valueOf(200)) > 0;
        }
        
        return false;
    }

    private void notifyAbnormalBill(Bill bill) {
        Property property = propertyService.getById(bill.getPropertyId());
        User user = userService.getByIdCardNumber(property.getOwnerIdentity());
        Long ownerId = user.getId();

        String billType = "";
        switch (bill.getType()) {
            case "electricity": billType = "电费"; break;
            case "water": billType = "水费"; break;
            case "property_fee": billType = "物业费"; break;
            default: billType = "费用";
        }
        
        String message = String.format("您的%s账单异常！金额: %.2f元，请及时核查", 
            billType, bill.getAmount());
        
            try {
                notificationService.handleAbnormalBillNotification(user, property, message);
            } catch (Exception e) {
                log.error("发送异常账单通知失败，用户ID: {}", ownerId, e);
            }
    }
}