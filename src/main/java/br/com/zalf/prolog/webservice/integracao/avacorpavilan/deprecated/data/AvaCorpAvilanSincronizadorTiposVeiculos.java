package br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.data;

import br.com.zalf.prolog.webservice.integracao.avacorpavilan.deprecated.cadastro.TipoVeiculoAvilan;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created on 10/11/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AvaCorpAvilanSincronizadorTiposVeiculos {

    @NotNull
    private final AvaCorpAvilanDao avaCorpAvilanDao;

    public AvaCorpAvilanSincronizadorTiposVeiculos(@NotNull final AvaCorpAvilanDao avaCorpAvilanDao) {
        this.avaCorpAvilanDao = checkNotNull(avaCorpAvilanDao, "avaCorpAvilanDao não pode ser nulo!");
    }

    /**
     * Esse método busca os tipos de veículos da Avilan que temos salvos no banco do ProLog, compara com os
     * tipos de veículos que recebe por parâmetro e caso algum não esteja ainda em nosso banco, é adicionado.
     *
     * Por fim ele retorna uma lista de objetos representando todos os dados de tipos de veículos que temos no banco.
     *
     * @param tiposVeiculosAvilan uma lista de tipos de veículo da Avilan.
     * @return uma lista contendo todos os tipos de veículo que estão no banco do ProLog.
     * @throws Exception caso aconteça algum erro na sincronização.
     */
    public List<TipoVeiculoAvilanProLog> sync(@NotNull final List<TipoVeiculoAvilan> tiposVeiculosAvilan) throws Exception {
        checkNotNull(tiposVeiculosAvilan, "tiposVeiculosAvilan não pode ser nulo!");

        final List<TipoVeiculoAvilanProLog> tiposVeiculosBanco = avaCorpAvilanDao.getTiposVeiculosAvilanProLog();
        for (final TipoVeiculoAvilan tipoVeiculoAvilan : tiposVeiculosAvilan) {
            if (!estaNoBanco(tiposVeiculosBanco, tipoVeiculoAvilan)) {
                final Long codProLog = avaCorpAvilanDao.insertTipoVeiculoAvilan(tipoVeiculoAvilan);
                final TipoVeiculoAvilanProLog tipoVeiculoAvilanProLog = new TipoVeiculoAvilanProLog();
                tipoVeiculoAvilanProLog.setCodigoAvilan(tipoVeiculoAvilan.getCodigo());
                tipoVeiculoAvilanProLog.setDescricao(tipoVeiculoAvilan.getNome());
                tipoVeiculoAvilanProLog.setCodProLog(codProLog);
                tiposVeiculosBanco.add(tipoVeiculoAvilanProLog);
            }
        }
        return tiposVeiculosBanco;
    }

    private boolean estaNoBanco(List<TipoVeiculoAvilanProLog> tiposVeiculosBanco, TipoVeiculoAvilan tipoVeiculoAvilan) {
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < tiposVeiculosBanco.size(); i++) {
            if (tiposVeiculosBanco.get(i).getCodigoAvilan().equals(tipoVeiculoAvilan.getCodigo())) {
                return true;
            }
        }

        return false;
    }

}