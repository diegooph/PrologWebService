package br.com.zalf.prolog.webservice.customfields._model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 2020-03-19
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class CampoPersonalizadoParaRealizacao {
    @NotNull
    @EqualsAndHashCode.Include
    private final Long codigo;
    @NotNull
    private final Long codEmpresa;
    private final Short codFuncaoProlog;
    @NotNull
    private final TipoCampoPersonalizado tipoCampo;
    @NotNull
    private final String nomeCampo;
    @NotNull
    private final String descricaoCampo;
    @Nullable
    private final String textoAuxilioPreenchimento;
    private final boolean preenchimentoObrigatorio;
    @Nullable
    private final String mensagemCasoCampoNaoPreenchido;
    @Nullable
    private final Boolean permiteSelecaoMultipla;
    @Nullable
    private final String[] opcoesSelecao;
    private final short ordemExibicao;
}
