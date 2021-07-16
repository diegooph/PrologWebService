package br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama;

import br.com.zalf.prolog.webservice.v3.fleet.vehicle.diagrama._model.DiagramaEntity;
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
        return diagramaDao.getOne(codDiagramaVeiculo);
    }
}
