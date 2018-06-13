package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

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
        Preconditions.checkNotNull(codUnidade, "Você precisa selecionar a Unidade");
        Preconditions.checkArgument(codUnidade > 0, "Unidade inválida");
    }

    private static void validacaoCodigoCliente(String codigoCliente) throws Exception {
        Preconditions.checkNotNull(codigoCliente, "Você precisa fornecer o Código");
        if (!StringUtils.stripAccents(codigoCliente).equals(codigoCliente)) {
            throw new GenericException("Código inválido\nO código não pode conter acentos", null);
        }
    }

    private static void validacaoMarca(Marca marca) {
        Preconditions.checkNotNull(marca, "Você precisa selecionar a Marca");
        Preconditions.checkArgument(marca.getCodigo() > 0, "Marca inválida");
    }

    private static void validacaoModelo(ModeloPneu modelo) {
        Preconditions.checkNotNull(modelo, "Você precisa selecionar o Modelo");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(modelo.getCodigo())) > 0,
                "Modelo inválido");
    }

    private static void validacaoValor(BigDecimal valor) {
        Preconditions.checkNotNull(valor, "Você precisa fornecer o valor");
        Preconditions.checkArgument(Double.parseDouble(String.valueOf(valor)) > 0,
                "Valor inválido\nO valor não pode ser negativo");
    }

    private static void validacaoVida(Pneu pneu) throws Exception {
        validacaoVidaRecapagem(pneu.getVidasTotal(), pneu.getVidaAtual());

        if (pneu.getVidaAtual() > VIDA_PNEU_NOVO) {
            validacaoBanda(pneu.getBanda());
        }

        if (pneu.getVidaAtual() > VIDA_MAXIMA) {
            throw new GenericException("Vida inválida\nO máximo de vidas que um pneu pode ter é 6", "getVidaAtual > 6");
        }

        if (pneu.getVidaAtual() < VIDA_PNEU_NOVO) {
            throw new GenericException("Vida inválida\nO pneu deve ter pelo menos vida 1", "getVidaAtual < 1");
        }
    }

    private static void validacaoVidaRecapagem(int vidaTotal, int vidaAtual) throws Exception {
        if (vidaTotal < vidaAtual) {
            throw new GenericException("A vida do pneu precisa ser menor ou igual ao máximo de recapagens",
                    "vidaTotal é menor que vidaAtual");
        }
    }

    private static void validacaoBanda (Banda banda) {
        Preconditions.checkNotNull(banda.getMarca(), "Você precisa selecionar uma marca de banda");
        Preconditions.checkNotNull(banda.getModelo(), "Você precisa selecionar um modelo");
        Preconditions.checkNotNull(banda.getValor(), "Vocẽ precisa fornecer o valor");

        validacaoMarcaDaBanda(banda.getMarca().getCodigo());
        validacaoModeloDaBanda(banda.getModelo().getCodigo());
        validacaoValorDaBanda(banda.getValor());
    }

    private static void validacaoMarcaDaBanda(Long codMarcaDaBanda) {
        Preconditions.checkNotNull(codMarcaDaBanda, "Você precisa selecionar a Marca da Banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codMarcaDaBanda)) > 0,
                "Marca da Banda inválida");
    }

    private static void validacaoModeloDaBanda(Long codModeloDaBanda) {
        Preconditions.checkNotNull(codModeloDaBanda, "Você precisa selecionar o Modelo da Banda");
        Preconditions.checkArgument(Integer.parseInt(String.valueOf(codModeloDaBanda)) > 0,
                "Modelo da Banda inválido");
    }

    private static void validacaoValorDaBanda(BigDecimal valor) {
        Preconditions.checkArgument(Double.parseDouble(String.valueOf(valor)) >= 0, "Valor " +
                "inválido\nO valor não pode ser negativo");
    }

    private static void validacaoPressao(double pressao) {
        Preconditions.checkArgument(pressao >= 0, "Pressão inválida\nA Pressão não pode ser negativa");
    }

    private static void validacaoDimensao(Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Você precisa fornecer a Dimensão");
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
        Preconditions.checkNotNull(sulcos, "Você precisa fornecer o Sulco");
        Preconditions.checkNotNull(sulcos.getCentralExterno(), "Você precisa fornecer o Sulco Central Externo");
        Preconditions.checkNotNull(sulcos.getCentralInterno(), "Você precisa fornecer o Sulco Central Interno");
        Preconditions.checkNotNull(sulcos.getExterno(), "Você precisa fornecer o Sulco Externo");
        Preconditions.checkNotNull(sulcos.getInterno(), "Você precisa fornecer o Sulco Externo");

        final int quantidadeDeSulcosSite = quantidadeDeSulcos % 2;
        if (quantidadeDeSulcosSite == PARIDADE) {
            Preconditions.checkArgument(sulcos.getCentralInterno() >= 0, "Sulco Atual Central " +
                    "Interno inválido\nO Sulco não pode ter um valor negativo");

            Preconditions.checkArgument(sulcos.getCentralExterno() >= 0, "Sulco Atual Central " +
                    "Externo inválido\nO Sulco não pode ter um valor negativo");
        } else {
            Preconditions.checkArgument(sulcos.getCentralInterno() >= 0, "Sulco Atual Central inválido" +
                    "\nO Sulco não pode ter um valor negativo");
        }

        Preconditions.checkArgument(sulcos.getInterno() >= 0, "Sulco Atual Interno inválido" +
                "\nO Sulco não pode ter um valor negativo");
        Preconditions.checkArgument(sulcos.getExterno() >= 0, "Sulco Atual Externo inválido" +
                "\nO Sulco não pode ter um valor negativo");
    }

    private static void validacaoDot(String dot) throws Exception {
        Preconditions.checkNotNull(dot, "Você precisa fornecer o DOT");

        if (!isDotValid(dot)) {
            throw new GenericException("DOT inválido.", null);
        }
    }
}