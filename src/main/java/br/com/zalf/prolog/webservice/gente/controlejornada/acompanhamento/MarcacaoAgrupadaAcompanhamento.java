package br.com.zalf.prolog.webservice.gente.controlejornada.acompanhamento;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 29/01/19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class MarcacaoAgrupadaAcompanhamento {
    @NotNull
    private final String nomeTipoMarcacao;
    @NotNull
    private final Long cpfColaborador;
    @NotNull
    private final String nomeColaborador;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoInicio;
    @Nullable
    private final MarcacaoAcompanhamento marcacaoFim;
    @Nullable
    private final String justificativaEstouro;
    @Nullable
    private final String justificativaTempoRecomendado;

    public MarcacaoAgrupadaAcompanhamento(@NotNull final String nomeTipoMarcacao,
                                          @NotNull final Long cpfColaborador,
                                          @NotNull final String nomeColaborador,
                                          @Nullable final MarcacaoAcompanhamento marcacaoInicio,
                                          @Nullable final MarcacaoAcompanhamento marcacaoFim,
                                          @Nullable final String justificativaEstouro,
                                          @Nullable final String justificativaTempoRecomendado) {
        this.nomeTipoMarcacao = nomeTipoMarcacao;
        this.cpfColaborador = cpfColaborador;
        this.nomeColaborador = nomeColaborador;
        this.marcacaoInicio = marcacaoInicio;
        this.marcacaoFim = marcacaoFim;
        this.justificativaEstouro = justificativaEstouro;
        this.justificativaTempoRecomendado = justificativaTempoRecomendado;
    }

    @NotNull
    public String getNomeTipoMarcacao() {
        return nomeTipoMarcacao;
    }

    @NotNull
    public Long getCpfColaborador() {
        return cpfColaborador;
    }

    @NotNull
    public String getNomeColaborador() {
        return nomeColaborador;
    }

    @Nullable
    public MarcacaoAcompanhamento getMarcacaoInicio() {
        return marcacaoInicio;
    }

    @Nullable
    public MarcacaoAcompanhamento getMarcacaoFim() {
        return marcacaoFim;
    }

    @Nullable
    public String getJustificativaEstouro() {
        return justificativaEstouro;
    }

    @Nullable
    public String getJustificativaTempoRecomendado() {
        return justificativaTempoRecomendado;
    }
}