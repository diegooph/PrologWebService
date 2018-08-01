package br.com.zalf.prolog.webservice.raizen.produtividade.error;

import br.com.zalf.prolog.webservice.commons.util.ProLogValidator;
import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.commons.util.ValidationUtils;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;
import br.com.zalf.prolog.webservice.errorhandling.exception.GenericException;
import br.com.zalf.prolog.webservice.errorhandling.exception.ProLogException;
import br.com.zalf.prolog.webservice.raizen.produtividade.model.insert.RaizenProdutividadeItemInsert;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created on 30/07/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class RaizenProdutividadeValidator extends ProLogValidator {

    private static final int MAX_LENGTH_PLACA = 7;
    private static final int QTD_NUMEROS_PLACA = 4;
    private static final int QTD_LETRAS_PLACA = 3;
    private static final int ANO_MINIMO_PERMITIDO = 1900;
    private static final int ANO_MAXIMO_PERMITIDO = 2050;

    private RaizenProdutividadeValidator () {
        throw new IllegalStateException(RaizenProdutividadeValidator.class.getSimpleName() + " cannot be instantiated!");
    }

    public static void validacaoAtributosRaizenProdutividade(@NotNull final RaizenProdutividadeItemInsert itemInsert)
            throws GenericException {
        try {
            checkNotNull(itemInsert, "Não pode ser nulo");

            validacaoCPF(itemInsert.getCpfMotorista());
            validacaoPlaca(itemInsert.getPlaca());
            validacaoDataViagem(itemInsert.getDataViagem());
            validacaoValor(itemInsert.getValor());
            validacaoUsina(itemInsert.getUsina());
            validacaoFazenda(itemInsert.getFazenda());
            validacaoRaio(itemInsert.getRaio());
            validacaoToneladas(itemInsert.getToneladas());
        } catch (final GenericException e) {
            throw e;
        } catch (final Throwable t) {
            throw new GenericException("Erro ao validar os parâmetros da produtividade, tente enviar novamente" +
                    "\nSe o problema persistir, contate nosso suporte", t.getMessage());
        }
    }

    private static void validacaoCPF(@NotNull final Long cpfMotorista) throws ProLogException {
        checkNotNull(cpfMotorista, "Você precisa fornecer o CPF");

        if (!ValidationUtils.isValidCpf(String.format("%011d", cpfMotorista))) {
            throw new GenericException("CPF inválido", "CPF informado: " + cpfMotorista);
        }
    }

    private static void validacaoPlaca(@NotNull final String placa) throws ProLogException {
        checkNotNull(placa, "Vocẽ precisa fornecer a placa");

        if (placa.length() != MAX_LENGTH_PLACA) {
            throw new GenericException("Placa inválida\nA placa deve conter sete caracteres", "Placa informada: " + placa);
        }
        if (!verificaQuantidadeLetrasPlaca(placa)) {
            throw new GenericException("Placa inválida\nA placa deve conter três letras", "Placa informada: " + placa);
        }
        if (!verificaQuantidadeNumerosPlaca(placa)) {
            throw new GenericException("Placa inválida\nA placa deve conter quatro números", "Placa informada: " + placa);
        }
        if (!(StringUtils.stripCharactersWithAccents(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter acentos", "Placa informada: " + placa);
        }
        if (!(StringUtils.stripCharactersSpecials(placa)).equals(placa)) {
            throw new GenericException("Placa inválida\nA placa não deve conter caracteres especiais", "Placa informada: " + placa);
        }
    }

    private static boolean verificaQuantidadeNumerosPlaca(@NotNull final String string) {
        String placaNumeros = StringUtils.getOnlyNumbers(string);
        return placaNumeros.length() == QTD_NUMEROS_PLACA;
    }

    private static boolean verificaQuantidadeLetrasPlaca(@NotNull final String placa) {
        String placaLetras = StringUtils.getOnlyLetters(placa.trim().toUpperCase());
        return placaLetras.length() == QTD_LETRAS_PLACA;
    }

    private static void validacaoDataViagem(@NotNull final LocalDate dataViagem) throws ProLogException {
        checkNotNull(dataViagem, "Você precisa fornecer a data da viagem");
        if (DateUtils.verificaAno(dataViagem, ANO_MAXIMO_PERMITIDO, ANO_MINIMO_PERMITIDO)) {
            throw new GenericException("Ano de viagem inválido", "Data de viagem informada: " + dataViagem);
        }
    }

    private static void validacaoValor(@NotNull final BigDecimal valor) throws ProLogException {
        checkNotNull(valor, "Você precisa fornecer o valor");
        if (!StringUtils.isIntegerValue(String.valueOf(valor))) {
            throw new GenericException("O valor deve conter apenas números", "Valor informado:" +valor);
        }
        checkArgument(Double.parseDouble(String.valueOf(valor)) > 0,
                "Valor inválido\nO valor não pode ser negativo");
    }

    private static void validacaoUsina(@NotNull final String usina) throws ProLogException {
        checkNotNull(usina, "Você precisa fornecer uma usina");

        if (StringUtils.isNullOrEmpty(usina.trim())) {
            throw new GenericException("Vocẽ precisa fornecer uma usina", "fazenda com apenas espaços em branco" + usina);
        }
    }

    private static void validacaoFazenda(@NotNull final String fazenda) throws ProLogException {
        checkNotNull(fazenda, "Você precisa fornecer uma fazenda");

        if (StringUtils.isNullOrEmpty(fazenda.trim())) {
            throw new GenericException("Vocẽ precisa fornecer uma fazenda", "fazenda com apenas espaços em branco" + fazenda);
        }
    }

    private static void validacaoRaio(final double raio) throws ProLogException {
        checkNotNull(raio, "Você precisa fornecer o raio");
        checkArgument(Double.parseDouble(String.valueOf(raio)) > 0,
                "Valor inválido\nO raio não pode ser negativo");
    }

    private static void validacaoToneladas(final double toneladas) throws ProLogException {
        checkNotNull(toneladas, "Você precisa fornecer a tonelada");
        checkArgument(Double.parseDouble(String.valueOf(toneladas)) > 0,
                "Valor inválido\nA tonelada não pode ser negativa");
    }
}