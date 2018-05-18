package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;


public class VeiculoValidator {

    public static void validacaoAtributosVeiculo(@NotNull final Veiculo veiculo) throws GenericException {

        try {
            validacaoRegional(veiculo.getCodRegionalAlocado());
            validacaoUnidade(veiculo.getCodUnidadeAlocado());
            validacaoPlaca(veiculo.getPlaca());
            validacaoKmAtual(veiculo.getKmAtual());
            validacaoMarca(veiculo.getMarca().getCodigo());
            validacaoModelo(veiculo.getModelo().getCodigo());
            validacaoEixos(veiculo.getEixos().codigo);
            validacaoTipo(veiculo.getTipo().getCodigo());
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional(Long codRegional) throws Exception {
        Preconditions.checkNotNull(codRegional, "Você precisa selecionar a Regional");

        if (verificarNumeroNegativo(codRegional)) {
            throw new GenericException("Regional inválida", "codigo Regional retornou um valor negativo");
        }
    }

    private static void validacaoUnidade(Long codUnidade) {
        Preconditions.checkNotNull(codUnidade, "Vocẽ precisa selecionar a Unidade");
    }

    private static void validacaoPlaca(String placa) throws Exception {
        Preconditions.checkNotNull(placa, "Você deve fornecer a Placa");

        if (placa.length() != 7) {
            throw new GenericException("A placa deve conter 7 caracteres", null);
        }
    }

    private static void validacaoKmAtual(Long kmAtual) {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o Km Atual");
    }

    private static void validacaoMarca(Long codMarca) {
        Preconditions.checkNotNull(codMarca, "Você precisa selecionar a Marca");
    }

    private static void validacaoModelo(Long codModelo) {
        Preconditions.checkNotNull(codModelo, "Você precisa selecionar o Modelo");
    }

    private static void validacaoEixos(Long codEixos) {
        Preconditions.checkNotNull(codEixos, "Você precisa selecionar os Eixos");
    }

    private static void validacaoTipo(Long codTipo) {
        Preconditions.checkNotNull(codTipo, "Você precisa selecionar o Tipo");
    }

    private static boolean verificarNumeroNegativo(Long numero) {
        return numero < 0;
    }
}
