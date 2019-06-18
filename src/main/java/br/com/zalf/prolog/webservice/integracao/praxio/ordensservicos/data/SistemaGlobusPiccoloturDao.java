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
     * Este método irá inserir em um tabela específica, quais foram os itens <code>NOK</code> apontados no checklist e
     * que foram enviados ao Globus para abrir Ordem de Serviços.
     *
     * @param conn {@link Connection Conexão} utilizada para inserir os dados no banco de dados.
     * @param checklistItensNokGlobus Itens NOK apontados no checklist.
     * @throws Throwable Se algum erro ocorrer.
     */
    void insertItensNokEnviadosGlobus(
            @NotNull final Connection conn,
            @NotNull final ChecklistItensNokGlobus checklistItensNokGlobus) throws Throwable;
}
