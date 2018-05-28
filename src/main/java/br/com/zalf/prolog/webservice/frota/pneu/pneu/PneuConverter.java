package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
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
    public static Pneu createPneuCompleto(@NotNull final ResultSet rSet) throws SQLException {
        final Pneu pneu = new Pneu();

        pneu.setCodigo(rSet.getLong("CODIGO"));
        pneu.setCodigoCliente(rSet.getString("CODIGO_CLIENTE"));
        pneu.setPosicao(rSet.getInt("POSICAO_PNEU"));
        pneu.setDot(rSet.getString("DOT"));
        pneu.setValor(rSet.getBigDecimal("VALOR"));
        pneu.setCodUnidadeAlocado(rSet.getLong("COD_UNIDADE_ALOCADO"));
        pneu.setCodRegionalAlocado(rSet.getLong("COD_REGIONAL_ALOCADO"));
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

        final Pneu.Dimensao dimensao = new Pneu.Dimensao();
        dimensao.codigo = rSet.getLong("COD_DIMENSAO");
        dimensao.altura = rSet.getInt("ALTURA");
        dimensao.largura = rSet.getInt("LARGURA");
        dimensao.aro = rSet.getDouble("ARO");
        pneu.setDimensao(dimensao);

        final Sulcos sulcoAtual = new Sulcos();
        sulcoAtual.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        sulcoAtual.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        sulcoAtual.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        sulcoAtual.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        pneu.setSulcosAtuais(sulcoAtual);

        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_ATUAL"));
        pneu.setStatus(StatusPneu.fromString(rSet.getString("STATUS")));
        pneu.setVidaAtual(rSet.getInt("VIDA_ATUAL"));
        pneu.setVidasTotal(rSet.getInt("VIDA_TOTAL"));
        return pneu;
    }

    @NotNull
    public static PneuAnalise createPneuAnaliseCompleto(@NotNull final ResultSet rSet)
            throws SQLException {
        final Pneu pneu = createPneuCompleto(rSet);
        final PneuAnalise pneuAnalise = new PneuAnalise();
        pneuAnalise.setCodigo(pneu.getCodigo());
        pneuAnalise.setCodigoCliente(pneu.getCodigoCliente());
        pneuAnalise.setPosicao(pneu.getPosicao());
        pneuAnalise.setDot(pneu.getDot());
        pneuAnalise.setValor(pneu.getValor());
        pneuAnalise.setCodUnidadeAlocado(pneu.getCodUnidadeAlocado());
        pneuAnalise.setCodRegionalAlocado(pneu.getCodRegionalAlocado());
        pneuAnalise.setPneuNovoNuncaRodado(pneu.isPneuNovoNuncaRodado());
        pneuAnalise.setMarca(pneu.getMarca());
        pneuAnalise.setModelo(pneu.getModelo());
        pneuAnalise.setBanda(pneu.getBanda());
        pneuAnalise.setDimensao(pneu.getDimensao());
        pneuAnalise.setSulcosAtuais(pneu.getSulcosAtuais());
        pneuAnalise.setPressaoCorreta(pneu.getPressaoCorreta());
        pneuAnalise.setPressaoAtual(pneu.getPressaoAtual());
        pneuAnalise.setStatus(pneu.getStatus());
        pneuAnalise.setVidaAtual(pneu.getVidaAtual());
        pneuAnalise.setVidasTotal(pneu.getVidasTotal());
        // Seta informações extras do pneu que está em Análise.
        pneuAnalise.setRecapadora(createRecapadoraPneu(rSet));
        pneuAnalise.setCodigoColeta(rSet.getString("COD_COLETA"));
        return pneuAnalise;
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
            banda.setModelo(createModeloBanda(rSet));
            banda.setMarca(createMarcaBanda(rSet));
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
            // pneus com essa inconsistência em banco. Isso será eliminado no futuro e poderemos lançar uma exceção aqui.
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