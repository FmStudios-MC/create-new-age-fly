package org.antarcticgardens.cna.config;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.antarcticgardens.cna.CreateNewAge;

public class CNAConfig {
    private static final CNAConfig INSTANCE = new CNAConfig();

    private final ClientConfig client;
    private final ServerConfig server;

    private CNAConfig() {
        var client = new ModConfigSpec.Builder().configure(ClientConfig::new);
        this.client = client.getLeft();
        CreateNewAge.getInstance().getPlatform().getRegistrar().registerConfig(ModConfig.Type.CLIENT, client.getRight());

        var common = new ModConfigSpec.Builder().configure(ServerConfig::new);
        this.server = common.getLeft();
        CreateNewAge.getInstance().getPlatform().getRegistrar().registerConfig(ModConfig.Type.SERVER, common.getRight());
    }

    public static ClientConfig getClient() {
        return INSTANCE.client;
    }

    public static ServerConfig getServer() {
        return INSTANCE.server;
    }
    
    public static void load() {  }
}
