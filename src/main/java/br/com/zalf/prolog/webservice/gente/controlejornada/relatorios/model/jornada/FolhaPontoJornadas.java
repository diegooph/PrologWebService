package br.com.zalf.prolog.webservice.gente.controlejornada.relatorios.model.jornada;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created on 09/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class FolhaPontoJornadas {
    @Nullable
    private final LocalDateTime dataHoraInicioJornada;
    @Nullable
    private final LocalDateTime dataHoraFimJornada;
    @NotNull
    private final Long codTipoMarcacao;
    @NotNull
    private final Long codTipoMarcacaoPorUnidade;
    @NotNull
    private final List<FolhaPontoMarcacoes> marcacoes;
    /**
     * Indica se as marcações de início e fim foram feitas em dias diferentes.
     */
    private final boolean trocouDia;
    private final boolean marcacaoInicioAjustada;
    private final boolean marcacaoFimAjustada;

    private Duration jornadaBruta;
    private Duration jornadaLiquida;

    public FolhaPontoJornadas(@Nullable final LocalDateTime dataHoraInicioJornada,
                              @Nullable final LocalDateTime dataHoraFimJornada,
                              @NotNull final Long codTipoMarcacao,
                              @NotNull final Long codTipoMarcacaoPorUnidade,
                              @NotNull final List<FolhaPontoMarcacoes> marcacoes,
                              final boolean trocouDia,
                              final boolean marcacaoInicioAjustada,
                              final boolean marcacaoFimAjustada) {
        this.dataHoraInicioJornada = dataHoraInicioJornada;
        this.dataHoraFimJornada = dataHoraFimJornada;
        this.codTipoMarcacao = codTipoMarcacao;
        this.codTipoMarcacaoPorUnidade = codTipoMarcacaoPorUnidade;
        this.marcacoes = marcacoes;
        this.trocouDia = trocouDia;
        this.marcacaoInicioAjustada = marcacaoInicioAjustada;
        this.marcacaoFimAjustada = marcacaoFimAjustada;
    }

    @Nullable
    public LocalDateTime getDataHoraInicioJornada() {
        return dataHoraInicioJornada;
    }

    @Nullable
    public LocalDateTime getDataHoraFimJornada() {
        return dataHoraFimJornada;
    }

    @NotNull
    public Long getCodTipoMarcacao() {
        return codTipoMarcacao;
    }

    @NotNull
    public Long getCodTipoMarcacaoPorUnidade() {
        return codTipoMarcacaoPorUnidade;
    }

    @NotNull
    public List<FolhaPontoMarcacoes> getMarcacoes() {
        return marcacoes;
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