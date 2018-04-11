package br.com.zalf.prolog.webservice.imports.escala_diaria;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
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
     * @param codUnidade      - Código da unidade dos dados.
     * @param fileName        - Nome do arquivo onde os dados se encontram.
     * @param fileInputStream - Endereço de memória para leitura do arquivo.
     * @throws SQLException   - Erro ao executar consulta no Banco de Dados.
     * @throws IOException    - Erro ao ler arquivo da memória.
     * @throws ParseException - Erro ao transformar informações do arquivo.
     */
    void insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                    @NotNull final String fileName,
                                    @NotNull final InputStream fileInputStream)
            throws SQLException, IOException, ParseException;

    /**
     * Insere uma {@link EscalaDiariaItem} específica.
     *
     * @param escalaDiariaItem - Item que será inserido.
     * @throws SQLException - Erro na execução do insert.
     */
    void insertOrUpdateEscalaDiariaItem(@NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException;

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
                                         @NotNull final Long dataInicial,
                                         @NotNull final Long dataFinal) throws SQLException;

    /**
     * Deleta uma {@link EscalaDiariaItem} específica.
     *
     * @param escalaDiariaItem - Item que será deletado.
     * @throws SQLException - Erro na execução do delete.
     */
    void deleteEscalaDiariaItem(@NotNull final EscalaDiariaItem escalaDiariaItem) throws SQLException;

    /**
     * Deleta uma {@link List<EscalaDiariaItem>}.
     *
     * @param escalaDiariaItens - {@link List<EscalaDiariaItem>} que serão deletados.
     * @throws SQLException - Erro na execução do delete.
     */
    void deleteEscalaDiariaItens(@NotNull final List<EscalaDiariaItem> escalaDiariaItens) throws SQLException;
}
