package org.antarcticgardens.cna.config;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class CNAConfig {
    private static final CNAConfig INSTANCE = new CNAConfig();

    private final ClientConfig client;
    private final ServerConfig server;

    private CNAConfig() {
        var client = new ModConfigSpec.Builder().configure(ClientConfig::new);
        this.client = client.getLeft();
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            client.getRight().load("create_new_age-client.toml");

        // NeoForge kept this per world and synced it to clients. Here it is one global file, read on
        // both sides; a server's values only reach clients where they drive the server's own logic.
        var server = new ModConfigSpec.Builder().configure(ServerConfig::new);
        this.server = server.getLeft();
        server.getRight().load("create_new_age-server.toml");
    }

    public static ClientConfig getClient() {
        return INSTANCE.client;
    }

    public static ServerConfig getServer() {
        return INSTANCE.server;
    }

    public static void load() {  }
}
