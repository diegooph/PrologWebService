package test.routines;

import br.com.zalf.prolog.webservice.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.DateUtils;
import com.google.common.collect.ImmutableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 27/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class CleanChecklistRoutine {
    private static final int DIFERENCA_MINIMA_ENTRE_CHECKS_SEGUNDOS = 30;

    /**
     * A Key do Map deve ser a cobinação dos atributos:
     * <p>
     * codUnidade
     * codModelo
     * cpfColaborador
     * placaVeiculo
     * tipo
     * kmAtualVeiculo
     */
    private Map<String, List<ChecklistClean>> groupCheLists;

    public CleanChecklistRoutine() {
        this.groupCheLists = new HashMap<>();
    }

    /**
     * Rotina para limpar checklists duplicados do Bando de Dados.
     *
     * @throws Exception se algum erro ocorrer
     */
    public void rotinaParaLimparChecklistsRepetidos() throws Exception {

        populateChecklistGroup();
    }

    private void populateChecklistGroup() throws SQLException {
        final DataBase4TestConnection dataBase = new DataBase4TestConnection();
        final List<ChecklistClean> allChecklists = dataBase.getAllChecklists();

        Map<String, List<ChecklistClean>> collect = allChecklists
                .stream()
                .collect(Collectors.groupingBy(ChecklistClean::generateKey));

        // Printa elementos.
        collect.forEach((s, checklistCleans) -> System.out.println(s));

        // Ordena os checklists por data.
        collect.forEach((s, checklistCleans) -> checklistCleans.sort(Comparator.comparing(ChecklistClean::getData)));

        final Map<String, List<ChecklistClean>> finalMap = new HashMap<>();
        collect.forEach((s, checklistCleans) -> {
            for (ChecklistClean c1 : checklistCleans) {
                for (int i = checklistCleans.indexOf(c1); i < checklistCleans.size(); i++) {
                    ChecklistClean c2 = checklistCleans.get(i);
                    // Garante que não comparamos um check com ele mesmo. Evitando 0 como resultado nos segundos.
                    if (!c1.getCodigo().equals(c2.getCodigo())) {
                        final long seconds = Math.abs(Duration.between(c1.getData(), c2.getData()).getSeconds());
                        if (seconds < DIFERENCA_MINIMA_ENTRE_CHECKS_SEGUNDOS) {
                            finalMap.put(c1.getCodigo() + "<->" + c2.getCodigo(), ImmutableList.of(c1, c2));
                        }
                    }
                }
            }
        });

        System.out.println("\n=========================================\n");

        // Printa elementos.
        finalMap.forEach((s, checklistCleans) -> System.out.println(s + " -> " + checklistCleans.size()));
    }

    private class DataBase4TestConnection extends DatabaseConnection {

        public List<ChecklistClean> getAllChecklists() throws SQLException {

            final List<ChecklistClean> checklists = new ArrayList<>();

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            final String query = "SELECT * FROM checklist";
            try {
                conn = getConnection();
                stmt = conn.prepareStatement(query);
                rSet = stmt.executeQuery();
                while (rSet.next()) {
                    ChecklistClean checklistClean = createChecklistClean(rSet);
                    checklists.add(checklistClean);
                }
            } finally {
                closeConnection(conn, stmt, rSet);
            }
            return checklists;
        }

        private ChecklistClean createChecklistClean(ResultSet rSet) throws SQLException {
            ChecklistClean checklistClean = new ChecklistClean();
            checklistClean.setCodUnidade(rSet.getLong("COD_UNIDADE"));
            checklistClean.setCodModelo(rSet.getLong("COD_CHECKLIST_MODELO"));
            checklistClean.setCodigo(rSet.getLong("CODIGO"));
            checklistClean.setData(DateUtils.toLocalDateTime(rSet.getDate("DATA_HORA")));
            checklistClean.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
            checklistClean.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
            checklistClean.setTipo(rSet.getString("PLACA_VEICULO").charAt(0));
            checklistClean.setTempoRealizacaoCheckInMillis(rSet.getLong("TEMPO_REALIZACAO"));
            checklistClean.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
            return checklistClean;
        }
    }
}
