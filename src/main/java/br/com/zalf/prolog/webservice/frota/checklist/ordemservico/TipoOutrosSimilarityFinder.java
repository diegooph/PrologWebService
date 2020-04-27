package br.com.zalf.prolog.webservice.frota.checklist.ordemservico;

import br.com.zalf.prolog.webservice.frota.checklist.model.insercao.ChecklistAlternativaResposta;
import br.com.zalf.prolog.webservice.frota.checklist.ordemservico.model.InfosAlternativaAberturaOrdemServico;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simmetrics.StringMetric;

import java.util.List;
import java.util.Optional;

import static br.com.zalf.prolog.webservice.commons.util.StringUtils.*;

/**
 * Created on 2019-11-13
 *
 * @author Luiz Felipe (https://github.com/luizfp)
 */
public final class TipoOutrosSimilarityFinder {
    // 1.0 muito similar ou idêntico.
    // 0.0 muito dissimilar ou totalmente diferente.
    private static final double ACCEPTED_SIMILARITY_PERCENTAGE = 0.82;

    @NotNull
    private final StringMetric algorithm;

    public TipoOutrosSimilarityFinder(@NotNull final StringMetric algorithm) {
        this.algorithm = algorithm;
    }

    @NotNull
    public Optional<InfosAlternativaAberturaOrdemServico> findBestMatch(
            @NotNull final ChecklistAlternativaResposta alternativaResposta,
            @NotNull final List<InfosAlternativaAberturaOrdemServico> alternativasAbertura) {
        return internalFindBestMatch(alternativaResposta.getRespostaTipoOutros(), alternativasAbertura);
    }

    @NotNull
    public Optional<InfosAlternativaAberturaOrdemServico> findBestMatch(
            @Nullable final String respostaTipoOutros,
            @NotNull final List<InfosAlternativaAberturaOrdemServico> alternativasAbertura) {
        return internalFindBestMatch(respostaTipoOutros, alternativasAbertura);
    }

    @NotNull
    private Optional<InfosAlternativaAberturaOrdemServico> internalFindBestMatch(
            @Nullable final String respostaTipoOutros,
            @NotNull final List<InfosAlternativaAberturaOrdemServico> alternativasAbertura) {
        double maxSimilarityDetected = 0.0;
        InfosAlternativaAberturaOrdemServico infoMaxSimilarity = null;
        for (final InfosAlternativaAberturaOrdemServico a : alternativasAbertura) {
            if (a.getRespostaTipoOutrosAberturaItem() == null || respostaTipoOutros == null) {
                continue;
            }

            final String source = normalizeString(a.getRespostaTipoOutrosAberturaItem());
            final String target = normalizeString(respostaTipoOutros);

            final double generatedSimilarity = algorithm.compare(source, target);
            if (generatedSimilarity > maxSimilarityDetected) {
                maxSimilarityDetected = generatedSimilarity;
                infoMaxSimilarity = a;
            }
        }

        //noinspection ConstantConditions
        if (maxSimilarityDetected >= ACCEPTED_SIMILARITY_PERCENTAGE && infoMaxSimilarity != null) {
            return Optional.of(infoMaxSimilarity);
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    private String normalizeString(@NotNull final String respostaTipoOutrosAberturaItem) {
        return stripSpecialCharacters(
                stripAccents(
                        removeExtraSpaces(respostaTipoOutrosAberturaItem.trim().toLowerCase())));
    }
}
