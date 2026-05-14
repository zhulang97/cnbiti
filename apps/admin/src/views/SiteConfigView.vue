<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-admin-100 font-semibold text-base">站点设置</h2>
        <p class="text-admin-500 text-xs mt-0.5">维护前台展示的品牌、联系方式和社交链接</p>
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
import { useMockStore } from '@/stores/mock'
import type { SiteConfig } from '@cnbjti/types'

const mock = useMockStore()
const saving = ref(false)
const uploadingHomeHeroVideo = ref(false)
const uploadingHomeHeroPoster = ref(false)
const uploadingContactCardImage = ref(false)
const uploadingWechatQr = ref(false)
const uploadingWhatsappQr = ref(false)
const form = reactive<SiteConfig>(emptyConfig())
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
  })
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
  }
}
</script>
