package br.com.zalf.prolog.webservice.frota.checklist.offline;

import br.com.zalf.prolog.webservice.frota.checklist.offline.model.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 10/03/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ChecklistOfflineDao {

    /**
     * Método utilizado para identificar se a empresa do colaborador, representado pelo {@code cpfColaborador cpf},
     * está liberada para realizar o checklist offline.
     * Para a empresa que não está apta a realizar checklist offline, a aplicação irá solicitar acesso à rede para que
     * o processo de realização de cehcklist seja executado.
     *
     * @param cpfColaborador CPF do colaborador que será utilizado para identificar a empresa.
     * @return <code>TRUE</code> se a empresa está apta a realizar o checklist offline, <code>FALSE</code>
     * caso contrário.
     * @throws Throwable Caso ocorrer algum erro na busca dos dados.
     */
    boolean getChecklistOfflineAtivoEmpresa(@NotNull final Long cpfColaborador) throws Throwable;

    @NotNull
    DadosChecklistOfflineUnidade getVersaoDadosAtual(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<ModeloChecklistOffline> getModelosChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    @NotNull
    List<ColaboradorChecklistOffline> getColaboradoresChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    List<VeiculoChecklistOffline> getVeiculosChecklistOffline(Long codUnidade);

    EmpresaChecklistOffline getEmpresaChecklistOffline(Long codUnidade);
}
