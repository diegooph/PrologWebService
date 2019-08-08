package br.com.zalf.prolog.webservice.integracao.api.checklist;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created on 07/08/19.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface ApiChecklistDao {
    /**
     * Método utilizado para listar as alternativas dos modelos de checklists.
     * <p>
     * Esse método irá retornar todas as alternativas da empresa a qual o {@code tokenIntegracao} pertence.
     * <p>
     * Para buscar apenas alternativas ativas, <code>apenasAlternativasAtivas = true</code>;
     * Para buscar apenas perguntas ativas, <code>apenasPerguntasAtivas = true</code>;
     * Para buscar apenas modelos ativos, <code>apenasModelosAtivos = true</code>;
     *
     * @param tokenIntegracao          Token da empresa que está buscando a listagem de alternativas.
     * @param apenasModelosAtivos      Booleano indicando que será filtrado apenas modelos de checklist ativos.
     * @param apenasPerguntasAtivas    Booleano indicando que será filtrado apenas perguntas ativas.
     * @param apenasAlternativasAtivas Booleano indicando que será filtrado apenas alternativas ativas.
     * @return Uma lista de {@link ApiAlternativaModeloChecklist alternativas} contendo todas as informações.
     * @throws Throwable Caso algum erro aconteça.
     */
    @NotNull
    List<ApiAlternativaModeloChecklist> getAlternativasModeloChecklist(
            @NotNull final String tokenIntegracao,
            final boolean apenasModelosAtivos,
            final boolean apenasPerguntasAtivas,
            final boolean apenasAlternativasAtivas) throws Throwable;
}
