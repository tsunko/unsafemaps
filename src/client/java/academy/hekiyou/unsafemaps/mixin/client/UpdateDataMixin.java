package academy.hekiyou.unsafemaps.mixin.client;

import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapState.UpdateData.class)
public abstract class UpdateDataMixin {

    @Final @Shadow private byte[] colors;

    @Inject(method = "setColorsTo(Lnet/minecraft/item/map/MapState;)V",
            at = @At("HEAD"), cancellable = true)
    public void setColorsTo(MapState mapState, CallbackInfo info) {
        if(colors.length == 128*128) {
            // cool, we can just simply set target.colors instead of doing a costly copy
            mapState.colors = colors;
            info.cancel();
        }
    }

}
