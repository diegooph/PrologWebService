package br.com.zalf.prolog.webservice.gente.controleintervalo.controleintervalorelatorios;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Zart on 28/08/2017.
 */
public interface ControleIntervaloRelatoriosDao {

    @NotNull
    void getIntervalosCsv(OutputStream out, Long codUnidade, Date dataInicial, Date dataFinal, String cpf)
            throws SQLException, IOException;

}
