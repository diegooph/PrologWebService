package br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.PneuDao;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.pneu.pneutiposervico._model.PneuServicoRealizadoIncrementaVida;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 05/06/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class PneuServicoRealizadoDaoImpl extends DatabaseConnection implements PneuServicoRealizadoDao {

    public PneuServicoRealizadoDaoImpl() {
    }

    @NotNull
    @Override
    public Long insertServicoByMovimentacao(
            @NotNull final Connection conn,
            @NotNull final PneuDao pneuDao,
            @NotNull final Long codUnidade,
            @NotNull final Pneu pneu,
            @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable {
        final Long codServicoRealizado = insertPneuServicoRealizado(
                conn,
                codUnidade,
                pneu.getCodigo(),
                servicoRealizado,
                PneuServicoRealizado.FONTE_MOVIMENTACAO);
        if (servicoRealizado instanceof PneuServicoRealizadoIncrementaVida) {
            insertPneuServicoRealizadoIncrementaVida(
                    conn,
                    codServicoRealizado,
                    (PneuServicoRealizadoIncrementaVida) servicoRealizado,
                    PneuServicoRealizado.FONTE_MOVIMENTACAO);

            // Ao realizar um serviço que incrementa a vida do Pneu, precisamos alterar essa
            // mudança na Tabela PNEU para que seja refletida em banco.
            incrementaVidaPneu(conn, pneuDao, pneu, (PneuServicoRealizadoIncrementaVida) servicoRealizado);
        }
        return codServicoRealizado;
    }

    @NotNull
    @Override
    public Long insertServicoByPneuCadastro(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final PneuServicoRealizado servicoRealizado) throws Throwable {
        final Long codServicoRealizado = insertPneuServicoRealizado(
                conn,
                codUnidade,
                codPneu,
                servicoRealizado,
                PneuServicoRealizado.FONTE_CADASTRO);
        if (servicoRealizado instanceof PneuServicoRealizadoIncrementaVida) {
            insertPneuServicoRealizadoIncrementaVida(
                    conn,
                    codServicoRealizado,
                    (PneuServicoRealizadoIncrementaVida) servicoRealizado,
                    PneuServicoRealizado.FONTE_CADASTRO);
        }
        return codServicoRealizado;
    }

    @NotNull
    private Long insertPneuServicoRealizado(@NotNull final Connection conn,
                                            @NotNull final Long codUnidade,
                                            @NotNull final Long codPneu,
                                            @NotNull final PneuServicoRealizado servicoRealizado,
                                            @NotNull final String fonteServicoRealizado) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PNEU_SERVICO_REALIZADO " +
                    "(COD_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, FONTE_SERVICO_REALIZADO) " +
                    "VALUES (?, ?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, servicoRealizado.getCodPneuTipoServico());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPneu);
            stmt.setBigDecimal(4, servicoRealizado.getCusto());
            stmt.setInt(5, servicoRealizado.getVidaMomentoRealizacaoServico());
            stmt.setString(6, fonteServicoRealizado);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir o serviço realizado no pneu: " + codPneu);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }

    private void insertPneuServicoRealizadoIncrementaVida(
            @NotNull final Connection conn,
            @NotNull final Long codServicoRealizado,
            @NotNull final PneuServicoRealizadoIncrementaVida servicoIncrementaVida,
            @NotNull final String fonteServicoRealizado) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO PNEU_SERVICO_REALIZADO_INCREMENTA_VIDA " +
                    "(COD_SERVICO_REALIZADO, COD_MODELO_BANDA, VIDA_NOVA_PNEU, FONTE_SERVICO_REALIZADO) " +
                    "VALUES (?, ?, ?, ?);");
            stmt.setLong(1, codServicoRealizado);
            stmt.setLong(2, servicoIncrementaVida.getCodModeloBanda());
            stmt.setInt(3, servicoIncrementaVida.getVidaNovaPneu());
            stmt.setString(4, fonteServicoRealizado);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir o servico de recapagem realizado no pneu: ");
            }
        } finally {
            closeStatement(stmt);
        }
    }

    private void incrementaVidaPneu(@NotNull final Connection conn,
                                    @NotNull final PneuDao pneuDao,
                                    @NotNull final Pneu pneu,
                                    @NotNull final PneuServicoRealizadoIncrementaVida servicoIncrementaVida)
            throws Throwable {
        pneuDao.incrementaVidaPneu(
                conn,
                pneu.getCodigo(),
                servicoIncrementaVida.getCodModeloBanda());
        // Desse modo garantimos que o objeto Pneu reflete o estado do pneu em banco.
        pneu.incrementaVida();
    }
}