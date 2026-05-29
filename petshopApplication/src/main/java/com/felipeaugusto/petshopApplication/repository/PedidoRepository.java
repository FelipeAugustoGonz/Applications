package com.felipeaugusto.petshopApplication.repository;

import com.felipeaugusto.petshopApplication.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByIdUsuarioFkAndStatus(Long idUsuarioFk, String status);
}
