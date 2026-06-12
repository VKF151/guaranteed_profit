package vance.profit.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import vance.profit.Guaranteed_profit;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record AcceptedCurrencies(List<Holder<Item>> currencyType) {

    public static final Codec<AcceptedCurrencies> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Item.CODEC.listOf().fieldOf("accepted_currencies").forGetter(AcceptedCurrencies::currencyType)
    ).apply(instance, AcceptedCurrencies::new));


    private static final Logger LOGGER = LogUtils.getLogger();
    public static final List<Item> ACCEPTED_CURRENCIES = new ArrayList<>();

    public static void loadCurrencies() {
        try {
            Path jsonPath = FabricLoader.getInstance()
                    .getModContainer(Guaranteed_profit.MOD_ID)
                    .flatMap(container -> container.findPath("data/guaranteed_profit/accepted_currencies/accepted_currencies.json"))
                    .orElseThrow();

            try (BufferedReader reader = Files.newBufferedReader(jsonPath)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);

                AcceptedCurrencies.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                        .resultOrPartial(err -> LOGGER.error("Error parsing json: {}", err))
                        .ifPresent(record -> record.currencyType().stream().map(Holder::value).forEach(ACCEPTED_CURRENCIES::add));
            }
        } catch (Exception e) {
            LOGGER.error("Could not read accepted currency file, Defaulting to diamond.", e);
            ACCEPTED_CURRENCIES.add(Items.DIAMOND);
        }
    }

}
