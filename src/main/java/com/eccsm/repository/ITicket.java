package com.eccsm.repository;

import com.eccsm.model.Ticket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ITicket extends JpaRepository<Ticket, Long> {

}