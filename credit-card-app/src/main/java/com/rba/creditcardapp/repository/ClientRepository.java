package com.rba.creditcardapp.repository;

import com.rba.creditcardapp.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByOib(String oib);

    boolean existsByOib(String oib);

    void deleteByOib(String oib);

    Page<Client> findAll(Pageable pageable);
}