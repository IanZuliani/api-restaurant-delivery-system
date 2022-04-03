package com.algaworks.algafood.api.controller;

import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroRestauranteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private CadastroRestauranteService cadastroRestaurante;

    @GetMapping
    private List<Restaurante> listar(){
        return restauranteRepository.listar();
    }

    @GetMapping("/{restauranteId}")
    private ResponseEntity<Restaurante> buscar(@PathVariable Long restauranteId){

        Restaurante restaurante = restauranteRepository.buscar(restauranteId);

        if(restaurante != null){
            return ResponseEntity.ok(restaurante);
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{restauranteId}")
    private ResponseEntity<?> atualizar(@PathVariable Long restauranteId,
                                         @RequestBody Restaurante restaurante){

        try {
            Restaurante restauranteAtual = restauranteRepository.buscar(restauranteId);
            if(restauranteAtual != null){
                BeanUtils.copyProperties(restaurante, restauranteAtual, "id");
                cadastroRestaurante.salvar(restauranteAtual);
                return ResponseEntity.ok(restauranteAtual);
            }
            return ResponseEntity.notFound().build();
        }catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    @PostMapping
    private ResponseEntity<?> adicionar(@RequestBody Restaurante restaurante) {
        try {
            restaurante = cadastroRestaurante.salvar(restaurante);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(restaurante);
        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarParcial(@PathVariable Long id,
                                              @RequestBody Map<String, Object> campos){
        Restaurante restauranteAtual = restauranteRepository.buscar(id);
        if(restauranteAtual == null){
            ResponseEntity.notFound().build();
        }

        merge(campos, restauranteAtual);

        //atualiza os dados.
        return atualizar(id, restauranteAtual);
    }

    private void merge(Map<String, Object> dadossOrigem, Restaurante restauranteDestino) {
        //cria um objeto do tipo objectMapper para conseguimos convertere valores
        ObjectMapper objectMapper = new ObjectMapper();
        //usa a funcao convertValue, para podermos converter os valores vindos da API num tipo Restaurante
        Restaurante restauranteOrigem = objectMapper.convertValue(dadossOrigem, Restaurante.class);

        //faz um foreach nos dados de origem para sabermos qual campo alterar
        dadossOrigem.forEach((nomePropriedade, valorPropriedade)->{
            //busca os campos que sao para atualizar sao iguais a classe
            Field field = ReflectionUtils.findField(Restaurante.class, nomePropriedade);
            //como os atributos da classe e private precisamos deixalas acessiveis
            field.setAccessible(true);
            //pega o valor com base nos campos passados pela api que sao iguais os que vem do banco
            Object novoValor = ReflectionUtils.getField(field, restauranteOrigem);

            System.out.println(nomePropriedade + " = " + valorPropriedade);

            //seta os valores dos campos field para o restairante que vamos atualizar
            ReflectionUtils.setField(field, restauranteDestino, novoValor);

        });
    }
}