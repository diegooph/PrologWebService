package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value(staticConstructor = "of")
public class ServicoPneuListagemDto {
    Long codUnidadeServico;
    Long codServico;
    TipoServico tipoServico;
    Integer quantidadeApontamentos;
    LocalDateTime dataHoraAbertura;
    String nomeColaborador;
    String placa;
    String identificadorFrota;
    String codPneu;
    int posicaoPneuAberturaServico;
    Long codDimensaoPneu;
    Long codAfericao;
    Double psiAfericao;
    LocalDateTime dataAfericao;
    Double sulcoInterno;
    Double sulcoCentralInterno;
    Double sulcoCentralExterno;
    Double sulcoExterno;
    Double menorSulco;
    Double psi;
    Double psiRecomendada;
    int vidaAtual;
    int vidaTotal;
    LocalDateTime dataResolucao;
    Long kmConserto;
    Double psiInserida;
    String nomeMecanico;
    String problemaApontado;
    boolean fechadoAutomaticamente;
    FormaColetaDadosAfericaoEnum formaColetaDados;
}
