package com.cnbjti.api.service;

import com.cnbjti.api.entity.ContactMessageEntity;
import com.cnbjti.api.model.ApiModels.AdminContactMessage;
import com.cnbjti.api.model.ApiModels.ContactResponse;
import com.cnbjti.api.model.ApiModels.PublicContactRequest;
import com.cnbjti.api.repository.ContactMessageRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

  private static final Set<String> STATUSES = Set.of("new", "in_progress", "replied", "archived");

  private final ContactMessageRepository contactMessageRepository;

  public ContactMessageService(ContactMessageRepository contactMessageRepository) {
    this.contactMessageRepository = contactMessageRepository;
  }

  @Transactional
  public ContactResponse create(PublicContactRequest request) {
    ContactMessageEntity entity = new ContactMessageEntity(
        nextId(),
        request.name().trim(),
        text(request.company(), ""),
        request.email().trim(),
        text(request.phone(), ""),
        text(request.subject(), ""),
        request.message().trim(),
        text(request.source(), "contact-page"),
        "new",
        LocalDateTime.now(),
        "",
        null);
    ContactMessageEntity saved = contactMessageRepository.save(entity);
    return new ContactResponse(saved.getId(), "Message submitted successfully", "24 hours");
  }

  public List<AdminContactMessage> adminMessages() {
    return contactMessageRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
        .map(this::toAdminContactMessage)
        .toList();
  }

  public AdminContactMessage adminMessage(String id) {
    return toAdminContactMessage(find(id));
  }

  @Transactional
  public AdminContactMessage updateStatus(String id, String status) {
    if (!STATUSES.contains(status)) {
      throw new IllegalArgumentException("Unsupported contact message status");
    }
    ContactMessageEntity entity = find(id);
    entity.setStatus(status);
    return toAdminContactMessage(contactMessageRepository.save(entity));
  }

  @Transactional
  public AdminContactMessage updateNotes(String id, String notes) {
    ContactMessageEntity entity = find(id);
    entity.setNotes(text(notes, ""));
    entity.setNotesUpdatedAt(LocalDateTime.now());
    return toAdminContactMessage(contactMessageRepository.save(entity));
  }

  private ContactMessageEntity find(String id) {
    return contactMessageRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Contact message not found"));
  }

  private String nextId() {
    int next = contactMessageRepository.findAll().stream()
        .map(ContactMessageEntity::getId)
        .map(id -> id.replaceFirst("^MSG-\\d{4}-", ""))
        .filter(value -> value.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0) + 1;
    return "MSG-" + LocalDate.now().getYear() + "-" + "%03d".formatted(next);
  }

  private AdminContactMessage toAdminContactMessage(ContactMessageEntity entity) {
    return new AdminContactMessage(
        entity.getId(),
        entity.getName(),
        text(entity.getCompany(), ""),
        entity.getEmail(),
        text(entity.getPhone(), ""),
        text(entity.getSubject(), ""),
        entity.getMessage(),
        entity.getSource(),
        entity.getStatus(),
        entity.getCreatedAt().toString(),
        text(entity.getNotes(), ""),
        entity.getNotesUpdatedAt() == null ? "" : entity.getNotesUpdatedAt().toString());
  }

  private String text(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value.trim();
  }
}
