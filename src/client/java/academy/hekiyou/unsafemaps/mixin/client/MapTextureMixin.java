package academy.hekiyou.unsafemaps.mixin.client;

import academy.hekiyou.unsafemaps.PixelBufferObject;
import academy.hekiyou.unsafemaps.PixelBufferObject.BoundPixelBufferObject;
import net.minecraft.client.texture.*;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(targets = "net/minecraft/client/texture/MapTextureManager$MapTexture")
public abstract class MapTextureMixin {

    @Shadow private boolean needsUpdate;
    @Shadow private NativeImageBackedTexture texture;
    @Shadow private MapState state;

    @Unique private PixelBufferObject pbo;

    @Redirect(
            method = "<init>*",
            at = @At(value = "NEW", target = "net/minecraft/client/texture/NativeImageBackedTexture")
    )
    NativeImageBackedTexture createTextureHandler(Supplier<String> nameSupplier, int width, int height, boolean useStb) {
        // inject a custom NativeImage that's 1bpp to trick GlStateManager into allocating the correct buffer size
        return new NativeImageBackedTexture(nameSupplier, new NativeImage(NativeImage.Format.LUMINANCE, width, height, useStb));
    }

    @Inject(method = "<init>*", at = @At("TAIL"))
    void createPBO(MapTextureManager manager, final int id, final MapState state, CallbackInfo cbInfo) {
        // create PBO buffer for indexes
        this.pbo = new PixelBufferObject(this.texture, 128 * 128);
    }

    @Overwrite
    public void updateTexture() {
        if (this.needsUpdate) {
            NativeImage image = this.texture.getImage();
            if (image != null) {
                // load our PBO buffer
                try (BoundPixelBufferObject bound = this.pbo.bindAndMap()) {
                    bound.getBuffer().put(this.state.colors);
                }
            }
            this.needsUpdate = false;
        }
    }

}
