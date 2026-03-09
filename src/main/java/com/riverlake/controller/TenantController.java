package com.riverlake.controller;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TenantController {

    private static final Map<String, Map<String, Object>> TENANT_CONFIGS = new HashMap<>();

    static {
        Map<String, Object> tenant1 = new HashMap<>();
        tenant1.put("tenantId", "tenant1");
        tenant1.put("tenantName", "租户A公司");
        tenant1.put("apiBaseUrl", "/api");
        tenant1.put("appTitle", "租户A系统");
        tenant1.put("themeColor", "#1890ff");
        tenant1.put("logo", "/logo-tenant1.png");
        TENANT_CONFIGS.put("tenant1.test.com", tenant1);

        Map<String, Object> tenant2 = new HashMap<>();
        tenant2.put("tenantId", "tenant2");
        tenant2.put("tenantName", "租户B公司");
        tenant2.put("apiBaseUrl", "/api");
        tenant2.put("appTitle", "租户B系统");
        tenant2.put("themeColor", "#52c41a");
        tenant2.put("logo", "/logo-tenant2.png");
        TENANT_CONFIGS.put("tenant2.test.com", tenant2);

        Map<String, Object> defaultTenant = new HashMap<>();
        defaultTenant.put("tenantId", "default");
        defaultTenant.put("tenantName", "默认租户");
        defaultTenant.put("apiBaseUrl", "/api");
        defaultTenant.put("appTitle", "RiverLake Help");
        defaultTenant.put("themeColor", "#1890ff");
        defaultTenant.put("logo", "/logo-default.png");
        TENANT_CONFIGS.put("default", defaultTenant);
    }

    @GetMapping("/tenant-config")
    public Map<String, Object> getTenantConfig(@RequestHeader(value = "Host", required = false) String host) {
        Map<String, Object> config = TENANT_CONFIGS.getOrDefault(host, TENANT_CONFIGS.get("default"));
        
        Map<String, Object> result = new HashMap<>(config);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    @GetMapping("/tenants")
    public Map<String, Map<String, Object>> getAllTenants() {
        return TENANT_CONFIGS;
    }
}
