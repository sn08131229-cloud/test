package com.example.trace.service;

import com.example.trace.model.Models.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AppService {
    private final ChainService chainService;
    public AppService(ChainService chainService){this.chainService=chainService;seed();}
    private final Map<String,User> users=new HashMap<>();
    private final Map<String,Device> devices=new ConcurrentHashMap<>();
    private final Map<Long,PurchaseOrder> purchases=new ConcurrentHashMap<>();
    private final Map<Long,BorrowOrder> borrows=new ConcurrentHashMap<>();
    private final List<FlowRecord> flows=Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong idGen = new AtomicLong(10000);

    void seed(){users.put("admin",new User(1L,"admin","admin123","ADMIN",1L,"平台管理方")); users.put("sup1",new User(11L,"sup1","sup123","SUPPLIER",11L,"供应商甲")); users.put("hosp1",new User(21L,"hosp1","hosp123","HOSPITAL",21L,"第一医院")); users.put("hosp2",new User(22L,"hosp2","hosp123","HOSPITAL",22L,"第二医院")); users.put("reg1",new User(2L,"reg1","reg123","REGULATOR",2L,"市监管局"));}
    public User login(String u,String p){var x=users.get(u); if(x==null||!x.password().equals(p)) throw new IllegalArgumentException("用户名或密码错误"); return x;}
    public Collection<Device> devices(){return devices.values();}
    public Device device(String code){var d=devices.get(code); if(d==null) throw new IllegalArgumentException("设备不存在"); return d;}
    public List<FlowRecord> deviceFlows(String code){return flows.stream().filter(f->f.deviceCode().equals(code)).toList();}
    public Device registerDevice(String code,String name,String manufacturer,Long supplierId,String supplierName,String operator){
        if(devices.containsKey(code)) throw new IllegalArgumentException("deviceCode重复");
        var d=new Device(idGen.incrementAndGet(),code,name,manufacturer,supplierId,supplierName,"PRODUCED",supplierId,supplierName,supplierId,supplierName);
        devices.put(code,d); addFlow(d,"DEVICE_REGISTER",supplierId,supplierName,supplierId,supplierName,operator,String.valueOf(d.deviceId()),"DEVICE"); return d;
    }
    public PurchaseOrder purchaseApply(String code,Long hospitalId,String hospitalName,String operator){
        var d=device(code); if(!List.of("PRODUCED","SUPPLIED").contains(d.status())) throw new IllegalArgumentException("设备当前状态不允许采购");
        long oid=idGen.incrementAndGet(); var po=new PurchaseOrder(oid,d.deviceId(),code,d.supplierId(),d.supplierName(),hospitalId,hospitalName,"PURCHASE_APPLIED",LocalDateTime.now(),null,null); purchases.put(oid,po);
        addFlow(d,"PURCHASE_APPLY",hospitalId,hospitalName,d.supplierId(),d.supplierName(),operator,String.valueOf(oid),"PURCHASE"); return po;
    }
    public PurchaseOrder purchaseApprove(Long orderId,String operator){var p=mustP(orderId,"PURCHASE_APPLIED");
        var np=new PurchaseOrder(p.orderId(),p.deviceId(),p.deviceCode(),p.supplierId(),p.supplierName(),p.hospitalId(),p.hospitalName(),"PURCHASE_APPROVED",p.applyTime(),LocalDateTime.now(),null); purchases.put(orderId,np); addFlow(device(p.deviceCode()),"PURCHASE_APPROVE",p.supplierId(),p.supplierName(),p.hospitalId(),p.hospitalName(),operator,String.valueOf(orderId),"PURCHASE"); return np;}
    public PurchaseOrder purchaseReceive(Long orderId,String operator){var p=mustP(orderId,"PURCHASE_APPROVED");
        var np=new PurchaseOrder(p.orderId(),p.deviceId(),p.deviceCode(),p.supplierId(),p.supplierName(),p.hospitalId(),p.hospitalName(),"PURCHASE_RECEIVED",p.applyTime(),p.approveTime(),LocalDateTime.now()); purchases.put(orderId,np);
        var d=device(p.deviceCode()); devices.put(d.deviceCode(),new Device(d.deviceId(),d.deviceCode(),d.deviceName(),d.manufacturer(),d.supplierId(),d.supplierName(),"IN_STOCK",p.hospitalId(),p.hospitalName(),p.hospitalId(),p.hospitalName())); addFlow(d,"PURCHASE_RECEIVE",p.supplierId(),p.supplierName(),p.hospitalId(),p.hospitalName(),operator,String.valueOf(orderId),"PURCHASE"); return np;}
    private PurchaseOrder mustP(Long id,String status){var p=purchases.get(id); if(p==null||!p.status().equals(status)) throw new IllegalArgumentException("采购状态非法"); return p;}

    public BorrowOrder borrowApply(String code,Long lenderId,String lenderName,Long borrowerId,String borrowerName,String operator){var d=device(code); if(!d.currentHolderOrgId().equals(lenderId)) throw new IllegalArgumentException("仅当前持有方可借出");
      long id=idGen.incrementAndGet(); var b=new BorrowOrder(id,d.deviceId(),code,lenderId,lenderName,borrowerId,borrowerName,"BORROW_APPLIED",LocalDateTime.now(),null,null,null,null,null); borrows.put(id,b); addFlow(d,"BORROW_APPLY",borrowerId,borrowerName,lenderId,lenderName,operator,String.valueOf(id),"BORROW"); return b;}
    public BorrowOrder borrowApprove(Long id,String operator){var b=mustB(id,"BORROW_APPLIED"); var nb=new BorrowOrder(b.borrowId(),b.deviceId(),b.deviceCode(),b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),"BORROW_APPROVED",b.applyTime(),LocalDateTime.now(),null,null,null,null); borrows.put(id,nb); addFlow(device(b.deviceCode()),"BORROW_APPROVE",b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),operator,String.valueOf(id),"BORROW"); return nb;}
    public BorrowOrder lend(Long id,String operator){var b=mustB(id,"BORROW_APPROVED"); var nb=new BorrowOrder(b.borrowId(),b.deviceId(),b.deviceCode(),b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),"LENT_OUT",b.applyTime(),b.approveTime(),LocalDateTime.now(),null,null,null); borrows.put(id,nb); var d=device(b.deviceCode()); devices.put(d.deviceCode(),new Device(d.deviceId(),d.deviceCode(),d.deviceName(),d.manufacturer(),d.supplierId(),d.supplierName(),"BORROWED",d.currentOwnerOrgId(),d.currentOwnerOrgName(),b.borrowerHospitalId(),b.borrowerHospitalName())); addFlow(d,"DEVICE_LEND_OUT",b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),operator,String.valueOf(id),"BORROW"); return nb;}
    public BorrowOrder borrowReceive(Long id,String operator){var b=mustB(id,"LENT_OUT"); var nb=new BorrowOrder(b.borrowId(),b.deviceId(),b.deviceCode(),b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),"BORROW_RECEIVED",b.applyTime(),b.approveTime(),b.lendTime(),LocalDateTime.now(),null,null); borrows.put(id,nb); addFlow(device(b.deviceCode()),"BORROW_RECEIVE",b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),operator,String.valueOf(id),"BORROW"); return nb;}
    public BorrowOrder returnApply(Long id,String operator){var b=mustB(id,"BORROW_RECEIVED"); var nb=new BorrowOrder(b.borrowId(),b.deviceId(),b.deviceCode(),b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),"RETURN_APPLIED",b.applyTime(),b.approveTime(),b.lendTime(),b.receiveTime(),LocalDateTime.now(),null); borrows.put(id,nb); addFlow(device(b.deviceCode()),"RETURN_APPLY",b.borrowerHospitalId(),b.borrowerHospitalName(),b.lenderHospitalId(),b.lenderHospitalName(),operator,String.valueOf(id),"BORROW"); return nb;}
    public BorrowOrder returnConfirm(Long id,String operator){var b=mustB(id,"RETURN_APPLIED"); var nb=new BorrowOrder(b.borrowId(),b.deviceId(),b.deviceCode(),b.lenderHospitalId(),b.lenderHospitalName(),b.borrowerHospitalId(),b.borrowerHospitalName(),"RETURN_CONFIRMED",b.applyTime(),b.approveTime(),b.lendTime(),b.receiveTime(),b.returnApplyTime(),LocalDateTime.now()); borrows.put(id,nb); var d=device(b.deviceCode()); devices.put(d.deviceCode(),new Device(d.deviceId(),d.deviceCode(),d.deviceName(),d.manufacturer(),d.supplierId(),d.supplierName(),"RETURNED",d.currentOwnerOrgId(),d.currentOwnerOrgName(),b.lenderHospitalId(),b.lenderHospitalName())); addFlow(d,"RETURN_CONFIRM",b.borrowerHospitalId(),b.borrowerHospitalName(),b.lenderHospitalId(),b.lenderHospitalName(),operator,String.valueOf(id),"BORROW"); return nb;}
    private BorrowOrder mustB(Long id,String st){var b=borrows.get(id); if(b==null||!b.status().equals(st)) throw new IllegalArgumentException("借用状态非法"); return b;}
    private void addFlow(Device d,String evt,Long from,String fromName,Long to,String toName,String op,String bizId,String bizType){var c=chainService.write(d.deviceCode(),evt,bizId+"|"+op); flows.add(new FlowRecord(idGen.incrementAndGet(),d.deviceId(),d.deviceCode(),evt,from,fromName,to,toName,op,LocalDateTime.now(),bizId,bizType,c.dataHash(),c.txHash(),c.chainStatus()));}
    public Collection<PurchaseOrder> purchases(){return purchases.values();}
    public Collection<BorrowOrder> borrows(){return borrows.values();}
    public List<FlowRecord> flows(){return flows;}
    public FlowRecord byTx(String tx){return flows.stream().filter(f->f.txHash().equals(tx)).findFirst().orElseThrow(()->new IllegalArgumentException("tx不存在"));}
}
