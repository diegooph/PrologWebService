package br.com.zalf.prolog.webservice.raizen.produtividade;

import br.com.zalf.prolog.webservice.commons.util.database.PostgresUtils;
import br.com.zalf.prolog.webservice.commons.util.database.SqlType;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividade;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeColaborador;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeData;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.RaizenProdutividadeIndividualHolder;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenProdutividadeItemVisualizacao;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.itens.RaizenprodutividadeItemIndividual;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeDaoImpl extends DatabaseConnection implements RaizenProdutividadeDao {

    public RaizenProdutividadeDaoImpl() {

    }

    @Override
    public void insertOrUpdateProdutividadeRaizen(
            @NotNull final String token,
            @NotNull final List<RaizenProdutividadeItemInsert> raizenItens) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            for (final RaizenProdutividadeItemInsert item : raizenItens) {
                if (!updateRaizenProdutividadeUpload(conn, token, item)) {
                    internalInsertRaizenProdutividadeItem(conn, token, item);
                }
            }
        } finally {
            close(conn);
        }
    }

    @Override
    public void insertRaizenProdutividadeItem(@NotNull final String token,
                                              @NotNull final RaizenProdutividadeItemInsert item) throws Throwable {
        Connection conn = null;
        try {
            conn = getConnection();
            internalInsertRaizenProdutividadeItem(conn, token, item);
        } finally {
            close(conn);
        }
    }

    @Override
    public void updateRaizenProdutividadeItem(@NotNull final String token,
                                              @NotNull final Long codItem,
                                              @NotNull final RaizenProdutividadeItemInsert item) throws Throwable {
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIVIDADE SET CPF_MOTORISTA = ?," +
                    "   DATA_VIAGEM = ?," +
                    "   PLACA = ?, " +
                    "   VALOR = ?," +
                    "   USINA = ?," +
                    "   FAZENDA = ?," +
                    "   RAIO_KM = ?," +
                    "   TONELADAS = ?," +
                    "   COD_UNIDADE = ?," +
                    "   COD_COLABORADOR_ALTERACAO = (SELECT CO.CODIGO FROM COLABORADOR CO JOIN TOKEN_AUTENTICACAO TA " +
                    "ON CO.CPF = TA.CPF_COLABORADOR WHERE TA.TOKEN = ?) " +
                    "WHERE CODIGO = ?");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setObject(2, item.getDataViagem());
            stmt.setString(3, item.getPlaca().toUpperCase());
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setBigDecimal(7, item.getRaioKm());
            stmt.setBigDecimal(8, item.getToneladas());
            stmt.setLong(9, item.getCodUnidade());
            stmt.setString(10, token);
            stmt.setLong(11, item.getCodigo());
            if (stmt.executeUpdate() == 0) {
                // nenhum para item atualizado
                throw new SQLDataException("Não foi possível atualizar o item de código: " + item.getCodigo());
            }
        } finally {
            close(conn, stmt, null);
        }
    }

    @NotNull
    @Override
    public List<RaizenProdutividade> getRaizenProdutividadeColaborador(
            @NotNull final Long codUnidade,
            @NotNull final LocalDate dataInicial,
            @NotNull final LocalDate dataFinal) throws Throwable {
        final List<RaizenProdutividade> produtividades = new ArrayList<>();
        RaizenProdutividadeColaborador raizenProdutividadeColaborador = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_COLABORADOR(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            boolean primeiraLinha = true;
            while (rSet.next()) {
                final Long cpfMotoristaAtual = rSet.getLong("CPF_MOTORISTA");
                if (primeiraLinha) {
                    raizenProdutividadeColaborador = RaizenProdutividadeConverter
                            .createRaizenProdutividadeColaborador(rSet);
                    produtividades.add(raizenProdutividadeColaborador);
                } else {
                    if (raizenProdutividadeColaborador.getCpf().equals(cpfMotoristaAtual)) {
                        raizenProdutividadeColaborador
                                .getItensRaizen()
                                .add(RaizenProdutividadeConverter.createRaizenProdutividadeItemData(rSet));
                    } else {
                        raizenProdutividadeColaborador = RaizenProdutividadeConverter
                                .createRaizenProdutividadeColaborador(rSet);
                        produtividades.add(raizenProdutividadeColaborador);
                    }
                }
                primeiraLinha = false;
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return produtividades;
    }

    @NotNull
    @Override
    public List<RaizenProdutividade> getRaizenProdutividadeData(@NotNull final Long codUnidade,
                                                                @NotNull final LocalDate dataInicial,
                                                                @NotNull final LocalDate dataFinal) throws Throwable {
        final List<RaizenProdutividade> produtividades = new ArrayList<>();
        RaizenProdutividadeData raizenProdutividadeData = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_POR_DATA(?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            rSet = stmt.executeQuery();
            boolean primeiraLinha = true;
            while (rSet.next()) {
                final LocalDate dataAtual = rSet.getObject("DATA_VIAGEM", LocalDate.class);
                if (primeiraLinha) {
                    raizenProdutividadeData = RaizenProdutividadeConverter.createRaizenProdutividadeData(rSet);
                    produtividades.add(raizenProdutividadeData);
                } else {
                    if (raizenProdutividadeData.getData().equals(dataAtual)) {
                        raizenProdutividadeData
                                .getItensRaizen()
                                .add(RaizenProdutividadeConverter.createRaizenProdutividadeItemColaborador(rSet));
                    } else {
                        raizenProdutividadeData = RaizenProdutividadeConverter.createRaizenProdutividadeData(rSet);
                        produtividades.add(raizenProdutividadeData);
                    }
                }
                primeiraLinha = false;
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return produtividades;
    }

    @NotNull
    @Override
    public RaizenProdutividadeItemVisualizacao getRaizenProdutividadeItemVisualizacao(@NotNull final Long codItem)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITEM_POR_CODIGO(?);");
            stmt.setLong(1, codItem);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return RaizenProdutividadeConverter.createRaizenProdutividadeItemVisualizacao(rSet);
            } else {
                throw new Throwable("Item não encontrado com código: " + codItem);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public RaizenProdutividadeIndividualHolder getRaizenProdutividadeIndividual(@NotNull final Long codUnidade,
                                                                                @NotNull final Long codColaborador,
                                                                                final int mes,
                                                                                final int ano) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        List<RaizenprodutividadeItemIndividual> itens;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM RAIZEN.FUNC_RAIZEN_PRODUTIVIDADE_GET_ITENS_INDIVIDUAL(?, ?, ?, ?);");
            stmt.setLong(1, codUnidade);
            stmt.setLong(2, codColaborador);
            stmt.setInt(3, mes);
            stmt.setInt(4, ano);
            rSet = stmt.executeQuery();
            itens = new ArrayList<>();
            while (rSet.next()) {
                itens.add(RaizenProdutividadeConverter.createRaizenProdutividadeItemIndividual(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return new RaizenProdutividadeIndividualHolder(itens);
    }

    @Override
    public void deleteRaizenProdutividadeItens(@NotNull final List<Long> codRaizenProdutividade) throws Throwable {
        if (codRaizenProdutividade.isEmpty())
            return;

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("DELETE FROM RAIZEN.PRODUTIVIDADE WHERE CODIGO::TEXT LIKE ANY (ARRAY[?]);");
            stmt.setArray(1, PostgresUtils.listToArray(conn, SqlType.TEXT, codRaizenProdutividade));
            if (stmt.executeUpdate() == 0) {
                throw new Throwable("Erro ao deletar produtividade");
            }
        } finally {
            close(conn, stmt, null);
        }
    }

    private void internalInsertRaizenProdutividadeItem(
            @NotNull final Connection conn,
            @NotNull final String token,
            @NotNull final RaizenProdutividadeItemInsert item) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO RAIZEN.PRODUTIVIDADE (CPF_MOTORISTA," +
                    "                           PLACA," +
                    "                           DATA_VIAGEM," +
                    "                           VALOR," +
                    "                           USINA," +
                    "                           FAZENDA," +
                    "                           RAIO_KM," +
                    "                           TONELADAS, " +
                    "                           COD_COLABORADOR_CADASTRO, " +
                    "                           COD_COLABORADOR_ALTERACAO, " +
                    "                           COD_UNIDADE)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
                    "   (SELECT CO.CODIGO FROM COLABORADOR CO JOIN TOKEN_AUTENTICACAO TA ON CO.CPF = TA.CPF_COLABORADOR " +
                    "WHERE TA.TOKEN = ?)," +
                    "   (SELECT CO.CODIGO FROM COLABORADOR CO JOIN TOKEN_AUTENTICACAO TA ON CO.CPF = TA.CPF_COLABORADOR " +
                    "WHERE TA.TOKEN = ?)," +
                    "   ?)");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca().toUpperCase());
            stmt.setObject(3, item.getDataViagem());
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setBigDecimal(7, item.getRaioKm());
            stmt.setBigDecimal(8, item.getToneladas());
            stmt.setString(9, token);
            stmt.setString(10, token);
            stmt.setLong(11, item.getCodUnidade());
            if (stmt.executeUpdate() == 0) {
                throw new Throwable("Erro ao inserir item na tabela produtividade");
            }
        } finally {
            close(stmt);
        }
    }

    private boolean updateRaizenProdutividadeUpload(@NotNull final Connection conn,
                                                    @NotNull final String token,
                                                    @NotNull final RaizenProdutividadeItemInsert item) throws Throwable {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("UPDATE RAIZEN.PRODUTIVIDADE SET CPF_MOTORISTA = ?," +
                    "   PLACA = ?," +
                    "   DATA_VIAGEM = ?," +
                    "   VALOR = ?," +
                    "   USINA = ?," +
                    "   FAZENDA = ?," +
                    "   RAIO_KM = ?," +
                    "   TONELADAS = ?, " +
                    "   COD_COLABORADOR_ALTERACAO = " +
                    "(SELECT CO.CODIGO FROM COLABORADOR CO JOIN TOKEN_AUTENTICACAO TA ON CO.CPF = TA.CPF_COLABORADOR " +
                    "WHERE TA.TOKEN = ?) " +
                    "WHERE CPF_MOTORISTA = ?" +
                    "AND PLACA = ?" +
                    "AND DATA_VIAGEM = ?" +
                    "AND COD_UNIDADE = ?");
            stmt.setLong(1, item.getCpfMotorista());
            stmt.setString(2, item.getPlaca());
            stmt.setObject(3, item.getDataViagem());
            stmt.setBigDecimal(4, item.getValor());
            stmt.setString(5, item.getUsina());
            stmt.setString(6, item.getFazenda());
            stmt.setBigDecimal(7, item.getRaioKm());
            stmt.setBigDecimal(8, item.getToneladas());
            stmt.setString(9, token);
            stmt.setLong(10, item.getCpfMotorista());
            stmt.setString(11, item.getPlaca());
            stmt.setObject(12, item.getDataViagem());
            stmt.setLong(13, item.getCodUnidade());
            // True se o item foi atualizado.
            return stmt.executeUpdate() != 0;
        } finally {
            close(stmt);
        }
    }
}