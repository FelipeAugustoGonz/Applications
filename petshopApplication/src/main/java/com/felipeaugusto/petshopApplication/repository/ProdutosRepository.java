package com.felipeaugusto.petshopApplication.repository;

import com.felipeaugusto.petshopApplication.entity.Categoria;
import com.felipeaugusto.petshopApplication.entity.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutosRepository extends JpaRepository<Produtos, Long > {
    List<Produtos> findByCategoriaId(Long id);

    boolean existsByCategoriaId(Long id);
}
