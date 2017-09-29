package br.com.zalf.prolog.webservice.integracao.avacorpavilan;

import com.sun.istack.internal.NotNull;

/**
 * Created on 9/29/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum  AvacorpAvilanTipoChecklist {
    SAIDA("Saida"),
    RETORNO("Retorno");

    private final String tipoChecklist;

    AvacorpAvilanTipoChecklist(final String tipoChecklist) {
        this.tipoChecklist = tipoChecklist;
    }

    public String asString() {
        return tipoChecklist;
    }

    public static AvacorpAvilanTipoChecklist fromString(@NotNull final String value) {

        final AvacorpAvilanTipoChecklist[] tipos = AvacorpAvilanTipoChecklist.values();
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].tipoChecklist.equals(value)) {
                return tipos[i];
            }
        }

        throw new IllegalArgumentException("Nenhum tipo de checklist encontrado para o valor: " + value);
    }
}