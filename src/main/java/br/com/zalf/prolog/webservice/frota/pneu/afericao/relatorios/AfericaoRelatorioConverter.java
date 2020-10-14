package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.commons.util.NullIf;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheus;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheusInfos;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheusInfosPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheusInfosVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class AfericaoRelatorioConverter {
    @NotNull
    public static List<AfericaoExportacaoProtheus> createAfericaoExportacaoProtheus(
            @NotNull final ResultSet rSet) throws Throwable {
        final List<AfericaoExportacaoProtheus> afericoesExportacaoProtheus = new ArrayList<>();
        long codAfericaoAntiga = -1;
        do {
            final long codAfericaoAtual = rSet.getLong("codigo_afericao");
            if (codAfericaoAtual != codAfericaoAntiga) {
                afericoesExportacaoProtheus.add(
                        new AfericaoExportacaoProtheus(codAfericaoAtual, new ArrayList<>()));
            }
            afericoesExportacaoProtheus
                    .get(afericoesExportacaoProtheus.size() - 1)
                    .getInfosAfericao()
                    .add(createAfericaoExportacaoProtheusInfos(rSet));
            codAfericaoAntiga = codAfericaoAtual;
        } while (rSet.next());
        return afericoesExportacaoProtheus;
    }

    @NotNull
    private static AfericaoExportacaoProtheusInfos createAfericaoExportacaoProtheusInfos(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheusInfos(
                createAfericaoExportacaoProtheusInfosVeiculo(rSet),
                createAfericaoExportacaoProtheusInfosPneu(rSet)
        );
    }

    @NotNull
    private static AfericaoExportacaoProtheusInfosVeiculo createAfericaoExportacaoProtheusInfosVeiculo(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheusInfosVeiculo(
                rSet.getString("cabecalho_placa"),
                rSet.getString("placa"),
                rSet.getString("data"),
                rSet.getString("hora")
        );
    }

    @NotNull
    private static AfericaoExportacaoProtheusInfosPneu createAfericaoExportacaoProtheusInfosPneu(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheusInfosPneu(
                rSet.getString("cabecalho_pneu"),
                rSet.getString("codigo_cliente_pneu"),
                rSet.getString("nomenclatura_posicao"),
                NullIf.less(rSet.getDouble("calibragem_aferida"), 0.00),
                NullIf.less(rSet.getDouble("calibragem_realizada"), 0.00),
                NullIf.less(rSet.getDouble("sulco_interno"), 0.00),
                NullIf.less(rSet.getDouble("sulco_central"), 0.00),
                NullIf.less(rSet.getDouble("sulco_externo"), 0.00)
        );
    }
}
