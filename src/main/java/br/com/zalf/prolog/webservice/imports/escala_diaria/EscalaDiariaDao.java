package br.com.zalf.prolog.webservice.imports.escala_diaria;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Created on 10/04/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public interface EscalaDiariaDao {

    /**
     * Insere ou atualiza a escala diária de uma unidade.
     *
     * @param codUnidade        - Código da unidade dos dados.
     * @param escalaDiariaItens -
     * @throws SQLException   - Erro ao executar consulta no Banco de Dados.
     */
    void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                    @NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException;

    /**
     * Insere uma {@link EscalaDiariaItem} específica.
     *
     * @param codUnidade       - Código da Unidade que será inserido ou
     *                         atualizado o {@link EscalaDiariaItem}.
     * @param escalaDiariaItem - Item que será inserido.
     * @param isInsert         - {@link Boolean} para distinguir um insert de um update.
     * @throws SQLException - Erro na execução do insert.
     */
    void insertOrUpdateEscalaDiariaItem(@NotNull final Long codUnidade,
                                        @NotNull final EscalaDiariaItem escalaDiariaItem,
                                        boolean isInsert) throws SQLException;

    /**
     * Busca as escalas diárias de uma unidade filtradas por um período de tempo.
     *
     * @param codUnidade  - Unidade que será buscado os dados.
     * @param dataInicial - Data inicial do filtro de busca.
     * @param dataFinal   - Data final do filtro de busca.
     * @return - Um {@link List<EscalaDiaria>} contendo a
     * {@link EscalaDiariaItem} de cada dia dentro do período buscado.
     * @throws SQLException - Erro na execução da busca dos dados no Banco.
     */
    List<EscalaDiaria> getEscalasDiarias(@NotNull final Long codUnidade,
                                         @NotNull final LocalDate dataInicial,
                                         @NotNull final LocalDate dataFinal) throws SQLException;

    /**
     * Deleta uma {@link List<EscalaDiariaItem>}.
     *
     * @param codUnidade - Código da Unidade que será deletado a {@link List<EscalaDiariaItem>}.
     * @param codEscalas - {@link List<Long>} contendo os códigos das escalas que deverão ser deletaddas.
     * @throws SQLException - Erro na execução do delete.
     */
    void deleteEscalaDiariaItens(@NotNull final Long codUnidade,
                                 @NotNull final List<Long> codEscalas) throws SQLException;
}
