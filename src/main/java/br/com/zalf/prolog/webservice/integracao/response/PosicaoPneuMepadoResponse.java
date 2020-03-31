package br.com.zalf.prolog.webservice.integracao.response;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 31/03/20.
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosicaoPneuMepadoResponse {
    @NotNull
    private boolean success;
    @NotNull
    private int codDiagrama;
    @Nullable
    private String errors;

    @NotNull
    public static PosicaoPneuMepadoResponse ok(@NotNull final boolean success,
                                               @NotNull final int codDiagrama,
                                               final String errors){
        return new PosicaoPneuMepadoResponse(
                success,
                codDiagrama,
                errors
        );
    }

    @NotNull
    public static PosicaoPneuMepadoResponse error(@NotNull final boolean success,
                                                  @NotNull final int codDiagrama,
                                                  @NotNull final String errors){
        return new PosicaoPneuMepadoResponse(
                success,
                codDiagrama,
                errors
        );
    }
}
