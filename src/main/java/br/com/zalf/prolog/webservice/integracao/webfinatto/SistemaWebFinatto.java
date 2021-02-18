package br.com.zalf.prolog.webservice.integracao.webfinatto;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.*;
import br.com.zalf.prolog.webservice.integracao.IntegracaoDao;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.MetodoIntegrado;
import br.com.zalf.prolog.webservice.integracao.RecursoIntegrado;
import br.com.zalf.prolog.webservice.integracao.praxio.data.ApiAutenticacaoHolder;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.PneuWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.ResponseAfericaoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto._model.VeiculoWebFinatto;
import br.com.zalf.prolog.webservice.integracao.webfinatto.data.SistemaWebFinattoRequester;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;

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
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_VEICULOS_CRONOGRAMA_AFERICAO);
            final List<VeiculoWebFinatto> veiculosByFiliais =
                    requester.getVeiculosByFiliais(apiAutenticacaoHolder, "10:02", null);
            return super.getCronogramaAfericao(codUnidades);
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
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_VEICULO_NOVA_AFERICAO_PLACA);
            final VeiculoWebFinatto veiculoByPlaca =
                    requester.getVeiculoByPlaca(apiAutenticacaoHolder, "10:01", "MJA8092");
            return super.getNovaAfericaoPlaca(codUnidade, placaVeiculo, tipoAfericao);
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
            final ApiAutenticacaoHolder apiAutenticacaoHolder =
                    integracaoDao.getApiAutenticacaoHolder(conn,
                                                           3L,
                                                           getSistemaKey(),
                                                           MetodoIntegrado.GET_PNEUS_AFERICAO_AVULSA);
            final List<PneuWebFinatto> pneusByFiliais =
                    requester.getPneusByFiliais(apiAutenticacaoHolder,
                                                "10:01",
                                                "ESTOQUE",
                                                null);
            return super.getPneusAfericaoAvulsa(codUnidade);
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
}
