package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface JustificativaAjusteDao {

    @NotNull
    Long insertJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                   @NotNull final String token) throws Throwable;

    void atualizaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                     @NotNull final String token) throws Throwable;

    @NotNull
    List<JustificativaAjuste> getJustificativasAjuste(@NotNull final Long codEmpresa,
                                                      @Nullable final Boolean ativos) throws Throwable;

    @NotNull
    JustificativaAjuste getJustificativaAjuste(@NotNull final Long codEmpresa,
                                               @NotNull final Long codJustificativaAjuste) throws Throwable;

    void ativaInativaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                         @NotNull final String token) throws Throwable;
}