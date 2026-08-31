package vance.profit.world;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import vance.profit.Guaranteed_profit;

public class SavedGamblingData extends SavedData {
    private int masksWon = 0;

    public SavedGamblingData() {}

    public SavedGamblingData(int count) {
        this.masksWon = count;
    }

    public int getMasksWon() {
        return masksWon;
    }

    public void incrementMasksWon() {
        this.masksWon++;
        setDirty();
    }

    private static final Codec<SavedGamblingData> CODEC = Codec.INT.xmap(
            SavedGamblingData::new,
            SavedGamblingData::getMasksWon
    );

    private static final SavedDataType<SavedGamblingData> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "saved_gambling_data"),
            SavedGamblingData::new,
            CODEC,
            null
    );

    public static SavedGamblingData getSavedGamblingData(MinecraftServer server) {
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);

        if (level == null) {
            return new SavedGamblingData();
        }

        return level.getDataStorage().computeIfAbsent(TYPE);
    }
}
