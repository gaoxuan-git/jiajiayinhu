

# 4 系统实现

本章结合悦芽婴护管理系统的实际开发，从家长模块、保姆模块、机构模块和管理员模块四个方面，选取各端具有代表性的功能界面与核心代码进行说明。

## 4.1 家长模块

### 4.1.1 个人中心

家长可通过注册、登录进入系统。在个人中心可查看与修改基本信息（真实姓名、联系电话、电子邮箱等）、上传头像、修改登录密码，并管理服务地址（添加、编辑、删除及设置默认地址）。个人中心界面如图4.1所示。

（此处插入图4.1 个人中心界面截图）

个人中心相关接口由 `ParentController` 提供，修改密码等核心代码如下：

```java
@ApiOperation("修改密码")
@PostMapping("/change-password")
public R<Void> changePassword(@Validated @RequestBody ChangePasswordDTO changePasswordDTO,
                              HttpServletRequest request) {
    Long userId = getCurrentUserId(request);
    parentService.changePassword(userId, changePasswordDTO);
    return R.success();
}

@ApiOperation("获取家长个人信息")
@GetMapping("/profile")
public R<ParentProfileVO> getProfile(HttpServletRequest request) {
    Long userId = getCurrentUserId(request);
    ParentProfileVO profile = parentService.getProfile(userId);
    return R.success(profile);
}
```

### 4.1.2 婴幼儿信息管理

家长可在系统中录入婴幼儿的姓名、性别、出生日期、血型、过敏史、既往病史及特殊注意事项等，并上传婴幼儿头像，建立电子档案。支持对婴幼儿信息的查看、修改与删除。婴幼儿信息管理界面如图4.2所示。

（此处插入图4.2 婴幼儿信息管理界面截图）

婴幼儿信息由 `BabyController` 处理，新增与列表查询核心代码如下：

```java
@ApiOperation("新增婴幼儿")
@PostMapping
public R<Boolean> addBaby(@Validated @RequestBody BabyDTO dto, HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 1) return R.fail("只有家长可以添加婴幼儿");
    boolean result = babyService.addBaby(dto, userInfo.getUserId());
    return result ? R.success("添加成功", true) : R.fail("添加失败");
}

@ApiOperation("获取当前用户的婴幼儿列表")
@GetMapping("/list")
public R<List<BabyVO>> getBabyList(HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    List<BabyVO> list = babyService.listByParentId(userInfo.getUserId());
    return R.success(list);
}
```

### 4.1.3 健康档案与成长记录

家长可为婴幼儿维护健康档案，记录体温、体重、身高等指标，并按日期范围查询与图表展示；同时可添加、查看、修改成长记录（饮食、睡眠、排便、活动等），支持按时间线展示与上传成长照片。健康档案与成长记录界面如图4.3、图4.4所示。

（此处插入图4.3 健康档案界面、图4.4 成长记录界面截图）

成长记录新增与按宝宝查询的核心代码如下（`BabyGrowthRecordController`）：

```java
@ApiOperation("新增成长记录")
@PostMapping("/baby/{babyId}/growth-record")
public R<Long> addGrowthRecord(@PathVariable Long babyId,
                               @Validated @RequestBody BabyGrowthRecordDTO dto,
                               HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    Byte recorderType = getRecorderType(userInfo.getUserType());
    Long recorderId = userInfo.getUserId();
    if (!growthRecordService.hasPermission(babyId, recorderType, recorderId))
        return R.fail("无权限操作该宝宝的成长记录");
    Long recordId = growthRecordService.addGrowthRecord(babyId, dto, recorderType, recorderId);
    return R.success("新增成功", recordId);
}

@ApiOperation("获取宝宝的成长记录列表")
@GetMapping("/baby/{babyId}/growth-records")
public R<List<BabyGrowthRecordVO>> getGrowthRecords(@PathVariable Long babyId,
    @RequestParam(required = false) Byte recordType, HttpServletRequest request) {
    // 权限校验后查询
    List<BabyGrowthRecordVO> list = growthRecordService.getGrowthRecordsByBabyId(babyId, recordType);
    return R.success(list);
}
```

### 4.1.4 机构与保姆浏览及预约服务

家长可搜索、浏览护理机构与机构下属保姆，查看机构详情与保姆档案（简介、从业经历、资格证书、服务评价与评分）。在选择机构、保姆及服务时间后，填写服务地址与特殊需求即可创建预约；系统根据保姆排班提供可预约时间段查询。支持查看预约列表（按状态筛选）、预约详情及取消预约。机构浏览与在线预约界面如图4.5、图4.6所示。

（此处插入图4.5 机构与保姆浏览界面、图4.6 在线预约界面截图）

预约创建与可预约时间段接口核心代码如下（`AppointmentController`）：

```java
@ApiOperation("新增预约")
@PostMapping("/appointment")
public R<Long> createAppointment(@Validated @RequestBody AppointmentDTO dto,
                                  HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 1) return R.fail("只有家长可以创建预约");
    try {
        Long appointmentId = appointmentService.createAppointment(dto, userInfo.getUserId());
        return R.success("预约创建成功", appointmentId);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("获取可预约时间段")
@GetMapping("/appointment/available-slots")
public R<List<String>> getAvailableSlots(
    @RequestParam Long institutionId, @RequestParam(required = false) Long nannyId,
    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    List<String> slots = appointmentService.getAvailableSlots(institutionId, nannyId, date);
    return R.success(slots);
}
```

### 4.1.5 护理计划与护理记录查看

机构为婴幼儿制定护理计划后，家长可查看计划名称、计划内容、起止日期及执行状态；并可按婴幼儿或护理计划维度查看保姆填写的护理记录，了解护理过程。护理计划与护理记录查看界面如图4.7所示。

（此处插入图4.7 护理计划与护理记录查看界面截图）

护理计划列表由 `CarePlanController` 提供，家长端按父用户筛选：

```java
@ApiOperation("获取护理计划列表")
@GetMapping("/care-plan/list")
public R<List<CarePlanVO>> getCarePlanList(@RequestParam(required = false) Byte status,
                                           HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    List<CarePlanVO> list;
    if (userInfo.getUserType() == 1)
        list = carePlanService.getCarePlansByParentId(userInfo.getUserId(), status);
    else if (userInfo.getUserType() == 2)
        list = carePlanService.getCarePlansByNannyId(userInfo.getUserId(), status);
    else
        return R.fail("请使用机构专用接口");
    return R.success(list);
}
```

### 4.1.6 护理评价管理

家长可对已完成的服务进行评分与文字评价，支持查看、修改与删除自己的评价；提交前系统会校验该护理计划是否已评价，避免重复。家长还可查看指定保姆的评价列表与评分统计，作为选人参考。护理评价界面如图4.8所示。

（此处插入图4.8 护理评价界面截图）

新增评价与检查是否已评价核心代码如下（`CareEvaluationController`）：

```java
@ApiOperation("新增护理评价")
@PostMapping("/care-evaluation")
public R<Long> addEvaluation(@Validated @RequestBody CareEvaluationDTO dto,
                             HttpServletRequest request) {
    String token = getTokenFromRequest(request);
    Parent parent = parentService.getParentByToken(token);
    if (parent == null) return R.fail("请先登录");
    try {
        Long id = careEvaluationService.addEvaluation(parent.getId(), dto);
        return R.success(id);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("检查护理计划是否已评价")
@GetMapping("/care-evaluation/check/{carePlanId}")
public R<Boolean> checkEvaluated(@PathVariable Long carePlanId, HttpServletRequest request) {
    Parent parent = parentService.getParentByToken(getTokenFromRequest(request));
    if (parent == null) return R.fail("请先登录");
    boolean hasEvaluated = careEvaluationService.hasEvaluated(carePlanId, parent.getId());
    return R.success(hasEvaluated);
}
```

### 4.1.7 母婴商城与订单管理

家长可浏览商品分类与商品详情、将商品加入购物车并管理数量与选中状态，从购物车创建订单并填写收货信息与备注，支持微信、支付宝、余额等支付方式；可查看订单列表与详情、取消订单、确认收货及订单统计。母婴商城与订单界面如图4.9、图4.10所示。

（此处插入图4.9 母婴商城界面、图4.10 订单管理界面截图）

订单创建与支付核心代码如下（`OrderController`）：

```java
@ApiOperation("创建订单")
@PostMapping
public R<Long> createOrder(HttpServletRequest request, @Validated @RequestBody OrderDTO dto) {
    Long parentId = getParentIdFromRequest(request);
    if (parentId == null) return R.fail("请先登录");
    try {
        Long orderId = orderService.createOrder(parentId, dto);
        return R.success(orderId);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("支付订单")
@PostMapping("/{id}/pay")
public R<Boolean> payOrder(HttpServletRequest request, @PathVariable Long id,
                           @RequestParam Byte payType) {
    Long parentId = getParentIdFromRequest(request);
    if (parentId == null) return R.fail("请先登录");
    try {
        boolean success = orderService.payOrder(parentId, id, payType);
        return R.success(success);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}
```

### 4.1.8 数据统计与消息中心

家长端数据中心展示婴幼儿相关数据与护理数据统计，并支持订单历史分页查询。消息中心支持与保姆、机构收发消息、查看对话记录及联系人列表，并提供未读计数、标记已读、删除消息等功能。数据统计与消息中心界面如图4.11所示。

（此处插入图4.11 家长端数据统计与消息中心界面截图）

---

## 4.2 保姆模块

### 4.2.1 个人信息管理

保姆可在个人中心完善真实姓名、照片、联系方式等，修改登录密码，并管理资格证书（上传、查看、删除）与个人地址。个人信息管理界面如图4.12所示。

（此处插入图4.12 保姆个人信息管理界面截图）

保姆信息与修改资料由 `NannyController` 提供，核心代码如下：

```java
@ApiOperation("获取保姆信息")
@GetMapping("/profile")
public R<Nanny> getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
    String token = parseToken(authorization);
    Nanny nanny = nannyService.getNannyByToken(token);
    if (nanny == null) return R.fail("未登录或登录已过期");
    Nanny latest = nannyService.getById(nanny.getId());
    if (latest == null) return R.fail("用户信息不存在");
    latest.setPassword(null);
    return R.success(latest);
}

@ApiOperation("修改保姆信息")
@PutMapping("/profile")
public R<String> updateProfile(@RequestBody Nanny nanny,
                                @RequestHeader(value = "Authorization", required = false) String authorization) {
    Nanny current = nannyService.getNannyByToken(parseToken(authorization));
    if (current == null) return R.fail("未登录或登录已过期");
    nanny.setId(current.getId());
    nannyService.updateProfile(nanny);
    return R.success("修改成功", null);
}
```

### 4.2.2 排班查看与调班申请

保姆可查看个人排班计划（排班日期、班次类型、工作时间、服务地点等），并提交调班申请，由机构审批。排班查看界面如图4.13所示。

（此处插入图4.13 保姆排班查看界面截图）

### 4.2.3 预约查看

保姆可查看分配给自己的预约列表，按状态（待确认、已确认、进行中、已完成、已取消）筛选，并查看预约详情（服务对象、地址、特殊需求等）。预约查看界面如图4.14所示。

（此处插入图4.14 保姆预约查看界面截图）

### 4.2.4 签到签退管理

保姆通过签到、签退记录服务开始与结束时间；签到时可提交经纬度、地址与备注。可查看某预约的签到记录列表及当日签到状态。签到签退界面如图4.15所示。

（此处插入图4.15 签到签退界面截图）

签到、签退核心代码如下（`CarePlanCheckinController`）：

```java
@ApiOperation("签到")
@PostMapping("/appointment/{id}/checkin")
public R<Long> checkin(@PathVariable Long id, @Validated @RequestBody CheckinDTO dto,
                       @RequestHeader(value = "Authorization", required = false) String authorization) {
    Nanny nanny = getCurrentNanny(authorization);
    if (nanny == null) return R.fail("未登录或登录已过期");
    try {
        Long checkinId = carePlanCheckinService.checkin(id, nanny.getId(), dto);
        return R.success("签到成功", checkinId);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("签退")
@PostMapping("/appointment/{id}/checkout")
public R<CheckoutResultVO> checkout(@PathVariable Long id, @Validated @RequestBody CheckinDTO dto,
                                    @RequestHeader(value = "Authorization", required = false) String authorization) {
    Nanny nanny = getCurrentNanny(authorization);
    if (nanny == null) return R.fail("未登录或登录已过期");
    try {
        CheckoutResultVO result = carePlanCheckinService.checkout(id, nanny.getId(), dto);
        return R.success("签退成功，" + (result.getMessage() != null ? result.getMessage() : ""), result);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}
```

### 4.2.5 护理计划查看与护理记录创建

保姆可查看分配给自己的护理计划及执行状态，在已签到前提下执行护理任务；可创建护理记录（类型含喂养、洗护、哄睡、早教、其他），填写内容、上传图片与备注，并支持修改、删除及查看今日与历史护理记录。护理记录创建界面如图4.16所示。

（此处插入图4.16 护理记录创建界面截图）

添加护理记录核心代码如下（`CareRecordController`）：

```java
@ApiOperation("添加护理记录")
@PostMapping("/care-plan/{carePlanId}/record")
public R<Long> addRecord(@PathVariable Long carePlanId, @Validated @RequestBody CareRecordDTO dto,
                         HttpServletRequest request) {
    String token = getTokenFromRequest(request);
    Nanny nanny = nannyService.getNannyByToken(token);
    if (nanny == null) return R.fail("请先登录");
    try {
        Long recordId = careRecordService.addCareRecord(carePlanId, nanny.getId(), dto);
        return R.success(recordId);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("获取今日护理记录")
@GetMapping("/care-record/today")
public R<List<CareRecordVO>> getTodayRecords(HttpServletRequest request) {
    Nanny nanny = nannyService.getNannyByToken(getTokenFromRequest(request));
    if (nanny == null) return R.fail("请先登录");
    List<CareRecordVO> records = careRecordService.getTodayRecords(nanny.getId());
    return R.success(records);
}
```

### 4.2.6 评价查看与回复

保姆可查看家长对自己的评价列表与评分统计（平均分、各分数段占比等），并对评价进行回复。评价查看与回复界面如图4.17所示。

（此处插入图4.17 保姆评价查看与回复界面截图）

### 4.2.7 消息中心

保姆可通过消息中心与家长、所属机构收发消息、查看对话记录与联系人列表，并支持未读计数、标记已读、删除消息等操作。

---

## 4.3 机构模块

### 4.3.1 机构信息管理

机构管理员可维护机构名称、法人代表、联系人、联系电话、邮箱、地址及机构简介，并管理服务地址。机构信息管理界面如图4.18所示。

（此处插入图4.18 机构信息管理界面截图）

机构登录与资料修改由 `InstitutionController` 提供，核心代码如下：

```java
@ApiOperation("机构登录")
@PostMapping("/login")
public R<Map<String, String>> login(@RequestBody Institution institution) {
    String token = institutionService.login(institution.getUsername(), institution.getPassword());
    Map<String, String> data = new HashMap<>();
    data.put("token", token);
    return R.success(data);
}

@ApiOperation("获取机构信息")
@GetMapping("/profile")
public R<Institution> getProfile(@RequestHeader(value = "Authorization", required = false) String authorization) {
    Institution institution = institutionService.getInstitutionByToken(parseToken(authorization));
    if (institution == null) return R.fail("未登录或登录已过期");
    institution.setPassword(null);
    return R.success(institution);
}
```

### 4.3.2 保姆管理

机构可审核保姆注册申请，查看、修改与删除保姆档案，并查看保姆绩效数据。保姆管理界面如图4.19所示。

（此处插入图4.19 机构保姆管理界面截图）

### 4.3.3 排班管理

机构可为保姆创建单个或批量排班，设置排班日期、班次类型与工作时间；支持日历视图查看排班、按保姆与日期查询，以及调班申请的审批；创建排班时进行冲突检测。排班管理界面如图4.20所示。

（此处插入图4.20 排班管理界面截图）

排班创建与批量创建核心代码如下（`NannyScheduleController`）：

```java
@ApiOperation("新增排班")
@PostMapping
public R<String> createSchedule(@RequestBody NannySchedule schedule,
                                @RequestHeader(value = "Authorization", required = false) String authorization) {
    Institution institution = getInstitutionFromToken(authorization);
    if (institution == null) return R.fail("未登录或登录已过期");
    nannyScheduleService.createSchedule(institution.getId(), schedule);
    return R.success("排班创建成功", null);
}

@ApiOperation("批量新增排班")
@PostMapping("/batch")
public R<Integer> batchCreateSchedule(@RequestBody List<NannySchedule> schedules,
                                       @RequestHeader(value = "Authorization", required = false) String authorization) {
    Institution institution = getInstitutionFromToken(authorization);
    if (institution == null) return R.fail("未登录或登录已过期");
    int successCount = nannyScheduleService.batchCreateSchedule(institution.getId(), schedules);
    return R.success("成功创建 " + successCount + " 条排班", successCount);
}
```

### 4.3.4 预约管理

机构可查看全部预约列表，按状态、服务类型、日期、关键词筛选；可确认或取消预约，并为预约分配保姆；支持预约统计。预约管理界面如图4.21所示。

（此处插入图4.21 机构预约管理界面截图）

机构修改预约状态与分配保姆核心代码如下（`AppointmentController`）：

```java
@ApiOperation("修改预约状态")
@PutMapping("/appointment/{id}/status")
public R<Boolean> updateAppointmentStatus(@PathVariable Long id, @RequestParam Byte status,
                                           HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 3) return R.fail("只有机构可以修改预约状态");
    try {
        boolean result = appointmentService.updateAppointmentStatus(id, status, userInfo.getUserId());
        return result ? R.success("状态更新成功", true) : R.fail("状态更新失败");
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("为预约分配保姆")
@PostMapping("/appointment/{id}/assign-nanny")
public R<Boolean> assignNanny(@PathVariable Long id, @RequestParam Long nannyId,
                               HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 3) return R.fail("只有机构可以分配保姆");
    try {
        boolean result = appointmentService.assignNanny(id, nannyId, userInfo.getUserId());
        return result ? R.success("分配成功", true) : R.fail("分配失败");
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}
```

### 4.3.5 护理计划管理

机构可根据预约为婴幼儿制定护理计划（计划名称、内容、起止日期），并分配执行保姆、管理计划状态（待执行、进行中、已完成、已取消）。护理计划管理界面如图4.22所示。

（此处插入图4.22 护理计划管理界面截图）

新增护理计划与分配保姆核心代码如下（`CarePlanController`）：

```java
@ApiOperation("新增护理计划")
@PostMapping("/care-plan")
public R<Long> createCarePlan(@Validated @RequestBody CarePlanDTO dto, HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 3) return R.fail("只有机构可以创建护理计划");
    try {
        Long planId = carePlanService.createCarePlan(dto, userInfo.getUserId());
        return R.success("创建成功", planId);
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}

@ApiOperation("为护理计划分配保姆")
@PostMapping("/care-plan/{id}/assign-nanny")
public R<Boolean> assignNanny(@PathVariable Long id, @RequestParam Long nannyId,
                               HttpServletRequest request) {
    UserInfoVO userInfo = getCurrentUser(request);
    if (userInfo == null) return R.fail("用户未登录");
    if (userInfo.getUserType() != 3) return R.fail("只有机构可以分配保姆");
    try {
        boolean result = carePlanService.assignNanny(id, nannyId, userInfo.getUserId());
        return result ? R.success("分配成功", true) : R.fail("分配失败");
    } catch (RuntimeException e) {
        return R.fail(e.getMessage());
    }
}
```

### 4.3.6 护理记录查看与评价监管

机构可按保姆、婴幼儿、护理类型等条件查看护理记录；可查看机构下保姆收到的评价与评分统计，用于服务质量监管。

### 4.3.7 商城管理、财务管理与数据统计

机构可管理商品（增删改、上下架）、订单（列表、发货、统计）与库存（出入库、预警）；可录入收支记录、按日期统计与月度汇总，并导出财务报表。仪表板展示服务数据、保姆绩效、评价与财务概况。相关界面如图4.23所示。

（此处插入图4.23 机构商城与仪表板界面截图）

---

## 4.4 管理员模块

### 4.4.1 管理员账号与用户管理

系统管理员可管理管理员账号（增删改查、分页）；对家长、机构、保姆进行列表查看、详情查看、启用/禁用及删除等操作，并可从家长管理跳转查看其名下婴幼儿信息。家长管理界面如图4.24所示。

（此处插入图4.24 管理员家长管理界面截图）

### 4.4.2 机构与保姆管理

管理员可查看机构详情、启用或禁用机构、删除机构；对保姆进行详情查看、状态修改（在职、离职、待审核等）与删除，实施平台级监管。

### 4.4.3 婴幼儿与健康档案管理

管理员可查看、编辑与删除所有婴幼儿信息，支持按姓名、性别等筛选；可多维度查询健康档案与成长记录，监督数据质量。

### 4.4.4 商品分类与商品管理

管理员可维护商品分类（树形结构、启用/禁用）；对平台所有商品进行查看、修改、删除、上架/下架及批量操作，实现统一审核与管理。

### 4.4.5 公告管理与消息中心

管理员可创建、编辑、删除系统公告并控制发布与下线；通过消息中心与家长、保姆、机构沟通，查看对话与未读消息。

### 4.4.6 数据统计与报表

管理员首页仪表板展示用户统计（各角色数量）、机构统计、订单统计与收入统计等；支持按时间范围生成订单、收入、用户等报表。数据统计核心代码如下（`AdminDashboardController`）：

```java
@ApiOperation("获取管理后台首页数据")
@GetMapping("/dashboard")
public R<IAdminDashboardService.DashboardVO> getDashboard(HttpServletRequest request) {
    if (!isAdmin(request)) return R.fail("无权限访问");
    IAdminDashboardService.DashboardVO data = adminDashboardService.getDashboardData();
    return R.success(data);
}

@ApiOperation("获取用户统计")
@GetMapping("/user-stats")
public R<IAdminDashboardService.UserStatsVO> getUserStats(HttpServletRequest request) {
    if (!isAdmin(request)) return R.fail("无权限访问");
    IAdminDashboardService.UserStatsVO stats = adminDashboardService.getUserStats();
    return R.success(stats);
}
```

数据统计界面如图4.25所示。

（此处插入图4.25 管理员数据统计界面截图）

---

## 4.5 本章小结

因篇幅所限，本章未对悦芽婴护管理系统的全部功能逐一展开，而是按家长、保姆、机构、管理员四类角色，选取各端具有代表性的功能，结合界面说明与关键代码，对系统实现进行了有重点的阐述。家长端重点包括个人中心、婴幼儿与健康档案、成长记录、预约服务、护理计划与记录查看、护理评价、母婴商城与订单、数据统计与消息中心；保姆端包括个人信息、排班与预约查看、签到签退、护理计划执行与护理记录创建、评价查看与回复及消息中心；机构端包括机构信息、保姆与排班、预约与护理计划管理、护理记录与评价监管、商城与财务及数据统计；管理员端包括用户与机构、保姆、婴幼儿及健康档案管理、商品与分类、公告、消息及平台数据统计与报表。所述功能均基于 Spring Boot 与 Vue 前后端分离架构实现，通过 RESTful 接口与统一响应封装完成前后端协作，为系统测试与后续维护提供了清晰实现依据。

