package br.com.zalf.prolog.webservice.frota.pneu.pneu;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.*;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Marca;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created on 11/01/18.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public final class PneuConverter {

    private PneuConverter() {
    }

    @NotNull
    public static Pneu createPneuCompleto(@NotNull final ResultSet rSet) throws SQLException {
        final Pneu pneu = new Pneu();

        pneu.setCodigo(rSet.getString("CODIGO"));
        pneu.setDot(rSet.getString("DOT"));
        pneu.setValor(rSet.getBigDecimal("VALOR"));
        pneu.setCodUnidadeAlocado(rSet.getLong("COD_UNIDADE"));
        pneu.setCodRegionalAlocado(rSet.getLong("COD_REGIONAL"));

        final Marca marcaPneu = new Marca();
        marcaPneu.setCodigo(rSet.getLong("COD_MARCA"));
        marcaPneu.setNome(rSet.getString("MARCA"));
        pneu.setMarca(marcaPneu);

        final ModeloPneu modeloPneu = new ModeloPneu();
        modeloPneu.setCodigo(rSet.getLong("COD_MODELO"));
        modeloPneu.setNome(rSet.getString("MODELO"));
        modeloPneu.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_MODELO"));
        pneu.setModelo(modeloPneu);

        pneu.setBanda(createBanda(pneu, rSet));

        final Pneu.Dimensao dimensao = new Pneu.Dimensao();
        dimensao.codigo = rSet.getLong("COD_DIMENSAO");
        dimensao.altura = rSet.getInt("ALTURA");
        dimensao.aro = rSet.getInt("ARO");
        dimensao.largura = rSet.getInt("LARGURA");
        pneu.setDimensao(dimensao);

        final Sulcos sulcoAtual = new Sulcos();
        sulcoAtual.setCentralInterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_INTERNO"));
        sulcoAtual.setCentralExterno(rSet.getDouble("ALTURA_SULCO_CENTRAL_EXTERNO"));
        sulcoAtual.setExterno(rSet.getDouble("ALTURA_SULCO_EXTERNO"));
        sulcoAtual.setInterno(rSet.getDouble("ALTURA_SULCO_INTERNO"));
        pneu.setSulcosAtuais(sulcoAtual);

        final Sulcos sulcoNovo = new Sulcos();
        sulcoNovo.setCentralExterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setCentralInterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setExterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        sulcoNovo.setInterno(rSet.getDouble("ALTURA_SULCOS_NOVOS"));
        pneu.setSulcosPneuNovo(sulcoNovo);

        pneu.setPressaoCorreta(rSet.getDouble("PRESSAO_RECOMENDADA"));
        pneu.setPressaoAtual(rSet.getDouble("PRESSAO_ATUAL"));
        pneu.setStatus(rSet.getString("STATUS"));
        pneu.setVidaAtual(rSet.getInt("VIDA_ATUAL"));
        pneu.setVidasTotal(rSet.getInt("VIDA_TOTAL"));
        return pneu;
    }

    @NotNull
    private static Banda createBanda(@NotNull final Pneu pneu, @NotNull final ResultSet rSet) throws SQLException {
        final Banda banda = new Banda();
        if (rSet.getString("COD_MARCA_BANDA") != null) {
            banda.setModelo(createModeloBanda(rSet));
            banda.setMarca(createMarcaBanda(rSet));
            banda.setValor(rSet.getBigDecimal("VALOR_BANDA"));
        } else {
            final ModeloBanda modeloBanda = new ModeloBanda();
            modeloBanda.setQuantidadeSulcos(pneu.getModelo().getQuantidadeSulcos());
            modeloBanda.setCodigo(pneu.getModelo().getCodigo());
            modeloBanda.setNome(pneu.getModelo().getNome());
            banda.setModelo(modeloBanda);
            banda.setMarca(pneu.getMarca());
        }
        return banda;
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
        modeloBanda.setQuantidadeSulcos(rSet.getInt("QT_SULCOS_BANDA"));
        return modeloBanda;
    }
}
