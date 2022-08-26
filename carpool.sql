/*
SQLyog Community v13.1.6 (64 bit)
MySQL - 8.0.25 : Database - carpool
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`carpool` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `carpool`;

/*Table structure for table `carpool_form` */

CREATE TABLE `carpool_form` (
  `id` varchar(50) NOT NULL COMMENT '拼车单编号',
  `student_id` varchar(10) NOT NULL COMMENT '学号',
  `origin` varchar(20) NOT NULL COMMENT '出发地',
  `destination` varchar(20) NOT NULL COMMENT '目的地',
  `category` varchar(10) NOT NULL COMMENT '目的地类别',
  `is_limit` tinyint(1) NOT NULL COMMENT '是否需要信用需求',
  `credit_floor` int NOT NULL DEFAULT '0' COMMENT '信用下限',
  `people_number` int NOT NULL DEFAULT '1' COMMENT '单子目前拼车人数',
  `need_count` int NOT NULL COMMENT '需要拼车人数',
  `remark` varchar(30) NOT NULL COMMENT '备注',
  `deadline` datetime NOT NULL COMMENT '截止时间',
  `status` varchar(15) NOT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `carpool_form` */

/*Table structure for table `destination` */

CREATE TABLE `destination` (
  `category` varchar(10) NOT NULL COMMENT '目的地的种类',
  `place` varchar(20) NOT NULL COMMENT '目的地',
  `count` int NOT NULL DEFAULT '0' COMMENT '计数',
  PRIMARY KEY (`place`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `destination` */

insert  into `destination`(`category`,`place`,`count`) values 
('医院','中医医院',0),
('公园','爱心公园',0),
('公园','福州国家森林公园',0),
('大学','福州大学',0),
('医院','福州市第一医院',0),
('机场','福州机场',0),
('机场','福州长乐机场',0),
('大学','福建医科大学',0),
('医院','福建医科大学附属第一医院',0),
('大学','福建师范大学',0),
('公园','船政文化景区',0);

/*Table structure for table `evaluate` */

CREATE TABLE `evaluate` (
  `form_id` varchar(255) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL COMMENT '发起评价的人',
  `target` varchar(255) DEFAULT NULL COMMENT '被评价的人',
  `starcount` int DEFAULT NULL COMMENT '*星评价'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `evaluate` */

/*Table structure for table `group` */

CREATE TABLE `group` (
  `form_id` varchar(50) NOT NULL COMMENT '拼车单编号',
  `student_id` varchar(15) NOT NULL COMMENT '学号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `group` */

/*Table structure for table `user` */

CREATE TABLE `user` (
  `student_id` varchar(15) NOT NULL COMMENT '学号',
  `name` varchar(10) NOT NULL COMMENT '昵称',
  `phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'MA==' COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `credit_points` int NOT NULL DEFAULT '100' COMMENT '信用分',
  `head_portrait` varchar(100) NOT NULL COMMENT '头像',
  `current_form_count` int NOT NULL DEFAULT '0' COMMENT '相关订单数',
  `history_form_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `user` */

insert  into `user`(`student_id`,`name`,`phone`,`password`,`credit_points`,`head_portrait`,`current_form_count`,`history_form_count`) values 
('1000000','呦西','MTg5NTk4MjI3Njk=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/526c4618ffa145abbdbe9db828b44d27.png',0,0),
('10086','好哇','MTgyMDg4OTQ2NDI=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('102101227','艺','MTg5NTk4MjI3Njk=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/92426f0c4bfe4abdaed0b9976fc1870f.jpg',0,0),
('1234567','张三','MTg5NTk4MjI3Njk=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/ffd6501b2a2b459ba2f39a99134d36db.jpg',0,0),
('222000106','林登万','MTU4NTkxMzE1NDY=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/d4850a3eadb841a986f38241b4c80dc9.jpg',0,0),
('222000425','yang','MTgyMTM1OTgwNDU=','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/ce15bda9498b4523a7edda584b93d1a6.jpg',0,0),
('2222222','2222222','MA==','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('3333333','3333333','MA==','MTIzNDU2',50,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('4444444','4444444','MA==','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('5555555','5555555','MA==','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('6666666','6666666','MA==','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0),
('7777777','7777777','MA==','MTIzNDU2',100,'https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/65085d3427024923aec6bc91064bcc13.png',0,0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
