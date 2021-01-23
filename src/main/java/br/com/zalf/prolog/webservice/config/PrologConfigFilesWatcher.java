package br.com.zalf.prolog.webservice.config;

import br.com.zalf.prolog.webservice.commons.util.Log;
import br.com.zalf.prolog.webservice.commons.util.PrologUtils;
import br.com.zalf.prolog.webservice.entrega.mapa.validator.RegrasPlanilhaMapaLoader;
import br.com.zalf.prolog.webservice.integracao.protheusnepomuceno.utils.FamiliaModeloBloqueadoLoader;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.filewatch.FileChangeListener;
import org.springframework.boot.devtools.filewatch.FileSystemWatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Set;

@Configuration
public class PrologConfigFilesWatcher implements FileChangeListener {
    @NotNull
    private static final String TAG = PrologConfigFilesWatcher.class.getSimpleName();
    @NotNull
    private static final String WATCH_DIRECTORY = "configs/";
    @NotNull
    private static final Duration TIME_BETWEEN_CHECKS =
            PrologUtils.isDebug() ? Duration.ofMillis(5000L) : Duration.ofMinutes(5);
    @NotNull
    private static final Duration TIME_TO_APPLY_CHANGES =
            PrologUtils.isDebug() ? Duration.ofMillis(3000L) : Duration.ofMinutes(1);
    @NotNull
    private static final ImmutableMap<String, FileWatchListener> LISTENERS = setupListeners();

    public interface FileWatchListener {
        @NotNull
        String getFileNameToWatchChanges();

        void onWatchedFileChanged();
    }

    @NotNull
    private static ImmutableMap<String, FileWatchListener> setupListeners() {
        final FamiliaModeloBloqueadoLoader familiaModeloBloqueadoLoader = FamiliaModeloBloqueadoLoader.of();
        final RegrasPlanilhaMapaLoader regrasPlanilhaMapaLoader = RegrasPlanilhaMapaLoader.of();
        return ImmutableMap
                .<String, FileWatchListener>builder()
                .put(familiaModeloBloqueadoLoader.getFileNameToWatchChanges(), familiaModeloBloqueadoLoader)
                .put(regrasPlanilhaMapaLoader.getFileNameToWatchChanges(), regrasPlanilhaMapaLoader)
                .build();
    }

    @Override
    public void onChange(final Set<ChangedFiles> changeSet) {
        Log.d(TAG, "File changed");
        changeSet.forEach(
                changedFiles -> changedFiles.getFiles().stream()
                        .map(file -> file.getFile().getName())
                        .filter(LISTENERS::containsKey)
                        .forEach(fileName -> LISTENERS.get(fileName).onWatchedFileChanged()));
    }

    @Bean
    public FileSystemWatcher fileSystemWatcher() {
        final URL resource = getClass().getClassLoader().getResource(WATCH_DIRECTORY);
        if (resource == null) {
            return null;
        }
        final FileSystemWatcher watcher =
                new FileSystemWatcher(true, TIME_BETWEEN_CHECKS, TIME_TO_APPLY_CHANGES);
        watcher.addSourceDirectory(new File(resource.getFile()));
        watcher.addListener(this);
        watcher.start();
        Log.d(TAG, "starting watcher");
        return watcher;
    }

    @PreDestroy
    public void onDestroy() {
        Log.d(TAG, "stopping watcher");
        fileSystemWatcher().stop();
    }
}