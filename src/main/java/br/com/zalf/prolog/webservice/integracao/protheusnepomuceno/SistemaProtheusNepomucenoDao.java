package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosTipoVeiculoConfiguracaoAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosUnidadeRestricao;
import org.glassfish.jersey.internal.guava.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public interface SistemaProtheusNepomucenoDao {
    /**
     * Dado uma lista de códigos de unidades, este método retorna os valores da lista que possuem cod_auxiliar mapeados
     * banco de dados. Caso nenhum código de unidade possuir cod_auxiliar, uma lista vazia será retornada.
     *
     * @param conn        Conexão para buscar os dados no banco de dados.
     * @param codUnidades Lista de códigos de unidades para verificar.
     * @return Uma lista contendo os códigos de unidades que possuem cod_auxiliar. A lista estará vazia caso nenhum dos
     * códigos de unidades possuir cod_auxiliar.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    List<Long> getApenasUnidadesMapeadas(@NotNull final Connection conn,
                                         @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método utilizado para inserir uma aferição e os valores no banco de dados.
     *
     * @param conn               Conexão que será utilizada para inserir a aferição.
     * @param codUnidade         Código da unidade onde a Aferição foi realizada.
     * @param codAuxiliarUnidade Código Auxiliar da unidade onde a Aferição foi realizada.
     * @param afericao           Objeto contendo as medidas capturadas no processo de aferição.
     * @return Código da aferição inserida.
     * @throws Throwable Se ocorrer erro na inserção.
     */
    @NotNull
    Long insert(@NotNull final Connection conn,
                @NotNull final Long codUnidade,
                @NotNull final String codAuxiliarUnidade,
                @NotNull final Afericao afericao) throws Throwable;

    /**
     * Busca as possíveis informações de aferições integradas de acordo com a lista de pneus e unidade.
     *
     * @param conn       Conexão que será utilizada para buscar as informações de aferições.
     * @param codUnidade Código da unidade base para buscar as informações.
     * @param codPneus   Lista de códigos de pneus buscado do cliente.
     * @return Lista de registros de aferições integradas.
     * @throws Throwable Se ocorrer algum erro na busca.
     */
    @NotNull
    List<InfosAfericaoAvulsa> getInfosAfericaoAvulsa(@NotNull final Connection conn,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final List<String> codPneus) throws Throwable;

    /**
     * Método responsável por buscar as informações de Restrição para cada unidade Prolog. As restrições serão buscadas
     * diretamente da base do Prolog de acordo com a parametrização feita para cada Unidade Prolog.
     *
     * @param conn        Conexão utilizada para buscar as informações.
     * @param codUnidades Lista de códigos de Unidades Prolog utilizadas como base para a busca.
     * @return Uma estrutura de chave e valor onde a chave é o código auxiliar da unidade (presente no DE-PARA) e o
     * valor é uma objeto contendo as informações de restrição para a chave específica.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Map<String, InfosUnidadeRestricao> getInfosUnidadeRestricao(@NotNull final Connection conn,
                                                                @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método utilizado para buscar as informações de configurações de aferições para os tipos de veículos da Empresa.
     * Utilizamos as Unidades Prolog como base para as buscas das informações.
     *
     * @param conn        Conexão que será utilizada para buscara as informações.
     * @param codUnidades Códigos das Unidades Prolog que serão utilizadas como base para as buscas.
     * @return Uma estrutura de chave e valor contendo duas chaves, onde a primeira chave é o código auxiliar da unidade
     * e a segunda chave é o código auxiliar do tipo de veículo (presente no DE-PARA).
     * e os valores são as configurações de aferição para cada tipo de veículo.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Table<String, String, InfosTipoVeiculoConfiguracaoAfericao> getInfosTipoVeiculoConfiguracaoAfericao(
            @NotNull final Connection conn,
            @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método utilizado para buscar as informações das aferições realizadas nas placas. Utilizamos como base das buscas
     * as placas.
     *
     * @param conn             Conexão que será utilizada para buscar as informações.
     * @param codEmpresa       Código da empresa a qual iremos buscar os dados das aferições realizadas.
     * @param placasNepomuceno Placas que iremos buscar as informações das aferições realizadas.
     * @return Uma estrutura de chave e valor onde a chave é a placa e o valor é um objeto contendo as informações da
     * última vez que essa placa foi aferida.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Map<String, InfosAfericaoRealizadaPlaca> getInfosAfericaoRealizadaPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<String> placasNepomuceno) throws Throwable;

    /**
     * Método utilizado para buscar as configurações necessárias para realizar uma nova aferição de placa.
     *
     * @param conn                Conexão que eserá utilizada pra buscar os dados.
     * @param codUnidade          Código da Unidade Prolog que será utilizada como base para buscar os dados.
     * @param codEstruturaVeiculo Código Auxiliar do tipo de veículo. Será utilizado para indetificar de qual tipo de
     *                            veículo iremos buscar as configurações para aferir.
     * @return Um objeto contendo as configurações para realização de uma nova aferição de placa.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    ConfiguracaoNovaAfericaoPlaca getConfigNovaAfericaoPlaca(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final String codEstruturaVeiculo) throws Throwable;

    /**
     * Método utilizado para buscar as configurações necessárias para realizar uma nova aferição avulsa.
     *
     * @param conn       Conexão que eserá utilizada pra buscar os dados.
     * @param codUnidade Código da Unidade Prolog que será utilizada como base para buscar os dados.
     * @return Um objeto contendo as configurações para realização de uma nova aferição avulsa.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    ConfiguracaoNovaAfericaoAvulsa getConfigNovaAfericaoAvulsa(@NotNull final Connection conn,
                                                               @NotNull final Long codUnidade) throws Throwable;

    /**
     * Método utilizado para buscar o mapeamento de posições do Prolog. Temos uma tabela onde salvamos a nomenclatura
     * que o cliente utiliza para cada posição do Prolog, utilizamos essa estrutura aqui como um DE-PARA de informações.
     *
     * @param conn                Conexão que será utilizada para buscar o mapeamento de posições.
     * @param codEmpresa          Código da empresa que buscaremos o mapeamento.
     * @param codEstruturaVeiculo Código de qual estrutura buscaremos as posições.
     * @return Uma estrutura de chave e valor, onde a chave é nomenclatura cadastrada e o valor é a posição do Prolog.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Map<String, Integer> getMapeamentoPosicoesProlog(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final String codEstruturaVeiculo) throws Throwable;

    /**
     * Método utilizado para buscar o código de diagrama Prolog dado um código auxiliar de tipo de veículo.
     *
     * @param conn                Conexão que será utilizada para buscar a informação.
     * @param codEmpresa          Código da empresa a qual o diagrama será buscado.
     * @param codEstruturaVeiculo Código auxiliar do tipo de veículo, utilizado como base para identificar o diagrama.
     * @return O código do diagrama mapeado para o tipo de veículo.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Short getCodDiagramaByCodEstrutura(@NotNull final Connection conn,
                                       @NotNull final Long codEmpresa,
                                       @NotNull final String codEstruturaVeiculo) throws Throwable;

    /**
     * Método utilizado para buscar os códigos das Filiais mapeadas no Prolog. Para realizar a busca utilizamos como
     * base os códigos de unidades Prolog.
     *
     * @param conn        Conexão utilizada para buscar as informações.
     * @param codUnidades Lista de códigos de Unidades do Prolog para utilizar como base na busca.
     * @return Um map contendo o código da unidade Prolog e código da filial associada.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    Map<Long, String> getCodFiliais(@NotNull final Connection conn,
                                    @NotNull final List<Long> codUnidades) throws Throwable;

    /**
     * Método utilizado para buscar os códigos auxiliares mapeados. Esse método não retorna, caso exisitir, o código
     * auxiliar do próprio código de tipo de veículo sendo filtrado.
     *
     * @param codEmpresaTipoVeiculo Código da empresa onde o tipo de veículo pertence. Null caso o tipo de veículo
     *                              estiver sendo atualizado. Para esse cenário, o {@code codTipoVeiculo} não será null.
     * @param codTipoVeiculo        Código do tipo de veículo. Null caso o tipo de veículo ainda não estiver
     *                              cadastrado.
     * @return Lista contendo os códigos auxiliares mapeados, exceto o codigo auxiliar do tipo de veículo filtrado.
     * @throws Throwable Se algum erro acontecer.
     */
    @NotNull
    List<String> verificaCodAuxiliarTipoVeiculoValido(@Nullable final Long codEmpresaTipoVeiculo,
                                                      @Nullable final Long codTipoVeiculo) throws Throwable;
}
