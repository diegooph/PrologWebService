package br.com.zalf.prolog.webservice.integracao.integrador;

import br.com.zalf.prolog.webservice.frota.checklist.model.PrioridadeAlternativa;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusItemOrdemServico;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.StatusOrdemServico;
import br.com.zalf.prolog.webservice.frota.pneu.afericao._model.ConfiguracaoNovaAfericaoPlaca;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.ItemOsIntegracao;
import br.com.zalf.prolog.webservice.integracao.avacorpavilan._model.OsIntegracao;
import br.com.zalf.prolog.webservice.integracao.integrador._model.*;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import static br.com.zalf.prolog.webservice.frota.pneu.afericao.configuracao._model.FormaColetaDadosAfericaoEnum.fromString;

/**
 * Created on 2020-08-19
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class IntegracaoConverter {
    private IntegracaoConverter() {
        throw new IllegalStateException(IntegracaoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static OsIntegracao createOsIntegracao(@NotNull final ResultSet rSet) throws Throwable {
        final StatusOrdemServico statusOs = StatusOrdemServico.fromString(rSet.getString("status_os"));
        return new OsIntegracao(
                rSet.getLong("cod_unidade"),
                rSet.getString("cod_auxiliar_unidade"),
                rSet.getLong("cod_interno_os_prolog"),
                rSet.getLong("cod_os_prolog"),
                rSet.getObject("data_hora_abertura_os", LocalDateTime.class),
                rSet.getString("placa_veiculo"),
                rSet.getLong("km_veiculo_na_abertura"),
                rSet.getString("cpf_colaborador_checklist"),
                statusOs,
                statusOs == StatusOrdemServico.ABERTA ?
                        null : rSet.getObject("data_hora_fechamento_os", LocalDateTime.class),
                new ArrayList<>());
    }

    @NotNull
    public static ItemOsIntegracao createItemOsIntegracao(@NotNull final ResultSet rSet) throws Throwable {
        final StatusItemOrdemServico statusItemOs = StatusItemOrdemServico.fromString(rSet.getString("status_item_os"));
        return new ItemOsIntegracao(
                rSet.getLong("cod_item_os"),
                rSet.getLong("cod_alternativa"),
                rSet.getString("cod_auxiliar_alternativa"),
                rSet.getString("descricao_alternativa"),
                PrioridadeAlternativa.fromString(rSet.getString("prioridade_alternativa")),
                statusItemOs,
                rSet.getBoolean("alternativa_tipo_outros"),
                rSet.getString("descricao_tipo_outros"),
                rSet.getObject("data_hora_fechamento_item_os", LocalDateTime.class),
                rSet.getString("descricao_fechamento_item_os"),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getLong("km_veiculo_fechamento_item"),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getObject("data_hora_inicio_resolucao", LocalDateTime.class),
                statusItemOs == StatusItemOrdemServico.PENDENTE ?
                        null : rSet.getObject("data_hora_fim_resolucao", LocalDateTime.class));
    }

    @NotNull
    public static UnidadeDeParaHolder createUnidadeDeParaHolder(@NotNull final ResultSet rSet) throws Throwable {
        return new UnidadeDeParaHolder(rSet.getLong("cod_empresa_prolog"),
                                       new ArrayList<>());
    }

    @NotNull
    public static UnidadeDePara createUnidadeDePara(@NotNull final ResultSet rSet) throws Throwable {
        return new UnidadeDePara(rSet.getLong("cod_unidade_prolog"),
                                 rSet.getString("cod_auxiliar_unidade"));
    }

    @NotNull
    public static UnidadeRestricao createUnidadeRestricao(@NotNull final ResultSet rSet) throws Throwable {
        return new UnidadeRestricao(
                rSet.getLong("cod_unidade"),
                rSet.getInt("periodo_dias_afericao_sulco"),
                rSet.getInt("periodo_dias_afericao_pressao"));
    }

    @NotNull
    public static UnidadeRestricaoHolder createUnidadeRestricaoHolder(
            @NotNull final Map<String, UnidadeRestricao> unidadeRestricao) {
        return new UnidadeRestricaoHolder(unidadeRestricao);
    }

    @NotNull
    public static TipoVeiculoConfigAfericao createTipoVeiculoConfigAfericao(
            @NotNull final ResultSet rSet) throws Throwable {
        return new TipoVeiculoConfigAfericao(
                rSet.getLong("cod_unidade"),
                rSet.getLong("cod_tipo_veiculo"),
                fromString(rSet.getString("forma_coleta_dados_sulco")),
                fromString(rSet.getString("forma_coleta_dados_pressao")),
                fromString(rSet.getString("forma_coleta_dados_sulco_pressao")),
                rSet.getBoolean("pode_aferir_estepe"));
    }

    @NotNull
    public static TipoVeiculoConfigAfericaoHolder createTipoVeiculoConfigAfericaoHolder(
            @NotNull final Table<String, String, TipoVeiculoConfigAfericao> tipoVeiculoConfiguracao) {
        return new TipoVeiculoConfigAfericaoHolder(tipoVeiculoConfiguracao);
    }

    @NotNull
    public static AfericaoRealizadaPlaca createAfericaoRealizadaPlaca(@NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoRealizadaPlaca(
                rSet.getString("placa_afericao"),
                rSet.getInt("intervalo_sulco"),
                rSet.getInt("intervalo_pressao"));
    }

    @NotNull
    public static AfericaoRealizadaPlacaHolder createAfericaoRealizadaPlacaHolder(
            @NotNull final Map<String, AfericaoRealizadaPlaca> afericaoRealizadaPlaca) {
        return new AfericaoRealizadaPlacaHolder(afericaoRealizadaPlaca);
    }

    @NotNull
    public static ConfiguracaoNovaAfericaoPlaca createConfiguracaoNovaAfericaoPlaca(
            @NotNull final ResultSet rSet) throws Throwable {
        return new ConfiguracaoNovaAfericaoPlaca(
                fromString(rSet.getString("forma_coleta_dados_sulco")),
                fromString(rSet.getString("forma_coleta_dados_pressao")),
                fromString(rSet.getString("forma_coleta_dados_sulco_pressao")),
                rSet.getBoolean("pode_aferir_estepe"),
                rSet.getDouble("sulco_minimo_descarte"),
                rSet.getDouble("sulco_minimo_recapagem"),
                rSet.getDouble("tolerancia_inspecao"),
                rSet.getDouble("tolerancia_calibragem"),
                rSet.getInt("periodo_afericao_sulco"),
                rSet.getInt("periodo_afericao_pressao"),
                rSet.getDouble("variacao_aceita_sulco_menor_milimetros"),
                rSet.getDouble("variacao_aceita_sulco_maior_milimetros"),
                rSet.getBoolean("variacoes_sulco_default_prolog"),
                rSet.getBoolean("bloquear_valores_menores"),
                rSet.getBoolean("bloquear_valores_maiores"));
    }
}
