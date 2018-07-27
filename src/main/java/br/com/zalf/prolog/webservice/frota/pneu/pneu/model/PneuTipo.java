package br.com.zalf.prolog.webservice.frota.pneu.pneu.model;

import br.com.zalf.prolog.webservice.frota.pneu.afericao.model.PneuAfericaoAvulsa;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

/**
 * Created on 31/05/2018
 *
 * Esse enum existe para listarmos os diferentes tipos de pneus que existem. É importante notar que NÃO devemos renomear
 * as constantes do enum, pois elas são automaticamente serializadas pelo {@link Gson} quando esse objeto é utilizado.
 * Caso seja EXTREMAMENTE necessário alterar o nome das constantes, será necessário implementar um
 * serializer/deserializer do {@link Gson} para que o valor da String tipo possa ser utilizando no JSON. Isso não foi
 * feito desde o princípio para pouparmos processamento.
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum PneuTipo {
    PNEU_COMUM("PNEU_COMUM", PneuComum.class),
    PNEU_ANALISE("PNEU_ANALISE", PneuAnalise.class),
    PNEU_DESCARTE("PNEU_DESCARTE", PneuDescarte.class),
    PNEU_ESTOQUE("PNEU_ESTOQUE", PneuEstoque.class),
    PNEU_EM_USO("PNEU_EM_USO", PneuEmUso.class),
    PNEU_AFERICAO_AVULSA("PNEU_AFERICAO_AVULSA", PneuAfericaoAvulsa.class);

    @NotNull
    final String tipo;
    @NotNull
    final Class<? extends Pneu> clazz;

    PneuTipo(@NotNull final String tipo, @NotNull final Class<? extends Pneu> clazz) {
        this.tipo = tipo;
        this.clazz = clazz;
    }

    @NotNull
    public Class<? extends Pneu> getClazz() {
        return clazz;
    }

    @NotNull
    public String asString() {
        return tipo;
    }

    @Override
    public String toString() {
        return tipo;
    }
}