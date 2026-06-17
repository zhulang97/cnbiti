# CNBJTI 前端实施规划

日期：2026-04-29
项目：cnbjti.com 宝鸡钛材外贸独立站
阶段策略：先做前端，再做后端；前端先用 Mock API 和本地数据跑通完整体验。

## 1. 前端目标

第一阶段前端要做成一个可以真实演示、可以给客户/团队评审、后续能平滑接 Java API 的完整产品壳。

前端分两个应用：

- `web`：海外买家访问的独立站官网，使用 Nuxt 3。
- `admin`：内部管理后台，使用 Vue 3 + Vite。

前端优先要解决：

- 站点信息架构是否清楚。
- 产品页、牌号页、标准页是否适合 SEO。
- 询价流程是否足够专业。
- 后台管理流程是否覆盖产品、内容、多语言、询盘。
- 页面视觉是否像可信工业供应商，而不是模板站。
- 所有核心页面在没有后端时也能用 mock 数据演示。

## 2. 前端技术栈

### 2.1 官网 `apps/web`

- Nuxt 3。
- Vue 3。
- TypeScript。
- Tailwind CSS。
- Pinia。
- Nuxt i18n。
- Nuxt Image。
- Nuxt SEO / sitemap / robots。
- Zod：前端表单校验和 mock API 类型校验。
- VueUse：常用组合式工具。

官网必须使用 SSR/预渲染能力，因为外贸站高度依赖 Google SEO。

### 2.2 后台 `apps/admin`

- Vue 3。
- Vite。
- TypeScript。
- Vue Router。
- Pinia。
- Element Plus。
- Axios 或 ofetch 封装请求。
- TipTap：富文本编辑器。
- ECharts：数据看板。
- VueUse。
- Zod。

后台不需要 SSR，标准 SPA 即可。

### 2.3 共用包

建议预留：

```text
packages/
  types/       # 前后端契约类型，后面可由 OpenAPI 生成
  mock-data/   # 前端阶段 mock 数据
  utils/       # 少量纯函数工具
```

第一版不要过度抽公共 UI 组件。官网和后台的视觉和交互差别很大，强行共用反而拖慢。

## 3. 前端仓库结构

建议结构：

```text
cnbjti/
  apps/
    web/
      app.vue
      nuxt.config.ts
      pages/
      layouts/
      components/
      composables/
      stores/
      server/api/       # 前端阶段 mock API
      assets/
      public/
      i18n/
      types/
    admin/
      index.html
      vite.config.ts
      src/
        main.ts
        router/
        layouts/
        views/
        components/
        stores/
        api/
        mocks/
        styles/
        types/
  packages/
    types/
    mock-data/
    utils/
  docs/
```

## 4. 官网信息架构

### 4.1 主导航

官网顶部导航：

- Products。
- Grades。
- Standards。
- Processing。
- Industries。
- Resources。
- Quality。
- About。
- Contact。
- Get a Quote。

Products 使用 mega menu：

- Titanium Bar & Rod。
- Titanium Sheet / Plate / Foil。
- Titanium Tube & Pipe。
- Titanium Wire。
- Titanium Forgings。
- Titanium Fasteners。
- Titanium Fittings & Flanges。
- Titanium Anode & Electrode。
- Sintered Titanium Filter。
- CNC Titanium Parts。

顶部固定联系入口：

- Get a Quote。
- Send Drawing。
- WhatsApp。
- Email。
- Language Switcher。

移动端：

- 底部固定操作条：Quote、Products、WhatsApp。
- 菜单展开后按产品、资源、公司信息分组。

### 4.2 URL 结构

第一版就按多语言 URL 做：

```text
/en/
/en/products/
/en/products/titanium-bar/
/en/products/titanium-bar/grade-2-titanium-round-bar/
/en/grades/
/en/grades/grade-2-titanium/
/en/standards/
/en/standards/astm-b348-titanium-bar/
/en/processing/
/en/processing/cut-to-size/
/en/industries/
/en/industries/chemical-processing/
/en/resources/
/en/resources/titanium-weight-calculator/
/en/request-a-quote/
/en/contact/
```

预留语言：

```text
/es/
/de/
/fr/
/ar/
/ru/
/pt/
/zh/
```

第一版只发布英文，中文可作为内部内容辅助，不必优先公开。

## 5. 官网页面规划

### 5.1 首页

目标：让海外采购商 3 秒内知道“你是谁、卖什么、为什么可信、怎么询价”。

模块顺序：

1. 顶部细条：Baoji Titanium Valley / MTR Available / Global Shipping。
2. Header + Mega Menu。
3. Hero：
   - 标题：Baoji Titanium Materials & Custom Processing for Global Buyers。
   - 副标题：Titanium bar, plate, tube, wire, forgings, fittings, anodes and CNC parts supplied with export-ready documentation。
   - CTA：Get a Quote、Send Drawing、View Products。
   - 背景建议：真实钛材、仓库、机加工或包装出货照片，不用抽象渐变。
4. Trust strip：
   - ASTM / ASME / AMS。
   - MTR Available。
   - Cut-to-size & CNC。
   - Small MOQ。
   - Global Shipping。
5. Product category grid。
6. Quick RFQ panel。
7. Processing capabilities。
8. Industry applications。
9. Quality documentation。
10. Resources / guides。
11. Shipment / packaging proof。
12. Footer。

首页不要做营销感太强的“漂亮大空白”。外贸工业站更需要高信息密度、清晰路径和可信材料。

### 5.2 产品中心页

路径：`/en/products/`

功能：

- 产品分类卡片。
- 按形态筛选：bar、plate、tube、wire、forging、machined part。
- 按牌号筛选：Gr1、Gr2、Gr5、Gr7、Gr9、Gr12、Gr23。
- 按标准筛选：ASTM、ASME、AMS、AWS、EN、DIN、ISO。
- 搜索产品。
- 右侧或顶部 RFQ 入口。
- SEO 文案区。

### 5.3 产品分类页

示例：`/en/products/titanium-bar/`

模块：

- 分类 Hero。
- 常用牌号。
- 常用标准。
- 规格范围表。
- 产品列表。
- 加工能力。
- 应用行业。
- FAQ。
- 相关资料。
- 分类 RFQ 表单。

### 5.4 产品详情页

示例：`/en/products/titanium-bar/grade-2-titanium-round-bar/`

模块：

- 产品 Hero：产品图、标题、简述、CTA。
- 快速规格摘要。
- Available Grades。
- Standards。
- Size Range 表。
- Surface Finish。
- Processing Options。
- Testing & Documents。
- Applications。
- Packaging & Shipping。
- Related Products。
- Related Articles。
- Sticky RFQ Card。

详情页 CTA：

- Get Quote for This Product。
- Upload Drawing。
- Ask Technical Question。

### 5.5 牌号页

路径：`/en/grades/grade-2-titanium/`

模块：

- 牌号简介。
- Chemical Composition 表。
- Mechanical Properties 表。
- Equivalent Grades。
- Common Product Forms。
- Applicable Standards。
- Typical Applications。
- Related Products。
- RFQ CTA。

重点牌号：

- Grade 1。
- Grade 2。
- Grade 3。
- Grade 4。
- Grade 5 / Ti-6Al-4V。
- Grade 7。
- Grade 9。
- Grade 12。
- Grade 23 / Ti-6Al-4V ELI。

### 5.6 标准页

路径：`/en/standards/astm-b348-titanium-bar/`

模块：

- 标准说明。
- 覆盖产品类型。
- 常见牌号。
- 尺寸/检测相关说明。
- Related Products。
- Related Grades。
- RFQ CTA。

重点标准：

- ASTM B348。
- ASTM B265。
- ASTM B338 / B337。
- ASTM B381。
- ASTM B363。
- ASTM B861 / B862。
- AWS A5.16。
- EN 10204 3.1。

### 5.7 加工能力页

路径：`/en/processing/`

页面：

- Cut-to-size。
- CNC Machining。
- Waterjet Cutting。
- Turning / Grinding / Polishing。
- Drilling / Threading。
- Welding / Fabrication。
- Surface Treatment。

每个加工页包含：

- 能做什么。
- 适用产品。
- 可接受图纸格式。
- 公差和表面说明。
- 设备/加工实拍图。
- RFQ CTA。

### 5.8 行业应用页

路径：`/en/industries/`

页面：

- Chemical Processing。
- Marine & Desalination。
- Aerospace。
- Medical。
- Oil & Gas。
- Energy。
- Automotive / Performance。

每个行业页包含：

- 行业痛点。
- 推荐牌号。
- 推荐产品。
- 常用标准。
- 文档要求。
- 相关案例。
- RFQ CTA。

### 5.9 Resources 知识库

路径：`/en/resources/`

第一版页面：

- Titanium Grade Guide。
- Titanium Weight Calculator。
- Titanium Standards Guide。
- MTR / EN 10204 3.1 Guide。
- Titanium Corrosion Resistance Guide。
- FAQ。

文章列表功能：

- 分类筛选。
- 搜索。
- 相关文章。
- 文章页目录锚点。
- FAQ schema。

### 5.10 Titanium Weight Calculator

路径：`/en/resources/titanium-weight-calculator/`

支持：

- Round bar。
- Plate / sheet。
- Tube / pipe。
- Wire。

输入：

- Grade / density。
- Diameter。
- Thickness。
- Width。
- Length。
- Wall thickness。
- Quantity。
- Unit 切换：mm/inch、kg/lb。

输出：

- 单件重量。
- 总重量。
- 可复制结果。
- 生成 RFQ。

### 5.11 RFQ 页面

路径：`/en/request-a-quote/`

使用多步骤表单：

1. Product。
2. Specification。
3. Requirements。
4. Attachments。
5. Contact。
6. Review & Submit。

字段：

- Product type。
- Grade。
- Standard。
- Shape。
- Dimensions。
- Quantity。
- Unit。
- Tolerance。
- Surface finish。
- Processing requirements。
- Certificate requirements。
- Destination country。
- Incoterms。
- Expected delivery time。
- File uploads。
- Contact name。
- Company。
- Email。
- WhatsApp / phone。
- Message。

交互：

- 从产品页进入时自动带入产品。
- 草稿保存在 localStorage。
- 文件上传先做前端 mock。
- 表单校验清晰提示。
- 提交后展示 RFQ 编号。
- 提交后显示：We will review your request within 24 hours。

### 5.12 Contact 页面

模块：

- Contact form。
- WhatsApp。
- Email。
- Address。
- Business hours。
- Map 占位。
- Sales contact cards。
- Upload inquiry sheet 入口。

### 5.13 Quality 页面

路径：`/en/quality/`

模块：

- Quality Assurance。
- MTR Available。
- Chemical / mechanical testing。
- Third-party inspection。
- Packaging control。
- Document examples。
- Certificate cards。

### 5.14 About 页面

模块：

- CNBJTI 定位。
- Baoji Titanium Valley 背书。
- Supply chain capability。
- Export experience。
- Values。
- Factory / warehouse / processing photos。

### 5.15 法务页面

必须有：

- Privacy Policy。
- Cookie Policy。
- Terms of Use。

## 6. 官网组件规划

### 6.1 Layout 组件

- `SiteHeader`。
- `SiteMegaMenu`。
- `LanguageSwitcher`。
- `StickyContactBar`。
- `MobileBottomActions`。
- `SiteFooter`。
- `Breadcrumbs`。
- `SeoHead`。

### 6.2 产品组件

- `ProductCategoryCard`。
- `ProductCard`。
- `ProductGallery`。
- `ProductSpecTable`。
- `GradeChips`。
- `StandardChips`。
- `SurfaceFinishList`。
- `ProcessingOptions`。
- `RelatedProducts`。

### 6.3 RFQ 组件

- `QuickRfqPanel`。
- `RfqStepper`。
- `RfqProductStep`。
- `RfqSpecificationStep`。
- `RfqRequirementStep`。
- `RfqAttachmentStep`。
- `RfqContactStep`。
- `RfqReviewStep`。
- `FileDropzone`。
- `RfqSuccessCard`。

### 6.4 内容组件

- `PageHero`。
- `SectionHeader`。
- `TrustStrip`。
- `CapabilityGrid`。
- `IndustryCard`。
- `ResourceCard`。
- `FaqAccordion`。
- `DownloadCard`。
- `CertificateCard`。
- `ShipmentProofCard`。

### 6.5 工具组件

- `UnitInput`。
- `UnitSwitcher`。
- `WeightCalculatorForm`。
- `CopyButton`。
- `EmptyState`。
- `LoadingState`。
- `ErrorState`。

## 7. 官网视觉方向

### 7.1 风格关键词

- Industrial。
- Precise。
- Export-ready。
- Trustworthy。
- Technical。
- Clean。

### 7.2 颜色建议

避免廉价蓝色模板感，建议：

- 主色：深石墨灰 / 钛灰。
- 强调色：工业蓝或青蓝，少量使用在 CTA。
- 辅助色：冷白、浅灰、钢色。
- 状态色：绿色表示 available / verified，橙色表示 attention。

不要做大面积紫蓝渐变，不要做花哨科技感背景。钛材买家更吃“真实、清楚、专业”。

### 7.3 字体与排版

- 英文优先，标题清晰但不夸张。
- 表格信息要易扫读。
- 产品详情页的信息密度要高。
- 移动端优先保证 RFQ 按钮和联系方式可见。

### 7.4 图片策略

第一版先用真实素材占位结构：

- 产品实拍。
- 车间。
- 仓库。
- 检测。
- 包装。
- 装柜。

如果素材暂时不足，前端先放高质量占位图和明确的图片位，但文案标注要真实，不能伪造证书和客户案例。

## 8. 管理后台信息架构

### 8.1 菜单结构

```text
Dashboard

Catalog
  Product Categories
  Products
  Titanium Grades
  Standards
  Industries
  Processing Services
  Specification Templates

RFQ & CRM
  RFQs
  Customers
  Follow-ups
  Spam Review

Content
  Pages
  Articles
  FAQs
  Case Studies
  Banners
  Navigation

Media
  Media Library
  Documents
  Uploads

Translations
  Languages
  Translation Queue
  Termbase

SEO
  SEO Overview
  Redirects
  Sitemap
  Schema Preview

System
  Users
  Roles
  Permissions
  Settings
  Email Templates
  Audit Logs
```

第二阶段加：

```text
Quotes
  Quotes
  Quote Templates
  Approval

Orders
  Orders
  Shipments
  Documents

Analytics
  Traffic
  Conversions
  Product Performance
```

### 8.2 Dashboard

卡片：

- Today RFQs。
- New RFQs。
- Need Follow-up。
- Pending Translation。
- Published Products。
- Top Product Pages。

图表：

- RFQ trend。
- RFQ source。
- RFQ by product type。
- RFQ by country。

列表：

- Latest RFQs。
- Content tasks。
- Recent activities。

### 8.3 Product Categories

功能：

- 树形分类。
- 新增/编辑/排序。
- slug。
- 图标/图片。
- SEO 字段。
- 多语言内容。
- 启用/停用。

### 8.4 Products

列表字段：

- Product image。
- Product title。
- Category。
- Grades。
- Standards。
- Status。
- Language completeness。
- Updated at。

筛选：

- 分类。
- 牌号。
- 标准。
- 发布状态。
- 语言状态。

编辑页 Tabs：

- Basic。
- Specifications。
- Grades & Standards。
- Media。
- Processing。
- Applications。
- SEO。
- Translations。
- Related Content。

### 8.5 Titanium Grades

功能：

- 牌号管理。
- 化学成分表。
- 机械性能表。
- 等效牌号。
- 相关产品。
- SEO。
- 多语言。

### 8.6 Standards

功能：

- 标准管理。
- 标准适用产品。
- 标准适用牌号。
- 内容说明。
- SEO。
- 多语言。

### 8.7 RFQs

列表字段：

- RFQ No。
- Customer。
- Company。
- Country。
- Product type。
- Grade。
- Quantity。
- Source。
- Status。
- Owner。
- Created at。

筛选：

- 状态。
- 业务员。
- 国家。
- 产品类型。
- 牌号。
- 来源。
- 是否有附件。

详情页布局：

- 左侧：客户与询盘摘要。
- 中间：RFQ Items、规格、证书、附件。
- 右侧：状态、分配、跟进、内部备注。

操作：

- Assign。
- Mark as spam。
- Need technical review。
- Create quote，第二阶段可先占位。
- Add follow-up。
- Export。

### 8.8 Customers

功能：

- 客户列表。
- 客户详情。
- 联系人。
- 历史 RFQ。
- 国家/行业。
- 标签。
- 黑名单。
- 内部备注。

### 8.9 Content

页面管理：

- 首页模块。
- About。
- Quality。
- Processing landing。
- Industry landing。
- Contact。

文章管理：

- 列表。
- 编辑。
- 分类。
- 标签。
- SEO。
- 发布状态。
- 多语言。

FAQ：

- 问题。
- 答案。
- 分类。
- 关联产品/页面。

### 8.10 Media Library

功能：

- 图片/视频/PDF/附件列表。
- 文件夹或标签。
- 文件类型筛选。
- 公开/私有。
- 复制 URL。
- 图片 alt 文本。
- 文件关联记录。

前端阶段先做 UI 和 mock 上传，等后端接 MinIO 后再真实上传。

### 8.11 Translation Center

功能：

- 语言列表。
- 内容翻译完成度。
- 翻译队列。
- 术语库。
- 状态：Not started、Machine draft、Human reviewed、Published。
- 对照编辑：左侧英文，右侧目标语言。

### 8.12 SEO Center

功能：

- 缺失 title 的页面。
- 缺失 description 的页面。
- slug 冲突。
- 未设置 og image。
- sitemap 预览。
- 301 redirect 管理。
- schema 预览。

### 8.13 System

功能：

- 用户管理。
- 角色管理。
- 权限管理。
- 菜单管理。
- 邮件模板。
- 站点配置。
- 操作日志。
- 登录日志。

## 9. 管理后台组件规划

### 9.1 Layout

- `AdminLayout`。
- `SidebarNav`。
- `TopBar`。
- `BreadcrumbBar`。
- `UserMenu`。
- `PageTabs`。

### 9.2 通用业务组件

- `DataTable`。
- `SearchFilters`。
- `StatusTag`。
- `LanguageStatusBadge`。
- `SeoScoreBadge`。
- `MediaPicker`。
- `FileUploadPanel`。
- `RichTextEditor`。
- `SpecTableEditor`。
- `GradeSelector`。
- `StandardSelector`。
- `OwnerSelector`。
- `CountrySelector`。

### 9.3 RFQ 组件

- `RfqStatusTag`。
- `RfqSummaryCard`。
- `RfqItemTable`。
- `RfqAttachmentList`。
- `FollowUpTimeline`。
- `AssignmentPanel`。
- `InternalNoteBox`。
- `CustomerSnapshot`。

### 9.4 内容编辑组件

- `SeoEditor`。
- `SlugInput`。
- `TranslationTabs`。
- `PublishControls`。
- `RelatedContentPicker`。
- `FaqEditor`。
- `ArticleEditor`。

## 10. Mock 数据与接口策略

前端先于后端开发，必须先做 Mock API。

### 10.1 Mock 原则

- 数据结构尽量贴近后端最终 API。
- 所有列表支持分页、筛选、排序的假实现。
- RFQ 提交返回模拟 RFQ 编号。
- 文件上传先返回 mock file id。
- 后台登录使用 mock 用户和权限。
- 所有 mock 数据集中放在 `packages/mock-data`。

### 10.2 官网 mock API

```text
GET  /api/public/site-config
GET  /api/public/navigation
GET  /api/public/products
GET  /api/public/products/:slug
GET  /api/public/categories/:slug
GET  /api/public/grades/:slug
GET  /api/public/standards/:slug
GET  /api/public/articles
GET  /api/public/articles/:slug
POST /api/public/rfqs
POST /api/public/uploads/mock
```

### 10.3 后台 mock API

```text
POST /api/auth/login
GET  /api/admin/me
GET  /api/admin/dashboard

GET  /api/admin/products
POST /api/admin/products
GET  /api/admin/products/:id
PUT  /api/admin/products/:id

GET  /api/admin/rfqs
GET  /api/admin/rfqs/:id
PUT  /api/admin/rfqs/:id/status
POST /api/admin/rfqs/:id/followups

GET  /api/admin/customers
GET  /api/admin/media
GET  /api/admin/articles
GET  /api/admin/translations
```

### 10.4 类型策略

先手写 TypeScript 类型：

- `Product`。
- `ProductCategory`。
- `TitaniumGrade`。
- `Standard`。
- `Article`。
- `MediaAsset`。
- `Rfq`。
- `RfqItem`。
- `Customer`。
- `AdminUser`。
- `Language`。
- `SeoMeta`。

后端 OpenAPI 出来后，用生成类型替换手写类型。

## 11. SEO 前端要求

官网每个页面必须实现：

- 唯一 title。
- meta description。
- canonical。
- hreflang。
- og title。
- og description。
- og image。
- breadcrumb。
- 页面结构化 H1/H2。

页面类型 schema：

- 首页：Organization。
- 产品页：Product。
- FAQ：FAQPage。
- 文章：Article。
- 面包屑：BreadcrumbList。

sitemap：

- 产品。
- 分类。
- 牌号。
- 标准。
- 行业。
- 加工。
- 文章。

## 12. 表单与转化设计

所有关键页面都要有询价入口：

- Header CTA。
- 产品卡片 CTA。
- 产品详情 sticky CTA。
- 分类页 RFQ。
- 文章结尾 RFQ。
- 计算器结果 RFQ。
- Contact 页上传询盘。

RFQ 提交事件：

- `rfq_started`。
- `rfq_file_added`。
- `rfq_submitted`。
- `whatsapp_clicked`。
- `email_clicked`。
- `product_quote_clicked`。

前端先把事件封装到 `useAnalytics()`，后面再接 GA4、Plausible 或其它统计工具。

## 13. 响应式要求

桌面端：

- 适合采购人员快速扫表格。
- 产品详情可使用两栏：主内容 + sticky RFQ。
- Mega menu 展示完整分类。

平板：

- 表格允许横向滚动。
- RFQ card 下移。

移动端：

- 固定底部 CTA。
- 表单分步骤。
- 产品规格表可折叠。
- 联系方式必须一键触达。

断点建议：

```text
sm 640
md 768
lg 1024
xl 1280
2xl 1536
```

## 14. 无障碍与可用性

- 所有表单字段有 label。
- 错误提示明确。
- 按钮有 loading 和 disabled 状态。
- 图片有 alt。
- 链接可键盘访问。
- 颜色对比度合格。
- 表格移动端不挤压文本。
- 文件上传失败要能重试。

## 15. 前端阶段开发顺序

### 阶段 0：项目骨架

- 初始化 monorepo。
- 创建 `apps/web`。
- 创建 `apps/admin`。
- 配置 TypeScript、ESLint、Prettier。
- 配置 Tailwind。
- 配置基础 mock 数据。
- 配置统一类型。

### 阶段 1：官网基础

- 官网 layout。
- 首页。
- 产品分类页。
- 产品详情页。
- RFQ 页面。
- Contact 页面。
- 多语言路由结构。
- SEO 基础。

### 阶段 2：官网 SEO 内容页

- Grades。
- Standards。
- Processing。
- Industries。
- Resources。
- Titanium Weight Calculator。
- Quality。
- About。
- FAQ。

### 阶段 3：后台基础

- 登录页。
- Admin layout。
- Dashboard。
- 产品分类管理。
- 产品管理。
- 媒体库。
- 页面/文章管理。

### 阶段 4：后台询盘与客户

- RFQ 列表。
- RFQ 详情。
- 跟进时间线。
- 客户管理。
- 附件列表。
- 状态流转。

### 阶段 5：后台多语言与 SEO

- Translation Center。
- SEO Center。
- Redirects。
- Sitemap preview。
- Email templates。
- Users/Roles。

### 阶段 6：前后端联调准备

- 把 mock API 封装替换为真实 API base。
- 对齐字段名。
- 补充错误态。
- 补充权限控制。
- 补充上传流程。
- 准备 OpenAPI 类型生成。

## 16. 第一版验收标准

官网：

- 能打开英文首页。
- 能浏览产品分类和产品详情。
- 能从产品页进入 RFQ 并自动带入产品。
- 能提交 mock RFQ 并看到 RFQ 编号。
- 能使用重量计算器。
- 产品、牌号、标准、文章页面都有 SEO meta。
- 移动端能正常使用。

后台：

- 能 mock 登录。
- 能查看 Dashboard。
- 能查看和编辑产品。
- 能查看 RFQ 列表和详情。
- 能改变 RFQ 状态。
- 能查看附件列表。
- 能查看客户信息。
- 能查看翻译任务和 SEO 检查。

质量：

- TypeScript 无明显类型错误。
- 主要页面无布局错位。
- 表单有校验。
- loading、empty、error 状态齐全。
- 页面适配桌面和移动。

## 17. 不在第一版前端范围内

这些先做占位或隐藏，不进入第一版完整开发：

- 在线支付。
- 真实客户登录门户。
- 真实报价 PDF 生成。
- 报价审批全流程。
- 订单和出货管理。
- ERP 对接。
- 在线聊天机器人。
- 实时库存。
- 复杂营销自动化。

## 18. 前端素材清单

为了让页面有真实质感，前端开始前最好准备：

- Logo。
- 品牌英文名和中文名。
- 产品图片：每类至少 5 张。
- 工厂/仓库/加工/检测/包装/装柜图。
- 证书图片或 PDF 样例。
- MTR 样例，隐去客户信息。
- 产品目录 PDF。
- 常用规格表。
- 联系邮箱、WhatsApp、电话、地址。
- 公司简介英文版。

没有素材时，前端先用占位素材，但必须保留真实素材位。

## 19. 前端优先的关键决策

- 官网用 Nuxt 3，不用普通 Vue SPA。
- 后台用 Vue 3 + Vite + Element Plus。
- 前端第一阶段用 mock API，页面和流程先跑通。
- RFQ 是官网最核心转化，不是普通留言表单。
- 产品、牌号、标准、行业、文章都是 SEO 页面，不是后台随便填的普通内容。
- 多语言第一版搭结构，先发布英文，后续逐步审核发布其它语言。
- 前端接口类型先手写，后端 OpenAPI 出来后再生成替换。
