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
public class ChecklistListagem {
    @NotNull
    private Long codigo;
    @NotNull
    private Long codModelo;
    @NotNull
    private Long codVersaoModelo;
    @NotNull
    private LocalDateTime dataHora;
    @Nullable
    private LocalDateTime dataHoraImportadoProLog;
    @NotNull
    private Long kmVeiculoMomentoRealizacao;
    @NotNull
    private Long duracaoRealizacaoInMillis;
    @NotNull
    private Long codColaborador;
    @NotNull
    private String cpfColaborador;
    @NotNull
    private String nomeColaborador;
    @NotNull
    private Long codVeiculo;
    @NotNull
    private String placaVeiculo;
    @NotNull
    private final TipoChecklist tipo;
    @NotNull
    private Integer totalPerguntasOk;
    @NotNull
    private Integer totalPerguntasNok;
    @NotNull
    private Integer totalAlternativasOk;
    @NotNull
    private Integer totalAlternativasNok;
    private int totalImagensPerguntasOk;
    private int totalImagensAlternativasNOk;
}
