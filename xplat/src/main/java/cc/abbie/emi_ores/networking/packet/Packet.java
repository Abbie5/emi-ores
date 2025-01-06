package cc.abbie.emi_ores.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface Packet<T> {
    ResourceLocation getId();

    void write(FriendlyByteBuf buf);

    void handle();

    T create(FriendlyByteBuf buf);
}
