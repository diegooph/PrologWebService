package test.br.com.zalf.prolog.webservice.routines;

/**
 * Created on 27/10/17.
 *
 * @author Diogenes Vanzela (https://github.com/diogenesvanzella)
 */
public class RunRoutines {

    public static void main(String[] args) {
        CleanChecklistRoutine routine = new CleanChecklistRoutine();

        try {
            routine.rotinaParaLimparChecklistsRepetidos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
