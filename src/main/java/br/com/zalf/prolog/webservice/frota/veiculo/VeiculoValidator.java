package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import org.jetbrains.annotations.NotNull;


public class VeiculoValidator {

    public static void validacaoAtributosVeiculo(@NotNull final Veiculo veiculo) throws GenericException {

        try {


        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

}
