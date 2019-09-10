package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItem;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaService {
    private static final String TAG = PneuNomenclaturaService.class.getSimpleName();
    private final PneuNomenclaturaDao dao = Injection.providePneuNomenclaturaDao();

    @NotNull
    public Response insertOrUpdateNomenclatura(@NotNull final List<PneuNomenclaturaItem> pneuNomenclaturaItem,
                                               @NotNull final String userToken) throws ProLogException {
        try {
            dao.insertOrUpdateNomenclatura(pneuNomenclaturaItem, TokenCleaner.getOnlyToken(userToken));
            return Response.ok("Nomenclatura inserida com sucesso");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir nomenclatura", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir a nomenclatura, tente novamente");
        }
    }

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
