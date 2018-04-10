package br.com.zalf.prolog.webservice.imports.escala_diaria;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;

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
     * @return                - True se operação realizada com sucesso, Falso caso contrário.
     * @throws SQLException   - Erro ao executar consulta no Banco de Dados.
     * @throws IOException    - Erro ao ler arquivo da memória.
     * @throws ParseException - Erro ao transformar informações do arquivo.
     */
    boolean insertOrUpdateEscalaDiaria(@NotNull final Long codUnidade,
                                       @NotNull final String fileName,
                                       @NotNull final InputStream fileInputStream)
            throws SQLException, IOException, ParseException;
}
