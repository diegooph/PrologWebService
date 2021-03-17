package br.com.zalf.prolog.webservice.frota.pneu.servico._model;

import br.com.zalf.prolog.webservice.frota.checklist.modelo.model.AnexoMidiaChecklistEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum OrigemFechamentoAutomaticoEnum {
    MOVIMENTACAO("MOVIMENTACAO"){
        @Override
        public String getCodigoColumnName() {
            return "COD_PROCESSO_MOVIMENTACAO";
        }

        @Override
        public String getFlagColumnName() {
            return "FECHADO_AUTOMATICAMENTE_MOVIMENTACAO";
        }
    },
    AFERICAO("AFERICAO") {
        @Override
        public String getCodigoColumnName() {
            return "COD_AFERICAO_FECHAMENTO_AUTOMATICO";
        }

        @Override
        public String getFlagColumnName() {
            return "FECHADO_AUTOMATICAMENTE_AFERICAO";
        }
    };

    @NotNull
    private final String stringRepresentation;

    OrigemFechamentoAutomaticoEnum(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public static OrigemFechamentoAutomaticoEnum fromString(@Nullable final String s) throws IllegalArgumentException {
        if (s != null) {
            final OrigemFechamentoAutomaticoEnum[] values = OrigemFechamentoAutomaticoEnum.values();
            for (final OrigemFechamentoAutomaticoEnum value : values) {
                if (s.equalsIgnoreCase(value.stringRepresentation)) {
                    return value;
                }
            }
        }

        throw new IllegalArgumentException(String.format("Nenhum enum com valor %s encontrado", s));
    }

    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    public abstract String getCodigoColumnName();

    public abstract String getFlagColumnName();

}
