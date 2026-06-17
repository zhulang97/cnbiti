package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessageEntity {

  @Id
  private String id;

  @Column(nullable = false)
  private String name;

  private String company;

  @Column(nullable = false)
  private String email;

  private String phone;

  private String subject;

  @Column(nullable = false, columnDefinition = "text")
  private String message;

  @Column(nullable = false)
  private String source;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(columnDefinition = "text")
  private String notes;

  @Column(name = "notes_updated_at")
  private LocalDateTime notesUpdatedAt;

  protected ContactMessageEntity() {
  }

  public ContactMessageEntity(String id, String name, String company, String email, String phone, String subject,
      String message, String source, String status, LocalDateTime createdAt, String notes,
      LocalDateTime notesUpdatedAt) {
    this.id = id;
    this.name = name;
    this.company = company;
    this.email = email;
    this.phone = phone;
    this.subject = subject;
    this.message = message;
    this.source = source;
    this.status = status;
    this.createdAt = createdAt;
    this.notes = notes;
    this.notesUpdatedAt = notesUpdatedAt;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCompany() {
    return company;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getSubject() {
    return subject;
  }

  public String getMessage() {
    return message;
  }

  public String getSource() {
    return source;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getNotes() {
    return notes;
  }

  public LocalDateTime getNotesUpdatedAt() {
    return notesUpdatedAt;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setNotesUpdatedAt(LocalDateTime notesUpdatedAt) {
    this.notesUpdatedAt = notesUpdatedAt;
  }
}
