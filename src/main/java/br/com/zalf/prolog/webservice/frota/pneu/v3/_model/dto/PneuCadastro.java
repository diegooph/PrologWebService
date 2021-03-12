package br.com.zalf.prolog.webservice.frota.pneu.v3._model.dto;

import lombok.Value;

import java.util.List;

/**
 * Created on 2021-03-10
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class PneuCadastro {
    Long codUnidade;
    Long codEmpresa;
    Long codCliente;
    Long codModeloBanda;
    Long codDimensao;
    Long vidaAtual;
    Long vidaTotal;
    Long pressaoRecomendada;
    String dot;
    Long custoAquisicao;
    boolean pneuUsado;
    DadosFotoPneu dadosFotoPneu;

    @Value(staticConstructor = "of")
    public static class DadosFotoPneu {
        List<String> urls;
    }
}
