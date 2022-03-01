package com.algaworks.algafood.api.controller;


import com.algaworks.algafood.api.model.CozinhasXmlWrapper;
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/cozinhas", produces = MediaType.APPLICATION_JSON_VALUE)
public class CozinhaController {

    @Autowired
    private CozinhaRepository cozinhaRepository;

    @Autowired
    private CadastroCozinhaService cadastroCozinhaService;

    @GetMapping()
    public List<Cozinha> listar(){
        return cozinhaRepository.listar();
    }

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public CozinhasXmlWrapper listarXml(){
        return new CozinhasXmlWrapper(cozinhaRepository.listar());
    }

    //@ResponseStatus(HttpStatus.CREATED)
    @GetMapping("/{cozinhaId}")
    public ResponseEntity<Cozinha> buscar(@PathVariable Long cozinhaId){

        Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);
        if(cozinha != null)
        {
            return ResponseEntity.ok(cozinha);
        }
        return ResponseEntity.notFound().build();

       // return ResponseEntity.status(HttpStatus.OK).body(cozinha);
        //return ResponseEntity.ok(cozinha);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.LOCATION, "http://api.algafood.local:8080/cozinhas");
//        return ResponseEntity
//                .status(HttpStatus.FOUND)
//                .headers(headers)
//                .build();

    }
    @PostMapping
    public ResponseEntity<Cozinha> adicionar(@RequestBody Cozinha cozinha){
        Cozinha cozinhaSalva =  cozinhaRepository.salvar(cozinha);
        return ResponseEntity.status(HttpStatus.CREATED).body(cozinhaSalva);
    }

    @PutMapping("/{cozinhaId}")
    public ResponseEntity<Cozinha> atualizar(@PathVariable Long cozinhaId, @RequestBody Cozinha cozinha){

        System.out.println(cozinha.toString());
        Cozinha cozinhaAtual = cozinhaRepository.buscar(cozinhaId);
        //cozinhaAtual.setNome(cozinha.getNome());

        if(cozinhaAtual != null) {
            BeanUtils.copyProperties(cozinha, cozinhaAtual, "id");
            cozinhaRepository.salvar(cozinhaAtual);
            return ResponseEntity.ok(cozinhaAtual);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{cozinhaId}")
    public ResponseEntity<Cozinha> remover(@PathVariable Long cozinhaId){

        try {
            Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);
            if(cozinha != null) {
                cozinhaRepository.remover(cozinha.getId());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.notFound().build();
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
