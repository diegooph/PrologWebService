package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.integracao.praxio.afericao.MedicaoIntegracaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoCadastroPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoEdicaoPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.cadastro.VeiculoTransferenciaPraxio;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ChecklistParaSincronizar;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemOSAbertaGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.ItemResolvidoGlobus;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.OrdemServicoAbertaGlobus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 12/12/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
interface IntegracaoPraxioDao {

    /**
     * Método utilizado para inserir, no banco de dados do ProLog, um veículo cadastrado no Sistema Globus da Praxio.
     * Este método será utilizado pelo sistema Globus sempre que um novo cadastro de veículo ocorrer.
     * As informações repassadas para o ProLog serão inseridas no banco de dados e os vínculos criados de acordo com a
     * lógica do ProLog.
     *
     * @param tokenIntegracao       Token utilizado para autenticar a empresa que realizou a requisição.
     * @param veiculoCadastroPraxio Objeto que contém as informações que o Globus repassou ao ProLog sobre o veículo
     *                              cadastrado.
     * @throws Throwable Se algum erro ocorrer.
     */
    void inserirVeiculoCadastroPraxio(@NotNull final String tokenIntegracao,
                                      @NotNull final VeiculoCadastroPraxio veiculoCadastroPraxio) throws Throwable;

    /**
     * Método utilizado para alterar informações de um veículo. Este método é utilizando sempre que um veículo sofrer
     * alterações no sistema Globus.
     * As informações recebidas serão validadas e inseridas no Banco de dados, refletindo as mudanças executadas no
     * Globus.
     * <p>
     * O ProLog possui uma limitação onde não é possível a alteraçãa da Placa, porém, o Globus permite. Enquanto o
     * ProLog ainda conter essa limitação, sempre que identificado uma mudança de placa, iremos retornar um erro para o
     * usuário, dizendo que não é possível realizar a operação no ProLog.
     *
     * @param tokenIntegracao              Token utilizado para autenticar a empresa que realizou a requisição.
     * @param codUnidadeVeiculoAntesEdicao Código da Unidade antes do veículo ser alterado.
     * @param placaVeiculoAntesEdicao      Placa do veículo antes de ser alterado.
     * @param veiculoEdicaoPraxio          Objeto contendo as novas informações do veículo.
     * @throws Throwable Se algum erro acontecer.
     */
    void atualizarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                @NotNull final Long codUnidadeVeiculoAntesEdicao,
                                @NotNull final String placaVeiculoAntesEdicao,
                                @NotNull final VeiculoEdicaoPraxio veiculoEdicaoPraxio) throws Throwable;

    /**
     * Método utilizado para transferir um veículo entre Unidades a partir da integração. Por limitações do Sistema
     * Globus, o processo de transferência de veículo não irá contér mais de um veículo por vez.
     * <p>
     * Para não gerar inconsistências com a integração de Ordem de Serviço, a Transferência de Veículo a partir da
     * integração não deverá fechar as Ordens de Serviços Abertas.
     *
     * @param tokenIntegracao            Token utilizado para autenticar a empresa que realizou a requisição.
     * @param veiculoTransferenciaPraxio Objeto contendo as informações do veículo que será transferido entre unidades.
     * @throws Throwable Se algum erro acontecer.
     */
    void transferirVeiculoPraxio(@NotNull final String tokenIntegracao,
                                 @NotNull final VeiculoTransferenciaPraxio veiculoTransferenciaPraxio) throws Throwable;

    /**
     * Método utilizado para Ativar ou Desetivar um veículo a partir da integrtação com o Sistema Globus.
     *
     * @param tokenIntegracao Token utilizado para autenticar a empresa que realizou a requisição.
     * @param placaVeiculo    Placa do veículo que será atualizado.
     * @param veiculoAtivo    Valor booleano que diz se o veículo será Ativado (<code>veiculoAtivo = true</code>) ou se
     *                        o veículo será desativado (<code>veiculoAtivo = false</code>).
     * @throws Throwable Se algum erro acontecer.
     */
    void ativarDesativarVeiculoPraxio(@NotNull final String tokenIntegracao,
                                      @NotNull final String placaVeiculo,
                                      @NotNull final Boolean veiculoAtivo) throws Throwable;

    /**
     * Este método busca as {@link MedicaoIntegracaoPraxio aferições} a partir
     * do {@code codUltimaAfericao} recebido.
     * O código da Última Aferição Sincronizada é utilizado como um Offset de busca,
     * todas as aferições a partir deste código serão retornadas por este método.
     *
     * @param tokenIntegracao   Token utilizado para a requisição. Este token será utilizado para
     *                          descobrir qual empresa está requisitando as informações.
     * @param codUltimaAfericao Código da Última aferição sincronizada.
     * @return Uma lista de {@link List<MedicaoIntegracaoPraxio> aferições} não sincronizadas.
     * @throws Throwable Se algum erro ocorrer durante a busca das novas aferições.
     */
    @NotNull
    List<MedicaoIntegracaoPraxio> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                         @NotNull final Long codUltimaAfericao) throws Throwable;

    /**
     * Método utilizado para inserir uma série de {@link OrdemServicoAbertaGlobus Ordens de Serviço Abertas} do Sistema
     * Globus no ProLog.
     * <p>
     * Esse método deverá receber apenas Ordens de Serviço Abertas e apenas
     * {@link ItemOSAbertaGlobus Itens de O.S. Pendentes}. Itens já resolvidos não devem ser enviados nesta lista.
     * <p>
     * Caso a Ordem de Serviço já exista, não será criada uma nova. Os itens presentes na Segunda O.S. (O.S. recebida)
     * serão adicionados na O.S. que já existe, desde que esta O.S. não esteja finalizada.
     *
     * @param tokenIntegracao      Token utilizado para autenticar o Sistema que deseja realizar a operação.
     * @param ordensServicoAbertas Lista de informações que serão inseridas no Banco de Dados.
     * @throws Throwable Se algum erro ocorrer no processamento das informações.
     */
    void inserirOrdensServicoGlobus(
            @NotNull final String tokenIntegracao,
            @NotNull final List<OrdemServicoAbertaGlobus> ordensServicoAbertas) throws Throwable;

    /**
     * Método utilizado para inserir, no ProLog, as informações dos {@link ItemResolvidoGlobus Itens} resolvidos no
     * Sistema Globus.
     * <p>
     * Esse método deverá receber apenas {@link ItemResolvidoGlobus Itens de O.S Resolvidos}. Para tratar de itens
     * pendetes deve-se utilizar outro método, {@link IntegracaoPraxioDao#inserirOrdensServicoGlobus(String, List);}
     * <p>
     * Esse método, além de inserir as informações dos itens resolvidos, irá marcar a Ordem de Serviço como
     * <i>FECHADA</i> caso todos os itens dela estiverem <i>RESOLVIDOS</i>.
     *
     * @param tokenIntegracao Token utilizado para autenticar o Sistema que deseja realizar a operação.
     * @param itensResolvidos Lista de itens que foram resolvidos no sistema Globus.
     * @throws Throwable Se algum erro ocorrer no processamento das informações.
     */
    void resolverMultiplosItens(@NotNull final String tokenIntegracao,
                                @NotNull final List<ItemResolvidoGlobus> itensResolvidos) throws Throwable;

    /**
     * Método utilizado para buscar o código do checklist que deve ser sincronizado no momento.
     * <p>
     * Se não tem nenhum checklist para ser sincronizado {@link ChecklistParaSincronizar#codChecklist} é 0 (ZERO).
     * <p>
     * A propriedade {@link ChecklistParaSincronizar#isLastCod} indica se este é o último código da lista.
     *
     * @return Um {@link ChecklistParaSincronizar objeto} onde contendo as informações do checklist para sincronizar.
     * @throws Throwable Se algum erro ocorrer.
     */
    @NotNull
    ChecklistParaSincronizar getCodChecklistParaSincronizar() throws Throwable;

    VeiculoEdicaoStatus getVeiculoEdicaoStatus(@NotNull final String placaVeiculo, final Boolean veiculoAtivo) throws Throwable;
}
