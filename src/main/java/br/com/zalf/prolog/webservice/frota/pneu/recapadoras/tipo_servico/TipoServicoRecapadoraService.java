package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.network.AbstractResponse;
import br.com.zalf.prolog.webservice.commons.network.Response;
import br.com.zalf.prolog.webservice.commons.network.ResponseWithCod;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.TokenCleaner;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            Log.e(TAG, "Erro ao inserir o tipo de serviço", e);
            throw new GenericException("Erro ao inserir o tipo de serviço", "Algo deu errado no servidor. " +
                    "Erro ao inserir o tipo de serviço", e);
        }
    }

    public Response atualizaTipoServicoRecapadora(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final TipoServicoRecapadora tipoServico) throws GenericException {
        try {
            dao.atualizaTipoServicoRecapadora(TokenCleaner.getOnlyToken(token), codEmpresa, tipoServico);
            return Response.ok("Tipo de Serviço atualizado com sucesso");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao atualizar o tipo de serviço", e);
            throw new GenericException("Erro ao atualizar o tipo de serviço", "Algo deu errado no servidor. " +
                    "Erro ao atualizar o tipo de serviço", e);
        }
    }

    public List<TipoServicoRecapadora> getTiposServicosRecapadora(@NotNull final Long codEmpresa,
                                                                  @Nullable final Boolean ativas) throws GenericException {
        try {
            return dao.getTiposServicosRecapadora(codEmpresa, ativas);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar os tipos de serviços", e);
            throw new GenericException("Erro ao buscar os tipos de serviços", "Algo deu errado no servidor. " +
                    "Erro ao buscar os tipos de serviços", e);
        }
    }

    public TipoServicoRecapadora getTipoServicoRecapadora(@NotNull final Long codEmpresa,
                                                          @NotNull final Long codTipoServico) throws GenericException {
        try {
            return dao.getTipoServicoRecapadora(codEmpresa, codTipoServico);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o tipo de serviço", e);
            throw new GenericException("Erro ao buscar os tipo de serviço", "Algo deu errado no servidor. " +
                    "Erro ao buscar os tipo de serviço", e);
        }
    }

    public Response alterarStatusTipoServicoRecapadora(
            @NotNull final String token,
            @NotNull final Long codEmpresa,
            @NotNull final TipoServicoRecapadora tipoServico) throws GenericException {
        try {
            dao.alterarStatusTipoServicoRecapadora(TokenCleaner.getOnlyToken(token), codEmpresa, tipoServico);
            return Response.ok("Status de serviço alterado com sucesso");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao alterar o status do serviço", e);
            throw new GenericException("Erro ao alterar o status do serviço", "Algo deu errado no servidor. " +
                    "Erro ao alterar o status do serviço", e);
        }
    }
}
