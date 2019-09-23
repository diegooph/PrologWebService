package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaCadastro;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class PneuNomenclaturaService {
    private static final String TAG = PneuNomenclaturaService.class.getSimpleName();
    @NotNull
    private final PneuNomenclaturaDao dao = Injection.providePneuNomenclaturaDao();

    @NotNull
    public Response insertOrUpdateNomenclatura(@NotNull final PneuNomenclaturaCadastro pneuNomenclaturaCadastro,
                                               @NotNull final String userToken) throws ProLogException {
        try {
            if (pneuNomenclaturaCadastro.getNomenclaturas().isEmpty()) {
                throw new GenericException("Erro!\nNenhuma informação de nomenclatura enviada.");
            }

            dao.insertOrUpdateNomenclatura(pneuNomenclaturaCadastro, TokenCleaner.getOnlyToken(userToken));
            return Response.ok("Nomenclaturas cadastradas com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao cadastradar nomenclaturas", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao cadastradar a nomenclaturas, tente novamente");
        }
    }

    @NotNull
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(@NotNull final Long codEmpresa,
                                                                                      @NotNull final Long codDiagrama)
            throws ProLogException {
        try {
            return dao.getPneuNomenclaturaItemVisualizacao(
                    codEmpresa,
                    codDiagrama);
        } catch (final Throwable t) {
            final String errorMessage = "Erro ao buscar nomenclaturas";
            Log.e(TAG, errorMessage, t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar nomenclaturas, tente novamente");
        }
    }
}
