package com.felipeaugusto.petshopApplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.felipeaugusto.petshopApplication.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long > {
}