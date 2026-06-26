package com.cnbjti.api.service;

import com.cnbjti.api.config.AppProperties.AdminBootstrapProperties;
import com.cnbjti.api.entity.AdminUserEntity;
import com.cnbjti.api.entity.CatalogContentEntity;
import com.cnbjti.api.entity.CustomerEntity;
import com.cnbjti.api.entity.RfqEntity;
import com.cnbjti.api.model.ApiModels.AboutFeature;
import com.cnbjti.api.model.ApiModels.AboutPageConfig;
import com.cnbjti.api.model.ApiModels.AboutStat;
import com.cnbjti.api.model.ApiModels.AboutTimelineEvent;
import com.cnbjti.api.model.ApiModels.Article;
import com.cnbjti.api.model.ApiModels.GalleryPageConfig;
import com.cnbjti.api.model.ApiModels.HomeCapability;
import com.cnbjti.api.model.ApiModels.HomeFeature;
import com.cnbjti.api.model.ApiModels.HomePageConfig;
import com.cnbjti.api.model.ApiModels.HomeQualityItem;
import com.cnbjti.api.model.ApiModels.HomeStat;
import com.cnbjti.api.model.ApiModels.IndustryProductLink;
import com.cnbjti.api.model.ApiModels.IndustryProfile;
import com.cnbjti.api.model.ApiModels.ManagedGalleryItem;
import com.cnbjti.api.model.ApiModels.MediaAsset;
import com.cnbjti.api.model.ApiModels.Product;
import com.cnbjti.api.model.ApiModels.ProductCategory;
import com.cnbjti.api.model.ApiModels.ProductSpec;
import com.cnbjti.api.model.ApiModels.SiteConfig;
import com.cnbjti.api.model.ApiModels.Standard;
import com.cnbjti.api.model.ApiModels.TitaniumGrade;
import com.cnbjti.api.repository.AdminUserRepository;
import com.cnbjti.api.repository.CatalogContentRepository;
import com.cnbjti.api.repository.CustomerRepository;
import com.cnbjti.api.repository.RfqRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseSeeder implements ApplicationRunner {

  private final CatalogContentRepository catalogRepository;
  private final RfqRepository rfqRepository;
  private final CustomerRepository customerRepository;
  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JsonCodec jsonCodec;
  private final AdminBootstrapProperties adminBootstrapProperties;

  public DatabaseSeeder(CatalogContentRepository catalogRepository, RfqRepository rfqRepository,
      CustomerRepository customerRepository, AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder,
      JsonCodec jsonCodec, AdminBootstrapProperties adminBootstrapProperties) {
    this.catalogRepository = catalogRepository;
    this.rfqRepository = rfqRepository;
    this.customerRepository = customerRepository;
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.jsonCodec = jsonCodec;
    this.adminBootstrapProperties = adminBootstrapProperties;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    seedAdminUser();
    if (catalogRepository.count() == 0) {
      seedCatalog();
    }
    if (rfqRepository.count() == 0) {
      seedRfqs();
    }
    if (customerRepository.count() == 0) {
      seedCustomers();
    }
  }

  private void seedAdminUser() {
    if (adminUserRepository.findByUsername("admin").isPresent()) {
      return;
    }
    adminUserRepository.save(new AdminUserEntity("u1", text(adminBootstrapProperties.username(), "admin"),
        text(adminBootstrapProperties.displayName(), "admin"),
        text(adminBootstrapProperties.email(), "admin@cnbjti.com"), "ADMIN",
        passwordEncoder.encode(text(adminBootstrapProperties.password(), "cnbjti2026")), null));
  }

  private String text(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
  }

  private void seedCatalog() {
    ProductCategory bar = category("1", "titanium-bar", "Titanium Bar & Rod",
        "Round, square, hexagonal and flat bars in all titanium grades. Cut-to-size available.", "bar", 24,
        "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&q=80");
    ProductCategory sheet = category("2", "titanium-sheet", "Titanium Sheet / Plate / Foil",
        "Titanium sheet, plate and foil in standard and custom sizes. Mill finish and polished.", "sheet", 18,
        "https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=600&q=80");
    ProductCategory tube = category("3", "titanium-tube", "Titanium Tube & Pipe",
        "Seamless and welded titanium tubes and pipes for industrial and medical applications.", "tube", 16,
        "https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=600&q=80");
    ProductCategory wire = category("4", "titanium-wire", "Titanium Wire",
        "Titanium wire for welding, medical devices, aerospace and industrial applications.", "wire", 10,
        "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&q=80");
    ProductCategory forgings = category("5", "titanium-forgings", "Titanium Forgings",
        "Custom titanium forgings including discs, rings, blocks and near-net-shape parts.", "forging", 12,
        "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600&q=80");
    ProductCategory fasteners = category("6", "titanium-fasteners", "Titanium Fasteners",
        "Titanium bolts, nuts, screws, washers and studs. Standard and custom specifications.", "fastener", 20,
        "https://images.unsplash.com/photo-1609205807107-2d5e9e5e5e5e?w=600&q=80");
    ProductCategory fittings = category("7", "titanium-fittings", "Titanium Fittings & Flanges",
        "Titanium pipe fittings, flanges, elbows, tees and reducers for corrosive environments.", "fitting", 14,
        "https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=600&q=80");
    ProductCategory cnc = category("8", "cnc-parts", "CNC Titanium Parts",
        "Custom CNC machined titanium components from drawings. Tight tolerances, fast turnaround.", "cnc", 8,
        "https://images.unsplash.com/photo-1565193566173-7a0ee3dbe261?w=600&q=80");
    List<ProductCategory> categories = List.of(bar, sheet, tube, wire, forgings, fasteners, fittings, cnc);

    TitaniumGrade gr1 = grade("gr1", "grade-1-titanium", "Grade 1 Titanium", "Gr.1",
        "Commercially Pure (CP) Ti, lowest oxygen content",
        "Highest purity, best corrosion resistance and formability. Used in chemical processing and marine.",
        "240 MPa min", "170 MPa min", "24% min", 4.51,
        List.of("Chemical processing", "Marine", "Desalination", "Medical implants"));
    TitaniumGrade gr2 = grade("gr2", "grade-2-titanium", "Grade 2 Titanium", "Gr.2",
        "Commercially Pure (CP) Ti, standard oxygen content",
        "Most widely used CP titanium. Excellent balance of strength, formability and corrosion resistance.",
        "345 MPa min", "275 MPa min", "20% min", 4.51,
        List.of("Chemical processing", "Aerospace", "Marine", "Architecture", "Medical"));
    TitaniumGrade gr5 = grade("gr5", "grade-5-titanium", "Grade 5 / Ti-6Al-4V", "Gr.5",
        "Ti-6Al-4V (6% Aluminum, 4% Vanadium)",
        "The most widely used titanium alloy. High strength-to-weight ratio. Aerospace workhorse.",
        "895 MPa min", "828 MPa min", "10% min", 4.43,
        List.of("Aerospace structures", "Jet engine components", "Medical implants", "High-performance automotive"));
    TitaniumGrade gr7 = grade("gr7", "grade-7-titanium", "Grade 7 Titanium", "Gr.7",
        "CP Ti + 0.12-0.25% Palladium",
        "Best corrosion resistance of all titanium grades. Ideal for highly corrosive environments.",
        "345 MPa min", "275 MPa min", "20% min", 4.52,
        List.of("Chemical processing", "Desalination", "Offshore oil & gas", "Pulp and paper"));
    TitaniumGrade gr9 = grade("gr9", "grade-9-titanium", "Grade 9 / Ti-3Al-2.5V", "Gr.9",
        "Ti-3Al-2.5V (3% Aluminum, 2.5% Vanadium)",
        "Higher strength than CP grades with good formability. Used in tubing and aerospace.",
        "620 MPa min", "483 MPa min", "15% min", 4.48,
        List.of("Aerospace tubing", "Hydraulic systems", "Bicycle frames", "Sporting goods"));
    TitaniumGrade gr23 = grade("gr23", "grade-23-titanium", "Grade 23 / Ti-6Al-4V ELI", "Gr.23",
        "Ti-6Al-4V ELI (Extra Low Interstitials)",
        "Medical-grade Ti-6Al-4V with lower oxygen and iron. Superior fracture toughness.",
        "860 MPa min", "795 MPa min", "10% min", 4.43,
        List.of("Orthopedic implants", "Surgical instruments", "Dental implants", "Cardiac devices"));
    List<TitaniumGrade> grades = List.of(gr1, gr2, gr5, gr7, gr9, gr23);

    Standard astmB348 = standard("s1", "astm-b348", "ASTM B348",
        "Standard Specification for Titanium and Titanium Alloy Bar and Billet",
        "Covers titanium and titanium alloy bar and billet for general industrial use.", List.of("Bar", "Billet"));
    Standard astmB265 = standard("s2", "astm-b265", "ASTM B265",
        "Standard Specification for Titanium and Titanium Alloy Strip, Sheet, and Plate",
        "Covers titanium and titanium alloy strip, sheet, and plate.", List.of("Sheet", "Plate", "Strip", "Foil"));
    Standard astmB338 = standard("s3", "astm-b338", "ASTM B338",
        "Standard Specification for Seamless and Welded Titanium and Titanium Alloy Tubes",
        "Covers seamless and welded titanium tubes for condensers and heat exchangers.", List.of("Tube", "Pipe"));
    Standard astmB381 = standard("s4", "astm-b381", "ASTM B381",
        "Standard Specification for Titanium and Titanium Alloy Forgings",
        "Covers titanium and titanium alloy forgings for general industrial use.", List.of("Forgings"));
    Standard ams4928 = standard("s5", "ams-4928", "AMS 4928",
        "Titanium Alloy Bars, Billets, and Rings 6Al-4V",
        "Aerospace Material Specification for Ti-6Al-4V bar, billet and rings.", List.of("Bar", "Billet", "Ring"));
    List<Standard> standards = List.of(astmB348, astmB265, astmB338, astmB381, ams4928);

    saveCatalog(CatalogDataService.TYPE_SITE_CONFIG, "site", "site", "Site Config", CatalogDataService.STATUS_PUBLISHED, 0,
        new SiteConfig("CNBJTI", "Baoji Titanium Materials & Custom Processing for Global Buyers", "sales@cnbjti.com",
            "+86-917-3456789", "+8613912345678", "No.88 Titanium Valley Industrial Park, High-Tech Zone",
            "Baoji, Shaanxi", "China", Map.of("linkedin", "https://linkedin.com/company/cnbjti"), defaultAboutPage(),
            defaultHomePage(), defaultCertificatesPage(), defaultFactoryTourPage()));
    for (int i = 0; i < categories.size(); i++) {
      ProductCategory item = categories.get(i);
      saveCatalog(CatalogDataService.TYPE_CATEGORY, item.id(), item.slug(), item.name(), CatalogDataService.STATUS_PUBLISHED, i, item);
    }
    for (int i = 0; i < grades.size(); i++) {
      TitaniumGrade item = grades.get(i);
      saveCatalog(CatalogDataService.TYPE_GRADE, item.id(), item.slug(), item.name(), CatalogDataService.STATUS_PUBLISHED, i, item);
    }
    for (int i = 0; i < standards.size(); i++) {
      Standard item = standards.get(i);
      saveCatalog(CatalogDataService.TYPE_STANDARD, item.id(), item.slug(), item.code(), CatalogDataService.STATUS_PUBLISHED, i, item);
    }

    List<Product> products = List.of(
        product("p1", "grade-2-titanium-round-bar", "Grade 2 Titanium Round Bar", bar, List.of(gr2), List.of(astmB348), "Diameter", "3 - 300", "mm"),
        product("p2", "grade-5-titanium-plate", "Grade 5 Ti-6Al-4V Plate", sheet, List.of(gr5), List.of(astmB265), "Thickness", "1 - 100", "mm"),
        product("p3", "grade-2-titanium-seamless-tube", "Grade 2 Titanium Seamless Tube", tube, List.of(gr2), List.of(astmB338), "OD", "6 - 219", "mm"),
        product("p4", "grade-1-titanium-sheet", "Grade 1 Titanium Sheet", sheet, List.of(gr1), List.of(astmB265), "Thickness", "0.3 - 20", "mm"),
        product("p5", "grade-7-titanium-bar", "Grade 7 Titanium Bar", bar, List.of(gr7), List.of(astmB348), "Diameter", "5 - 160", "mm"),
        product("p6", "grade-9-titanium-tube", "Grade 9 Titanium Tube", tube, List.of(gr9), List.of(astmB338), "OD", "6 - 80", "mm"),
        product("p7", "grade-23-titanium-bar", "Grade 23 Ti-6Al-4V ELI Bar", bar, List.of(gr23), List.of(astmB348), "Diameter", "3 - 120", "mm"),
        product("p8", "grade-2-titanium-wire", "Grade 2 Titanium Welding Wire", wire, List.of(gr2), List.of(astmB348), "Diameter", "0.5 - 6", "mm"),
        product("p9", "grade-5-titanium-forged-disc", "Grade 5 Titanium Forged Disc", forgings, List.of(gr5), List.of(astmB381, ams4928), "Diameter", "50 - 600", "mm"),
        product("p10", "grade-2-titanium-elbow", "Grade 2 Titanium Elbow 90", fittings, List.of(gr2), List.of(astmB338), "Size", "DN15 - DN300", null)
    );
    for (int i = 0; i < products.size(); i++) {
      Product item = products.get(i);
      String status = "p10".equals(item.id()) ? "draft" : CatalogDataService.STATUS_PUBLISHED;
      saveCatalog(CatalogDataService.TYPE_PRODUCT, item.id(), item.slug(), item.name(), status, i, item);
    }

    List<Article> articles = List.of(
        article("a1", "titanium-grade-guide", "Complete Guide to Titanium Grades: Gr.1 to Gr.23",
            "Understand the differences between commercially pure and alloyed titanium grades, their properties and best applications.", "Technical Guide", "2026-03-15", 12),
        article("a2", "astm-b348-titanium-bar-standard", "ASTM B348 Titanium Bar: What Buyers Need to Know",
            "A practical guide to ASTM B348 requirements, grade options, tolerances and documentation for titanium bar procurement.", "Standards", "2026-02-28", 8),
        article("a3", "titanium-corrosion-resistance", "Titanium Corrosion Resistance: Why It Outperforms Stainless Steel",
            "Explore the corrosion mechanisms that make titanium the material of choice for chemical processing, marine and desalination.", "Technical Guide", "2026-02-10", 10),
        article("a4", "how-to-read-mtr", "How to Read a Material Test Report (MTR)",
            "A buyer's guide to interpreting titanium material test reports.", "Procurement", "", 7),
        article("a5", "titanium-in-desalination", "Titanium in Desalination: MSF vs MED Plant Applications",
            "How titanium tubes and sheets are used in desalination plants.", "Industry", "", 6)
    );
    for (int i = 0; i < articles.size(); i++) {
      Article item = articles.get(i);
      String status = i < 3 ? CatalogDataService.STATUS_PUBLISHED : "draft";
      saveCatalog(CatalogDataService.TYPE_ARTICLE, item.id(), item.slug(), item.title(), status, i, item);
    }

    List<IndustryProfile> industries = defaultIndustries();
    for (int i = 0; i < industries.size(); i++) {
      IndustryProfile item = industries.get(i);
      saveCatalog(CatalogDataService.TYPE_INDUSTRY, item.id(), item.slug(), item.name(), CatalogDataService.STATUS_PUBLISHED, i, item);
    }
  }

  private void seedRfqs() {
    rfqRepository.saveAll(List.of(
        rfq("RFQ-2026-015", "Chemours Company", "United States", "US", "procurement@chemours.com", "+1 302 774 1000", "Titanium Bar", "Gr.2", "500 kg", "Need Grade 2 titanium round bar, diameter 50mm, length 1000mm, quantity 500kg. Please quote with MTR and EN 10204 3.1 certificate.", "new", "2026-05-03", ""),
        rfq("RFQ-2026-014", "BASF SE", "Germany", "DE", "materials@basf.com", "+49 621 60 0", "Titanium Sheet", "Gr.2", "200 kg", "Requesting quote for Grade 2 titanium sheet, 2mm thickness, 1000x2000mm, for chemical reactor lining. ASTM B265 required.", "in_progress", "2026-05-02", "Sent initial response. Waiting for drawing."),
        rfq("RFQ-2026-013", "Tata Chemicals", "India", "IN", "purchase@tatachemicals.com", null, "Titanium Tube", "Gr.2", "1000 m", "Heat exchanger tubes, OD 25mm, wall 1.5mm, length 6000mm, Grade 2, ASTM B338. Quantity 1000 meters.", "quoted", "2026-04-30", "Quote sent on 2026-05-01. Awaiting PO."),
        rfq("RFQ-2026-012", "Hyundai Heavy Industries", "South Korea", "KR", "procurement@hhi.co.kr", null, "Titanium Forgings", "Gr.5", "300 kg", "Ti-6Al-4V forged discs, diameter 200mm, thickness 50mm, AMS 4928. Need 3.1 MTR and UT report.", "won", "2026-04-25", "PO received. Production in progress."),
        rfq("RFQ-2026-011", "Veolia Water Technologies", "France", "FR", "supply@veolia.com", null, "Titanium Tube", "Gr.1", "500 m", "Desalination plant tubes, Grade 1, OD 19mm, wall 0.7mm, ASTM B338. Delivery to Marseille port.", "in_progress", "2026-04-22", "Technical spec review ongoing."),
        rfq("RFQ-2026-010", "Nippon Steel", "Japan", "JP", "titanium@nipponsteel.com", null, "Titanium Bar", "Gr.5", "150 kg", "Ti-6Al-4V round bar, diameter 30mm, length 500mm, AMS 4928, annealed condition.", "quoted", "2026-04-20", "Quote sent. Competitive pricing needed."),
        rfq("RFQ-2026-009", "Sulzer Ltd", "Switzerland", "CH", "procurement@sulzer.com", null, "CNC Titanium Parts", "Gr.2", "50 pcs", "Custom CNC machined pump impellers in Grade 2 titanium. Drawing attached. Tolerance +/-0.05mm.", "new", "2026-05-03", ""),
        rfq("RFQ-2026-008", "Saipem SpA", "Italy", "IT", "materials@saipem.com", null, "Titanium Fittings", "Gr.2", "200 pcs", "Titanium pipe fittings for offshore platform: elbows 90 degrees, tees, reducers, DN50-DN150, ASTM B363.", "lost", "2026-04-15", "Lost to competitor on price.")
    ));
  }

  private void seedCustomers() {
    customerRepository.saveAll(List.of(
        customer("c1", "Chemours Company", "United States", "US", "procurement@chemours.com", 4, "2026-05-03"),
        customer("c2", "BASF SE", "Germany", "DE", "materials@basf.com", 3, "2026-05-02"),
        customer("c3", "Tata Chemicals", "India", "IN", "purchase@tatachemicals.com", 2, "2026-04-30"),
        customer("c4", "Hyundai Heavy Industries", "South Korea", "KR", "procurement@hhi.co.kr", 5, "2026-04-25"),
        customer("c5", "Veolia Water Technologies", "France", "FR", "supply@veolia.com", 2, "2026-04-22"),
        customer("c6", "Nippon Steel", "Japan", "JP", "titanium@nipponsteel.com", 3, "2026-04-20"),
        customer("c7", "Sulzer Ltd", "Switzerland", "CH", "procurement@sulzer.com", 1, "2026-05-03"),
        customer("c8", "Alfa Laval", "Sweden", "SE", "procurement@alfalaval.com", 4, "2026-04-08")
    ));
  }

  private void saveCatalog(String type, String itemId, String slug, String title, String status, int sortOrder, Object payload) {
    catalogRepository.save(new CatalogContentEntity(type + ":" + itemId, type, itemId, slug, title, status, sortOrder,
        jsonCodec.write(payload), LocalDateTime.now()));
  }

  private ProductCategory category(String id, String slug, String name, String description, String icon, int count, String imageUrl) {
    return new ProductCategory(id, slug, name, description, media("m" + id, imageUrl, name, slug + ".jpg"), icon, count, null, true, null);
  }

  private TitaniumGrade grade(String id, String slug, String name, String shortName, String composition, String description,
      String tensile, String yield, String elongation, Double density, List<String> applications) {
    return new TitaniumGrade(id, slug, name, shortName, composition, description, tensile, yield, elongation, density, applications);
  }

  private Standard standard(String id, String slug, String code, String name, String description, List<String> productTypes) {
    return new Standard(id, slug, code, name, description, productTypes);
  }

  private Product product(String id, String slug, String name, ProductCategory category, List<TitaniumGrade> grades,
      List<Standard> standards, String specLabel, String specValue, String specUnit) {
    return new Product(id, slug, name, name + " with ASTM certification and MTR available.", null, category, grades, standards,
        List.of(new ProductSpec(specLabel, specValue, specUnit), new ProductSpec("Surface", "Mill finish, polished", null)),
        List.of(media("pm" + id, category.image().url(), name, slug + ".jpg")), null, true, true);
  }

  private Article article(String id, String slug, String title, String excerpt, String category, String publishedAt, int readingTime) {
    return new Article(id, slug, title, excerpt, null,
        media("am" + id, "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80", title, slug + ".jpg"),
        category, List.of(), publishedAt, readingTime, null);
  }

  private List<IndustryProfile> defaultIndustries() {
    return List.of(
        industry("industry-chemical-processing", "chemical-processing", "Chemical Processing", "Reactors, piping and anodes",
            "Titanium is specified for corrosive chemical service where chloride attack, oxidizing media and long maintenance intervals matter.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_58a53e1a37ce4e8b8607c380292bd0bd.png", "Titanium Chemical Industry Application",
            List.of("Gr.1", "Gr.2", "Gr.7", "Gr.12"), List.of("ASTM B338", "ASTM B265", "ASTM B348", "ASME SB338"),
            List.of("Heat exchangers", "Pressure vessels", "Electrolyzer parts", "Process piping"),
            List.of("Heat number traceability", "PMI or third-party test options", "Pickled or polished surfaces", "Export crating for tubes and sheets"),
            List.of(link("Titanium Tubes", "/products/titanium-tube"), link("Titanium Sheets", "/products/titanium-sheet"), link("Titanium Fittings", "/products/titanium-fittings")),
            List.of("chemical", "reactor", "piping", "anode", "electrolyzer", "water treatment")),
        industry("industry-marine-desalination", "marine-desalination", "Marine and Desalination", "Seawater corrosion resistance",
            "Commercially pure titanium is used for ship systems, offshore structures and desalination equipment exposed to seawater and chlorides.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2db6859fc7914665b067b94f9dc60da0.jpg", "Shipbuilding and Desalination Application",
            List.of("Gr.1", "Gr.2", "Gr.7", "Gr.12"), List.of("ASTM B338", "ASTM B265", "ASTM B381"),
            List.of("Condenser tubing", "Seawater piping", "Offshore fasteners", "Plate heat exchangers"),
            List.of("Clean tube ends", "UT or eddy-current testing when required", "MTR with chemical and mechanical values", "Moisture-proof export packing"),
            List.of(link("Titanium Tubes", "/products/titanium-tube"), link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Plates", "/products/titanium-sheet")),
            List.of("marine", "ship", "offshore", "desalination", "seawater", "water treatment")),
        industry("industry-medical", "medical", "Medical and Dental", "Biocompatible titanium supply",
            "Medical projects typically require tight chemistry control, clean surfaces and full documentation for implant, dental and instrument components.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_0cd3cdf4a59044799351dcd285ef728d.jpg", "Titanium Medical Application",
            List.of("Gr.2", "Gr.4", "Gr.5", "Gr.23"), List.of("ASTM F67", "ASTM F136", "ASTM B348"),
            List.of("Dental implant blanks", "Surgical instruments", "Orthopedic parts", "Precision machined components"),
            List.of("Grade 23 ELI availability", "Low interstitial chemistry control", "Fine machining surface finish", "Lot-level MTR documentation"),
            List.of(link("Titanium Bars", "/products/titanium-bar"), link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners")),
            List.of("medical", "dental", "implant", "medicine", "knee", "biocompatible")),
        industry("industry-aerospace", "aerospace", "Aerospace and Defense", "High strength with lower weight",
            "Aerospace buyers use titanium alloys for structural parts, fasteners and machined components where strength-to-weight ratio is critical.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_747a102ba40e42f7ba78e2efa2b16b74.jpg", "Titanium Forging",
            List.of("Gr.5", "Gr.9", "Gr.23"), List.of("AMS 4928", "ASTM B348", "ASTM B381"),
            List.of("Fasteners", "Forged blanks", "CNC parts", "Hydraulic tubing"),
            List.of("Drawing review before quotation", "Tight dimensional tolerance", "Optional third-party inspection", "Batch traceability"),
            List.of(link("Titanium Bars", "/products/titanium-bar"), link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners")),
            List.of("aerospace", "aircraft", "drone", "performance", "strength", "fastener")),
        industry("industry-energy", "energy", "Energy and Hydrogen", "Electrochemical and power projects",
            "Titanium is used in energy storage, hydrogen, fuel cell and electrolyzer applications that need corrosion resistance and stable conductivity support.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_5e341e461ece4a9086ff293b260ffde9.webp", "Titanium Heat Treatment Furnace",
            List.of("Gr.1", "Gr.2", "Gr.5", "Gr.7"), List.of("ASTM B265", "ASTM B348", "ASTM B381"),
            List.of("Electrolyzer plates", "Titanium anodes", "Battery pack hardware", "Fuel cell components"),
            List.of("Surface treatment options", "Flatness and thickness control", "Custom cutting service", "Consistent batch documentation"),
            List.of(link("Titanium Sheets", "/products/titanium-sheet"), link("Titanium Electrolyzer", "/products/cnc-parts/titanium-electrolyzer-for-water-treatment"), link("CNC Titanium Parts", "/products/cnc-parts")),
            List.of("energy", "battery", "fuel cell", "hydrogen", "anode", "electrolyzer")),
        industry("industry-industrial-machining", "industrial-machining", "Industrial Machining", "Custom parts and processed blanks",
            "For industrial buyers, CNBJTI can combine stock titanium material with cutting, CNC machining, forging and finishing support.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_842b07feb73b4582b2c4d57ede43978b.png", "Titanium CNC Machining",
            List.of("Gr.2", "Gr.5", "Gr.9", "Gr.12"), List.of("ASTM B348", "ASTM B265", "ASTM B381"),
            List.of("Machined housings", "Custom washers", "Forged blanks", "Cut-to-size plate"),
            List.of("STEP, DXF, DWG or PDF drawings", "Tolerance review", "First article confirmation", "Protective packing for finished surfaces"),
            List.of(link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Bars", "/products/titanium-bar"), link("Titanium Plates", "/products/titanium-sheet")),
            List.of("machining", "casting", "manufacturing", "custom", "industrial", "component")),
        industry("industry-automotive", "automotive", "Automotive and Motorsport", "Weight reduction and heat resistance",
            "Motorsport and advanced automotive projects use titanium for exhaust, fasteners and structural parts that need lower mass without weak hardware.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_535475e4c15a489dae12d2a841b4e878.jpg", "Titanium CNC Factory",
            List.of("Gr.5", "Gr.9", "Gr.2"), List.of("ASTM B348", "ASTM B338", "ASTM B265"),
            List.of("Exhaust tubing", "Bolts and nuts", "Bracket blanks", "CNC lightweight parts"),
            List.of("Reliable alloy grade control", "Thread and tolerance inspection", "Surface finish choice", "Small-batch manufacturing support"),
            List.of(link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Tubes", "/products/titanium-tube"), link("CNC Titanium Parts", "/products/cnc-parts")),
            List.of("automotive", "motorsport", "racing", "vehicle", "nev", "battery")),
        industry("industry-electronics", "electronics", "Electronics and Consumer Devices", "Durable housings and fine finishes",
            "Consumer electronics projects use titanium when housings, buttons and small precision parts need strength, premium finish and corrosion stability.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2577ed70f8764881bc9b430246fc4bd8.png", "Titanium Sheet Surface Treatment",
            List.of("Gr.2", "Gr.5"), List.of("ASTM B348", "ASTM B265"),
            List.of("3C device housings", "Precision screws", "Machined buttons", "Thin sheet components"),
            List.of("Consistent surface appearance", "Fine machining support", "Scratch protection packing", "Prototype to batch supply"),
            List.of(link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Sheets", "/products/titanium-sheet")),
            List.of("3c", "electronic", "housing", "consumer", "device", "screw"))
    );
  }

  private IndustryProfile industry(String id, String slug, String name, String kicker, String summary, String image,
      String imageAlt, List<String> grades, List<String> standards, List<String> applications, List<String> requirements,
      List<IndustryProductLink> productLinks, List<String> articleKeywords) {
    return new IndustryProfile(id, slug, name, kicker, summary, image, imageAlt, grades, standards, applications,
        requirements, productLinks, articleKeywords);
  }

  private IndustryProductLink link(String label, String href) {
    return new IndustryProductLink(label, href);
  }

  private AboutPageConfig defaultAboutPage() {
    return new AboutPageConfig(
        "Our Story",
        "Baoji's Titanium Specialists Since 2008",
        "CNBJTI was founded in Baoji - China's titanium capital - with a single mission: make certified titanium materials accessible to global buyers with the documentation and service they need.",
        "Today we supply titanium bar, sheet, tube, wire, forgings and custom CNC parts to customers in over 50 countries. Every order ships with a full EN 10204 3.1 MTR and is backed by our technical team.",
        List.of(
            new AboutStat("2008", "Founded in Baoji"),
            new AboutStat("50+", "Countries Served"),
            new AboutStat("100+", "Titanium Specifications"),
            new AboutStat("15+", "Years Export Experience")),
        "Location Advantage",
        "Why Baoji?",
        "Baoji, Shaanxi produces over 60% of China's titanium output. Being here means direct access to the best mills, fastest lead times and most competitive pricing.",
        List.of(
            new AboutFeature("Ti", "Direct Mill Access", "Located minutes from Baoji's major titanium mills. We source directly, cutting out middlemen and reducing lead times."),
            new AboutFeature("QA", "Certified Supply Chain", "All mills are ISO 9001 certified. We audit suppliers regularly and maintain approved vendor lists for each product type."),
            new AboutFeature("EX", "Export Expertise", "Over 15 years exporting to North America, Europe, Southeast Asia and the Middle East. We handle all customs documentation.")),
        "Milestones",
        "Our Journey",
        List.of(
            new AboutTimelineEvent("2008", "Founded in Baoji", "CNBJTI established as a titanium materials trading company, focusing on export to Southeast Asia."),
            new AboutTimelineEvent("2012", "ISO 9001 Certification", "Achieved ISO 9001:2008 quality management certification. Expanded product range to include forgings and CNC parts."),
            new AboutTimelineEvent("2015", "European Market Entry", "First major contracts with German and Dutch chemical processing companies. Began supplying EN 10204 3.1 MTRs as standard."),
            new AboutTimelineEvent("2018", "CNC Machining Division", "Launched in-house CNC machining capability for custom titanium components. Added aerospace-grade Ti-6Al-4V to stock."),
            new AboutTimelineEvent("2021", "North America Expansion", "Established relationships with US aerospace and medical device manufacturers. AMS 4928 supply capability added."),
            new AboutTimelineEvent("2024", "50+ Countries Milestone", "Reached the milestone of supplying customers in over 50 countries. Launched our export website and 24-hour RFQ response commitment.")),
        "What We Stand For",
        "Our Values",
        List.of(
            new AboutFeature("TC", "Technical Accuracy", "We know titanium. Our team can advise on grade selection, processing and certification requirements."),
            new AboutFeature("MTR", "Documentation First", "Every order ships with complete MTR and certification. No exceptions, no shortcuts."),
            new AboutFeature("24h", "Fast Response", "24-hour quote turnaround. We respect your time and your project deadlines."),
            new AboutFeature("LT", "Long-term Partnership", "We build relationships, not just transactions. Many customers have been with us for over 10 years.")),
        "Ready to Work Together?",
        "Send us your requirements and our team will respond within 24 hours with a quote and technical recommendations.",
        "About CNBJTI - Baoji Titanium Materials Supplier Since 2008",
        "CNBJTI is a Baoji-based titanium materials supplier with 15+ years of export experience. We supply certified titanium to 50+ countries with EN 10204 3.1 MTR.");
  }

  private HomePageConfig defaultHomePage() {
    return new HomePageConfig(
        "Baoji Titanium Materials Manufacturer & Custom Processing Supplier",
        "CNBJTI is based in Baoji, China, focused on certified titanium materials and custom processing for global buyers.",
        "We supply titanium bar, sheet, plate, tube, wire, forgings, fittings, fasteners, anodes and CNC titanium parts with MTR, export packing and drawing-based processing support.",
        List.of(
            new HomeStat("2008", "Founded in Baoji"),
            new HomeStat("50+", "Countries Served"),
            new HomeStat("100+", "Specifications"),
            new HomeStat("24h", "RFQ Response")),
        List.of(
            new HomeFeature("MTR", "MTR Available", "Heat number, chemistry and mechanical values"),
            new HomeFeature("MOQ", "Small MOQ", "Prototype, cut-to-size and export orders"),
            new HomeFeature("OEM", "Custom Parts", "Machining, forging and drawing-based supply")),
        List.of(
            new HomeFeature("MTR", "Certified Documentation", "EN 10204 3.1 MTR, heat number traceability and third-party inspection available."),
            new HomeFeature("CUT", "Cut-to-size Supply", "Bar, plate, sheet and tube can be cut or processed before export packing."),
            new HomeFeature("OEM", "Drawing-based Parts", "CNC machining, forging and special titanium components quoted from drawings.")),
        List.of(
            new HomeCapability("Cut-to-size", "Saw, waterjet, laser", "", ""),
            new HomeCapability("CNC Machining", "Turning, milling, drilling", "", ""),
            new HomeCapability("Grinding & Polishing", "Ra 0.4 achievable", "", ""),
            new HomeCapability("Welding & Fabrication", "TIG, electron beam", "", ""),
            new HomeCapability("Surface Treatment", "Anodizing, passivation", "", ""),
            new HomeCapability("Inspection & Testing", "UT, PMI, hardness", "", "")),
        List.of(
            new HomeQualityItem("MTR", "Mill Test Report (MTR)", "EN 10204 3.1 certified, traceable to heat number"),
            new HomeQualityItem("CHEM", "Chemical Composition", "Full elemental analysis per ASTM/AMS requirements"),
            new HomeQualityItem("MECH", "Mechanical Properties", "Tensile, yield, elongation, hardness test results"),
            new HomeQualityItem("SGS", "Third-Party Inspection", "SGS, BV, TUV available on request"))
    );
  }

  private GalleryPageConfig defaultCertificatesPage() {
    return new GalleryPageConfig(
        "Certificates",
        "Certificates",
        "Our certificate gallery can be managed from the admin panel. Upload ISO, compliance, business license or third-party inspection documents here so buyers can review them before sending an RFQ.",
        List.of(),
        "Certificates - Titanium Quality Documents | CNBJTI",
        "View CNBJTI certificate images and quality documents for titanium materials, export supply and custom processing.");
  }

  private GalleryPageConfig defaultFactoryTourPage() {
    return new GalleryPageConfig(
        "Factory Tour",
        "Factory Tour",
        "A visual look at titanium processing, cutting, CNC machining, tube production, surface treatment and inspection resources used for export orders.",
        List.of(
            new ManagedGalleryItem("Titanium CNC Factory", "CNC machining and drawing-based titanium parts.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_535475e4c15a489dae12d2a841b4e878.jpg", "Titanium CNC factory"),
            new ManagedGalleryItem("Titanium Sheet Factory", "Sheet, plate and foil handling for export supply.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_07a15b16c4134c07816d6b6b56d377e7.png", "Titanium sheet factory"),
            new ManagedGalleryItem("Cutting Workshop", "Cut-to-size service for bar, plate and tube orders.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_37c2349a8a274995acd1fbab1cc85bcc.webp", "Titanium cutting workshop"),
            new ManagedGalleryItem("Surface Treatment", "Grinding, polishing and surface treatment support.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2577ed70f8764881bc9b430246fc4bd8.png", "Titanium surface treatment"),
            new ManagedGalleryItem("Tube Production", "Titanium welded tube production and inspection.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_845cb1487b3a4b57a7f269a3d82e748c.jpg", "Titanium welded tube production"),
            new ManagedGalleryItem("Pipe Equipment", "Equipment for pipe, fittings and related titanium products.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_dee5b328751444b6aa1d811a5adc00fb.png", "Titanium pipe equipment")),
        "Factory Tour - Titanium Processing Workshop | CNBJTI",
        "View CNBJTI factory tour images for titanium CNC machining, cutting, tube production, surface treatment and inspection.");
  }

  private MediaAsset media(String id, String url, String alt, String filename) {
    return new MediaAsset(id, url, null, alt, null, null, "image/jpeg", 0, filename);
  }

  private RfqEntity rfq(String id, String company, String country, String countryCode, String email, String phone,
      String product, String grade, String qty, String message, String status, String createdAt, String notes) {
    return new RfqEntity(id, company, country, countryCode, email, phone, product, grade, qty, message, status,
        LocalDate.parse(createdAt), notes);
  }

  private CustomerEntity customer(String id, String company, String country, String countryCode, String email, int count, String date) {
    return new CustomerEntity(id, company, country, countryCode, email, count, LocalDate.parse(date));
  }
}
