package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;


public class VeiculoValidator {

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

        if (placa.length() != 7) {
            throw new GenericException("A placa deve conter 7 caracteres", null);
        }

        String codClienteSemAcento = placa;
        codClienteSemAcento = Normalizer.normalize(codClienteSemAcento, Normalizer.Form.NFD);
        codClienteSemAcento.replaceAll("[^\\p{ASCII}]", "");
        if (!codClienteSemAcento.equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não pode conter acentos", null);
        }

        if (!placa.substring(4, 5).matches(".*\\d+.*")) {

            if (verificaPlacaNova(placa)) {
                throw new GenericException("Plava inválida", null);
            }

        } else {

            if (verificaPlacaAntiga(placa)) {
                throw new GenericException("Placa inválida", null);
            }
        }
    }

    private static void validacaoKmAtual(Long kmAtual) throws Exception {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o Km Atual");

        if (verificaNumeroNegativo(Integer.parseInt(String.valueOf(kmAtual)))) {
            throw new GenericException("Km Atual inválido\nA quilometragem não pode ser negativa", null);
        }
    }

    private static void validacaoMarca(Long codMarca) throws Exception {
        Preconditions.checkNotNull(codMarca, "Você precisa selecionar a Marca");

        if (verificaNumeroNegativo(Integer.parseInt(String.valueOf(codMarca)))) {
            throw new GenericException("Marca inválida", "codigo Regional retornou um valor negativo");
        }
    }

    private static void validacaoModelo(Long codModelo) throws Exception {
        Preconditions.checkNotNull(codModelo, "Você precisa selecionar o Modelo");

        if (verificaNumeroNegativo(Integer.parseInt(String.valueOf(codModelo)))) {
            throw new GenericException("Modelo inválido", "codigo do modelo retornou um valor negativo");
        }
    }

    private static void validacaoEixos(Long codEixos) throws Exception {
        Preconditions.checkNotNull(codEixos, "Você precisa selecionar os Eixos");

        if (verificaNumeroNegativo(Integer.parseInt(String.valueOf(codEixos)))) {
            throw new GenericException("Eixos inválido", "codigo de eixos retornou um valor negativo");
        }
    }

    private static void validacaoTipo(Long codTipo) throws Exception {
        Preconditions.checkNotNull(codTipo, "Você precisa selecionar o Tipo");

        if (verificaNumeroNegativo(Integer.parseInt(String.valueOf(codTipo)))) {
            throw new GenericException("Tipo inválido", "codigo de tipo retornou um valor negativo");
        }
    }

    private static boolean verificaNumeroNegativo(int numero) {

        return numero < 0;
    }

    private static boolean verificaPlacaNova(String placa) {

        if (placa.substring(0, 3).matches(".*\\d+.*")) {
            return true;
        }
        if (!placa.substring(3, 4).matches("^[0-9]*$")) {
            return true;
        }
        if (placa.substring(4, 5).matches(".*\\d+.*")) {
            return true;
        }
        if (!placa.substring(5, 7).matches("^[0-9]*$")) {
            return true;
        }

        return false;
    }

    private static boolean verificaPlacaAntiga(String placa) {

        if (placa.substring(0, 3).matches(".*\\d+.*")) {
            return true;
        }
        if (!placa.substring(3, 7).matches("^[0-9]*$")) {
            return true;
        }

        return false;
    }

}
