package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.SQLException;

/**
 * Created by luiz on 7/17/17.
 */
public interface IntegracaoDao {

    /**
     * Nos métodos que possuem integração, esse método será chamado para buscar a chave do {@link Sistema}
     * com o qual a empresa possui integração. Será utilizado a empresa do qual o usuário que faz o request
     * faz parte (identificado através de seu token) para buscar a chave. Caso a empresa não possua integração
     * será retornado null.
     *
     * @param userToken token do usuário.
     * @return identificador único de um {@link Sistema}.
     * @throws SQLException caso aconteça algum erro na consulta.
     */
    @Nullable
    SistemaKey getSistemaKey(@NotNull final String userToken) throws SQLException;
}