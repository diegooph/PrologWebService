package br.com.zalf.prolog.webservice.integracao.rodoparhorizonte;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.colaborador.model.Colaborador;
import br.com.zalf.prolog.webservice.database.DatabaseConnectionProvider;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.Afericao;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.AfericaoAvulsa;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.AfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.IntegradorProLog;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.ProtheusRodalogCredentialCreator;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.data.RodoparHorizonteRequester;
import br.com.zalf.prolog.webservice.integracao.rodoparhorizonte.model.token.RodoparHorizonteTokenIntegracao;
import br.com.zalf.prolog.webservice.integracao.sistema.Sistema;
import br.com.zalf.prolog.webservice.integracao.sistema.SistemaKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

/**
 * Created on 25/05/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class SistemaRodoparHorizonte extends Sistema {
    @NotNull
    private final RodoparHorizonteRequester requester;

    public SistemaRodoparHorizonte(@NotNull final RodoparHorizonteRequester requester,
                                   @NotNull final IntegradorProLog integradorProLog,
                                   @NotNull final SistemaKey sistemaKey,
                                   @NotNull final String userToken) {
        super(integradorProLog, sistemaKey, userToken);
        this.requester = requester;
    }

    @Nullable
    @Override
    public Long insertAfericao(@NotNull final Long codUnidade, @NotNull final Afericao afericao) throws Throwable {
        Connection conn = null;
        try {
            conn = new DatabaseConnectionProvider().provideDatabaseConnection();
            conn.setAutoCommit(false);
            final Colaborador colaboradorRequisicao = getIntegradorProLog().getColaboradorByToken(getUserToken());
            final RodoparHorizonteTokenIntegracao tokenIntegracao =
                    requester.getTokenUsuarioIntegracao(
                            ProtheusRodalogCredentialCreator.createCredentials(colaboradorRequisicao));
            final Long codAfericaoInserida = Injection.provideAfericaoDao().insert(conn, codUnidade, afericao);

            if (afericao instanceof AfericaoPlaca) {
                requester.insertAfericaoPlaca(
                        tokenIntegracao.getToken(),
                        RodoparHorizonteConverter.convert(codUnidade, (AfericaoPlaca) afericao));
            } else {
                requester.insertAfericaoAvulsa(
                        tokenIntegracao.getToken(),
                        RodoparHorizonteConverter.convert(codUnidade, (AfericaoAvulsa) afericao));
            }
            conn.commit();
            return codAfericaoInserida;
        } catch (final Throwable t) {
            if (conn != null) {
                conn.rollback();
            }
            throw t;
        } finally {
            new DatabaseConnectionProvider().closeResources(conn);
        }
    }
}
