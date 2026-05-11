CREATE DATABASE IF NOT EXISTS medical_device_trace DEFAULT CHARACTER SET utf8mb4;
USE medical_device_trace;

DROP TABLE IF EXISTS flow_record;
DROP TABLE IF EXISTS borrow_order;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS device;
DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS organization;

CREATE TABLE organization (
  org_id BIGINT PRIMARY KEY,
  org_name VARCHAR(128) NOT NULL,
  org_type VARCHAR(32) NOT NULL,
  contact_name VARCHAR(64),
  contact_phone VARCHAR(32),
  address VARCHAR(255),
  license_no VARCHAR(128)
);

CREATE TABLE user_account (
  id BIGINT PRIMARY KEY,
  username VARCHAR(64) UNIQUE NOT NULL,
  password VARCHAR(128) NOT NULL,
  role VARCHAR(32) NOT NULL,
  org_id BIGINT,
  org_name VARCHAR(128)
);

CREATE TABLE device (
  device_id BIGINT PRIMARY KEY,
  device_code VARCHAR(64) UNIQUE NOT NULL,
  device_name VARCHAR(128) NOT NULL,
  device_type VARCHAR(64),
  model VARCHAR(64),
  manufacturer VARCHAR(128),
  supplier_id BIGINT,
  supplier_name VARCHAR(128),
  batch_no VARCHAR(64),
  serial_no VARCHAR(64),
  production_date DATE,
  valid_until DATE,
  current_owner_org_id BIGINT,
  current_owner_org_name VARCHAR(128),
  current_holder_org_id BIGINT,
  current_holder_org_name VARCHAR(128),
  status VARCHAR(32) NOT NULL,
  maintenance_status VARCHAR(32),
  created_at DATETIME,
  updated_at DATETIME
);

CREATE TABLE purchase_order (
  order_id BIGINT PRIMARY KEY,
  device_id BIGINT NOT NULL,
  device_code VARCHAR(64) NOT NULL,
  supplier_id BIGINT NOT NULL,
  supplier_name VARCHAR(128) NOT NULL,
  hospital_id BIGINT NOT NULL,
  hospital_name VARCHAR(128) NOT NULL,
  apply_time DATETIME,
  approve_time DATETIME,
  receive_time DATETIME,
  status VARCHAR(32) NOT NULL,
  remark VARCHAR(255)
);

CREATE TABLE borrow_order (
  borrow_id BIGINT PRIMARY KEY,
  device_id BIGINT NOT NULL,
  device_code VARCHAR(64) NOT NULL,
  lender_hospital_id BIGINT NOT NULL,
  lender_hospital_name VARCHAR(128) NOT NULL,
  borrower_hospital_id BIGINT NOT NULL,
  borrower_hospital_name VARCHAR(128) NOT NULL,
  reason VARCHAR(255),
  expected_return_date DATE,
  apply_time DATETIME,
  approve_time DATETIME,
  lend_time DATETIME,
  receive_time DATETIME,
  return_apply_time DATETIME,
  return_confirm_time DATETIME,
  status VARCHAR(32) NOT NULL,
  remark VARCHAR(255)
);

CREATE TABLE flow_record (
  flow_id BIGINT PRIMARY KEY,
  device_id BIGINT,
  device_code VARCHAR(64) NOT NULL,
  event_type VARCHAR(64) NOT NULL,
  from_org_id BIGINT,
  from_org_name VARCHAR(128),
  to_org_id BIGINT,
  to_org_name VARCHAR(128),
  operator VARCHAR(64),
  operate_time DATETIME,
  business_id VARCHAR(64),
  business_type VARCHAR(32),
  remark VARCHAR(255),
  data_hash VARCHAR(128) NOT NULL,
  tx_hash VARCHAR(128) NOT NULL,
  block_number BIGINT,
  chain_status VARCHAR(32) NOT NULL,
  verify_status VARCHAR(32)
);

INSERT INTO organization VALUES
(1, '平台管理方', 'ADMIN', 'admin', '13800000001', 'Beijing', NULL),
(2, '市监管局', 'REGULATOR', 'regulator', '13800000002', 'Beijing', NULL),
(11, '供应商甲', 'SUPPLIER', 'supplierA', '13800000011', 'Shanghai', 'LIC-SUP-A'),
(12, '供应商乙', 'SUPPLIER', 'supplierB', '13800000012', 'Shenzhen', 'LIC-SUP-B'),
(21, '第一医院', 'HOSPITAL', 'hospA', '13800000021', 'Beijing', 'LIC-HOS-A'),
(22, '第二医院', 'HOSPITAL', 'hospB', '13800000022', 'Tianjin', 'LIC-HOS-B'),
(23, '第三医院', 'HOSPITAL', 'hospC', '13800000023', 'Hebei', 'LIC-HOS-C');

INSERT INTO user_account VALUES
(1, 'admin', 'admin123', 'ADMIN', 1, '平台管理方'),
(2, 'reg1', 'reg123', 'REGULATOR', 2, '市监管局'),
(11, 'sup1', 'sup123', 'SUPPLIER', 11, '供应商甲'),
(12, 'sup2', 'sup123', 'SUPPLIER', 12, '供应商乙'),
(21, 'hosp1', 'hosp123', 'HOSPITAL', 21, '第一医院'),
(22, 'hosp2', 'hosp123', 'HOSPITAL', 22, '第二医院'),
(23, 'hosp3', 'hosp123', 'HOSPITAL', 23, '第三医院');

INSERT INTO device VALUES
(1001, 'DEV-001', '呼吸机', '生命支持', 'RX-900', '制造商M1', 11, '供应商甲', 'B001', 'S001', '2026-01-01', '2031-01-01', 21, '第一医院', 21, '第一医院', 'IN_STOCK', NULL, NOW(), NOW()),
(1002, 'DEV-002', '监护仪', '监护', 'MH-700', '制造商M2', 12, '供应商乙', 'B002', 'S002', '2026-01-02', '2031-01-02', 21, '第一医院', 21, '第一医院', 'IN_STOCK', NULL, NOW(), NOW());

INSERT INTO purchase_order VALUES
(5001, 1001, 'DEV-001', 11, '供应商甲', 21, '第一医院', '2026-03-01 10:00:00', '2026-03-01 11:00:00', '2026-03-01 15:00:00', 'PURCHASE_RECEIVED', '演示采购链路');

INSERT INTO borrow_order VALUES
(7001, 1001, 'DEV-001', 21, '第一医院', 22, '第二医院', '应急借用', '2026-04-01', '2026-03-10 09:00:00', '2026-03-10 10:00:00', '2026-03-10 12:00:00', '2026-03-10 14:00:00', '2026-03-20 10:00:00', '2026-03-20 11:00:00', 'RETURN_CONFIRMED', '演示借还链路');

INSERT INTO flow_record VALUES
(9001, 1001, 'DEV-001', 'DEVICE_REGISTER', 11, '供应商甲', 11, '供应商甲', 'sup1', '2026-03-01 09:00:00', '1001', 'DEVICE', '设备登记', 'hash_9001', '0xaaaabbbbccccddddeeeeffff0000111122223333444455556666777788889999', 10001, 'MOCK_SUCCESS', 'PASS'),
(9002, 1001, 'DEV-001', 'PURCHASE_APPLY', 21, '第一医院', 11, '供应商甲', 'hosp1', '2026-03-01 10:00:00', '5001', 'PURCHASE', '采购申请', 'hash_9002', '0xbbbbccccddddeeeeffff0000111122223333444455556666777788889999aaaa', 10002, 'MOCK_SUCCESS', 'PASS'),
(9003, 1001, 'DEV-001', 'PURCHASE_APPROVE', 11, '供应商甲', 21, '第一医院', 'sup1', '2026-03-01 11:00:00', '5001', 'PURCHASE', '采购审批', 'hash_9003', '0xccccddddeeeeffff0000111122223333444455556666777788889999aaaabbbb', 10003, 'MOCK_SUCCESS', 'PASS'),
(9004, 1001, 'DEV-001', 'PURCHASE_RECEIVE', 11, '供应商甲', 21, '第一医院', 'hosp1', '2026-03-01 15:00:00', '5001', 'PURCHASE', '确认收货', 'hash_9004', '0xddddeeeeffff0000111122223333444455556666777788889999aaaabbbbcccc', 10004, 'MOCK_SUCCESS', 'PASS'),
(9005, 1001, 'DEV-001', 'BORROW_APPLY', 22, '第二医院', 21, '第一医院', 'hosp2', '2026-03-10 09:00:00', '7001', 'BORROW', '借用申请', 'hash_9005', '0xeeeeffff0000111122223333444455556666777788889999aaaabbbbccccdddd', 10005, 'MOCK_SUCCESS', 'PASS'),
(9006, 1001, 'DEV-001', 'BORROW_APPROVE', 21, '第一医院', 22, '第二医院', 'hosp1', '2026-03-10 10:00:00', '7001', 'BORROW', '借用审批', 'hash_9006', '0xffff0000111122223333444455556666777788889999aaaabbbbccccddddeeee', 10006, 'MOCK_SUCCESS', 'PASS'),
(9007, 1001, 'DEV-001', 'DEVICE_LEND_OUT', 21, '第一医院', 22, '第二医院', 'hosp1', '2026-03-10 12:00:00', '7001', 'BORROW', '确认借出', 'hash_9007', '0x0000111122223333444455556666777788889999aaaabbbbccccddddeeeeffff', 10007, 'MOCK_SUCCESS', 'PASS'),
(9008, 1001, 'DEV-001', 'BORROW_RECEIVE', 21, '第一医院', 22, '第二医院', 'hosp2', '2026-03-10 14:00:00', '7001', 'BORROW', '确认收到', 'hash_9008', '0x111122223333444455556666777788889999aaaabbbbccccddddeeeeffff0000', 10008, 'MOCK_SUCCESS', 'PASS'),
(9009, 1001, 'DEV-001', 'RETURN_APPLY', 22, '第二医院', 21, '第一医院', 'hosp2', '2026-03-20 10:00:00', '7001', 'BORROW', '发起归还', 'hash_9009', '0x22223333444455556666777788889999aaaabbbbccccddddeeeeffff00001111', 10009, 'MOCK_SUCCESS', 'PASS'),
(9010, 1001, 'DEV-001', 'RETURN_CONFIRM', 22, '第二医院', 21, '第一医院', 'hosp1', '2026-03-20 11:00:00', '7001', 'BORROW', '确认归还', 'hash_9010', '0x3333444455556666777788889999aaaabbbbccccddddeeeeffff000011112222', 10010, 'MOCK_SUCCESS', 'PASS');
