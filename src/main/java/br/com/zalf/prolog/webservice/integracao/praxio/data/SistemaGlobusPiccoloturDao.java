package br.com.zalf.prolog.webservice.integracao.praxio.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistToSyncGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaGlobusPiccoloturDao {
    /**
     * Método utilizado para buscar as informações do checklist que deverá ser sincronizado.
     *
     * @param conn               Conexão com o banco para buscar as informações do checklist.
     * @param codChecklistProLog Código do checklist que será sincronizado.
     * @return Um objeto contendo as informações do checklist para sincronizar.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    ChecklistToSyncGlobus getChecklistToSyncGlobus(@NotNull final Connection conn,
                                                   @NotNull final Long codChecklistProLog) throws Throwable;

    /**
     * Método utilizado exclusivamente para a integração entre ProLog e Globus.
     * <p>
     * Utilizado para no banco de dados um código de checklist que deve ser sincronizado posteriormente.
     *
     * @param conn                        Conexão utilizada para inserir os dados no banco de dados.
     * @param codChecklistParaSincronizar Código do checklist que será inserido.
     * @throws Throwable Se algum erro ocorrer.
     */
    void insertItensNokPendentesParaSincronizar(@NotNull final Connection conn,
                                                @NotNull final Long codChecklistParaSincronizar) throws Throwable;

    /**
     * Método utilizado exclusivamente para a integração entre ProLog e Globus.
     * <p>
     * Este método irá inserir em um tabela específica, quais foram os itens <code>NOK</code> apontados no checklist e
     * que foram enviados ao Globus para abrir Ordem de Serviços.
     *
     * @param conn                    {@link Connection Conexão} utilizada para inserir os dados no banco de dados.
     * @param checklistItensNokGlobus Itens NOK apontados no checklist.
     * @throws Throwable Se algum erro ocorrer.
     */
    void insertItensNokEnviadosGlobus(
            @NotNull final Connection conn,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable;

    /**
     * Método utilizado exclusivamente para a integração entre ProLog e Globus.
     * <p>
     * Este método marca o código de um checklist com a flag <code>precisa_ser_sincronizado = false</code> evitando que
     * o mesmo seja processado e enviado para o sistema integrado de forma desnecessária.
     *
     * @param conn                              Conexão utilizada para inserir os dados no banco de dados.
     * @param codChecklistNaoPrecisaSincronizar Código do checklist que será alterado.
     * @throws Throwable Se algum erro ocorrer.
     */
    void marcaChecklistNaoPrecisaSincronizar(@NotNull final Connection conn,
                                             @NotNull final Long codChecklistNaoPrecisaSincronizar) throws Throwable;

    /**
     * Método utilizado exclusivamente para a integração entre ProLog e Globus.
     * <p>
     * Este método marca o código de um checklist como a flag <code>sincronizado = true</code> evitando que
     * o mesmo seja processado e enviado para o sistema integrado de forma desnecessária.
     *
     * @param conn                     Conexão utilizada para inserir os dados no banco de dados.
     * @param codChecklistSincronizado Código do checklist que será marcado como sincronizado.
     * @throws Throwable Se algum erro ocorrer.
     */
    void marcaChecklistSincronizado(@NotNull final Connection conn,
                                    @NotNull final Long codChecklistSincronizado) throws Throwable;

    /**
     * Método utilizado para atualizar um código de checklist com o erro que foi identificado ao tentar sincronizar.
     *
     * @param conn               Conexão utilizada para inserir os dados no banco de dados.
     * @param codChecklistProLog Código do checklist que teve erro ao sincronizar.
     * @param errorMessage       Mensagem de erro identificada ao sincronizar.
     * @param throwable          Exception que gerou a mensagem de erro.
     * @throws Throwable Se algum erro ocorrer ao salvar a identificação.
     */
    void erroAoSicronizarChecklist(@NotNull final Connection conn,
                                   @NotNull final Long codChecklistProLog,
                                   @NotNull final String errorMessage,
                                   @NotNull final Throwable throwable) throws Throwable;
}
