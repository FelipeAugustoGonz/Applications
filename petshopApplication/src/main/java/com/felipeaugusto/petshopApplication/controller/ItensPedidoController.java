package com.felipeaugusto.petshopApplication.controller;

import com.felipeaugusto.petshopApplication.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping({"/itens-pedido", "/api/itens-pedido"})
public class ItensPedidoController {

    private final PedidoService pedidoService;

    public ItensPedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> listarPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(pedidoService.buscarItensPorPedido(idPedido));
    }

    @DeleteMapping("/{idItemPedido}/pedido/{idPedido}")
    public ResponseEntity<?> removerItem(@PathVariable Long idItemPedido, @PathVariable Long idPedido) {
        try {
            return ResponseEntity.ok(pedidoService.removerItemDoCarrinho(idItemPedido, idPedido));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
