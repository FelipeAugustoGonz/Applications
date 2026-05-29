package com.felipeaugusto.petshopApplication.controller;

import com.felipeaugusto.petshopApplication.entity.Categoria;
import com.felipeaugusto.petshopApplication.service.CategoriaService;
import com.felipeaugusto.petshopApplication.service.ProdutosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/categorias", "/api/categorias"})
@Tag(name = "Categorias", description = "Operacoes para cadastro, consulta, atualizacao e exclusao de categorias.")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final ProdutosService produtosService;

    public CategoriaController(CategoriaService categoriaService, ProdutosService produtosService) {
        this.categoriaService = categoriaService;
        this.produtosService = produtosService;
    }

    @GetMapping
    @Operation(summary = "Lista todas as categorias")
    public List<Categoria> getAll() {
        return categoriaService.getAll();
    }

    @PostMapping
    @Operation(summary = "Cria uma categoria")
    public Categoria criar(@RequestBody Categoria categoria) {
        return categoriaService.save(categoria);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma categoria pelo ID")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {

        Categoria categoria = categoriaService.buscarPorId(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma categoria")
    public ResponseEntity<Categoria> atualizar(@PathVariable Long id,
                                               @RequestBody Categoria categoria) {

        Categoria atualizada = categoriaService.atualizar(id, categoria);

        if (atualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma categoria")
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
