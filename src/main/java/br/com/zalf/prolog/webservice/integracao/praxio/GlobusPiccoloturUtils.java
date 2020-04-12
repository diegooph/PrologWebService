package br.com.zalf.prolog.webservice.integracao.praxio;

import br.com.zalf.prolog.webservice.commons.util.StringUtils;
import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoResposta;
import br.com.zalf.prolog.webservice.customfields._model.TipoCampoPersonalizado;
import br.com.zalf.prolog.webservice.integracao.praxio.ordensservicos.model.error.GlobusPiccoloturException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static br.com.zalf.prolog.webservice.integracao.praxio.GlobusPiccoloturConstants.COD_UNIDADE_NOME_LOCAL_MOVIMENTO_SEPARATOR;

/**
 * Created on 11/12/19
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class GlobusPiccoloturUtils {
    private static final int PLACA_PADRAO_ANTIGO_LENGTH = 7;

    private GlobusPiccoloturUtils() {
        throw new IllegalStateException(GlobusPiccoloturUtils.class.getSimpleName() + " cannot be instantiated");
    }

    @NotNull
    public static String addHifenPlacaSePadraoAntigo(@NotNull final String placa) {
        if (placa.length() == PLACA_PADRAO_ANTIGO_LENGTH) {
            final String letras = (String) placa.subSequence(0, 3);
            final String numeros = (String) placa.subSequence(3, placa.length());
            if (StringUtils.isAlpha(letras) && StringUtils.isIntegerValuePositive(numeros)) {
                return letras + "-" + numeros;
            }
        }

        // Placa no novo padrão, não adicionamos o hífen.
        return placa;
    }

    @NotNull
    static String formatNumeroFogo(@NotNull final String codigoCliente) {
        return StringUtils.containsLetters(codigoCliente)
                ? codigoCliente
                : String.format("%07d", Integer.parseInt(codigoCliente));
    }

    @NotNull
    public static Long getCodUnidadeMovimentoFromCampoPersonalizado(
            @Nullable final List<CampoPersonalizadoResposta> respostasCamposPersonalizados) {
        if (respostasCamposPersonalizados == null || respostasCamposPersonalizados.isEmpty()) {
            throw new GlobusPiccoloturException("Nenhuma resposta fornecida para os campos personalizados");
        }

        final CampoPersonalizadoResposta campoSelecao = respostasCamposPersonalizados
                .stream()
                .filter(campo -> campo.getTipoCampo().equals(TipoCampoPersonalizado.LISTA_SELECAO))
                .findFirst()
                .orElseThrow(() -> {
                    throw new GlobusPiccoloturException("Resposta da unidade de movimento não foi fornecida");
                });

        if (campoSelecao.getRespostaListaSelecao() == null || campoSelecao.getRespostaListaSelecao().isEmpty()) {
            throw new GlobusPiccoloturException("Resposta da unidade de movimento não foi fornecida");
        }
        // Fazemos um get(0) pois temos a certeza que nunca terá mais que uma resposta.
        // A resposta sempre estará no padrão 'cod_unidade - nome_unidade', pegaremos apenas o código da unidade.
        final String resposta = campoSelecao.getRespostaListaSelecao().get(0);
        return Long.valueOf(resposta.split(COD_UNIDADE_NOME_LOCAL_MOVIMENTO_SEPARATOR)[0].trim());
    }
}