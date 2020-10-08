package br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheus;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheusInfosPneu;
import br.com.zalf.prolog.webservice.frota.pneu.afericao.relatorios._model.AfericaoExportacaoProtheusInfosVeiculo;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Created on 2020-10-08
 *
 * @author Gustavo Navarro (https://github.com/gustavocnp95)
 */
public final class AfericaoRelatorioConverter {
    public static AfericaoExportacaoProtheus createAfericaoExportacaoProtheus(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheus(
                createAfericaoExportacaoProtheusInfosVeiculo(rSet),
                createAfericaoExportacaoProtheusInfosPneu(rSet)
        );
    }

    private static AfericaoExportacaoProtheusInfosVeiculo createAfericaoExportacaoProtheusInfosVeiculo(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheusInfosVeiculo(
                rSet.getString("cabecalho_linha_um"),
                rSet.getString("placa"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy").format(rSet.getObject("data", LocalDate.class)),
                DateTimeFormatter.ofPattern("hh:mm").format(rSet.getObject("hora", LocalTime.class))
        );
    }

    private static AfericaoExportacaoProtheusInfosPneu createAfericaoExportacaoProtheusInfosPneu(
            @NotNull final ResultSet rSet) throws Throwable {
        return new AfericaoExportacaoProtheusInfosPneu(
                rSet.getString("cabecalho_linha_dois"),
                rSet.getString("codigo_cliente_pneu"),
                rSet.getString("nomenclatura_posicao"),
                rSet.getDouble("calibragem_aferida"),
                rSet.getDouble("calibragem_realizada"),
                rSet.getDouble("sulco_interno"),
                rSet.getDouble("sulco_central_interno"),
                rSet.getDouble("sulco_externo")
        );
    }
}
