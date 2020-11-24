package br.com.zalf.prolog.webservice.frota.veiculo;

import br.com.zalf.prolog.webservice.frota.veiculo.model.edicao.VeiculoAntesEdicao;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoAcopladoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculoListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculosAcopladosListagem;
import br.com.zalf.prolog.webservice.frota.veiculo.model.listagem.VeiculosAcopladosPorVeiculo;
import br.com.zalf.prolog.webservice.frota.veiculo.model.visualizacao.*;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
    public static List<VeiculoListagem> createVeiculosListagem(
            @NotNull final ResultSet rSet,
            @NotNull final VeiculosAcopladosPorVeiculo veiculosAcopladosPorVeiculo) throws SQLException {
        if (rSet.next()) {
            final List<VeiculoListagem> veiculosListagem = new ArrayList<>();
            do {
                veiculosListagem.add(VeiculoConverter.createVeiculoListagem(rSet, veiculosAcopladosPorVeiculo));
            } while (rSet.next());
            return veiculosListagem;
        }

        return Collections.emptyList();
    }

    @NotNull
    public static VeiculoListagem createVeiculoListagem(
            @NotNull final ResultSet rSet,
            @NotNull final VeiculosAcopladosPorVeiculo veiculosAcoplados) throws SQLException {
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
                veiculosAcoplados.getVeiculosAcopladosByCodVeiculo(rSet.getLong("CODIGO")));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @NotNull
    public static VeiculoVisualizacao createVeiculoVisualizacao(
            @NotNull final ResultSet rSet,
            @NotNull final List<VeiculoVisualizacaoPneu> pneus,
            @NotNull final Optional<VeiculosAcopladosVisualizacao> veiculosAcoplados) throws SQLException {
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
                veiculosAcoplados.orElse(null));
    }

    @NotNull
    public static Optional<VeiculosAcopladosVisualizacao> createVeiculosAcopladosVisualizacao(
            @NotNull final ResultSet rSet) throws SQLException {
        if (rSet.next()) {
            final List<VeiculoAcopladoVisualizacao> veiculosAcoplados = new ArrayList<>();
            final Long codProcessoAcoplamento = rSet.getLong("COD_PROCESSO_ACOPLAMENTO");
            do {
                veiculosAcoplados.add(VeiculoConverter.createVeiculoAcopladoVisualizacao(rSet));
            } while (rSet.next());
            return Optional.of(new VeiculosAcopladosVisualizacao(codProcessoAcoplamento, veiculosAcoplados));
        }

        return Optional.empty();
    }

    @NotNull
    private static VeiculoAcopladoVisualizacao createVeiculoAcopladoVisualizacao(@NotNull final ResultSet rSet)
            throws SQLException {
        return new VeiculoAcopladoVisualizacao(
                rSet.getLong("COD_VEICULO"),
                rSet.getString("PLACA"),
                rSet.getString("IDENTIFICADOR_FROTA"),
                rSet.getBoolean("MOTORIZADO"),
                rSet.getInt("POSICAO_ACOPLADO"));
    }

    @NotNull
    public static VeiculosAcopladosPorVeiculo createVeiculosAcopladosPorVeiculo(@NotNull final ResultSet rSet)
            throws SQLException {
        final Optional<List<VeiculosAcopladosListagem>> optional = createVeiculosAcopladosListagem(rSet);
        return optional
                .map(VeiculoConverter::createAcoplamentosPorVeiculo)
                .orElse(VeiculosAcopladosPorVeiculo.EMPTY);
    }

    @NotNull
    private static Optional<List<VeiculosAcopladosListagem>> createVeiculosAcopladosListagem(
            @NotNull final ResultSet rSet) throws SQLException {
        Long codProcessoAntigo = null;
        VeiculosAcopladosListagem veiculoListagem = null;
        if (rSet.next()) {
            final List<VeiculosAcopladosListagem> veiculosAcopladosListagem = new ArrayList<>();

            do {
                final Long codProcessoAtual = rSet.getLong("COD_PROCESSO_ACOPLAMENTO");
                if (codProcessoAntigo == null || !codProcessoAntigo.equals(codProcessoAtual)) {
                    veiculoListagem = new VeiculosAcopladosListagem(codProcessoAtual, new ArrayList<>());
                    veiculosAcopladosListagem.add(veiculoListagem);
                }

                veiculoListagem.add(new VeiculoAcopladoListagem(
                        rSet.getLong("COD_VEICULO"),
                        rSet.getString("PLACA"),
                        rSet.getString("IDENTIFICADOR_FROTA"),
                        rSet.getBoolean("MOTORIZADO"),
                        rSet.getInt("POSICAO_ACOPLADO")));

                codProcessoAntigo = codProcessoAtual;
            } while (rSet.next());
            return Optional.of(veiculosAcopladosListagem);
        }
        return Optional.empty();
    }

    @NotNull
    private static VeiculosAcopladosPorVeiculo createAcoplamentosPorVeiculo(
            @NotNull final List<VeiculosAcopladosListagem> veiculosAcoplados) {
        final Map<Long, VeiculosAcopladosListagem> acoplamentosPorVeiculo = new HashMap<>();
        for (final VeiculosAcopladosListagem acoplamentosProcesso : veiculosAcoplados) {
            acoplamentosProcesso
                    .getVeiculosAcoplados()
                    .forEach(acoplamentosVeiculo -> acoplamentosPorVeiculo.put(
                            acoplamentosVeiculo.getCodVeiculo(),
                            acoplamentosProcesso));
        }
        return new VeiculosAcopladosPorVeiculo(acoplamentosPorVeiculo);
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
        final VeiculoDadosColetaKm.VeiculoDadosTratorColetaKm.Builder builder = VeiculoDadosColetaKm
                .VeiculoDadosTratorColetaKm
                .builder()
                .withPlacaTrator(rSet.getString("PLACA_TRATOR"))
                .withIdentificadorFrotaTrator(rSet.getString("IDENTIFICADOR_FROTA_TRATOR"));

        final long codVeiculoTrator = rSet.getLong("COD_VEICULO_TRATOR");
        final long kmAtualTrator = rSet.getLong("KM_ATUAL_TRATOR");

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