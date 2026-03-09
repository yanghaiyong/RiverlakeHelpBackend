package com.riverlake.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TenantController {

    private static final Map<String, TenantConfig> TENANT_CONFIGS = new HashMap<>();

    static {
        TENANT_CONFIGS.put("tenant1.test.com", new TenantConfig("tenant1", "租户A公司", "/api", "租户A系统", "#1890ff", "/logo-tenant1.png"));
        TENANT_CONFIGS.put("tenant2.test.com", new TenantConfig("tenant2", "租户B公司", "/api", "租户B系统", "#52c41a", "/logo-tenant2.png"));
        TENANT_CONFIGS.put("default", new TenantConfig("default", "默认租户", "/api", "RiverLake Help", "#1890ff", "/logo-default.png"));
    }

    @GetMapping("/tenant-config")
    public ResponseEntity<TenantConfig> getTenantConfig(@RequestHeader(value = "Host", required = false) String host) {
        TenantConfig config = TENANT_CONFIGS.getOrDefault(host, TENANT_CONFIGS.get("default"));
        return ResponseEntity.ok(config);
    }

    @GetMapping("/tenants")
    public ResponseEntity<Map<String, TenantConfig>> getAllTenants() {
        return ResponseEntity.ok(TENANT_CONFIGS);
    }

    public static class TenantConfig {
        private String tenantId;
        private String tenantName;
        private String apiBaseUrl;
        private String appTitle;
        private String themeColor;
        private String logo;

        public TenantConfig() {}

        public TenantConfig(String tenantId, String tenantName, String apiBaseUrl, String appTitle, String themeColor, String logo) {
            this.tenantId = tenantId;
            this.tenantName = tenantName;
            this.apiBaseUrl = apiBaseUrl;
            this.appTitle = appTitle;
            this.themeColor = themeColor;
            this.logo = logo;
        }

        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getTenantName() { return tenantName; }
        public void setTenantName(String tenantName) { this.tenantName = tenantName; }
        public String getApiBaseUrl() { return apiBaseUrl; }
        public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
        public String getAppTitle() { return appTitle; }
        public void setAppTitle(String appTitle) { this.appTitle = appTitle; }
        public String getThemeColor() { return themeColor; }
        public void setThemeColor(String themeColor) { this.themeColor = themeColor; }
        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }
    }
}
