package com.example.trace.controller;

import com.example.trace.common.ApiResponse;
import com.example.trace.service.AppService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final AppService s;
    public ApiController(AppService s){this.s=s;}

    @PostMapping("/login") public ApiResponse<?> login(@RequestBody Map<String,String> b){return ApiResponse.ok(s.login(b.get("username"),b.get("password")));}

    @GetMapping("/devices") public ApiResponse<?> devices(){return ApiResponse.ok(s.devices());}
    @PostMapping("/devices") public ApiResponse<?> addDevice(@RequestBody Map<String,String> b){return ApiResponse.ok(s.registerDevice(b.get("deviceCode"),b.get("deviceName"),b.getOrDefault("manufacturer","M"),Long.valueOf(b.get("supplierId")),b.get("supplierName"),b.getOrDefault("operator","system")));}
    @GetMapping("/devices/{deviceCode}") public ApiResponse<?> device(@PathVariable String deviceCode){return ApiResponse.ok(s.device(deviceCode));}
    @GetMapping("/devices/{deviceCode}/flows") public ApiResponse<?> deviceFlows(@PathVariable String deviceCode){return ApiResponse.ok(s.deviceFlows(deviceCode));}

    @PostMapping("/purchase/apply") public ApiResponse<?> purchaseApply(@RequestBody Map<String,String> b){return ApiResponse.ok(s.purchaseApply(b.get("deviceCode"),Long.valueOf(b.get("hospitalId")),b.get("hospitalName"),b.getOrDefault("operator","system")));}
    @PostMapping("/purchase/approve") public ApiResponse<?> purchaseApprove(@RequestBody Map<String,String> b){return ApiResponse.ok(s.purchaseApprove(Long.valueOf(b.get("orderId")),b.getOrDefault("operator","system")));}
    @PostMapping("/purchase/receive") public ApiResponse<?> purchaseReceive(@RequestBody Map<String,String> b){return ApiResponse.ok(s.purchaseReceive(Long.valueOf(b.get("orderId")),b.getOrDefault("operator","system")));}
    @GetMapping("/purchase/list") public ApiResponse<?> purchaseList(){return ApiResponse.ok(s.purchases());}

    @PostMapping("/borrow/apply") public ApiResponse<?> borrowApply(@RequestBody Map<String,String> b){return ApiResponse.ok(s.borrowApply(b.get("deviceCode"),Long.valueOf(b.get("lenderHospitalId")),b.get("lenderHospitalName"),Long.valueOf(b.get("borrowerHospitalId")),b.get("borrowerHospitalName"),b.getOrDefault("operator","system")));}
    @PostMapping("/borrow/approve") public ApiResponse<?> borrowApprove(@RequestBody Map<String,String> b){return ApiResponse.ok(s.borrowApprove(Long.valueOf(b.get("borrowId")),b.getOrDefault("operator","system")));}
    @PostMapping("/borrow/lend") public ApiResponse<?> lend(@RequestBody Map<String,String> b){return ApiResponse.ok(s.lend(Long.valueOf(b.get("borrowId")),b.getOrDefault("operator","system")));}
    @PostMapping("/borrow/receive") public ApiResponse<?> bReceive(@RequestBody Map<String,String> b){return ApiResponse.ok(s.borrowReceive(Long.valueOf(b.get("borrowId")),b.getOrDefault("operator","system")));}
    @PostMapping("/borrow/return/apply") public ApiResponse<?> rApply(@RequestBody Map<String,String> b){return ApiResponse.ok(s.returnApply(Long.valueOf(b.get("borrowId")),b.getOrDefault("operator","system")));}
    @PostMapping("/borrow/return/confirm") public ApiResponse<?> rConfirm(@RequestBody Map<String,String> b){return ApiResponse.ok(s.returnConfirm(Long.valueOf(b.get("borrowId")),b.getOrDefault("operator","system")));}
    @GetMapping("/borrow/list") public ApiResponse<?> borrowList(){return ApiResponse.ok(s.borrows());}

    @GetMapping("/flows") public ApiResponse<?> flows(){return ApiResponse.ok(s.flows());}
    @GetMapping("/flows/byTxHash/{txHash}") public ApiResponse<?> byTx(@PathVariable String txHash){return ApiResponse.ok(s.byTx(txHash));}
    @GetMapping("/dashboard/stats") public ApiResponse<?> stats(){var d=s.devices(); return ApiResponse.ok(Map.of("deviceTotal",d.size(),"borrowed",d.stream().filter(x->"BORROWED".equals(x.status())).count(),"inStock",d.stream().filter(x->"IN_STOCK".equals(x.status())).count(),"flowTotal",s.flows().size()));}

    @ExceptionHandler(Exception.class) public ApiResponse<?> ex(Exception e){return ApiResponse.fail(e.getMessage());}
}
