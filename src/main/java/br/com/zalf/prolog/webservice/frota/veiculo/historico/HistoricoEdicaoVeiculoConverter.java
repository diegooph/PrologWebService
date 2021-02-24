package br.com.zalf.prolog.webservice.frota.veiculo.historico;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.frota.veiculo.historico._model.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created on 2020-09-14
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class HistoricoEdicaoVeiculoConverter {

    @NotNull
    public static HistoricoEdicaoVeiculo createHistoricoEdicaoVeiculo(@NotNull final EstadoVeiculo estadoAntigo,
                                                                      @NotNull final EstadoVeiculo estadoNovo) {
        //noinspection ConstantConditions
        return new HistoricoEdicaoVeiculo(
                estadoAntigo.getCodColaboradorEdicao(),
                estadoAntigo.getNomeColaboradorEdicao(),
                OrigemAcaoEnum.fromString(estadoAntigo.getOrigemEdicao()),
                estadoAntigo.getOrigemEdicaoLegivel(),
                estadoAntigo.getDataHoraEdicao(),
                estadoAntigo.getInformacoesExtras(),
                estadoAntigo.getTotalEdicoes(),
                createEdicoesVeiculo(estadoAntigo.getValoresModificaveis(), estadoNovo.getValoresModificaveis()));
    }

    @NotNull
    private static List<EdicaoVeiculo> createEdicoesVeiculo(@NotNull final Map<TipoAlteracaoEnum, Object> valoresAntigos,
                                                            @NotNull final Map<TipoAlteracaoEnum, Object> valoresNovos) {
        final List<EdicaoVeiculo> edicoesVeiculo = new ArrayList<>();
        valoresAntigos
                .keySet()
                .stream()
                .filter(key -> !Objects.equals(valoresAntigos.get(key), (valoresNovos.get(key))))
                .forEach(key -> edicoesVeiculo.add(
                        createEdicaoVeiculo(
                                valoresAntigos.get(key),
                                valoresNovos.get(key),
                                key)));
        return edicoesVeiculo;
    }

    @NotNull
    private static EdicaoVeiculo createEdicaoVeiculo(@Nullable final Object valorAntigo,
                                                     @Nullable final Object valorNovo,
                                                     @NotNull final TipoAlteracaoEnum tipoAlteracao) {
        if (valorAntigo == null && valorNovo == null) {
            throw new IllegalStateException("O valor antigo e o valor novo não podem ser nulos juntos!");
        }
        return new EdicaoVeiculo(tipoAlteracao, valorAntigo, valorNovo);
    }

    @NotNull
    public static EstadoVeiculo createEstadoVeiculo(@NotNull final ResultSet rSet) throws Throwable {
        return new EstadoVeiculo(
                rSet.getLong("codigo_veiculo_edicao"),
                NullIf.equalOrLess(rSet.getLong("codigo_colaborador_edicao"), 0),
                rSet.getString("nome_colaborador_edicao"),
                rSet.getString("origem_edicao"),
                rSet.getString("origem_edicao_legivel"),
                rSet.getObject("data_hora_edicao", LocalDateTime.class),
                rSet.getInt("total_edicoes"),
                rSet.getString("informacoes_extras"),
                createValoresModificaveis(rSet));
    }

    @NotNull
    public static Map<TipoAlteracaoEnum, Object> createValoresModificaveis(@NotNull final ResultSet rSet) throws Throwable {
        final Map<TipoAlteracaoEnum, Object> valoresModificaveis = new LinkedHashMap<>();
        valoresModificaveis.put(TipoAlteracaoEnum.PLACA, rSet.getString("placa"));
        valoresModificaveis.put(TipoAlteracaoEnum.IDENTIFICADOR_FROTA, rSet.getString("identificador_frota"));
        valoresModificaveis.put(TipoAlteracaoEnum.KM_VEICULO, rSet.getLong("km_veiculo"));
        valoresModificaveis.put(TipoAlteracaoEnum.STATUS_ATIVO, rSet.getBoolean("status"));
        valoresModificaveis.put(TipoAlteracaoEnum.DIGRAMA_VEICULO, rSet.getString("diagrama_veiculo"));
        valoresModificaveis.put(TipoAlteracaoEnum.TIPO_VEICULO, rSet.getString("tipo_veiculo"));
        valoresModificaveis.put(TipoAlteracaoEnum.MARCA_VEICULO, rSet.getString("marca_veiculo"));
        valoresModificaveis.put(TipoAlteracaoEnum.MODELO_VEICULO, rSet.getString("modelo_veiculo"));
        valoresModificaveis.put(TipoAlteracaoEnum.POSSUI_HUBODOMETRO, rSet.getBoolean("possui_hubodometro"));
        return valoresModificaveis;
    }

}