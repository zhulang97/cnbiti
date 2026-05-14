# CNBJTI 技术架构方案

日期：2026-04-29
项目：cnbjti.com 宝鸡钛材外贸独立站
指定技术：前端 Vue、后端 Java、数据库 MySQL、文件存储 MinIO

## 1. 总体判断

cnbjti.com 是典型的 B2B 外贸询盘站，不是普通商城。技术架构要优先保证：

- SEO 收录：产品、牌号、标准、行业、知识文章都要能被 Google 抓取。
- 询盘转化：RFQ 表单、图纸上传、WhatsApp、邮件通知必须稳定。
- 内容运营：产品资料、多语言、SEO 字段、案例、知识文章要能由后台维护。
- 文件安全：图纸、MTR、报价单、检测报告等不能直接暴露公网。
- 后续扩展：报价、CRM、订单、客户门户可以逐步加，不要第一版就做成复杂微服务。

因此建议采用：

> Nuxt 3 官网前台 + Vue 3 管理后台 + Spring Boot 模块化单体 + MySQL 8.4 LTS + MinIO/S3 兼容对象存储 + Redis + Nginx。

第一版不建议上微服务。用“模块化单体”开发速度快、部署简单、事务一致性好；当询盘量、报价流程、客户门户真的变复杂后，再拆服务。

## 2. 推荐技术栈

### 2.1 前台官网

- 框架：Nuxt 3。
- 语言：TypeScript。
- UI：自定义业务组件 + Tailwind CSS。
- 状态：Pinia，前台少用全局状态，主要用于语言、询价篮、用户偏好。
- 国际化：`@nuxtjs/i18n`。
- SEO：Nuxt SEO 模块、自动 sitemap、hreflang、schema.org。
- 图片：Nuxt Image + MinIO 图片代理/缩略图。
- 渲染模式：SSR + 重点页面预渲染/缓存。

为什么前台用 Nuxt 而不是普通 Vue SPA：

- 普通 Vue SPA 初始 HTML 内容少，不利于外贸站 SEO。
- Nuxt 属于 Vue 生态，默认支持服务端渲染，产品页和文章页更容易被搜索引擎索引。
- 产品、牌号、标准、行业页面可以做静态化或 SWR 缓存，速度更好。

### 2.2 管理后台

- 框架：Vue 3 + Vite。
- 语言：TypeScript。
- UI：Element Plus。
- 状态：Pinia。
- 路由：Vue Router。
- 表格：Element Plus Table，后续需要复杂表格可引入 VXE Table。
- 富文本：TipTap 或 TinyMCE，建议 TipTap 优先。
- 上传：直传后端，由后端生成 MinIO 预签名地址；敏感文件不走公开地址。
- 权限：基于后端返回的菜单、角色和按钮权限动态控制。

管理后台不是给 Google 看，不需要 SSR，使用标准 SPA 即可。

### 2.3 后端

- 语言：Java 21 LTS。
- 框架：Spring Boot 3.x。
- 安全：Spring Security。
- 数据访问：MyBatis-Plus。
- 数据迁移：Flyway。
- API 文档：springdoc-openapi / Swagger UI。
- 对象映射：MapStruct。
- 参数校验：Jakarta Validation。
- 任务调度：Spring Scheduler，后续可升级 XXL-JOB。
- 邮件：Spring Mail，生产环境接企业邮箱/SMTP 服务。
- PDF：OpenPDF 或 LibreOffice/模板渲染方案，报价单阶段使用。
- Excel：EasyExcel，用于产品、询盘、报价导入导出。
- 日志：Logback + JSON 日志，便于后期接 Loki/ELK。

后端形态：

- 第一阶段：一个 Spring Boot 应用，内部按业务模块拆包。
- 第二阶段：仍保持单体，但把耗时任务异步化。
- 第三阶段：如果订单、客户门户、营销自动化变复杂，再拆独立服务。

### 2.4 数据库

- MySQL 8.4 LTS。
- 字符集：`utf8mb4`。
- 排序规则：建议 `utf8mb4_0900_ai_ci`，如果要做严格大小写/符号控制，部分字段单独设 collation。
- 存储引擎：InnoDB。
- 主键：业务表建议 `BIGINT` 雪花 ID 或数据库自增；对外展示用业务编号，不直接暴露主键。
- 软删除：重要业务表使用 `deleted_at` 或 `is_deleted`。
- 审计字段：`created_at`、`created_by`、`updated_at`、`updated_by`。
- 版本字段：报价、内容发布可用 `version`。

### 2.5 文件存储

- 存储：MinIO。
- 接口：按 S3 兼容接口封装，不让业务层直接依赖 MinIO SDK。
- 公开文件：产品图片、公开产品目录、公开证书。
- 私有文件：客户图纸、RFQ 附件、报价单、MTR、检测报告、合同、PI、发票。
- 访问方式：公开文件走 CDN/反向代理；私有文件走后端鉴权后生成短期预签名 URL。
- 图片处理：第一版可由后端生成缩略图；后续可加独立图片处理服务。

注意：MinIO 近年产品和许可证变化较多，建议代码层只依赖 S3 抽象。这样未来可平滑迁移到 AWS S3、阿里云 OSS、腾讯 COS、Cloudflare R2 或其他 S3 兼容存储。

### 2.6 其它基础设施

- Redis：缓存、登录 token 黑名单、限流、验证码、热点配置、异步任务队列。
- Nginx：反向代理、TLS、静态资源、压缩、缓存、限流。
- Meilisearch：第二阶段用于站内搜索，第一阶段可先用 MySQL 查询。
- Docker Compose：第一阶段部署。
- Kubernetes：不建议第一版上，除非已有运维团队。
- CI/CD：GitHub Actions、GitLab CI 或 Gitea Actions。
- 监控：Prometheus + Grafana，轻量阶段也可先用 Uptime Kuma。
- 日志：Loki + Promtail 或后期 ELK/OpenSearch。
- 备份：MySQL 定时备份 + MinIO bucket 备份 + 配置文件备份。

## 3. 系统架构

```text
海外买家
  |
  v
Cloudflare / CDN / DNS / WAF
  |
  v
Nginx
  |----------------------|
  v                      v
Nuxt 3 前台官网          Vue 3 管理后台
  |                      |
  |                      v
  |---------------> Spring Boot API
                         |
          |--------------|--------------|--------------|
          v              v              v              v
        MySQL          Redis          MinIO          SMTP/邮件
                         |
                         v
                 后台异步任务/通知
```

建议域名：

- `https://www.cnbjti.com`：英文前台。
- `https://cnbjti.com`：301 到 `www` 或反过来，二选一。
- `https://admin.cnbjti.com`：管理后台。
- `https://api.cnbjti.com`：API 服务。
- `https://assets.cnbjti.com`：公开图片和文件 CDN 域名。

不建议把后台放在 `/admin`，独立子域名更利于安全隔离和缓存配置。

## 4. 后端模块划分

建议包结构：

```text
com.cnbjti
  common
    config
    security
    exception
    response
    validation
    util
  system
    user
    role
    permission
    audit
    setting
  cms
    page
    article
    banner
    menu
    seo
  catalog
    category
    product
    grade
    standard
    industry
    processing
  i18n
    locale
    translation
    termbase
  rfq
    lead
    rfq
    rfqitem
    assignment
    followup
  quote
    quote
    quoteitem
    approval
    pdf
  order
    order
    shipment
    document
  media
    asset
    bucket
    image
  notification
    email
    template
    webhook
  analytics
    visit
    conversion
    source
```

第一版必须完成：

- `system`：用户、角色、权限、登录。
- `catalog`：产品、分类、牌号、标准。
- `cms`：页面、文章、SEO。
- `i18n`：语言、翻译内容。
- `rfq`：询盘、询盘明细、附件、分配、状态。
- `media`：图片、附件、MinIO。
- `notification`：询盘邮件通知。

第二版再做：

- `quote`：报价单、PDF、审批、发送记录。
- `analytics`：转化来源、热门产品、询盘漏斗。
- `order`：PI、订单、出货文件。

## 5. 前台功能架构

### 5.1 页面类型

- 首页。
- 产品分类页。
- 产品详情页。
- 牌号详情页。
- 标准详情页。
- 行业应用页。
- 加工能力页。
- 质量与证书页。
- 文章/知识库。
- 案例/出货记录。
- RFQ 页面。
- 联系我们。
- 隐私政策、Cookie 政策、Terms。

### 5.2 前台数据来源

前台不直接读数据库，通过 API 获取发布态内容：

- `/api/public/site-config`
- `/api/public/navigation`
- `/api/public/products`
- `/api/public/products/{slug}`
- `/api/public/categories/{slug}`
- `/api/public/grades/{slug}`
- `/api/public/standards/{slug}`
- `/api/public/articles/{slug}`
- `/api/public/rfq`

公开 API 要做缓存，RFQ 提交 API 要做限流、防垃圾和附件校验。

### 5.3 多语言 URL

建议第一版就按多语言结构建：

```text
/en/
/en/products/titanium-bar/
/en/grades/grade-2-titanium/
/en/request-a-quote/

/es/
/de/
/fr/
```

默认语言为英文。中文可以作为内部或辅助语言：

```text
/zh/
```

### 5.4 SEO 策略

后台每个可发布对象都要维护：

- slug。
- meta title。
- meta description。
- canonical。
- og title。
- og description。
- og image。
- index/follow。
- schema type。
- hreflang。

Nuxt 自动生成：

- sitemap。
- robots.txt。
- breadcrumb schema。
- product schema。
- FAQ schema。
- organization schema。

## 6. 管理后台功能架构

### 6.1 工作台

- 今日询盘。
- 待分配询盘。
- 超时未跟进。
- 热门产品。
- 热门语言。
- RFQ 来源。
- 待翻译内容。
- 待发布内容。

### 6.2 内容管理

- 首页模块。
- 页面管理。
- 文章/知识库。
- 行业应用。
- 案例/出货记录。
- FAQ。
- Banner。
- SEO 字段。

### 6.3 产品管理

- 产品分类。
- 产品。
- 牌号。
- 标准。
- 规格表。
- 表面处理。
- 加工能力。
- 相关产品。
- 产品图片。
- 产品 PDF。

### 6.4 询盘管理

- RFQ 列表。
- RFQ 详情。
- 附件预览/下载。
- 客户资料。
- 分配业务员。
- 状态流转。
- 内部备注。
- 跟进记录。
- 邮件模板。
- 导出 Excel。

状态建议：

```text
NEW
QUALIFIED
NEED_TECH_REVIEW
QUOTING
QUOTE_SENT
FOLLOW_UP
WON
LOST
SPAM
ARCHIVED
```

### 6.5 报价管理

第二阶段建设：

- 报价单。
- 报价明细。
- 多币种。
- 贸易条款。
- 付款条款。
- 有效期。
- PDF 生成。
- 报价版本。
- 审批。
- 邮件发送记录。

### 6.6 系统管理

- 用户。
- 角色。
- 菜单。
- 按钮权限。
- 字典。
- 系统设置。
- 邮件模板。
- 操作日志。
- 登录日志。
- 文件策略。

## 7. 核心数据表

### 7.1 内容与产品

```text
language
translation
translation_job
media_asset
seo_meta
cms_page
cms_article
product_category
product
product_i18n
product_spec
product_media
titanium_grade
titanium_grade_i18n
standard
standard_i18n
industry
processing_service
```

### 7.2 询盘与客户

```text
customer
customer_contact
lead
rfq
rfq_item
rfq_attachment
rfq_followup
rfq_assignment
rfq_status_log
```

### 7.3 报价与订单

```text
quote
quote_item
quote_version
quote_approval
quote_send_log
sales_order
shipment
business_document
```

### 7.4 系统与审计

```text
sys_user
sys_role
sys_permission
sys_user_role
sys_role_permission
sys_login_log
sys_audit_log
sys_config
notification_template
```

## 8. 关键业务流程

### 8.1 RFQ 流程

```text
客户浏览产品
  -> 点击 Get Quote
  -> 自动带入产品/分类/语言/来源
  -> 填写牌号、标准、规格、数量、目的国
  -> 上传图纸或询盘表
  -> 提交
  -> 后端校验和风控
  -> MinIO 保存附件
  -> MySQL 创建 RFQ 和 Lead
  -> 邮件通知业务员
  -> 客户收到确认邮件
  -> 后台分配业务员
  -> 跟进/报价/成交或丢单
```

### 8.2 内容发布流程

```text
编辑创建产品/文章
  -> 填写英文主内容
  -> 生成翻译任务
  -> 人工审核翻译
  -> 设置 SEO
  -> 预览
  -> 发布
  -> Nuxt 缓存刷新 / sitemap 更新
```

### 8.3 私有文件下载

```text
后台用户点击下载 RFQ 附件
  -> 后端验证登录和权限
  -> 记录审计日志
  -> 生成 MinIO 短期预签名 URL
  -> 浏览器下载
```

## 9. API 设计

接口风格：

- REST 优先。
- JSON 数据。
- 文件上传使用预签名 URL 或后端中转。
- 管理端 API 前缀：`/api/admin/**`。
- 前台公开 API 前缀：`/api/public/**`。
- 认证 API 前缀：`/api/auth/**`。

统一响应：

```json
{
  "code": "OK",
  "message": "success",
  "data": {},
  "requestId": "req_..."
}
```

分页：

```json
{
  "items": [],
  "page": 1,
  "pageSize": 20,
  "total": 100
}
```

错误码建议：

- `VALIDATION_ERROR`
- `UNAUTHORIZED`
- `FORBIDDEN`
- `NOT_FOUND`
- `DUPLICATE`
- `RATE_LIMITED`
- `UPLOAD_TOO_LARGE`
- `UNSUPPORTED_FILE_TYPE`
- `INTERNAL_ERROR`

## 10. 安全设计

### 10.1 前台安全

- RFQ 表单限流。
- 验证码，可用 Cloudflare Turnstile。
- 邮箱和电话格式校验。
- 附件类型白名单：PDF、DWG、DXF、STEP、STP、XLS、XLSX、JPG、PNG、ZIP。
- 附件大小限制，建议单文件 30MB，单询盘 100MB。
- XSS 过滤，富文本白名单。
- Cookie consent 和隐私政策。

### 10.2 后台安全

- Spring Security。
- JWT + Refresh Token，或后台使用 HttpOnly Cookie Session。建议后台用 HttpOnly Cookie 更稳。
- 2FA 预留，第二阶段启用。
- RBAC 权限。
- 登录失败锁定。
- 操作审计。
- IP 白名单可选。
- 敏感操作二次确认。

### 10.3 文件安全

- 私有 bucket 默认不公开。
- 所有私有文件下载都经过后端鉴权。
- 文件名随机化。
- 保留原始文件名但不作为对象 key。
- 上传后记录 hash、大小、mime type。
- 后续可接病毒扫描。

## 11. 部署方案

### 11.1 MVP 单机部署

适合第一版：

```text
1 台云服务器
  - Nginx
  - Nuxt server
  - Spring Boot API
  - MySQL
  - Redis
  - MinIO
```

建议配置：

- 4 核 8G 起步。
- SSD 100G 起步。
- 数据和 MinIO 单独挂载磁盘。
- 每日自动备份到异地。

### 11.2 推荐生产部署

更稳的方式：

```text
Web/API 服务器
  - Nginx
  - Nuxt
  - Spring Boot
  - Redis

数据库服务器
  - MySQL

对象存储服务器或托管对象存储
  - MinIO / S3 compatible storage

备份存储
  - 异地备份
```

### 11.3 CDN 与海外访问

外贸站建议接 Cloudflare：

- DNS。
- CDN。
- WAF。
- DDoS 基础防护。
- 图片缓存。
- Turnstile 防垃圾表单。
- Page Rules/Cache Rules。

前台静态资源和图片尽量走 CDN，后台和 API 不做公共缓存。

## 12. 环境划分

```text
local     本地开发
dev       内部联调
staging   上线前预览
prod      正式环境
```

每个环境独立：

- 数据库。
- MinIO bucket。
- Redis。
- SMTP 配置。
- 域名。
- 密钥。

## 13. 开发仓库结构

建议 monorepo：

```text
cnbjti/
  apps/
    web/          # Nuxt 3 前台
    admin/        # Vue 3 管理后台
    api/          # Spring Boot 后端
  packages/
    ui/           # 可选，共用 UI tokens 或组件
    types/        # 可选，OpenAPI 生成的 TS 类型
  deploy/
    docker/
    nginx/
    mysql/
    minio/
  docs/
```

如果团队 Java 和前端分工明显，也可以拆三个仓库。但第一版 monorepo 更利于协作和统一文档。

## 14. 开发规范

### 14.1 后端

- 所有表结构变更走 Flyway。
- Controller 只做参数接收和响应，不写业务。
- Service 写业务流程。
- Mapper 只负责数据库访问。
- 所有写操作记录审计。
- 重要状态流转封装成领域方法，避免随意改状态。

### 14.2 前端

- 所有 API 类型从 OpenAPI 生成。
- 表单校验前后端都做。
- SEO 页面组件和后台业务组件分开。
- 多语言文案不要散落组件，统一走 i18n。
- 前台图片必须有 alt。

### 14.3 Git 和发布

- 分支：`main`、`develop`、`feature/*`、`hotfix/*`。
- `main` 对应生产。
- `develop` 对应测试环境。
- 每次发布打 tag。
- CI 运行 lint、test、build。

## 15. 第一版开发范围

### 15.1 前台

- 首页。
- 产品分类页。
- 产品详情页。
- RFQ 页面。
- About。
- Quality。
- Processing。
- Industries。
- Contact。
- Blog/Resources。
- Sitemap、robots、基础 schema。

### 15.2 后台

- 登录。
- 用户/角色。
- 产品分类管理。
- 产品管理。
- 页面管理。
- 文章管理。
- 媒体管理。
- RFQ 管理。
- 客户管理。
- 邮件通知设置。
- SEO 字段管理。

### 15.3 后端

- 认证授权。
- 产品 API。
- CMS API。
- RFQ API。
- 文件上传 API。
- 邮件通知。
- 操作日志。
- 基础统计。

## 16. 第二版开发范围

- 多语言完整工作流。
- 报价单和 PDF。
- 报价审批。
- 询盘跟进提醒。
- 站内搜索。
- 资料下载留资。
- 客户门户雏形。
- Google Ads/LinkedIn 落地页管理。
- 转化分析。

## 17. 第三版开发范围

- 订单管理。
- 出货文件管理。
- 客户查看报价和订单。
- 自动邮件营销。
- 库存或常用规格管理。
- ERP/财务系统接口。
- 更复杂的数据看板。

## 18. 关键技术风险

- SEO 风险：如果前台只做 Vue SPA，收录和排名会吃亏，所以用 Nuxt。
- 多语言风险：机器翻译大面积发布会降低信任，后台必须有人工审核状态。
- 文件泄露风险：客户图纸和报价文件必须私有存储。
- 询盘垃圾风险：外贸站会收到大量垃圾表单，要做限流、验证码和评分。
- 产品数据风险：规格、牌号、标准一旦混乱，后期 SEO 和报价都会乱，所以第一版就要结构化。
- 运维风险：MySQL、MinIO、Redis 都要备份；只备份数据库不够。

## 19. 官方参考

- Nuxt 3 渲染模式与 SEO：https://nuxt.com/docs/3.x/guide/concepts/rendering
- Nuxt 3 介绍：https://nuxt.com/docs/3.x
- Pinia：https://pinia.vuejs.org/
- Spring Boot system requirements：https://docs.enterprise.spring.io/spring-boot/system-requirements.html
- MySQL 8.4 Reference Manual：https://dev.mysql.com/doc/mysql/en/
- MySQL LTS/Innovation release model：https://dev.mysql.com/doc/refman/8.4/en/mysql-releases.html
- MinIO S3 API compatibility：https://min.io/docs/minio/linux/reference/s3-api-compatibility.html
