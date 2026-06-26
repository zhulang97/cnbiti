import type { Article, IndustryProductLink, IndustryProfile } from '@cnbjti/types'
import { qinghangPageAssets } from './qinghangPageAssets'

export type { IndustryProductLink, IndustryProfile }

export const defaultIndustryProfiles: IndustryProfile[] = [
  {
    slug: 'chemical-processing',
    name: 'Chemical Processing',
    kicker: 'Reactors, piping and anodes',
    summary: 'Titanium is specified for corrosive chemical service where chloride attack, oxidizing media and long maintenance intervals matter.',
    image: qinghangPageAssets.chemicalIndustry.url,
    imageAlt: qinghangPageAssets.chemicalIndustry.label,
    grades: ['Gr.1', 'Gr.2', 'Gr.7', 'Gr.12'],
    standards: ['ASTM B338', 'ASTM B265', 'ASTM B348', 'ASME SB338'],
    applications: ['Heat exchangers', 'Pressure vessels', 'Electrolyzer parts', 'Process piping'],
    requirements: ['Heat number traceability', 'PMI or third-party test options', 'Pickled or polished surfaces', 'Export crating for tubes and sheets'],
    productLinks: [
      { label: 'Titanium Tubes', href: '/products/titanium-tube' },
      { label: 'Titanium Sheets', href: '/products/titanium-sheet' },
      { label: 'Titanium Fittings', href: '/products/titanium-fittings' },
    ],
    articleKeywords: ['chemical', 'reactor', 'piping', 'anode', 'electrolyzer', 'water treatment'],
  },
  {
    slug: 'marine-desalination',
    name: 'Marine and Desalination',
    kicker: 'Seawater corrosion resistance',
    summary: 'Commercially pure titanium is used for ship systems, offshore structures and desalination equipment exposed to seawater and chlorides.',
    image: qinghangPageAssets.shipbuilding.url,
    imageAlt: qinghangPageAssets.shipbuilding.label,
    grades: ['Gr.1', 'Gr.2', 'Gr.7', 'Gr.12'],
    standards: ['ASTM B338', 'ASTM B265', 'ASTM B381'],
    applications: ['Condenser tubing', 'Seawater piping', 'Offshore fasteners', 'Plate heat exchangers'],
    requirements: ['Clean tube ends', 'UT or eddy-current testing when required', 'MTR with chemical and mechanical values', 'Moisture-proof export packing'],
    productLinks: [
      { label: 'Titanium Tubes', href: '/products/titanium-tube' },
      { label: 'Titanium Fasteners', href: '/products/titanium-fasteners' },
      { label: 'Titanium Plates', href: '/products/titanium-sheet' },
    ],
    articleKeywords: ['marine', 'ship', 'offshore', 'desalination', 'seawater', 'water treatment'],
  },
  {
    slug: 'medical',
    name: 'Medical and Dental',
    kicker: 'Biocompatible titanium supply',
    summary: 'Medical projects typically require tight chemistry control, clean surfaces and full documentation for implant, dental and instrument components.',
    image: qinghangPageAssets.medicalApplication.url,
    imageAlt: qinghangPageAssets.medicalApplication.label,
    grades: ['Gr.2', 'Gr.4', 'Gr.5', 'Gr.23'],
    standards: ['ASTM F67', 'ASTM F136', 'ASTM B348'],
    applications: ['Dental implant blanks', 'Surgical instruments', 'Orthopedic parts', 'Precision machined components'],
    requirements: ['Grade 23 ELI availability', 'Low interstitial chemistry control', 'Fine machining surface finish', 'Lot-level MTR documentation'],
    productLinks: [
      { label: 'Titanium Bars', href: '/products/titanium-bar' },
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
      { label: 'Titanium Fasteners', href: '/products/titanium-fasteners' },
    ],
    articleKeywords: ['medical', 'dental', 'implant', 'medicine', 'knee', 'biocompatible'],
  },
  {
    slug: 'aerospace',
    name: 'Aerospace and Defense',
    kicker: 'High strength with lower weight',
    summary: 'Aerospace buyers use titanium alloys for structural parts, fasteners and machined components where strength-to-weight ratio is critical.',
    image: qinghangPageAssets.forging.url,
    imageAlt: qinghangPageAssets.forging.label,
    grades: ['Gr.5', 'Gr.9', 'Gr.23'],
    standards: ['AMS 4928', 'ASTM B348', 'ASTM B381'],
    applications: ['Fasteners', 'Forged blanks', 'CNC parts', 'Hydraulic tubing'],
    requirements: ['Drawing review before quotation', 'Tight dimensional tolerance', 'Optional third-party inspection', 'Batch traceability'],
    productLinks: [
      { label: 'Titanium Bars', href: '/products/titanium-bar' },
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
      { label: 'Titanium Fasteners', href: '/products/titanium-fasteners' },
    ],
    articleKeywords: ['aerospace', 'aircraft', 'drone', 'performance', 'strength', 'fastener'],
  },
  {
    slug: 'energy',
    name: 'Energy and Hydrogen',
    kicker: 'Electrochemical and power projects',
    summary: 'Titanium is used in energy storage, hydrogen, fuel cell and electrolyzer applications that need corrosion resistance and stable conductivity support.',
    image: qinghangPageAssets.heatTreatment.url,
    imageAlt: qinghangPageAssets.heatTreatment.label,
    grades: ['Gr.1', 'Gr.2', 'Gr.5', 'Gr.7'],
    standards: ['ASTM B265', 'ASTM B348', 'ASTM B381'],
    applications: ['Electrolyzer plates', 'Titanium anodes', 'Battery pack hardware', 'Fuel cell components'],
    requirements: ['Surface treatment options', 'Flatness and thickness control', 'Custom cutting service', 'Consistent batch documentation'],
    productLinks: [
      { label: 'Titanium Sheets', href: '/products/titanium-sheet' },
      { label: 'Titanium Electrolyzer', href: '/products/cnc-parts/titanium-electrolyzer-for-water-treatment' },
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
    ],
    articleKeywords: ['energy', 'battery', 'fuel cell', 'hydrogen', 'anode', 'electrolyzer'],
  },
  {
    slug: 'industrial-machining',
    name: 'Industrial Machining',
    kicker: 'Custom parts and processed blanks',
    summary: 'For industrial buyers, CNBJTI can combine stock titanium material with cutting, CNC machining, forging and finishing support.',
    image: qinghangPageAssets.cncMachining.url,
    imageAlt: qinghangPageAssets.cncMachining.label,
    grades: ['Gr.2', 'Gr.5', 'Gr.9', 'Gr.12'],
    standards: ['ASTM B348', 'ASTM B265', 'ASTM B381'],
    applications: ['Machined housings', 'Custom washers', 'Forged blanks', 'Cut-to-size plate'],
    requirements: ['STEP, DXF, DWG or PDF drawings', 'Tolerance review', 'First article confirmation', 'Protective packing for finished surfaces'],
    productLinks: [
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
      { label: 'Titanium Bars', href: '/products/titanium-bar' },
      { label: 'Titanium Plates', href: '/products/titanium-sheet' },
    ],
    articleKeywords: ['machining', 'casting', 'manufacturing', 'custom', 'industrial', 'component'],
  },
  {
    slug: 'automotive',
    name: 'Automotive and Motorsport',
    kicker: 'Weight reduction and heat resistance',
    summary: 'Motorsport and advanced automotive projects use titanium for exhaust, fasteners and structural parts that need lower mass without weak hardware.',
    image: qinghangPageAssets.factoryCnc.url,
    imageAlt: qinghangPageAssets.factoryCnc.label,
    grades: ['Gr.5', 'Gr.9', 'Gr.2'],
    standards: ['ASTM B348', 'ASTM B338', 'ASTM B265'],
    applications: ['Exhaust tubing', 'Bolts and nuts', 'Bracket blanks', 'CNC lightweight parts'],
    requirements: ['Reliable alloy grade control', 'Thread and tolerance inspection', 'Surface finish choice', 'Small-batch manufacturing support'],
    productLinks: [
      { label: 'Titanium Fasteners', href: '/products/titanium-fasteners' },
      { label: 'Titanium Tubes', href: '/products/titanium-tube' },
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
    ],
    articleKeywords: ['automotive', 'motorsport', 'racing', 'vehicle', 'nev', 'battery'],
  },
  {
    slug: 'electronics',
    name: 'Electronics and Consumer Devices',
    kicker: 'Durable housings and fine finishes',
    summary: 'Consumer electronics projects use titanium when housings, buttons and small precision parts need strength, premium finish and corrosion stability.',
    image: qinghangPageAssets.surfaceTreatment.url,
    imageAlt: qinghangPageAssets.surfaceTreatment.label,
    grades: ['Gr.2', 'Gr.5'],
    standards: ['ASTM B348', 'ASTM B265'],
    applications: ['3C device housings', 'Precision screws', 'Machined buttons', 'Thin sheet components'],
    requirements: ['Consistent surface appearance', 'Fine machining support', 'Scratch protection packing', 'Prototype to batch supply'],
    productLinks: [
      { label: 'CNC Titanium Parts', href: '/products/cnc-parts' },
      { label: 'Titanium Fasteners', href: '/products/titanium-fasteners' },
      { label: 'Titanium Sheets', href: '/products/titanium-sheet' },
    ],
    articleKeywords: ['3c', 'electronic', 'housing', 'consumer', 'device', 'screw'],
  },
]

export const industryProfiles = defaultIndustryProfiles

export function findIndustryProfile(slug: string, profiles = defaultIndustryProfiles) {
  return profiles.find((industry) => industry.slug === slug)
}

export function matchingIndustryArticles(articles: Article[], industry: IndustryProfile, limit = 3) {
  const applicationArticles = articles.filter((article) => article.category === 'Applications')
  const scored = applicationArticles
    .map((article) => ({
      article,
      score: industry.articleKeywords.reduce((count, keyword) => {
        const haystack = `${article.title} ${article.excerpt} ${(article.tags || []).join(' ')}`.toLowerCase()
        return haystack.includes(keyword.toLowerCase()) ? count + 1 : count
      }, 0),
    }))
    .filter((entry) => entry.score > 0)
    .sort((a, b) => b.score - a.score)
    .map((entry) => entry.article)

  const fallback = applicationArticles.filter((article) => !scored.some((item) => item.slug === article.slug))
  return [...scored, ...fallback].slice(0, limit)
}
