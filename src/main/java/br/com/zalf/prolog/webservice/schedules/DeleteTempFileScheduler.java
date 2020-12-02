package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.commons.util.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 2020-11-30
 *
 * @author Guilherme Steinert (https://github.com/steinert999)
 */
@Component
public class DeleteTempFileScheduler implements Scheduler {

    private static final String TAG = DeleteTempFileScheduler.class.getSimpleName();

    /*
     * Essa cron foi programada para ser executada á cada dois dias às 02:00,
     * todos os meses em todos os dias da semana
     */
    @Override
    @Scheduled(cron = "0 2 */2 * * *")
    public void doWork() {
        final File tmpDir = br.com.zalf.prolog.webservice.commons.util.Files.createTempDir();
        deleteFiles(tmpDir);
    }

    private void deleteFiles(final File dir) {
        Log.i(TAG, "Diretório analisado: " + dir.getAbsolutePath());
        final Path absolutePath = Paths.get(dir.toURI());
        try (final Stream<Path> walk = Files.walk(absolutePath)) {
            final List<File> files = walk
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            files.stream()
                 .peek(file -> Log.i(TAG, "Arquivo à ser deletado: " + file.getName()))
                 .forEach(File::delete);
        } catch (final IOException exception) {
            Log.e(TAG, "Erro ao realizar deleção dos arquivos", exception);
        }
    }

}
