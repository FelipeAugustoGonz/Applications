package com.felipeaugusto.petshopApplication.service;

import com.felipeaugusto.petshopApplication.entity.Produtos;
import com.felipeaugusto.petshopApplication.repository.CategoriaRepository;
import com.felipeaugusto.petshopApplication.repository.ProdutosRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutosService {
    private final ProdutosRepository produtosRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutosService(ProdutosRepository produtosRepository, CategoriaRepository categoriaRepository) {
        this.produtosRepository = produtosRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produtos> getAll() {
        return produtosRepository.findAll();
    }

    public Produtos save(Produtos produtos) {
        prepararProdutoParaSalvar(produtos);
        return produtosRepository.save(produtos);
    }

    public Produtos buscarPorId(Long id) {
        return produtosRepository.findById(id).orElse(null);
    }

    public boolean deletar(Long id) {
        if (!produtosRepository.existsById(id)) {
            return false;
        }
        produtosRepository.deleteById(id);
        return true;
    }

    public Produtos atualizar(Long id, Produtos produtos) {
        Optional<Produtos> existente = produtosRepository.findById(id);
        if (existente.isEmpty()) {
            return null;
        }
        Produtos p = existente.get();
        p.setNome(produtos.getNome());
        p.setDescricao(produtos.getDescricao());
        p.setPreco(produtos.getPreco());
        p.setPrecoDesconto(produtos.getPrecoDesconto());
        p.setImagem(produtos.getImagem());
        p.setQtdEstoque(produtos.getQtdEstoque());
        p.setAtivo(produtos.getAtivo());
        p.setCategoria(produtos.getCategoria());
        prepararProdutoParaSalvar(p);
        return produtosRepository.save(p);
    }

    public List<Produtos> buscarPorCategoria(Long id) {
        return produtosRepository.findByCategoriaId(id);
    }

    public boolean existeProdutoNaCategoria(Long idCategoria) {
        return produtosRepository.existsByCategoriaId(idCategoria);
    }

    private void prepararProdutoParaSalvar(Produtos produtos) {
        if (produtos.getCategoria() == null || produtos.getCategoria().getId() == null) {
            throw new RuntimeException("Categoria obrigatoria para cadastrar produto");
        }

        produtos.setCategoria(categoriaRepository.findById(produtos.getCategoria().getId())
                .orElseThrow(() -> new RuntimeException("Categoria nao encontrada")));

        if (produtos.getAtivo() == null) {
            produtos.setAtivo(true);
        }

        if (produtos.getQtdEstoque() == null) {
            produtos.setQtdEstoque(0);
        }
    }
}
