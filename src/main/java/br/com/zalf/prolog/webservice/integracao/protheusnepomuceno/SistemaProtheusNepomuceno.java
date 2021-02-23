package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.errorhandling.ErrorReportSystem;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.TipoVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.AfericaoRealizadaPlaca;
import br.com.zalf.prolog.webservice.integracao.integrador._model.TipoVeiculoConfigAfericao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.UnidadeRestricao;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.*;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.error.ProtheusNepomucenoException;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.data.ProtheusNepomucenoRequesterImpl;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoConverter;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoEncoderDecoder;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.ProtheusNepomucenoUtils;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 3/10/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomuceno extends Sistema {
    @NotNull
    private static final String TAG = SistemaProtheusNepomuceno.class.getSimpleName();
    @NotNull
    private final ProtheusNepomucenoRequesterImpl requester;
    @NotNull
    private final IntegracaoDao integracaoDao;

    public SistemaProtheusNepomuceno(@NotNull final ProtheusNepomucenoRequesterImpl requester,
                                     @NotNull final SistemaKey sistemaKey,
                                     @NotNull final RecursoIntegrado recursoIntegrado,
                                     @NotNull final IntegradorProLog integradorProLog,
                                     @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, recursoIntegrado, userToken);
        this.integracaoDao = Injection.provideIntegracaoDao();
        this.requester = requester;
    }

    @Override
    @NotNull
    public CronogramaAfericao getCronogramaAfericao(@NotNull final List<Long> codUnidades) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final SistemaProtheusNepomucenoDao sistema = new SistemaProtheusNepomucenoDaoImpl();
            // Podemos ter unidades cadastradas no Prolog que não tem cod_auxiliar, removemos esses casos.
            final List<Long> codUnidadesMapeadas = sistema.getApenasUnidadesMapeadas(conn, codUnidades);
            if (codUnidadesMapeadas.isEmpty()) {
                // Se, das unidades filtradas, nenhuma tiver cod_auxiliar mapeado, retornamos um Cronograma Vazio.
                // Fazemos isso para não mostrar ao usuário uma tela de erro sempre que ele entrar no Cronograma.
                return ProtheusNepomucenoConverter.createEmptyCronogramaAfericaoProlog();
            }
            // Podemos, com toda certeza, utilizar codUnidades.get(0) pois no mínimo teremos uma unidade nesta lista.
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidades.get(0));

            final Map<String, UnidadeRestricao> unidadeRestricao =
                    integracaoDao.getUnidadeRestricaoHolder(conn, codUnidadesMapeadas).getUnidadeRestricao();
            // Apenas tipos de veículos que possuem cod_auxiliar estarão nessa estrutura.
            final Table<String, String, TipoVeiculoConfigAfericao> tipoVeiculoConfiguracao =
                    integracaoDao
                            .getTipoVeiculoConfigAfericaoHolder(conn, codUnidadesMapeadas)
                            .getTipoVeiculoConfiguracao();

            final List<VeiculoListagemProtheusNepomuceno> listagemVeiculos =
                    internalGetVeiculos(conn, codEmpresa, codUnidadesMapeadas, sistema);
            listagemVeiculos.removeIf(VeiculoListagemProtheusNepomuceno::deveRemover);

            final List<String> placasNepomuceno = listagemVeiculos.stream()
                    .map(VeiculoListagemProtheusNepomuceno::getCodVeiculo)
                    .distinct()
                    .collect(Collectors.toList());

            final Map<String, AfericaoRealizadaPlaca> afericaoRealizadaPlaca =
                    integracaoDao
                            .getAfericaoRealizadaPlacaHolder(conn, codEmpresa, placasNepomuceno)
                            .getAfericaoRealizadaPlaca();

            // Aqui começamos a montar o cronograma
            final Map<String, ModeloPlacasAfericao> modelosEstruturaVeiculo = new HashMap<>();
            final Map<String, List<ModeloPlacasAfericao.PlacaAfericao>> placasEstruturaVeiculo = new HashMap<>();
            final Set<String> estruturasNaoMapeadas = new HashSet<>();
            for (final VeiculoListagemProtheusNepomuceno veiculo : listagemVeiculos) {
                if (!tipoVeiculoConfiguracao.contains(
                        veiculo.getCodEmpresaFilialVeiculo(),
                        veiculo.getCodEstruturaVeiculo())) {
                    // Adicionamos a estrutura não mapeada em uma estrutura para logar no sentry.
                    estruturasNaoMapeadas.add(veiculo.getCodEstruturaVeiculo());
                    continue;
                }

                if (!placasEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    placasEstruturaVeiculo.put(veiculo.getCodModeloVeiculo(), new ArrayList<>());
                }
                placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo()).add(
                        ProtheusNepomucenoConverter.createPlacaAfericaoProlog(
                                veiculo,
                                unidadeRestricao.get(veiculo.getCodEmpresaFilialVeiculo()),
                                tipoVeiculoConfiguracao.get(
                                        veiculo.getCodEmpresaFilialVeiculo(),
                                        veiculo.getCodEstruturaVeiculo()),
                                afericaoRealizadaPlaca.get(veiculo.getCodVeiculo())));

                if (!modelosEstruturaVeiculo.containsKey(veiculo.getCodModeloVeiculo())) {
                    modelosEstruturaVeiculo.put(
                            veiculo.getCodModeloVeiculo(),
                            ProtheusNepomucenoConverter.createModeloPlacasAfericaoProlog(
                                    veiculo,
                                    placasEstruturaVeiculo.get(veiculo.getCodModeloVeiculo())));
                }
            }

            // Caso identificarmos alguma estrutura que não está mapeada no Prolog, vamos logar no sistema de erros, sem
            // quebrar a aplicação. Assim deixamos o usuário aferir o que é mostrado, enquanto os erros de mapeamento
            // são corrigidos via sistema.
            if (!estruturasNaoMapeadas.isEmpty()) {
                logEstruturasNaoMapeadas(estruturasNaoMapeadas);
            }

            return ProtheusNepomucenoConverter.createCronogramaAfericaoProlog(modelosEstruturaVeiculo);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public NovaAfericaoPlaca getNovaAfericaoPlaca(@NotNull final Long codUnidade,
                                                  @NotNull final String placaVeiculo,
                                                  @NotNull final String tipoAfericao) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final SistemaProtheusNepomucenoDao sistema = new SistemaProtheusNepomucenoDaoImpl();
            final Long codEmpresa = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            String codEmpresaFilial = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            if (ProtheusNepomucenoUtils.containsMoreThanOneCodAuxiliar(codEmpresaFilial)) {
                codEmpresaFilial = getCodFilialByPlacaCronograma(conn, codEmpresa, codUnidade, placaVeiculo, sistema);
            }

            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           codEmpresa,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
            final VeiculoAfericaoProtheusNepomuceno veiculoAfericao =
                    requester.getPlacaPneusAfericaoPlaca(apiAutenticacaoHolder, codEmpresaFilial, placaVeiculo);

            final ConfiguracaoNovaAfericaoPlaca configuracaoAfericao =
                    integracaoDao.getConfigNovaAfericaoPlaca(conn,
                                                             codUnidade,
                                                             veiculoAfericao.getCodEstruturaVeiculo());
            final Short codDiagramaProlog =
                    integracaoDao.getCodDiagramaByDeParaTipoVeiculo(conn,
                                                                    codEmpresa,
                                                                    veiculoAfericao.getCodEstruturaVeiculo());
            if (codDiagramaProlog <= 0) {
                throw new ProtheusNepomucenoException(
                        "Identificamos aque a estrutura (" + veiculoAfericao.getCodEstruturaVeiculo() + ") " +
                                "não está configurada no Prolog.\n" +
                                "Por favor, solicite que esta esta estrutura seja cadastrada no Prolog para " +
                                "realizar a aferição.");
            }
            final ProtheusNepomucenoPosicaoPneuMapper posicaoPneuMapper = new ProtheusNepomucenoPosicaoPneuMapper(
                    veiculoAfericao.getCodEstruturaVeiculo(),
                    sistema.getMapeamentoPosicoesProlog(conn, codEmpresa, veiculoAfericao.getCodEstruturaVeiculo()));

            // Garantimos, antes de criar a nova aferição, que todas as posições estão mapeadas. Caso não estiverem,
            // estouramos uma exception mostrando as posições não mapeadas.
            ProtheusNepomucenoUtils
                    .validatePosicoesMapeadasVeiculo(
                            veiculoAfericao.getCodEstruturaVeiculo(),
                            veiculoAfericao.getPosicoesPneusAplicados(),
                            posicaoPneuMapper);

            final Veiculo veiculo =
                    ProtheusNepomucenoConverter
                            .createVeiculoProlog(
                                    codUnidade,
                                    codDiagramaProlog,
                                    veiculoAfericao,
                                    posicaoPneuMapper);
            return ProtheusNepomucenoConverter.createNovaAfericaoPlacaProlog(veiculo, configuracaoAfericao);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public List<PneuAfericaoAvulsa> getPneusAfericaoAvulsa(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus.
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           codEmpresaProlog,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA);
            final List<PneuEstoqueProtheusNepomuceno> pneusEstoqueNepomuceno =
                    requester.getListagemPneusEmEstoque(apiAutenticacaoHolder, codAuxiliarUnidade);

            final List<String> codPneus = pneusEstoqueNepomuceno.stream()
                    .map(PneuEstoqueProtheusNepomuceno::getCodigoCliente)
                    .collect(Collectors.toList());

            // Busca as infos de aferição com base nos pneus da lista codPneus.
            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa =
                    sistema.getInfosAfericaoAvulsa(conn, codUnidade, codPneus);

            final List<PneuAfericaoAvulsa> pneusAfericaoAvulsa = new ArrayList<>();
            for (final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno : pneusEstoqueNepomuceno) {
                final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa = infosAfericaoAvulsa.stream()
                        .filter(infoPneu ->
                                        infoPneu.getCodPneuCliente().equals(pneuEstoqueNepomuceno.getCodigoCliente()))
                        .findFirst()
                        .orElse(null);
                pneusAfericaoAvulsa.add(
                        ProtheusNepomucenoConverter
                                .createPneuAfericaoAvulsaProlog(
                                        codUnidade,
                                        pneuEstoqueNepomuceno,
                                        pneuInfoAfericaoAvulsa));
            }
            return pneusAfericaoAvulsa;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final TipoMedicaoColetadaAfericao tipoMedicaoColetadaAfericao) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            final SistemaProtheusNepomucenoDaoImpl sistema = new SistemaProtheusNepomucenoDaoImpl();
            final String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);

            // Busca a lista de pneus em estoque do Protheus.
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           codEmpresaProlog,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEU_NOVA_AFERICAO_AVULSA);
            final PneuEstoqueProtheusNepomuceno pneuEstoqueNepomuceno =
                    requester.getPneuEmEstoqueAfericaoAvulsa(
                            apiAutenticacaoHolder,
                            codAuxiliarUnidade,
                            ProtheusNepomucenoEncoderDecoder.decode(codPneu));

            // Busca as infos de aferição com base nos pneus da lista codPneus.
            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa =
                    sistema.getInfosAfericaoAvulsa(
                            conn,
                            codUnidade,
                            Collections.singletonList(pneuEstoqueNepomuceno.getCodigoCliente()));

            final InfosAfericaoAvulsa pneuInfoAfericaoAvulsa = infosAfericaoAvulsa.stream()
                    .filter(infoPneu ->
                                    infoPneu.getCodPneuCliente().equals(pneuEstoqueNepomuceno.getCodigoCliente()))
                    .findFirst()
                    .orElse(null);

            final ConfiguracaoNovaAfericaoAvulsa configuracaoAfericao =
                    sistema.getConfigNovaAfericaoAvulsa(conn, codUnidade);

            return ProtheusNepomucenoConverter
                    .createNovaAfericaoAvulsaProlog(
                            codUnidade,
                            pneuEstoqueNepomuceno,
                            configuracaoAfericao,
                            pneuInfoAfericaoAvulsa);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @Override
    @NotNull
    public Long insertAfericao(@NotNull final Long codUnidade,
                               @NotNull final Afericao afericao,
                               final boolean deveAbrirServico) throws Throwable {
        Connection conn = null;
        final DatabaseConnectionProvider connectionProvider = new DatabaseConnectionProvider();
        try {
            conn = connectionProvider.provideDatabaseConnection();
            conn.setAutoCommit(false);
            // buscamos as informações logo no começo do processo, assim, se der erro nada mais é executado.
            final SistemaProtheusNepomucenoDao sistema = new SistemaProtheusNepomucenoDaoImpl();
            final Long codEmpresaProlog = getIntegradorProLog().getCodEmpresaByCodUnidadeProLog(conn, codUnidade);
            final ZoneId zoneIdForCodUnidade = TimeZoneManager.getZoneIdForCodUnidade(codUnidade, conn);
            String codAuxiliarUnidade = getIntegradorProLog().getCodAuxiliarByCodUnidadeProlog(conn, codUnidade);

            // Não precisamos fazer essa tratativa na Aferição Avulsa.
            if (afericao instanceof AfericaoPlaca
                    && ProtheusNepomucenoUtils.containsMoreThanOneCodAuxiliar(codAuxiliarUnidade)) {
                codAuxiliarUnidade =
                        getCodFilialByPlacaCronograma(
                                conn,
                                codEmpresaProlog,
                                codUnidade,
                                ((AfericaoPlaca) afericao).getVeiculo().getPlaca(),
                                sistema);
            }

            // Deixamos para inserir a aferição no Prolog logo antes de enviar para o Protheus. Assim garantimos que
            // só teremos um rollback caso tenhamos erro no Protheus.
            final Long codAfericaoInserida = sistema.insert(conn, codUnidade, codAuxiliarUnidade, afericao);

            if (afericao instanceof AfericaoPlaca) {
                final ApiAutenticacaoHolder apiAutenticacaoHolder =
                        integracaoDao.getApiAutenticacaoHolder(conn,
                                                               codEmpresaProlog,
                                                               getSistemaKey(),
                                                               MetodoIntegrado.INSERT_AFERICAO_PLACA);
                final AfericaoPlacaProtheusNepomuceno afericaoPlacaProtheusNepomuceno = ProtheusNepomucenoConverter
                        .convert(codAuxiliarUnidade, (AfericaoPlaca) afericao, zoneIdForCodUnidade);
                requester.insertAfericaoPlaca(apiAutenticacaoHolder, afericaoPlacaProtheusNepomuceno);
            } else {
                final ApiAutenticacaoHolder apiAutenticacaoHolder =
                        integracaoDao.getApiAutenticacaoHolder(conn,
                                                               codEmpresaProlog,
                                                               getSistemaKey(),
                                                               MetodoIntegrado.INSERT_AFERICAO_AVULSA);
                final AfericaoAvulsaProtheusNepomuceno afericaoAvulsaProtheusNepomuceno = ProtheusNepomucenoConverter
                        .convert(codAuxiliarUnidade, (AfericaoAvulsa) afericao);
                requester.insertAfericaoAvulsa(apiAutenticacaoHolder, afericaoAvulsaProtheusNepomuceno);
            }
            conn.commit();
            return codAfericaoInserida;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    @Override
    public Long insertTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        // Validamos se o codAuxiliar está dentro dos padrões. Uma exception personalizada é lançada caso não estiver
        // de acordo.
        validateCodAuxiliar(tipoVeiculo.getCodEmpresa(), tipoVeiculo.getCodigo(), tipoVeiculo.getCodAuxiliar());
        // Usamos o fluxo padrão do Prolog, apenas validamos antes
        return getIntegradorProLog().insertTipoVeiculo(tipoVeiculo);
    }

    @Override
    public void updateTipoVeiculo(@NotNull final TipoVeiculo tipoVeiculo) throws Throwable {
        // Validamos se o codAuxiliar está dentro dos padrões. Uma exception personalizada é lançada caso não estiver
        // de acordo.
        validateCodAuxiliar(tipoVeiculo.getCodEmpresa(), tipoVeiculo.getCodigo(), tipoVeiculo.getCodAuxiliar());
        // Usamos o fluxo padrão do Prolog, apenas validamos antes
        getIntegradorProLog().updateTipoVeiculo(tipoVeiculo);
    }

    /**
     * Método privado utilizado para buscar a Filial Protheus de uma Placa específica.
     *
     * <p>
     * O mapeamento de unidades permite que mais de um código auxiliar seja mapeado para um único código
     * Prolog. Com isso, chegando nesse método o 'codUnidade' pode ter mais de um código auxiliar e
     * não conseguiríamos executar a busca no Protheus, pois não saberíamos qual dos códigos usar.
     * Assim, fazemos a busca do cronograma novamente (somente para unidade selecionada) e pegamos o
     * código direto da Placa selecionada.
     *
     * @param conn         Conexão com o banco utilizada no processo.
     * @param codEmpresa   Código da empresa Prolog que estamos utilizando.
     * @param codUnidade   Código da unidade Prolog que estamos utilizando.
     * @param placaVeiculo Placa do Veículo no qual queremos buscar a Filial.
     * @param sistema      Sistema utilizado para conexões e busca de dados internos.
     * @return Uma String contendo o código da Filial Protheus da placa.
     *
     * @throws Throwable Se algum erro acorrer inesperadamente ou se não for encontrado uma filial para a Placa.
     */
    @NotNull
    private String getCodFilialByPlacaCronograma(@NotNull final Connection conn,
                                                 @NotNull final Long codEmpresa,
                                                 @NotNull final Long codUnidade,
                                                 @NotNull final String placaVeiculo,
                                                 @NotNull final SistemaProtheusNepomucenoDao sistema) throws Throwable {
        final List<VeiculoListagemProtheusNepomuceno> listagemVeiculos =
                internalGetVeiculos(conn, codEmpresa, Collections.singletonList(codUnidade), sistema);
        return listagemVeiculos
                .stream()
                .filter(veiculo -> !veiculo.deveRemover())
                .filter(veiculo -> veiculo.getPlacaVeiculo().equals(placaVeiculo)
                        || veiculo.getCodVeiculo().equals(placaVeiculo))
                .map(VeiculoListagemProtheusNepomuceno::getCodEmpresaFilialVeiculo)
                .findFirst()
                .orElseThrow(() -> {
                    throw new ProtheusNepomucenoException("Placa não encontrada para Aferir");
                });
    }

    @NotNull
    private List<VeiculoListagemProtheusNepomuceno> internalGetVeiculos(
            @NotNull final Connection conn,
            @NotNull final Long codEmpresa,
            @NotNull final List<Long> codUnidades,
            @NotNull final SistemaProtheusNepomucenoDao sistema) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresa,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
        final Map<Long, String> codFiliais = sistema.getCodFiliais(conn, codUnidades);
        return requester.getListagemVeiculosUnidadesSelecionadas(
                apiAutenticacaoHolder,
                ProtheusNepomucenoUtils.getOnlyFiliais(codFiliais));
    }

    private void validateCodAuxiliar(@Nullable final Long codEmpresaTipoVeiculo,
                                     @Nullable final Long codTipoVeiculo,
                                     @Nullable final String codAuxiliarTipoVeiculo) throws Throwable {
        if (codAuxiliarTipoVeiculo == null || codAuxiliarTipoVeiculo.isEmpty()) {
            // Não precisamos validar nada, se o código auxiliar for nulo.
            return;
        }
        // Validamos se o código a ser cadastrado está no padrão
        ProtheusNepomucenoUtils.validateCodAuxiliarTipoVeiculo(codAuxiliarTipoVeiculo);
        final List<String> codsAuxiliares =
                new SistemaProtheusNepomucenoDaoImpl()
                        .verificaCodAuxiliarTipoVeiculoValido(codEmpresaTipoVeiculo, codTipoVeiculo);
        final List<String> novosCodsAuxiliares =
                ProtheusNepomucenoUtils.getCodAuxiliarTipoVeiculoAsArray(codAuxiliarTipoVeiculo);
        // Estouramos uma exception com mensagem personalizada caso já existir qualquer código idêntico já mapeado.
        codsAuxiliares
                .stream()
                .filter(codAuxiliar -> {
                    for (final String novoCodAuxiliar : novosCodsAuxiliares) {
                        if (codAuxiliar.contains(novoCodAuxiliar) || novoCodAuxiliar.contains(codAuxiliar)) {
                            return true;
                        }
                    }
                    return false;
                })
                .findAny()
                .ifPresent(codAuxiliar -> {
                    throw new ProtheusNepomucenoException(
                            "O código auxiliar " + codAuxiliarTipoVeiculo +
                                    " já está cadastrado em outro Tipo de Veículo e não pode ser repetido");
                });
    }

    private void logEstruturasNaoMapeadas(@NotNull final Set<String> estruturasNaoMapeadas) {
        final String message = "Estruturas não mapeadas: " + estruturasNaoMapeadas;
        Log.i(TAG, message);
        ErrorReportSystem.logMessage(message);
    }
}