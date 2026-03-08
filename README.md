# RiverLake Help Backend

RiverLake Help 后端服务，基于 Spring Boot 3.2 构建。

## 技术栈

- **框架**: Spring Boot 3.2
- **Java**: JDK 17
- **构建工具**: Maven 3.9+

## 项目结构

```
src/
├── main/
│   ├── java/com/riverlake/
│   │   ├── RiverLakeHelpApplication.java  # 启动类
│   │   ├── config/
│   │   │   └── CorsConfig.java           # CORS 配置
│   │   └── controller/
│   │       └── HelloController.java      # 示例控制器
│   ├── resources/
│   │   └── application.yml               # 应用配置
│   ├── docker/
│   │   └── Dockerfile                     # Docker 构建文件
│   └── k8s/
│       └── dev/riverlake-help-backend.yaml  # K8s 部署配置
└── pom.xml
```

## 快速开始

### 本地开发

```bash
# 构建项目
mvn clean package

# 运行项目
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /health | 测试接口 |
| POST | /api/echo | 回显接口 |

## 部署说明

### Docker 构建

```bash
# 构建镜像
docker build -t riverlake-help-backend:latest -f src/main/docker/Dockerfile .

# 运行容器
docker run -p 8080:8080 riverlake-help-backend:latest
```

### Kubernetes 部署

```bash
kubectl apply -f src/main/k8s/dev/riverlake-help-backend.yaml
```

## CI/CD

本项目使用 GitLab CI/CD，配置文件：`.gitlab-ci.yml`

流水线包含以下阶段：
1. **package** - Maven 编译打包
2. **test** - 单元测试
3. **build** - Docker 镜像构建并推送
4. **deploy** - Kubernetes 部署

### CI/CD 变量

在 GitLab 项目设置中配置以下变量：

| 变量名 | 说明 |
|--------|------|
| `CI_REGISTRY` | Docker Registry 地址 |
| `CI_REGISTRY_USER` | 仓库用户名 |
| `CI_REGISTRY_PASSWORD` | 仓库密码 |
| `KUBECONFIG_CONTENT` | Base64 编码的 kubeconfig |

## 许可证

MIT
