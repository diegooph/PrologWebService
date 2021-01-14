package br.com.zalf.prolog.webservice.frota.veiculo.error;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.VeiculoCadastro;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import com.google.common.base.Preconditions;
import io.sentry.util.Nullable;
import org.jetbrains.annotations.NotNull;

public class VeiculoValidator {
    private static final int MAX_LENGTH_PLACA = 7;

    private VeiculoValidator() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosVeiculo(@NotNull final VeiculoCadastro veiculo) throws GenericException {
        try {
            validacaoPlaca(veiculo.getPlacaVeiculo());
            validacaoKmAtual(veiculo.getKmAtualVeiculo());
            validacaoMarca(veiculo.getCodMarcaVeiculo());
            validacaoModelo(veiculo.getCodModeloVeiculo());
            validacaoTipo(veiculo.getCodTipoVeiculo());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    public static void validacaoAtributosVeiculo(@NotNull final VeiculoEdicaoStatus veiculo)
            throws VeiculoValidatorException {
        garanteVeiculosAcopladosNaoSejamInativados(veiculo.isStatusAtivo(), veiculo.isAcoplado());
    }

    private static void validacaoPlaca(final String placa) throws Exception {
        Preconditions.checkNotNull(placa, "Você deve fornecer a placa");

        if (StringUtils.isNullOrEmpty(placa.trim())) {
            throw new GenericException("Placa inválida\nA placa não pode estar vazia.", "Placa informada: " + placa);
        }

        if (!(StringUtils.stripCharactersWithAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter acentos", "Placa informada: " + placa);
        }

        if (!(StringUtils.stripAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter caracteres especiais",
                                       "Placa informada: " + placa);
        }
    }

    private static void validacaoKmAtual(final Long kmAtual) {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o km atual");
        Preconditions.checkArgument(kmAtual > 0, "Km atual inválido\nA quilometragem não deve " +
                "ser negativa");
    }

    private static void validacaoMarca(final Long codMarca) {
        Preconditions.checkNotNull(codMarca, "Você precisa selecionar a marca");
        Preconditions.checkArgument(codMarca > 0, "Marca inválida");
    }

    private static void validacaoModelo(final Long codModelo) {
        Preconditions.checkNotNull(codModelo, "Você precisa selecionar o modelo");
        Preconditions.checkArgument(codModelo > 0, "Modelo inválido");
    }

    private static void validacaoTipo(final Long codTipo) {
        Preconditions.checkNotNull(codTipo, "Você precisa selecionar o tipo");
        Preconditions.checkArgument(codTipo > 0, "Tipo inválido");
    }

    private static void garanteVeiculosAcopladosNaoSejamInativados(final boolean statusAtivo, final boolean acoplado) {
        if (!statusAtivo && acoplado) {
            fail("Não é possível inativar um veículo acoplado.");
        }
    }

    private static void fail(@NotNull final String detailedMessage, @Nullable final Object... args) {
        throw new VeiculoValidatorException(String.format(detailedMessage, args));
    }
}
