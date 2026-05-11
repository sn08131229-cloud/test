package com.example.trace.model;

import java.time.LocalDateTime;
import java.util.*;

public class Models {
    public record User(Long id,String username,String password,String role,Long orgId,String orgName){}
    public record Device(Long deviceId,String deviceCode,String deviceName,String manufacturer,Long supplierId,String supplierName,String status,Long currentOwnerOrgId,String currentOwnerOrgName,Long currentHolderOrgId,String currentHolderOrgName){}
    public record PurchaseOrder(Long orderId,Long deviceId,String deviceCode,Long supplierId,String supplierName,Long hospitalId,String hospitalName,String status,LocalDateTime applyTime,LocalDateTime approveTime,LocalDateTime receiveTime){}
    public record BorrowOrder(Long borrowId,Long deviceId,String deviceCode,Long lenderHospitalId,String lenderHospitalName,Long borrowerHospitalId,String borrowerHospitalName,String status,LocalDateTime applyTime,LocalDateTime approveTime,LocalDateTime lendTime,LocalDateTime receiveTime,LocalDateTime returnApplyTime,LocalDateTime returnConfirmTime){}
    public record FlowRecord(Long flowId,Long deviceId,String deviceCode,String eventType,Long fromOrgId,String fromOrgName,Long toOrgId,String toOrgName,String operator,LocalDateTime operateTime,String businessId,String businessType,String dataHash,String txHash,String chainStatus){}
}
