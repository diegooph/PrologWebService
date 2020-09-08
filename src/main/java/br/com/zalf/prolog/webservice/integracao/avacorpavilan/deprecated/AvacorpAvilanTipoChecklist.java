package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated;

import br.com.zalf.prolog.webservice.frota.checklist.OLD.Checklist;
import org.jetbrains.annotations.NotNull;

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

    public char asTipoProLog() {
        if (tipoChecklist.equals(SAIDA.asString())) {
            return Checklist.TIPO_SAIDA;
        }

        return Checklist.TIPO_RETORNO;
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

    public static AvacorpAvilanTipoChecklist fromTipoProLog(final char tipoProLog) {

        if (tipoProLog == Checklist.TIPO_SAIDA) {
            return AvacorpAvilanTipoChecklist.SAIDA;
        } else if (tipoProLog == Checklist.TIPO_RETORNO) {
            return AvacorpAvilanTipoChecklist.RETORNO;
        }

        throw new IllegalArgumentException("Nenhum tipo de checklist encontrado para o valor: " + tipoProLog);
    }
}