#  PropSmart

[toc]

---

## 后端计划进度安排

![image](image.png)

## 后端开发进度与开发详情

- [x] 	资源基础模块	   星期二 15:00 至	星期二 16:00
- [x] 	用户基础模块	   星期二 16:00 至	星期二 17:00	
- [x] 	公告基础模块	   星期三 8:00 至	星期三 8:40	
- [x] 	访客登记基础模块    星期三 8:00 至	星期三 8:30	
- [x] 	投诉建议模块	   星期三 8:00 至	星期三 9:00	
- [x] 	设施申请基础预订	星期三 8:00 至	星期三 9:30	
- [x] 	报修申请基础模块	星期三 8:00 至	星期三 10:00	
- [ ] 	缴费查询基础模块	星期三 8:00 至	星期三 9:00	
- [ ] 	基础后端功能完成	星期三 14:00 至	星期三 14:30	

## 资源基础

完成的表以及相关业务要求的资源对象有:

- 公共基础设施
- 楼房
- 用户
- 房产
- 公告类



公共基础设施的库表

```sql
-- ----------------------------
-- 5. 设施表
-- ----------------------------
CREATE TABLE IF NOT EXISTS facility (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    facilityName   VARCHAR(50)   NOT NULL COMMENT '设施名称',
    location       VARCHAR(100)  NULL COMMENT '位置',
    capacity       INT           NULL COMMENT '容量',
    description    TEXT          NULL COMMENT '描述',
    createTime     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) DEFAULT CHARSET=utf8mb4 COMMENT='小区设施';
```

楼房

```sql
-- ----------------------------
-- 2. 楼栋表
-- ----------------------------
CREATE TABLE IF NOT EXISTS building (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    buildingName  VARCHAR(50) UNIQUE NOT NULL COMMENT '楼栋名称/编号',
    address       VARCHAR(100)       NULL COMMENT '地理位置',
    createTime    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) DEFAULT CHARSET=utf8mb4 COMMENT='楼栋信息';
```

用户

```sql
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userPhoneNumber varchar(64)                         null comment '联系电话',
    userIdCardNumber varchar(64)                        null comment '身份证',
    userRealName varchar(64)                            null comment '用户真实姓名',
    userRole     varchar(256) default 'ROLE_USER'       not null comment '用户角色：ROLE_USER/ROLE_ADMIN/ROLE_BAN',
    isOwner       BOOLEAN                               not null default False comment '是否是业主',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '用户' collate = utf8mb4_unicode_ci;

```

房产

```sql
-- 3. 房产表
-- ----------------------------
        CREATE TABLE IF NOT EXISTS property (
            id            BIGINT AUTO_INCREMENT PRIMARY KEY,
            buildingId    BIGINT           NOT NULL COMMENT '楼栋ID',
            unitNumber    VARCHAR(20)   NOT NULL COMMENT '单元号',
            roomNumber    VARCHAR(20)   NOT NULL COMMENT '房号',
            area          DOUBLE NULL COMMENT '建筑面积',
            ownerIdentity BIGINT         NULL comment '持有者的身份证',
            createTime    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
            updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
            FOREIGN KEY (buildingId) REFERENCES building(id)
        ) DEFAULT CHARSET=utf8mb4 COMMENT='房产信息';
```

公告

```sql
-- ----------------------------
-- 6. 公告表
-- ----------------------------
CREATE TABLE IF NOT EXISTS notice (
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
```



管理员可以管理、录入各类信息

用户可以登录、注册，绑定身份证号

绑定完成后，如果小区某一个房产和当前身份证号关联，则用户自动升级为业主，并且可以通过

```java
@GetMapping("/get/idCardNumber")
@ApiOperation(value = "获取房产信息", notes = "这个函数可以通过传进来的身份证来查询名下房产, 但只是本人或者管理员")
BaseResponse<List<Property>> getPropertyByUserIdentity(@RequestParam("idCardNumber") String idCardNumber, HttpServletRequest httpServletRequest) {
    User loginUser = userService.getLoginUser(httpServletRequest);
    ThrowUtils.throwIf(ObjectUtil.isNull(loginUser), ErrorCode.NOT_LOGIN_ERROR, "未登录");
    if (!userService.isAdmin(loginUser) && !idCardNumber.equals(loginUser.getUserIdCardNumber())) {
        throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "权限不足");
    }

    List<Property> property = propertyService.listByFiled("ownerIdentity", idCardNumber);
    ThrowUtils.throwIf(CollUtil.isEmpty(property), ErrorCode.NOT_FOUND_ERROR, "房产不存在");

    return ResultUtils.success(property);
}
```

来获取到对应名下的所有房产, **注意人和房产是一对多关系**