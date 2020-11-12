package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoAntesEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoAcopladoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoAcopladoListagemHolder;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoAcopladoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoDadosColetaKm;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.VeiculoVisualizacaoPneu;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created on 05/05/2020.
 *
 * @author Thais Francisco (https://github.com/thaisksf)
 */
public final class VeiculoConverter {

    private VeiculoConverter() {
        throw new IllegalStateException(VeiculoConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static VeiculoAntesEdicao createVeiculoAntesEdicao(@NotNull final ResultSet rSet) throws SQLException {
        return new VeiculoAntesEdicao(
                rSet.getString("ANTIGA_PLACA"),
                rSet.getString("ANTIGO_IDENTIFICADOR_FROTA"),
                rSet.getLong("ANTIGO_COD_TIPO"),
                rSet.getLong("ANTIGO_COD_MODELO"),
                rSet.getLong("ANTIGO_KM"),
                rSet.getBoolean("ANTIGO_STATUS"));
    }

    @NotNull
    public static VeiculoListagem createVeiculoListagem(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoAcopladoListagem> veiculosAcoplados) throws SQLException {
        return new VeiculoListagem(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("COD_REGIONAL"),
                rSet.getString("NOME_REGIONAL"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getString("NOME_UNIDADE"),
                rSet.getLong("KM"),
                rSet.getBoolean("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getString("MODELO"),
                rSet.getString("NOME_DIAGRAMA"),
                rSet.getLong("DIANTEIRO"),
                rSet.getLong("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"),
                rSet.getBoolean("MOTORIZADO"),
                rSet.getBoolean("POSSUI_HUBODOMETRO"),
                veiculosAcoplados);
    }

    @NotNull
    public static VeiculoVisualizacao createVeiculoVisualizacao(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoVisualizacaoPneu> pneus,
            @NotNull final List<VeiculoAcopladoVisualizacao> veiculosAcoplados) throws SQLException {
        return new VeiculoVisualizacao(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getLong("COD_UNIDADE"),
                rSet.getLong("COD_EMPRESA"),
                rSet.getLong("KM"),
                rSet.getBoolean("STATUS_ATIVO"),
                rSet.getLong("COD_TIPO"),
                rSet.getLong("COD_MODELO"),
                rSet.getLong("COD_DIAGRAMA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getString("MODELO"),
                rSet.getString("NOME_DIAGRAMA"),
                rSet.getLong("DIANTEIRO"),
                rSet.getLong("TRASEIRO"),
                rSet.getString("TIPO"),
                rSet.getString("MARCA"),
                rSet.getLong("COD_MARCA"),
                rSet.getBoolean("MOTORIZADO"),
                rSet.getBoolean("POSSUI_HUBODOMETRO"),
                pneus,
                veiculosAcoplados);
    }

    @NotNull
    public static VeiculoAcopladoVisualizacao createVeiculoAcopladoVisualizacao(@NotNull final ResultSet rSet)
            throws SQLException {
        return new VeiculoAcopladoVisualizacao(
                rSet.getLong("CODIGO"),
                rSet.getString("PLACA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getBoolean("MOTORIZADO"),
                rSet.getInt("POSICAO_ACOPLADO"));
    }

    @NotNull
    public static VeiculoAcopladoListagemHolder createVeiculoAcopladoListagemHolder(@NotNull final ResultSet rSet)
            throws SQLException {
        if (rSet.next()) {
            final List<VeiculoAcopladoListagem> veiculosAcoplados = createVeiculosAcopladosListagem(rSet);
            final Map<Long, List<VeiculoAcopladoListagem>> acoplamentosProcesso =
                    agrupaAcoplamentosPorProcesso(veiculosAcoplados);
            final Map<Long, List<VeiculoAcopladoListagem>> acoplamentosVeiculo =
                    agrupaAcoplamentosPorVeiculo(veiculosAcoplados, acoplamentosProcesso);
            return new VeiculoAcopladoListagemHolder(acoplamentosVeiculo);
        }

        return VeiculoAcopladoListagemHolder.EMPTY;
    }

    @NotNull
    public static List<VeiculoAcopladoListagem> createVeiculosAcopladosListagem(@NotNull final ResultSet rSet)
            throws SQLException {
        final List<VeiculoAcopladoListagem> veiculosAcoplados = new ArrayList<>();
        do {
            veiculosAcoplados.add(new VeiculoAcopladoListagem(
                    rSet.getLong("COD_PROCESSO_ACOPLAMENTO"),
                    rSet.getLong("COD_VEICULO"),
                    rSet.getString("PLACA"),
                    rSet.getString("IDENTIFICADOR_FROTA"),
                    rSet.getBoolean("MOTORIZADO"),
                    rSet.getInt("POSICAO_ACOPLADO")));
        } while (rSet.next());
        return veiculosAcoplados;
    }

    @NotNull
    private static Map<Long, List<VeiculoAcopladoListagem>> agrupaAcoplamentosPorProcesso(
            @NotNull final List<VeiculoAcopladoListagem> veiculosAcoplados) {
        return veiculosAcoplados
                .stream()
                .collect(Collectors.groupingBy(
                        VeiculoAcopladoListagem::getCodProcessoAcoplamento,
                        HashMap::new,
                        Collectors.toList()));
    }

    @NotNull
    private static Map<Long, List<VeiculoAcopladoListagem>> agrupaAcoplamentosPorVeiculo(
            @NotNull final List<VeiculoAcopladoListagem> veiculosAcoplados,
            @NotNull final Map<Long, List<VeiculoAcopladoListagem>> acoplamentosProcesso) {
        final Map<Long, List<VeiculoAcopladoListagem>> acoplamentosVeiculo = new HashMap<>();
        veiculosAcoplados
                .forEach(veiculoAcoplado -> {
                    final List<VeiculoAcopladoListagem> acoplamentos = acoplamentosProcesso
                            .get(veiculoAcoplado.getCodProcessoAcoplamento());
                    acoplamentosVeiculo.put(veiculoAcoplado.getCodVeiculo(), acoplamentos);
                });
        return acoplamentosVeiculo;
    }

    @NotNull
    public static VeiculoVisualizacaoPneu createVeiculoVisualizacaoPneu(@NotNull final ResultSet rSet)
            throws SQLException {
        return new VeiculoVisualizacaoPneu(
                rSet.getLong("CODIGO"),
                rSet.getString("CODIGO_CLIENTE"),
                rSet.getString("NOME_MARCA_PNEU"),
                rSet.getLong("COD_MARCA_PNEU"),
                rSet.getLong("COD_UNIDADE_ALOCADO"),
                rSet.getLong("COD_REGIONAL_ALOCADO"),
                rSet.getDouble("PRESSAO_ATUAL"),
                rSet.getInt("VIDA_ATUAL"),
                rSet.getInt("VIDA_TOTAL"),
                rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"),
                rSet.getString("NOME_MODELO_PNEU"),
                rSet.getLong("COD_MODELO_PNEU"),
                rSet.getInt("QT_SULCOS_MODELO_PNEU"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_PNEU"),
                rSet.getInt("ALTURA"),
                rSet.getInt("LARGURA"),
                rSet.getDouble("ARO"),
                rSet.getLong("COD_DIMENSAO"),
                rSet.getDouble("PRESSAO_RECOMENDADA"),
                rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"),
                rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"),
                rSet.getDouble("ALTURA_SULCO_INTERNO"),
                rSet.getDouble("ALTURA_SULCO_EXTERNO"),
                rSet.getString("DOT"),
                rSet.getDouble("VALOR"),
                rSet.getLong("COD_MODELO_BANDA"),
                rSet.getString("NOME_MODELO_BANDA"),
                rSet.getInt("QT_SULCOS_MODELO_BANDA"),
                rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"),
                rSet.getLong("COD_MARCA_BANDA"),
                rSet.getString("NOME_MARCA_BANDA"),
                rSet.getDouble("VALOR_BANDA"),
                rSet.getInt("POSICAO_PNEU"),
                rSet.getString("NOMENCLATURA"),
                rSet.getLong("COD_VEICULO_APLICADO"),
                rSet.getString("PLACA_APLICADO"));
    }

    @NotNull
    public static VeiculoDadosColetaKm createVeiculoDadosColetaKm(@NotNull final ResultSet rSet)
            throws SQLException {
        final VeiculoDadosColetaKm.
                VeiculoDadosTratorColetaKm.
                Builder builder = VeiculoDadosColetaKm
                .VeiculoDadosTratorColetaKm
                .builder()
                .withPlacaTrator(rSet.getString("PLACA_TRATOR"))
                .withIdentificadorFrotaTrator(rSet.getString("IDENTIFICADOR_FROTA_TRATOR"));

        final Long codVeiculoTrator = rSet.getLong("COD_VEICULO_TRATOR");
        final Long kmAtualTrator = rSet.getLong("KM_ATUAL_TRATOR");

        if (codVeiculoTrator != 0) {
            builder.withCodVeiculoTrator(codVeiculoTrator);
        }
        if (kmAtualTrator != 0) {
            builder.withKmAtualTrator(kmAtualTrator);
        }


        return VeiculoDadosColetaKm.of(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA"),
                rSet.getLong("KM_ATUAL"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getBoolean("MOTORIZADO"),
                rSet.getBoolean("POSSUI_HUBODOMETRO"),
                rSet.getBoolean("ACOPLADO"),
                rSet.getBoolean("DEVE_COLETAR_KM"),
                builder.build());
    }
}