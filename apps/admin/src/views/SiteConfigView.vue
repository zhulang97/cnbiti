<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">站点设置</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护前台展示的品牌、联系方式、社交链接和 About 页面内容</p>
      </div>
      <el-button type="primary" size="small" :loading="saving" @click="void saveConfig()">
        <el-icon class="mr-1"><Check /></el-icon>
        保存
      </el-button>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-[1fr_320px] gap-5">
      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5">
        <el-form label-position="top" class="grid grid-cols-2 gap-x-4">
          <el-form-item label="站点名称" required>
            <el-input v-model="form.siteName" />
          </el-form-item>
          <el-form-item label="联系邮箱" required>
            <el-input v-model="form.email" />
          </el-form-item>
          <el-form-item label="标语" class="col-span-2">
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
          <el-form-item label="地址" class="col-span-2">
            <el-input v-model="form.address" type="textarea" :rows="3" resize="none" />
          </el-form-item>
          <el-form-item label="首页介绍视频" class="col-span-2">
            <div class="w-full space-y-3">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.homeHeroVideo" placeholder="上传 MP4/WebM 视频后自动填入，或粘贴视频 URL" />
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
                class="w-64 h-36 rounded-lg bg-admin-950 object-cover border border-admin-600"
                controls
                muted
                preload="metadata"
              />
            </div>
          </el-form-item>
          <el-form-item label="首页视频封面图" class="col-span-2">
            <div class="w-full space-y-3">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.homeHeroPosterImage" placeholder="上传封面图后自动填入，或粘贴图片 URL" />
                <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadHomeHeroPoster">
                  <el-button :loading="uploadingHomeHeroPoster">
                    <el-icon class="mr-1"><Upload /></el-icon>
                    上传
                  </el-button>
                </el-upload>
              </div>
              <img
                v-if="socialLinks.homeHeroPosterImage"
                :src="socialLinks.homeHeroPosterImage"
                alt="Home hero poster preview"
                class="w-64 h-36 rounded-lg bg-admin-950 object-cover border border-admin-600"
              >
            </div>
          </el-form-item>
          <el-form-item label="联系卡片展示图" class="col-span-2">
            <div class="w-full space-y-3">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.contactCardImage" placeholder="上传后自动填入，或粘贴图片 URL" />
                <el-upload :show-file-list="false" accept="image/*" :before-upload="uploadContactCardImage">
                  <el-button :loading="uploadingContactCardImage">
                    <el-icon class="mr-1"><Upload /></el-icon>
                    上传
                  </el-button>
                </el-upload>
              </div>
              <img
                v-if="socialLinks.contactCardImage"
                :src="socialLinks.contactCardImage"
                alt="Contact card preview"
                class="w-44 h-32 rounded-lg bg-admin-950 object-contain border border-admin-600"
              >
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="bg-admin-900 border border-admin-600/40 rounded-xl p-5 space-y-4">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">社交链接</h3>
          <p class="text-admin-500 text-xs mt-1">留空的链接不会保存到接口响应中</p>
        </div>
        <el-form label-position="top">
          <el-form-item label="LinkedIn">
            <el-input v-model="socialLinks.linkedin" />
          </el-form-item>
          <el-form-item label="WeChat">
            <el-input v-model="socialLinks.wechat" placeholder="WeChat ID or QR content" />
          </el-form-item>
          <el-form-item label="WeChat 二维码图片">
            <div class="w-full space-y-2">
              <div class="flex gap-2">
                <el-input v-model="socialLinks.wechatQrImage" placeholder="上传后自动填入，或粘贴图片 URL" />
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
                <el-input v-model="socialLinks.whatsappQrImage" placeholder="上传后自动填入，或粘贴图片 URL" />
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
      <div class="flex items-center justify-between gap-3">
        <div>
          <h3 class="text-admin-100 text-sm font-semibold">官网 About 页面</h3>
          <p class="text-admin-500 text-xs mt-1">保存后同步到前台 /about 页面</p>
        </div>
        <el-button size="small" @click="resetAboutPage">
          恢复默认
        </el-button>
      </div>

      <el-form label-position="top" class="space-y-5">
        <div class="grid grid-cols-1 lg:grid-cols-2 gap-x-4">
          <el-form-item label="Hero 标签">
            <el-input v-model="aboutPage.heroLabel" />
          </el-form-item>
          <el-form-item label="Hero 标题">
            <el-input v-model="aboutPage.heroTitle" />
          </el-form-item>
          <el-form-item label="Hero 第一段" class="lg:col-span-2">
            <el-input v-model="aboutPage.heroIntro" type="textarea" :rows="3" resize="none" />
          </el-form-item>
          <el-form-item label="Hero 第二段" class="lg:col-span-2">
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
          <div class="text-admin-500 text-xs">地址</div>
          <div class="text-admin-100 text-sm font-medium mt-1">{{ locationSummary }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadRawFile } from 'element-plus'
import { defaultAboutPageConfig, useMockStore } from '@/stores/mock'
import type { AboutFeature, AboutPageConfig, AboutStat, AboutTimelineEvent, SiteConfig } from '@cnbjti/types'

const mock = useMockStore()
const saving = ref(false)
const uploadingHomeHeroVideo = ref(false)
const uploadingHomeHeroPoster = ref(false)
const uploadingContactCardImage = ref(false)
const uploadingWechatQr = ref(false)
const uploadingWhatsappQr = ref(false)
const form = reactive<SiteConfig>(emptyConfig())
const aboutPage = reactive<AboutPageConfig>(defaultAboutPageConfig())
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
    ElMessage.warning('请填写站点名称和联系邮箱')
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
    })
    fillForm(updated)
    ElMessage.success('站点设置已保存')
  } finally {
    saving.value = false
  }
}

function fillForm(data: SiteConfig) {
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
    aboutPage: data.aboutPage ? cleanAboutPage(data.aboutPage) : defaultAboutPageConfig(),
  })
  Object.assign(aboutPage, data.aboutPage ? cleanAboutPage(data.aboutPage) : defaultAboutPageConfig())
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

function text(value: string | undefined, fallback: string) {
  return value === undefined || value === null ? fallback : value.trim()
}

function cleanAboutStats(values: AboutStat[]) {
  return values
    .map((item) => ({ value: item.value.trim(), label: item.label.trim() }))
    .filter((item) => item.value && item.label)
}

function cleanAboutFeatures(values: AboutFeature[]) {
  return values
    .map((item) => ({ icon: item.icon?.trim() || '', title: item.title.trim(), desc: item.desc.trim() }))
    .filter((item) => item.title && item.desc)
}

function cleanAboutTimeline(values: AboutTimelineEvent[]) {
  return values
    .map((item) => ({ year: item.year.trim(), title: item.title.trim(), desc: item.desc.trim() }))
    .filter((item) => item.year && item.title && item.desc)
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
    const message = key === 'homeHeroVideo'
      ? '首页视频已上传'
      : key === 'homeHeroPosterImage'
        ? '首页视频封面已上传'
        : key === 'contactCardImage'
          ? '联系卡片展示图已上传'
          : '二维码图片已上传'
    ElMessage.success(message)
  } finally {
    loading.value = false
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
  }
}
</script>
