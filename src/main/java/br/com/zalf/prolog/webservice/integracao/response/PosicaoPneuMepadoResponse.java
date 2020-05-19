package br.com.zalf.prolog.webservice.integracao.response;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 31/03/20.
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
public class PosicaoPneuMepadoResponse {
    @NotNull
    public static final String GENERIC_ERROR_MESSAGE = "Não foi possível cadastrar o pneu no Sistema ProLog";
    private final boolean success;
    private final int codTipoVeiculo;
    @Nullable
    private final String errors;
    @Nullable
    private final Throwable throwable;

    @NotNull
    public static PosicaoPneuMepadoResponse ok(final int codTipoVeiculo) {
        return new PosicaoPneuMepadoResponse(true, codTipoVeiculo, null, null);
    }

    @NotNull
    public static PosicaoPneuMepadoResponse error(final int codTipoVeiculo,
                                                  @NotNull final String errors) {
        return new PosicaoPneuMepadoResponse(false, codTipoVeiculo, errors, null);
    }

    @NotNull
    public static PosicaoPneuMepadoResponse error(final int codTipoVeiculo,
                                                  @NotNull final String errors,
                                                  @NotNull final Throwable throwable) {
        return new PosicaoPneuMepadoResponse(false, codTipoVeiculo, errors, throwable);
    }
}
