package br.com.zalf.prolog.webservice.customfields;

import br.com.zalf.prolog.webservice.customfields._model.CampoPersonalizadoParaRealizacao;
import br.com.zalf.prolog.webservice.customfields._model.TipoCampoPersonalizado;
import org.jetbrains.annotations.NotNull;

import java.sql.Array;
import java.sql.ResultSet;

/**
 * Created on 2020-03-23
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class CampoPersonalizadoConverter {

    private CampoPersonalizadoConverter() {
        throw new IllegalStateException(CampoPersonalizadoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static CampoPersonalizadoParaRealizacao createCampoPersonalizadoParaRealizacao(
            @NotNull final ResultSet rSet) throws Throwable {
        final Array opcoesSelecao = rSet.getArray("OPCOES_SELECAO_CAMPO");
        return new CampoPersonalizadoParaRealizacao(
                rSet.getLong("COD_CAMPO"),
                rSet.getLong("COD_EMPRESA"),
                rSet.getShort("COD_FUNCAO_PROLOG_AGRUPAMENTO"),
                TipoCampoPersonalizado.fromCodigo(rSet.getInt("COD_TIPO_CAMPO")),
                rSet.getString("NOME_CAMPO"),
                rSet.getString("DESCRICAO_CAMPO"),
                rSet.getString("TEXTO_AUXILIO_PREENCHIMENTO_CAMPO"),
                rSet.getBoolean("PREENCHIMENTO_OBRIGATORIO_CAMPO"),
                rSet.getString("MENSAGEM_CASO_CAMPO_NAO_PREENCHIDO_CAMPO"),
                rSet.getBoolean("PERMITE_SELECAO_MULTIPLA_CAMPO"),
                opcoesSelecao != null ? ((String[]) opcoesSelecao.getArray()) : null,
                rSet.getShort("ORDEM_EXIBICAO"));
    }
}
