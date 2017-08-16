package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.sun.istack.internal.NotNull;

/**
 * Created by luiz on 21/07/17.
 */
public enum AvaCorpAvilanTipoResposta {
    SELECAO_UNICA(1),
    DESCRITIVA(2);

    private final int tipoResposta;

    AvaCorpAvilanTipoResposta(final int tipoResposta) {
        this.tipoResposta = tipoResposta;
    }

    public int asInt() {
        return tipoResposta;
    }

    public static AvaCorpAvilanTipoResposta fromInt(@NotNull final int value) {

        final AvaCorpAvilanTipoResposta[] tipos = AvaCorpAvilanTipoResposta.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].tipoResposta == value) {
                return tipos[i];
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de resposta encontrada para o valor: " + value);
    }
}