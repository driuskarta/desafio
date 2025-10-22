package com.Santander.desafio.controller;

import com.Santander.desafio.dto.AgenciaRequestDTO;
import com.Santander.desafio.model.Agencia;
import com.Santander.desafio.service.AgenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/desafio")
public class AgenciaController {

    private final AgenciaService agenciaService;

    public AgenciaController(AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    // ENDPOINT 1: Cadastramento de Agência
    // URL: localhost:8080/desafio/cadastrar
    @PostMapping("/cadastrar")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<Agencia> cadastrarAgencia(@RequestBody AgenciaRequestDTO request) {
        Agencia novaAgencia = agenciaService.cadastrar(request);
        return ResponseEntity.ok(novaAgencia);
    }

    // ENDPOINT 2: Pesquisa de Distância
    // URL: localhost:8080/desafio/distancia?posX=-10&posY=5
    @GetMapping("/distancia")
    @PreAuthorize("isAuthenticated()") // Requer Autenticação OAuth2
    public ResponseEntity<Map<String, String>> pesquisarDistancia(
            @RequestParam("posX") double posX,
            @RequestParam("posY") double posY) {

        Map<String, String> distancias = agenciaService.calcularDistancias(posX, posY);
        return ResponseEntity.ok(distancias);
    }
}
