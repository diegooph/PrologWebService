package br.com.zalf.prolog.webservice.frota.veiculo.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-11-12
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public enum VeiculoTipoProcesso {

    ACOPLAMENTO("ACOPLAMENTO"),
    AFERICAO("AFERICAO"),
    FECHAMENTO_SERVICO_PNEU("FECHAMENTO_SERVICO_PNEU"),
    CHECKLIST("CHECKLIST"),
    FECHAMENTO_ITEM_CHECKLIST("FECHAMENTO_ITEM_CHECKLIST"),
    EDICAO_DE_VEICULOS("EDICAO_DE_VEICULOS"),
    MOVIMENTACAO("MOVIMENTACAO"),
    SOCORRO_EM_ROTA("SOCORRO_EM_ROTA"),
    TRANSFERENCIA_DE_VEICULO("TRANSFERENCIA_DE_VEICULOS");

    @NotNull
    private final String stringRepresentation;

    VeiculoTipoProcesso(@NotNull final String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    @NotNull
    public String asString() {
        return stringRepresentation;
    }

    @NotNull
    @Override
    public String toString() {
        return asString();
    }

    @NotNull
    public static VeiculoTipoProcesso fromString(@Nullable final String text) throws IllegalArgumentException {
        if (text != null) {
            for (final VeiculoTipoProcesso veiculoTipoProcesso : VeiculoTipoProcesso.values()) {
                if (text.equalsIgnoreCase(veiculoTipoProcesso.stringRepresentation)) {
                    return veiculoTipoProcesso;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum processo encontrado para a String: " + text);
    }
}