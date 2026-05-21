package com.felipeaugusto.petshopApplication.controller;

import com.felipeaugusto.petshopApplication.entity.Categoria;
import com.felipeaugusto.petshopApplication.entity.Produtos;
import com.felipeaugusto.petshopApplication.service.ProdutosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/produtos", "/api/produtos"})
public class ProdutosController {

    private final ProdutosService produtosService;

    public ProdutosController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @GetMapping
    public List<Produtos> getAll() {
        return produtosService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ProdutoRequest request) {
        try {
            Produtos produtos = request.toEntity();
            return ResponseEntity.ok(produtosService.save(produtos));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produtos> buscarPorId(@PathVariable Long id) {

        Produtos produtos = produtosService.buscarPorId(id);

        if (produtos == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(produtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                              @RequestBody ProdutoRequest request) {

        Produtos atualizada;
        try {
            Produtos produtos = request.toEntity();
            atualizada = produtosService.atualizar(id, produtos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        if (atualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        boolean deletado = produtosService.deletar(id);

        if (!deletado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{id}")
    public List<Produtos> buscarPorCategoria(@PathVariable Long id) {
        return produtosService.buscarPorCategoria(id);
    }

    public record ProdutoRequest(
            String nome,
            String descricao,
            BigDecimal preco,
            BigDecimal precoDesconto,
            String imagem,
            Integer qtdEstoque,
            Boolean ativo,
            Long categoriaId,
            Categoria categoria) {

        public Produtos toEntity() {
            Produtos produto = new Produtos();
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setPrecoDesconto(precoDesconto);
            produto.setImagem(imagem);
            if (qtdEstoque != null) {
                produto.setQtdEstoque(qtdEstoque);
            }
            produto.setAtivo(ativo);

            Long idCategoria = categoriaId;
            if (idCategoria == null && categoria != null) {
                idCategoria = categoria.getId();
            }

            if (idCategoria != null) {
                Categoria categoriaProduto = new Categoria();
                categoriaProduto.setId(idCategoria);
                produto.setCategoria(categoriaProduto);
            }

            return produto;
        }
    }
}
