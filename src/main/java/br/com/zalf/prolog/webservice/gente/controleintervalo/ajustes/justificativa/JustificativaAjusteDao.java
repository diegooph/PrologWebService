package br.com.zalf.prolog.webservice.gente.controleintervalo.ajustes.justificativa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created on 05/09/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface JustificativaAjusteDao {

    /**
     * Método utilizado para inserir uma {@link JustificativaAjuste}. Uma {@link JustificativaAjuste}
     * é uma opção que o usuário seleciona ao editar uma marcação de jornada.
     *
     * @param token               Identificador do usuário que está solicitando a operação.
     * @param justificativaAjuste A {@link JustificativaAjuste} a ser inserida.
     * @return O código da {@link JustificativaAjuste} criada.
     * @throws Throwable Caso algum erro no processamento ou conexão com o Banco de Dados ocorrer.
     */
    @NotNull
    Long insertJustificativaAjuste(@NotNull final String token,
                                   @NotNull final JustificativaAjuste justificativaAjuste) throws Throwable;

    void atualizaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                     @NotNull final String token) throws Throwable;

    @NotNull
    List<JustificativaAjuste> getJustificativasAjuste(@NotNull final Long codEmpresa,
                                                      @Nullable final Boolean ativas) throws Throwable;

    @NotNull
    JustificativaAjuste getJustificativaAjuste(@NotNull final Long codEmpresa,
                                               @NotNull final Long codJustificativaAjuste) throws Throwable;

    void ativaInativaJustificativaAjuste(@NotNull final JustificativaAjuste justificativaAjuste,
                                         @NotNull final String token) throws Throwable;
}