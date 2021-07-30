package br.com.zalf.prolog.webservice.frota.veiculo.validator;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.veiculo.error.VeiculoValidatorException;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoEdicaoStatus;
import br.com.zalf.prolog.webservice.frota.veiculo.tipoveiculo.TipoVeiculoDao;
import br.com.zalf.prolog.webservice.v3.fleet.vehicle._model.VehicleCreateDto;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VehicleValidator {
    private static final int MAX_LENGTH_PLACA = 7;
    @NotNull
    private static final TipoVeiculoDao dao = Injection.provideTipoVeiculoDao();

    private VehicleValidator() {
        throw new IllegalStateException(StringUtils.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosVeiculo(@NotNull final VehicleCreateDto veiculo) throws Throwable {
        try {
            validacaoPlaca(veiculo.getPlacaVeiculo());
            validacaoKmAtual(veiculo.getKmAtualVeiculo());
            validacaoModelo(veiculo.getCodModeloVeiculo());
            validacaoTipo(veiculo.getCodTipoVeiculo());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), null, GenericException.NO_LOGS_INTO_SENTRY);
        }
        validacaoMotorizadoSemHubodometro(veiculo.getPossuiHubodometro(), veiculo.getCodTipoVeiculo());
    }

    public static void validacaoAtributosVeiculo(@NotNull final VeiculoEdicao veiculo) throws Throwable {
        try {
            validacaoPlaca(veiculo.getPlacaVeiculo());
            validacaoKmAtual(veiculo.getKmAtualVeiculo());
            validacaoModelo(veiculo.getCodModeloVeiculo());
            validacaoTipo(veiculo.getCodTipoVeiculo());
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), null, GenericException.NO_LOGS_INTO_SENTRY);
        }
        validacaoMotorizadoSemHubodometro(veiculo.getPossuiHubodometro(), veiculo.getCodTipoVeiculo());
    }

    public static void validacaoAtributosVeiculo(@NotNull final VeiculoEdicaoStatus veiculo)
            throws VeiculoValidatorException {
        garanteVeiculosAcopladosNaoSejamInativados(veiculo.isStatusAtivo(), veiculo.isAcoplado());
    }

    public static void validacaoMotorizadoSemHubodometro(@NotNull final Boolean possuiHubodometro,
                                                         @NotNull final Long codTipoVeiculo) throws Throwable {
        if (dao.getTipoVeiculo(codTipoVeiculo).isMotorizado() && possuiHubodometro) {
            fail("Veículos motorizados não devem possuir hubodômetro.", codTipoVeiculo);
        }
    }

    private static void validacaoPlaca(final String placa) throws Exception {
        Preconditions.checkNotNull(placa, "Você deve fornecer a placa");

        if (StringUtils.isNullOrEmpty(placa.trim())) {
            throw new GenericException("Placa inválida\nA placa não pode estar vazia.", "Placa informada: " + placa,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }

        if (!(StringUtils.stripCharactersWithAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter acentos", "Placa informada: " + placa,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }

        if (!(StringUtils.stripAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter caracteres especiais",
                                       "Placa informada: " + placa,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    private static void validacaoKmAtual(final Long kmAtual) {
        Preconditions.checkNotNull(kmAtual, "Você precisa fornecer o km atual");
        Preconditions.checkArgument(kmAtual >= 0, "Km atual inválido\nA quilometragem não deve " +
                "ser negativa");
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

    private static void fail(@NotNull final String detailedMessage, @Nullable final Object... args)
            throws VeiculoValidatorException {
        throw new VeiculoValidatorException(String.format(detailedMessage, args));
    }
}