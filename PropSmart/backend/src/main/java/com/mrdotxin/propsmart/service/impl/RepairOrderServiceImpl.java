package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Repairorder;
import generator.service.RepairOrderService;
import generator.mapper.RepairOrderMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【repairorder(报修申请)】的数据库操作Service实现
* @createDate 2025-06-04 09:36:05
*/
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, Repairorder>
    implements RepairOrderService {

}




