package br.com.zalf.prolog.webservice.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 04/06/18.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public class GenericUtils {

    /**
     * Método que verifica se um número inteiro é negativo.
     * @param numero - número a ser analisado.
     * @return Verdadeiro caso o numero seja negativo.
     */
    public static boolean verificaNumeroNegativo(int numero) {

        return numero < 0;
    }

    /**
     * Método que verifica se um número decimal é negativo.
     * @param numero - número a ser analisado.
     * @return Verdadeiro caso o numero seja negativo.
     */
    public static boolean verificaNumeroNegativo(Double numero) {
        return numero < 0;
    }

    /**
     * Método que verifica se uma String contém apenas letras.
     * @param palavra - palavra a ser analisada.
     * @return Verdadeiro caso tenha apenas letras ou False se tenha algum número.
     */
    public static boolean verificaContemApenasLetras(String palavra) {

        return !palavra.matches(".*\\d+.*");

    }

    /**
     * Método que verifica se uma data está dentro do limite estabelecido.
     * @param data - data a ser analisada.
     * @return Verdadeiro caso o ano esteja fora do limite estabelecido.
     */
    public static boolean verificaAno(Date data) {
        final int anoMinimoPermitido = 1900;
        final int anoMaximoPermitido = 2050;
        SimpleDateFormat ano = new SimpleDateFormat("yyyy");
        final int anoDataNascimento = Integer.parseInt(ano.format(data));

        System.out.println(data);
        System.out.println(anoDataNascimento >= anoMaximoPermitido || anoDataNascimento <= anoMinimoPermitido);

        return anoDataNascimento >= anoMaximoPermitido || anoDataNascimento <= anoMinimoPermitido;
    }
}
