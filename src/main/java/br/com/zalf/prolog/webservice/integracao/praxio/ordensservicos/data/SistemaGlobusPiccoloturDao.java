package br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.data;

import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistItensNokGlobus;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Created on 17/06/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface SistemaGlobusPiccoloturDao {

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
     * Este método marca o código de um checklist como a flag <code>precisa_ser_sincroniza = false</code> evitando que
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
}
