package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class CustomerEntity {

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

  @Column(name = "rfq_count", nullable = false)
  private int rfqCount;

  @Column(name = "last_contact", nullable = false)
  private LocalDate lastContact;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(name = "notes_updated_at")
  private LocalDateTime notesUpdatedAt;

  protected CustomerEntity() {
  }

  public CustomerEntity(String id, String company, String country, String countryCode, String email, int rfqCount,
      LocalDate lastContact) {
    this(id, company, country, countryCode, email, rfqCount, lastContact, "", null);
  }

  public CustomerEntity(String id, String company, String country, String countryCode, String email, int rfqCount,
      LocalDate lastContact, String notes, LocalDateTime notesUpdatedAt) {
    this.id = id;
    this.company = company;
    this.country = country;
    this.countryCode = countryCode;
    this.email = email;
    this.rfqCount = rfqCount;
    this.lastContact = lastContact;
    this.notes = notes;
    this.notesUpdatedAt = notesUpdatedAt;
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

  public int getRfqCount() {
    return rfqCount;
  }

  public LocalDate getLastContact() {
    return lastContact;
  }

  public String getNotes() {
    return notes;
  }

  public LocalDateTime getNotesUpdatedAt() {
    return notesUpdatedAt;
  }

  public void recordContact(LocalDate date) {
    this.rfqCount += 1;
    this.lastContact = date;
  }

  public void setLastContact(LocalDate lastContact) {
    this.lastContact = lastContact;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setNotesUpdatedAt(LocalDateTime notesUpdatedAt) {
    this.notesUpdatedAt = notesUpdatedAt;
  }
}
