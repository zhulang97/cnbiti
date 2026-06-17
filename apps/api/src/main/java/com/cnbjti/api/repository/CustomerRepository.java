package com.cnbjti.api.repository;

import com.cnbjti.api.entity.CustomerEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {

  Optional<CustomerEntity> findByEmailIgnoreCase(String email);

  List<CustomerEntity> findAllByOrderByLastContactDescCompanyAsc();
}
