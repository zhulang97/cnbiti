<template>
  <div class="space-y-5">
    <div class="sticky top-0 z-30 flex items-center justify-between gap-4 rounded-xl border border-admin-700/70 bg-admin-950/95 px-4 py-3 shadow-lg shadow-admin-950/30 backdrop-blur">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">站点设置</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护官网联系方式、首页内容、媒体资源和关于我们页面内容。</p>
      </div>
      <el-button type="primary" size="small" :loading="saving" @click="void saveConfig()">
        <el-icon class="mr-1"><Check /></el-icon>
        保存
      </el-button>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-[1fr_360px] gap-5">
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <div class="mb-4">
          <h3 class="text-admin-100 text-sm font-semibold">公司与联系方式</h3>
          <p class="text-admin-500 text-xs mt-1">这些字段会用于官网页头、页脚、联系卡片和询盘入口。</p>
        </div>
        <el-form label-position="top" class="grid grid-cols-1 md:grid-cols-2 gap-x-4">
          <el-form-item label="站点名称" required>
            <el-input v-model="form.siteName" />
          </el-form-item>
          <el-form-item label="邮箱" required>
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="宣传语" class="md:col-span-2">
            <el-input v-model="form.tagline" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="form.phone" />
          </el-form-item>
          <el-form-item label="WhatsApp">
            <el-input v-model="form.whatsapp" />
          </el-form-item>
          <el-form-item label="城市">
            <el-input v-model="form.city" />
          </el-form-item>
          <el-form-item label="国家">
            <el-input v-model="form.country" />
          </el-form-item>
          <el-form-item label="地址" class="md:col-span-2">
            <el-input v-model="form.address" type="textarea" :rows="3" resize="none" />
          </el-form-item>
        </el-form>
      </div>

      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-4">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">社交链接</h3>
          <p class="text-admin-500 text-xs mt-1">留空的链接不会保存到官网接口。</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="LinkedIn">
            <el-input v-model="socialLinks.linkedin" />
          </el-form-item>
          <el-form-item label="WeChat">
            <el-input v-model="socialLinks.wechat" placeholder="微信号或二维码说明" />
          </el-form-item>
          <el-form-item label="微信二维码图片">
            <div class="w-full space-y-2">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.wechatQrImage" placeholder="上传后自动填入，或粘贴图片链接" />
                <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadWechatQr">
                  <el-button :loading="uploadingWechatQr">
                    <el-icon class="mr-1"><Upload /></el-icon>
                    上传
                  </el-button>
                </el-upload>
              </div>
              <img
                v-if="socialLinks.wechatQrImage"
                :src="socialLinks.wechatQrImage"
                alt="WeChat QR preview"
                class="w-28 h-28 rounded-lg bg-white object-contain p-2 border border-admin-600"
              >
            </div>
          </el-form-item>
          <el-form-item label="WhatsApp 二维码图片">
            <div class="w-full space-y-2">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.whatsappQrImage" placeholder="上传后自动填入，或粘贴图片链接" />
                <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadWhatsappQr">
                  <el-button :loading="uploadingWhatsappQr">
                    <el-icon class="mr-1"><Upload /></el-icon>
                    上传
                  </el-button>
                </el-upload>
              </div>
              <img
                v-if="socialLinks.whatsappQrImage"
                :src="socialLinks.whatsappQrImage"
                alt="WhatsApp QR preview"
                class="w-28 h-28 rounded-lg bg-white object-contain p-2 border border-admin-600"
              >
            </div>
          </el-form-item>
          <el-form-item label="Twitter / X">
            <el-input v-model="socialLinks.twitter" />
          </el-form-item>
          <el-form-item label="YouTube">
            <el-input v-model="socialLinks.youtube" />
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-5">
      <div>
        <h3 class="text-admin-100 text-sm font-semibold">首页媒体资源</h3>
        <p class="text-admin-500 text-xs mt-1">工厂介绍视频、视频封面和联系卡片图片建议上传到本地 MinIO。</p>
      </div>

      <el-form label-position="top" class="grid grid-cols-1 items-start gap-4 xl:grid-cols-3">
        <el-form-item label="工厂介绍视频">
          <div class="w-full space-y-3">
            <div class="flex gap-2">
              <el-input v-model="socialLinks.homeHeroVideo" placeholder="上传 MP4/WebM 后自动填入，或粘贴视频链接" />
              <el-upload :show-file-list="false" accept="video/mp4,video/webm,video/quicktime,video/*" :before-upload="uploadHomeHeroVideo">
                <el-button :loading="uploadingHomeHeroVideo">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传
                </el-button>
              </el-upload>
            </div>
            <video
              v-if="socialLinks.homeHeroVideo"
              :src="socialLinks.homeHeroVideo"
              class="h-56 w-full rounded-lg border border-admin-600 bg-admin-950 object-contain xl:h-64"
              controls
              muted
              preload="metadata"
            />
          </div>
        </el-form-item>
        <el-form-item label="视频封面图片">
          <div class="w-full space-y-3">
            <div class="flex gap-2">
              <el-input v-model="socialLinks.homeHeroPosterImage" placeholder="上传后自动填入，或粘贴图片链接" />
              <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadHomeHeroPoster">
                <el-button :loading="uploadingHomeHeroPoster">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传
                </el-button>
              </el-upload>
            </div>
            <a
              v-if="socialLinks.homeHeroPosterImage"
              :href="socialLinks.homeHeroPosterImage"
              target="_blank"
              rel="noreferrer"
              class="block rounded-xl border border-admin-600 bg-admin-950 p-2"
            >
              <img
                :src="socialLinks.homeHeroPosterImage"
                alt="Home hero poster preview"
                class="h-56 w-full rounded-lg bg-admin-950 object-contain xl:h-64"
              >
            </a>
          </div>
        </el-form-item>
        <el-form-item label="联系卡片图片">
          <div class="w-full space-y-3">
            <div class="flex gap-2">
              <el-input v-model="socialLinks.contactCardImage" placeholder="上传后自动填入，或粘贴图片链接" />
              <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadContactCardImage">
                <el-button :loading="uploadingContactCardImage">
                  <el-icon class="mr-1"><Upload /></el-icon>
                  上传
                </el-button>
              </el-upload>
            </div>
            <a
              v-if="socialLinks.contactCardImage"
              :href="socialLinks.contactCardImage"
              target="_blank"
              rel="noreferrer"
              class="block rounded-xl border border-admin-600 bg-admin-950 p-2"
            >
              <img
                :src="socialLinks.contactCardImage"
                alt="Contact card preview"
                class="h-56 w-full rounded-lg bg-admin-950 object-contain xl:h-64"
              >
            </a>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-5">
      <div class="flex items-center justify-between gap-3">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">官网首页内容</h3>
          <p class="text-admin-500 text-xs mt-1">这里填写的英文内容会展示到官网首页的首屏、产品说明、加工能力和质量模块。</p>
        </div>
        <el-button size="small" @click="resetHomePage">
          恢复默认
        </el-button>
      </div>

      <el-form label-position="top" class="space-y-5">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="首屏标题" class="lg:col-span-2">
            <el-input v-model="homePage.heroTitle" />
          </el-form-item>
          <el-form-item label="首屏简介">
            <el-input v-model="homePage.heroIntro" type="textarea" :rows="3" resize="none" />
          </el-form-item>
          <el-form-item label="首屏补充说明">
            <el-input v-model="homePage.heroBody" type="textarea" :rows="3" resize="none" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">首屏统计数据</h4>
            <el-button size="small" text @click="addHomeStat">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-2">
            <div v-for="(item, index) in homePage.stats" :key="`home-stat-${index}`" class="grid grid-cols-[120px_1fr_auto] gap-2">
              <el-input v-model="item.value" placeholder="2008" />
              <el-input v-model="item.label" placeholder="Founded in Baoji" />
              <el-button circle text type="danger" @click="removeHomeStat(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">首屏卖点</h4>
            <el-button size="small" text @click="addHomeProofPoint">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in homePage.proofPoints" :key="`proof-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[90px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.code" placeholder="MTR" />
                <el-input v-model="item.title" placeholder="MTR Available" />
                <el-button circle text type="danger" @click="removeHomeProofPoint(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="简短说明" />
            </div>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">产品区采购说明</h4>
            <el-button size="small" text @click="addHomeBuyerNote">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in homePage.buyerNotes" :key="`buyer-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[90px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.code" placeholder="CUT" />
                <el-input v-model="item.title" placeholder="Cut-to-size Supply" />
                <el-button circle text type="danger" @click="removeHomeBuyerNote(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="简短说明" />
            </div>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">加工能力</h4>
            <el-button size="small" text @click="addHomeCapability">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in homePage.capabilities" :key="`capability-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[1fr_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.title" placeholder="CNC Machining" />
                <el-input v-model="item.desc" placeholder="Turning, milling, drilling" />
                <el-button circle text type="danger" @click="removeHomeCapability(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <div class="grid grid-cols-1 gap-2 lg:grid-cols-[120px_minmax(0,1fr)_minmax(0,1fr)]">
                <div class="h-20 rounded-lg border border-admin-600 bg-admin-950 overflow-hidden">
                  <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.imageAlt || item.title" class="h-full w-full object-contain bg-white">
                  <div v-else class="flex h-full items-center justify-center text-xs text-admin-500">暂无图片</div>
                </div>
                <div class="flex gap-2">
                  <el-input v-model="item.imageUrl" placeholder="可选图片链接" />
                  <el-upload :show-file-list="false" accept="image/*" action="#" :http-request="capabilityUploadRequest(index)">
                    <el-button :loading="uploadingCapabilities[`capability-${index}`]">
                      <el-icon class="mr-1"><Upload /></el-icon>
                      上传
                    </el-button>
                  </el-upload>
                </div>
                <el-input v-model="item.imageAlt" placeholder="可选图片说明" />
              </div>
            </div>
          </div>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">质量文档项</h4>
            <el-button size="small" text @click="addHomeQualityItem">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in homePage.qualityItems" :key="`quality-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[90px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.code" placeholder="MTR" />
                <el-input v-model="item.title" placeholder="Mill Test Report" />
                <el-button circle text type="danger" @click="removeHomeQualityItem(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="简短说明" />
            </div>
          </div>
        </div>
      </el-form>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-5">
      <div class="flex items-center justify-between gap-3">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">官网关于我们页面</h3>
          <p class="text-admin-500 text-xs mt-1">保存后会同步展示到官网关于我们页面。</p>
        </div>
        <el-button size="small" @click="resetAboutPage">
          恢复默认
        </el-button>
      </div>

      <el-form label-position="top" class="space-y-5">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="首屏标签">
            <el-input v-model="aboutPage.heroLabel" />
          </el-form-item>
          <el-form-item label="首屏标题">
            <el-input v-model="aboutPage.heroTitle" />
          </el-form-item>
          <el-form-item label="首屏简介" class="lg:col-span-2">
            <el-input v-model="aboutPage.heroIntro" type="textarea" :rows="3" resize="none" />
          </el-form-item>
          <el-form-item label="首屏补充说明" class="lg:col-span-2">
            <el-input v-model="aboutPage.heroBody" type="textarea" :rows="3" resize="none" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">统计数据</h4>
            <el-button size="small" text @click="addAboutStat">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-2">
            <div v-for="(item, index) in aboutPage.stats" :key="`stat-${index}`" class="grid grid-cols-[120px_1fr_auto] gap-2">
              <el-input v-model="item.value" placeholder="数值" />
              <el-input v-model="item.label" placeholder="说明" />
              <el-button circle text type="danger" @click="removeAboutStat(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="地理优势标签">
            <el-input v-model="aboutPage.locationLabel" />
          </el-form-item>
          <el-form-item label="地理优势标题">
            <el-input v-model="aboutPage.locationTitle" />
          </el-form-item>
          <el-form-item label="地理优势描述" class="lg:col-span-2">
            <el-input v-model="aboutPage.locationDescription" type="textarea" :rows="3" resize="none" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">地理/供应链优势</h4>
            <el-button size="small" text @click="addAboutAdvantage">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in aboutPage.advantages" :key="`advantage-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[80px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.icon" placeholder="图标" />
                <el-input v-model="item.title" placeholder="标题" />
                <el-button circle text type="danger" @click="removeAboutAdvantage(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="描述" />
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="历程标签">
            <el-input v-model="aboutPage.timelineLabel" />
          </el-form-item>
          <el-form-item label="历程标题">
            <el-input v-model="aboutPage.timelineTitle" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">发展历程</h4>
            <el-button size="small" text @click="addAboutTimelineEvent">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in aboutPage.timeline" :key="`timeline-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[100px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.year" placeholder="年份" />
                <el-input v-model="item.title" placeholder="标题" />
                <el-button circle text type="danger" @click="removeAboutTimelineEvent(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="描述" />
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="价值观标签">
            <el-input v-model="aboutPage.valuesLabel" />
          </el-form-item>
          <el-form-item label="价值观标题">
            <el-input v-model="aboutPage.valuesTitle" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">价值观</h4>
            <el-button size="small" text @click="addAboutValue">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加
            </el-button>
          </div>
          <div class="space-y-3">
            <div v-for="(item, index) in aboutPage.values" :key="`value-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-[80px_1fr_auto] gap-2 mb-2">
                <el-input v-model="item.icon" placeholder="图标" />
                <el-input v-model="item.title" placeholder="标题" />
                <el-button circle text type="danger" @click="removeAboutValue(index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <el-input v-model="item.desc" type="textarea" :rows="2" resize="none" placeholder="描述" />
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="CTA 标题">
            <el-input v-model="aboutPage.ctaTitle" />
          </el-form-item>
          <el-form-item label="SEO 标题">
            <el-input v-model="aboutPage.seoTitle" />
          </el-form-item>
          <el-form-item label="CTA 描述" class="lg:col-span-2">
            <el-input v-model="aboutPage.ctaDescription" type="textarea" :rows="2" resize="none" />
          </el-form-item>
          <el-form-item label="SEO 描述" class="lg:col-span-2">
            <el-input v-model="aboutPage.seoDescription" type="textarea" :rows="2" resize="none" />
          </el-form-item>
        </div>
      </el-form>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-5">
      <div class="flex items-center justify-between gap-3">
        <h3 class="text-admin-100 text-sm font-semibold">官网证书页面</h3>
      </div>

      <el-form label-position="top" class="space-y-5">
        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">证书图片</h4>
            <el-button size="small" text :disabled="certificatesPage.items.length >= 6" @click="addCertificateItem">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加图片
            </el-button>
          </div>
          <div v-if="!certificatesPage.items.length" class="rounded-lg border border-dashed border-admin-600/70 p-6 text-center text-xs text-admin-500">
            还没有证书图片，点击“添加图片”后上传。
          </div>
          <div v-else class="grid grid-cols-1 xl:grid-cols-2 gap-3">
            <div v-for="(item, index) in certificatesPage.items" :key="`cert-gallery-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-1 md:grid-cols-[150px_1fr_auto] gap-3">
                <div class="h-40 rounded-lg border border-admin-600 bg-admin-950 overflow-hidden">
                  <img v-if="item.imageUrl" :src="item.imageUrl" alt="Certificate image" class="h-full w-full object-contain bg-white">
                  <div v-else class="flex h-full items-center justify-center text-xs text-admin-500">暂无图片</div>
                </div>
                <div class="flex items-center gap-2">
                  <el-input v-model="item.imageUrl" placeholder="图片链接，上传后自动填入" />
                  <el-upload :show-file-list="false" accept="image/*" action="#" :http-request="galleryUploadRequest('certificatesPage', index)">
                    <el-button :loading="uploadingGallery[`certificatesPage-${index}`]">
                      <el-icon class="mr-1"><Upload /></el-icon>
                      上传
                    </el-button>
                  </el-upload>
                </div>
                <el-button circle text type="danger" @click="removeGalleryItem(certificatesPage, index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-form>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-5">
      <div class="flex items-center justify-between gap-3">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">官网工厂参观页面</h3>
          <p class="text-admin-500 text-xs mt-1">管理 /factory-tour 页面文案和工厂图库，可上传车间、设备、检测、生产线图片。</p>
        </div>
        <el-button size="small" @click="resetFactoryTourPage">恢复默认</el-button>
      </div>

      <el-form label-position="top" class="space-y-5">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="页面标签">
            <el-input v-model="factoryTourPage.heroLabel" />
          </el-form-item>
          <el-form-item label="页面标题">
            <el-input v-model="factoryTourPage.heroTitle" />
          </el-form-item>
          <el-form-item label="页面简介" class="lg:col-span-2">
            <el-input v-model="factoryTourPage.heroIntro" type="textarea" :rows="3" resize="none" />
          </el-form-item>
          <el-form-item label="SEO 标题">
            <el-input v-model="factoryTourPage.seoTitle" />
          </el-form-item>
          <el-form-item label="SEO 描述">
            <el-input v-model="factoryTourPage.seoDescription" />
          </el-form-item>
        </div>

        <div class="space-y-3">
          <div class="flex items-center justify-between">
            <h4 class="text-admin-200 text-xs font-semibold">工厂图片</h4>
            <el-button size="small" text @click="addGalleryItem(factoryTourPage)">
              <el-icon class="mr-1"><Plus /></el-icon>
              添加图片
            </el-button>
          </div>
          <div v-if="!factoryTourPage.items.length" class="rounded-lg border border-dashed border-admin-600/70 p-6 text-center text-xs text-admin-500">
            还没有工厂图片，点击“添加图片”后上传。
          </div>
          <div v-else class="grid grid-cols-1 xl:grid-cols-2 gap-3">
            <div v-for="(item, index) in factoryTourPage.items" :key="`factory-gallery-${index}`" class="border border-admin-600/40 rounded-lg p-3">
              <div class="grid grid-cols-1 md:grid-cols-[150px_1fr_auto] gap-3">
                <div class="h-40 rounded-lg border border-admin-600 bg-admin-950 overflow-hidden">
                  <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.imageAlt || item.title" class="h-full w-full object-contain bg-white">
                  <div v-else class="flex h-full items-center justify-center text-xs text-admin-500">暂无图片</div>
                </div>
                <div class="space-y-2">
                  <el-input v-model="item.title" placeholder="标题，例如 CNC Machining Workshop" />
                  <el-input v-model="item.desc" placeholder="说明，可选" />
                  <el-input v-model="item.imageAlt" placeholder="图片 Alt 文本，可选" />
                  <div class="flex gap-2">
                    <el-input v-model="item.imageUrl" placeholder="图片链接，上传后自动填入" />
                    <el-upload :show-file-list="false" accept="image/*" action="#" :http-request="galleryUploadRequest('factoryTourPage', index)">
                      <el-button :loading="uploadingGallery[`factoryTourPage-${index}`]">
                        <el-icon class="mr-1"><Upload /></el-icon>
                        上传
                      </el-button>
                    </el-upload>
                  </div>
                </div>
                <el-button circle text type="danger" @click="removeGalleryItem(factoryTourPage, index)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-form>
    </div>

    <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <div class="text-admin-500 text-xs">站点</div>
          <div class="text-admin-100 text-sm font-medium mt-1">{{ form.siteName || '-' }}</div>
        </div>
        <div>
          <div class="text-admin-500 text-xs">邮箱</div>
          <div class="text-admin-100 text-sm font-medium mt-1">{{ form.email || '-' }}</div>
        </div>
        <div>
          <div class="text-admin-500 text-xs">电话</div>
          <div class="text-admin-100 text-sm font-medium mt-1">{{ form.phone || '-' }}</div>
        </div>
        <div>
          <div class="text-admin-500 text-xs">位置</div>
          <div class="text-admin-100 text-sm font-medium mt-1">{{ locationSummary }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRawFile, UploadRequestOptions } from 'element-plus'
import { defaultAboutPageConfig, defaultCertificatesPageConfig, defaultFactoryTourPageConfig, defaultHomePageConfig, useMockStore } from '@/stores/mock'
import type {
  AboutFeature,
  AboutPageConfig,
  AboutStat,
  AboutTimelineEvent,
  GalleryPageConfig,
  HomeCapability,
  HomeFeature,
  HomePageConfig,
  HomeQualityItem,
  HomeStat,
  SiteConfig,
} from '@cnbjti/types'

const mock = useMockStore()
const saving = ref(false)
const uploadingHomeHeroVideo = ref(false)
const uploadingHomeHeroPoster = ref(false)
const uploadingContactCardImage = ref(false)
const uploadingWechatQr = ref(false)
const uploadingWhatsappQr = ref(false)
const uploadingGallery = reactive<Record<string, boolean>>({})
const uploadingCapabilities = reactive<Record<string, boolean>>({})
const form = reactive<SiteConfig>(emptyConfig())
const aboutPage = reactive<AboutPageConfig>(defaultAboutPageConfig())
const homePage = reactive<HomePageConfig>(defaultHomePageConfig())
const certificatesPage = reactive<GalleryPageConfig>(defaultCertificatesPageConfig())
const factoryTourPage = reactive<GalleryPageConfig>(defaultFactoryTourPageConfig())
const socialLinks = reactive({
  wechat: '',
  homeHeroVideo: '',
  homeHeroPosterImage: '',
  contactCardImage: '',
  wechatQrImage: '',
  whatsappQrImage: '',
  linkedin: '',
  twitter: '',
  youtube: '',
})

const locationSummary = computed(() => {
  return [form.city, form.country].filter(Boolean).join(', ') || '-'
})

onMounted(() => {
  void loadConfig()
})

async function loadConfig() {
  const data = await mock.loadSiteConfig(true)
  fillForm(data)
}

async function saveConfig() {
  if (!form.siteName.trim() || !form.email.trim()) {
    ElMessage.warning('请填写站点名称和联系邮箱。')
    return
  }

  saving.value = true
  try {
    const updated = await mock.saveSiteConfig({
      siteName: form.siteName.trim(),
      tagline: form.tagline.trim(),
      email: form.email.trim(),
      phone: form.phone.trim(),
      whatsapp: form.whatsapp.trim(),
      address: form.address.trim(),
      city: form.city.trim(),
      country: form.country.trim(),
      socialLinks: cleanLinks(socialLinks),
      aboutPage: cleanAboutPage(aboutPage),
      homePage: cleanHomePage(homePage),
      certificatesPage: cleanCertificatePage(certificatesPage),
      factoryTourPage: cleanGalleryPage(factoryTourPage, defaultFactoryTourPageConfig()),
    })
    fillForm(updated)
    ElMessage.success('站点设置已保存。')
  } finally {
    saving.value = false
  }
}

function fillForm(data: SiteConfig) {
  const nextAboutPage = data.aboutPage ? cleanAboutPage(data.aboutPage) : defaultAboutPageConfig()
  const nextHomePage = data.homePage ? cleanHomePage(data.homePage) : defaultHomePageConfig()
  const nextCertificatesPage = cleanCertificatePage(data.certificatesPage)
  const nextFactoryTourPage = cleanGalleryPage(data.factoryTourPage, defaultFactoryTourPageConfig())

  Object.assign(form, {
    siteName: data.siteName || '',
    tagline: data.tagline || '',
    email: data.email || '',
    phone: data.phone || '',
    whatsapp: data.whatsapp || '',
    address: data.address || '',
    city: data.city || '',
    country: data.country || '',
    socialLinks: data.socialLinks || {},
    aboutPage: nextAboutPage,
    homePage: nextHomePage,
    certificatesPage: nextCertificatesPage,
    factoryTourPage: nextFactoryTourPage,
  })
  Object.assign(aboutPage, nextAboutPage)
  Object.assign(homePage, nextHomePage)
  Object.assign(certificatesPage, nextCertificatesPage)
  Object.assign(factoryTourPage, nextFactoryTourPage)
  socialLinks.wechat = data.socialLinks?.wechat || ''
  socialLinks.homeHeroVideo = data.socialLinks?.homeHeroVideo || ''
  socialLinks.homeHeroPosterImage = data.socialLinks?.homeHeroPosterImage || ''
  socialLinks.contactCardImage = data.socialLinks?.contactCardImage || ''
  socialLinks.wechatQrImage = data.socialLinks?.wechatQrImage || ''
  socialLinks.whatsappQrImage = data.socialLinks?.whatsappQrImage || ''
  socialLinks.linkedin = data.socialLinks?.linkedin || ''
  socialLinks.twitter = data.socialLinks?.twitter || ''
  socialLinks.youtube = data.socialLinks?.youtube || ''
}

function cleanLinks(values: typeof socialLinks): SiteConfig['socialLinks'] {
  const result: SiteConfig['socialLinks'] = {}
  if (values.wechat.trim()) result.wechat = values.wechat.trim()
  if (values.homeHeroVideo.trim()) result.homeHeroVideo = values.homeHeroVideo.trim()
  if (values.homeHeroPosterImage.trim()) result.homeHeroPosterImage = values.homeHeroPosterImage.trim()
  if (values.contactCardImage.trim()) result.contactCardImage = values.contactCardImage.trim()
  if (values.wechatQrImage.trim()) result.wechatQrImage = values.wechatQrImage.trim()
  if (values.whatsappQrImage.trim()) result.whatsappQrImage = values.whatsappQrImage.trim()
  if (values.linkedin.trim()) result.linkedin = values.linkedin.trim()
  if (values.twitter.trim()) result.twitter = values.twitter.trim()
  if (values.youtube.trim()) result.youtube = values.youtube.trim()
  return result
}

function addHomeStat() {
  homePage.stats.push({ value: '', label: '' })
}

function removeHomeStat(index: number) {
  homePage.stats.splice(index, 1)
}

function addHomeProofPoint() {
  homePage.proofPoints.push({ code: '', title: '', desc: '' })
}

function removeHomeProofPoint(index: number) {
  homePage.proofPoints.splice(index, 1)
}

function addHomeBuyerNote() {
  homePage.buyerNotes.push({ code: '', title: '', desc: '' })
}

function removeHomeBuyerNote(index: number) {
  homePage.buyerNotes.splice(index, 1)
}

function addHomeCapability() {
  homePage.capabilities.push({ title: '', desc: '', imageUrl: '', imageAlt: '' })
}

function removeHomeCapability(index: number) {
  homePage.capabilities.splice(index, 1)
}

function capabilityUploadRequest(index: number) {
  return async (options: UploadRequestOptions) => {
    await handleCapabilityUpload(options.file, index)
    return {}
  }
}

function addHomeQualityItem() {
  homePage.qualityItems.push({ code: '', title: '', desc: '' })
}

function removeHomeQualityItem(index: number) {
  homePage.qualityItems.splice(index, 1)
}

function resetHomePage() {
  Object.assign(homePage, defaultHomePageConfig())
}

function cleanHomePage(value: Partial<HomePageConfig>): HomePageConfig {
  const fallback = defaultHomePageConfig()
  return {
    heroTitle: text(value.heroTitle, fallback.heroTitle),
    heroIntro: text(value.heroIntro, fallback.heroIntro),
    heroBody: text(value.heroBody, fallback.heroBody),
    stats: cleanHomeStats(value.stats ?? fallback.stats),
    proofPoints: cleanHomeFeatures(value.proofPoints ?? fallback.proofPoints),
    buyerNotes: cleanHomeFeatures(value.buyerNotes ?? fallback.buyerNotes),
    capabilities: cleanHomeCapabilities(value.capabilities ?? fallback.capabilities),
    qualityItems: cleanHomeQualityItems(value.qualityItems ?? fallback.qualityItems),
  }
}

function cleanHomeStats(values: HomeStat[]) {
  return values
    .map((item) => ({ value: text(item.value, ''), label: text(item.label, '') }))
    .filter((item) => item.value && item.label)
}

function cleanHomeFeatures(values: HomeFeature[]) {
  return values
    .map((item) => ({
      code: text(item.code, ''),
      title: text(item.title, ''),
      desc: text(item.desc, ''),
    }))
    .filter((item) => item.title && item.desc)
}

function cleanHomeCapabilities(values: HomeCapability[]) {
  return values
    .map((item) => ({
      title: text(item.title, ''),
      desc: text(item.desc, ''),
      imageUrl: text(item.imageUrl, ''),
      imageAlt: text(item.imageAlt, ''),
    }))
    .filter((item) => item.title && item.desc)
}

function cleanHomeQualityItems(values: HomeQualityItem[]) {
  return values
    .map((item) => ({
      code: text(item.code, ''),
      title: text(item.title, ''),
      desc: text(item.desc, ''),
    }))
    .filter((item) => item.title && item.desc)
}

function addAboutStat() {
  aboutPage.stats.push({ value: '', label: '' })
}

function removeAboutStat(index: number) {
  aboutPage.stats.splice(index, 1)
}

function addAboutAdvantage() {
  aboutPage.advantages.push({ icon: '', title: '', desc: '' })
}

function removeAboutAdvantage(index: number) {
  aboutPage.advantages.splice(index, 1)
}

function addAboutTimelineEvent() {
  aboutPage.timeline.push({ year: '', title: '', desc: '' })
}

function removeAboutTimelineEvent(index: number) {
  aboutPage.timeline.splice(index, 1)
}

function addAboutValue() {
  aboutPage.values.push({ icon: '', title: '', desc: '' })
}

function removeAboutValue(index: number) {
  aboutPage.values.splice(index, 1)
}

function resetAboutPage() {
  Object.assign(aboutPage, defaultAboutPageConfig())
}

function cleanAboutPage(value: Partial<AboutPageConfig>): AboutPageConfig {
  const fallback = defaultAboutPageConfig()
  return {
    heroLabel: text(value.heroLabel, fallback.heroLabel),
    heroTitle: text(value.heroTitle, fallback.heroTitle),
    heroIntro: text(value.heroIntro, fallback.heroIntro),
    heroBody: text(value.heroBody, fallback.heroBody),
    stats: cleanAboutStats(value.stats ?? fallback.stats),
    locationLabel: text(value.locationLabel, fallback.locationLabel),
    locationTitle: text(value.locationTitle, fallback.locationTitle),
    locationDescription: text(value.locationDescription, fallback.locationDescription),
    advantages: cleanAboutFeatures(value.advantages ?? fallback.advantages),
    timelineLabel: text(value.timelineLabel, fallback.timelineLabel),
    timelineTitle: text(value.timelineTitle, fallback.timelineTitle),
    timeline: cleanAboutTimeline(value.timeline ?? fallback.timeline),
    valuesLabel: text(value.valuesLabel, fallback.valuesLabel),
    valuesTitle: text(value.valuesTitle, fallback.valuesTitle),
    values: cleanAboutFeatures(value.values ?? fallback.values),
    ctaTitle: text(value.ctaTitle, fallback.ctaTitle),
    ctaDescription: text(value.ctaDescription, fallback.ctaDescription),
    seoTitle: text(value.seoTitle, fallback.seoTitle),
    seoDescription: text(value.seoDescription, fallback.seoDescription),
  }
}

function text(value: string | null | undefined, fallback: string) {
  return value === undefined || value === null ? fallback : value.trim()
}

function cleanAboutStats(values: AboutStat[]) {
  return values
    .map((item) => ({ value: text(item.value, ''), label: text(item.label, '') }))
    .filter((item) => item.value && item.label)
}

function cleanAboutFeatures(values: AboutFeature[]) {
  return values
    .map((item) => ({ icon: text(item.icon, ''), title: text(item.title, ''), desc: text(item.desc, '') }))
    .filter((item) => item.title && item.desc)
}

function cleanAboutTimeline(values: AboutTimelineEvent[]) {
  return values
    .map((item) => ({ year: text(item.year, ''), title: text(item.title, ''), desc: text(item.desc, '') }))
    .filter((item) => item.year && item.title && item.desc)
}

function addCertificateItem() {
  if (certificatesPage.items.length >= 6) {
    ElMessage.warning('证书页最多展示 6 张图片')
    return
  }
  addGalleryItem(certificatesPage)
}

function resetFactoryTourPage() {
  Object.assign(factoryTourPage, defaultFactoryTourPageConfig())
}

function addGalleryItem(page: GalleryPageConfig) {
  page.items.push({ title: '', desc: '', imageUrl: '', imageAlt: '' })
}

function removeGalleryItem(page: GalleryPageConfig, index: number) {
  page.items.splice(index, 1)
}

function cleanGalleryPage(value: Partial<GalleryPageConfig> | null | undefined, fallback: GalleryPageConfig): GalleryPageConfig {
  const source = value || fallback
  return {
    heroLabel: text(source.heroLabel, fallback.heroLabel),
    heroTitle: text(source.heroTitle, fallback.heroTitle),
    heroIntro: text(source.heroIntro, fallback.heroIntro),
    items: cleanGalleryItems(source.items ?? fallback.items),
    seoTitle: text(source.seoTitle, fallback.seoTitle),
    seoDescription: text(source.seoDescription, fallback.seoDescription),
  }
}

function cleanCertificatePage(value: Partial<GalleryPageConfig> | null | undefined): GalleryPageConfig {
  const fallback = defaultCertificatesPageConfig()
  return {
    heroLabel: fallback.heroLabel,
    heroTitle: fallback.heroTitle,
    heroIntro: '',
    items: cleanGalleryItems(value?.items ?? []).slice(0, 6).map((item) => ({
      title: '',
      desc: '',
      imageUrl: item.imageUrl,
      imageAlt: '',
    })),
    seoTitle: fallback.seoTitle,
    seoDescription: fallback.seoDescription,
  }
}

function cleanGalleryItems(values: GalleryPageConfig['items']) {
  return values
    .map((item) => ({
      title: text(item.title, ''),
      desc: text(item.desc, ''),
      imageUrl: text(item.imageUrl, ''),
      imageAlt: text(item.imageAlt, ''),
    }))
    .filter((item) => item.imageUrl)
}

function uploadHomeHeroVideo(file: UploadRawFile) {
  void handleMediaUpload(file, 'homeHeroVideo')
  return false
}

function uploadHomeHeroPoster(file: UploadRawFile) {
  void handleMediaUpload(file, 'homeHeroPosterImage')
  return false
}

function uploadContactCardImage(file: UploadRawFile) {
  void handleMediaUpload(file, 'contactCardImage')
  return false
}

function uploadWechatQr(file: UploadRawFile) {
  void handleMediaUpload(file, 'wechatQrImage')
  return false
}

function uploadWhatsappQr(file: UploadRawFile) {
  void handleMediaUpload(file, 'whatsappQrImage')
  return false
}

type SiteMediaKey = 'homeHeroVideo' | 'homeHeroPosterImage' | 'contactCardImage' | 'wechatQrImage' | 'whatsappQrImage'
type GalleryPageKey = 'certificatesPage' | 'factoryTourPage'

function galleryUploadRequest(pageKey: GalleryPageKey, index: number) {
  return async (options: UploadRequestOptions) => {
    await handleGalleryUpload(options.file, pageKey, index)
    return {}
  }
}

async function handleMediaUpload(file: File, key: SiteMediaKey) {
  const loading = key === 'homeHeroVideo'
    ? uploadingHomeHeroVideo
    : key === 'homeHeroPosterImage'
      ? uploadingHomeHeroPoster
      : key === 'contactCardImage'
        ? uploadingContactCardImage
        : key === 'wechatQrImage'
          ? uploadingWechatQr
          : uploadingWhatsappQr
  loading.value = true
  try {
    const asset = await mock.uploadFile(file)
    socialLinks[key] = asset.url
    ElMessage.success('文件已上传。')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '文件上传失败')
  } finally {
    loading.value = false
  }
}

async function handleGalleryUpload(file: File, pageKey: GalleryPageKey, index: number) {
  const uploadKey = `${pageKey}-${index}`
  uploadingGallery[uploadKey] = true
  try {
    const asset = await mock.uploadFile(file)
    const page = pageKey === 'certificatesPage' ? certificatesPage : factoryTourPage
    const item = page.items[index]
    if (item) {
      item.imageUrl = asset.url
      if (!item.imageAlt) item.imageAlt = asset.alt || asset.filename
      if (!item.title) item.title = asset.alt || asset.filename.replace(/\.[^.]+$/, '')
    }
    ElMessage.success('图片已上传')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '图片上传失败')
    throw error
  } finally {
    uploadingGallery[uploadKey] = false
  }
}

async function handleCapabilityUpload(file: File, index: number) {
  const uploadKey = `capability-${index}`
  uploadingCapabilities[uploadKey] = true
  try {
    const asset = await mock.uploadFile(file)
    const item = homePage.capabilities[index]
    if (item) {
      item.imageUrl = asset.url
      if (!item.imageAlt) item.imageAlt = asset.alt || asset.filename
    }
    ElMessage.success('图片已上传')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '图片上传失败')
    throw error
  } finally {
    uploadingCapabilities[uploadKey] = false
  }
}

function emptyConfig(): SiteConfig {
  return {
    siteName: '',
    tagline: '',
    email: '',
    phone: '',
    whatsapp: '',
    address: '',
    city: '',
    country: '',
    socialLinks: {},
    aboutPage: defaultAboutPageConfig(),
    homePage: defaultHomePageConfig(),
    certificatesPage: defaultCertificatesPageConfig(),
    factoryTourPage: defaultFactoryTourPageConfig(),
  }
}
</script>
