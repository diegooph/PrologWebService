package br.com.zalf.prolog.webservice.gente.controleintervalo.justificativa_ajuste;

import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico.model.PneuTipoServico;
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
    List<PneuTipoServico> getJustificativasAjuste(@NotNull final Long codEmpresa,
                                                  @Nullable final Boolean ativos) throws Throwable;

    @NotNull
    JustificativaAjuste getJustificativaAjuste(@NotNull final Long codEmpresa,
                                               @NotNull final Long codTipoServico) throws Throwable;

    void ativaInativaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                         @NotNull final String token) throws Throwable;
}
