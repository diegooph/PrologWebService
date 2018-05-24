package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 24/05/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class TipoServicoRecapadoraService {

    private static final String TAG = TipoServicoRecapadoraService.class.getSimpleName();
    @NotNull
    private final TipoServicoRecapadoraDao dao = Injection.provideTipoServicoRecapadoraDao();

    public AbstractResponse insertTipoServicoRecapadora(
            @NotNull final String token,
            @NotNull final TipoServicoRecapadora tipoServico) throws GenericException {
        try {
            return ResponseWithCod.ok("Tipo de Serviço inserido com sucesso",
                    dao.insertTipoServicoRecapadora(TokenCleaner.getOnlyToken(token), tipoServico));
        } catch (Exception e) {
            Log.e(TAG, "", e);
            throw new GenericException("", "", e);
        }
    }

    public Response atualizaTipoServicoRecapadora(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final TipoServicoRecapadora tipoServico) throws GenericException {
        try {
            dao.atualizaTipoServicoRecapadora(token, codEmpresa, tipoServico);
            return Response.ok("");
        } catch (Exception e) {
            Log.e(TAG, "", e);
            throw new GenericException("", "", e);
        }
    }

    public List<TipoServicoRecapadora> getTiposServicosRecapadora(@NotNull final Long codEmpresa,
                                                                  final boolean ativas) throws GenericException {
        try {
            return dao.getTiposServicosRecapadora(codEmpresa, ativas);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            throw new GenericException("", "", e);
        }
    }

    public TipoServicoRecapadora getTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                                          @NotNull final Long codTipoServico) throws GenericException {
        try {
            return dao.getTipoServicoRecapadora(codEmpresa, codTipoServico);
        } catch (Exception e) {
            Log.e(TAG, "", e);
            throw new GenericException("", "", e);
        }
    }

    public Response alterarStatusTipoServicoRecapadora(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final TipoServicoRecapadora tipoServico) throws GenericException {
        try {
            dao.alterarStatusTipoServicoRecapadora(TokenCleaner.getOnlyToken(token), codEmpresa, tipoServico);
            return Response.ok("");
        } catch (Exception e) {
            Log.e(TAG, "", e);
            throw new GenericException("", "", e);
        }
    }
}
