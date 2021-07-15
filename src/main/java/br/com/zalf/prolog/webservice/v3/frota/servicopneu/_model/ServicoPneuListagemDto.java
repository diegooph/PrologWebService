package br.com.zalf.prolog.webservice.v3.frota.servicopneu._model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.servico._model.TipoServico;
import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 2021-05-21
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Value
@Builder
public class ServicoPneuListagemDto {
    @NotNull
    Long codUnidadeServico;
    @NotNull
    Long codServico;
    @NotNull
    TipoServico tipoServico;
    @NotNull
    Integer quantidadeApontamentos;
    @Nullable
    LocalDateTime dataResolucao;
    @Nullable
    Double psiInserida;
    @Nullable
    Long kmConserto;
    @Nullable
    String problemaApontado;
    boolean fechadoAutomaticamente;
    @Nullable
    FormaColetaDadosAfericaoEnum formaColetaDados;
    @NotNull
    ServicoPneuStatus status;
    @Nullable
    Long codPneu;
    @Nullable
    String codCliente;
    @Nullable
    Integer posicaoPneuAberturaServico;
    @Nullable
    Long codDimensaoPneu;
    @Nullable
    Double sulcoInterno;
    @Nullable
    Double sulcoCentralInterno;
    @Nullable
    Double sulcoCentralExterno;
    @Nullable
    Double sulcoExterno;
    @Nullable
    Double menorSulco;
    @Nullable
    Double psi;
    @Nullable
    Double psiRecomendada;
    int vidaAtual;
    int vidaTotal;
    @NotNull
    Long codAfericao;
    @NotNull
    Double psiAfericao;
    @NotNull
    LocalDateTime dataHoraAbertura;
    @NotNull
    Long codVeiculo;
    @NotNull
    String placa;
    @Nullable
    String identificadorFrota;
    @Nullable
    String nomeMecanico;
    @Nullable
    String cpfMecanico;
    @Nullable
    Long codMecanico;
}
