package vance.profit.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import vance.profit.Guaranteed_profit;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record BlockSideEffects(BlockState blockState, List<Holder<MobEffect>> sideEffects, Integer duration, Integer strength) {
    public static final Codec<BlockSideEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("block_state").forGetter(BlockSideEffects::blockState),
            MobEffect.CODEC.listOf().fieldOf("effects").forGetter(BlockSideEffects::sideEffects),
            Codec.INT.fieldOf("duration").forGetter(BlockSideEffects::duration),
            Codec.INT.fieldOf("strength").forGetter(BlockSideEffects::strength)
    ).apply(instance, BlockSideEffects::new));


    private static final Logger LOGGER = LogUtils.getLogger();
    public static final List<BlockSideEffects> BLOCK_SIDE_EFFECTS = new ArrayList<>();

    public static void loadSideEffects() {
        try {
            Path jsonPath = FabricLoader.getInstance()
                    .getModContainer(Guaranteed_profit.MOD_ID)
                    .flatMap(container -> container.findPath("data/guaranteed_profit/block_side_effects/"))
                    .orElseThrow();

            try (
                    Stream<@NotNull Path> paths = Files.walk(jsonPath)
            ) {
                paths.filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                    try (BufferedReader reader = Files.newBufferedReader(path)) {
                        JsonElement jsonElement = JsonParser.parseReader(reader);

                        BlockSideEffects.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                                .resultOrPartial(err -> LOGGER.error("Error parsing json: {}", err))
                                .ifPresent(BLOCK_SIDE_EFFECTS::add);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        } catch (Exception e) {
            LOGGER.error("Could not read side effect file.", e);
        }
    }

    public List<BlockSideEffects> getBlockSideEffects() {
        return BLOCK_SIDE_EFFECTS;
    }



}
