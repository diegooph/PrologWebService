package br.com.zalf.prolog.webservice.integracao;

import br.com.zalf.prolog.webservice.gente.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.geral.unidade._model.Unidade;
import br.com.zalf.prolog.webservice.integracao.agendador.os._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.checklist.ModelosChecklistBloqueados;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;

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
     * Método necessário para buscar o token utilizado para autenticações de requisições em métodos integrados. No banco
     * de dados o token é geral para a empresa.
     * O método executa a busca do token com base no {@code codUnidadeProLog} fornecido.
     *
     * @param codUnidadeProLog Código da {@link Unidade unidade} do ProLog.
     * @return Valor alfanumérico, podendo conter letras e números em posições aleatórias, mas de tamanho fixo.
     * @throws Throwable Caso ocorra algum problema na busca do token.
     */
    @NotNull
    String getTokenIntegracaoByCodUnidadeProLog(@NotNull final Long codUnidadeProLog) throws Throwable;

    /**
     * Busca o código da empresa vinculado ao {@code tokenIntegracao} fornecido.
     *
     * @param conn            Connection que será utilizada na requisição.
     * @param tokenIntegracao Um token de integração.
     * @return O código da empresa.
     * @throws Throwable Caso ocorra algum problema na busca do token.
     */
    @NotNull
    Long getCodEmpresaByTokenIntegracao(@NotNull final Connection conn,
                                        @NotNull final String tokenIntegracao) throws Throwable;

    /**
     * Este método busca o código da empresa a partir de um código de unidade do ProLog.
     *
     * @param conn             Conexão que será utilizada para buscar os dados.
     * @param codUnidadeProLog Código da Unidade ProLog que será utilizada para buscar o código da empresa.
     * @return Código da empresa a qual a unidade ProLog pertence.
     * @throws Throwable Caso ocorra algum problema na busca do código da empresa.
     */
    @NotNull
    Long getCodEmpresaByCodUnidadeProLog(@NotNull final Connection conn,
                                         @NotNull final Long codUnidadeProLog) throws Throwable;

    /**
     * Método utilizado para buscar a URL para qual a integração deverá se comunicar. A URL é completa, contendo a
     * <code>baseUrl</code> e também o <code>path</code> do endpoint que a integração irá se comunicar.
     * <p>
     * Para identificar a URL correta, utilizamos o {@code codEmpresa} e também o {@code sistemaKey}, contendo a
     * chave do sistema integrado, e o {@code metodoIntegrado} identificando para qual método será utilizada a URL.
     *
     * @param conn            Conexão que será utilizada para buscar os dados.
     * @param codEmpresa      Código da empresa integrada que iremos buscar o método.
     * @param sistemaKey      Chave do Sistema que a empresa utiliza.
     * @param metodoIntegrado Metodo que irá utilizar a URL.
     * @return Uma String contendo o URL completa do endpoint onde a integração irá comunicar.
     * @throws Throwable Se algum erro acontecer na busca da URL.
     */
    @NotNull
    String getUrl(@NotNull final Connection conn,
                  @NotNull final Long codEmpresa,
                  @NotNull final SistemaKey sistemaKey,
                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    /**
     * Método utilizado para buscar a URL para qual a integração deverá se comunicar. A URL é completa, contendo a
     * <code>baseUrl</code> e também o <code>path</code> do endpoint que a integração irá se comunicar.
     * <p>
     * Para identificar a URL correta, utilizamos o {@code codEmpresa} e também o {@code sistemaKey}, contendo a
     * chave do sistema integrado, e o {@code metodoIntegrado} identificando para qual método será utilizada a URL.
     *
     * @param codEmpresa      Código da empresa integrada que iremos buscar o método.
     * @param sistemaKey      Chave do Sistema que a empresa utiliza.
     * @param metodoIntegrado Metodo que irá utilizar a URL.
     * @return Uma String contendo o URL completa do endpoint onde a integração irá comunicar.
     * @throws Throwable Se algum erro acontecer na busca da URL.
     */
    @NotNull
    String getUrl(@NotNull final Long codEmpresa,
                  @NotNull final SistemaKey sistemaKey,
                  @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    /**
     * Método responsável por retornar o Código Auxiliar mapeado para o código de Unidade Prolog.
     *
     * @param conn             Conexão com o banco de dados que será utilizada para buscar os dados.
     * @param codUnidadeProlog Código da Unidade Prolog para buscar o código Auxiliar.
     * @return String contendo o Código Auxiliar da Unidade Prolog.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    String getCodAuxiliarByCodUnidadeProlog(@NotNull final Connection conn,
                                            @NotNull final Long codUnidadeProlog) throws Throwable;

    /**
     * Holder contendo as informações necessárias para autenticação de requisições.
     *
     * @param conn            Conexão que será utilizada para buscar os dados.
     * @param codEmpresa      Código da empresa integrada que iremos buscar as informações para autenticar.
     * @param sistemaKey      Chave do Sistema que a empresa utiliza.
     * @param metodoIntegrado Metodo que será utilizado.
     * @return {@link ApiAutenticacaoHolder Objeto} contendo as informações a serem utilizadas para autenticar a
     * requisição.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    ApiAutenticacaoHolder getApiAutenticacaoHolder(@NotNull final Connection conn,
                                                   @NotNull final Long codEmpresa,
                                                   @NotNull final SistemaKey sistemaKey,
                                                   @NotNull final MetodoIntegrado metodoIntegrado) throws Throwable;

    /**
     * Método utilizado para buscar as unidades da empresa do colaborador que possuem a integração bloqueada. O método
     * considera apenas unidades bloqueadas aquelas que estão associadas ao {@code sistemaKey} e ao
     * {@code recursoIntegrado} especificados.
     *
     * @param userToken        Token do usuário, utilizado para saber a qual empresa ele pertence.
     * @param sistemaKey       Chave do sistema para buscar as unidades bloqueadas.
     * @param recursoIntegrado Recurso integrado para buscar as unidades bloqueadas.
     * @return Uma lista de unidades bloqueadas.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueada(@NotNull final String userToken,
                                                 @NotNull final SistemaKey sistemaKey,
                                                 @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable;

    /**
     * Método utilizado para buscar as unidades da empresa que possuem a integração bloqueada. O método
     * considera apenas unidades bloqueadas aquelas que estão associadas ao {@code sistemaKey} e ao
     * {@code recursoIntegrado} especificados.
     * O método utiliza o {@code tokenIntegracao} para retornar as unidades bloqueadas.
     *
     * @param tokenIntegracao  Token da integração, utilizado para saber a qual empresa ele pertence.
     * @param sistemaKey       Chave do sistema para buscar as unidades bloqueadas.
     * @param recursoIntegrado Recurso integrado para buscar as unidades bloqueadas.
     * @return Uma lista de unidades bloqueadas.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    List<Long> getCodUnidadesIntegracaoBloqueadaByTokenIntegracao(
            @NotNull final String tokenIntegracao,
            @NotNull final SistemaKey sistemaKey,
            @NotNull final RecursoIntegrado recursoIntegrado) throws Throwable;

    /**
     * Método utilizado para verificar se uma unidade possui configuração para abrir serviço para pneus.
     *
     * @param codUnidade código da unidade.
     * @return Uma flag para identificar se deve abrir serviço para os pneus.
     * @throws Throwable Se algum erro acontecer.
     */
    boolean getConfigAberturaServicoPneuIntegracao(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Método utilizado para buscar os códigos de modelos de checklist que estão bloqueados para integração.
     *
     * @param codUnidade Código da unidade.
     * @return Um objeto contendo a lista dos códigos de modelo bloqueados para uma unidade..
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    ModelosChecklistBloqueados getModelosChecklistBloqueados(@NotNull final Long codUnidade) throws Throwable;

    /**
     * Método para inserir uma O.S na tabela de pendencia sicronia, com a finalidade de realizar a sincronia
     * com um sistema terceiro.
     *
     * @param codUnidade código da unidade da ordem ser serviço a sicronizar.
     * @param codOs      um código de ordem de serviço a sincronizar.
     * @return Um código interno de OS do prolog.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    Long insertOsPendente(@NotNull final Long codUnidade, @NotNull final Long codOs) throws Throwable;

    /**
     * Método com a responsabilidade de buscar todas as informações pertinentes à integração de uma OS baseado
     * em um código.
     *
     * @param codOs um código de ordem de serviço a ser buscada.
     * @return Um objeto complexo contendo as informações da OS.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    OsIntegracao getOsIntegracaoByCod(@NotNull final Long codOs) throws Throwable;

    /**
     * Busca todas as ordens de serviço que estão pendentes de sincronização e não estão marcadas como bloqueadas.
     *
     * @return Uma lista de códigos prolog de ordem de serviço.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    @NotNull
    List<Long> buscaCodOrdensServicoPendenteSincronizacao() throws Throwable;

    /**
     * Método com a responsabilidade de atualizar o status de uma O.S integrada.
     *
     * @param codInternoOsProlog    um código interno de ordem de serviço a ser buscada.
     * @param pendente              indica se a O.S deve ser atualizada como pendente.
     * @param bloqueada             indica se a O.S deve ser setada como bloqueada.
     * @param incrementarTentativas indica se deve incrementar a quantidade de tentativas na O.S.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    void atualizaStatusOsIntegrada(@NotNull final Long codInternoOsProlog,
                                   final boolean pendente,
                                   final boolean bloqueada,
                                   final boolean incrementarTentativas) throws Throwable;

    /**
     * Método com a responsabilidade de atualizar a O.S como pendente, logar mensagem de erro e incrementar
     * a exception.
     *
     * @param codInternoOsProlog um código interno de ordem de serviço a ser buscada.
     * @param errorMessage       uma mensagem de erro que será gravada no banco de dados.
     * @throws Throwable Se qualquer erro ocorrer.
     */
    void logarStatusOsComErro(@NotNull final Long codInternoOsProlog,
                              @Nullable final String errorMessage) throws Throwable;

}