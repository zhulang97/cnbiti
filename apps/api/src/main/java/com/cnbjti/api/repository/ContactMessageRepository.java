package com.cnbjti.api.repository;

import com.cnbjti.api.entity.ContactMessageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessageEntity, String> {

  List<ContactMessageEntity> findAllByOrderByCreatedAtDescIdDesc();
}
