package at.fhv.roomix.domain.session.configuration;

import org.aeonbits.owner.ConfigCache;

/**
 * Roomix
 * at.fhv.roomix.domain.session.configuration
 * ConfigurationLoader
 * 22/04/2018 Oliver
 * <p>
 * Enter Description here
 */
public class ConfigurationLoader {
    private ConfigurationLoader() {
    }

    public static Configuration getConfiguration() {
        return ConfigCache.getOrCreate(Configuration.class);
    }

    public static IUiConfiguration getUiConfiguration() {
        IUiConfiguration create = ConfigCache.getOrCreate(IUiConfiguration.class);
        return create;
    }
}
