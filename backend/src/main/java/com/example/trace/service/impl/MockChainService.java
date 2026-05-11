package com.example.trace.service.impl;

import com.example.trace.service.ChainService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class MockChainService implements ChainService {
    @Override
    public ChainResult write(String deviceCode, String eventType, String payload) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            String dataHash = HexFormat.of().formatHex(md.digest((deviceCode+"|"+eventType+"|"+payload).getBytes(StandardCharsets.UTF_8)));
            String txHash = "0x" + UUID.randomUUID().toString().replace("-","") + UUID.randomUUID().toString().replace("-","").substring(0,24);
            return new ChainResult(dataHash, txHash.substring(0,66), "MOCK_SUCCESS");
        } catch (Exception e){ throw new RuntimeException(e); }
    }
}
