package org.antarcticgardens.cna.config;

import net.fabricmc.loader.api.FabricLoader;
import org.antarcticgardens.cna.CreateNewAge;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Stand-in for the slice of NeoForge's {@code ModConfigSpec} that CNA's configs use, so
 * {@link ServerConfig} and {@link ClientConfig} keep their upstream shape. Values are stored as a
 * small TOML file under {@code config/}, in the same layout NeoForge wrote.
 * <p>
 * Create Fly's Catnip config has no double values, which several of CNA's settings need.
 */
public final class ModConfigSpec {
    private final List<ConfigValue<?>> values;

    private ModConfigSpec(List<ConfigValue<?>> values) {
        this.values = values;
    }

    /** Reads {@code config/<fileName>}, falling back to defaults, then writes it back with comments. */
    public void load(String fileName) {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(fileName);
        Map<String, String> stored = new HashMap<>();
        if (Files.exists(path)) {
            try {
                String section = "";
                for (String raw : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                    String line = raw.strip();
                    if (line.isEmpty() || line.startsWith("#"))
                        continue;
                    if (line.startsWith("[") && line.endsWith("]")) {
                        section = unquote(line.substring(1, line.length() - 1).strip());
                        continue;
                    }
                    int eq = line.indexOf('=');
                    if (eq < 0)
                        continue;
                    String key = unquote(line.substring(0, eq).strip());
                    stored.put(section.isEmpty() ? key : section + "." + key, line.substring(eq + 1).strip());
                }
            } catch (IOException e) {
                CreateNewAge.LOGGER.error("Failed to read config {}, using defaults", path, e);
            }
        }
        for (ConfigValue<?> value : values) {
            String text = stored.get(value.path());
            if (text != null && !value.parse(text))
                CreateNewAge.LOGGER.warn("Invalid value '{}' for {} in {}, using default", text, value.path(), fileName);
        }
        save(path);
    }

    private void save(Path path) {
        StringBuilder out = new StringBuilder();
        String section = "";
        for (ConfigValue<?> value : values) {
            if (!value.section.equals(section)) {
                section = value.section;
                out.append('\n').append('[').append(quoteIfNeeded(section)).append("]\n");
            }
            for (String comment : value.comment)
                out.append("#").append(comment).append('\n');
            if (value.range != null)
                out.append("#").append(value.range).append('\n');
            out.append(quoteIfNeeded(value.name)).append(" = ").append(value.get()).append('\n');
        }
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, out.toString().stripLeading(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            CreateNewAge.LOGGER.error("Failed to write config {}", path, e);
        }
    }

    private static String unquote(String s) {
        return s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"") ? s.substring(1, s.length() - 1) : s;
    }

    private static String quoteIfNeeded(String s) {
        return s.matches("[A-Za-z0-9_.-]+") ? s : "\"" + s + "\"";
    }

    public static final class ConfigValue<T> implements Supplier<T> {
        private final String section;
        private final String name;
        private final String[] comment;
        private final String range;
        private final Function<String, T> parser;
        private final T defaultValue;
        private volatile T value;

        private ConfigValue(String section, String name, String[] comment, String range, T defaultValue, Function<String, T> parser) {
            this.section = section;
            this.name = name;
            this.comment = comment;
            this.range = range;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
            this.parser = parser;
        }

        @Override
        public T get() {
            return value;
        }

        public T getDefault() {
            return defaultValue;
        }

        private String path() {
            return section.isEmpty() ? name : section + "." + name;
        }

        private boolean parse(String text) {
            try {
                T parsed = parser.apply(text);
                if (parsed == null)
                    return false;
                value = parsed;
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
    }

    public static final class Builder {
        private final List<ConfigValue<?>> values = new ArrayList<>();
        private final Deque<String> path = new ArrayDeque<>();
        private String[] pendingComment = new String[0];

        public Builder comment(String... comment) {
            pendingComment = comment;
            return this;
        }

        public Builder push(String name) {
            path.addLast(name);
            return this;
        }

        public Builder pop() {
            path.removeLast();
            return this;
        }

        public ConfigValue<Boolean> define(String name, boolean defaultValue) {
            return add(name, null, defaultValue, text -> switch (text) {
                case "true" -> true;
                case "false" -> false;
                default -> null;
            });
        }

        public ConfigValue<Integer> defineInRange(String name, int defaultValue, int min, int max) {
            return add(name, "Range: " + min + " ~ " + max, defaultValue, text -> {
                int v = Integer.parseInt(text);
                return v < min || v > max ? null : v;
            });
        }

        public ConfigValue<Double> defineInRange(String name, double defaultValue, double min, double max) {
            return add(name, "Range: " + min + " ~ " + max, defaultValue, text -> {
                double v = Double.parseDouble(text);
                return v < min || v > max ? null : v;
            });
        }

        private <T> ConfigValue<T> add(String name, String range, T defaultValue, Function<String, T> parser) {
            ConfigValue<T> value = new ConfigValue<>(String.join(".", path), name, pendingComment, range, defaultValue, parser);
            pendingComment = new String[0];
            values.add(value);
            return value;
        }

        public <T> Pair<T, ModConfigSpec> configure(Function<Builder, T> consumer) {
            T config = consumer.apply(this);
            return Pair.of(config, new ModConfigSpec(List.copyOf(values)));
        }
    }
}
