package com.serasa.balanca.service;

import com.serasa.balanca.config.BalancaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class PesagemBuffer {

    private final BalancaProperties props;

    private record Leitura(double peso, long timestamp) {}

    private final Map<String, Deque<Leitura>> buffer = new ConcurrentHashMap<>();

    public OptionalDouble registrarLeitura(String placa, double peso) {
        long agora = System.currentTimeMillis();
        Leitura nova = new Leitura(peso, agora);

        int minLeituras = props.getPesagem().getMinLeituras();
        double tolerancia = props.getPesagem().getToleranciaKg();
        long maxIdade = props.getPesagem().getMaxIdadeLeituraMs();

        final OptionalDouble[] resultado = {OptionalDouble.empty()};

        buffer.compute(placa, (key, deque) -> {
            if (deque == null) deque = new ArrayDeque<>();

            deque.removeIf(l -> agora - l.timestamp() > maxIdade);
            deque.addLast(nova);

            if (deque.size() >= minLeituras) {
                List<Double> ultimas = deque.stream()
                        .skip(deque.size() - minLeituras)
                        .map(Leitura::peso)
                        .toList();

                double ultimoPeso = ultimas.get(ultimas.size() - 1);
                boolean estabilizou = ultimas.stream()
                        .allMatch(p -> Math.abs(p - ultimoPeso) < tolerancia);

                if (estabilizou) {
                    resultado[0] = OptionalDouble.of(ultimoPeso);
                    return null;
                }
            }
            return deque;
        });

        return resultado[0];
    }
}
