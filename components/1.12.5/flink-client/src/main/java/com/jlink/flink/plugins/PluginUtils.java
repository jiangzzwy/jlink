package com.jlink.flink.plugins;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.plugin.*;
import org.apache.flink.util.FlinkRuntimeException;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class PluginUtils {
    private PluginUtils() {
        throw new AssertionError("Singleton class.");
    }

    public static PluginManager createPluginManagerFromRootFolder(Configuration configuration) {
        return createPluginManagerFromRootFolder(PluginConfig.fromConfiguration(configuration));
    }

    private static PluginManager createPluginManagerFromRootFolder(PluginConfig pluginConfig) {
        if (pluginConfig.getPluginsPath().isPresent()) {
            try {
                Collection<PluginDescriptor> pluginDescriptors =
                        new DirectoryBasedPluginFinder(pluginConfig.getPluginsPath().get())
                                .findPlugins();
                return new DefaultPluginManager(
                        pluginDescriptors, pluginConfig.getAlwaysParentFirstPatterns());
            } catch (IOException e) {
                throw new FlinkRuntimeException(
                        "Exception when trying to initialize plugin system.", e);
            }
        } else {
            return new DefaultPluginManager(
                    Collections.emptyList(), pluginConfig.getAlwaysParentFirstPatterns());
        }
    }
}
