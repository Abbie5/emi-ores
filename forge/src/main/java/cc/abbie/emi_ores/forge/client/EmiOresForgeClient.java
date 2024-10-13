package cc.abbie.emi_ores.forge.client;

import cc.abbie.emi_ores.EmiOres;
import cc.abbie.emi_ores.client.BiomeSpriteRegistry;
import cc.abbie.emi_ores.client.EmiOresClient;
import cc.abbie.emi_ores.client.FeaturesReciever;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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

    @SubscribeEvent
    public static void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (event.getAtlas().location() != InventoryMenu.BLOCK_ATLAS) return;
        BiomeSpriteRegistry.registerSprites(event::addSprite);
    }

    @SubscribeEvent
    public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new BiomeSpriteRegistry.ReloadListener());
    }
}
