package br.com.zalf.prolog.webservice.frota.pneu;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu._model.*;
import br.com.zalf.prolog.webservice.frota.pneu.recapadoras.Recapadora;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 11/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PneuConverter {
    private static final String TAG = PneuConverter.class.getSimpleName();

    private PneuConverter() {
        throw new IllegalStateException(PneuConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    @NotNull
    public static Pneu createPneuCompleto(@NotNull final ResultSet rSet,
                                          @NotNull final PneuTipo pneuTipo,
                                          final boolean listagem) throws SQLException {
        final Pneu pneu = pneuTipo.createNew();
        pneu.setCodigo(rSet.getLong("CODIGO"));
        pneu.setCodigoCliente(rSet.getString("CODIGO_CLIENTE"));
        pneu.setPosicao(rSet.getInt("POSICAO_PNEU"));
        pneu.setDot(rSet.getString("DOT"));
        pneu.setValor(rSet.getBigDecimal("VALOR"));
        pneu.setCodUnidadeAlocado(rSet.getLong("COD_UNIDADE_ALOCADO"));
        pneu.setCodRegionalAlocado(rSet.getLong("COD_REGIONAL_ALOCADO"));
        if (listagem) {
            pneu.setNomeUnidadeAlocado(rSet.getString("NOME_UNIDADE_ALOCADO"));
            pneu.setNomeRegionalAlocado(rSet.getString("NOME_REGIONAL_ALOCADO"));
        }
        pneu.setPneuNovoNuncaRodado(rSet.getBoolean("PNEU_NOVO_NUNCA_RODADO"));

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(rSet.getLong("COD_MARCA_PNEU"));
        marcaPneu.setNome(rSet.getString("NOME_MARCA_PNEU"));
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(rSet.getLong("COD_MODELO_PNEU"));
        modeloPneu.setNome(rSet.getString("NOME_MODELO_PNEU"));
        modeloPneu.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_MODELO_PNEU"));
        modeloPneu.setAlturaSulcos(rSet.getDouble("ALTURA_SULCOS_MODELO_PNEU"));
        pneu.setModelo(modeloPneu);

        pneu.setBanda(createBanda(pneu, rSet));

        final PneuComum.Dimensao dimensao = new PneuComum.Dimensao();
        dimensao.codigo = rSet.getLong("COD_DIMENSAO");
        dimensao.altura = rSet.getInt("ALTURA");
        dimensao.largura = rSet.getInt("LARGURA");
        dimensao.aro = rSet.getDouble("ARO");
        pneu.setDimensao(dimensao);

        final double sulcoCentralInterno = rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO");
        final double sulcoCentralExterno = rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO");
        final double sulcoExterno = rSet.getDouble("ALTURA_SULCO_EXTERNO");
        final double sulcoInterno = rSet.getDouble("ALTURA_SULCO_INTERNO");
        final boolean temSulcosAtuais = !rSet.wasNull();
        if (temSulcosAtuais) {
            final Sulcos sulcosAtuais = new Sulcos();
            sulcosAtuais.setCentralInterno(sulcoCentralInterno);
            sulcosAtuais.setCentralExterno(sulcoCentralExterno);
            sulcosAtuais.setExterno(sulcoExterno);
            sulcosAtuais.setInterno(sulcoInterno);
            pneu.setSulcosAtuais(sulcosAtuais);
        }

        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_ATUAL"));
        pneu.setStatus(StatusPneu.fromString(rSet.getString("STATUS")));
        pneu.setVidaAtual(rSet.getInt("VIDA_ATUAL"));
        pneu.setVidasTotal(rSet.getInt("VIDA_TOTAL"));

        if (pneu instanceof PneuEmUso) {
            final PneuEmUso pneuEmUso = (PneuEmUso) pneu;
            pneuEmUso.setPlaca(rSet.getString("PLACA_APLICADO"));
            pneuEmUso.setIdentificadorFrota(rSet.getString("IDENTIFICADOR_FROTA"));
            pneuEmUso.setCodVeiculo(rSet.getLong("COD_VEICULO_APLICADO"));
            pneuEmUso.setPosicaoAplicado(rSet.getString("POSICAO_APLICADO_CLIENTE"));
        }

        return pneu;
    }

    @NotNull
    public static PneuAnalise createPneuAnaliseCompleto(@NotNull final ResultSet rSet)
            throws SQLException {
        final PneuAnalise pneuAnalise = (PneuAnalise) createPneuCompleto(rSet, PneuTipo.PNEU_ANALISE, false);
        // Seta informações extras do pneu que está em Análise.
        pneuAnalise.setRecapadora(createRecapadoraPneu(rSet));
        pneuAnalise.setCodigoColeta(rSet.getString("COD_COLETA"));
        return pneuAnalise;
    }

    @NotNull
    public static PneuRetornoDescarteSuccess createPneuRetornoDescarteSuccess(@NotNull final ResultSet rSet)
            throws SQLException {
        return PneuRetornoDescarteSuccess.builder()
                .codPneuRetornado(rSet.getLong("cod_pneu_retornado"))
                .codMovimentacaoGerada(rSet.getLong("cod_movimentacao_gerada"))
                .build();
    }

    @NotNull
    private static Recapadora createRecapadoraPneu(@NotNull final ResultSet rSet) throws SQLException {
        final Recapadora recapadora = new Recapadora();
        recapadora.setCodigo(rSet.getLong("COD_RECAPADORA"));
        recapadora.setNome(rSet.getString("NOME_RECAPADORA"));
        recapadora.setCodEmpresa(rSet.getLong("COD_EMPRESA_RECAPADORA"));
        recapadora.setAtiva(rSet.getBoolean("RECAPADORA_ATIVA"));
        return recapadora;
    }

    @Nullable
    private static Banda createBanda(@NotNull final Pneu pneu, @NotNull final ResultSet rSet) throws SQLException {
        if (rSet.getString("COD_MODELO_BANDA") != null) {
            final Banda banda = new Banda();
            banda.setMarca(createMarcaBanda(rSet));
            banda.setModelo(createModeloBanda(rSet));
            banda.setValor(rSet.getBigDecimal("VALOR_BANDA"));
            return banda;
        } else if (rSet.getInt("VIDA_ATUAL") == 1) {
            final Banda banda = new Banda();
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setQuantidadeSulcos(pneu.getModelo().getQuantidadeSulcos());
            modeloBanda.setCodigo(pneu.getModelo().getCodigo());
            modeloBanda.setNome(pneu.getModelo().getNome());
            modeloBanda.setAlturaSulcos(pneu.getModelo().getAlturaSulcos());
            banda.setModelo(modeloBanda);
            banda.setMarca(pneu.getMarca());
            return banda;
        } else {
            // TODO: 12/01/2018 - Atualmente não podemos quebrar o servidor caso atinja esse estado porque possuimos
            // pneus com essa inconsistência em banco. Isso será eliminado no futuro e poderemos lançar uma exceção
            // aqui.
            Log.w(TAG, "Esse estado é uma inconsistência e não deveria acontecer! " +
                    "Algum pneu está acima da primeira vida porém não possui banda associada.");
            return null;
        }
    }

    @NotNull
    private static Marca createMarcaBanda(@NotNull final ResultSet rSet) throws SQLException {
        final Marca marcaBanda = new Marca();
        marcaBanda.setCodigo(rSet.getLong("COD_MARCA_BANDA"));
        marcaBanda.setNome(rSet.getString("NOME_MARCA_BANDA"));
        return marcaBanda;
    }

    @NotNull
    private static ModeloBanda createModeloBanda(@NotNull final ResultSet rSet) throws SQLException {
        final ModeloBanda modeloBanda = new ModeloBanda();
        modeloBanda.setCodigo(rSet.getLong("COD_MODELO_BANDA"));
        modeloBanda.setNome(rSet.getString("NOME_MODELO_BANDA"));
        modeloBanda.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_MODELO_BANDA"));
        modeloBanda.setAlturaSulcos(rSet.getDouble("ALTURA_SULCOS_MODELO_BANDA"));
        return modeloBanda;
    }
}