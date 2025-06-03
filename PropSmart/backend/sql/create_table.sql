
use JCD;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userPhoneNumber varchar(64)                         null comment '联系电话',
    userRole     varchar(256) default 'ROLE_USER'       not null comment '用户角色：ROLE_USER/ROLE_OWNER/ROLE_ADMIN/ROLE_BAN',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户' collate = utf8mb4_unicode_ci;

-- ----------------------------
-- 2. 楼栋表
-- ----------------------------
CREATE TABLE IF NOT EXISTS buildings (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    buildingName  VARCHAR(50) UNIQUE NOT NULL COMMENT '楼栋名称/编号',
    address       VARCHAR(100)       NULL COMMENT '地理位置',
    createTime    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) DEFAULT CHARSET=utf8mb4 COMMENT='楼栋信息';

-- ----------------------------
-- 3. 房产表
-- ----------------------------
CREATE TABLE IF NOT EXISTS properties (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    buildingId   INT           NOT NULL COMMENT '楼栋ID',
    unitNumber   VARCHAR(20)   NOT NULL COMMENT '单元号',
    roomNumber   VARCHAR(20)   NOT NULL COMMENT '房号',
    area         DECIMAL(10,2) NULL COMMENT '建筑面积',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    FOREIGN KEY (buildingId) REFERENCES buildings(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='房产信息';

-- ----------------------------
-- 4. 用户-房产关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS userProperty (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId      BIGINT       NOT NULL COMMENT '用户ID',
    propertyId  INT          NOT NULL COMMENT '房产ID',
    isOwner     TINYINT(1) DEFAULT 0 COMMENT '是否为主业主',
    createTime  DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    updateTime   datetime  default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (propertyId) REFERENCES properties(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='用户-房产关联';

-- ----------------------------
-- 8. 报修申请表
-- ----------------------------
CREATE TABLE IF NOT EXISTS repairOrder (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId             BIGINT          NOT NULL COMMENT '报修用户ID',
    propertyId         INT             NOT NULL COMMENT '报修房产ID',
    description        TEXT            NOT NULL COMMENT '问题描述',
    status             ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '状态',
    createTime         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime         DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    reviewerId         BIGINT          NULL COMMENT '处理人ID（管理员）',
    reviewTime         DATETIME        NULL COMMENT '完成时间',
    reviewMessage      VARCHAR(512)    NULL COMMENT '审批原因'
) DEFAULT CHARSET=utf8mb4 COMMENT='报修申请';


-- ----------------------------
-- 7. 账单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS bills (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    propertyId     INT             NOT NULL COMMENT '房产ID',
    type           ENUM('property_fee', 'water', 'electricity', 'other') NOT NULL COMMENT '费用类型',
    amount         DECIMAL(10,2)   NOT NULL COMMENT '金额',
    deadline       DATE            NOT NULL COMMENT '截止日期',
    status         ENUM('unpaid', 'paid', 'overdue') DEFAULT 'unpaid' COMMENT '缴费状态',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime     datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    paidTime       DATETIME        NULL COMMENT '缴费时间',
    FOREIGN KEY (propertyId) REFERENCES properties(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='费用账单';

-- ----------------------------
-- 6. 公告表
-- ----------------------------
CREATE TABLE IF NOT EXISTS notices (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(100)  NOT NULL COMMENT '标题',
    content       TEXT          NOT NULL COMMENT '内容',
    publishTime   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    expireTime    DATETIME      NULL COMMENT '过期时间',
    publisherId   BIGINT        NOT NULL COMMENT '发布者ID（管理员）',
    createTime    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    FOREIGN KEY (publisherId) REFERENCES user(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='小区公告';

-- ----------------------------
-- 9. 投诉建议表
-- ----------------------------
CREATE TABLE IF NOT EXISTS complaintSuggestion (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId           BIGINT          NOT NULL COMMENT '提交用户ID',
    content          TEXT            NOT NULL COMMENT '内容',
    type             ENUM('complaint', 'suggestion') NOT NULL COMMENT '类型',
    status           ENUM('pending', 'success', 'rejected') DEFAULT 'pending' COMMENT '状态',
    createTime       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    reviewerId        BIGINT          NULL COMMENT '处理人ID（管理员）',
    reviewMessage     TEXT            NULL COMMENT '回复内容',
    reviewTime     DATETIME        NULL COMMENT '回复时间',
    FOREIGN KEY (userId) REFERENCES user(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议';

-- ----------------------------
-- 10. 访客表
-- ----------------------------
CREATE TABLE IF NOT EXISTS visitor (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId           BIGINT          NOT NULL COMMENT '被访用户ID',
    visitorName      VARCHAR(50)     NOT NULL COMMENT '访客姓名',
    idNumber         VARCHAR(18)     NULL COMMENT '身份证号',
    visitReason      VARCHAR(200)    NULL COMMENT '访问原因',
    visitTime        DATETIME        NOT NULL COMMENT '预计访问时间',
    duration         INT             NULL COMMENT '预计时长（小时）',
    reviewStatus     ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审批状态',
    reviewerId       BIGINT          NULL COMMENT '审批人ID（管理员）',
    reviewTime       DATETIME        NULL COMMENT '审批时间',
    reviewMessage    VARCHAR(512)    NULL COMMENT '审批理由',
    passCode         VARCHAR(20)     NULL COMMENT '电子通行证',
    createTime       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime       DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (reviewerId) REFERENCES user(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='访客管理';

-- ----------------------------
-- 5. 设施表
-- ----------------------------
CREATE TABLE IF NOT EXISTS facilities (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    facilityName   VARCHAR(50)   NOT NULL COMMENT '设施名称',
    location       VARCHAR(100)  NULL COMMENT '位置',
    capacity       INT           NULL COMMENT '容量',
    description    TEXT          NULL COMMENT '描述',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) DEFAULT CHARSET=utf8mb4 COMMENT='小区设施';

-- ----------------------------
-- 11. 设施预订表
-- ----------------------------
CREATE TABLE IF NOT EXISTS facilityReservation (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId           BIGINT          NOT NULL COMMENT '预订用户ID',
    facilityId       INT             NOT NULL COMMENT '设施ID',
    ReservationTime  DATETIME        NOT NULL COMMENT '预订时间',
    duration         INT             NOT NULL COMMENT '时长（小时）',
    status           ENUM('pending', 'success', 'rejected') DEFAULT 'pending' COMMENT '状态',
    createTime       DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    reviewerId       BIGINT          NULL COMMENT '审批人ID（管理员）',
    reviewMessage    VARCHAR(512)    NULL COMMENT '同意/拒绝原因',
    reviewTime     DATETIME        NULL COMMENT '审批时间',
    FOREIGN KEY (userId) REFERENCES user(id),
    FOREIGN KEY (facilityId) REFERENCES facilities(id),
    FOREIGN KEY (reviewerId) REFERENCES user(id)
) DEFAULT CHARSET=utf8mb4 COMMENT='设施预订';




