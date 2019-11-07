package br.com.zalf.prolog.webservice.frota.pneu._model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 31/05/2018
 *
 * Esse enum serve para listarmos os diferentes tipos de pneus que existem. É importante notar que NÃO devemos renomear
 * as constantes do enum, pois elas são automaticamente serializadas pelo {@link Gson} quando esse objeto é utilizado.
 * Caso seja EXTREMAMENTE necessário alterar o nome das constantes, será necessário implementar um
 * serializer/deserializer do {@link Gson} para que o valor da String 'tipo' possa ser utilizando no JSON. Isso não foi
 * feito desde o princípio para pouparmos processamento.
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public enum PneuTipo implements PneuFactory {
    PNEU_COMUM("PNEU_COMUM", PneuComum.class) {
        @NotNull
        @Override
        public Pneu createNew() {
            return new PneuComum();
        }
    },
    PNEU_ANALISE("PNEU_ANALISE", PneuAnalise.class) {
        @NotNull
        @Override
        public Pneu createNew() {
            return new PneuAnalise();
        }
    },
    PNEU_DESCARTE("PNEU_DESCARTE", PneuDescarte.class) {
        @NotNull
        @Override
        public Pneu createNew() {
            return new PneuDescarte();
        }
    },
    PNEU_ESTOQUE("PNEU_ESTOQUE", PneuEstoque.class) {
        @NotNull
        @Override
        public Pneu createNew() {
            return new PneuEstoque();
        }
    },
    PNEU_EM_USO("PNEU_EM_USO", PneuEmUso.class) {
        @NotNull
        @Override
        public Pneu createNew() {
            return new PneuEmUso();
        }
    };

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

    @NotNull
    public static PneuTipo fromString(@Nullable final String text) {
        if (text != null) {
            for (final PneuTipo tipoPneu : PneuTipo.values()) {
                if (text.equalsIgnoreCase(tipoPneu.tipo)) {
                    return tipoPneu;
                }
            }
        }
        throw new IllegalArgumentException("Nenhum tipo encontrado para a String: " + text);
    }
}