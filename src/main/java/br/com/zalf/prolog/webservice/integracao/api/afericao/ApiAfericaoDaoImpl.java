package br.com.zalf.prolog.webservice.integracao.api.afericao;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.integracao.api.afericao._model.AfericaoRealizada;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-05-06
 *
 * @author Natan Rotta (https://github.com/natanrotta)
 */
final class ApiAfericaoDaoImpl extends DatabaseConnection implements ApiAfericaoDao {

    @NotNull
    @Override
    public List<AfericaoRealizada> getAfericoesRealizadas(@NotNull final String tokenIntegracao,
                                                          @NotNull final Long codUltimaAfericao) throws Throwable {
        final List<AfericaoRealizada> afericoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM " +
                    "INTEGRACAO.FUNC_INTEGRACAO_BUSCA_AFERICOES_REALIZADAS_EMPRESA(?, ?);");
            stmt.setString(1, tokenIntegracao);
            stmt.setLong(2, codUltimaAfericao);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                AfericaoRealizada afericaoRealizada = new AfericaoRealizada();
                afericaoRealizada.
                        setCodigo(rSet.getLong("COD_AFERICAO"));
                afericaoRealizada.
                        setCodUnidadeAfericao(rSet.getLong("COD_UNIDADE_AFERICAO"));
                afericaoRealizada.
                        setCpfColaborador(rSet.getString("CPF_COLABORADOR"));
                afericaoRealizada.
                        setPlacaVeiculoAferido(rSet.getString("PLACA_VEICULO_AFERIDO"));
                afericaoRealizada.
                        setCodPneuAferido(rSet.getLong("COD_PNEU_AFERIDO"));
                afericaoRealizada.
                        setNumeroFogoPneu(rSet.getString("NUMERO_FOGO"));
                afericaoRealizada.
                        setAlturaSulcoInternoEmMilimetros(rSet.getDouble("ALTURA_SULCO_INTERNO"));
                afericaoRealizada.
                        setAlturaSulcoCentralInternoEmMilimetros(rSet.
                                getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
                afericaoRealizada.
                        setAlturaSulcoCentralExternoEmMilimetros(rSet.
                                getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
                afericaoRealizada.
                        setAlturaSulcoExternoEmMilimetros(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
                afericaoRealizada.
                        setPressaoEmPsi(rSet.getDouble("PRESSAO"));
                afericaoRealizada.
                        setKmVeiculoMomentoAfericao(rSet.getLong("KM_VEICULO_MOMENTO_AFERICAO"));
                afericaoRealizada.
                        setTempoRealizacaoEmSegundos(rSet.getLong("TEMPO_REALIZACAO_AFERICAO_EM_MILIS"));
                afericaoRealizada.
                        setVidaPneuMomentoAfericao(rSet.getInt("VIDA_MOMENTO_AFERICAO"));
                afericaoRealizada.
                        setPosicaoPneuMomentoAfericao(rSet.getInt("POSICAO_PNEU_MOMENTO_AFERICAO"));
                afericaoRealizada.
                        setDataHoraAfericaoEmUTC(rSet.getObject("DATA_HORA_AFERICAO", LocalDateTime.class));
                switch (rSet.getString("TIPO_MEDICAO_COLETADA")) {
                    case "SULCO":
                        afericaoRealizada.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO);
                        break;
                    case "PRESSAO":
                        afericaoRealizada.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.PRESSAO);
                        break;
                    case "SULCO_PRESSAO":
                        afericaoRealizada.setTipoMedicaoColetadaAfericao(TipoMedicaoColetadaAfericao.SULCO_PRESSAO);
                        break;
                }
                switch (rSet.getString("TIPO_PROCESSO_COLETA")) {
                    case "PLACA":
                        afericaoRealizada.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PLACA);
                        break;
                    case "PNEU_AVULSO":
                        afericaoRealizada.setTipoProcessoColetaAfericao(TipoProcessoColetaAfericao.PNEU_AVULSO);
                        break;
                }
                afericoes.add(afericaoRealizada);
            }
            return afericoes;
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
