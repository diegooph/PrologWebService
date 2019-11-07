package br.com.zalf.prolog.webservice.gente.prontuarioCondutor;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.gente.prontuarioCondutor.model.ProntuarioCondutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Created by Zart on 03/07/2017.
 */
public final class ProntuarioCondutorService {
    private static final String TAG = ProntuarioCondutorService.class.getSimpleName();
    @NotNull
    private final ProntuarioCondutorDao dao = Injection.provideProntuarioCondutorDao();

    @NotNull
    public ProntuarioCondutor getProntuario(@NotNull final Long cpf) {
        try {
            return dao.getProntuario(cpf);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o prontuário do colaborador.\n" +
                    "cpf: %d", cpf), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar prontuário do condutor, tenve novamente");
        }
    }

    @Nullable
    public Double getPontuacaoProntuario(@NotNull final Long cpf) {
        try {
            return dao.getPontuacaoProntuario(cpf);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar a pontuação do prontuário do colaborador.\n" +
                    "cpf: %d", cpf), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar pontuação do prontuário, tenve novamente");
        }
    }

    @NotNull
    public List<ProntuarioCondutor> getResumoProntuarios(@NotNull final Long codUnidade,
                                                         @NotNull final String codEquipe) {
        try {
            return dao.getResumoProntuarios(codUnidade, codEquipe);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar o resumo dos prontuários.\n" +
                    "codUnidade: %d\n" +
                    "codEquipe: %s", codUnidade, codEquipe), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar resumo dos prontuários, tenve novamente");
        }
    }

    public void insertOrUpdate(@NotNull final String path) {
        try {
            dao.insertOrUpdate(path);
        } catch (final SQLException e) {
            Log.e(TAG, "Erro relacionado ao banco de dados ao inserir ou atualizar o prontuário", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro ao inserir as informações do arquivo do arquivo no banco de dados");
        } catch (final IOException e) {
            Log.e(TAG, "Erro relacionado ao processamento do arquivo ao inserir ou atualizar o prontuário", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro relacionado ao processamento do arquivo");
        } catch (final ParseException | DateTimeParseException e) {
            Log.e(TAG, "Erro nos dados da planilha ao inserir ou atualizar o prontuário", e);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(e, "Erro nos dados da planilha");
        } catch (final Throwable t) {
            Log.e(TAG, "Erro ao inserir prontuário do condutor", t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao inserir prontuário do condutor");
        }
    }
}