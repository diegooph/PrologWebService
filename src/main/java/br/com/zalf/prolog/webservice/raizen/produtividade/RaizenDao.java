package br.com.zalf.prolog.webservice.raizen.produtividade;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

/**
 * Created on 04/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public interface RaizenDao {

    /**
     * Insere ou atualiza a produtividade RaizenItem.
     *
     * @param token            - Token recebido no request.
     *                         Será utilizado para inserir o cpf do colaborador que está requisitando esta ação.
     * @param raizenItensItens -
     * @throws SQLException - Erro ao executar consulta no Banco de Dados.
     */
    void insertOrUpdateRaizen(@NotNull final String token,
                              @NotNull final List<RaizenItem> raizenItensItens) throws SQLException;

}
