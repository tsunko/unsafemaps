package academy.hekiyou.unsafemaps.mixin.client;

import academy.hekiyou.unsafemaps.MapRenderPipeline;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MapRenderer.class)
public abstract class MapRendererMixin {

    @WrapOperation(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayers;text(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer returnMapPipeline(Identifier id, Operation<RenderLayer> original) {
        if (id.getPath().startsWith("map/")) {
            return MapRenderPipeline.MAP.apply(id);
        }
        return original.call(id);
    }

}