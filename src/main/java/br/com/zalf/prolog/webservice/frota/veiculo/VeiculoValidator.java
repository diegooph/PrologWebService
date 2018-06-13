package br.com.zalf.prolog.webservice.frota.veiculo;

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
        Preconditions.checkNotNull(placa, "Você deve fornecer a Placa");

        if (placa.length() != MAX_LENGTH_PLACA) {
            throw new GenericException("A placa deve conter 7 caracteres", null);
        }

        if (!StringUtils.stripAccents(placa).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não pode conter acentos", null);
        }

        if (StringUtils.isAlpabetsValue(placa.substring(4, 5))) {
            if (!verificaPlacaNova(placa)) {
                throw new GenericException("Plava inválida", null);
            }
        } else {
            if (!verificaPlacaAntiga(placa)) {
                throw new GenericException("Placa inválida", null);
            }
        }
    }

    private static boolean verificaPlacaNova(String placa) {
        return StringUtils.isAlpabetsValue(placa.substring(0, 3)) && StringUtils.isIntegerValue(placa.substring(3, 4)) &&
                StringUtils.isAlpabetsValue(placa.substring(4, 5)) && StringUtils.isIntegerValue(placa.substring(5, 7));
    }

    private static boolean verificaPlacaAntiga(String placa) {
        return StringUtils.isAlpabetsValue(placa.substring(0, 3)) && StringUtils.isIntegerValue(placa.substring(3, 7));
    }

    private static void validacaoKmAtual(Long kmAtual) {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o Km Atual");
        Preconditions.checkArgument(kmAtual > 0, "Km Atual inválido\nA quilometragem não pode " +
                "ser negativa");
    }

    private static void validacaoMarca(Long codMarca) {
        Preconditions.checkNotNull(codMarca, "Você precisa selecionar a Marca");
        Preconditions.checkArgument(codMarca > 0, "Marca inválida");
    }

    private static void validacaoModelo(Long codModelo) {
        Preconditions.checkNotNull(codModelo, "Você precisa selecionar o Modelo");
        Preconditions.checkArgument(codModelo > 0, "Modelo inválido");
    }

    private static void validacaoEixos(Long codEixos) throws Exception {
        Preconditions.checkNotNull(codEixos, "Você precisa selecionar os Eixos");
        Preconditions.checkArgument(codEixos > 0, "Eixos inválido");
    }

    private static void validacaoTipo(Long codTipo) throws Exception {
        Preconditions.checkNotNull(codTipo, "Você precisa selecionar o Tipo");
        Preconditions.checkArgument(codTipo > 0, "Tipo inválido");
    }
}
