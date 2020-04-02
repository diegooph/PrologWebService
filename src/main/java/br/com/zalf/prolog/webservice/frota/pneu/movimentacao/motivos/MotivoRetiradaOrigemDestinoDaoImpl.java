package br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos;

import br.com.zalf.prolog.webservice.commons.util.date.Now;
import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.frota.pneu._model.StatusPneu;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao._model.OrigemDestinoEnum;
import br.com.zalf.prolog.webservice.frota.pneu.movimentacao.motivos._model.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2020-03-20
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class MotivoRetiradaOrigemDestinoDaoImpl extends DatabaseConnection implements
        MotivoRetiradaOrigemDestinoDao {

    @Override
    public void insert(@NotNull final List<MotivoRetiradaOrigemDestinoInsercao> unidades,
                       @NotNull final Long codigoColaboradorInsercao) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_ORIGEM_DESTINO_INSERE(" +
                    "F_COD_MOTIVO := ?," +
                    "F_COD_EMPRESA := ?," +
                    "F_COD_UNIDADE :=?," +
                    "F_ORIGEM := ?::origem_destino_type," +
                    "F_DESTINO := ?::origem_destino_type," +
                    "F_OBRIGATORIO := ?," +
                    "F_DATA_HORA_INSERCAO := ?," +
                    "F_COD_COLABORADOR_INSERCAO := ?)" +
                    "AS COD_MOTIVO_ORIGEM_DESTINO;");

            stmt.setObject(7, Now.offsetDateTimeUtc());
            stmt.setLong(8, codigoColaboradorInsercao);

            int totalInserts = 0;
            for (final MotivoRetiradaOrigemDestinoInsercao unidade : unidades) {
                stmt.setLong(2, unidade.getCodEmpresa());
                stmt.setLong(3, unidade.getCodUnidade());

                for (final MotivoRetiradaOrigemDestinoMotivosResumido origemDestino : unidade.getOrigensDestinos()) {
                    stmt.setString(4, origemDestino.getOrigem().asString());
                    stmt.setString(5, origemDestino.getDestino().asString());
                    stmt.setBoolean(6, origemDestino.isObrigatorio());

                    for (final Long codMotivo : origemDestino.getCodMotivos()) {
                        stmt.setLong(1, codMotivo);
                        stmt.addBatch();
                        totalInserts++;
                    }
                }
            }

            if (stmt.executeBatch().length != totalInserts) {
                throw new SQLException("Erro ao inserir relação de origem e destino com motivo");
            }
        } finally {
            close(conn, stmt);
        }
    }

    @NotNull
    @Override
    public MotivoRetiradaOrigemDestinoVisualizacao getMotivoOrigemDestino(@NotNull final Long codMotivoOrigemDestino,
                                                                          @NotNull final ZoneId timeZone)
            throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_ORIGEM_DESTINO_VISUALIZACAO(" +
                    "F_COD_MOTIVO_ORIGEM_DESTINO := ?," +
                    "F_TIME_ZONE := ?);");
            stmt.setLong(1, codMotivoOrigemDestino);
            stmt.setString(2, timeZone.toString());
            rSet = stmt.executeQuery();
            if (rSet.next()) {
                return MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaOrigemDestinoVisualizacao(rSet);
            } else {
                throw new IllegalStateException("Nenhuma relação motivo, origem e destino foi encontrada com o código: "
                        + codMotivoOrigemDestino);
            }
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<MotivoRetiradaOrigemDestinoListagem> getMotivosOrigemDestino(
            @NotNull final Long codColaborador) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_ORIGEM_DESTINO_LISTAGEM(" +
                    "F_COD_COLABORADOR := ?)");
            stmt.setLong(1, codColaborador);
            rSet = stmt.executeQuery();

            final List<MotivoRetiradaOrigemDestinoListagem> unidades = new ArrayList<>();
            while (rSet.next()) {
                if (unidades.isEmpty() || unidades.get(unidades.size() - 1).getCodUnidade() != rSet.getLong("codigo_unidade")) {
                    unidades.add(MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaOrigemDestinoListagem(rSet));
                } else {
                    final MotivoRetiradaOrigemDestinoListagem ultimaUnidade = unidades.get(unidades.size() - 1);
                    final List<MotivoRetiradaOrigemDestinoListagemMotivos> rotasUltimaUnidade = ultimaUnidade
                            .getOrigensDestinos();
                    final List<MotivoRetiradaListagemResumida> ultimaListaMotivosRetirada =
                            rotasUltimaUnidade.get(rotasUltimaUnidade.size() - 1).getMotivosRetirada();

                    if (rotasUltimaUnidade.get(rotasUltimaUnidade.size() - 1).getOrigemMovimento()
                            !=
                            OrigemDestinoEnum.getFromStatusPneu(
                                    StatusPneu.fromString(rSet.getString("origem_movimento")))
                            ||
                            rotasUltimaUnidade.get(rotasUltimaUnidade.size() - 1).getDestinoMovimento()
                                    !=
                                    OrigemDestinoEnum.getFromStatusPneu(
                                            StatusPneu.fromString(rSet.getString("destino_movimento")))) {

                        rotasUltimaUnidade.add(MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaOrigemDestinoListagemMotivos(rSet));
                    } else {
                        ultimaListaMotivosRetirada.add(MotivoRetiradaOrigemDestinoConverter.createMotivoRetiradaListagem(rSet));
                    }
                }
            }
            return unidades;
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public MotivoRetiradaOrigemDestinoListagemMotivos getMotivosByOrigemAndDestinoAndUnidade(
            @NotNull final OrigemDestinoEnum origemMovimento,
            @NotNull final OrigemDestinoEnum destinoMovimento,
            @NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_GET_BY_ORIGEM_DESTINO(" +
                    "F_ORIGEM := ?::ORIGEM_DESTINO_TYPE," +
                    "F_DESTINO := ?::ORIGEM_DESTINO_TYPE," +
                    "F_COD_UNIDADE := ?);");
            stmt.setString(1, origemMovimento.asString());
            stmt.setString(2, destinoMovimento.asString());
            stmt.setLong(3, codUnidade);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<MotivoRetiradaListagemResumida> motivos = new ArrayList<>();
                boolean obrigatorioMotivoRetirada;
                do {
                    motivos.add(MotivoRetiradaConverter.createMotivoRetiradaListagemResumida(rSet));
                    obrigatorioMotivoRetirada = rSet.getBoolean("OBRIGATORIO");
                } while (rSet.next());
                return new MotivoRetiradaOrigemDestinoListagemMotivos(
                        origemMovimento,
                        destinoMovimento,
                        motivos,
                        obrigatorioMotivoRetirada);
            }

            // Se a unidade não possuir relação para a origem e destino informados, retornará lista vazia com
            // obrigatório null.
            return new MotivoRetiradaOrigemDestinoListagemMotivos(
                    origemMovimento,
                    destinoMovimento,
                    Collections.emptyList(),
                    null);
        } finally {
            close(conn, stmt, rSet);
        }
    }

    @NotNull
    @Override
    public List<OrigemDestinoListagem> getRotasExistentesByUnidade(@NotNull final Long codUnidade) throws Throwable {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rSet = null;
        try {
            conn = getConnection();
            stmt = conn.prepareStatement("SELECT * FROM FUNC_MOTIVO_RETIRADA_GET_ORIGEM_DESTINO_BY_UNIDADE(" +
                    "F_COD_UNIDADE := ?);");
            stmt.setLong(1, codUnidade);

            rSet = stmt.executeQuery();
            if (rSet.next()) {
                final List<OrigemDestinoListagem> origensDestinos = new ArrayList<>();
                do {
                    origensDestinos.add(MotivoRetiradaOrigemDestinoConverter.createOrigemDestinoListagem(rSet));
                } while (rSet.next());
                return origensDestinos;
            }
            return Collections.emptyList();
        } finally {
            close(conn, stmt, rSet);
        }
    }

}
