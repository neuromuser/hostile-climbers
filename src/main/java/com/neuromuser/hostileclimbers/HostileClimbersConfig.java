package com.neuromuser.hostileclimbers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HostileClimbersConfig {

    public boolean enabled = true;
    public String[] mobs = DEFAULT_MOBS;

    public float fallDamageMultiplier = 1.0f;

    private static final String[] DEFAULT_MOBS = {
            "minecraft:zombie",
            "minecraft:zombie_villager",
            "minecraft:husk",
            "minecraft:drowned",
            "minecraft:skeleton",
            "minecraft:stray",
            "minecraft:wither_skeleton",
            "minecraft:vindicator",
            "minecraft:pillager",
            "minecraft:evoker",
            "minecraft:witch",
            "minecraft:piglin",
            "minecraft:piglin_brute",
            "minecraft:zombified_piglin",
            "minecraft:silverfish",
            "minecraft:endermite",
            "minecraft:blaze",
            "minecraft:bogged",
            "minecraft:breeze"
    };

    private transient Set<String> mobSet;

    public void bake() {
        mobSet = new HashSet<>(Arrays.asList(mobs));
    }

    public boolean isClimbingAllowed(MobEntity mob) {
        if (!enabled) return false;
        String id = Registries.ENTITY_TYPE.getId(mob.getType()).toString();
        return mobSet.contains(id);
    }

    // -----------------------------------------------------------------------

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_NAME = "hostileclimbers.json";

    public static HostileClimbersConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME);

        if (Files.exists(path)) {
            try (Reader r = Files.newBufferedReader(path)) {
                JsonReader jr = new JsonReader(r);
                jr.setLenient(true);
                HostileClimbersConfig cfg = GSON.fromJson(jr, HostileClimbersConfig.class);
                if (cfg == null) cfg = new HostileClimbersConfig();
                cfg.bake();
                HostileClimbers.LOGGER.info("[HostileClimbers] Config loaded.");
                return cfg;
            } catch (IOException e) {
                HostileClimbers.LOGGER.error("[HostileClimbers] Failed to read config, using defaults.", e);
            }
        }

        HostileClimbersConfig defaults = new HostileClimbersConfig();
        defaults.bake();
        defaults.save(path);
        HostileClimbers.LOGGER.info("[HostileClimbers] Default config written to {}", path);
        return defaults;
    }

    private void save(Path path) {
        try (Writer w = Files.newBufferedWriter(path)) {
            w.write(buildConfigText());
        } catch (IOException e) {
            HostileClimbers.LOGGER.error("[HostileClimbers] Failed to write default config.", e);
        }
    }

    private String buildConfigText() {
        StringBuilder sb = new StringBuilder();

        sb.append("  \"enabled\": ").append(enabled).append(",\n");

        sb.append("  // Fall damage multiplier for mobs. 0.5 = half, 1.0 = normal, 0.0 = none.\n");
        sb.append("  \"fallDamageMultiplier\": ").append(fallDamageMultiplier).append(",\n\n");
        sb.append("\n");
        sb.append("  // Mobs listed here are allowed to climb walls.\n");
        sb.append("  // Add modded mobs using their registry ID, e.g. \"mymod:custom_zombie\".\n");

        sb.append("  \"mobs\": [\n");
        for (int i = 0; i < mobs.length; i++) {
            sb.append("    \"").append(mobs[i]).append("\"");
            if (i < mobs.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
}