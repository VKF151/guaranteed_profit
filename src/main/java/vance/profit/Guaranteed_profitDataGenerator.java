package vance.profit;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import vance.profit.datagen.ModBlockTagProvider;
import vance.profit.datagen.ModCodecRewardProvider;
import vance.profit.datagen.ModItemTagProvider;

public class Guaranteed_profitDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(ModBlockTagProvider::new);
		pack.addProvider(ModItemTagProvider::new);
		pack.addProvider(ModCodecRewardProvider::new);

	}
}
