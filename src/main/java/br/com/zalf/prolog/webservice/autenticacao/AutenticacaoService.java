package br.com.zalf.prolog.webservice.autenticacao;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.ProLogDateParser;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Classe AutenticacaoService responsavel por comunicar-se com a interface DAO.
 */
public class AutenticacaoService {
    private static final String TAG = AutenticacaoService.class.getSimpleName();
    private final AutenticacaoDao dao = Injection.provideAutenticacaoDao();

    @NotNull
    Autenticacao insertOrUpdate(@NotNull final Long cpf) {
        try {
            return dao.insertOrUpdate(cpf);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao inserir o token para o cpf: %d", cpf), e);
            return new Autenticacao(Autenticacao.ERROR, cpf, "-1");
        }
    }

    public boolean delete(@NotNull final String token) {
        try {
            return dao.delete(token);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao deletar o token: %s", token), e);
            return false;
        }
    }

    public boolean verifyIfTokenExists(@NotNull final String token, final boolean apenasUsuariosAtivos) {
        try {
            return dao.verifyIfTokenExists(token, apenasUsuariosAtivos);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao verificar se o token existe: %s", token), e);
            return false;
        }
    }

    public boolean verifyIfUserExists(@NotNull final Long cpf,
                                      @NotNull final String dataNascimento,
                                      final boolean apenasUsuariosAtivos) {
        try {
            return dao.verifyIfUserExists(
                    cpf,
                    ProLogDateParser.toLocalDate(dataNascimento),
                    apenasUsuariosAtivos);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao verificar se o usuário com os seguintes dados existe: cpf - %s |" +
                    " Data de Nascimento - %s", cpf, dataNascimento), e);
            return false;
        }
    }

    public boolean userHasPermission(@NotNull final String token,
                                     @NotNull final int[] permissions,
                                     final boolean needsToHaveAllPermissions,
                                     final boolean apenasUsuariosAtivos) {
        try {
            return dao.userHasPermission(token, permissions, needsToHaveAllPermissions, apenasUsuariosAtivos);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao verificar se o usuário com o token: %s tem acesso as permissões: %s |" +
                            " needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b", token, Arrays.toString(permissions),
                    needsToHaveAllPermissions, apenasUsuariosAtivos), e);
            return false;
        }
    }

    public boolean userHasPermission(final long cpf,
                                     @NotNull final String dataNascimento,
                                     @NotNull final int[] permissions,
                                     final boolean needsToHaveAllPermissions,
                                     final boolean apenasUsuariosAtivos) {
        try {
            return dao.userHasPermission(
                    cpf,
                    ProLogDateParser.toLocalDate(dataNascimento),
                    permissions,
                    needsToHaveAllPermissions,
                    apenasUsuariosAtivos);
        } catch (SQLException e) {
            Log.e(TAG, String.format("Erro ao verificar se o usuário com o cpf/Nascimento: %d / %s tem acesso as permissões: %s |" +
                            " needsToHaveAllPermissions/apenasUsuariosAtivos: %b/%b", cpf, dataNascimento, Arrays.toString(permissions),
                    needsToHaveAllPermissions, apenasUsuariosAtivos), e);
            return false;
        }
    }
}
