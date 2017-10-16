package br.com.zalf.prolog.webservice.integracao.avacorpavilan.pneumapper;

import br.com.zalf.prolog.webservice.integracao.PosicaoPneuMapper;

import javax.annotation.Nonnull;

/**
 * Created on 16/10/17
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class AvaCorpAvilanPosicaoPneuMapperFactory {

    private AvaCorpAvilanPosicaoPneuMapperFactory() {
        throw new IllegalStateException(AvaCorpAvilanPosicaoPneuMapperFactory.class.getSimpleName()
                + " cannot be instantiated!");
    }

    @Nonnull
    public PosicaoPneuMapper createMapperByCodigoDiagramaProLog(@Nonnull final String tipoVeiculo) {
//        switch (tipoVeiculo) {
//                return 1L;
//            case "CVTR":
//            case "CTR":
//                return 2L;
//            case "C3RRB":
//            case "C3S":
//            case "C3":
//                return 3L;
//            case "CTR2E":
//            case "CTR2D":
//                return 4L;
//            case "C2RRR":
//            case "C2RRB":
//            case "C2RCB":
//            case "C2RCR":
//                return 5L;
//                default:
//                    throw new IllegalArgumentException();
        return null;
    }
}