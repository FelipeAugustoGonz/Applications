package com.felipeaugusto.petshopApplication.service;

import com.felipeaugusto.petshopApplication.entity.ItensPedido;
import com.felipeaugusto.petshopApplication.entity.Pedido;
import com.felipeaugusto.petshopApplication.entity.Produtos;
import com.felipeaugusto.petshopApplication.repository.PedidoRepository;
import com.felipeaugusto.petshopApplication.repository.ProdutosRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private static final String STATUS_ABERTO = "ABERTO";
    private static final String STATUS_FINALIZADO = "FINALIZADO";

    private final PedidoRepository pedidoRepository;
    private final ItensPedidoService itensPedidoService;
    private final ProdutosRepository produtosRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItensPedidoService itensPedidoService,
            ProdutosRepository produtosRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itensPedidoService = itensPedidoService;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public Pedido adicionarAoCarrinho(Long idUsuario, Long idProduto, Integer quantidade) {
        Pedido pedido = pedidoRepository.findByIdUsuarioFkAndStatus(idUsuario, STATUS_ABERTO)
                .orElseGet(() -> criarCarrinho(idUsuario));

        itensPedidoService.adicionarOuAtualizarItem(pedido.getIdPedido(), idProduto, quantidade);
        return atualizarFinanceiro(pedido.getIdPedido());
    }

    @Transactional
    public Pedido removerItemDoCarrinho(Long idItemPedido, Long idPedido) {
        itensPedidoService.remover(idItemPedido);
        return atualizarFinanceiro(idPedido);
    }

    public Pedido atualizarFinanceiro(Long idPedido) {
        List<ItensPedido> itens = itensPedidoService.listarPorPedido(idPedido);
        BigDecimal total = itens.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"));
        pedido.setValorTotal(total);
        return pedidoRepository.save(pedido);
    }

    public Pedido buscarCarrinhoAberto(Long idUsuario) {
        return pedidoRepository.findByIdUsuarioFkAndStatus(idUsuario, STATUS_ABERTO)
                .orElseThrow(() -> new RuntimeException("Carrinho vazio ou nao encontrado para este usuario"));
    }

    public List<ItensPedido> buscarItensPorPedido(Long idPedido) {
        return itensPedidoService.listarPorPedido(idPedido);
    }

    @Transactional
    public Pedido finalizarPedido(Long idPedido, String enderecoEntrega) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido nao encontrado"));

        if (!STATUS_ABERTO.equals(pedido.getStatus())) {
            throw new RuntimeException("Este pedido nao pode ser finalizado pois seu status e: " + pedido.getStatus());
        }

        List<ItensPedido> itens = itensPedidoService.listarPorPedido(idPedido);
        if (itens.isEmpty()) {
            throw new RuntimeException("Nao e possivel finalizar um pedido sem itens");
        }

        baixarEstoque(itens);
        pedido.setStatus(STATUS_FINALIZADO);
        if (enderecoEntrega != null && !enderecoEntrega.isBlank()) {
            pedido.setEnderecoEntrega(enderecoEntrega);
        }

        return pedidoRepository.save(pedido);
    }

    private Pedido criarCarrinho(Long idUsuario) {
        Pedido pedido = new Pedido();
        pedido.setIdUsuarioFk(idUsuario);
        pedido.setStatus(STATUS_ABERTO);
        pedido.setValorTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }

    private void baixarEstoque(List<ItensPedido> itens) {
        for (ItensPedido item : itens) {
            Produtos produto = produtosRepository.findById(item.getIdProdutoFk())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado: " + item.getNomeProduto()));

            if (produto.getQtdEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto " + produto.getNome());
            }

            produto.setQtdEstoque(produto.getQtdEstoque() - item.getQuantidade());
            produtosRepository.save(produto);
        }
    }
}
