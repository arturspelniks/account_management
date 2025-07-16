package com.mintos.account_management.repository;

import com.mintos.account_management.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
