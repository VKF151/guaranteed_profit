package vance.profit.datagen;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import vance.profit.codec.AcceptedCurrencies;

import java.util.List;
import java.util.function.BiConsumer;

public class ModCodecRewardProvider extends ModRewardProvider{

    private static final Submitter[] SUBMITTERS = new Submitter[] {
            ModCodecRewardProvider::currencyCodec
    };

    public ModCodecRewardProvider(FabricPackOutput output) {
        super(output, "accepted_currencies", SUBMITTERS);
    }

    private static void currencyCodec(BiConsumer<String, JsonElement> consumer){
        AcceptedCurrencies currencies = new AcceptedCurrencies(
                List.of(
                        BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND),
                        BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_SCRAP)
                )
        );

        final var json = encode(AcceptedCurrencies.CODEC, currencies);
        consumer.accept("accepted_currencies", json);
    }
}
