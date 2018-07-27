package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogExceptionHandler;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.*;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericao;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe AfericaoService responsavel por comunicar-se com a interface DAO
 */
public class AfericaoService {
    private static final String TAG = AfericaoService.class.getSimpleName();
    @NotNull
    private final AfericaoDao dao = Injection.provideAfericaoDao();
    @NotNull
    private final ProLogExceptionHandler exceptionHandler = Injection.provideProLogExceptionHandler();

    public boolean insert(Afericao afericao, Long codUnidade, String userToken) {
        try {
            afericao.setDataHora(LocalDateTime.now(Clock.systemUTC()));
            return RouterAfericao
                    .create(dao, userToken)
                    .insertAfericao(afericao, codUnidade);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao inserir a aferição", e);
            return false;
        }
    }

    public boolean updateKmAfericao(Afericao afericao) {
        try {
            return dao.update(afericao);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao atualizar o KM de uma aferição", e);
            return false;
        }
    }

    public NovaAfericaoPlaca getNovaAfericaoPlaca(Long codUnidade,
                                                  String placa,
                                                  String tipoAfericao,
                                                  String userToken) throws ProLogException {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getNovaAfericaoPlaca(codUnidade, placa, tipoAfericao);
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para a placa: " + placa, e);
            throw exceptionHandler.map(e, "Erro ao inicar uma nova aferição");
        }
    }

    public NovaAfericaoAvulsa getNovaAfericaoAvulsa(Long codUnidade,
                                                    Long codPneu,
                                                    String tipoAfericao) throws ProLogException {
        try {
            return dao.getNovaAfericaoAvulsa(codUnidade, codPneu, TipoMedicaoColetadaAfericao.fromString(tipoAfericao));
        } catch (final Throwable e) {
            Log.e(TAG, "Erro ao buscar NovaAfericao para o pneu: " + codPneu, e);
            throw exceptionHandler.map(e, "Erro ao inicar uma nova aferição");
        }
    }

    public Afericao getByCod(Long codUnidade, Long codAfericao, String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericaoByCodigo(codUnidade, codAfericao);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar uma aferição específica", e);
            throw new RuntimeException(e);
        }
    }

    public CronogramaAfericao getCronogramaAfericao(final Long codUnidade, final String userToken) throws Exception {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getCronogramaAfericao(codUnidade);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar o cronograma de aferições", e);
            throw e;
        }
    }

    public List<Afericao> getAfericoes(Long codUnidade,
                                       String codTipoVeiculo,
                                       String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       int limit,
                                       long offset,
                                       final String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar as aferições", e);
            throw new RuntimeException("Erro ao buscar aferições. Unidade: "
                    + codUnidade + " || Tipo: "
                    + codTipoVeiculo + " || Placa: "
                    + placaVeiculo);
        }
    }

    @Deprecated
    public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, int limit,
                                                          long offset) {
        try {
            return dao.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar as aferições de uma placa", e);
            return null;
        }
    }

    public Restricao getRestricaoByCodUnidade(Long codUnidade) {
        try {
            return dao.getRestricaoByCodUnidade(codUnidade);
        } catch (SQLException e) {
            Log.e(TAG, "Erro ao buscar as restrições", e);
            return null;
        }
    }
}
