package com.Santander.desafio.service;

import com.Santander.desafio.dto.AgenciaRequestDTO;
import com.Santander.desafio.model.Agencia;
import com.Santander.desafio.repository.AgenciaRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final Cache distanciaCache;
    private final AtomicInteger cacheHitCounter = new AtomicInteger(0);
    private static final int MAX_CACHE_HITS = 10;
    private static final String CACHE_KEY = "distancias_agencias_ordenadas"; 

    public AgenciaService(AgenciaRepository agenciaRepository, CacheManager cacheManager) {
        this.agenciaRepository = agenciaRepository;
        this.distanciaCache = cacheManager.getCache("distancias"); 
    }

    public Agencia cadastrar(AgenciaRequestDTO request) {
        Agencia novaAgencia = new Agencia(); 
        novaAgencia.setNome(request.getNome() != null ? request.getNome() : "AGENCIA_" + (agenciaRepository.count() + 1));
        novaAgencia.setPosX(request.getPosX());
        novaAgencia.setPosY(request.getPosY());
        
        Agencia salva = agenciaRepository.save(novaAgencia);
        
        distanciaCache.evict(CACHE_KEY); 
        cacheHitCounter.set(0); 

        return salva;
    }

    public Map<String, String> calcularDistancias(double usuarioX, double usuarioY) {
        
        if (cacheHitCounter.incrementAndGet() > MAX_CACHE_HITS) {
            System.out.println("Cache renovado por limite de consultas (10)!");
            distanciaCache.evict(CACHE_KEY);
            cacheHitCounter.set(1);
        }

        Map<String, String> resultado = (Map<String, String>) distanciaCache.get(CACHE_KEY, Map.class);

        if (resultado == null) {
            System.out.println("Tempo excedido de busca de cache, buscando no banco");
            
            List<Agencia> agencias = agenciaRepository.findAll();

            class DistanciaAgencia {
                String nome;
                double distancia;
            }

            List<DistanciaAgencia> distanciasOrdenadas = agencias.stream()
                .map(ag -> {
                    DistanciaAgencia disAgencia = new DistanciaAgencia();
                    disAgencia.nome = ag.getNome();
                    disAgencia.distancia = calcularDistancia(usuarioX, usuarioY, ag.getPosX(), ag.getPosY());
                    return disAgencia;
                })
                .sorted(Comparator.comparingDouble(distancia -> distancia.distancia)) 
                .collect(Collectors.toList());

            resultado = distanciasOrdenadas.stream()
                .collect(Collectors.toMap(
                    disAgencia -> disAgencia.nome,
                    disAgencia -> "distância=\"" + String.format("%.2f", disAgencia.distancia) + "\"",
                    (e1, e2) -> e1,
                    LinkedHashMap::new 
                ));

            distanciaCache.put(CACHE_KEY, resultado);
            
        } else {
            System.out.println("Retornando do Cache. Contador atual: " + cacheHitCounter.get());
        }

        return resultado;
    }
    
    /*
     
     * Para achar a distancia no ponto cartesiano foi usada a formula 
     * d = Raiz de (2x-1x)² + (2y-1y)²
    
    */
    private double calcularDistancia(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
