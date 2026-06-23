import { acceptHMRUpdate, defineStore } from 'pinia'
import { ref } from 'vue'
import { adminHeaders, apiRequest, apiUrl } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { prepareUploadFile } from '@/utils/imageUpload'
import type { AboutPageConfig, GalleryPageConfig, HomePageConfig, MediaAsset, NavigationItem, ProductCategory, ProductSpec, SeoMeta, SiteConfig, Standard, TitaniumGrade } from '@cnbjti/types'

export type RfqStatus = 'new' | 'in_progress' | 'quoted' | 'won' | 'lost'
export type ContentStatus = 'published' | 'draft'
export type ContactMessageStatus = 'new' | 'in_progress' | 'replied' | 'archived'

export interface Rfq {
  id: string
  company: string
  country: string
  countryCode: string
  email: string
  phone?: string
  product: string
  grade: string
  qty: string
  message: string
  status: RfqStatus
  createdAt: string
  notes: string
  statusUpdatedAt?: string
  notesUpdatedAt?: string
  attachments?: MediaAsset[]
}

export interface AdminProduct {
  id: string
  name: string
  category: string
  grade: string
  status: ContentStatus
  updatedAt: string
}

export interface AdminProductDetail {
  id: string
  name: string
  slug: string
  categoryId: string
  gradeIds: string[]
  standardIds: string[]
  status: ContentStatus
  shortDescription: string
  description: string
  imageUrl: string
  images: MediaAsset[]
  specs: ProductSpec[]
  seo: SeoMeta
  featured: boolean
  inStock: boolean
}

export type ProductSavePayload = Omit<AdminProductDetail, 'id'>

export interface AdminArticle {
  id: string
  title: string
  category: string
  status: ContentStatus
  publishedAt: string
}

export interface AdminStoredFile {
  id: string
  filename: string
  objectName: string
  contentType: string
  size: number
  url: string
  createdAt: string
}

export interface AdminAuditLog {
  id: string
  actorId?: string
  actorName: string
  action: string
  targetType: string
  targetId?: string
  method: string
  path: string
  statusCode: number
  ipAddress?: string
  userAgent?: string
  createdAt: string
}

export interface AdminAuditLogSummary {
  total: number
  success: number
  failed: number
  fileActions: number
}

export interface AdminAuditLogPage {
  items: AdminAuditLog[]
  total: number
  page: number
  pageSize: number
  totalPages: number
  summary: AdminAuditLogSummary
}

export interface AdminAuditLogQuery {
  page?: number
  pageSize?: number
  keyword?: string
  action?: string
  targetType?: string
  status?: string
}

export interface AdminManagedUser {
  id: string
  username: string
  displayName: string
  email: string
  role: 'ADMIN' | 'EDITOR' | 'SALES'
  status: 'active' | 'disabled'
  avatar?: string
  createdAt?: string
  updatedAt?: string
  lastLoginAt?: string
}

export interface AdminUserSavePayload {
  username: string
  displayName: string
  email: string
  role: 'ADMIN' | 'EDITOR' | 'SALES'
  status: 'active' | 'disabled'
  avatar?: string
  password?: string
}

export interface AdminArticleDetail {
  id: string
  title: string
  slug: string
  category: string
  status: ContentStatus
  excerpt: string
  content: string
  coverImageUrl: string
  tags: string[]
  publishedAt: string
  readingTime: number
}

export type ArticleSavePayload = Omit<AdminArticleDetail, 'id'>

export interface AdminContentOptions {
  categories: ProductCategory[]
  grades: TitaniumGrade[]
  standards: Standard[]
}

export interface AdminCategoryDetail {
  id: string
  slug: string
  name: string
  description: string
  imageUrl: string
  icon: string
  productCount: number
  seo: SeoMeta | null
  showOnHome: boolean
  homeSort: number
  status: ContentStatus
  updatedAt: string
}

export interface CategorySavePayload {
  slug: string
  name: string
  description: string
  imageUrl: string
  icon: string
  productCount: number
  seo: SeoMeta
  showOnHome: boolean
  homeSort: number
  status: ContentStatus
}

export interface AdminGradeDetail {
  id: string
  slug: string
  name: string
  shortName: string
  composition: string
  description: string
  tensileStrength: string
  yieldStrength: string
  elongation: string
  density: number | null
  applications: string[]
  status: ContentStatus
  updatedAt: string
}

export type GradeSavePayload = Omit<AdminGradeDetail, 'id' | 'updatedAt'>

export interface AdminStandardDetail {
  id: string
  slug: string
  code: string
  name: string
  description: string
  productTypes: string[]
  status: ContentStatus
  updatedAt: string
}

export type StandardSavePayload = Omit<AdminStandardDetail, 'id' | 'updatedAt'>

export interface Customer {
  id: string
  company: string
  country: string
  countryCode: string
  email: string
  rfqCount: number
  lastContact: string
}

export interface CustomerDetail extends Customer {
  notes: string
  notesUpdatedAt?: string
  rfqs: Rfq[]
}

export interface CustomerUpdatePayload {
  notes: string
  lastContact: string
}

export interface AdminContactMessage {
  id: string
  name: string
  company: string
  email: string
  phone: string
  subject: string
  message: string
  source: string
  status: ContactMessageStatus
  createdAt: string
  notes: string
  notesUpdatedAt?: string
}

export interface AdminDashboard {
  todayRfqs: number
  newRfqs: number
  needFollowUp: number
  pendingTranslation: number
  publishedProducts: number
  rfqTrend: number[]
  rfqByCountry: { country: string; count: number }[]
  rfqByProduct: { product: string; count: number }[]
}

export function defaultAboutPageConfig(): AboutPageConfig {
  return {
    heroLabel: 'Our Story',
    heroTitle: "Baoji's Titanium Specialists Since 2008",
    heroIntro: "CNBJTI was founded in Baoji - China's titanium capital - with a single mission: make certified titanium materials accessible to global buyers with the documentation and service they need.",
    heroBody: 'Today we supply titanium bar, sheet, tube, wire, forgings and custom CNC parts to customers in over 50 countries. Every order ships with a full EN 10204 3.1 MTR and is backed by our technical team.',
    stats: [
      { value: '2008', label: 'Founded in Baoji' },
      { value: '50+', label: 'Countries Served' },
      { value: '100+', label: 'Titanium Specifications' },
      { value: '15+', label: 'Years Export Experience' },
    ],
    locationLabel: 'Location Advantage',
    locationTitle: 'Why Baoji?',
    locationDescription: "Baoji, Shaanxi produces over 60% of China's titanium output. Being here means direct access to the best mills, fastest lead times and most competitive pricing.",
    advantages: [
      { icon: 'Ti', title: 'Direct Mill Access', desc: "Located minutes from Baoji's major titanium mills. We source directly, cutting out middlemen and reducing lead times." },
      { icon: 'QA', title: 'Certified Supply Chain', desc: 'All mills are ISO 9001 certified. We audit suppliers regularly and maintain approved vendor lists for each product type.' },
      { icon: 'EX', title: 'Export Expertise', desc: 'Over 15 years exporting to North America, Europe, Southeast Asia and the Middle East. We handle all customs documentation.' },
    ],
    timelineLabel: 'Milestones',
    timelineTitle: 'Our Journey',
    timeline: [
      { year: '2008', title: 'Founded in Baoji', desc: 'CNBJTI established as a titanium materials trading company, focusing on export to Southeast Asia.' },
      { year: '2012', title: 'ISO 9001 Certification', desc: 'Achieved ISO 9001:2008 quality management certification. Expanded product range to include forgings and CNC parts.' },
      { year: '2015', title: 'European Market Entry', desc: 'First major contracts with German and Dutch chemical processing companies. Began supplying EN 10204 3.1 MTRs as standard.' },
      { year: '2018', title: 'CNC Machining Division', desc: 'Launched in-house CNC machining capability for custom titanium components. Added aerospace-grade Ti-6Al-4V to stock.' },
      { year: '2021', title: 'North America Expansion', desc: 'Established relationships with US aerospace and medical device manufacturers. AMS 4928 supply capability added.' },
      { year: '2024', title: '50+ Countries Milestone', desc: 'Reached the milestone of supplying customers in over 50 countries. Launched multilingual website and 24-hour RFQ response commitment.' },
    ],
    valuesLabel: 'What We Stand For',
    valuesTitle: 'Our Values',
    values: [
      { icon: 'TC', title: 'Technical Accuracy', desc: 'We know titanium. Our team can advise on grade selection, processing and certification requirements.' },
      { icon: 'MTR', title: 'Documentation First', desc: 'Every order ships with complete MTR and certification. No exceptions, no shortcuts.' },
      { icon: '24h', title: 'Fast Response', desc: '24-hour quote turnaround. We respect your time and your project deadlines.' },
      { icon: 'LT', title: 'Long-term Partnership', desc: 'We build relationships, not just transactions. Many customers have been with us for over 10 years.' },
    ],
    ctaTitle: 'Ready to Work Together?',
    ctaDescription: 'Send us your requirements and our team will respond within 24 hours with a quote and technical recommendations.',
    seoTitle: 'About CNBJTI - Baoji Titanium Materials Supplier Since 2008',
    seoDescription: 'CNBJTI is a Baoji-based titanium materials supplier with 15+ years of export experience. We supply certified titanium to 50+ countries with EN 10204 3.1 MTR.',
  }
}

export function defaultHomePageConfig(): HomePageConfig {
  return {
    heroTitle: 'Baoji Titanium Materials Manufacturer & Custom Processing Supplier',
    heroIntro: 'CNBJTI is based in Baoji, China, focused on certified titanium materials and custom processing for global buyers.',
    heroBody: 'We supply titanium bar, sheet, plate, tube, wire, forgings, fittings, fasteners, anodes and CNC titanium parts with MTR, export packing and drawing-based processing support.',
    stats: [
      { value: '2008', label: 'Founded in Baoji' },
      { value: '50+', label: 'Countries Served' },
      { value: '100+', label: 'Specifications' },
      { value: '24h', label: 'RFQ Response' },
    ],
    proofPoints: [
      { code: 'MTR', title: 'MTR Available', desc: 'Heat number, chemistry and mechanical values' },
      { code: 'MOQ', title: 'Small MOQ', desc: 'Prototype, cut-to-size and export orders' },
      { code: 'OEM', title: 'Custom Parts', desc: 'Machining, forging and drawing-based supply' },
    ],
    buyerNotes: [
      { code: 'MTR', title: 'Certified Documentation', desc: 'EN 10204 3.1 MTR, heat number traceability and third-party inspection available.' },
      { code: 'CUT', title: 'Cut-to-size Supply', desc: 'Bar, plate, sheet and tube can be cut or processed before export packing.' },
      { code: 'OEM', title: 'Drawing-based Parts', desc: 'CNC machining, forging and special titanium components quoted from drawings.' },
    ],
    capabilities: [
      { title: 'Cut-to-size', desc: 'Saw, waterjet, laser' },
      { title: 'CNC Machining', desc: 'Turning, milling, drilling' },
      { title: 'Grinding & Polishing', desc: 'Ra 0.4 achievable' },
      { title: 'Welding & Fabrication', desc: 'TIG, electron beam' },
      { title: 'Surface Treatment', desc: 'Anodizing, passivation' },
      { title: 'Inspection & Testing', desc: 'UT, PMI, hardness' },
    ],
    qualityItems: [
      { code: 'MTR', title: 'Mill Test Report (MTR)', desc: 'EN 10204 3.1 certified, traceable to heat number' },
      { code: 'CHEM', title: 'Chemical Composition', desc: 'Full elemental analysis per ASTM/AMS requirements' },
      { code: 'MECH', title: 'Mechanical Properties', desc: 'Tensile, yield, elongation, hardness test results' },
      { code: 'SGS', title: 'Third-Party Inspection', desc: 'SGS, BV, TUV available on request' },
    ],
  }
}

export function defaultCertificatesPageConfig(): GalleryPageConfig {
  return {
    heroLabel: 'Certificates',
    heroTitle: 'Certificates',
    heroIntro: 'Our certificate gallery can be managed from the admin panel. Upload ISO, compliance, business license or third-party inspection documents here so buyers can review them before sending an RFQ.',
    items: [],
    seoTitle: 'Certificates - Titanium Quality Documents | CNBJTI',
    seoDescription: 'View CNBJTI certificate images and quality documents for titanium materials, export supply and custom processing.',
  }
}

export function defaultFactoryTourPageConfig(): GalleryPageConfig {
  return {
    heroLabel: 'Factory Tour',
    heroTitle: 'Factory Tour',
    heroIntro: 'A visual look at titanium processing, cutting, CNC machining, tube production, surface treatment and inspection resources used for export orders.',
    items: [
      { title: 'Titanium CNC Factory', desc: 'CNC machining and drawing-based titanium parts.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_535475e4c15a489dae12d2a841b4e878.jpg', imageAlt: 'Titanium CNC factory' },
      { title: 'Titanium Sheet Factory', desc: 'Sheet, plate and foil handling for export supply.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_07a15b16c4134c07816d6b6b56d377e7.png', imageAlt: 'Titanium sheet factory' },
      { title: 'Cutting Workshop', desc: 'Cut-to-size service for bar, plate and tube orders.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_37c2349a8a274995acd1fbab1cc85bcc.webp', imageAlt: 'Titanium cutting workshop' },
      { title: 'Surface Treatment', desc: 'Grinding, polishing and surface treatment support.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_2577ed70f8764881bc9b430246fc4bd8.png', imageAlt: 'Titanium surface treatment' },
      { title: 'Tube Production', desc: 'Titanium welded tube production and inspection.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_845cb1487b3a4b57a7f269a3d82e748c.jpg', imageAlt: 'Titanium welded tube production' },
      { title: 'Pipe Equipment', desc: 'Equipment for pipe, fittings and related titanium products.', imageUrl: 'https://cnbjti.com/cnbjti-assets/2026-05-14/file_dee5b328751444b6aa1d811a5adc00fb.png', imageAlt: 'Titanium pipe equipment' },
    ],
    seoTitle: 'Factory Tour - Titanium Processing Workshop | CNBJTI',
    seoDescription: 'View CNBJTI factory tour images for titanium CNC machining, cutting, tube production, surface treatment and inspection.',
  }
}

export const useMockStore = defineStore('mock', () => {
  const loading = ref(false)
  const loaded = ref(false)
  const contentOptions = ref<AdminContentOptions>({
    categories: [],
    grades: [],
    standards: [],
  })
  const siteConfig = ref<SiteConfig>({
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
  })
  const navigationItems = ref<NavigationItem[]>([])
  const dashboard = ref<AdminDashboard>({
    todayRfqs: 0,
    newRfqs: 0,
    needFollowUp: 0,
    pendingTranslation: 0,
    publishedProducts: 0,
    rfqTrend: [],
    rfqByCountry: [],
    rfqByProduct: [],
  })

  const rfqs = ref<Rfq[]>([
    { id: 'RFQ-2026-015', company: 'Chemours Company', country: 'United States', countryCode: 'US', email: 'procurement@chemours.com', phone: '+1 302 774 1000', product: 'Titanium Bar', grade: 'Gr.2', qty: '500 kg', message: 'Need Grade 2 titanium round bar, diameter 50mm, length 1000mm, quantity 500kg. Please quote with MTR and EN 10204 3.1 certificate.', status: 'new', createdAt: '2026-05-03', notes: '' },
    { id: 'RFQ-2026-014', company: 'BASF SE', country: 'Germany', countryCode: 'DE', email: 'materials@basf.com', phone: '+49 621 60 0', product: 'Titanium Sheet', grade: 'Gr.2', qty: '200 kg', message: 'Requesting quote for Grade 2 titanium sheet, 2mm thickness, 1000x2000mm, for chemical reactor lining. ASTM B265 required.', status: 'in_progress', createdAt: '2026-05-02', notes: 'Sent initial response. Waiting for drawing.' },
    { id: 'RFQ-2026-013', company: 'Tata Chemicals', country: 'India', countryCode: 'IN', email: 'purchase@tatachemicals.com', product: 'Titanium Tube', grade: 'Gr.2', qty: '1000 m', message: 'Heat exchanger tubes, OD 25mm, wall 1.5mm, length 6000mm, Grade 2, ASTM B338. Quantity 1000 meters.', status: 'quoted', createdAt: '2026-04-30', notes: 'Quote sent on 2026-05-01. Price: $28/kg. Awaiting PO.' },
    { id: 'RFQ-2026-012', company: 'Hyundai Heavy Industries', country: 'South Korea', countryCode: 'KR', email: 'procurement@hhi.co.kr', product: 'Titanium Forgings', grade: 'Gr.5', qty: '300 kg', message: 'Ti-6Al-4V forged discs, diameter 200mm, thickness 50mm, AMS 4928. Need 3.1 MTR and UT report.', status: 'won', createdAt: '2026-04-25', notes: 'PO received. Production in progress.' },
    { id: 'RFQ-2026-011', company: 'Veolia Water Technologies', country: 'France', countryCode: 'FR', email: 'supply@veolia.com', product: 'Titanium Tube', grade: 'Gr.1', qty: '500 m', message: 'Desalination plant tubes, Grade 1, OD 19mm, wall 0.7mm, ASTM B338. Delivery to Marseille port.', status: 'in_progress', createdAt: '2026-04-22', notes: 'Technical spec review ongoing.' },
    { id: 'RFQ-2026-010', company: 'Nippon Steel', country: 'Japan', countryCode: 'JP', email: 'titanium@nipponsteel.com', product: 'Titanium Bar', grade: 'Gr.5', qty: '150 kg', message: 'Ti-6Al-4V round bar, diameter 30mm, length 500mm, AMS 4928, annealed condition.', status: 'quoted', createdAt: '2026-04-20', notes: 'Quote sent. Competitive pricing needed.' },
    { id: 'RFQ-2026-009', company: 'Sulzer Ltd', country: 'Switzerland', countryCode: 'CH', email: 'procurement@sulzer.com', product: 'CNC Titanium Parts', grade: 'Gr.2', qty: '50 pcs', message: 'Custom CNC machined pump impellers in Grade 2 titanium. Drawing attached. Tolerance ±0.05mm.', status: 'new', createdAt: '2026-05-03', notes: '' },
    { id: 'RFQ-2026-008', company: 'Saipem SpA', country: 'Italy', countryCode: 'IT', email: 'materials@saipem.com', product: 'Titanium Fittings', grade: 'Gr.2', qty: '200 pcs', message: 'Titanium pipe fittings for offshore platform: elbows 90°, tees, reducers, DN50-DN150, ASTM B363.', status: 'lost', createdAt: '2026-04-15', notes: 'Lost to competitor on price.' },
    { id: 'RFQ-2026-007', company: 'Dow Chemical', country: 'United States', countryCode: 'US', email: 'sourcing@dow.com', product: 'Titanium Sheet', grade: 'Gr.7', qty: '100 kg', message: 'Grade 7 (Ti-0.15Pd) sheet for HCl service, 3mm thickness, ASTM B265. Corrosion test report required.', status: 'won', createdAt: '2026-04-10', notes: 'PO confirmed. Shipped 2026-04-28.' },
    { id: 'RFQ-2026-006', company: 'Alfa Laval', country: 'Sweden', countryCode: 'SE', email: 'procurement@alfalaval.com', product: 'Titanium Tube', grade: 'Gr.2', qty: '2000 m', message: 'Heat exchanger tubes for marine application, Grade 2, OD 16mm, wall 1mm, ASTM B338, welded type.', status: 'in_progress', createdAt: '2026-04-08', notes: 'Large order. Negotiating lead time.' },
    { id: 'RFQ-2026-005', company: 'Stryker Corporation', country: 'United States', countryCode: 'US', email: 'supply@stryker.com', product: 'Titanium Bar', grade: 'Gr.23', qty: '80 kg', message: 'Ti-6Al-4V ELI bar, Grade 23, diameter 20mm, ASTM F136, for orthopedic implant machining.', status: 'quoted', createdAt: '2026-04-05', notes: 'Medical grade. Strict documentation required.' },
    { id: 'RFQ-2026-004', company: 'Siemens Energy', country: 'Germany', countryCode: 'DE', email: 'procurement@siemens-energy.com', product: 'Titanium Fasteners', grade: 'Gr.5', qty: '5000 pcs', message: 'Ti-6Al-4V bolts M12x50, DIN 933, for turbine assembly. Need full traceability and 3.1 MTR.', status: 'new', createdAt: '2026-05-02', notes: '' },
    { id: 'RFQ-2026-003', company: 'POSCO', country: 'South Korea', countryCode: 'KR', email: 'titanium@posco.com', product: 'Titanium Wire', grade: 'Gr.2', qty: '500 kg', message: 'Titanium welding wire, Grade 2, diameter 2.4mm, AWS A5.16 ERTi-2, spooled 5kg/spool.', status: 'won', createdAt: '2026-03-28', notes: 'Repeat customer. PO received.' },
    { id: 'RFQ-2026-002', company: 'Technip Energies', country: 'France', countryCode: 'FR', email: 'materials@technip.com', product: 'Titanium Bar', grade: 'Gr.2', qty: '800 kg', message: 'CP titanium round bar for subsea application, Grade 2, various diameters 20-100mm, ASTM B348.', status: 'in_progress', createdAt: '2026-03-20', notes: 'Awaiting final spec confirmation.' },
    { id: 'RFQ-2026-001', company: 'Air Products', country: 'United States', countryCode: 'US', email: 'procurement@airproducts.com', product: 'Titanium Sheet', grade: 'Gr.2', qty: '300 kg', message: 'Grade 2 titanium plate, 6mm thickness, 1500x3000mm, ASTM B265, for pressure vessel fabrication.', status: 'won', createdAt: '2026-03-15', notes: 'Completed. Customer satisfied.' },
  ])

  const products = ref<AdminProduct[]>([
    { id: 'p1', name: 'Grade 2 Titanium Round Bar', category: 'Titanium Bar & Rod', grade: 'Gr.2', status: 'published', updatedAt: '2026-04-20' },
    { id: 'p2', name: 'Grade 5 Ti-6Al-4V Plate', category: 'Titanium Sheet / Plate', grade: 'Gr.5', status: 'published', updatedAt: '2026-04-18' },
    { id: 'p3', name: 'Grade 2 Titanium Seamless Tube', category: 'Titanium Tube & Pipe', grade: 'Gr.2', status: 'published', updatedAt: '2026-04-15' },
    { id: 'p4', name: 'Grade 1 Titanium Sheet', category: 'Titanium Sheet / Plate', grade: 'Gr.1', status: 'published', updatedAt: '2026-04-12' },
    { id: 'p5', name: 'Grade 7 Titanium Bar', category: 'Titanium Bar & Rod', grade: 'Gr.7', status: 'published', updatedAt: '2026-04-10' },
    { id: 'p6', name: 'Grade 9 Titanium Tube', category: 'Titanium Tube & Pipe', grade: 'Gr.9', status: 'published', updatedAt: '2026-04-08' },
    { id: 'p7', name: 'Grade 23 Ti-6Al-4V ELI Bar', category: 'Titanium Bar & Rod', grade: 'Gr.23', status: 'published', updatedAt: '2026-04-05' },
    { id: 'p8', name: 'Grade 2 Titanium Welding Wire', category: 'Titanium Wire', grade: 'Gr.2', status: 'published', updatedAt: '2026-04-02' },
    { id: 'p9', name: 'Grade 5 Titanium Forged Disc', category: 'Titanium Forgings', grade: 'Gr.5', status: 'published', updatedAt: '2026-03-30' },
    { id: 'p10', name: 'Grade 2 Titanium Elbow 90°', category: 'Titanium Fittings & Flanges', grade: 'Gr.2', status: 'draft', updatedAt: '2026-03-25' },
  ])

  const articles = ref<AdminArticle[]>([
    { id: 'a1', title: 'Complete Guide to Titanium Grades: Gr.1 to Gr.23', category: 'Technical Guide', status: 'published', publishedAt: '2026-03-15' },
    { id: 'a2', title: 'ASTM B348 Titanium Bar: What Buyers Need to Know', category: 'Standards', status: 'published', publishedAt: '2026-02-28' },
    { id: 'a3', title: 'Titanium Corrosion Resistance: Why It Outperforms Stainless Steel', category: 'Technical Guide', status: 'published', publishedAt: '2026-02-10' },
    { id: 'a4', title: 'How to Read a Material Test Report (MTR)', category: 'Procurement', status: 'draft', publishedAt: '' },
    { id: 'a5', title: 'Titanium in Desalination: MSF vs MED Plant Applications', category: 'Industry', status: 'draft', publishedAt: '' },
  ])

  const customers = ref<Customer[]>([
    { id: 'c1', company: 'Chemours Company', country: 'United States', countryCode: 'US', email: 'procurement@chemours.com', rfqCount: 4, lastContact: '2026-05-03' },
    { id: 'c2', company: 'BASF SE', country: 'Germany', countryCode: 'DE', email: 'materials@basf.com', rfqCount: 3, lastContact: '2026-05-02' },
    { id: 'c3', company: 'Tata Chemicals', country: 'India', countryCode: 'IN', email: 'purchase@tatachemicals.com', rfqCount: 2, lastContact: '2026-04-30' },
    { id: 'c4', company: 'Hyundai Heavy Industries', country: 'South Korea', countryCode: 'KR', email: 'procurement@hhi.co.kr', rfqCount: 5, lastContact: '2026-04-25' },
    { id: 'c5', company: 'Veolia Water Technologies', country: 'France', countryCode: 'FR', email: 'supply@veolia.com', rfqCount: 2, lastContact: '2026-04-22' },
    { id: 'c6', company: 'Nippon Steel', country: 'Japan', countryCode: 'JP', email: 'titanium@nipponsteel.com', rfqCount: 3, lastContact: '2026-04-20' },
    { id: 'c7', company: 'Sulzer Ltd', country: 'Switzerland', countryCode: 'CH', email: 'procurement@sulzer.com', rfqCount: 1, lastContact: '2026-05-03' },
    { id: 'c8', company: 'Alfa Laval', country: 'Sweden', countryCode: 'SE', email: 'procurement@alfalaval.com', rfqCount: 4, lastContact: '2026-04-08' },
    { id: 'c9', company: 'Stryker Corporation', country: 'United States', countryCode: 'US', email: 'supply@stryker.com', rfqCount: 2, lastContact: '2026-04-05' },
    { id: 'c10', company: 'Siemens Energy', country: 'Germany', countryCode: 'DE', email: 'procurement@siemens-energy.com', rfqCount: 1, lastContact: '2026-05-02' },
  ])

  const categories = ref<AdminCategoryDetail[]>([])
  const grades = ref<AdminGradeDetail[]>([])
  const standards = ref<AdminStandardDetail[]>([])
  const files = ref<AdminStoredFile[]>([])
  const auditLogs = ref<AdminAuditLog[]>([])
  const auditLogPage = ref<AdminAuditLogPage | null>(null)
  const adminUsers = ref<AdminManagedUser[]>([])
  const contactMessages = ref<AdminContactMessage[]>([])

  async function loadAll(force = false) {
    if (loaded.value && !force) return
    loading.value = true
    try {
      const auth = useAuthStore()
      dashboard.value = await apiRequest<AdminDashboard>('/admin/dashboard')

      const canManageSales = hasAnyRole(auth.role, ['ADMIN', 'SALES'])
      const canManageContent = hasAnyRole(auth.role, ['ADMIN', 'EDITOR'])
      const tasks: Promise<unknown>[] = []
      if (canManageSales) {
        tasks.push(
          apiRequest<Rfq[]>('/admin/rfqs').then((data) => { rfqs.value = data }),
          apiRequest<Customer[]>('/admin/customers').then((data) => { customers.value = data }),
          apiRequest<AdminContactMessage[]>('/admin/contact-messages').then((data) => { contactMessages.value = data }),
        )
      }
      if (canManageContent) {
        tasks.push(
          apiRequest<AdminProduct[]>('/admin/products').then((data) => { products.value = data }),
          loadArticles(true),
        )
      }
      await Promise.all(tasks)
      loaded.value = true
    } finally {
      loading.value = false
    }
  }

  function hasAnyRole(role: string, roles: string[]) {
    return roles.includes(role)
  }

  async function loadContentOptions(force = false) {
    if (!force && contentOptions.value.categories.length && contentOptions.value.grades.length && contentOptions.value.standards.length) {
      return contentOptions.value
    }
    contentOptions.value = await apiRequest<AdminContentOptions>('/admin/content-options')
    return contentOptions.value
  }

  async function loadReferenceData(force = false) {
    if (!force && categories.value.length && grades.value.length && standards.value.length) {
      return { categories: categories.value, grades: grades.value, standards: standards.value }
    }
    const [categoryData, gradeData, standardData] = await Promise.all([
      apiRequest<AdminCategoryDetail[]>('/admin/categories'),
      apiRequest<AdminGradeDetail[]>('/admin/grades'),
      apiRequest<AdminStandardDetail[]>('/admin/standards'),
    ])
    categories.value = categoryData
    grades.value = gradeData
    standards.value = standardData
    return { categories: categories.value, grades: grades.value, standards: standards.value }
  }

  async function loadSiteConfig(force = false) {
    if (!force && siteConfig.value.siteName) {
      return siteConfig.value
    }
    siteConfig.value = await apiRequest<SiteConfig>('/admin/site-config')
    return siteConfig.value
  }

  async function saveSiteConfig(payload: SiteConfig) {
    const updated = await apiRequest<SiteConfig>('/admin/site-config', {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
    siteConfig.value = updated
    return updated
  }

  async function loadNavigation(force = false) {
    if (!force && navigationItems.value.length) {
      return navigationItems.value
    }
    navigationItems.value = await apiRequest<NavigationItem[]>('/admin/navigation')
    return navigationItems.value
  }

  async function saveNavigation(items: NavigationItem[]) {
    const updated = await apiRequest<NavigationItem[]>('/admin/navigation', {
      method: 'PUT',
      body: JSON.stringify({ items }),
    })
    navigationItems.value = updated
    return updated
  }

  async function loadFiles(force = false) {
    if (!force && files.value.length) {
      return files.value
    }
    files.value = await apiRequest<AdminStoredFile[]>('/admin/files')
    return files.value
  }

  async function deleteStoredFile(id: string) {
    await apiRequest<void>(`/admin/files/${encodeURIComponent(id)}`, { method: 'DELETE' })
    files.value = files.value.filter(file => file.id !== id)
  }

  async function loadAuditLogs(params: AdminAuditLogQuery = {}) {
    const searchParams = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && `${value}`.trim()) {
        searchParams.set(key, `${value}`.trim())
      }
    })
    const query = searchParams.toString()
    const page = await apiRequest<AdminAuditLogPage>(`/admin/audit-logs${query ? `?${query}` : ''}`)
    auditLogs.value = page.items
    auditLogPage.value = page
    return page
  }

  async function loadAdminUsers(force = false) {
    if (!force && adminUsers.value.length) {
      return adminUsers.value
    }
    adminUsers.value = await apiRequest<AdminManagedUser[]>('/admin/users')
    return adminUsers.value
  }

  async function createAdminUser(payload: AdminUserSavePayload) {
    const user = await apiRequest<AdminManagedUser>('/admin/users', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
    adminUsers.value = [user, ...adminUsers.value.filter((item) => item.id !== user.id)]
      .sort((a, b) => a.username.localeCompare(b.username))
    return user
  }

  async function updateAdminUser(id: string, payload: AdminUserSavePayload) {
    const user = await apiRequest<AdminManagedUser>(`/admin/users/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
    adminUsers.value = adminUsers.value.map((item) => item.id === id ? user : item)
      .sort((a, b) => a.username.localeCompare(b.username))
    return user
  }

  async function resetAdminUserPassword(id: string, password: string) {
    const user = await apiRequest<AdminManagedUser>(`/admin/users/${id}/password`, {
      method: 'PUT',
      body: JSON.stringify({ password }),
    })
    adminUsers.value = adminUsers.value.map((item) => item.id === id ? user : item)
    return user
  }

  async function loadContactMessages(force = false) {
    if (!force && contactMessages.value.length) {
      return contactMessages.value
    }
    contactMessages.value = await apiRequest<AdminContactMessage[]>('/admin/contact-messages')
    return contactMessages.value
  }

  async function loadArticles(force = false) {
    if (!force && articles.value.length > 5) {
      return articles.value
    }
    articles.value = await apiRequest<AdminArticle[]>('/admin/articles')
    return articles.value
  }

  async function loadProducts(force = false) {
    if (!force && products.value.length > 10) {
      return products.value
    }
    products.value = await apiRequest<AdminProduct[]>('/admin/products')
    return products.value
  }

  async function getContactMessage(id: string) {
    return apiRequest<AdminContactMessage>(`/admin/contact-messages/${encodeURIComponent(id)}`)
  }

  async function updateContactMessageStatus(id: string, status: ContactMessageStatus) {
    const updated = await apiRequest<AdminContactMessage>(`/admin/contact-messages/${encodeURIComponent(id)}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    })
    upsert(contactMessages.value, updated)
    return updated
  }

  async function updateContactMessageNotes(id: string, notes: string) {
    const updated = await apiRequest<AdminContactMessage>(`/admin/contact-messages/${encodeURIComponent(id)}/notes`, {
      method: 'PUT',
      body: JSON.stringify({ notes }),
    })
    upsert(contactMessages.value, updated)
    return updated
  }

  async function saveCategory(payload: CategorySavePayload, id?: string) {
    const updated = await apiRequest<AdminCategoryDetail>(id ? `/admin/categories/${encodeURIComponent(id)}` : '/admin/categories', {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    })
    upsert(categories.value, updated)
    contentOptions.value.categories = categories.value.map(toProductCategory)
    return updated
  }

  async function deleteCategory(id: string) {
    await apiRequest<void>(`/admin/categories/${encodeURIComponent(id)}`, { method: 'DELETE' })
    categories.value = categories.value.filter(item => item.id !== id)
    contentOptions.value.categories = contentOptions.value.categories.filter(item => item.id !== id)
  }

  async function saveGrade(payload: GradeSavePayload, id?: string) {
    const updated = await apiRequest<AdminGradeDetail>(id ? `/admin/grades/${encodeURIComponent(id)}` : '/admin/grades', {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    })
    upsert(grades.value, updated)
    contentOptions.value.grades = grades.value.map(toTitaniumGrade)
    return updated
  }

  async function deleteGrade(id: string) {
    await apiRequest<void>(`/admin/grades/${encodeURIComponent(id)}`, { method: 'DELETE' })
    grades.value = grades.value.filter(item => item.id !== id)
    contentOptions.value.grades = contentOptions.value.grades.filter(item => item.id !== id)
  }

  async function saveStandard(payload: StandardSavePayload, id?: string) {
    const updated = await apiRequest<AdminStandardDetail>(id ? `/admin/standards/${encodeURIComponent(id)}` : '/admin/standards', {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    })
    upsert(standards.value, updated)
    contentOptions.value.standards = standards.value.map(toStandard)
    return updated
  }

  async function deleteStandard(id: string) {
    await apiRequest<void>(`/admin/standards/${encodeURIComponent(id)}`, { method: 'DELETE' })
    standards.value = standards.value.filter(item => item.id !== id)
    contentOptions.value.standards = contentOptions.value.standards.filter(item => item.id !== id)
  }

  async function getProductDetail(id: string) {
    return apiRequest<AdminProductDetail>(`/admin/products/${encodeURIComponent(id)}`)
  }

  async function saveProduct(payload: ProductSavePayload, id?: string) {
    const updated = await apiRequest<AdminProduct>(id ? `/admin/products/${encodeURIComponent(id)}` : '/admin/products', {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    })
    upsert(products.value, updated)
    return updated
  }

  async function deleteProduct(id: string) {
    await apiRequest<void>(`/admin/products/${encodeURIComponent(id)}`, { method: 'DELETE' })
    products.value = products.value.filter(product => product.id !== id)
  }

  async function getArticleDetail(id: string) {
    return apiRequest<AdminArticleDetail>(`/admin/articles/${encodeURIComponent(id)}`)
  }

  async function saveArticle(payload: ArticleSavePayload, id?: string) {
    const updated = await apiRequest<AdminArticle>(id ? `/admin/articles/${encodeURIComponent(id)}` : '/admin/articles', {
      method: id ? 'PUT' : 'POST',
      body: JSON.stringify(payload),
    })
    upsert(articles.value, updated)
    return updated
  }

  async function deleteArticle(id: string) {
    await apiRequest<void>(`/admin/articles/${encodeURIComponent(id)}`, { method: 'DELETE' })
    articles.value = articles.value.filter(article => article.id !== id)
  }

  async function getCustomerDetail(id: string) {
    return apiRequest<CustomerDetail>(`/admin/customers/${encodeURIComponent(id)}`)
  }

  async function updateCustomer(id: string, payload: CustomerUpdatePayload) {
    const updated = await apiRequest<CustomerDetail>(`/admin/customers/${encodeURIComponent(id)}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    })
    const index = customers.value.findIndex(customer => customer.id === id)
    if (index >= 0) {
      customers.value[index] = {
        id: updated.id,
        company: updated.company,
        country: updated.country,
        countryCode: updated.countryCode,
        email: updated.email,
        rfqCount: updated.rfqCount,
        lastContact: updated.lastContact,
      }
    }
    return updated
  }

  async function uploadFile(file: File) {
    const uploadFile = await prepareUploadFile(file)
    const formData = new FormData()
    formData.append('file', uploadFile)
    const asset = await apiRequest<MediaAsset>('/public/uploads', {
      method: 'POST',
      body: formData,
    })
    upsertUploadedFile(asset)
    return asset
  }

  function upsertUploadedFile(asset: MediaAsset) {
    const uploaded: AdminStoredFile = {
      id: asset.id,
      filename: asset.filename,
      objectName: asset.url.split('/').slice(-2).join('/'),
      contentType: asset.mimeType,
      size: asset.size,
      url: asset.url,
      createdAt: new Date().toISOString(),
    }
    files.value = [uploaded, ...files.value.filter(file => file.id !== uploaded.id)]
  }

  async function updateRfqStatus(id: string, status: RfqStatus) {
    const updated = await apiRequest<Rfq>(`/admin/rfqs/${encodeURIComponent(id)}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status }),
    })
    const index = rfqs.value.findIndex(r => r.id === id)
    if (index >= 0) rfqs.value[index] = updated
  }

  async function updateRfqNotes(id: string, notes: string) {
    const updated = await apiRequest<Rfq>(`/admin/rfqs/${encodeURIComponent(id)}/notes`, {
      method: 'PUT',
      body: JSON.stringify({ notes }),
    })
    const index = rfqs.value.findIndex(r => r.id === id)
    if (index >= 0) rfqs.value[index] = updated
  }

  async function exportRfqCsv() {
    const response = await fetch(apiUrl('/admin/rfqs/export.csv'), {
      headers: adminHeaders(),
    })
    if (!response.ok) throw new Error('Unable to export RFQs')
    return response.blob()
  }

  async function toggleProductStatus(id: string) {
    const p = products.value.find(p => p.id === id)
    if (!p) return
    const nextStatus = p.status === 'published' ? 'draft' : 'published'
    const updated = await apiRequest<AdminProduct>(`/admin/products/${encodeURIComponent(id)}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status: nextStatus }),
    })
    const index = products.value.findIndex(product => product.id === id)
    if (index >= 0) products.value[index] = updated
  }

  async function toggleArticleStatus(id: string) {
    const article = articles.value.find(article => article.id === id)
    if (!article) return
    const nextStatus: ContentStatus = article.status === 'published' ? 'draft' : 'published'
    const updated = await apiRequest<AdminArticle>(`/admin/articles/${encodeURIComponent(id)}/status`, {
      method: 'PUT',
      body: JSON.stringify({ status: nextStatus }),
    })
    const index = articles.value.findIndex(article => article.id === id)
    if (index >= 0) articles.value[index] = updated
  }

  function upsert<T extends { id: string }>(items: T[], updated: T) {
    const index = items.findIndex(item => item.id === updated.id)
    if (index >= 0) {
      items[index] = updated
      return
    }
    items.unshift(updated)
  }

  function toProductCategory(item: AdminCategoryDetail): ProductCategory {
    return {
      id: item.id,
      slug: item.slug,
      name: item.name,
      description: item.description,
      image: item.imageUrl ? {
        id: `cm${item.id}`,
        url: item.imageUrl,
        alt: item.name,
        mimeType: 'image/jpeg',
        size: 0,
        filename: `${item.slug}.jpg`,
      } : undefined,
      icon: item.icon,
      productCount: item.productCount,
      seo: item.seo || undefined,
      showOnHome: item.showOnHome,
      homeSort: item.homeSort,
    }
  }

  function toTitaniumGrade(item: AdminGradeDetail): TitaniumGrade {
    return {
      id: item.id,
      slug: item.slug,
      name: item.name,
      shortName: item.shortName,
      composition: item.composition,
      description: item.description,
      tensileStrength: item.tensileStrength,
      yieldStrength: item.yieldStrength,
      elongation: item.elongation,
      density: item.density || undefined,
      applications: item.applications,
    }
  }

  function toStandard(item: AdminStandardDetail): Standard {
    return {
      id: item.id,
      slug: item.slug,
      code: item.code,
      name: item.name,
      description: item.description,
      productTypes: item.productTypes,
    }
  }

  return {
    loading,
    loaded,
    dashboard,
    rfqs,
    products,
    articles,
    customers,
    categories,
    grades,
    standards,
    files,
    auditLogs,
    auditLogPage,
    adminUsers,
    contactMessages,
    siteConfig,
    navigationItems,
    contentOptions,
    loadAll,
    loadContentOptions,
    loadReferenceData,
    loadSiteConfig,
    saveSiteConfig,
    loadNavigation,
    saveNavigation,
    loadFiles,
    deleteStoredFile,
    loadAuditLogs,
    loadAdminUsers,
    createAdminUser,
    updateAdminUser,
    resetAdminUserPassword,
    loadContactMessages,
    loadArticles,
    loadProducts,
    getContactMessage,
    updateContactMessageStatus,
    updateContactMessageNotes,
    saveCategory,
    deleteCategory,
    saveGrade,
    deleteGrade,
    saveStandard,
    deleteStandard,
    getProductDetail,
    saveProduct,
    deleteProduct,
    getArticleDetail,
    saveArticle,
    deleteArticle,
    getCustomerDetail,
    updateCustomer,
    uploadFile,
    updateRfqStatus,
    updateRfqNotes,
    exportRfqCsv,
    toggleProductStatus,
    toggleArticleStatus,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useMockStore, import.meta.hot))
}
