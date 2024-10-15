package cc.abbie.emi_ores.networking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

public class NetworkUtils {
    public static <T> T readNbtWithCodec(FriendlyByteBuf buf, Codec<T> codec) {
        Tag tag = buf.readAnySizeNbt().get("");
        DataResult<T> dataResult = codec.parse(NbtOps.INSTANCE, tag);
        return Util.getOrThrow(dataResult, s -> new DecoderException("Failed to decode NBT: " + s));
    }

    public static <T> void writeNbtWithCodec(FriendlyByteBuf buf, Codec<T> codec, T value) {
        CompoundTag compoundTag = new CompoundTag();
        DataResult<Tag> dataResult = codec.encodeStart(NbtOps.INSTANCE, value);
        Tag tag = Util.getOrThrow(dataResult, s -> new EncoderException("Failed to encode: " + s + " " + value));
        compoundTag.put("", tag);
        buf.writeNbt(compoundTag);
    }
}
