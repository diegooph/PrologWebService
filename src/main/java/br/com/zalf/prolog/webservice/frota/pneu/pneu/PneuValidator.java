package br.com.zalf.prolog.webservice.frota.pneu.pneu;


import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.Normalizer;

import static br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu.isDotValid;

public class PneuValidator {

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

    private static void validacaoUnidade(Long codUnidade) throws Exception {
        Preconditions.checkNotNull(codUnidade, "Você precisa selecionar a Unidade");

        if (verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codUnidade)))) {
            throw new GenericException("Unidade inválida", "getCodUnidadeAlocado() < 1");
        }
    }

    private static void validacaoCodigoCliente(String codigoCliente) throws Exception {
        Preconditions.checkNotNull(codigoCliente, "Você precisa fornecer o Código");

        String codClienteSemAcento = codigoCliente;


        codClienteSemAcento = Normalizer.normalize(codClienteSemAcento, Normalizer.Form.NFD);
        codClienteSemAcento.replaceAll("[^\\p{ASCII}]", "");

        if (!codClienteSemAcento.equals(codigoCliente)) {
            throw new GenericException("Código inválido\nO código não pode conter acentos", null);
        }
    }

    private static void validacaoMarca(Marca marca) throws Exception {
        Preconditions.checkNotNull(marca, "Você precisa selecionar a Marca");


        if (verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(marca.getCodigo())))) {
            throw new GenericException("Marca inválida", "getCodigo() < 1");
        }
    }

    private static void validacaoModelo(ModeloPneu modelo) throws Exception {
        Preconditions.checkNotNull(modelo, "Você precisa selecionar o Modelo");

        if (verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(modelo.getCodigo())))) {
            throw new GenericException("Modelo inválido", "getCodigo() < 1");
        }
    }

    private static void validacaoValor(BigDecimal valor) throws Exception {

        if (verificacaoNumeroPositivo(Double.parseDouble(String.valueOf(valor)))) {
            throw new GenericException("Valor inválido\nO valor não pode ser negativo", "valor negativo");
        }
    }

    private static void validacaoVida(Pneu pneu) throws Exception {
        final int vidaPneuNovo = 1;
        final int vidaMaxima = 6;

        if (pneu.getVidaAtual() > vidaPneuNovo) {
            validacaoRecapagem(pneu.getVidasTotal(), pneu.getVidaAtual());
            validacaoMarcaDaBanda(pneu.getBanda().getMarca().getCodigo());
            validacaoModeloDaBanda(pneu.getBanda().getModelo().getCodigo());
            validacaoValorDaBanda(pneu.getBanda().getValor());
        }

        if (pneu.getVidaAtual() > vidaMaxima) {
            throw new GenericException("Vida inválida\nO máximo de vidas que um pneu pode ter é 6", "getVidaAtual > 6");
        }

        if (pneu.getVidaAtual() < vidaPneuNovo) {
            throw new GenericException("Vida inválida\nO pneu deve ter pelo menos 1 vida", "getVidaAtual < 1");
        }
    }

    private static void validacaoRecapagem(int vidaTotal, int vidaAtual) throws Exception {

        if (vidaTotal > vidaAtual) {
            throw new GenericException("A vida do pneu precisa ser maior que a quantidade de recapagens", "vidaTotal é maior que vidaAtual");
        }

    }

    private static void validacaoMarcaDaBanda(Long codMarcaDaBanda) throws Exception {
        Preconditions.checkNotNull(codMarcaDaBanda, "Você precisa selecionar a Marca da Banda");

        if (verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codMarcaDaBanda)))) {
            throw new GenericException("Marca da Banda inválida\n", "codMarcaDaBanda < 1");
        }

    }

    private static void validacaoModeloDaBanda(Long codModeloDaBanda) throws Exception {
        Preconditions.checkNotNull(codModeloDaBanda, "Você precisa selecionar o Modelo da Banda");

        if (verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codModeloDaBanda)))) {
            throw new GenericException("Modelo da Banda inválido\n", "codModeloDaBanda < 1");
        }

    }

    private static void validacaoValorDaBanda(BigDecimal valor) throws Exception {

        if (verificacaoNumeroPositivo(Double.parseDouble(String.valueOf(valor)))) {
            throw new GenericException("Valor inválido\nO valor não pode ser negativo", "valor negativo");
        }

    }

    private static void validacaoPressao(double pressao) throws Exception {
        Preconditions.checkNotNull(pressao, "Você fornecer a Pressão Correta");

        if (verificacaoNumeroPositivo(pressao)) {
            throw new GenericException("Pressão inválida\nA Pressão não pode ser negativa", "valor da pressão negativo");
        }

    }

    private static void validacaoDimensao(Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Você precisa fornecer a Dimensão");
    }

    private static void tipoValidacaoSulcos(Pneu pneu) throws GenericException{
        final int pneuNovo = 1;

        try {
            if (pneu.getVidaAtual() == pneuNovo) {
                validacaoSulcos(pneu.getSulcosAtuais(), pneu.getModelo().getQuantidadeSulcos());
            } else {
                validacaoSulcos(pneu.getSulcosAtuais(), pneu.getBanda().getModelo().getQuantidadeSulcos());
            }
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoSulcos(Sulcos sulcos, int quantidadeDeSulcos) throws Exception {
        Preconditions.checkNotNull(sulcos, "Você precisa fornecer o Sulco");
        Preconditions.checkNotNull(sulcos.getCentralExterno(), "Você precisa fornecer o Sulco Central Externo");
        Preconditions.checkNotNull(sulcos.getCentralInterno(), "Você precisa fornecer o Sulco Central Interno");
        Preconditions.checkNotNull(sulcos.getExterno(), "Você precisa fornecer o Sulco Externo");
        Preconditions.checkNotNull(sulcos.getInterno(), "Você precisa fornecer o Sulco Externo");

        final int quantidadeDeSulcosSite = quantidadeDeSulcos % 2;
        final int par = 0;

        if (quantidadeDeSulcosSite == par) {
            if (verificacaoNumeroPositivo(sulcos.getCentralInterno())) {
                throw new GenericException("Sulco Atual Central Interno inválido\nO Sulco não pode ter um valor negativo", "Sulco Central Interno com valor negativo");
            } else if (verificacaoNumeroPositivo(sulcos.getCentralExterno())) {
                throw new GenericException("Sulco Atual Central Externo inválido\nO Sulco não pode ter um valor negativo", "Sulco Central Externo com valor negativo");
            }
        } else {
            if (verificacaoNumeroPositivo(sulcos.getCentralInterno())) {
                throw new GenericException("Sulco Atual Central inválido\nO Sulco não pode ter um valor negativo", "Sulco Central com valor negativo");
            }
        }

        if (verificacaoNumeroPositivo(sulcos.getExterno())) {
            throw new GenericException("Sulco Atual Externo inválido\nO Sulco não pode ter um valor negativo", "Sulco Externo com valor negativo");
        } else if (verificacaoNumeroPositivo(sulcos.getInterno())) {
            throw new GenericException("Sulco Atual Interno inválido\nO Sulco não pode ter um valor negativo", "Sulco Externo com valor negativo");
        }
    }

    private static void validacaoDot(String dot) throws Exception {
        Preconditions.checkNotNull(dot, "Você precisa fornecer o DOT");

        if (!isDotValid(dot)) {
            throw new GenericException("DOT inválido.", null);
        }
    }

    private static boolean verificacaoNumeroPositivo(Double numero) {
        return numero < 0;
    }

    private static boolean verificacaoNumeroPositivo(int numero) {
        return numero < 0;
    }

}