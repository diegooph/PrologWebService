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
        if (servicoRealizado instanceof ServicoRealizadoRecapagem) {
            return insertServicoRealizadoRecapagem(
                    conn,
                    codUnidade,
                    codPneu,
                    (ServicoRealizadoRecapagem) servicoRealizado);
        } else {
            return insertServicoRealizado(conn, codUnidade, codPneu, servicoRealizado);
        }
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

    @NotNull
    private Long insertServicoRealizadoRecapagem(
            @NotNull final Connection conn,
            @NotNull final Long codUnidade,
            @NotNull final Long codPneu,
            @NotNull final ServicoRealizadoRecapagem servicoRecapagem) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO SERVICO_REALIZADO_RECAPAGEM(" +
                    "COD_TIPO_SERVICO, COD_UNIDADE, COD_PNEU, VALOR, VIDA, COD_MODELO_BANDA, VIDA_NOVA_PNEU) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING CODIGO;");
            stmt.setLong(1, servicoRecapagem.getCodTipoServicoRecapadora());
            stmt.setLong(2, codUnidade);
            stmt.setLong(3, codPneu);
            stmt.setBigDecimal(4, servicoRecapagem.getValor());
            stmt.setInt(5, servicoRecapagem.getVidaMomentoRealizacaoServico());
            stmt.setLong(6, servicoRecapagem.getCodModeloBanda());
            stmt.setInt(7, servicoRecapagem.getVidaNovaPneu());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getLong("CODIGO");
            } else {
                throw new SQLException("Não foi possível inserir o servico de recapagem realizado no pneu: " + codPneu);
            }
        } finally {
            closeConnection(null, stmt, rSet);
        }
    }
}
