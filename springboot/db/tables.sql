SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------------------------------------------------- Core tables below
-- Tables contained in this secrtion: admin, dorm_manager, dorm_compound, dorm_build, student, student_checkin, dorm_room, adjust_room, out_live, quit_room
-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `gender` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '男' COMMENT '性别',
  `age` int(0) NOT NULL COMMENT '年龄',
  `phone_num` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for dorm_manager
-- ----------------------------
DROP TABLE IF EXISTS `dorm_manager`;
CREATE TABLE `dorm_manager`  (
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '123456' COMMENT '密码',
  `dormbuild_id` int(0) NULL DEFAULT NULL COMMENT '所管理的宿舍楼栋号（楼栋宿管）',
  -- `compound_id` int(0) NULL DEFAULT NULL COMMENT '所管理的园区ID（园区宿管）',
  -- `manager_type` enum('楼栋宿管','园区宿管') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '楼栋宿管' COMMENT '宿管类型',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '名字',
  `gender` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '男' COMMENT '性别',
  `age` int(0) NOT NULL COMMENT '年龄',
  `phone_num` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for dorm_compound
-- ----------------------------
DROP TABLE IF EXISTS `dorm_compound`;
CREATE TABLE `dorm_compound`  (
  `compound_id` int(0) NOT NULL AUTO_INCREMENT COMMENT '园区ID',
  `compound_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '园区名称',
  `campus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '所属校区',
  `compound_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '园区备注',
  PRIMARY KEY (`compound_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for dorm_build
-- ----------------------------
DROP TABLE IF EXISTS `dorm_build`;
CREATE TABLE `dorm_build`  (
  `dormbuild_id` int(0) NOT NULL COMMENT '宿舍楼号码',
  `dormbuild_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '宿舍楼名称',
  `dormbuild_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '宿舍楼备注',
  `compound_id` int(0) NOT NULL COMMENT '所属园区ID',
  -- `campus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '园区',
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_dormbuild_id` (`dormbuild_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '学号',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '123456' COMMENT '密码',
  `age` int(0) UNSIGNED NOT NULL COMMENT '年龄',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `gender` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '男' COMMENT '性别',
  `phone_num` varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
  PRIMARY KEY (`username`) USING BTREE,
  UNIQUE INDEX `stu_num`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for student_checkin
-- ----------------------------
DROP TABLE IF EXISTS `student_checkin`;
CREATE TABLE `student_checkin` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `student_username` varchar(255) NOT NULL COMMENT '学生学号',
  `student_name` varchar(255) NOT NULL COMMENT '学生姓名',
  `action_time` datetime NOT NULL COMMENT '操作时间',
  `dormbuild_id` int DEFAULT NULL COMMENT '宿舍楼号',
  `dormroom_id` int DEFAULT NULL COMMENT '房间号',
  `bed_number` int DEFAULT NULL COMMENT '床位号',
  `operator` varchar(255) DEFAULT NULL COMMENT '操作人',
  `remarks` text COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生住宿操作记录表';
-- ----------------------------
-- Table structure for dorm_room
-- ----------------------------
DROP TABLE IF EXISTS `dorm_room`;
CREATE TABLE `dorm_room`  (
  `dormroom_id` int(0) NOT NULL COMMENT '宿舍房间号',
  `dormbuild_id` int(0) NOT NULL COMMENT '宿舍楼号',
  `floor_num` int(0) NOT NULL COMMENT '楼层',
  `max_capacity` int(0) NOT NULL DEFAULT 4 COMMENT '房间最大入住人数',
  `current_capacity` int(0) NOT NULL DEFAULT 0 COMMENT '当前房间入住人数',
  `first_bed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '一号床位',
  `second_bed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '二号床位',
  `third_bed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '三号床位',
  `fourth_bed` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '四号床位',
  PRIMARY KEY (`dormroom_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for adjust_room
-- ----------------------------
DROP TABLE IF EXISTS `adjust_room`;
CREATE TABLE `adjust_room`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `currentroom_id` int(0) NOT NULL COMMENT '当前房间',
  `currentbed_id` int(0) NOT NULL COMMENT '当前床位号',
  `towardsroom_id` int(0) NOT NULL COMMENT '目标房间',
  `towardsbed_id` int(0) NOT NULL COMMENT '目标床位号',
  `state` enum('未处理','通过','驳回') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未处理' COMMENT '申请状态',
  `apply_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '申请时间',
  `finish_time` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for out_live
-- ----------------------------
DROP TABLE IF EXISTS `out_live`;
CREATE TABLE `out_live` (
                            `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
                            `username` VARCHAR(255) NOT NULL COMMENT '学号',
                            `name` VARCHAR(255) NOT NULL COMMENT '学生姓名',
                            `reason` VARCHAR(255) NOT NULL COMMENT '外出住宿原因',
                            `start_date` DATE NOT NULL COMMENT '开始日期',
                            `end_date` DATE NOT NULL COMMENT '结束日期',
                            `state` ENUM('未处理', '通过', '驳回') DEFAULT '未处理' COMMENT '审批状态',
                            `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                            `finish_time` DATETIME DEFAULT NULL COMMENT '处理时间',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='学生外出住宿申请表';
-- ----------------------------
-- Table structure for quit_room
-- ----------------------------
DROP TABLE IF EXISTS `quit_room`;
CREATE TABLE `quit_room` (
                             `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                             `username` VARCHAR(255) NOT NULL COMMENT '学号',
                             `name` VARCHAR(255) NOT NULL COMMENT '学生姓名',
                             `dormroom_id` INT NOT NULL COMMENT '当前宿舍号',
                             `bed_number` INT NOT NULL COMMENT '床位号（1~4）',
                             `reason` VARCHAR(255) NOT NULL COMMENT '退宿原因',
                             `state` ENUM('未处理', '通过', '驳回') NOT NULL DEFAULT '未处理' COMMENT '申请状态',
                             `apply_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
                             `finish_time` DATETIME DEFAULT NULL COMMENT '处理时间',
                             PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8 COMMENT='学生退宿申请表';
-- ---------------------------------------------------------------------------------------------------------------------- Miscellaneous tables below
-- Tables contained in this secrtion: repair, notice, visitor, hygiene_check, violation_record and dorm_evaluation.
-- ----------------------------
-- Table structure for repair
-- ----------------------------
DROP TABLE IF EXISTS `repair`;
CREATE TABLE `repair`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '订单编号',
  `repairer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '报修人',
  `dormbuild_id` int(0) NOT NULL COMMENT '报修宿舍楼',
  `dormroom_id` int(0) NOT NULL COMMENT '报修宿舍房间号',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表单标题',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表单内容',
  `state` enum('完成','未完成') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '未完成' COMMENT '订单状态（是否维修完成）',
  `order_buildtime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '订单创建时间',
  `order_finishtime` datetime(0) NULL DEFAULT NULL COMMENT '订单完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1413525505 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主题',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '内容',
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '作者',
  `release_time` datetime(0) NOT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
-- ----------------------------
-- Table structure for visitor
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '姓名',
  `gender` enum('男','女') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '男' COMMENT '性别',
  `phone_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '电话',
  `origin_city` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '来源城市',
  `visit_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '来访时间',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '事情',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- 新增表结构 - 卫生管理
-- ----------------------------
DROP TABLE IF EXISTS `hygiene_check`;
CREATE TABLE `hygiene_check` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dormroom_id` int NOT NULL COMMENT '宿舍房间号',
  `dormbuild_id` int NOT NULL COMMENT '宿舍楼号',
  `check_date` date NOT NULL COMMENT '检查日期',
  `door_window_score` int DEFAULT 0 COMMENT '门窗得分(0-20)',
  `item_placement_score` int DEFAULT 0 COMMENT '物品放置得分(0-20)',
  `bedding_score` int DEFAULT 0 COMMENT '被褥得分(0-20)',
  `cleanliness_score` int DEFAULT 0 COMMENT '蜘蛛网积灰得分(0-20)',
  `overall_score` int DEFAULT 0 COMMENT '寝室整体效果得分(0-20)',
  `total_score` int DEFAULT 0 COMMENT '总分',
  `grade` enum('优','良','中','差') DEFAULT '中' COMMENT '等级',
  `checker` varchar(255) NOT NULL COMMENT '检查人员',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_date` (`dormroom_id`, `check_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='卫生检查记录表';

-- ----------------------------
-- 新增表结构 - 违纪管理
-- ----------------------------
DROP TABLE IF EXISTS `violation_record`;
CREATE TABLE `violation_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dormroom_id` int NOT NULL COMMENT '宿舍房间号',
  `dormbuild_id` int NOT NULL COMMENT '宿舍楼号',
  `student_username` varchar(255) NOT NULL COMMENT '违纪学生学号',
  `student_name` varchar(255) NOT NULL COMMENT '违纪学生姓名',
  `violation_type` varchar(255) NOT NULL COMMENT '违纪类型',
  `violation_description` text NOT NULL COMMENT '违纪描述',
  `violation_date` datetime NOT NULL COMMENT '违纪时间',
  `penalty_score` int DEFAULT 0 COMMENT '扣分',
  `status` enum('待处理','已处理','已驳回') DEFAULT '待处理' COMMENT '处理状态',
  `reporter` varchar(255) NOT NULL COMMENT '上报人员',
  `handler` varchar(255) DEFAULT NULL COMMENT '处理人员',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_result` text COMMENT '处理结果',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_student` (`student_username`),
  KEY `idx_room` (`dormroom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='违纪记录表';

-- ----------------------------
-- 新增表结构 - 寝室评比
-- ----------------------------
DROP TABLE IF EXISTS `dorm_evaluation`;
CREATE TABLE `dorm_evaluation` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dormroom_id` int NOT NULL COMMENT '宿舍房间号',
  `dormbuild_id` int NOT NULL COMMENT '宿舍楼号',
  `evaluation_period` varchar(50) NOT NULL COMMENT '评比周期(如:2024-2025-1)',
  `hygiene_score` decimal(5,2) DEFAULT 0.00 COMMENT '卫生得分',
  `gpa_score` decimal(5,2) DEFAULT 0.00 COMMENT '人均学分绩点',
  `total_score` decimal(5,2) DEFAULT 0.00 COMMENT '综合得分',
  `star_level` enum('无星','二星','三星','四星','五星') DEFAULT '无星' COMMENT '星级',
  `evaluation_type` enum('星级寝室','院文明寝室') DEFAULT '星级寝室' COMMENT '评比类型',
  `status` enum('待评选','已评选','已发布') DEFAULT '待评选' COMMENT '状态',
  `evaluator` varchar(255) DEFAULT NULL COMMENT '评选人员',
  `evaluation_time` datetime DEFAULT NULL COMMENT '评选时间',
  `remarks` text COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_period` (`dormroom_id`, `evaluation_period`),
  KEY `idx_building` (`dormbuild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='寝室评比表';

-- ----------------------------
-- Table structure for dorm_evaluation
-- ----------------------------
DROP TABLE IF EXISTS `dorm_evaluation`;
CREATE TABLE `dorm_evaluation` (
                                   `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   `dormroom_id` int NOT NULL COMMENT '宿舍房间号',
                                   `dormbuild_id` int NOT NULL COMMENT '宿舍楼号',
                                   `evaluation_period` varchar(50) NOT NULL COMMENT '评比周期(如:2024-2025-1)',
                                   `hygiene_score` decimal(5,2) DEFAULT 0.00 COMMENT '卫生得分',
                                   `gpa_score` decimal(5,2) DEFAULT 0.00 COMMENT '人均学分绩点',
                                   `total_score` decimal(5,2) DEFAULT 0.00 COMMENT '综合得分',
                                   `star_level` enum('无星','二星','三星','四星','五星') DEFAULT '无星' COMMENT '星级',
                                   `evaluation_type` enum('星级寝室','院文明寝室') DEFAULT '星级寝室' COMMENT '评比类型',
                                   `status` enum('待评选','已评选','已发布') DEFAULT '待评选' COMMENT '状态',
                                   `evaluator` varchar(255) DEFAULT NULL COMMENT '评选人员',
                                   `evaluation_time` datetime DEFAULT NULL COMMENT '评选时间',
                                   `remarks` text COMMENT '备注',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`),
                                   UNIQUE KEY `uk_room_period` (`dormroom_id`, `evaluation_period`),
                                   KEY `idx_building` (`dormbuild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='寝室评比表';
-- ----------------------------
-- Table structure for batch_checkin_record
-- ----------------------------
DROP TABLE IF EXISTS `batch_checkin_record`;
CREATE TABLE `batch_checkin_record` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `operator` varchar(50) NOT NULL COMMENT '操作人',
  `total_count` int NOT NULL COMMENT '总数量',
  `success_count` int DEFAULT 0 COMMENT '成功数量',
  `fail_count` int DEFAULT 0 COMMENT '失败数量',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/COMPLETED/FAILED',
  `strategy` varchar(20) DEFAULT 'CONTINUE_ON_ERROR' COMMENT '处理策略',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_no` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量入住批次记录表';

-- ----------------------------
-- Table structure for batch_checkin_detail
-- ----------------------------
DROP TABLE IF EXISTS `batch_checkin_detail`;
CREATE TABLE `batch_checkin_detail` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `student_username` varchar(50) NOT NULL COMMENT '学号',
  `student_name` varchar(50) DEFAULT NULL COMMENT '学生姓名',
  `dormbuild_id` int NOT NULL COMMENT '楼栋ID',
  `dormroom_id` int NOT NULL COMMENT '房间号',
  `bed_number` int NOT NULL COMMENT '床位号',
  `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED/SKIPPED',
  `error_message` text COMMENT '错误信息',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `remark` text COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_student_username` (`student_username`),
  KEY `idx_dormbuild_id` (`dormbuild_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量入住详情记录表';

-- ----------------------------
-- Table structure for batch_student_import_record
-- ----------------------------
DROP TABLE IF EXISTS `batch_student_import_record`;
CREATE TABLE `batch_student_import_record` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `batch_no` varchar(32) NOT NULL COMMENT '批次号',
    `operator` varchar(50) NOT NULL COMMENT '操作人',
    `total_count` int NOT NULL COMMENT '总数量',
    `success_count` int DEFAULT 0 COMMENT '成功数量',
    `fail_count` int DEFAULT 0 COMMENT '失败数量',
    `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/COMPLETED/FAILED',
    `strategy` varchar(20) DEFAULT 'CONTINUE_ON_ERROR' COMMENT '处理策略',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
    `remark` text COMMENT '备注',
        PRIMARY KEY (`id`),
    UNIQUE KEY `uk_batch_no_student` (`batch_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量导入学生批次记录表';

-- ----------------------------
-- Table structure for batch_student_import_detail
-- ----------------------------
DROP TABLE IF EXISTS `batch_student_import_detail`;
CREATE TABLE `batch_student_import_detail` (
    `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `batch_no` varchar(32) NOT NULL COMMENT '批次号',
    `username` varchar(50) NOT NULL COMMENT '   学号',
    `name` varchar(50) DEFAULT NULL COMMENT '姓名',
    `gender` enum('男','女') DEFAULT NULL COMMENT '性别',
    `age` int DEFAULT NULL COMMENT '年龄',
    `phone_num` varchar(11) DEFAULT NULL COMMENT '手机号',
    `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
    `status` varchar(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED/SKIPPED',
    `error_message` text COMMENT '错误信息',
    `process_time` datetime DEFAULT NULL COMMENT '处理时间',
    `remark` text COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_batch_no_student` (`batch_no`),
    KEY `idx_username_student` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量导入学生详情记录表';

SET FOREIGN_KEY_CHECKS = 1;



