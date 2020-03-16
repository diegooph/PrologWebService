package br.com.zalf.prolog.webservice.integracao.protheusnepomuceno;

import br.com.zalf.prolog.webservice.commons.util.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoMedicaoColetadaAfericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.TipoProcessoColetaAfericao;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno._model.InfosAfericaoAvulsa;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/03/20
 *
 * @author Wellington Moraes (https://github.com/wvinim)
 */
public final class SistemaProtheusNepomucenoDaoImpl extends DatabaseConnection implements SistemaProtheusNepomucenoDao {
    public SistemaProtheusNepomucenoDaoImpl() {

    }

    @NotNull
    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final Long codUnidade,
                       @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_INTEGRADA(" +
                    "F_COD_UNIDADE_PROLOG := ?," +
                    "F_CPF_AFERIDOR := ?, " +
                    "F_PLACA_VEICULO := ?, " +
                    "F_COD_TIPO_VEICULO_PROLOG := ?, " +
                    "F_KM_VEICULO := ?, " +
                    "F_TEMPO_REALIZACAO := ?, " +
                    "F_DATA_HORA := ?, " +
                    "F_TIPO_MEDICAO_COLETADA := ?, " +
                    "F_TIPO_PROCESSO_COLETA := ?) AS COD_AFERICAO_INTEGRADA;");
            stmt.setString(1, String.valueOf(codUnidade));
            stmt.setString(2, String.valueOf(afericao.getColaborador().getCpf()));
            stmt.setString(6, String.valueOf(afericao.getTempoRealizacaoAfericaoInMillis()));
            stmt.setString(7, String.valueOf(afericao.getDataHora().atOffset(ZoneOffset.UTC)));
            stmt.setString(8, afericao.getTipoMedicaoColetadaAfericao().asString());
            stmt.setString(9, afericao.getTipoProcessoColetaAfericao().asString());

            if (afericao instanceof AfericaoPlaca) {
                final AfericaoPlaca afericaoPlaca = (AfericaoPlaca) afericao;
                stmt.setString(3, afericaoPlaca.getVeiculo().getPlaca());
                stmt.setString(4, String.valueOf(afericaoPlaca.getVeiculo().getCodTipo()));
                stmt.setString(5, String.valueOf(afericaoPlaca.getKmMomentoAfericao()));
            } else {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.VARCHAR);
            }
            Long codAfericaoIntegrada = null;
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                codAfericaoIntegrada = rSet.getLong("COD_AFERICAO_INTEGRADA");
                afericao.setCodigo(codAfericaoIntegrada);
                insertValores(conn, afericao);
            }
            if (codAfericaoIntegrada != null && codAfericaoIntegrada != 0) {
                return codAfericaoIntegrada;
            } else {
                throw new IllegalStateException("Não foi possível retornar o código da aferição realizada");
            }
        } finally {
            close(stmt, rSet);
        }
    }

    private void insertValores(@NotNull final Connection conn,
                               @NotNull final Afericao afericao) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_INSERT_AFERICAO_VALORES_INTEGRADA(" +
                    "F_COD_AFERICAO_INTEGRADA := ?," +
                    "F_COD_PNEU_PROLOG := ?, " +
                    "F_COD_PNEU_CLIENTE := ?, " +
                    "F_COD_PNEU_CLIENTE_AUXILIAR := ?, " +
                    "F_VIDA_ATUAL := ?, " +
                    "F_PSI := ?, " +
                    "F_ALTURA_SULCO_INTERNO := ?, " +
                    "F_ALTURA_SULCO_CENTRAL_INTERNO := ?, " +
                    "F_ALTURA_SULCO_EXTERNO := ?, " +
                    "F_ALTURA_SULCO_CENTRAL_EXTERNO := ?, " +
                    "F_POSICAO_PROLOG := ?) AS COD_AFERICAO_INTEGRADA;");

            final List<Pneu> pneusAferidos = afericao.getPneusAferidos();
            for (Pneu pneu : pneusAferidos) {
                stmt.setLong(1, afericao.getCodigo());
                // A integração com o sistema da Nepomuceno não mantém pneus no Prolog
                stmt.setNull(2, Types.VARCHAR);
                stmt.setString(3, String.valueOf(pneu.getCodigo()));
                stmt.setString(4, String.valueOf(pneu.getCodigoCliente()));
                stmt.setString(5, String.valueOf(pneu.getVidaAtual()));

                // Já aproveitamos esse switch para atualizar as medições do pneu na tabela PNEU.
                switch (afericao.getTipoMedicaoColetadaAfericao()) {
                    case SULCO_PRESSAO:
                        stmt.setString(6, String.valueOf(pneu.getPressaoAtual()));
                        stmt.setString(7, String.valueOf(pneu.getSulcosAtuais().getInterno()));
                        stmt.setString(8, String.valueOf(pneu.getSulcosAtuais().getCentralInterno()));
                        stmt.setString(9, String.valueOf(pneu.getSulcosAtuais().getExterno()));
                        stmt.setString(10, String.valueOf(pneu.getSulcosAtuais().getCentralExterno()));
                        break;
                    case SULCO:
                        stmt.setNull(6, Types.VARCHAR);
                        stmt.setString(7, String.valueOf(pneu.getSulcosAtuais().getInterno()));
                        stmt.setString(8, String.valueOf(pneu.getSulcosAtuais().getCentralInterno()));
                        stmt.setString(9, String.valueOf(pneu.getSulcosAtuais().getExterno()));
                        stmt.setString(10, String.valueOf(pneu.getSulcosAtuais().getCentralExterno()));
                        break;
                    case PRESSAO:
                        stmt.setString(6, String.valueOf(pneu.getPressaoAtual()));
                        stmt.setNull(7, Types.VARCHAR);
                        stmt.setNull(8, Types.VARCHAR);
                        stmt.setNull(9, Types.VARCHAR);
                        stmt.setNull(10, Types.VARCHAR);
                        break;
                }
                stmt.setString(11, String.valueOf(pneu.getPosicao()));
                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Não foi possível atualizar as medidas para o pneu: " + pneu.getCodigo());
                }
            }
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public String getCodAuxiliarUnidade(@NotNull final Connection conn,
                                        @NotNull final Long codUnidade) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT COD_AUXILIAR FROM PUBLIC.UNIDADE WHERE CODIGO = ?;");
            stmt.setLong(1, codUnidade);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("COD_AUXILIAR");
            }

            throw new SQLException("Não foi possível encontrar o código auxiliar da unidade: " + codUnidade);
        } finally {
            close(stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<InfosAfericaoAvulsa> getInfosAfericaoAvulsa(@NotNull final Connection conn,
                                                     @NotNull final Long codUnidade,
                                                     @NotNull final List<String> codPneus) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM INTEGRACAO.FUNC_PNEU_AFERICAO_GET_INFOS_AFERICOES_INTEGRADA(" +
                    "F_COD_UNIDADE := ?," +
                    "F_COD_PNEUS_CLIENTE := ?);");
            stmt.setLong(1, codUnidade);
            stmt.setArray(2, PostgresUtils.listToArray(conn, SqlType.TEXT, codPneus));
            rSet = stmt.executeQuery();

            final List<InfosAfericaoAvulsa> infosAfericaoAvulsa = new ArrayList<>();
            while (rSet.next()) {
                infosAfericaoAvulsa.add(createInfosAfericaoAvulsa(rSet));
            }

            return infosAfericaoAvulsa;
        } finally {
            close(stmt, rSet);
        }
    };

    @NotNull
    private InfosAfericaoAvulsa createInfosAfericaoAvulsa(@NotNull final ResultSet rSet) throws Throwable{
        return new InfosAfericaoAvulsa(
                rSet.getLong("CODIGO_ULTIMA_AFERICAO"),
                rSet.getString("COD_PNEU_PROLOG"),
                rSet.getString("COD_PNEU_CLIENTE"),
                rSet.getString("COD_PNEU_CLIENTE_AUXILIAR"),
                rSet.getString("DATA_HORA_ULTIMA_AFERICAO"),
                rSet.getString("NOME_COLABORADOR_AFERICAO"),
                TipoMedicaoColetadaAfericao.fromString(rSet.getString("TIPO_MEDICAO_COLETADA")),
                TipoProcessoColetaAfericao.fromString(rSet.getString("TIPO_PROCESSO_COLETA")),
                rSet.getString("PLACA_APLICADO_QUANDO_AFERIDO")
        );
    }
}
