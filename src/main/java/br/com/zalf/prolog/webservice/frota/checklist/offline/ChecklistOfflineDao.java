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

    /**
     * Este método é utilizado internamente para a validação da versão dos dados da Unidade em comparação com a versão
     * dos dados recebidos na requisição.
     *
     * @param codUnidade Código da Unidade de onde os dados serão buscados.
     * @return Objeto {@link DadosChecklistOfflineUnidade} contendo as informações relacionadas ao checklist offline.
     * @throws Throwable Caso algum erro aconteça na execução da busca dos dados.
     * @see ChecklistOfflineService#getDadosChecklistOffline(Long, Long, boolean)
     */
    @NotNull
    DadosChecklistOfflineUnidade getVersaoDadosAtual(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Método utilizado para buscar os {@link ModeloChecklistOffline modelos de checklists} aptos a serem
     * realizados de forma offline.
     *
     * @param codUnidade Código da Unidade dos modelos de checklist.
     * @return Uma lista de {@link ModeloChecklistOffline modelos} contendo as informações buscadas.
     * @throws Throwable Se algum erro ocorrer na busca dos modelos.
     * @see ChecklistOfflineService#getDadosChecklistOffline(Long, Long, boolean)
     */
    @NotNull
    List<ModeloChecklistOffline> getModelosChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Este método busca os {@link ColaboradorChecklistOffline colaboradores} que estão aptos a realizar algum
     * {@link ModeloChecklistOffline modelo de checklist offline}.
     *
     * @param codUnidade Código da Unidade onde os colaboradores serão buscados.
     * @return Uma lista de {@link ColaboradorChecklistOffline colaboradores} contendo as informações buscadas.
     * @throws Throwable Se algum erro na busca dos colaboradores acontecer.
     */
    @NotNull
    List<ColaboradorChecklistOffline> getColaboradoresChecklistOffline(@NotNull final Long codUnidade) throws Throwable;

    List<VeiculoChecklistOffline> getVeiculosChecklistOffline(Long codUnidade);

    EmpresaChecklistOffline getEmpresaChecklistOffline(Long codUnidade);
}
