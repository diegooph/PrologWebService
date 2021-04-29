package br.com.zalf.prolog.webservice.schedules;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.files.FileUtils;
import br.com.zalf.prolog.webservice.schedules.time.EveryTwoDaysAtTwoHours;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final File baseTempDir = FileUtils.getTempDir();
        deleteFiles(baseTempDir);
    }

    private void deleteFiles(@NotNull final File dir) {
        Log.i(TAG, "Diretório analisado: " + dir.getAbsolutePath());
        final Path absolutePath = Paths.get(dir.toURI());
        try (final Stream<Path> walk = Files.walk(absolutePath)) {
            final boolean allFillesDeleted = walk
                    .map(Path::toFile)
                    .filter(FileUtils::isOutdated)
                    .peek(file -> Log.i(TAG, "Arquivo ou diretório à ser deletado: " + file.getName()))
                    .allMatch(File::delete);
            if (allFillesDeleted) {
                Log.i(TAG, "Arquivos e diretórios temporários deletados com sucesso!");
                return;
            }
            throw new IllegalStateException("Não foi possivel deletar todos os arquivos e diretórios!");
        } catch (final IOException exception) {
            Log.e(TAG, "Erro ao realizar deleção dos arquivos", exception);
        }
    }

}
