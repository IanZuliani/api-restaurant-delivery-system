package com.algaworks.algafood.domain.service;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CadastroCozinhaService {

    @Autowired
    private CozinhaRepository cozinhaRepository;

    public Cozinha salvar(Cozinha cozinha){
        return cozinhaRepository.salvar(cozinha);
    }

    public void excluir(Long cozinhaId){
        cozinhaRepository.remover(cozinhaId);
//        try {
//                cozinhaRepository.remover(cozinhaId);
//        }catch (
//                DataIntegrityViolationException e){
//            throw new EntidadeEmUsoExcption();
//        }
    }
}
