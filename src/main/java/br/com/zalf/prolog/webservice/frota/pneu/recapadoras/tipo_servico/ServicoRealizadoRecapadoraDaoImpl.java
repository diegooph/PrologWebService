package br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapadora;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.tipo_servico.model.ServicoRealizadoRecapagem;
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
public class ServicoRealizadoRecapadoraDaoImpl extends DatabaseConnection implements ServicoRealizadoRecapadoraDao {

    public ServicoRealizadoRecapadoraDaoImpl() {
    }

    @Override
    public Long insert(@NotNull final Connection conn,
                       @NotNull final Long codUnidade,
                       @NotNull final Long codPneu,
                       @NotNull final ServicoRealizadoRecapadora servicoRealizado) throws SQLException {
        final Long codServicoRealizado = insertServicoRealizado(conn, codUnidade, codPneu, servicoRealizado);
        if (servicoRealizado instanceof ServicoRealizadoRecapagem) {
            insertServicoRealizadoRecapagem(conn, codServicoRealizado, (ServicoRealizadoRecapagem) servicoRealizado);
        }
        return codServicoRealizado;
    }

    @NotNull
    private Long insertServicoRealizado(@NotNull final Connection conn,
                                        @NotNull final Long codUnidade,
                                        @NotNull final Long codPneu,
                                        @NotNull final ServicoRealizadoRecapadora servicoRealizado) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO SERVICO_REALIZADO_RECAPADORA(" +
                    "COD_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, VALOR, VIDA) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, servicoRealizado.getCodTipoServicoRecapadora());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPneu);
            stmt.setBigDecimal(4, servicoRealizado.getValor());
            stmt.setInt(5, servicoRealizado.getVidaMomentoRealizacaoServico());
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

    private void insertServicoRealizadoRecapagem(
            @NotNull final Connection conn,
            @NotNull final Long codServicoRealizado,
            @NotNull final ServicoRealizadoRecapagem servicoRecapagem) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO SERVICO_REALIZADO_RECAPAGEM(" +
                    "COD_SERVICO_REALIZADO_RECAPADORA, COD_MODELO_BANDA, VIDA_NOVA_PNEU) " +
                    "VALUES (?, ?, ?);");
            stmt.setLong(1, codServicoRealizado);
            stmt.setLong(2, servicoRecapagem.getCodModeloBanda());
            stmt.setInt(3, servicoRecapagem.getVidaNovaPneu());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Não foi possível inserir o servico de recapagem realizado no pneu: ");
            }
        } finally {
            closeStatement(stmt);
        }
    }
}
