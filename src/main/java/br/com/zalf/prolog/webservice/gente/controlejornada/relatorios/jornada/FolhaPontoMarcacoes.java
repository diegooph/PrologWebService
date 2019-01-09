package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.jornada;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoMarcacoes {
    @Nullable
    private final LocalDateTime dataHoraInicio;
    @Nullable
    private final LocalDateTime dataHoraFim;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final Long codTipoMarcacaoPorUnidade;
    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;

    public FolhaPontoMarcacoes(@Nullable final LocalDateTime dataHoraInicio,
                               @Nullable final LocalDateTime dataHoraFim,
                               @NotNull final Long codTipoMarcacao,
                               @NotNull final Long codTipoMarcacaoPorUnidade,
                               final boolean trocouDia,
                               final boolean marcacaoInicioAjustada,
                               final boolean marcacaoFimAjustada) {
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.trocouDia = trocouDia;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
    }

    @Nullable
    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    @Nullable
    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public Long getCodTipoMarcacaoPorUnidade() {
        return codTipoMarcacaoPorUnidade;
    }

    public boolean isTrocouDia() {
        return trocouDia;
    }

    public boolean isMarcacaoInicioAjustada() {
        return marcacaoInicioAjustada;
    }

    public boolean isMarcacaoFimAjustada() {
        return marcacaoFimAjustada;
    }
}