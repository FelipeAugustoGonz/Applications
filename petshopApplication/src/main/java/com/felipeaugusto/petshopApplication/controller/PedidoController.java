package com.felipeaugusto.petshopApplication.controller;

import com.felipeaugusto.petshopApplication.entity.ItensPedido;
import com.felipeaugusto.petshopApplication.entity.Pedido;
import com.felipeaugusto.petshopApplication.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/pedidos", "/api/pedidos"})
@Tag(name = "Pedidos e carrinho", description = "Operacoes para visualizar carrinho, adicionar itens e finalizar pedidos.")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/carrinho/{usuarioId}")
    @Operation(summary = "Busca o carrinho aberto de um usuario")
    public ResponseEntity<?> obterCarrinho(@PathVariable Long usuarioId) {
        try {
            Pedido pedido = pedidoService.buscarCarrinhoAberto(usuarioId);
            List<ItensPedido> itens = pedidoService.buscarItensPorPedido(pedido.getIdPedido());
            return ResponseEntity.ok(new CarrinhoResponse(pedido, itens));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/carrinho/adicionar")
    @Operation(summary = "Adiciona um produto ao carrinho")
    public ResponseEntity<?> adicionarAoCarrinho(@RequestBody AdicionarItemRequest request) {
        try {
            Pedido pedido = pedidoService.adicionarAoCarrinho(
                    request.idUsuario(),
                    request.idProduto(),
                    request.quantidade());
            List<ItensPedido> itens = pedidoService.buscarItensPorPedido(pedido.getIdPedido());
            return ResponseEntity.ok(new CarrinhoResponse(pedido, itens));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/finalizar/{idPedido}")
    @Operation(summary = "Finaliza um pedido aberto")
    public ResponseEntity<?> finalizarPedido(@PathVariable Long idPedido,
            @RequestBody(required = false) FinalizarPedidoRequest request) {
        try {
            String enderecoEntrega = request == null ? null : request.enderecoEntrega();
            return ResponseEntity.ok(pedidoService.finalizarPedido(idPedido, enderecoEntrega));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record AdicionarItemRequest(Long idUsuario, Long idProduto, Integer quantidade) {
    }

    public record FinalizarPedidoRequest(String enderecoEntrega) {
    }

    public record CarrinhoResponse(Pedido pedido, List<ItensPedido> itens) {
    }
}
