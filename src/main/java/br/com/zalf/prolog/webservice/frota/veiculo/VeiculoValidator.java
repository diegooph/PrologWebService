package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;


public class VeiculoValidator {

    public static void validacaoAtributosVeiculo(@NotNull final Veiculo veiculo) throws GenericException {

        try {


        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional(Long codRegional){
        Preconditions.checkNotNull(codRegional, "Você precisa selecionar a Regional");
    }

    private static void validacaoUnidade (Long codUnidade){
        Preconditions.checkNotNull(codUnidade, "Vocẽ precisa selecionar a Unidade");
    }

    private static void validacaoPlaca (String placa){
        Preconditions.checkNotNull(placa, "Você deve fornecer a placa");
    }

    private static void validacaoKmAtual (){

    }
}
