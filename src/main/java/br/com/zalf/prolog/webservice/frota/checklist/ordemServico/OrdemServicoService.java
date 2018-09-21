package br.com.zalf.prolog.webservice.frota.checklist.ordemServico;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.ManutencaoHolder;
import br.com.zalf.prolog.webservice.frota.checklist.ordemServico.model.OrdemServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jean on 11/08/16.
 * Classe OrdemServicoService responsavel por comunicar-se com a interface DAO
 */
public class OrdemServicoService {

    private OrdemServicoDao dao = Injection.provideOrdemServicoDao();
    private static final String TAG = OrdemServicoService.class.getSimpleName();

    public List<OrdemServico> getOs(String placa, String status, Long codUnidade,
                                    String tipoVeiculo, Integer limit, Long offset){
        try{
            return dao.getOs(placa, status, codUnidade, tipoVeiculo, limit, offset);
        }catch (SQLException e){
            Log.e(TAG, String.format("Erro ao buscar as OS. \n" +
                    "Placa: %s \n" +
                    "Status: %s \n" +
                    "codUnidade: %d \n" +
                    "tipoVeiculo: %s \n" +
                    "limit: %d \n" +
                    "offset: %d", placa, status, codUnidade, tipoVeiculo, limit, offset), e);
            return null;
        }
    }

    public boolean consertaItem (ItemOrdemServico item, String placa){
        try{
            return dao.consertaItem(item, placa);
        }catch (SQLException e){
            Log.e(TAG, String.format("Erro consertar um item \n" +
                    "Placa: %s", placa), e);
            return false;
        }
    }

    @NotNull
    public List<ManutencaoHolder> getResumoManutencaoHolder(@NotNull final Long codUnidade,
                                                            @Nullable final Long codTipoVeiculo,
                                                            @Nullable final String placaVeiculo,
                                                            final boolean itensEmAberto,
                                                            final int limit,
                                                            final int offset) throws ProLogException {
        try {
            return dao.getResumoManutencaoHolder(codUnidade, codTipoVeiculo, placaVeiculo, itensEmAberto, limit, offset);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao a quantidade de itens de OS do checklist.\n" +
                    "codUnidade: %d\n" +
                    "codTipoVeiculo: %d\n" +
                    "Placa: %s\n" +
                    "Itens em aberto: %b\n" +
                    "limit: %d\n" +
                    "offset: %d\n", codUnidade, codTipoVeiculo, placaVeiculo, itensEmAberto, limit, offset), throwable);
            throw Injection.provideProLogExceptionHandler().map(
                    throwable,
                    "Erro ao buscar os itens de ordem de serviço, tente novamente");
        }
    }

    public List<ItemOrdemServico> getItensOsManutencaoHolder(String placa, String status, int limit, long offset, String prioridade){
        try{
            return dao.getItensOs(placa, status, limit, offset, prioridade);
        }catch (SQLException e){
            Log.e(TAG, String.format("Erro ao buscar os itens de uma OS. \n" +
                    "Placa: %s \n" +
                    "status: %s \n" +
                    "limit: %d \n" +
                    "offset: %d \n" +
                    "prioridade: %s", placa, status, limit, offset, prioridade), e);
            return null;
        }
    }
}
