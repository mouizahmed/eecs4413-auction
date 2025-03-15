package com.teamAgile.backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.teamAgile.backend.model.Receipt;
import com.teamAgile.backend.model.User;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

}
