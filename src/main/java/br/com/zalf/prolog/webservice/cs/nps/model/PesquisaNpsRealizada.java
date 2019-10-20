package br.com.zalf.prolog.webservice.cs.nps.model;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2019-10-10
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class PesquisaNpsRealizada {
    @NotNull
    private final Long codPesquisaNps;
    @NotNull
    private final Long codColaboradorRealizacao;
    private final short respostaPerguntaEscala;
    @Nullable
    private final String respostaPerguntaDescritiva;

    public PesquisaNpsRealizada(@NotNull final Long codPesquisaNps,
                                @NotNull final Long codColaboradorRealizacao,
                                final short respostaPerguntaEscala,
                                @Nullable final String respostaPerguntaDescritiva) {
        this.codPesquisaNps = codPesquisaNps;
        this.codColaboradorRealizacao = codColaboradorRealizacao;
        this.respostaPerguntaEscala = respostaPerguntaEscala;
        this.respostaPerguntaDescritiva = respostaPerguntaDescritiva;
    }

    @NotNull
    public Long getCodPesquisaNps() {
        return codPesquisaNps;
    }

    @NotNull
    public Long getCodColaboradorRealizacao() {
        return codColaboradorRealizacao;
    }

    public short getRespostaPerguntaEscala() {
        return respostaPerguntaEscala;
    }

    @Nullable
    public String getRespostaPerguntaDescritiva() {
        return StringUtils.trimToNull(respostaPerguntaDescritiva);
    }
}