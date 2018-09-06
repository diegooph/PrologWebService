package br.com.zalf.prolog.webservice.gente.controleintervalo.justificativa_ajuste;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class JustificativaAjusteDaoImpl implements JustificativaAjusteDao {

    @NotNull
    @Override
    public Long insertJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                          @NotNull final String token) throws Throwable {
        return null;
    }

    @Override
    public void atualizaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                            @NotNull final String token) throws Throwable {

    }

    @NotNull
    @Override
    public List<JustificativaAjuste> getJustificativasAjuste(@NotNull final Long codEmpresa,
                                                             @Nullable final Boolean ativos) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public JustificativaAjuste getJustificativaAjuste(@NotNull final Long codEmpresa,
                                                      @NotNull final Long codJustificativaAjuste) throws Throwable {
        return null;
    }

    @Override
    public void ativaInativaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                                @NotNull final String token) throws Throwable {

    }
}
