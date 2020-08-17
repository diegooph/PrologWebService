package br.com.zalf.prolog.webservice.integracao.avacorpavilan.data;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan.cadastro.TipoVeiculoAvilan;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public class AvaCorpAvilanDaoImpl extends DatabaseConnection implements AvaCorpAvilanDao {

    private static final String TAG = AvaCorpAvilanDaoImpl.class.getSimpleName();

    @Nonnull
    @Override
    public List<TipoVeiculoAvilanProLog> getTiposVeiculosAvilanProLog() throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        final List<TipoVeiculoAvilanProLog> tiposVeiculos = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM AVILAN.VEICULO_TIPO;");
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                tiposVeiculos.add(createTipoAvilanProLog(rSet));
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return tiposVeiculos;
    }

    @Nonnull
    @Override
    public Long insertTipoVeiculoAvilan(final TipoVeiculoAvilan tipoVeiculoAvilan) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("INSERT INTO AVILAN.VEICULO_TIPO (CODIGO, DESCRICAO) VALUES (?, ?) " +
                    "RETURNING COD_PROLOG");
            stmt.setString(1, tipoVeiculoAvilan.getCodigo());
            stmt.setString(2, tipoVeiculoAvilan.getNome());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("COD_PROLOG");
            } else {
                throw new SQLException("Erro ao inserir o tipo de veículo da avilan");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nonnull
    @Override
    public String getCodTipoVeiculoAvilanByCodTipoVeiculoProLog(final Long codigo) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT CODIGO FROM AVILAN.VEICULO_TIPO WHERE COD_PROLOG = ?;");
            stmt.setLong(1, codigo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("CODIGO");
            }

            throw new IllegalArgumentException("Nenhum tipo de veículo encontrado para o código do ProLog: " + codigo);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nonnull
    @Override
    public FilialUnidadeAvilanProLog getFilialUnidadeAvilanByCodUnidadeProLog(final Long codUnidadeProLog) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT F.CODIGO AS COD_FILIAL_AVILAN, F.COD_UNIDADE_PROLOG AS " +
                    "COD_UNIDADE_PROLOG, U.CODIGO AS COD_UNIDADE_AVILAN FROM AVILAN.FILIAL F JOIN AVILAN.UNIDADE U " +
                    "ON F.CODIGO = U.COD_FILIAL WHERE F.COD_UNIDADE_PROLOG = ?;");
            stmt.setLong(1, codUnidadeProLog);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return createFilialUnidadeAvilanProLog(rSet);
            } else {
                throw new SQLException("Erro ao buscar filial/unidade da Avilan com o código de unidade do ProLog: "
                        + codUnidadeProLog);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nonnull
    @Override
    public Short getCodDiagramaVeiculoProLogByCodTipoVeiculoAvilan(@Nonnull final String codTipoVeiculoAvilan)
            throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COD_VEICULO_DIAGRAMA_PROLOG " +
                    "FROM AVILAN.VEICULO_TIPO_VEICULO_DIAGRAMA " +
                    "WHERE COD_VEICULO_TIPO = ?;");
            stmt.setString(1, codTipoVeiculoAvilan);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getShort("COD_VEICULO_DIAGRAMA_PROLOG");
            }

            throw new IllegalArgumentException("Nenhum diagrama encontrado com o tipo: " + codTipoVeiculoAvilan);
        } finally {
            closeConnection(conn, stmt, rSet);
        }
    }

    @Nonnull
    @Override
    public BiMap<String, Integer> getPosicoesPneuAvilanProLogByCodTipoVeiculoAvilan(
            @Nonnull final String codTipoVeiculoAvilan) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        final BiMap<String, Integer> posicoesPneu = HashBiMap.create();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT POSICAO_PNEU_AVILAN, POSICAO_PNEU_PROLOG FROM " +
                    "AVILAN.PNEU_POSICAO_AVILAN_PROLOG WHERE COD_VEICULO_TIPO = ?;");
            stmt.setString(1, codTipoVeiculoAvilan);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                do {
                    posicoesPneu.put(
                            rSet.getString("POSICAO_PNEU_AVILAN"),
                            rSet.getInt("POSICAO_PNEU_PROLOG"));
                } while (rSet.next());
            } else {
                throw new RuntimeException("Nenhuma posição mapeada para o tipo de veículo: " + codTipoVeiculoAvilan);
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return posicoesPneu;
    }

    @Override
    public Map<Long, String> getMapeamentoCodPerguntaUrlImagem(final Long codQuestionario) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        final Map<Long, String> mapPerguntaUrlImagem = new HashMap<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT COD_PERGUNTA, URL_IMAGEM FROM " +
                    "AVILAN.CHECKLIST_PERGUNTA_URL_IMAGEM WHERE COD_QUESTIONARIO = ?;");
            stmt.setLong(1, codQuestionario);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                do {
                    mapPerguntaUrlImagem.put(
                            rSet.getLong("COD_PERGUNTA"),
                            rSet.getString("URL_IMAGEM"));
                } while (rSet.next());
            } else {
                Log.d(TAG, "Não há imagens mapeadas para o questionário com código " + codQuestionario
                        + " da Avilan");
            }
        } finally {
            closeConnection(conn, stmt, rSet);
        }
        return mapPerguntaUrlImagem;
    }

    @Nonnull
    @Override
    public String getPlacaByCodVeiculo(@Nonnull final Long codVeiculo) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT PLACA FROM VEICULO_DATA V WHERE V.CODIGO = ?;");
            stmt.setLong(1, codVeiculo);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getString("PLACA");
            } else {
                throw new SQLException("Erro ao buscar a placa do veículo: " + codVeiculo);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @Nonnull
    private TipoVeiculoAvilanProLog createTipoAvilanProLog(final ResultSet rSet) throws SQLException {
        final TipoVeiculoAvilanProLog tipoVeiculo = new TipoVeiculoAvilanProLog();
        tipoVeiculo.setCodigoAvilan(rSet.getString("CODIGO"));
        tipoVeiculo.setDescricao(rSet.getString("DESCRICAO"));
        tipoVeiculo.setCodProLog(rSet.getLong("COD_PROLOG"));
        return tipoVeiculo;
    }

    @Nonnull
    private FilialUnidadeAvilanProLog createFilialUnidadeAvilanProLog(final ResultSet rSet) throws SQLException {
        final FilialUnidadeAvilanProLog filialUnidade = new FilialUnidadeAvilanProLog();
        filialUnidade.setCodFilialAvilan(rSet.getInt("COD_FILIAL_AVILAN"));
        filialUnidade.setCodUnidadeAvilan(rSet.getInt("COD_UNIDADE_AVILAN"));
        filialUnidade.setCodUnidadeProLog(rSet.getLong("COD_UNIDADE_PROLOG"));
        return filialUnidade;
    }
}