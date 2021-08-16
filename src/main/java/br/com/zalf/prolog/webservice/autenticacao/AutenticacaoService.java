package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.autenticacao._model.AutenticacaoLogin;
import br.com.zalf.prolog.webservice.autenticacao._model.AutenticacaoResponse;
import br.com.zalf.prolog.webservice.autenticacao._model.token.TokenGenerator;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.datetime.PrologDateParser;
import br.com.zalf.prolog.webservice.interceptors.auth.ColaboradorAutenticado;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

public class AutenticacaoService {
    private static final String TAG = AutenticacaoService.class.getSimpleName();
    private final AutenticacaoDao dao = Injection.provideAutenticacaoDao();

    public boolean delete(@NotNull final String token) {
        try {
            return dao.delete(token);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao deletar o token: %s", token), throwable);
            return false;
        }
    }

    @NotNull
    public Optional<ColaboradorAutenticado> verifyIfTokenExists(@NotNull final String token,
                                                                final boolean apenasUsuariosAtivos) {
        try {
            return dao.verifyIfTokenExists(token, apenasUsuariosAtivos);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao verificar se o token existe: %s", token), throwable);
            // Optamos por não tratar com o handler do Prolog, pois não queremos mandar nenhuma mensagem
            // aos usuários nesse caso.
            throw new RuntimeException(throwable);
        }
    }

    @NotNull
    public AutenticacaoResponse authenticate(@NotNull final Long cpf,
                                             @NotNull final String dataNascimento) {
        try {
            final LocalDate dataNascimentoLocalDate = PrologDateParser.toLocalDate(dataNascimento);
            final AutenticacaoLogin autenticacaoLogin = dao.authenticate(cpf, dataNascimentoLocalDate);
            autenticacaoLogin.validate();
            final String token = createTokenByCpf(cpf);
            return autenticacaoLogin.toAutenticacaoResponse(token);
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao verificar se o usuário com os seguintes dados existe: cpf - %s |" +
                                             " Data de Nascimento - %s", cpf, dataNascimento), throwable);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(throwable, "Erro ao realizar login, tente novamente.");
        }
    }

    @NotNull
    public Optional<ColaboradorAutenticado> userHasPermission(@NotNull final String token,
                                                              @NotNull final int[] permissions,
                                                              final boolean needsToHaveAllPermissions,
                                                              final boolean apenasUsuariosAtivos) {
        try {
            return dao.userHasPermission(token, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
        } catch (final Throwable t) {
            Log.e(TAG,
                  String.format("Erro ao verificar se o usuário com o token: %s tem acesso as permissões: %s |" +
                                        " needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b",
                                token,
                                Arrays.toString(permissions),
                                needsToHaveAllPermissions,
                                apenasUsuariosAtivos), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao validar permissões");
        }
    }

    @NotNull
    public Optional<ColaboradorAutenticado> userHasPermission(final long cpf,
                                                              @NotNull final String dataNascimento,
                                                              @NotNull final int[] permissions,
                                                              final boolean needsToHaveAllPermissions,
                                                              final boolean apenasUsuariosAtivos) {
        try {
            return dao.userHasPermission(
                    cpf,
                    PrologDateParser.toLocalDate(dataNascimento),
                    permissions,
                    needsToHaveAllPermissions,
                    apenasUsuariosAtivos);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao verificar se o usuário com o cpf/Nascimento: %d/%s tem acesso as " +
                                             "permissões: %s | needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b",
                                     cpf,
                                     dataNascimento,
                                     Arrays.toString(permissions),
                                     needsToHaveAllPermissions,
                                     apenasUsuariosAtivos), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao validar permissões");
        }
    }

    @NotNull
    String createTokenByCpf(@NotNull final Long cpf) throws Throwable {
        try {
            final String token = new TokenGenerator().getNextToken();
            dao.insertTokenForCpf(cpf, token);
            return token;
        } catch (final Throwable throwable) {
            Log.e(TAG, String.format("Erro ao inserir o token para o cpf: %d", cpf), throwable);
            throw throwable;
        }
    }
}
