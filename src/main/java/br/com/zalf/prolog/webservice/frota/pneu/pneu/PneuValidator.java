package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

import static br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.isDotValid;

public class PneuValidator {

    private static final int VIDA_PNEU_NOVO = 1;
    private static final int VIDA_MAXIMA = 6;
    private static final int PARIDADE = 0;

    public static void validacaoAtributosPneu(@NotNull final Pneu pneu, Long codUnidade) throws GenericException {
        try {
            validacaoUnidade(codUnidade);
            validacaoCodigoCliente(pneu.getCodigoCliente());
            validacaoMarca(pneu.getMarca());
            validacaoModelo(pneu.getModelo());
            validacaoValor(pneu.getValor());
            validacaoVida(pneu);
            validacaoPressao(pneu.getPressaoCorreta());
            validacaoDimensao(pneu.getDimensao());
            tipoValidacaoSulcos(pneu);
            validacaoDot(pneu.getDot());
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
        Preconditions.checkArgument(Double.parseDouble(String.valueOf(valor)) > 0,
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
        Preconditions.checkArgument(Double.parseDouble(String.valueOf(valor)) >= 0, "Valor " +
                "inválido\nO valor não deve ser negativo");
    }

    private static void validacaoPressao(double pressao) {
        Preconditions.checkArgument(pressao >= 0, "Pressão inválida\nA pressão não deve ser negativa");
    }

    private static void validacaoDimensao(Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Você precisa fornecer a dimensão");
    }

    private static void tipoValidacaoSulcos(Pneu pneu) throws Exception {
        try {
            if (pneu.getVidaAtual() == VIDA_PNEU_NOVO) {
                validacaoSulcos(pneu.getSulcosAtuais(), pneu.getModelo().getQuantidadeSulcos());
            } else {
                validacaoSulcos(pneu.getSulcosAtuais(), pneu.getBanda().getModelo().getQuantidadeSulcos());
            }
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoSulcos(Sulcos sulcos, int quantidadeDeSulcos) {
        Preconditions.checkNotNull(sulcos, "Você precisa fornecer o sulco");
        Preconditions.checkNotNull(sulcos.getCentralExterno(), "Você precisa fornecer o sulco central externo");
        Preconditions.checkNotNull(sulcos.getCentralInterno(), "Você precisa fornecer o sulco central interno");
        Preconditions.checkNotNull(sulcos.getExterno(), "Você precisa fornecer o sulco externo");
        Preconditions.checkNotNull(sulcos.getInterno(), "Você precisa fornecer o sulco interno");

        final int quantidadeDeSulcosSite = quantidadeDeSulcos % 2;
        if (quantidadeDeSulcosSite == PARIDADE) {
            Preconditions.checkArgument(sulcos.getCentralInterno() >= 0, "Sulco atual central " +
                    "interno inválido\nO sulco não deve ser negativo");

            Preconditions.checkArgument(sulcos.getCentralExterno() >= 0, "Sulco atual central " +
                    "externo inválido\nO sulco não deve ser negativo");
        } else {
            Preconditions.checkArgument(sulcos.getCentralInterno() >= 0, "Sulco atual central inválido" +
                    "\nO sulco não deve ser negativo");
        }

        Preconditions.checkArgument(sulcos.getInterno() >= 0, "Sulco atual interno inválido" +
                "\nO sulco não deve ser negativo");
        Preconditions.checkArgument(sulcos.getExterno() >= 0, "Sulco atual externo inválido" +
                "\nO sulco não deve ser negativo");
    }

    private static void validacaoDot(@Nullable final String dot) throws Exception {
        if (dot != null && !isDotValid(dot)) {
            throw new GenericException("DOT inválido", null);
        }
    }
}