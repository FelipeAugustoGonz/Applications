package com.felipeaugusto.petshopApplication.service;

import com.felipeaugusto.petshopApplication.entity.ItensPedido;
import com.felipeaugusto.petshopApplication.entity.Produtos;
import com.felipeaugusto.petshopApplication.repository.ItensPedidoRepository;
import com.felipeaugusto.petshopApplication.repository.ProdutosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItensPedidoService {

    private final ItensPedidoRepository itensPedidoRepository;
    private final ProdutosRepository produtosRepository;

    public ItensPedidoService(ItensPedidoRepository itensPedidoRepository, ProdutosRepository produtosRepository) {
        this.itensPedidoRepository = itensPedidoRepository;
        this.produtosRepository = produtosRepository;
    }

    public ItensPedido adicionarOuAtualizarItem(Long idPedido, Long idProduto, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }

        Produtos produto = produtosRepository.findById(idProduto)
                .orElseThrow(() -> new RuntimeException("Produto nao encontrado"));

        if (!Boolean.TRUE.equals(produto.getAtivo())) {
            throw new RuntimeException("Produto inativo");
        }

        ItensPedido item = itensPedidoRepository.findByIdPedidoFkAndIdProdutoFk(idPedido, idProduto)
                .orElseGet(ItensPedido::new);

        int quantidadeAtual = item.getQuantidade() == null ? 0 : item.getQuantidade();
        int novaQuantidade = quantidadeAtual + quantidade;
        if (produto.getQtdEstoque() < novaQuantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto " + produto.getNome());
        }

        item.setIdPedidoFk(idPedido);
        item.setIdProdutoFk(idProduto);
        item.setNomeProduto(produto.getNome());
        item.setQuantidade(novaQuantidade);
        item.setPrecoUnitario(produto.getPrecoDesconto());
        return itensPedidoRepository.save(item);
    }

    public List<ItensPedido> listarPorPedido(Long idPedido) {
        return itensPedidoRepository.findByIdPedidoFk(idPedido);
    }

    public void remover(Long idItemPedido) {
        itensPedidoRepository.deleteById(idItemPedido);
    }

    public void limparItensDoPedido(Long idPedido) {
        itensPedidoRepository.deleteAll(itensPedidoRepository.findByIdPedidoFk(idPedido));
    }
}
