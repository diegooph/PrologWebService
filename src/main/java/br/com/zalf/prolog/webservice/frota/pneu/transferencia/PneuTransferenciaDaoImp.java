package br.com.zalf.prolog.webservice.frota.pneu.transferencia;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.pneu.transferencia.model.realizacao.PneuTransferenciaRealizacao;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 07/12/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuTransferenciaDaoImp implements PneuTransferenciaDao{


    @Override
    public void insertTransferencia(@NotNull final PneuTransferenciaRealizacao pneuTransferenciaRealizacao,
                                    @NotNull final List<String> codPneusCliente) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        Long codTransferencia = null;
        try {
            conn = getConnection();

            stmt = conn.prepareStatement("INSERT INTO PNEU_TRANSFERENCIA " +
                    "(COD_UNIDADE_ORIGEM, COD_UNIDADE_DESTINO, COD_COLABORADOR, DATA_HORA) VALUES (?, ?, ?, ?)");
            stmt.setLong(1, pneuTransferenciaRealizacao.getCodUnidadeOrigem());
            stmt.setLong(2, pneuTransferenciaRealizacao.getCodUnidadeDestino());
            stmt.setLong(3, pneuTransferenciaRealizacao.getCodColaboradorRealizacaoTransferencia());
            stmt.setObject(4, Now.offsetDateTimeUtc());

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                codTransferencia = rSet.getLong("CODIGO");
                insertTransferenciaValores(conn, codPneusCliente, codTransferencia);
            }
            conn.commit();
        } catch (final Throwable e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    private void insertTransferenciaValores(@NotNull final Connection conn,
                                            @NotNull final List<String> codPneusCliente,
                                            @NotNull final Long codTransferencia) throws Throwable {

        final PreparedStatement stmt = conn.prepareStatement("INSERT INTO PNEU_TRANSFERENCIA_VALORES " +
                "(COD_TRANSFERENCIA, COD_PNEU, ALTURA_SULCO_CENTRAL_INTERNO, ALTURA_SULCO_INTERNO, ALTURA_SULCO_EXTERNO," +
                " ALTURA_SULCO_CENTRAL_EXTERNO, PSI, VIDA_MOMENTO_TRANSFERENCIA) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");


    }

}