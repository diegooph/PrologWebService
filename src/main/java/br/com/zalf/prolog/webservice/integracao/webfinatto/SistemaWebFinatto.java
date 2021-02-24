package br.com.zalf.prolog.webservice.integracao.webfinatto;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.IntegracaoPosicaoPneuMapper;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.integrador.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.ResponseAfericaoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.error.SistemaWebFinattoException;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequester;
import br.com.zalf.prolog.webservice.integracao.webfinatto.utils.SistemaWebFinattoConverter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SistemaWebFinatto extends Sistema {
    @NotNull
    private static final String TAG = SistemaWebFinatto.class.getSimpleName();
    @NotNull
    private final SistemaWebFinattoRequester requester;
    @NotNull
    private final IntegracaoDao integracaoDao;

    public SistemaWebFinatto(@NotNull final SistemaWebFinattoRequester requester,
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
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, codUnidades);
            if (unidadeDeParaHolder.isEmpty()) {
                return SistemaWebFinattoConverter.createEmptyCronogramaAfericaoProlog();
            }
            final UnidadeRestricaoHolder unidadeRestricaoHolder =
                    integracaoDao.getUnidadeRestricaoHolder(conn, unidadeDeParaHolder.getCodUnidadesMapeadas());
            final TipoVeiculoConfigAfericaoHolder tipoVeiculoConfigAfericaoHolder =
                    integracaoDao.getTipoVeiculoConfigAfericaoHolder(conn,
                                                                     unidadeDeParaHolder.getCodUnidadesMapeadas());
            final List<VeiculoWebFinatto> veiculosByFiliais =
                    internalGetVeiculosByFiliais(conn,
                                                 unidadeDeParaHolder.getCodEmpresaProlog(),
                                                 unidadeDeParaHolder.getCodFiliais());
            final List<String> placas = veiculosByFiliais.stream()
                    .map(VeiculoWebFinatto::getCodVeiculo)
                    .distinct()
                    .collect(Collectors.toList());
            final AfericaoRealizadaPlacaHolder afericaoRealizadaPlacaHolder =
                    integracaoDao.getAfericaoRealizadaPlacaHolder(conn,
                                                                  unidadeDeParaHolder.getCodEmpresaProlog(),
                                                                  placas);

            return SistemaWebFinattoConverter.createCronogramaAfericaoProlog(veiculosByFiliais,
                                                                             unidadeRestricaoHolder,
                                                                             tipoVeiculoConfigAfericaoHolder,
                                                                             afericaoRealizadaPlacaHolder);
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
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final VeiculoWebFinatto veiculoByPlaca =
                    internalGetVeiculoByPlaca(conn,
                                              unidadeDeParaHolder.getCodEmpresaProlog(),
                                              unidadeDeParaHolder.getCodFiliais(),
                                              placaVeiculo);
            final Short codDiagramaProlog =
                    integracaoDao.getCodDiagramaByDeParaTipoVeiculo(conn,
                                                                    unidadeDeParaHolder.getCodEmpresaProlog(),
                                                                    veiculoByPlaca.getCodEstruturaVeiculo());
            if (codDiagramaProlog <= 0) {
                throw new SistemaWebFinattoException(
                        "Identificamos aque a estrutura (" + veiculoByPlaca.getCodEstruturaVeiculo() + ") " +
                                "não está configurada no Prolog.\n" +
                                "Por favor, solicite que esta esta estrutura seja cadastrada no Prolog para " +
                                "realizar a aferição.");
            }
            final ConfiguracaoNovaAfericaoPlaca configNovaAfericaoPlaca =
                    integracaoDao.getConfigNovaAfericaoPlaca(conn,
                                                             codUnidade,
                                                             veiculoByPlaca.getCodEstruturaVeiculo());
            final IntegracaoPosicaoPneuMapper posicaoPneuMapper = new IntegracaoPosicaoPneuMapper(
                    veiculoByPlaca.getCodEstruturaVeiculo(),
                    integracaoDao.getMapeamentoPosicoesPrologByDeParaTipoVeiculo(
                            conn,
                            unidadeDeParaHolder.getCodEmpresaProlog(),
                            veiculoByPlaca.getCodEstruturaVeiculo()));
            return SistemaWebFinattoConverter.createNovaAfericaoPlacaProlog(codUnidade,
                                                                            codDiagramaProlog,
                                                                            veiculoByPlaca,
                                                                            posicaoPneuMapper,
                                                                            configNovaAfericaoPlaca);
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
            final UnidadeDeParaHolder unidadeDeParaHolder =
                    integracaoDao.getCodAuxiliarByCodUnidadeProlog(conn, Collections.singletonList(codUnidade));
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           unidadeDeParaHolder.getCodEmpresaProlog(),
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA);
            final List<PneuWebFinatto> pneusByFiliais =
                    requester.getPneusByFiliais(apiAutenticacaoHolder,
                                                unidadeDeParaHolder.getCodFiliais(),
                                                "ESTOQUE",
                                                null);

            final List<String> codPneus = pneusByFiliais.stream()
                    .map(PneuWebFinatto::getCodigoCliente)
                    .collect(Collectors.toList());

            final AfericaoRealizadaAvulsaHolder afericaoRealizadaAvulsaHolder =
                    integracaoDao.getAfericaoRealizadaAvulsaHolder(conn,
                                                                   codUnidade,
                                                                   codPneus);
            return SistemaWebFinattoConverter.createPneusAfericaoAvulsa(codUnidade,
                                                                        pneusByFiliais,
                                                                        afericaoRealizadaAvulsaHolder);
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
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEU_NOVA_AFERICAO_AVULSA);
            final PneuWebFinatto pneuByCodigo = requester.getPneuByCodigo(apiAutenticacaoHolder,
                                                                          "10:01",
                                                                          "507");
            return super.getNovaAfericaoAvulsa(codUnidade, codPneu, tipoMedicaoColetadaAfericao);
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
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.INSERT_AFERICAO_PLACA);
            final ResponseAfericaoWebFinatto responseAfericaoWebFinatto =
                    requester.insertAfericaoPlaca(apiAutenticacaoHolder, null);
            return super.insertAfericao(codUnidade, afericao, deveAbrirServico);
        } finally {
            connectionProvider.closeResources(conn);
        }
    }

    @NotNull
    private List<VeiculoWebFinatto> internalGetVeiculosByFiliais(@NotNull final Connection conn,
                                                                 @NotNull final Long codEmpresaProlog,
                                                                 @NotNull final String codFiliais) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresaProlog,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
        return requester.getVeiculosByFiliais(apiAutenticacaoHolder,
                                              codFiliais,
                                              null);
    }

    @NotNull
    private VeiculoWebFinatto internalGetVeiculoByPlaca(@NotNull final Connection conn,
                                                        @NotNull final Long codEmpresaProlog,
                                                        @NotNull final String codFilial,
                                                        @NotNull final String placaVeiculo) throws Throwable {
        final ApiAutenticacaoHolder apiAutenticacaoHolder =
                integracaoDao.getApiAutenticacaoHolder(conn,
                                                       codEmpresaProlog,
                                                       getSistemaKey(),
                                                       MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
        return requester.getVeiculoByPlaca(apiAutenticacaoHolder,
                                           codFilial,
                                           placaVeiculo);
    }
}
