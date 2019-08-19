package br.com.zalf.prolog.webservice.frota.checklist.modelo.model.edicao;

import org.jetbrains.annotations.NotNull;

/**
 * Created on 2019-08-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PerguntaAlteracaoDescricao {
    @NotNull
    private final Long codigo;
    @NotNull
    private final String novaDescricao;
    @NotNull
    private final Integer novaOrdemExibicao;

    public PerguntaAlteracaoDescricao(@NotNull final Long codigo,
                                      @NotNull final String novaDescricao,
                                      @NotNull final Integer novaOrdemExibicao) {
        this.codigo = codigo;
        this.novaDescricao = novaDescricao;
        this.novaOrdemExibicao = novaOrdemExibicao;
    }

    @NotNull
    public Long getCodigo() {
        return codigo;
    }

    @NotNull
    public String getNovaDescricao() {
        return novaDescricao;
    }

    @NotNull
    public Integer getNovaOrdemExibicao() {
        return novaOrdemExibicao;
    }
}