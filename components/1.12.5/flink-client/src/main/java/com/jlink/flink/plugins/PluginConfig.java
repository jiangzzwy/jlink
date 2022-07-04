package com.jlink.flink.plugins;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;

import org.apache.flink.configuration.DeploymentOptionsInternal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

/** Stores the configuration for plugins mechanism. */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class PluginConfig {
    private static final Logger LOG = LoggerFactory.getLogger(PluginConfig.class);

    private final Optional<Path> pluginsPath;

    private final String[] alwaysParentFirstPatterns;

    private PluginConfig(Optional<Path> pluginsPath, String[] alwaysParentFirstPatterns) {
        this.pluginsPath = pluginsPath;
        this.alwaysParentFirstPatterns = alwaysParentFirstPatterns;
    }

    public Optional<Path> getPluginsPath() {
        return pluginsPath;
    }

    public String[] getAlwaysParentFirstPatterns() {
        return alwaysParentFirstPatterns;
    }

    public static PluginConfig fromConfiguration(Configuration configuration) {
        return new PluginConfig(
                getPluginsDir(configuration).map(File::toPath),
                CoreOptions.getPluginParentFirstLoaderPatterns(configuration));
    }

    public static Optional<File> getPluginsDir(Configuration configuration) {
        String pluginsDir = configuration.getString(DeploymentOptionsInternal.CONF_DIR)+"/../plugins";
        File pluginsDirFile = new File(pluginsDir);
        if (!pluginsDirFile.isDirectory()) {
            LOG.warn("The plugins directory [{}] does not exist.", pluginsDirFile);
            return Optional.empty();
        }
        return Optional.of(pluginsDirFile);
    }
}
