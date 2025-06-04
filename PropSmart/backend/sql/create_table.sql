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