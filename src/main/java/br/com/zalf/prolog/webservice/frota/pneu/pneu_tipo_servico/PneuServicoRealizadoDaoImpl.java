package br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuServicoRealizado;
import br.com.zalf.prolog.webservice.frota.pneu.pneu_tipo_servico.model.PneuServicoRealizadoIncrementaVida;
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

    @Override
    public Long insertServicoByMovimentacao(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final PneuServicoRealizado servicoRealizado) throws SQLException {
        return internalInsert(conn, codUnidade, codPneu, servicoRealizado, PneuServicoRealizado.FONTE_MOVIMENTACAO);
    }

    @Override
    public Long insertServicoByPneuCadastro(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final PneuServicoRealizado servicoRealizado) throws SQLException {
        return internalInsert(conn, codUnidade, codPneu, servicoRealizado, PneuServicoRealizado.FONTE_CADASTRO);
    }

    @NotNull
    private Long internalInsert(@NotNull final Connection conn,
                                @NotNull final Long codUnidade,
                                @NotNull final Long codPneu,
                                @NotNull final PneuServicoRealizado servicoRealizado,
                                @NotNull final String fonteServicoRealizado) throws SQLException {
        final Long codServicoRealizado =
                insertPneuServicoRealizado(conn, codUnidade, codPneu, servicoRealizado, fonteServicoRealizado);
        if (servicoRealizado instanceof PneuServicoRealizadoIncrementaVida) {
            insertPneuServicoRealizadoIncrementaVida(
                    conn,
                    codServicoRealizado,
                    (PneuServicoRealizadoIncrementaVida) servicoRealizado,
                    fonteServicoRealizado);
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
                    "(COD_PNEU_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, CUSTO, VIDA, FONTE_SERVICO_REALIZADO) " +
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
                throw new SQLException("Não foi possível inserir o servico realizado no pneu: " + codPneu);
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
            stmt = conn.prepareStatement("INSERT INTO PNEU_SERVICO_REALIZADO_RECAPAGEM " +
                    "(COD_PNEU_SERVICO_REALIZADO, COD_MODELO_BANDA, VIDA_NOVA_PNEU, FONTE_SERVICO_REALIZADO) " +
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
}
