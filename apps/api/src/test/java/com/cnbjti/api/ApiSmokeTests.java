package com.cnbjti.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cnbjti.api.entity.CatalogContentEntity;
import com.cnbjti.api.entity.StoredFileEntity;
import com.cnbjti.api.repository.CatalogContentRepository;
import com.cnbjti.api.repository.StoredFileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSmokeTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private StoredFileRepository storedFileRepository;

  @Autowired
  private CatalogContentRepository catalogContentRepository;

  @Test
  void publicCatalogIsReadable() throws Exception {
    mockMvc.perform(get("/api/public/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("OK"))
        .andExpect(jsonPath("$.data[0].slug").value("titanium-bar"));
  }

  @Test
  void loginReturnsToken() throws Exception {
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"admin\",\"password\":\"cnbjti2026\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.token").isString());
  }

  @Test
  void adminCanManageAdminUsers() throws Exception {
    String token = adminToken();

    MvcResult created = mockMvc.perform(post("/api/admin/users")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "username": "smoke-sales",
                  "displayName": "Smoke Sales",
                  "email": "smoke-sales@example.com",
                  "role": "SALES",
                  "status": "active",
                  "password": "SmokePass2026"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.username").value("smoke-sales"))
        .andExpect(jsonPath("$.data.role").value("SALES"))
        .andExpect(jsonPath("$.data.status").value("active"))
        .andReturn();
    String id = objectMapper.readTree(created.getResponse().getContentAsString()).path("data").path("id").asText();

    mockMvc.perform(get("/api/admin/users").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].username").value("admin"));

    MvcResult salesLogin = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"smoke-sales\",\"password\":\"SmokePass2026\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.user.role").value("SALES"))
        .andReturn();
    String salesToken = objectMapper.readTree(salesLogin.getResponse().getContentAsString()).path("data").path("token").asText();
    mockMvc.perform(get("/api/admin/rfqs").header(HttpHeaders.AUTHORIZATION, bearer(salesToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/admin/products").header(HttpHeaders.AUTHORIZATION, bearer(salesToken)))
        .andExpect(status().isForbidden());

    mockMvc.perform(put("/api/admin/users/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "username": "smoke-sales",
                  "displayName": "Smoke Editor",
                  "email": "smoke-editor@example.com",
                  "role": "EDITOR",
                  "status": "disabled"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.displayName").value("Smoke Editor"))
        .andExpect(jsonPath("$.data.role").value("EDITOR"))
        .andExpect(jsonPath("$.data.status").value("disabled"));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"smoke-sales\",\"password\":\"SmokePass2026\"}"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(put("/api/admin/users/{id}/password", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"password\":\"ResetPass2026\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(id));

    mockMvc.perform(put("/api/admin/users/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "username": "smoke-sales",
                  "displayName": "Smoke Editor",
                  "email": "smoke-editor@example.com",
                  "role": "EDITOR",
                  "status": "active"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("active"));

    MvcResult editorLogin = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"smoke-sales\",\"password\":\"ResetPass2026\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.user.role").value("EDITOR"))
        .andReturn();
    String editorToken = objectMapper.readTree(editorLogin.getResponse().getContentAsString()).path("data").path("token").asText();

    mockMvc.perform(get("/api/admin/users").header(HttpHeaders.AUTHORIZATION, bearer(editorToken)))
        .andExpect(status().isForbidden());
    mockMvc.perform(get("/api/admin/products").header(HttpHeaders.AUTHORIZATION, bearer(editorToken)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/admin/rfqs").header(HttpHeaders.AUTHORIZATION, bearer(editorToken)))
        .andExpect(status().isForbidden());

    mockMvc.perform(put("/api/admin/users/{id}", "u1")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "username": "admin",
                  "displayName": "admin",
                  "email": "admin@cnbjti.com",
                  "role": "SALES",
                  "status": "active"
                }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void adminCanUpdateSiteConfig() throws Exception {
    String token = adminToken();

    mockMvc.perform(get("/api/admin/site-config").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.siteName").value("CNBJTI"));

    mockMvc.perform(put("/api/admin/site-config")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "siteName": "CNBJTI Smoke",
                  "tagline": "Smoke-tested titanium supplier",
                  "email": "smoke-sales@cnbjti.com",
                  "phone": "+86-917-0000000",
                  "whatsapp": "+8613800000000",
                  "address": "Smoke Test Road 88",
                  "city": "Baoji, Shaanxi",
                  "country": "China",
                  "socialLinks": {
                    "linkedin": "https://linkedin.com/company/cnbjti-smoke",
                    "twitter": " "
                  }
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.siteName").value("CNBJTI Smoke"))
        .andExpect(jsonPath("$.data.email").value("smoke-sales@cnbjti.com"))
        .andExpect(jsonPath("$.data.socialLinks.linkedin").value("https://linkedin.com/company/cnbjti-smoke"))
        .andExpect(jsonPath("$.data.socialLinks.twitter").doesNotExist());

    mockMvc.perform(get("/api/public/site-config"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.siteName").value("CNBJTI Smoke"))
        .andExpect(jsonPath("$.data.email").value("smoke-sales@cnbjti.com"));

    mockMvc.perform(get("/api/admin/audit-logs")
            .param("targetType", "SITE_CONFIG")
            .param("action", "UPDATE")
            .param("pageSize", "10")
            .header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.items[0].actorName").value("admin"))
        .andExpect(jsonPath("$.data.items[0].action").value("UPDATE"))
        .andExpect(jsonPath("$.data.items[0].targetType").value("SITE_CONFIG"))
        .andExpect(jsonPath("$.data.items[0].targetId").value("main"))
        .andExpect(jsonPath("$.data.items[0].statusCode").value(200))
        .andExpect(jsonPath("$.data.page").value(1))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.summary.success").isNumber());
  }

  @Test
  void adminCanUpdateNavigation() throws Exception {
    String token = adminToken();

    mockMvc.perform(get("/api/admin/navigation").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].label").value("Products"))
        .andExpect(jsonPath("$.data[0].children").isArray());

    mockMvc.perform(put("/api/admin/navigation")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "items": [
                    {
                      "label": "Products",
                      "children": [
                        { "label": "Titanium Bar", "href": "/en/products/titanium-bar", "icon": "bar" },
                        { "label": "Titanium Sheet", "href": "/en/products/titanium-sheet", "icon": "sheet" }
                      ]
                    },
                    { "label": "Technical Guides", "href": "/en/resources", "badge": "New" },
                    { "label": "Contact", "href": "/en/contact" }
                  ]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[1].label").value("Technical Guides"))
        .andExpect(jsonPath("$.data[1].badge").value("New"));

    mockMvc.perform(get("/api/public/navigation"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].children[1].label").value("Titanium Sheet"))
        .andExpect(jsonPath("$.data[1].href").value("/en/resources"));

    mockMvc.perform(put("/api/admin/navigation")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"items\":[{\"label\":\" \",\"href\":\"/en/bad\"}]}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void adminCanListStoredFiles() throws Exception {
    String token = adminToken();
    String id = "file_mockmvc_media";
    storedFileRepository.deleteById(id);
    storedFileRepository.save(new StoredFileEntity(
        id,
        "2026-05-04/file_mockmvc_media.pdf",
        "drawing-pack.pdf",
        "application/pdf",
        2048,
        "http://localhost:9002/cnbjti-assets/2026-05-04/file_mockmvc_media.pdf",
        LocalDateTime.of(2026, 5, 4, 10, 30)));

    try {
      mockMvc.perform(get("/api/admin/files").header(HttpHeaders.AUTHORIZATION, bearer(token)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data[0].id").value(id))
          .andExpect(jsonPath("$.data[0].filename").value("drawing-pack.pdf"))
          .andExpect(jsonPath("$.data[0].objectName").value("2026-05-04/file_mockmvc_media.pdf"))
          .andExpect(jsonPath("$.data[0].size").value(2048));
    } finally {
      storedFileRepository.deleteById(id);
    }
  }

  @Test
  void publicUploadRejectsUnsupportedFileType() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "installer.exe",
        "application/x-msdownload",
        "not a supported upload".getBytes());

    mockMvc.perform(multipart("/api/public/uploads").file(file))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
  }

  @Test
  void adminCannotDeleteReferencedStoredFile() throws Exception {
    String token = adminToken();
    String id = "file_referenced_media";
    String url = "http://localhost:9002/cnbjti-assets/2026-05-04/file_referenced_media.jpg";
    storedFileRepository.deleteById(id);
    catalogContentRepository.deleteById("TEST:file-reference");
    storedFileRepository.save(new StoredFileEntity(
        id,
        "2026-05-04/file_referenced_media.jpg",
        "referenced-media.jpg",
        "image/jpeg",
        4096,
        url,
        LocalDateTime.of(2026, 5, 4, 11, 0)));
    catalogContentRepository.save(new CatalogContentEntity(
        "TEST:file-reference",
        "TEST",
        "file-reference",
        "file-reference",
        "File Reference",
        "published",
        0,
        "{\"image\":{\"id\":\"%s\",\"url\":\"%s\"}}".formatted(id, url),
        LocalDateTime.of(2026, 5, 4, 11, 1)));

    try {
      mockMvc.perform(delete("/api/admin/files/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    } finally {
      catalogContentRepository.deleteById("TEST:file-reference");
      storedFileRepository.deleteById(id);
    }
  }

  @Test
  void publicRfqStoresAttachmentsAndAdminCanExportCsv() throws Exception {
    String token = adminToken();
    MvcResult created = mockMvc.perform(post("/api/public/rfqs")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "productType": "Titanium Bar",
                  "grade": "Gr.2",
                  "quantity": "20 kg",
                  "dimensions": "Dia 20mm",
                  "message": "Please quote with MTR.",
                  "name": "Attachment Buyer",
                  "company": "Attachment Test Ltd",
                  "email": "attachment@example.com",
                  "destinationCountry": "United States",
                  "attachments": [
                    {
                      "id": "file_test",
                      "url": "http://localhost:9000/cnbjti-assets/test.pdf",
                      "mimeType": "application/pdf",
                      "size": 1234,
                      "filename": "drawing.pdf"
                    }
                  ]
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.rfqNo").isString())
        .andReturn();

    String id = objectMapper.readTree(created.getResponse().getContentAsString()).path("data").path("rfqNo").asText();
    mockMvc.perform(get("/api/admin/rfqs/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.attachments[0].filename").value("drawing.pdf"))
        .andExpect(jsonPath("$.data.statusUpdatedAt").isString());

    MvcResult csv = mockMvc.perform(get("/api/admin/rfqs/export.csv").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(csv.getResponse().getContentAsString()).contains("RFQ No", "Attachment Test Ltd", "drawing.pdf");
  }

  @Test
  void publicContactMessageIsVisibleAndManageableInAdmin() throws Exception {
    String token = adminToken();

    MvcResult created = mockMvc.perform(post("/api/public/contact-messages")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "name": "Contact Buyer",
                  "company": "Contact Test Ltd",
                  "email": "contact-buyer@example.com",
                  "subject": "Titanium plate inquiry",
                  "message": "Please help confirm Grade 2 plate availability.",
                  "source": "mockmvc-contact"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").isString())
        .andReturn();
    String id = objectMapper.readTree(created.getResponse().getContentAsString()).path("data").path("id").asText();

    mockMvc.perform(get("/api/admin/contact-messages").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].id").value(id))
        .andExpect(jsonPath("$.data[0].status").value("new"));

    mockMvc.perform(put("/api/admin/contact-messages/{id}/status", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"replied\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("replied"));

    mockMvc.perform(put("/api/admin/contact-messages/{id}/notes", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"notes\":\"Replied with ASTM B265 stock list.\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.notes").value("Replied with ASTM B265 stock list."))
        .andExpect(jsonPath("$.data.notesUpdatedAt").isString());

    mockMvc.perform(get("/api/admin/audit-logs")
            .param("targetType", "CONTACT_MESSAGE")
            .param("keyword", id)
            .param("status", "success")
            .header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.items[0].targetType").value("CONTACT_MESSAGE"))
        .andExpect(jsonPath("$.data.items[0].targetId").value(id))
        .andExpect(jsonPath("$.data.summary.failed").value(0));
  }

  @Test
  void adminCanReadAndUpdateCustomerDetail() throws Exception {
    String token = adminToken();

    mockMvc.perform(get("/api/admin/customers/{id}", "c1").header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.email").value("procurement@chemours.com"))
        .andExpect(jsonPath("$.data.rfqs").isArray());

    mockMvc.perform(put("/api/admin/customers/{id}", "c1")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "notes": "Prefers ASTM certificates and quick lead-time confirmation.",
                  "lastContact": "2026-05-03"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.notes").value("Prefers ASTM certificates and quick lead-time confirmation."))
        .andExpect(jsonPath("$.data.lastContact").value("2026-05-03"))
        .andExpect(jsonPath("$.data.notesUpdatedAt").isString());
  }

  @Test
  void adminCanManageCatalogReferenceData() throws Exception {
    String token = adminToken();

    MvcResult categoryCreated = mockMvc.perform(post("/api/admin/categories")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "test-titanium-anode",
                  "name": "Test Titanium Anode",
                  "description": "Test category for catalog reference data.",
                  "imageUrl": "https://example.com/test-category.jpg",
                  "icon": "anode",
                  "productCount": 1,
                  "seo": {
                    "title": "Test Titanium Anode | CNBJTI",
                    "description": "Catalog category smoke test."
                  },
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("Test Titanium Anode"))
        .andReturn();
    String categoryId = objectMapper.readTree(categoryCreated.getResponse().getContentAsString()).path("data").path("id").asText();

    MvcResult gradeCreated = mockMvc.perform(post("/api/admin/grades")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "test-grade-16-titanium",
                  "name": "Test Grade 16 Titanium",
                  "shortName": "Gr.16T",
                  "composition": "CP Ti + Pd test",
                  "description": "Test grade for catalog reference data.",
                  "tensileStrength": "345 MPa min",
                  "yieldStrength": "275 MPa min",
                  "elongation": "20% min",
                  "density": 4.52,
                  "applications": ["Chemical processing", "Smoke testing"],
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.shortName").value("Gr.16T"))
        .andReturn();
    String gradeId = objectMapper.readTree(gradeCreated.getResponse().getContentAsString()).path("data").path("id").asText();

    MvcResult standardCreated = mockMvc.perform(post("/api/admin/standards")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "test-astm-b999",
                  "code": "ASTM B999",
                  "name": "Test Titanium Reference Standard",
                  "description": "Test standard for catalog reference data.",
                  "productTypes": ["Anode", "Plate"],
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.code").value("ASTM B999"))
        .andReturn();
    String standardId = objectMapper.readTree(standardCreated.getResponse().getContentAsString()).path("data").path("id").asText();

    MvcResult productCreated = mockMvc.perform(post("/api/admin/products")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "name": "Test Reference Product",
                  "slug": "test-reference-product",
                  "categoryId": "%s",
                  "gradeIds": ["%s"],
                  "standardIds": ["%s"],
                  "status": "published",
                  "shortDescription": "Uses newly managed reference data.",
                  "description": "Created by reference data smoke test.",
                  "imageUrl": "https://example.com/test-reference-product.jpg",
                  "featured": false,
                  "inStock": true
                }
                """.formatted(categoryId, gradeId, standardId)))
        .andExpect(status().isOk())
        .andReturn();
    String productId = objectMapper.readTree(productCreated.getResponse().getContentAsString()).path("data").path("id").asText();

    mockMvc.perform(put("/api/admin/categories/{id}", categoryId)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "updated-test-titanium-anode",
                  "name": "Updated Test Titanium Anode",
                  "description": "Updated category for catalog reference data.",
                  "imageUrl": "https://example.com/updated-test-category.jpg",
                  "icon": "anode",
                  "productCount": 2,
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("updated-test-titanium-anode"));

    mockMvc.perform(put("/api/admin/grades/{id}", gradeId)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "updated-test-grade-16-titanium",
                  "name": "Updated Test Grade 16 Titanium",
                  "shortName": "Gr.16U",
                  "composition": "Updated CP Ti + Pd test",
                  "description": "Updated test grade.",
                  "tensileStrength": "350 MPa min",
                  "yieldStrength": "280 MPa min",
                  "elongation": "19% min",
                  "density": 4.52,
                  "applications": ["Chemical processing"],
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.shortName").value("Gr.16U"));

    mockMvc.perform(put("/api/admin/standards/{id}", standardId)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "slug": "updated-test-astm-b999",
                  "code": "ASTM B999-U",
                  "name": "Updated Test Titanium Reference Standard",
                  "description": "Updated test standard.",
                  "productTypes": ["Anode"],
                  "status": "published"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.code").value("ASTM B999-U"));

    mockMvc.perform(get("/api/public/products/{slug}", "test-reference-product"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.category.slug").value("updated-test-titanium-anode"))
        .andExpect(jsonPath("$.data.grades[0].shortName").value("Gr.16U"))
        .andExpect(jsonPath("$.data.standards[0].code").value("ASTM B999-U"));

    mockMvc.perform(delete("/api/admin/categories/{id}", categoryId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    mockMvc.perform(delete("/api/admin/grades/{id}", gradeId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    mockMvc.perform(delete("/api/admin/standards/{id}", standardId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

    mockMvc.perform(delete("/api/admin/products/{id}", productId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc.perform(delete("/api/admin/categories/{id}", categoryId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc.perform(delete("/api/admin/grades/{id}", gradeId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc.perform(delete("/api/admin/standards/{id}", standardId).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
  }

  @Test
  void adminCanCreateUpdateAndDeleteProduct() throws Exception {
    String token = adminToken();
    String createPayload = """
        {
          "name": "Test Titanium Sheet",
          "slug": "test-titanium-sheet",
          "categoryId": "2",
          "gradeIds": ["gr2"],
          "standardIds": ["s2"],
          "status": "draft",
          "shortDescription": "A focused test product.",
          "description": "Created by the API smoke test.",
          "imageUrl": "https://example.com/test-sheet.jpg",
          "images": [
            {
              "id": "test-product-front",
              "url": "https://example.com/test-sheet-front.jpg",
              "alt": "Test Titanium Sheet front",
              "mimeType": "image/jpeg",
              "size": 1200,
              "filename": "test-sheet-front.jpg"
            }
          ],
          "specs": [
            { "label": "Thickness", "value": "1 - 20", "unit": "mm" },
            { "label": "Surface", "value": "Mill finish" }
          ],
          "seo": {
            "title": "Test Titanium Sheet | CNBJTI",
            "description": "SEO description for a smoke-test titanium sheet.",
            "canonical": "https://example.com/products/test-titanium-sheet",
            "noIndex": false
          },
          "featured": false,
          "inStock": true
        }
        """;

    MvcResult created = mockMvc.perform(post("/api/admin/products")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("Test Titanium Sheet"))
        .andExpect(jsonPath("$.data.status").value("draft"))
        .andReturn();

    String id = objectMapper.readTree(created.getResponse().getContentAsString()).path("data").path("id").asText();
    mockMvc.perform(get("/api/admin/products/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.slug").value("test-titanium-sheet"))
        .andExpect(jsonPath("$.data.gradeIds[0]").value("gr2"))
        .andExpect(jsonPath("$.data.images[0].filename").value("test-sheet-front.jpg"))
        .andExpect(jsonPath("$.data.specs[0].label").value("Thickness"))
        .andExpect(jsonPath("$.data.seo.title").value("Test Titanium Sheet | CNBJTI"));

    String updatePayload = """
        {
          "name": "Updated Test Titanium Sheet",
          "slug": "updated-test-titanium-sheet",
          "categoryId": "2",
          "gradeIds": ["gr2", "gr5"],
          "standardIds": ["s2"],
          "status": "published",
          "shortDescription": "An updated test product.",
          "description": "Updated by the API smoke test.",
          "imageUrl": "https://example.com/updated-sheet.jpg",
          "images": [
            {
              "id": "test-product-front-updated",
              "url": "https://example.com/updated-sheet-front.jpg",
              "alt": "Updated Test Titanium Sheet front",
              "mimeType": "image/jpeg",
              "size": 1300,
              "filename": "updated-sheet-front.jpg"
            },
            {
              "id": "test-product-detail-updated",
              "url": "https://example.com/updated-sheet-detail.jpg",
              "alt": "Updated Test Titanium Sheet detail",
              "mimeType": "image/jpeg",
              "size": 1400,
              "filename": "updated-sheet-detail.jpg"
            }
          ],
          "specs": [
            { "label": "Thickness", "value": "2 - 30", "unit": "mm" },
            { "label": "Width", "value": "Up to 1500", "unit": "mm" }
          ],
          "seo": {
            "title": "Updated Test Titanium Sheet | CNBJTI",
            "description": "Updated SEO description for smoke testing.",
            "canonical": "https://example.com/products/updated-test-titanium-sheet",
            "ogTitle": "Updated Test Titanium Sheet",
            "noIndex": true
          },
          "featured": true,
          "inStock": false
        }
        """;

    mockMvc.perform(put("/api/admin/products/{id}", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatePayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.name").value("Updated Test Titanium Sheet"))
        .andExpect(jsonPath("$.data.status").value("published"));

    mockMvc.perform(get("/api/admin/products/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.images[1].filename").value("updated-sheet-detail.jpg"))
        .andExpect(jsonPath("$.data.specs[0].value").value("2 - 30"))
        .andExpect(jsonPath("$.data.seo.noIndex").value(true));

    mockMvc.perform(get("/api/public/products/{slug}", "updated-test-titanium-sheet"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.images[0].url").value("https://example.com/updated-sheet-front.jpg"))
        .andExpect(jsonPath("$.data.specs[1].label").value("Width"))
        .andExpect(jsonPath("$.data.seo.title").value("Updated Test Titanium Sheet | CNBJTI"));

    mockMvc.perform(delete("/api/admin/products/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/admin/products/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isNotFound());
  }

  @Test
  void adminCanCreatePublishAndDeleteArticle() throws Exception {
    String token = adminToken();
    String createPayload = """
        {
          "title": "Test Titanium Buying Guide",
          "slug": "test-titanium-buying-guide",
          "category": "Procurement",
          "status": "draft",
          "excerpt": "A short article used by tests.",
          "content": "Test article body.",
          "coverImageUrl": "https://example.com/test-article.jpg",
          "tags": ["Titanium", "Procurement"],
          "publishedAt": "",
          "readingTime": 4
        }
        """;

    MvcResult created = mockMvc.perform(post("/api/admin/articles")
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content(createPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.title").value("Test Titanium Buying Guide"))
        .andExpect(jsonPath("$.data.status").value("draft"))
        .andReturn();

    String id = objectMapper.readTree(created.getResponse().getContentAsString()).path("data").path("id").asText();
    mockMvc.perform(get("/api/admin/articles/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.tags[0]").value("Titanium"));

    mockMvc.perform(put("/api/admin/articles/{id}/status", id)
            .header(HttpHeaders.AUTHORIZATION, bearer(token))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"published\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("published"));

    MvcResult publicArticles = mockMvc.perform(get("/api/public/articles"))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode matchingSummary = null;
    for (JsonNode article : objectMapper.readTree(publicArticles.getResponse().getContentAsString()).path("data")) {
      if ("test-titanium-buying-guide".equals(article.path("slug").asText())) {
        matchingSummary = article;
        break;
      }
    }
    assertThat(matchingSummary).isNotNull();
    assertThat(matchingSummary.path("content").isNull()).isTrue();

    mockMvc.perform(get("/api/public/articles/test-titanium-buying-guide"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content").value("Test article body."));

    mockMvc.perform(delete("/api/admin/articles/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isOk());
    mockMvc.perform(get("/api/admin/articles/{id}", id).header(HttpHeaders.AUTHORIZATION, bearer(token)))
        .andExpect(status().isNotFound());
  }

  private String adminToken() throws Exception {
    MvcResult result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"admin\",\"password\":\"cnbjti2026\"}"))
        .andExpect(status().isOk())
        .andReturn();
    JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
    return root.path("data").path("token").asText();
  }

  private String bearer(String token) {
    return "Bearer " + token;
  }
}
