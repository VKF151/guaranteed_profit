package vance.profit.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import vance.profit.Guaranteed_profit;

public class SavedGamblingData extends SavedData {
    private int masksWon = 0;
    private int housesHoard = 0;

    public SavedGamblingData() {}

    public SavedGamblingData(int masksWon, int housesHoard) {
        this.masksWon = masksWon;
        this.housesHoard = housesHoard;
    }

    public int getMasksWon() {
        return masksWon;
    }
    public int getHousesHoard() {return housesHoard;}

    public void incrementMasksWon() {
        this.masksWon++;
        setDirty();
    }
    public void incrementHousesHoard(int amount) {
        this.housesHoard += amount;
        setDirty();
    }

    private static final Codec<SavedGamblingData> CODEC = RecordCodecBuilder.create(i -> i.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("masks_won").forGetter(SavedGamblingData::getMasksWon),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("housesHoard").forGetter(SavedGamblingData::getHousesHoard)
    ).apply(i, SavedGamblingData::new));

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
