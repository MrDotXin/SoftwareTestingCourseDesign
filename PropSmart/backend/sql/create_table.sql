CREATE DATABASE IF NOT EXISTS JCD;

USE JCD;

-- 用户表
CREATE TABLE IF NOT EXISTS user (
                                    id                  BIGINT AUTO_INCREMENT COMMENT '用户ID',
                                    userAccount         VARCHAR(256)    NOT NULL COMMENT '账号',
    userPassword        VARCHAR(512)    NOT NULL COMMENT '密码',
    userName            VARCHAR(256)        NULL COMMENT '用户昵称',
    userAvatar          VARCHAR(1024)       NULL COMMENT '用户头像',
    userProfile         VARCHAR(512)        NULL COMMENT '用户简介',
    userPhoneNumber     VARCHAR(64)         NULL COMMENT '联系电话',
    userIdCardNumber    VARCHAR(64)         NULL COMMENT '身份证',
    userRealName        VARCHAR(64)         NULL COMMENT '用户真实姓名',
    userRole            VARCHAR(256) DEFAULT 'ROLE_USER' NOT NULL COMMENT '用户角色：ROLE_USER/ROLE_ADMIN/ROLE_BAN',
    isOwner             BOOLEAN      DEFAULT FALSE NOT NULL COMMENT '是否是业主',
    createTime          DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime          DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
    ) COMMENT '用户表' COLLATE = utf8mb4_unicode_ci;

-- 楼栋表
CREATE TABLE IF NOT EXISTS building (
                                        id              BIGINT AUTO_INCREMENT,
                                        buildingName    VARCHAR(50)     UNIQUE NOT NULL COMMENT '楼栋名称/编号',
    address         VARCHAR(100)        NULL COMMENT '地理位置',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='楼栋信息';

-- 房产表
CREATE TABLE IF NOT EXISTS property (
                                        id              BIGINT AUTO_INCREMENT,
                                        buildingId      BIGINT          NOT NULL COMMENT '楼栋ID',
                                        unitNumber      VARCHAR(20)     NOT NULL COMMENT '单元号',
    roomNumber      VARCHAR(20)     NOT NULL COMMENT '房号',
    area            DOUBLE              NULL COMMENT '建筑面积',
    ownerIdentity   BIGINT              NULL COMMENT '持有者的身份证',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (buildingId) REFERENCES building(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='房产信息';

-- 公告表
CREATE TABLE IF NOT EXISTS notice (
                                      id              BIGINT AUTO_INCREMENT,
                                      title           VARCHAR(100)    NOT NULL COMMENT '标题',
    content         TEXT            NOT NULL COMMENT '内容',
    publishTime     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    expireTime      DATETIME            NULL COMMENT '过期时间',
    publisherId     BIGINT          NOT NULL COMMENT '发布者ID（管理员）',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (publisherId) REFERENCES user(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='小区公告';

-- 设施表
CREATE TABLE IF NOT EXISTS facility (
                                        id              BIGINT AUTO_INCREMENT,
                                        facilityName    VARCHAR(50)     NOT NULL COMMENT '设施名称',
    location        VARCHAR(100)        NULL COMMENT '位置',
    capacity        INT                 NULL COMMENT '容量',
    description     TEXT                NULL COMMENT '描述',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='小区设施';

-- 账单表
CREATE TABLE IF NOT EXISTS bill (
                                    id              BIGINT AUTO_INCREMENT,
                                    propertyId      BIGINT          NOT NULL COMMENT '房产ID',
                                    type            ENUM('property_fee', 'water', 'electricity', 'other') NOT NULL COMMENT '费用类型',
    amount          DECIMAL(10,2)   NOT NULL COMMENT '金额',
    deadline        DATE            NOT NULL COMMENT '截止日期',
    status          ENUM('unpaid', 'paid', 'overdue') DEFAULT 'unpaid' COMMENT '缴费状态',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    paidTime        DATETIME            NULL COMMENT '缴费时间',
    PRIMARY KEY (id),
    FOREIGN KEY (propertyId) REFERENCES property(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='费用账单';

    -- 能耗流水
CREATE TABLE IF NOT EXISTS energyConsumption (
                                                 id  BIGINT PRIMARY KEY AUTO_INCREMENT ,
                                                 propertyId BIGINT  NOT NULL COMMENT '房产ID',
                                                 energyType ENUM('electricity', 'water') NOT NULL COMMENT '能耗类型',
                                                 consumption   DOUBLE NOT NULL COMMENT '花费值',
                                                 price         DOUBLE NOT NULL COMMENT '价格',
                                                 measureTime    DATETIME NOT NULL COMMENT '测量时间',
                                                 createTime      DATETIME NOT NULL COMMENT '创建时间'
) DEFAULT CHARSET=utf8mb4 COMMENT='账单流水';

-- 报修申请表
CREATE TABLE IF NOT EXISTS repairOrder (
                                           id              BIGINT AUTO_INCREMENT,
                                           userId          BIGINT          NOT NULL COMMENT '报修用户ID',
                                           propertyId      BIGINT          NOT NULL COMMENT '报修房产ID',
                                           description     TEXT            NOT NULL COMMENT '问题描述',
                                           status          ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    reviewerId      BIGINT              NULL COMMENT '处理人ID（管理员）',
    reviewTime      DATETIME            NULL COMMENT '完成时间',
    reviewMessage   VARCHAR(512)        NULL COMMENT '审批原因',
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (propertyId) REFERENCES property(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='报修申请';

-- 投诉建议表
CREATE TABLE IF NOT EXISTS complaintSuggestion (
                                                   id              BIGINT AUTO_INCREMENT,
                                                   userId          BIGINT          NOT NULL COMMENT '提交用户ID',
                                                   content         TEXT            NOT NULL COMMENT '内容',
                                                   type            ENUM('complaint', 'suggestion') NOT NULL COMMENT '类型',
    status          ENUM('pending', 'success', 'rejected') DEFAULT 'pending' COMMENT '状态',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    reviewerId      BIGINT              NULL COMMENT '处理人ID（管理员）',
    reviewMessage   TEXT                NULL COMMENT '回复内容',
    reviewTime      DATETIME            NULL COMMENT '回复时间',
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES user(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议';

-- 访客表
CREATE TABLE IF NOT EXISTS visitor (
                                       id              BIGINT AUTO_INCREMENT,
                                       userId          BIGINT          NOT NULL COMMENT '被访用户ID',
                                       visitorName     VARCHAR(50)     NOT NULL COMMENT '访客姓名',
    idNumber        VARCHAR(18)         NULL COMMENT '身份证号',
    visitReason     VARCHAR(200)        NULL COMMENT '访问原因',
    visitTime       DATETIME        NOT NULL COMMENT '预计访问时间',
    duration        INT                 NULL COMMENT '预计时长（小时）',
    reviewStatus    ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审批状态',
    reviewerId      BIGINT              NULL COMMENT '审批人ID（管理员）',
    reviewTime      DATETIME            NULL COMMENT '审批时间',
    reviewMessage   VARCHAR(512)        NULL COMMENT '审批理由',
    passCode        VARCHAR(20)         NULL COMMENT '电子通行证',
    createTime      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime      DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (reviewerId) REFERENCES user(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='访客管理';

-- 设施预订表
CREATE TABLE IF NOT EXISTS facilityReservation (
                                                   id                  BIGINT AUTO_INCREMENT,
                                                   userId              BIGINT          NOT NULL COMMENT '预订用户ID',
                                                   facilityId          BIGINT          NOT NULL COMMENT '设施ID',
                                                   reservationTime     DATETIME        NOT NULL COMMENT '预订时间',
                                                   duration            INT             NOT NULL COMMENT '时长（小时）',
                                                   status              ENUM('pending', 'success', 'rejected') DEFAULT 'pending' COMMENT '状态',
    createTime          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    reviewerId          BIGINT              NULL COMMENT '审批人ID（管理员）',
    reviewMessage       VARCHAR(512)        NULL COMMENT '同意/拒绝原因',
    reviewTime          DATETIME            NULL COMMENT '审批时间',
    PRIMARY KEY (id),
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (facilityId) REFERENCES facility(id),
    FOREIGN KEY (reviewerId) REFERENCES user(id)
    ) DEFAULT CHARSET=utf8mb4 COMMENT='设施预订';

-- 电梯基本信息及实时运行参数表
CREATE TABLE IF NOT EXISTS elevator (
                                        id                  BIGINT AUTO_INCREMENT COMMENT '电梯ID',
                                        buildingId          BIGINT          NOT NULL COMMENT '所属楼栋ID（关联building表）',
                                        elevatorNumber      VARCHAR(50)     NOT NULL COMMENT '电梯编号（如A栋1号电梯）',
                                        installationDate    DATE            NULL COMMENT '安装日期',
                                        lastMaintenanceDate DATE            NULL COMMENT '上次维护日期',
                                        currentStatus       ENUM('正常', '预警', '故障', '维护中') DEFAULT '正常' COMMENT '当前运行状态',
                                        currentFloor        INT             NULL COMMENT '当前所在楼层',
                                        runningDirection    ENUM('上行', '下行', '静止') NULL COMMENT '运行方向',
                                        loadPercentage      INT             DEFAULT 0 COMMENT '负载百分比（0-100）',
                                        doorStatus          ENUM('开启', '关闭') DEFAULT '关闭' COMMENT '电梯门状态',

    -- 温度参数
                                        cabinTemperature    DECIMAL(5,2)    NULL COMMENT '轿厢温度（单位：℃）',
                                        motorTemperature    DECIMAL(5,2)    NULL COMMENT '电机温度（单位：℃）',

    -- 速度参数
                                        runningSpeed        DECIMAL(5,2)    NULL COMMENT '运行速度（单位：m/s）',
                                        ratedSpeed          DECIMAL(5,2)    NULL COMMENT '额定速度（单位：m/s，出厂设定值）',

    -- 加速度参数（高速电梯专用）
                                        acceleration        DECIMAL(5,3)    NULL COMMENT '加速度/减速度（单位：m/s²，保留3位小数）',

    -- 能耗参数
                                        powerConsumption    DECIMAL(10,2)   NULL COMMENT '实时功耗（单位：kW）',

                                        createTime          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        updateTime          DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
                                        PRIMARY KEY (id),
                                        FOREIGN KEY (buildingId) REFERENCES building(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='电梯基本信息及实时运行参数表';

-- 电梯异常事件记录表
CREATE TABLE IF NOT EXISTS elevatorAbnormality (
                                                    id                  BIGINT AUTO_INCREMENT COMMENT '异常记录ID',
                                                    elevatorId          BIGINT          NOT NULL COMMENT '关联的电梯ID（关联elevator表）',
                                                    abnormalityType     ENUM('门故障', '超载', '温度异常', '速度异常', '加速度异常', '停电', '传感器异常', '其他') NOT NULL COMMENT '异常类型',
                                                    abnormalityLevel    ENUM('轻微', '中等', '严重') NOT NULL COMMENT '异常级别',
                                                    occurrenceTime      DATETIME        NOT NULL COMMENT '异常发生时间',
                                                    recoveryTime        DATETIME            NULL COMMENT '异常恢复时间',
                                                    status              ENUM('待处理', '处理中', '已解决', '已关闭') DEFAULT '待处理' COMMENT '处理状态',
                                                    handlerId           BIGINT              NULL COMMENT '处理人ID（关联user表，管理员或维修人员）',
                                                    description         TEXT            NULL COMMENT '异常详细描述',
                                                    handlingNotes       TEXT            NULL COMMENT '处理过程记录',
                                                    createTime          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                                                    updateTime          DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
                                                    PRIMARY KEY (id),
                                                    FOREIGN KEY (elevatorId) REFERENCES elevator(id),
                                                    FOREIGN KEY (handlerId) REFERENCES user(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='电梯异常事件记录表';

-- 电梯运行参数配置表（独立管理预警阈值）
CREATE TABLE IF NOT EXISTS elevatorConfig (
                                               elevatorId          BIGINT          PRIMARY KEY COMMENT '电梯ID（关联elevator表）',
    -- 温度预警阈值
                                               cabinTempAlertThr   DECIMAL(5,2)    DEFAULT 35.0 COMMENT '轿厢温度预警阈值（默认35℃）',
                                               motorTempAlertThr   DECIMAL(5,2)    DEFAULT 60.0 COMMENT '电机温度预警阈值（默认60℃）',
    -- 速度异常阈值（按额定速度百分比计算）
                                               speedAlertPercent   DECIMAL(5,2)    DEFAULT 10.0 COMMENT '速度异常百分比阈值（默认±10%）',
    -- 加速度异常阈值
                                               accelAlertThr       DECIMAL(5,3)    DEFAULT 1.500 COMMENT '加速度异常阈值（默认1.5m/s²）',
    -- 配置生效时间
                                               effectiveTime       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '配置生效时间',
                                               updateTime          DATETIME     DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '配置更新时间',
                                               FOREIGN KEY (elevatorId) REFERENCES elevator(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='电梯运行参数配置表';