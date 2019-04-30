package br.com.zalf.prolog.webservice.frota.veiculo.transferencia;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.ProcessoTransferenciaVeiculoRealizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.transferencia.realizacao.VeiculoEnvioTransferencia;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created on 29/04/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class VeiculoTransferenciaDaoImpl extends DatabaseConnection implements VeiculoTransferenciaDao {
    @NotNull
    @Override
    public Long insertProcessoTranseferenciaVeiculo(
            @NotNull final ProcessoTransferenciaVeiculoRealizacao processoTransferenciaVeiculo) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement("INSERT INTO VEICULO_TRANSFERENCIA_PROCESSO(" +
                    "  COD_UNIDADE_ORIGEM," +
                    "  COD_UNIDADE_DESTINO," +
                    "  COD_UNIDADE_COLABORADOR," +
                    "  COD_COLABORADOR_REALIZACAO," +
                    "  DATA_HORA_TRANSFERENCIA_PROCESSO," +
                    "  OBSERVACAO)" +
                    " VALUES (?, ?, (SELECT C.COD_UNIDADE FROM COLABORADOR C WHERE C.CODIGO = ?), ?, ?, ?)" +
                    " RETURNING CODIGO;");
            stmt.setLong(1, processoTransferenciaVeiculo.getCodUnidadeOrigem());
            stmt.setLong(2, processoTransferenciaVeiculo.getCodUnidadeDestino());
            stmt.setLong(3, processoTransferenciaVeiculo.getCodColaboradorRealizacaoTransferencia());
            stmt.setLong(4, processoTransferenciaVeiculo.getCodColaboradorRealizacaoTransferencia());
            stmt.setObject(5, Now.offsetDateTimeUtc());
            stmt.setString(6, processoTransferenciaVeiculo.getObservacao());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final long codProcessoTransferencia = rSet.getLong("CODIGO");
                final List<VeiculoEnvioTransferencia> veiculosTransferencia =
                        processoTransferenciaVeiculo.getVeiculosTransferencia();
                for (final VeiculoEnvioTransferencia veiculoEnvioTransferencia : veiculosTransferencia) {

                }
                if (codProcessoTransferencia <= 0) {
                    throw new SQLException("Erro ao inserir processo de transferência:\n" +
                            "codProcessoTransferencia: " + codProcessoTransferencia);
                }
                conn.commit();
                return codProcessoTransferencia;
            } else {
                throw new SQLException("Não foi possível salvar processo de transferência de veículo");
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }
}
