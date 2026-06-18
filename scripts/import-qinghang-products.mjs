const API_BASE = process.env.CNBJTI_API_BASE || 'http://127.0.0.1:8080/api'
const USERNAME = process.env.CNBJTI_ADMIN_USER || 'admin'
const PASSWORD = process.env.CNBJTI_ADMIN_PASSWORD || 'cnbjti2026'
const SOURCE_URL = 'https://www.qinghangmetal.com/full-list-of-titanium-manufacturers-in-usa/'

const products = [
  {
    name: 'Titanium Elbows',
    slug: 'titanium-elbows',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Size range', 'DN15 - DN300'),
      spec('Angles', '45, 90, 180 degree'),
      spec('Connection', 'Butt weld, threaded, custom'),
    ],
    shortDescription: 'Corrosion-resistant titanium elbows for chemical, marine and heat-exchanger piping.',
    description: 'CNBJTI supplies titanium elbows for corrosive process lines, seawater systems and heat-exchanger piping. Equal-wall and custom elbows can be supplied with MTR, dimensional inspection and packaging for export orders.',
  },
  {
    name: 'Titanium Bolts and Nuts',
    slug: 'titanium-bolts-and-nuts',
    categorySlug: 'titanium-fasteners',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s1', 's5'],
    specs: [
      spec('Thread range', 'M3 - M64'),
      spec('Types', 'Hex bolt, nut, stud, socket screw'),
      spec('Standard', 'DIN, ISO, ANSI, custom drawing'),
    ],
    shortDescription: 'Titanium fastener sets for lightweight, non-magnetic and corrosion-resistant assemblies.',
    description: 'Titanium bolts, nuts, washers and studs are available in commercially pure titanium and Ti-6Al-4V. CNBJTI supports standard fasteners and drawing-based machining for aerospace, marine and chemical equipment projects.',
  },
  {
    name: 'Titanium Bars',
    slug: 'titanium-bars',
    categorySlug: 'titanium-bar',
    gradeIds: ['gr2', 'gr5', 'gr23'],
    standardIds: ['s1', 's5'],
    specs: [
      spec('Diameter', '3 - 300', 'mm'),
      spec('Forms', 'Round, square, hex, flat'),
      spec('Condition', 'Annealed, polished, centerless ground'),
    ],
    shortDescription: 'Round, square, hex and flat titanium bars cut to project dimensions.',
    description: 'Titanium bars from CNBJTI cover commercially pure and alloy grades for machining, medical, aerospace and industrial components. Bars can be supplied cut-to-length with ultrasonic testing, MTR and export packaging.',
  },
  {
    name: 'Titanium Flanges',
    slug: 'titanium-flanges',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr5', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Size range', 'DN15 - DN600'),
      spec('Types', 'Weld neck, slip-on, blind, lap joint'),
      spec('Face', 'RF, FF, custom'),
    ],
    shortDescription: 'Titanium flanges for seawater, chlorides and chemical process piping.',
    description: 'CNBJTI manufactures titanium flanges for piping systems that need excellent chloride and acid corrosion resistance. Weld neck, slip-on, blind and custom flange designs can be matched to project drawings.',
  },
  {
    name: 'Titanium Wire',
    slug: 'titanium-wire',
    categorySlug: 'titanium-wire',
    gradeIds: ['gr2', 'gr5', 'gr23'],
    standardIds: ['s1'],
    specs: [
      spec('Diameter', '0.5 - 6', 'mm'),
      spec('Supply form', 'Coil, spool, straight length'),
      spec('Surface', 'Pickled, polished, bright'),
    ],
    shortDescription: 'Titanium wire for welding, medical devices, springs and precision components.',
    description: 'CNBJTI supplies titanium wire in spool, coil and straight length forms. Common use cases include welding filler, medical components, springs, aerospace parts and precision industrial assemblies.',
  },
  {
    name: 'Titanium Sheets',
    slug: 'titanium-sheets',
    categorySlug: 'titanium-sheet',
    gradeIds: ['gr1', 'gr2', 'gr5'],
    standardIds: ['s2'],
    specs: [
      spec('Thickness', '0.3 - 20', 'mm'),
      spec('Width', 'Up to 1500', 'mm'),
      spec('Surface', 'Mill finish, pickled, polished'),
    ],
    shortDescription: 'Titanium sheets for fabrication, lining, medical and industrial corrosion service.',
    description: 'Titanium sheets are supplied in commercial pure and alloy grades with stable flatness and clean surfaces. CNBJTI can provide shearing, cutting, polishing and certificate packages for production batches.',
  },
  {
    name: 'Titanium Forgings',
    slug: 'titanium-forgings',
    categorySlug: 'titanium-forgings',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s4', 's5'],
    specs: [
      spec('Forms', 'Disc, ring, block, shaft, custom blank'),
      spec('Diameter', '50 - 600', 'mm'),
      spec('Testing', 'UT, mechanical test, MTR'),
    ],
    shortDescription: 'Custom titanium forgings for high-strength blanks, rings, discs and blocks.',
    description: 'CNBJTI supports titanium forging projects from billet selection through heat treatment and machining allowance control. Forged discs, rings, shafts and custom blanks are available for demanding mechanical applications.',
  },
  {
    name: 'Titanium Welded Tubes',
    slug: 'titanium-welded-tubes',
    categorySlug: 'titanium-tube',
    gradeIds: ['gr1', 'gr2', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('OD', '6 - 219', 'mm'),
      spec('Wall thickness', '0.5 - 8', 'mm'),
      spec('Length', 'Up to 12000', 'mm'),
    ],
    shortDescription: 'Welded titanium tubes for condensers, exchangers and process equipment.',
    description: 'Titanium welded tubes are suitable for heat exchangers, condensers and chemical process equipment. CNBJTI can supply straight lengths with eddy-current testing, hydro testing and end protection.',
  },
  {
    name: 'Titanium Standard Parts',
    slug: 'titanium-standard-parts',
    categorySlug: 'titanium-fasteners',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s1'],
    specs: [
      spec('Part families', 'Fasteners, spacers, pins, sleeves'),
      spec('Standards', 'DIN, ISO, ANSI, JIS'),
      spec('Production', 'Small batch and repeat orders'),
    ],
    shortDescription: 'Standard titanium parts and repeatable small components for industrial assemblies.',
    description: 'CNBJTI provides titanium standard parts for customers that need stable dimensions, repeat purchasing and corrosion-resistant hardware. Drawing-based deviations and private-label packaging are available.',
  },
  {
    name: 'Titanium Pipe Cross',
    slug: 'titanium-pipe-cross',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Size range', 'DN15 - DN300'),
      spec('Type', 'Equal cross, reducing cross'),
      spec('Connection', 'Butt weld, threaded, custom'),
    ],
    shortDescription: 'Titanium pipe crosses for multi-branch corrosion-resistant piping systems.',
    description: 'Titanium pipe crosses are used where multiple process lines meet in aggressive media. CNBJTI can supply equal and reducing cross designs for chemical, desalination and offshore service.',
  },
  {
    name: 'Titanium Plates',
    slug: 'titanium-plates',
    categorySlug: 'titanium-sheet',
    gradeIds: ['gr2', 'gr5', 'gr7'],
    standardIds: ['s2'],
    specs: [
      spec('Thickness', '2 - 100', 'mm'),
      spec('Cutting', 'Saw cut, waterjet, plasma, custom'),
      spec('Certificate', 'MTR and heat number traceability'),
    ],
    shortDescription: 'Titanium plates for pressure vessels, machining blanks and corrosion-resistant structures.',
    description: 'CNBJTI supplies titanium plates in commercially pure and alloy grades. Plates can be cut to size and packaged with traceability documents for fabrication, machining and pressure equipment projects.',
  },
  {
    name: 'Titanium Tee',
    slug: 'titanium-tee',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Size range', 'DN15 - DN300'),
      spec('Type', 'Equal tee, reducing tee'),
      spec('Surface', 'Pickled, blasted, polished'),
    ],
    shortDescription: 'Titanium tees for branch connections in chemical and marine pipelines.',
    description: 'Titanium tees from CNBJTI are supplied for process branches, heat-exchanger circuits and seawater piping. Equal and reducing configurations can be produced to project drawings and inspected before shipment.',
  },
  {
    name: 'Titanium Tubes',
    slug: 'titanium-tubes',
    categorySlug: 'titanium-tube',
    gradeIds: ['gr1', 'gr2', 'gr9'],
    standardIds: ['s3'],
    specs: [
      spec('OD', '6 - 219', 'mm'),
      spec('Type', 'Seamless, welded'),
      spec('Length', 'Straight length or custom cut'),
    ],
    shortDescription: 'Titanium tubes for heat exchangers, condensers, hydraulics and precision assemblies.',
    description: 'CNBJTI supplies seamless and welded titanium tubes with stable wall thickness, clean inner surface and traceable raw material. Tube orders can include testing, marking and export-ready packaging.',
  },
  {
    name: 'Titanium Foil',
    slug: 'titanium-foil',
    categorySlug: 'titanium-sheet',
    gradeIds: ['gr1', 'gr2'],
    standardIds: ['s2'],
    specs: [
      spec('Thickness', '0.01 - 0.3', 'mm'),
      spec('Width', 'Custom slit width'),
      spec('Supply form', 'Roll, sheet, cut piece'),
    ],
    shortDescription: 'Thin titanium foil for electronics, chemical, laboratory and precision fabrication use.',
    description: 'Titanium foil is supplied in thin gauges for applications that need light weight, corrosion resistance and controlled thickness. CNBJTI can provide slit rolls, flat pieces and small-batch trial quantities.',
  },
  {
    name: 'Titanium Coil',
    slug: 'titanium-coil',
    categorySlug: 'titanium-sheet',
    gradeIds: ['gr1', 'gr2', 'gr5'],
    standardIds: ['s2'],
    specs: [
      spec('Thickness', '0.1 - 3', 'mm'),
      spec('Width', 'Custom slit width'),
      spec('Surface', 'Pickled, bright, polished'),
    ],
    shortDescription: 'Titanium coils and strip for stamping, forming and continuous fabrication.',
    description: 'CNBJTI supplies titanium coil and strip for customers that need continuous feedstock. Slitting, edge control and coil packaging can be arranged for fabrication and component production.',
  },
  {
    name: 'Titanium Valve',
    slug: 'titanium-valve',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr5', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Valve types', 'Ball, butterfly, check, custom body'),
      spec('Service', 'Chloride, seawater, acid process lines'),
      spec('Connection', 'Flanged, threaded, welded'),
    ],
    shortDescription: 'Titanium valve components and assemblies for corrosive media handling.',
    description: 'CNBJTI supplies titanium valve bodies, stems and related components for harsh process environments. Custom valve parts can be produced from drawings with machining and inspection support.',
  },
  {
    name: 'Titanium Spring',
    slug: 'titanium-spring',
    categorySlug: 'titanium-fasteners',
    gradeIds: ['gr5', 'gr23'],
    standardIds: ['s1'],
    specs: [
      spec('Wire diameter', '0.3 - 8', 'mm'),
      spec('Spring type', 'Compression, extension, torsion'),
      spec('Production', 'Prototype and batch orders'),
    ],
    shortDescription: 'Lightweight titanium springs for corrosion-resistant and non-magnetic assemblies.',
    description: 'Titanium springs provide low weight and excellent corrosion behavior in demanding assemblies. CNBJTI can support spring wire supply and custom spring production from technical drawings.',
  },
  {
    name: 'Titanium Washer',
    slug: 'titanium-washer',
    categorySlug: 'titanium-fasteners',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s1'],
    specs: [
      spec('Thread match', 'M3 - M64'),
      spec('Types', 'Flat, spring, lock, custom'),
      spec('Surface', 'Polished, pickled, blasted'),
    ],
    shortDescription: 'Titanium washers for fastener assemblies exposed to seawater and chemicals.',
    description: 'Titanium washers are available as flat, spring, lock and drawing-based components. CNBJTI supplies washers together with bolts and nuts for matched titanium fastener kits.',
  },
  {
    name: 'Titanium Channel',
    slug: 'titanium-channel',
    categorySlug: 'titanium-bar',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s1'],
    specs: [
      spec('Profile', 'U channel, C channel, custom section'),
      spec('Height', '20 - 200', 'mm'),
      spec('Length', 'Cut to order'),
    ],
    shortDescription: 'Titanium channel profiles for frames, supports and corrosion-resistant structures.',
    description: 'CNBJTI supplies titanium channel profiles and custom sections for lightweight frames, equipment supports and chemical-service structures. Cutting and hole machining can be arranged.',
  },
  {
    name: 'Titanium Card',
    slug: 'titanium-card',
    categorySlug: 'titanium-sheet',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s2'],
    specs: [
      spec('Thickness', '0.5 - 2', 'mm'),
      spec('Shape', 'Card blank, plate blank, custom laser cut'),
      spec('Surface', 'Brushed, polished, etched'),
    ],
    shortDescription: 'Titanium card blanks and thin plate pieces for branded, industrial and precision uses.',
    description: 'Titanium card blanks are made from sheet or plate with controlled dimensions and surface finish. CNBJTI supports brushed, polished, etched and custom-cut titanium card projects.',
  },
  {
    name: 'Titanium Rack',
    slug: 'titanium-rack',
    categorySlug: 'cnc-parts',
    gradeIds: ['gr1', 'gr2'],
    standardIds: ['s1'],
    specs: [
      spec('Use', 'Electroplating, anodizing, chemical handling'),
      spec('Build', 'Welded frame, hook, clamp, custom fixture'),
      spec('Input', 'Sample, sketch or drawing'),
    ],
    shortDescription: 'Custom titanium racks and fixtures for electrochemical and surface-treatment lines.',
    description: 'Titanium racks are used for anodizing, plating and chemical processing where corrosion resistance and conductivity matter. CNBJTI can fabricate racks, hooks and fixtures from drawings or samples.',
  },
  {
    name: 'Titanium Nipple',
    slug: 'titanium-nipple',
    categorySlug: 'titanium-fittings',
    gradeIds: ['gr2', 'gr7'],
    standardIds: ['s3'],
    specs: [
      spec('Thread', 'NPT, BSP, metric, custom'),
      spec('Size range', 'DN6 - DN100'),
      spec('Type', 'Close nipple, hex nipple, reducing nipple'),
    ],
    shortDescription: 'Titanium nipples and threaded connectors for compact corrosion-resistant piping.',
    description: 'CNBJTI supplies titanium nipples and threaded pipe connectors for compact piping assemblies. NPT, BSP and metric threads can be machined to customer drawings and inspected before shipment.',
  },
  {
    name: 'Titanium Disc',
    slug: 'titanium-disc',
    categorySlug: 'titanium-forgings',
    gradeIds: ['gr2', 'gr5'],
    standardIds: ['s2', 's4'],
    specs: [
      spec('Diameter', '20 - 600', 'mm'),
      spec('Thickness', '2 - 120', 'mm'),
      spec('Process', 'Plate cut, forged, machined'),
    ],
    shortDescription: 'Titanium discs for targets, blanks, flanges, medical and industrial machining.',
    description: 'Titanium discs are available as plate-cut or forged blanks depending on size and mechanical requirements. CNBJTI can supply rough blanks or machined discs with traceability documents.',
  },
  {
    name: 'Titanium Anode',
    slug: 'titanium-anode',
    categorySlug: 'cnc-parts',
    gradeIds: ['gr1', 'gr2'],
    standardIds: ['s2'],
    specs: [
      spec('Forms', 'Mesh, plate, rod, custom anode'),
      spec('Base material', 'Commercially pure titanium'),
      spec('Use', 'Electrolysis, water treatment, cathodic protection'),
    ],
    shortDescription: 'Titanium anode bases and custom electrochemical components for industrial systems.',
    description: 'CNBJTI supplies titanium anode bases, plates, mesh and custom fabricated components for electrochemical service. Coating requirements can be discussed based on current density and electrolyte conditions.',
  },
  {
    name: 'Titanium Powder',
    slug: 'titanium-powder',
    categorySlug: 'cnc-parts',
    gradeIds: ['gr2', 'gr5', 'gr23'],
    standardIds: ['s1'],
    specs: [
      spec('Particle size', 'Custom mesh or micron range'),
      spec('Grades', 'CP titanium, Ti-6Al-4V, ELI options'),
      spec('Use', 'Additive manufacturing, metallurgy, research'),
    ],
    shortDescription: 'Titanium powder options for additive manufacturing, metallurgy and research use.',
    description: 'CNBJTI can source titanium powder for additive manufacturing, powder metallurgy and laboratory projects. Particle size, chemistry and packaging can be matched to customer technical requirements.',
  },
]

function spec(label, value, unit = null) {
  return { label, value, unit }
}

async function api(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...(options.headers || {}),
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    },
  })
  const payload = await response.json()
  if (!response.ok || payload.code !== 'OK') {
    throw new Error(`${path}: ${payload.message || response.statusText}`)
  }
  return payload.data
}

function bySlug(items) {
  return new Map(items.map((item) => [item.slug, item]))
}

function mediaFromCategory(product, category) {
  const image = category.image
  if (!image?.url) return []
  return [{
    id: `import-${product.slug}`,
    url: image.url,
    thumbnailUrl: image.thumbnailUrl || null,
    alt: product.name,
    width: image.width || null,
    height: image.height || null,
    mimeType: image.mimeType || 'image/jpeg',
    size: image.size || 0,
    filename: `${product.slug}.jpg`,
  }]
}

function payloadFor(product, category) {
  const categoryPath = `/products/${category.slug}/${product.slug}`
  return {
    name: product.name,
    slug: product.slug,
    categoryId: category.id,
    gradeIds: product.gradeIds,
    standardIds: product.standardIds,
    status: 'published',
    shortDescription: product.shortDescription,
    description: `${product.description}\n\nReference product taxonomy: ${SOURCE_URL}\nContent rewritten for CNBJTI product presentation.`,
    imageUrl: category.image?.url || '',
    images: mediaFromCategory(product, category),
    specs: [
      ...product.specs,
      spec('Documents', 'MTR, certificate, inspection report'),
      spec('Source reference', 'Qinghang product category, rewritten'),
    ],
    seo: {
      title: `${product.name} Supplier | CNBJTI Baoji Titanium`,
      description: product.shortDescription,
      canonical: categoryPath,
      ogTitle: `${product.name} - CNBJTI`,
      ogDescription: product.shortDescription,
      ogImage: category.image?.url || null,
      noIndex: false,
    },
    featured: false,
    inStock: true,
  }
}

async function main() {
  const login = await api('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username: USERNAME, password: PASSWORD }),
  })
  const token = login.token
  const authHeaders = { Authorization: `Bearer ${token}` }

  const options = await api('/admin/content-options', { headers: authHeaders })
  const categories = bySlug(options.categories)

  const adminProducts = await api('/admin/products', { headers: authHeaders })
  const existingBySlug = new Map()
  for (const item of adminProducts) {
    const detail = await api(`/admin/products/${encodeURIComponent(item.id)}`, { headers: authHeaders })
    existingBySlug.set(detail.slug, detail)
  }

  const result = []
  for (const product of products) {
    const category = categories.get(product.categorySlug)
    if (!category) {
      throw new Error(`Missing category slug: ${product.categorySlug}`)
    }

    const body = JSON.stringify(payloadFor(product, category))
    const existing = existingBySlug.get(product.slug)
    if (existing) {
      await api(`/admin/products/${encodeURIComponent(existing.id)}`, {
        method: 'PUT',
        headers: authHeaders,
        body,
      })
      result.push({ action: 'updated', slug: product.slug })
    } else {
      await api('/admin/products', {
        method: 'POST',
        headers: authHeaders,
        body,
      })
      result.push({ action: 'created', slug: product.slug })
    }
  }

  console.log(JSON.stringify({
    source: SOURCE_URL,
    created: result.filter((item) => item.action === 'created').length,
    updated: result.filter((item) => item.action === 'updated').length,
    items: result,
  }, null, 2))
}

main().catch((error) => {
  console.error(error)
  process.exit(1)
})
