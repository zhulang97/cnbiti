export interface SeoMeta {
  title: string
  description: string
  canonical?: string
  ogTitle?: string
  ogDescription?: string
  ogImage?: string
  noIndex?: boolean
}

export interface Language {
  code: string
  name: string
  nativeName: string
  flag: string
  isDefault: boolean
}

export interface MediaAsset {
  id: string
  url: string
  thumbnailUrl?: string
  alt?: string
  width?: number
  height?: number
  mimeType: string
  size: number
  filename: string
}

export interface ProductCategory {
  id: string
  slug: string
  name: string
  description?: string
  image?: MediaAsset
  icon?: string
  productCount: number
  seo?: SeoMeta
}

export interface TitaniumGrade {
  id: string
  slug: string
  name: string
  shortName: string
  composition?: string
  description?: string
  tensileStrength?: string
  yieldStrength?: string
  elongation?: string
  density?: number
  applications?: string[]
}

export interface Standard {
  id: string
  slug: string
  code: string
  name: string
  description?: string
  productTypes?: string[]
}

export interface ProductSpec {
  label: string
  value: string
  unit?: string
}

export interface Product {
  id: string
  slug: string
  name: string
  shortDescription: string
  description?: string
  category: ProductCategory
  grades: TitaniumGrade[]
  standards: Standard[]
  specs: ProductSpec[]
  images: MediaAsset[]
  seo?: SeoMeta
  featured?: boolean
  inStock?: boolean
}

export interface Article {
  id: string
  slug: string
  title: string
  excerpt: string
  content?: string
  coverImage?: MediaAsset
  category?: string
  tags?: string[]
  publishedAt: string
  readingTime?: number
  seo?: SeoMeta
}

export interface RfqItem {
  productType: string
  grade?: string
  standard?: string
  shape?: string
  dimensions?: string
  quantity?: number
  unit?: string
  tolerance?: string
  surfaceFinish?: string
  processingRequirements?: string
  certificateRequirements?: string[]
}

export interface RfqSubmission {
  items: RfqItem[]
  destinationCountry?: string
  incoterms?: string
  expectedDelivery?: string
  attachments?: File[]
  contactName: string
  company?: string
  email: string
  phone?: string
  message?: string
}

export interface RfqResponse {
  rfqNo: string
  message: string
  estimatedResponseTime: string
}

export interface Rfq extends RfqSubmission {
  id: string
  rfqNo: string
  status: RfqStatus
  createdAt: string
  updatedAt: string
}

export type RfqStatus =
  | 'NEW'
  | 'QUALIFIED'
  | 'NEED_TECH_REVIEW'
  | 'QUOTING'
  | 'QUOTE_SENT'
  | 'FOLLOW_UP'
  | 'WON'
  | 'LOST'
  | 'SPAM'
  | 'ARCHIVED'

export interface Customer {
  id: string
  name: string
  company?: string
  email: string
  phone?: string
  country?: string
  industry?: string
  rfqCount: number
  createdAt: string
}

export interface AdminUser {
  id: string
  name: string
  email: string
  role: string
  avatar?: string
}

export interface AboutStat {
  value: string
  label: string
}

export interface AboutFeature {
  icon?: string
  title: string
  desc: string
}

export interface AboutTimelineEvent {
  year: string
  title: string
  desc: string
}

export interface AboutPageConfig {
  heroLabel: string
  heroTitle: string
  heroIntro: string
  heroBody: string
  stats: AboutStat[]
  locationLabel: string
  locationTitle: string
  locationDescription: string
  advantages: AboutFeature[]
  timelineLabel: string
  timelineTitle: string
  timeline: AboutTimelineEvent[]
  valuesLabel: string
  valuesTitle: string
  values: AboutFeature[]
  ctaTitle: string
  ctaDescription: string
  seoTitle: string
  seoDescription: string
}

export interface SiteConfig {
  siteName: string
  tagline: string
  email: string
  phone: string
  whatsapp: string
  address: string
  city: string
  country: string
  socialLinks: {
    wechat?: string
    homeHeroVideo?: string
    homeHeroPosterImage?: string
    contactCardImage?: string
    wechatQrImage?: string
    whatsappQrImage?: string
    linkedin?: string
    twitter?: string
    youtube?: string
  }
  aboutPage?: AboutPageConfig | null
}

export interface NavigationItem {
  label: string
  href?: string
  children?: NavigationItem[]
  badge?: string
  icon?: string
}

export interface PagedResult<T> {
  items: T[]
  page: number
  pageSize: number
  total: number
}

export interface ApiResponse<T = unknown> {
  code: string
  message: string
  data: T
  requestId?: string
}
