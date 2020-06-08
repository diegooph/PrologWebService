package br.com.zalf.prolog.webservice.frota.checklist.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 03/06/2020
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
@Data
public final class ChecklistListagem {
    @NotNull
    private final Long codigo;
    @NotNull
    private final Long codModelo;
    @NotNull
    private final Long codVersaoModelo;
    @NotNull
    private final LocalDateTime dataHora;
    @Nullable
    private final LocalDateTime dataHoraImportadoProLog;
    @NotNull
    private final Long kmVeiculoMomentoRealizacao;
    @NotNull
    private final Long duracaoRealizacaoInMillis;
    @NotNull
    private final Long codColaborador;
    @NotNull
    private final Long cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @NotNull
    private final Long codVeiculo;
    @NotNull
    private final String placaVeiculo;
    @NotNull
    private final TipoChecklist tipo;
    @NotNull
    private final Integer totalPerguntasOk;
    @NotNull
    private final Integer totalPerguntasNok;
    @NotNull
    private final Integer totalAlternativasOk;
    @NotNull
    private final Integer totalAlternativasNok;
    private final int totalImagensPerguntasOk;
    private final int totalImagensAlternativasNOk;
    private final int totalNOkBaixa;
    private final int totalNOkAlta;
    private final int totalNOkCritica;
}
