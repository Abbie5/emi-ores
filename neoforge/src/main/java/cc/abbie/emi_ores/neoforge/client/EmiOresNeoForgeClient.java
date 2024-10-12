package cc.abbie.emi_ores.neoforge.client;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

import static net.neoforged.fml.common.EventBusSubscriber.*;

@EventBusSubscriber(modid = EmiOres.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class EmiOresNeoForgeClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        EmiOresClient.init();

        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> FeaturesReciever.clearFeatures());
    }
}
