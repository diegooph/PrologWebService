package br.com.zalf.prolog.webservice.frota.v3.veiculo.diagrama;

import br.com.zalf.prolog.webservice.Injection;
import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.frota.v3.veiculo.diagrama._model.DiagramaEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagramaService {
    @NotNull
    private static final String TAG = DiagramaService.class.getSimpleName();
    @NotNull
    private final DiagramaDao diagramaDao;

    @Autowired
    public DiagramaService(@NotNull final DiagramaDao diagramaDao) {
        this.diagramaDao = diagramaDao;
    }

    @NotNull
    public DiagramaEntity getByCod(@NotNull final Short codDiagramaVeiculo) {
        try {
            return diagramaDao.getOne(codDiagramaVeiculo);
        } catch (final Throwable t) {
            Log.e(TAG, String.format("Erro ao buscar diagrama do veículo %d", codDiagramaVeiculo), t);
            throw Injection
                    .provideProLogExceptionHandler()
                    .map(t, "Erro ao buscar diagrama do veículo, tente novamente.");
        }
    }
}
