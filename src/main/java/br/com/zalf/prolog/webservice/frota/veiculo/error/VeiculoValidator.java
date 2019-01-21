package br.com.zalf.prolog.webservice.frota.veiculo.error;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public class VeiculoValidator {
    private static final int MAX_LENGTH_PLACA = 7;

    private VeiculoValidator() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosVeiculo(@NotNull final Veiculo veiculo) throws GenericException {
        try {
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

    private static void validacaoPlaca(String placa) throws Exception {
        Preconditions.checkNotNull(placa, "Você deve fornecer a placa");

        if (placa.length() != MAX_LENGTH_PLACA) {
            throw new GenericException("Placa inválida\nA placa deve conter sete caracteres", "Placa informada: " + placa);
        }
        if (!(StringUtils.stripCharactersWithAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter acentos", "Placa informada: " + placa);
        }
        if (!(StringUtils.stripCharactersSpecials(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter caracteres especiais", "Placa informada: " + placa);
        }
    }

    private static void validacaoKmAtual(Long kmAtual) {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o km atual");
        Preconditions.checkArgument(kmAtual > 0, "Km atual inválido\nA quilometragem não deve " +
                "ser negativa");
    }

    private static void validacaoMarca(Long codMarca) {
        Preconditions.checkNotNull(codMarca, "Você precisa selecionar a marca");
        Preconditions.checkArgument(codMarca > 0, "Marca inválida");
    }

    private static void validacaoModelo(Long codModelo) {
        Preconditions.checkNotNull(codModelo, "Você precisa selecionar o modelo");
        Preconditions.checkArgument(codModelo > 0, "Modelo inválido");
    }

    private static void validacaoEixos(Long codEixos) {
        Preconditions.checkNotNull(codEixos, "Você precisa selecionar o tipo de eixos");
        Preconditions.checkArgument(codEixos > 0, "Eixos inválido");
    }

    private static void validacaoTipo(Long codTipo) {
        Preconditions.checkNotNull(codTipo, "Você precisa selecionar o tipo");
        Preconditions.checkArgument(codTipo > 0, "Tipo inválido");
    }
}
