package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.colaborador.model.Unidade;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.sql.SQLException;

/**
 * DAO que conterá todos os métodos necessários para que as integrações funcionem.
 * Essa DAO <b>NÃO DEVE</b> possuir métodos que servem para uma ou outra integração com empresas específicas.
 * A ideia é que ela possua métodos que o ProLog utiliza para fazer as integrações (no geral) funcionarem do seu lado.
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
     * @param userToken        Token do usuário.
     * @param recursoIntegrado {@link RecursoIntegrado} para verificar se está integrado.
     * @return Identificador único de um {@link Sistema}.
     * @throws Exception Caso aconteça algum erro na consulta ou na execução.
     */
    @Nullable
    SistemaKey getSistemaKey(@NotNull final String userToken,
                             @NotNull final RecursoIntegrado recursoIntegrado) throws Exception;

    /**
     * Esse método retorna o código da unidade utilizado no ERP do cliente equivalente a mesma unidade utilizada
     * no ProLog.
     * <p>
     * Por exemplo, se quisermos saber o código de Santa Maria no ERP da Avilan, basta chamarmos essa função passando
     * o número 3 (código da unidade de Santa Maria no ProLog) e ela irá retornar o código único utilizado pela Avilan
     * para representar Santa Maria em seu ERP.
     *
     * @param codUnidadeProLog Código da {@link Unidade} utilizado no ProLog.
     * @return Código da unidade no ERP do cliente.
     * @throws SQLException Caso aconteça algum erro na consulta.
     */
    @NotNull
    String getCodUnidadeErpClienteByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws SQLException;

    /**
     * Método necessário para buscar o token utilizado para autenticações de requisições em métodos integrados. No banco
     * de dados o token é geral para a empresa.
     * O método executa a busca do token verificando se o {@code codUnidadeProLog} pertence à empresa que possue o token
     * cadastrado no banco de dados.
     *
     * @param codUnidadeProLog Código da {@link Unidade unidade} do ProLog.
     * @return Valor alfanumérico, podendo conter letras e números em um posições aleatórias, mas de tamanho fixo.
     * @throws Throwable Caso ocorra algum problema na busca do token.
     */
    @NotNull
    String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable;
}