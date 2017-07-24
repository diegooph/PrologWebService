package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.colaborador.Colaborador;
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
     * Verifica se a empresa do {@link Colaborador} que faz o request possui integração com o {@link RecursoIntegrado}
     * informado.
     * Caso ela não possua integração, será retornado {@code null}. Do contrário, retorna a chave do {@link Sistema}
     * com o qual a integração é feita para essa empresa.
     * É importante ressaltar que caso retorne uma chave não nula para o {@link RecursoIntegrado#CHECKLIST},
     * por exemplo, isso não quer dizer que a empresa integra todos os métodos do checklist com o ProLog, mas que pelo
     * menos um deles é integrado.
     *
     * @param userToken token do usuário.
     * @param recursoIntegrado o recurso para verificar se está integrado.
     *
     * @return identificador único de um {@link Sistema}.
     * @throws SQLException caso aconteça algum erro na consulta.
     */
    @Nullable
    SistemaKey getSistemaKey(@NotNull final String userToken, @NotNull final RecursoIntegrado recursoIntegrado)
            throws SQLException;
}