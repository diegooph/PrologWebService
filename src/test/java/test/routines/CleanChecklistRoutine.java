package test.routines;

import br.com.zalf.prolog.webservice.database.DatabaseConnection;
import br.com.zalf.prolog.webservice.commons.util.date.DateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 27/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class CleanChecklistRoutine {
    private static final int DIFERENCA_MINIMA_ENTRE_CHECKS_SEGUNDOS = 50;

    public CleanChecklistRoutine(){
    }

    /**
     * Rotina para limpar checklists duplicados do Bando de Dados.
     *
     * @throws Exception se algum erro ocorrer
     */
    public void rotinaParaLimparChecklistsRepetidos() throws Exception {
        generateDuplicates();
    }

    private void generateDuplicates() throws SQLException {
        final DataBase4TestConnection dataBase = new DataBase4TestConnection();
        final List<ChecklistClean> allChecklists = dataBase.getAllChecklists();
        System.out.println("AllChecklists count: "+allChecklists.size() +"\n");
        final List<ChecklistClean> duplicates = new ArrayList<>();

        for (int i = 0; i < allChecklists.size()-1; i++) {
            for (int j = i + 1; j < allChecklists.size(); j++) {
                final ChecklistClean check1 = allChecklists.get(i);
                final ChecklistClean check2 = allChecklists.get(j);
                // compara atributos genericos
                if (check1.equals(check2)) {
                    // verifica se os códigos são diferentes
                    if (!check1.getCodigo().equals(check2.getCodigo())) {
                        final Timestamp data1 = DateUtils.toTimestamp(check1.getData());
                        final Timestamp data2 = DateUtils.toTimestamp(check2.getData());
                        final long seconds = DateUtils.secondsBetween(data1.getTime(), data2.getTime());
                        if (seconds < DIFERENCA_MINIMA_ENTRE_CHECKS_SEGUNDOS) {
                            System.out.println(check1.toString());
                            System.out.println(check2.toString());
                            System.out.println("\n\n");
                            duplicates.add(check2);
                        }
                    }
                }
            }
        }

        for (ChecklistClean duplicate : duplicates) {
            System.out.println(duplicate.toString());
        }
        System.out.println("Duplicates size: "+duplicates.size());
    }

    private class DataBase4TestConnection extends DatabaseConnection {

        List<ChecklistClean> getAllChecklists() throws SQLException {

            final List<ChecklistClean> checklists = new ArrayList<>();

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rSet = null;
            final String query = "SELECT * FROM checklist ORDER BY data_hora";
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
            checklistClean.setData(rSet.getTimestamp("DATA_HORA"));
            checklistClean.setCpfColaborador(rSet.getLong("CPF_COLABORADOR"));
            checklistClean.setPlacaVeiculo(rSet.getString("PLACA_VEICULO"));
            checklistClean.setTipo(rSet.getString("TIPO").charAt(0));
            checklistClean.setTempoRealizacaoCheckInMillis(rSet.getLong("TEMPO_REALIZACAO"));
            checklistClean.setKmAtualVeiculo(rSet.getLong("KM_VEICULO"));
            return checklistClean;
        }
    }
}
