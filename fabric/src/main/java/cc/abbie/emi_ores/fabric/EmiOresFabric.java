package cc.abbie.emi_ores.fabric;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.networking.FeaturesSender;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EmiOresFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EmiOres.init();

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(FeaturesSender::onSyncDataPackContents);
    }
}
