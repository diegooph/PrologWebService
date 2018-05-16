package br.com.zalf.prolog.webservice.frota.pneu.pneu;


import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class PneuValidator {

    public static void validacaoAtributosPneu(@NotNull final Pneu pneu) throws GenericException {
        try {
            validacaoRegional(pneu.getCodRegionalAlocado());
            validacaoUnidade(pneu.getCodUnidadeAlocado());
            validacaoCodigoCliente(pneu.getCodigoCliente());
            validacaoMarca(pneu.getMarca());
            validacaoModelo(pneu.getModelo());
            validacaoValor(pneu.getValor());
            validacaoVida(pneu);
            validacaoPressao(pneu.getPressaoCorreta());
            validacaoDimensao(pneu.getDimensao());
            validacaoSulcos(pneu.getSulcosAtuais());
            validacaoDot(pneu.getDot());
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException(e.getMessage(), null);
        }
    }

    private static void validacaoRegional(Long codRegional) throws Exception {
        Preconditions.checkNotNull(codRegional, "Você precisa selecionar a Regional");

        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codRegional)))) {
            throw new GenericException("Regional inválida", "getCodRegionalAlocado() < 1");
        }
    }

    private static void validacaoUnidade(Long unidade) throws Exception {
        Preconditions.checkNotNull(unidade, "Você precisa selecionar a Unidade");

        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(unidade)))) {
            throw new GenericException("Unidade inválida", "getCodUnidadeAlocado() < 1");
        }
    }

    private static void validacaoCodigoCliente(String codigoCliente) {
        Preconditions.checkNotNull(codigoCliente.trim(), "Você precisa fornecer o Código");
    }

    private static void validacaoMarca(Marca marca) throws Exception {
        Preconditions.checkNotNull(marca, "Você precisa selecionar a Marca");


        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(marca.getCodigo())))) {
            throw new GenericException("Marca inválida", "getCodigo() < 1");
        }
    }

    private static void validacaoModelo(ModeloPneu modelo) throws Exception {
        Preconditions.checkNotNull(modelo, "Você precisa selecionar o Modelo");
        Preconditions.checkNotNull(modelo.getCodigo(), "Você precisa selecionar o Modelo");

        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(modelo.getCodigo())))) {
            throw new GenericException("Modelo inválido", "getCodigo() < 1");
        }
    }

    private static void validacaoValor(BigDecimal valor) throws Exception {

        if (!verificacaoNumeroPositivo(Double.parseDouble(String.valueOf(valor)))) {
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

        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codMarcaDaBanda)))) {
            throw new GenericException("Marca da Banda inválida\n", "codMarcaDaBanda < 1");
        }

    }

    private static void validacaoModeloDaBanda(Long codModeloDaBanda) throws Exception {
        Preconditions.checkNotNull(codModeloDaBanda, "Você precisa selecionar o Modelo da Banda");

        if (!verificacaoNumeroPositivo(Integer.parseInt(String.valueOf(codModeloDaBanda)))) {
            throw new GenericException("Modelo da Banda inválido\n", "codModeloDaBanda < 1");
        }

    }

    private static void validacaoValorDaBanda(BigDecimal valor) throws Exception {

        if (!verificacaoNumeroPositivo(Double.parseDouble(String.valueOf(valor)))) {
            throw new GenericException("Valor inválido\nO valor não pode ser negativo", "valor negativo");
        }

    }

    private static void validacaoPressao(double pressao) throws Exception {
        Preconditions.checkNotNull(pressao, "Você fornecer a Pressão Correta");

        if (!verificacaoNumeroPositivo(pressao)) {
            throw new GenericException("Pressão inválida\nA Pressão não pode ser negativa", "valor da pressão negativo");
        }

    }

    private static void validacaoDimensao(Pneu.Dimensao dimensao) {
        Preconditions.checkNotNull(dimensao, "Você precisa fornecer a Dimensão");
        Preconditions.checkNotNull(dimensao.codigo, "Você precisa fornecer a Dimensão");
    }

    private static void validacaoSulcos(Sulcos sulcos) throws Exception {
        Preconditions.checkNotNull(sulcos, "Você precisa fornecer o Sulco");
        Preconditions.checkNotNull(sulcos.getCentralExterno(), "Você precisa fornecer o Sulco Central Externo");
        Preconditions.checkNotNull(sulcos.getCentralInterno(), "Você precisa fornecer o Sulco Central Interno");
        Preconditions.checkNotNull(sulcos.getExterno(), "Você precisa fornecer o Sulco Externo");
        Preconditions.checkNotNull(sulcos.getInterno(), "Você precisa fornecer o Sulco Externo");

        if (verificacaoNumeroPositivo(sulcos.getCentralExterno())) {
            throw new GenericException("Sulco Central Externo inválido\n", "Sulco Central Externo com valor negativo");
        }

        if (verificacaoNumeroPositivo(sulcos.getCentralInterno())) {
            throw new GenericException("Sulco Central Interno inválido\n", "Sulco Central Interno com valor negativo");
        }

        if (verificacaoNumeroPositivo(sulcos.getExterno())) {
            throw new GenericException("Sulco Externo inválido\n", "Sulco Externo com valor negativo");
        }

        if (verificacaoNumeroPositivo(sulcos.getInterno())) {
            throw new GenericException("Sulco Interno inválido\n", "Sulco Externo com valor negativo");
        }
    }

    private static void validacaoDot(String dot) {
        Preconditions.checkNotNull(dot, "Você precisa fornecer o DOT");
    }


    private static boolean verificacaoNumeroPositivo(Double numero) {
        return numero < 0;
    }

    private static boolean verificacaoNumeroPositivo(int numero) {
        return numero < 0;
    }

}