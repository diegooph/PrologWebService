package br.com.zalf.prolog.webservice.frota.pneu.error;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu._model.Banda;
import br.com.zalf.prolog.webservice.frota.pneu._model.ModeloPneu;
import br.com.zalf.prolog.webservice.frota.pneu._model.Pneu;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

import static br.com.zalf.prolog.webservice.frota.pneu._model.Pneu.isDotValid;

public final class PneuValidator {
    private static final int VIDA_PNEU_NOVO = 1;
    private static final int VIDA_MAXIMA = 11;

    private PneuValidator() {
        throw new IllegalStateException(PneuValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosPneu(@NotNull final Pneu pneu,
                                              final Long codUnidade,
                                              final boolean ignoreDotValidation) throws GenericException {
        try {
            validacaoUnidade(codUnidade);
            validacaoCodigoCliente(pneu.getCodigoCliente());
            validacaoMarca(pneu.getMarca());
            validacaoModelo(pneu.getModelo());
            validacaoValor(pneu.getValor());
            validacaoVida(pneu.getVidaAtual(), pneu.getVidasTotal());
            validacaoBanda(pneu);
            validacaoPressao(pneu.getPressaoCorreta());
            validacaoDimensao(pneu.getDimensao());
            if (!ignoreDotValidation) {
                validacaoDot(pneu.getDot());
            }
        } catch (final GenericException e) {
            throw e;
        } catch (final Exception e) {
            throw new GenericException(e.getMessage(), null, GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    private static void validacaoUnidade(final Long codUnidade) {
        Preconditions.checkNotNull(codUnidade, "Voc?? precisa fornecer a unidade do pneu");
        Preconditions.checkArgument(codUnidade > 0, "Unidade inv??lida");
    }

    private static void validacaoCodigoCliente(final String codigoCliente) throws Exception {
        Preconditions.checkNotNull(codigoCliente, "Voc?? precisa fornecer o c??digo do pneu");
        if (!StringUtils.stripAccents(codigoCliente).equals(codigoCliente)) {
            throw new GenericException("C??digo inv??lido\nO c??digo n??o deve conter acentos", "C??digo informado: "
                    + codigoCliente,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    private static void validacaoMarca(final Marca marca) {
        Preconditions.checkNotNull(marca, "Voc?? precisa fornecer a marca do pneu");
        Preconditions.checkArgument(marca.getCodigo() > 0, "Marca do pneu inv??lida");
    }

    private static void validacaoModelo(final ModeloPneu modelo) {
        Preconditions.checkNotNull(modelo, "Voc?? precisa fornecer o modelo do pneu");
        Preconditions.checkArgument(modelo.getCodigo() > 0, "Modelo do pneu inv??lido");
    }

    private static void validacaoValor(final BigDecimal valor) {
        Preconditions.checkNotNull(valor, "Voc?? precisa fornecer o valor do pneu");
        Preconditions.checkArgument(valor.doubleValue() >= 0,
                                    "Valor inv??lido\nO valor n??o deve ser negativo");
    }

    public static void validacaoVida(final int vidaAtual, final int vidaTotal) throws Exception {
        validacaoVidaRecapagem(vidaTotal, vidaAtual);

        if (vidaAtual > VIDA_MAXIMA) {
            throw new GenericException("Vida inv??lida\nO m??ximo de vidas que um pneu deve ter ?? 11",
                                       "vidaAtual: " + vidaAtual,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }

        if (vidaAtual < VIDA_PNEU_NOVO) {
            throw new GenericException("Vida inv??lida\nO pneu deve ter pelo menos vida 1",
                                       "vidaAtual: " + vidaAtual,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    public static void validacaoVidaPneuNovoNuncaRodado(final int vidaAtual,
                                                        final boolean pneuNovoNuncaRodado) throws Exception {
        if (vidaAtual > VIDA_PNEU_NOVO && pneuNovoNuncaRodado) {
            throw new GenericException("Campo Pneu novo (nunca rodado) inv??lido\n" +
                                               "O pneu n??o pode ser novo (nunca rodado) e estar na primeira vida, " +
                                               "simultaneamente", "vidaAtual: " + vidaAtual,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    private static void validacaoVidaRecapagem(final int vidaTotal, final int vidaAtual) throws Exception {
        if (vidaTotal < vidaAtual) {
            throw new GenericException("A vida do pneu precisa ser menor ou igual ao m??ximo de recapagens",
                                       "vidaTotal ?? menor que vidaAtual\nvidaAtual: " + vidaAtual +
                                               " vidaTotal: " + vidaTotal,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }

    private static void validacaoBanda(final Pneu pneu) {
        if (pneu.getVidaAtual() > VIDA_PNEU_NOVO) {
            final Banda banda = pneu.getBanda();
            Preconditions.checkNotNull(banda.getMarca(), "Voc?? precisa fornecer a marca de banda");
            Preconditions.checkNotNull(banda.getModelo(), "Voc?? precisa fornecer o modelo da banda");

            validacaoMarcaDaBanda(banda.getMarca().getCodigo());
            validacaoModeloDaBanda(banda.getModelo().getCodigo());
            validacaoValorDaBanda(banda.getValor());
        }
    }

    private static void validacaoMarcaDaBanda(final Long codMarcaDaBanda) {
        Preconditions.checkNotNull(codMarcaDaBanda, "Voc?? precisa fornecer a marca da banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codMarcaDaBanda)) > 0,
                                    "Marca da banda inv??lida");
    }

    public static void validacaoModeloDaBanda(final Long codModeloDaBanda) {
        Preconditions.checkNotNull(codModeloDaBanda, "Voc?? precisa fornecer o modelo da banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codModeloDaBanda)) > 0,
                                    "Modelo da banda inv??lido");
    }

    public static void validacaoValorDaBanda(final BigDecimal valor) {
        Preconditions.checkNotNull(valor, "Voc?? precisa fornecer o valor");
        Preconditions.checkArgument(valor.doubleValue() >= 0, "Valor " +
                "inv??lido\nO valor da banda n??o pode ser negativo.");
    }

    private static void validacaoPressao(final double pressao) {
        Preconditions.checkArgument(pressao >= 0,
                                    "Press??o inv??lida\nA press??o n??o deve ser negativa");
    }

    private static void validacaoDimensao(final Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Voc?? precisa fornecer a dimens??o");
    }

    public static void validacaoDot(@Nullable final String dot) {
        if (!StringUtils.isNullOrEmpty(dot) && !isDotValid(dot)) {
            throw new GenericException("DOT inv??lido", "DOT informado: " + dot,
                                       GenericException.NO_LOGS_INTO_SENTRY);
        }
    }
}