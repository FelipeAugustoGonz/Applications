package com.felipeaugusto.petshopApplication.controller;

import com.felipeaugusto.petshopApplication.entity.Categoria;
import com.felipeaugusto.petshopApplication.service.CategoriaService;
import com.felipeaugusto.petshopApplication.service.ProdutosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/categorias", "/api/categorias"})
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProdutosService produtosService;

    public CategoriaController(CategoriaService categoriaService, ProdutosService produtosService) {
        this.categoriaService = categoriaService;
        this.produtosService = produtosService;
    }

    @GetMapping
    public List<Categoria> getAll() {
        return categoriaService.getAll();
    }

    @PostMapping
    public Categoria criar(@RequestBody Categoria categoria) {
        return categoriaService.save(categoria);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {

        Categoria categoria = categoriaService.buscarPorId(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id,
                                               @RequestBody Categoria categoria) {

        Categoria atualizada = categoriaService.atualizar(id, categoria);

        if (atualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (produtosService.existeProdutoNaCategoria(id)) {
            return ResponseEntity.status(409).build();
        }

        boolean deletado = categoriaService.deletar(id);

        if (!deletado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
