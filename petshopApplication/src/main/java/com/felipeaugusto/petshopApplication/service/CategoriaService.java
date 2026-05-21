package com.felipeaugusto.petshopApplication.service;

import com.felipeaugusto.petshopApplication.entity.Categoria;
import com.felipeaugusto.petshopApplication.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public boolean deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        categoriaRepository.deleteById(id);
        return true;
    }

    // 🔧 método atualizar COMPLETO
    public Categoria atualizar(Long id, Categoria categoria) {
        Optional<Categoria> existente = categoriaRepository.findById(id);
        if (existente.isEmpty()) {
            return null;
        }

        Categoria c = existente.get();
        c.setNome(categoria.getNome());
        c.setDescricao(categoria.getDescricao());   // ← agora atualiza descrição
        c.setAtivo(categoria.getAtivo());
        
        return categoriaRepository.save(c);
    }
}
