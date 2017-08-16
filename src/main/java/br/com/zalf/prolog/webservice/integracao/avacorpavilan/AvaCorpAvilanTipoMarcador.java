package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.sun.istack.internal.NotNull;


/**
 * Created by luiz on 21/07/17.
 */
public enum AvaCorpAvilanTipoMarcador {
    HODOMETRO(1),
    HORIMETRO(2);

    private final int tipoMarcador;

    AvaCorpAvilanTipoMarcador(final int tipoMarcador) {
        this.tipoMarcador = tipoMarcador;
    }

    public int asInt() {
        return tipoMarcador;
    }

    public static AvaCorpAvilanTipoMarcador fromInt(@NotNull final int value) {

        final AvaCorpAvilanTipoMarcador[] tipos = AvaCorpAvilanTipoMarcador.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].tipoMarcador == value) {
                return tipos[i];
            }
        }

        throw new IllegalArgumentException("Nenhum tipo marcador encontrado para o valor: " + value);
    }
}