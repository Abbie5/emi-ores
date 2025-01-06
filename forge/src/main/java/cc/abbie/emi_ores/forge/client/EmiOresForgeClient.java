package cc.abbie.emi_ores.forge.client;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EmiOres.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EmiOresForgeClient {
    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        EmiOresClient.init();

        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> FeaturesReciever.clearFeatures());
    }
}
