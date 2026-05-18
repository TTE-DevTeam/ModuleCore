package de.dertoaster.modulecore;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public interface IConfigurationHelper {

    public ConfigurationSection getMainSection();

    @NotNull
    public default ConfigurationSection getSection(final String path) {
        if (path == null || path.isEmpty()) {
            return this.getMainSection();
        }
        return getSection(path, this.getMainSection());
    }

    @NotNull
    default ConfigurationSection getSection(final String path, final ConfigurationSection parentSection) {
        final int firstDelimiterIndex = path.indexOf('/');
        final String firstSection = firstDelimiterIndex >= 0 ? path.substring(0, path.indexOf('/')) : path;

        ConfigurationSection newParent = parentSection.getConfigurationSection(firstSection);
        if (newParent == null) {
            newParent = parentSection.createSection(firstSection);
        }
        if (firstDelimiterIndex >= 0) {
            final String remainingPath = path.substring(firstDelimiterIndex + 1);
            return getSection(remainingPath, newParent);
        } else {
            return newParent;
        }
    }

}
