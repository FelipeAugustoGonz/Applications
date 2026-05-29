package com.felipeaugusto.petshopApplication.repository;

import com.felipeaugusto.petshopApplication.entity.ItensPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItensPedidoRepository extends JpaRepository<ItensPedido, Long> {

    Optional<ItensPedido> findByIdPedidoFkAndIdProdutoFk(Long idPedidoFk, Long idProdutoFk);

    List<ItensPedido> findByIdPedidoFk(Long idPedidoFk);
}
