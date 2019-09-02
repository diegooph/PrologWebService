package br.com.zalf.prolog.webservice.integracao.api.controlejornada.tipomarcacao.model;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Created on 29/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class ApiTipoMarcacao {
    @NotNull
    private final Long codEmpresa;
    @NotNull
    private final Long codUnidade;
    @NotNull
    private final Long codigo;
    @NotNull
    private final String nome;
    @NotNull
    private final String icone;
    @NotNull
    private final Duration tempoRecomendadoEmMinutos;
    @NotNull
    private final Duration tempoEstouroEmMinutos;
    @NotNull
    private final LocalTime horarioSugeridoMarcar;
    private final boolean isTipoJornada;
    private final boolean descontaJornadaBruta;
    private final boolean descontaJornadaLiquida;
    private final boolean statusAtivo;

    public ApiTipoMarcacao(@NotNull final Long codEmpresa,
                           @NotNull final Long codUnidade,
                           @NotNull final Long codigo,
                           @NotNull final String nome,
                           @NotNull final String icone,
                           @NotNull final Duration tempoRecomendadoEmMinutos,
                           @NotNull final Duration tempoEstouroEmMinutos,
                           @NotNull final LocalTime horarioSugeridoMarcar,
                           final boolean isTipoJornada,
                           final boolean descontaJornadaBruta,
                           final boolean descontaJornadaLiquida,
                           final boolean statusAtivo) {
        this.codEmpresa = codEmpresa;
        this.codUnidade = codUnidade;
        this.codigo = codigo;
        this.nome = nome;
        this.icone = icone;
        this.tempoRecomendadoEmMinutos = tempoRecomendadoEmMinutos;
        this.tempoEstouroEmMinutos = tempoEstouroEmMinutos;
        this.horarioSugeridoMarcar = horarioSugeridoMarcar;
        this.isTipoJornada = isTipoJornada;
        this.descontaJornadaBruta = descontaJornadaBruta;
        this.descontaJornadaLiquida = descontaJornadaLiquida;
        this.statusAtivo = statusAtivo;
    }

    @NotNull
    public Long getCodEmpresa() {
        return codEmpresa;
    }

    @NotNull
    public Long getCodUnidade() {
        return codUnidade;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNome() {
        return nome;
    }

    @NotNull
    public String getIcone() {
        return icone;
    }

    @NotNull
    public Duration getTempoRecomendadoEmMinutos() {
        return tempoRecomendadoEmMinutos;
    }

    @NotNull
    public Duration getTempoEstouroEmMinutos() {
        return tempoEstouroEmMinutos;
    }

    @NotNull
    public LocalTime getHorarioSugeridoMarcar() {
        return horarioSugeridoMarcar;
    }

    public boolean isTipoJornada() {
        return isTipoJornada;
    }

    public boolean isDescontaJornadaBruta() {
        return descontaJornadaBruta;
    }

    public boolean isDescontaJornadaLiquida() {
        return descontaJornadaLiquida;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }
}
