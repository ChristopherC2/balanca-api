package com.serasa.balanca.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "balanca")
@Getter
@Setter
public class BalancaProperties {

    private Pesagem pesagem = new Pesagem();
    private Transacao transacao = new Transacao();
    private Auth auth = new Auth();

    @Getter
    @Setter
    public static class Pesagem {
        /** Número mínimo de leituras consecutivas estáveis para confirmar o peso. */
        private int minLeituras = 5;
        /** Variação máxima (kg) aceitável entre as leituras para considerar estável. */
        private double toleranciaKg = 0.5;
        /** Tempo máximo (ms) que uma leitura permanece no buffer antes de ser descartada. */
        private long maxIdadeLeituraMs = 300_000L;
    }

    @Getter
    @Setter
    public static class Transacao {
        /** Tempo de vida (ms) de uma chave de idempotência processada. */
        private long expiracaoChaveMs = 3_600_000L;
        /** Margem de lucro aplicada sobre o custo bruto da transação. */
        private double margemLucro = 0.15;
        /** Minutos subtraídos de now() para simular o início da pesagem. */
        private int simulacaoMinutosInicio = 2;
    }

    @Getter
    @Setter
    public static class Auth {
        /** Tempo de expiração (ms) de um token gerado no login. */
        private long tokenExpiracaoMs = 600_000L;
    }
}
