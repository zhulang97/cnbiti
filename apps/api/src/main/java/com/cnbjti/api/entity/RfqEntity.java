package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rfqs")
public class RfqEntity {

  @Id
  private String id;

  @Column(nullable = false)
  private String company;

  @Column(nullable = false)
  private String country;

  @Column(name = "country_code", nullable = false)
  private String countryCode;

  @Column(nullable = false)
  private String email;

  private String phone;

  @Column(nullable = false)
  private String product;

  @Column(nullable = false)
  private String grade;

  @Column(nullable = false)
  private String qty;

  @Column(nullable = false, columnDefinition = "text")
  private String message;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "status_updated_at")
  private LocalDateTime statusUpdatedAt;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(name = "notes_updated_at")
  private LocalDateTime notesUpdatedAt;

  @Column(name = "attachments_json", columnDefinition = "text")
  private String attachmentsJson;

  protected RfqEntity() {
  }

  public RfqEntity(String id, String company, String country, String countryCode, String email, String phone,
      String product, String grade, String qty, String message, String status, LocalDate createdAt, String notes) {
    this(id, company, country, countryCode, email, phone, product, grade, qty, message, status, createdAt,
        createdAt == null ? null : createdAt.atStartOfDay(), notes, null, "[]");
  }

  public RfqEntity(String id, String company, String country, String countryCode, String email, String phone,
      String product, String grade, String qty, String message, String status, LocalDate createdAt,
      LocalDateTime statusUpdatedAt, String notes, LocalDateTime notesUpdatedAt, String attachmentsJson) {
    this.id = id;
    this.company = company;
    this.country = country;
    this.countryCode = countryCode;
    this.email = email;
    this.phone = phone;
    this.product = product;
    this.grade = grade;
    this.qty = qty;
    this.message = message;
    this.status = status;
    this.createdAt = createdAt;
    this.statusUpdatedAt = statusUpdatedAt;
    this.notes = notes;
    this.notesUpdatedAt = notesUpdatedAt;
    this.attachmentsJson = attachmentsJson;
  }

  public String getId() {
    return id;
  }

  public String getCompany() {
    return company;
  }

  public String getCountry() {
    return country;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getProduct() {
    return product;
  }

  public String getGrade() {
    return grade;
  }

  public String getQty() {
    return qty;
  }

  public String getMessage() {
    return message;
  }

  public String getStatus() {
    return status;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getStatusUpdatedAt() {
    return statusUpdatedAt;
  }

  public String getNotes() {
    return notes;
  }

  public LocalDateTime getNotesUpdatedAt() {
    return notesUpdatedAt;
  }

  public String getAttachmentsJson() {
    return attachmentsJson;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
    this.statusUpdatedAt = statusUpdatedAt;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setNotesUpdatedAt(LocalDateTime notesUpdatedAt) {
    this.notesUpdatedAt = notesUpdatedAt;
  }
}
