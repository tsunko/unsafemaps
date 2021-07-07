package hekiyou.academy.unsafemaps.mixin;

import hekiyou.academy.unsafemaps.PreComputedColors;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.item.map.MapState;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.nio.IntBuffer;

@Mixin(MapRenderer.MapTexture.class)
public class UpdateTextureMixin {

    @Shadow private MapState state;
    @Shadow private NativeImageBackedTexture texture;

    /**
     * @reason Minecraft's current updateTexture() method is slooooow. It iterates over the array in 2D fashion and also
     * computes different shades of a base color on the fly without caching the result or anything, so we effectively
     * hit getRenderColor 16k times every frame update. The solution here is to just pre-calculate getRenderColor() at
     * startup, and then just use a lookup table to find out where we are.
     * @author tsunko
     */
    @Overwrite
    public void updateTexture(){
        NativeImage internal = texture.getImage();
        if(internal == null) return;

        // traditionally we would just do pointer arithmetic, but this is a little easier to read and avoids having
        // to do mul, even if it gets optimized out as a bit-shift
        IntBuffer buffer = MemoryUtil.memIntBuffer(internal.pointer, 128 * 128);
        for (int i = 0; i < buffer.capacity(); i++)
            buffer.put(PreComputedColors.LOOKUP[this.state.colors[i] & 255]);
        texture.upload();
    }

}
