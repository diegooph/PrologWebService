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
    private final LocalDateTime dataHoraImportadoProlog;
    private final long kmVeiculoMomentoRealizacao;
    private final long duracaoRealizacaoInMillis;
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
    @Nullable
    private final String identificadorFrota;
    @NotNull
    private final TipoChecklist tipo;
    private final int totalPerguntasOk;
    private final int totalPerguntasNok;
    private final int totalAlternativasOk;
    private final int totalAlternativasNok;
    private final int totalImagensPerguntasOk;
    private final int totalImagensAlternativasNok;
    private final int totalNokBaixa;
    private final int totalNokAlta;
    private final int totalNokCritica;
}
