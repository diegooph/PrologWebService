package br.com.zalf.prolog.webservice.frota.pneu.nomenclatura;

import br.com.zalf.prolog.webservice.TimeZoneManager;
import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItemVisualizacao;
import br.com.zalf.prolog.webservice.frota.pneu.nomenclatura.model.PneuNomenclaturaItem;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static br.com.zalf.prolog.webservice.database.DatabaseConnection.close;
import static br.com.zalf.prolog.webservice.database.DatabaseConnection.getConnection;

/**
 * Created on 10/09/19.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class PneuNomenclaturaDaoImpl implements PneuNomenclaturaDao {

    @Override
    public void insertOrUpdateNomenclatura(@NotNull final List<PneuNomenclaturaItem> pneuNomenclaturaItens,
                                           @NotNull final String userToken) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        if (!pneuNomenclaturaItens.isEmpty()) {
            final Long posicaoInicialEstepe = 900L;
            int qtdObjetosValidos = 0;
            for (final PneuNomenclaturaItem pneuNomenclaturaItensValidos : pneuNomenclaturaItens ){
                if (pneuNomenclaturaItensValidos.getPosicaoProlog() < posicaoInicialEstepe){
                    qtdObjetosValidos++;
                }
            }
            final int indexPrimeiroObjeto = 0;
            try {
                conn = getConnection();
                final boolean nomenclaturaCompleta = confereNomenclaturaCompleta(conn, pneuNomenclaturaItens.get(indexPrimeiroObjeto).getCodDiagrama(), qtdObjetosValidos);
                if (nomenclaturaCompleta) {
                    try {
                        int linhasParaExecutar = 0;
                        stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_NOMENCLATURA_INSERE_EDITA_NOMENCLATURA(" +
                                "F_COD_DIAGRAMA := ?, " +
                                "F_COD_EMPRESA   := ?, " +
                                "F_POSICAO_PROLOG  := ?, " +
                                "F_NOMENCLATURA:= ?," +
                                "F_TOKEN_RESPONSAVEL_INSERCAO := ?," +
                                "F_DATA_HORA_CADASTRO := ?);");
                        for (final PneuNomenclaturaItem pneuNomenclaturaItem : pneuNomenclaturaItens) {
                            final ZoneId unidadeZoneId = TimeZoneManager.getZoneIdForCodUnidade(pneuNomenclaturaItem.getCodUnidade(), conn);
                            final LocalDateTime dataHoraCadastro = Now.localDateTimeUtc();
                            stmt.setLong(1, pneuNomenclaturaItem.getCodDiagrama());
                            stmt.setLong(2, pneuNomenclaturaItem.getCodEmpresa());
                            stmt.setLong(3, pneuNomenclaturaItem.getPosicaoProlog());
                            stmt.setString(4, pneuNomenclaturaItem.getNomenclatura());
                            stmt.setString(5, userToken);
                            stmt.setObject(6, dataHoraCadastro.atZone(unidadeZoneId).toOffsetDateTime());
                            stmt.addBatch();
                            linhasParaExecutar++;
                        }
                        final int[] batch = stmt.executeBatch();
                        if (batch.length != linhasParaExecutar) {
                            throw new SQLException("Não foi possível salvar todos as nomenclaturas");
                        }
                        if (batch.length == 0) {
                            throw new Throwable("Erro ao inserir nomenclatura");
                        }
                    } finally {
                        close(stmt);
                    }
                } else {
                    throw new SQLException("Nomenclatura incompleta");
                }
            } finally {
                close(conn);
            }
        }else{
            throw new Throwable("Sem informações de nomenclatura");
        }
    }

    @Override
    public List<PneuNomenclaturaItemVisualizacao> getPneuNomenclaturaItemVisualizacao(
            @NotNull final Long codEmpresa,
            @NotNull final Long codDiagrama) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        final List<PneuNomenclaturaItemVisualizacao> nomenclaturas = new ArrayList<>();
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_PNEU_NOMENCLATURA_GET_NOMENCLATURA(" +
                    "F_COD_EMPRESA        := ?," +
                    "F_COD_DIAGRAMA    := ?);");
            stmt.setLong(1, codEmpresa);
            stmt.setLong(2, codDiagrama);
            rSet = stmt.executeQuery();
            while (rSet.next()) {
                nomenclaturas.add(createNomenclatura(rSet));
            }
        } finally {
            close(conn, stmt, rSet);
        }
        return nomenclaturas;
    }

    @NotNull
    private PneuNomenclaturaItemVisualizacao createNomenclatura(@NotNull final ResultSet rSet) throws SQLException {
        final PneuNomenclaturaItemVisualizacao nomenclatura = new PneuNomenclaturaItemVisualizacao();
        nomenclatura.setNomenclatura(rSet.getString("NOMENCLATURA"));
        nomenclatura.setPosicaoProlog(rSet.getInt("POSICAO_PROLOG"));
        return nomenclatura;
    }

    private boolean confereNomenclaturaCompleta(@NotNull final Connection conn,
                                                @NotNull final Long codDiagrama,
                                                @NotNull final int qtdObjetos) throws Throwable {
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            stmt = conn.prepareStatement("SELECT * FROM FUNC_VERIFICA_PNEU_NOMENCLATURA_COMPLETA(" +
                    "F_COD_DIAGRAMA := ?," +
                    "F_QTD_OBJETOS := ?) AS NOMENCLATURA_COMPLETA;");
            stmt.setLong(1, codDiagrama);
            stmt.setInt(2, qtdObjetos);
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return rSet.getBoolean("NOMENCLATURA_COMPLETA");
            } else {
                throw new IllegalStateException("Erro ao verificar se a nomenclatura estava completa");
            }
        } finally {
            close(stmt, rSet);
        }
    }
}
