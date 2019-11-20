package br.com.zalf.prolog.webservice.frota.pneu.error;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

import static br.com.zalf.prolog.webservice.frota.pneu._model.Pneu.isDotValid;

public final class PneuValidator {
    private static final int VIDA_PNEU_NOVO = 1;
    private static final int VIDA_MAXIMA = 6;

    private PneuValidator() {
        throw new IllegalStateException(PneuValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosPneu(@NotNull final Pneu pneu,
                                              Long codUnidade,
                                              final boolean ignoreDotValidation) throws GenericException {
        try {
            validacaoUnidade(codUnidade);
            validacaoCodigoCliente(pneu.getCodigoCliente());
            validacaoMarca(pneu.getMarca());
            validacaoModelo(pneu.getModelo());
            validacaoValor(pneu.getValor());
            validacaoVida(pneu);
            validacaoPressao(pneu.getPressaoCorreta());
            validacaoDimensao(pneu.getDimensao());
            if (!ignoreDotValidation) {
                validacaoDot(pneu.getDot());
            }
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoUnidade(Long codUnidade) {
        Preconditions.checkNotNull(codUnidade, "Você precisa selecionar a unidade");
        Preconditions.checkArgument(codUnidade > 0, "Unidade inválida");
    }

    private static void validacaoCodigoCliente(String codigoCliente) throws Exception {
        Preconditions.checkNotNull(codigoCliente, "Você precisa fornecer o código");
        if (!StringUtils.stripAccents(codigoCliente).equals(codigoCliente)) {
            throw new GenericException("Código inválido\nO código não deve conter acentos", "Código informado: "
                    + codigoCliente);
        }
    }

    private static void validacaoMarca(Marca marca) {
        Preconditions.checkNotNull(marca, "Você precisa selecionar a marca");
        Preconditions.checkArgument(marca.getCodigo() > 0, "Marca inválida");
    }

    private static void validacaoModelo(ModeloPneu modelo) {
        Preconditions.checkNotNull(modelo, "Você precisa selecionar o modelo");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(modelo.getCodigo())) > 0,
                "Modelo inválido");
    }

    private static void validacaoValor(BigDecimal valor) {
        Preconditions.checkNotNull(valor, "Você precisa fornecer o valor");
        // TODO: O app tinha um bug que ocasionava o envio do valor 0 no cadastro do pneu, foi corrigido em nov/18, por
        // segurança é bom alterar essa verificação para >= apenas em 2019.
        Preconditions.checkArgument(valor.doubleValue() > 0,
                "Valor inválido\nO valor não deve ser negativo");
    }

    private static void validacaoVida(Pneu pneu) throws Exception {
        validacaoVidaRecapagem(pneu.getVidasTotal(), pneu.getVidaAtual());

        if (pneu.getVidaAtual() > VIDA_PNEU_NOVO) {
            validacaoBanda(pneu.getBanda());
        }

        if (pneu.getVidaAtual() > VIDA_MAXIMA) {
            throw new GenericException("Vida inválida\nO máximo de vidas que um pneu deve ter é 6", "vidaAtual: " + pneu.getVidaAtual());
        }

        if (pneu.getVidaAtual() < VIDA_PNEU_NOVO) {
            throw new GenericException("Vida inválida\nO pneu deve ter pelo menos vida 1", "vidaAtual: " + pneu.getVidaAtual());
        }
    }

    private static void validacaoVidaRecapagem(int vidaTotal, int vidaAtual) throws Exception {
        if (vidaTotal < vidaAtual) {
            throw new GenericException("A vida do pneu precisa ser menor ou igual ao máximo de recapagens",
                    "vidaTotal é menor que vidaAtual\nvidaAtual: " + vidaAtual + " vidaTotal: " + vidaTotal);
        }
    }

    private static void validacaoBanda(Banda banda) {
        Preconditions.checkNotNull(banda.getMarca(), "Você precisa selecionar a marca de banda");
        Preconditions.checkNotNull(banda.getModelo(), "Você precisa selecionar o modelo");
        Preconditions.checkNotNull(banda.getValor(), "Você precisa fornecer o valor");

        validacaoMarcaDaBanda(banda.getMarca().getCodigo());
        validacaoModeloDaBanda(banda.getModelo().getCodigo());
        validacaoValorDaBanda(banda.getValor());
    }

    private static void validacaoMarcaDaBanda(Long codMarcaDaBanda) {
        Preconditions.checkNotNull(codMarcaDaBanda, "Você precisa selecionar a marca da banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codMarcaDaBanda)) > 0,
                "Marca da banda inválida");
    }

    private static void validacaoModeloDaBanda(Long codModeloDaBanda) {
        Preconditions.checkNotNull(codModeloDaBanda, "Você precisa selecionar o modelo da banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codModeloDaBanda)) > 0,
                "Modelo da banda inválido");
    }

    private static void validacaoValorDaBanda(BigDecimal valor) {
        // TODO: O app tinha um bug que ocasionava o envio do valor 0 no cadastro do pneu, foi corrigido em nov/18, por
        // segurança é bom alterar essa verificação para >= apenas em 2019.
        Preconditions.checkArgument(valor.doubleValue() > 0, "Valor " +
                "inválido\nO valor não deve ser negativo");
    }

    private static void validacaoPressao(double pressao) {
        Preconditions.checkArgument(pressao >= 0, "Pressão inválida\nA pressão não deve ser negativa");
    }

    private static void validacaoDimensao(Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Você precisa fornecer a dimensão");
    }

    private static void validacaoDot(@Nullable final String dot) throws Exception {
        if (dot != null && !isDotValid(dot)) {
            throw new GenericException("DOT inválido", "DOT informado: " + dot);
        }
    }
}