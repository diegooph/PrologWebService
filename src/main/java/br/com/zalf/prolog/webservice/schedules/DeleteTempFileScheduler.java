package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.schedules.time.EveryTwoDaysAtTwoHours;
import org.springframework.context.annotation.Profile;
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
@Profile("prod")
@Component
public class DeleteTempFileScheduler implements Scheduler {

    private static final String TAG = DeleteTempFileScheduler.class.getSimpleName();

    @Override
    @EveryTwoDaysAtTwoHours
    public void doWork() {
        final List<File> allTempDirs = FileUtils.getAllCreatedTempDirs();
        allTempDirs.stream()
                .peek(dir -> {
                    final String message = String.format("Iniciando execução do schedule para limpeza da pasta %s",
                                                         dir.getName());
                    Log.i(TAG, message);
                })
                .forEach(this::deleteFiles);
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
