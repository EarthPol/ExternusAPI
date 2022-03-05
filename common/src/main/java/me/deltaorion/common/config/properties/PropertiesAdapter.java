package me.deltaorion.common.config.properties;

import me.deltaorion.common.config.ConfigSection;
import me.deltaorion.common.config.adapter.AdapterFactory;
import me.deltaorion.common.config.adapter.ConfigAdapter;
import org.jetbrains.annotations.NotNull;

public class PropertiesAdapter implements AdapterFactory {

    @Override
    public ConfigAdapter getNew(@NotNull ConfigSection adapterFor) {
        return new PropertiesConfigAdapter(adapterFor);
    }
}
