package br.com.zalf.prolog.webservice.frota.pneu.afericao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.CronogramaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.NovaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Restricao;
import br.com.zalf.prolog.webservice.integracao.router.RouterAfericao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Classe AfericaoService responsavel por comunicar-se com a interface DAO
 */
public class AfericaoService {

    private final AfericaoDao dao = Injection.provideAfericaoDao();

    public boolean insert(Afericao afericao, Long codUnidade, String userToken) {
        afericao.setDataHora(new Date(System.currentTimeMillis()));
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .insertAfericao(afericao, codUnidade);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateKmAfericao(Afericao afericao) {
        try {
            return dao.update(afericao);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public NovaAfericao getNovaAfericao(String placa, String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getNovaAfericao(placa);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Afericao getByCod(Long codUnidade, Long codAfericao, String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericaoByCodigo(codUnidade, codAfericao);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public CronogramaAfericao getCronogramaAfericao(final Long codUnidade, final String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getCronogramaAfericao(codUnidade);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Afericao> getAfericoes(Long codUnidade,
                                       String codTipoVeiculo,
                                       String placaVeiculo,
                                       long dataInicial,
                                       long dataFinal,
                                       long limit,
                                       long offset,
                                       final String userToken) {
        try {
            return RouterAfericao
                    .create(dao, userToken)
                    .getAfericoes(codUnidade, codTipoVeiculo, placaVeiculo, dataInicial, dataFinal, limit, offset);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao buscar aferições. Unidade: "
                    + codUnidade + " || Tipo: "
                    + codTipoVeiculo + " || Placa: "
                    + placaVeiculo);
        }
    }

    public List<Afericao> getAfericoesByCodUnidadeByPlaca(List<String> codUnidades, List<String> placas, long limit,
                                                          long offset) {
        try {
            return dao.getAfericoesByCodUnidadeByPlaca(codUnidades, placas, limit, offset);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Restricao getRestricaoByCodUnidade(Long codUnidade) {
        try {
            return dao.getRestricaoByCodUnidade(codUnidade);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
