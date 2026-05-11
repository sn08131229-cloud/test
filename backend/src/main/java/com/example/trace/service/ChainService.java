package com.example.trace.service;

public interface ChainService {
    ChainResult write(String deviceCode,String eventType,String payload);
    record ChainResult(String dataHash,String txHash,String chainStatus){}
}
