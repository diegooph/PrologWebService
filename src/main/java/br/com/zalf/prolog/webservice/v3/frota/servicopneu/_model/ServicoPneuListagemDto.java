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
    LocalDateTime dataResolucao;
    Double psiInserida;
    Long kmConserto;
    String problemaApontado;
    boolean fechadoAutomaticamente;
    FormaColetaDadosAfericaoEnum formaColetaDados;
    ServicoPneuStatus status;
    String nomeColaborador;
    String cpfColaborador;
    Long codColaborador;
    Long codPneu;
    String codCliente;
    Integer posicaoPneuAberturaServico;
    Long codDimensaoPneu;
    Double sulcoInterno;
    Double sulcoCentralInterno;
    Double sulcoCentralExterno;
    Double sulcoExterno;
    Double menorSulco;
    Double psi;
    Double psiRecomendada;
    int vidaAtual;
    int vidaTotal;
    Long codAfericao;
    Double psiAfericao;
    LocalDateTime dataAfericao;
    LocalDateTime dataHoraAbertura;
    Long codVeiculo;
    String placa;
    String identificadorFrota;
    String nomeMecanico;
    String cpfMecanico;
    Long codMecanico;
}
