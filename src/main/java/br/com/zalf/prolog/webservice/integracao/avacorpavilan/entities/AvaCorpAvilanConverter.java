package br.com.zalf.prolog.webservice.integracao.avacorpavilan.entities;

import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Pneu;
import br.com.zalf.prolog.webservice.frota.pneu.pneu.model.Sulcos;
import br.com.zalf.prolog.webservice.frota.veiculo.model.Veiculo;
import com.sun.istack.internal.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by luiz on 18/07/17.
 */
public final class AvaCorpAvilanConverter {

    private AvaCorpAvilanConverter() {
        throw new IllegalStateException(AvaCorpAvilanConverter.class.getSimpleName() + " cannot be instantiated!");
    }

    public static Veiculo convert(@NotNull final VeiculoAvaCorpAvilan veiculoAvaCorpAvilan) {
        checkNotNull(veiculoAvaCorpAvilan, "veiculoAvaCorpAvilan não pode ser nulo!");

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(veiculoAvaCorpAvilan.getPlaca());
        veiculo.setKmAtual(Long.parseLong(veiculoAvaCorpAvilan.getMarcador()));
        return veiculo;
    }

    public static VeiculoAvaCorpAvilan convert(@NotNull final Veiculo veiculo) {
        checkNotNull(veiculo, "veiculo não pode ser nulo!");

        return new VeiculoAvaCorpAvilan.Builder()
                .withPlaca(veiculo.getPlaca())
                .withMarcador(String.valueOf(veiculo.getKmAtual()))
                .build();
    }

    public static Pneu convert(@NotNull final PneuAvaCorpAvilan pneuAvaCorpAvilan) {
        checkNotNull(pneuAvaCorpAvilan, "pneuAvaCorpAvilan não pode ser nulo!");

        Pneu pneu = new Pneu();
        pneu.setCodigo(Integer.parseInt(pneuAvaCorpAvilan.getNumeroFogo()));
        pneu.setPosicao(Integer.parseInt(pneuAvaCorpAvilan.getPosicao()));
        Sulcos sulcos = new Sulcos();
        sulcos.setExterno(pneuAvaCorpAvilan.getSulco1());
        sulcos.setCentralExterno(pneuAvaCorpAvilan.getSulco2());
        sulcos.setCentralInterno(pneuAvaCorpAvilan.getSulco3());
        sulcos.setInterno(pneuAvaCorpAvilan.getSulco4());
        pneu.setSulcosAtuais(sulcos);
        return pneu;
    }

    public static PneuAvaCorpAvilan convert(@NotNull final Pneu pneu) {
        checkNotNull(pneu, "pneu não pode ser nulo!");

        return new PneuAvaCorpAvilan.Builder()
                .withNumeroFogo(String.valueOf(pneu.getCodigo()))
                .withPosicao(String.valueOf(pneu.getPosicao()))
                .withSulco1(pneu.getSulcosAtuais().getExterno())
                .withSulco2(pneu.getSulcosAtuais().getCentralExterno())
                .withSulco3(pneu.getSulcosAtuais().getCentralInterno())
                .withSulco4(pneu.getSulcosAtuais().getInterno())
                .build();
    }
}