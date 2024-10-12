package cc.abbie.emi_ores.compat.emi.stack;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.render.EmiTooltipComponents;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class BiomeEmiStack extends EmiStack {
    private final Biome biome;

    private BiomeEmiStack(Biome biome) {
        this.biome = biome;
    }

    public static EmiStack of(Biome biome, CompoundTag tag, long amount) {
        return new BiomeEmiStack(biome);
    }

    public static EmiStack of(Biome biome) {
        return BiomeEmiStack.of(biome, null, 0);
    }

    @Override
    public EmiStack copy() {
        return new BiomeEmiStack(biome);
    }

    @Override
    public void render(PoseStack pose, int x, int y, float delta, int flags) {
        Minecraft client = Minecraft.getInstance();

        if ((flags & RENDER_ICON) != 0) {
            pose.pushPose();
            pose.translate(0, 0, 150);

            TextureAtlasSprite sprite = client.getModelManager()
                    .getAtlas(InventoryMenu.BLOCK_ATLAS)
                    .getSprite(new ResourceLocation(getId().getNamespace(), "emi_ores/biome_icon/" + getId().getPath()));

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

            GuiComponent.blit(pose, x, y, 0, 16, 16, sprite);

            pose.popPose();
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public CompoundTag getNbt() {
        return null;
    }

    @Override
    public Object getKey() {
        return biome;
    }

    @Override
    public ResourceLocation getId() {
        return Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome);
    }

    @Override
    public List<Component> getTooltipText() {
        return null;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> list = new ArrayList<>();
        list.add(ClientTooltipComponent.create(getName().getVisualOrderText()));
        if (Minecraft.getInstance().options.advancedItemTooltips) {
            list.add(ClientTooltipComponent.create(Component.literal(getId().toString()).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText()));
        }
        EmiTooltipComponents.appendModName(list, getId().getNamespace());
        list.addAll(super.getTooltip());
        return list;
    }

    @Override
    public Component getName() {
        return Component.translatable(getId().toLanguageKey("biome"));
    }

    public static class Serializer implements EmiStackSerializer<BiomeEmiStack> {
        @Override
        public String getType() {
            return "biome";
        }

        @Override
        public EmiStack create(ResourceLocation id, CompoundTag nbt, long amount) {
            Registry<Biome> biomeRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
            return BiomeEmiStack.of(biomeRegistry.get(id), nbt, amount);
        }
    }
}
