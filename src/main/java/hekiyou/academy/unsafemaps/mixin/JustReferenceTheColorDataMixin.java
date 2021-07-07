package hekiyou.academy.unsafemaps.mixin;

import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MapState.UpdateData.class)
public class JustReferenceTheColorDataMixin {

	@Shadow public byte[] colors;

	/**
	 * @reason For whatever reason, the normal vanilla procedure for updating a map is as follows:
	 * - Receive 1D array of pixel data
	 * - Iterate over 1D as 2D array
	 * - For every pixel at (x, y), call MapState.setColor(x, y, pixel)
	 * - ... which then takes the 2D coordinates, and then converts it _back_ into a 1D index
	 * - With the 1D index, write the pixel color
	 *
	 * The obvious technical inefficiency is iterating the 1D as 2D, then using those 2D coordinates to go back to 1D.
	 * However, the slightly less obvious issue is the fact that we do this for every pixel regardless of how much
	 * has changed. If incoming colors are 128 x 128, why bother copying?
	 *
	 * With this Mixin, we just set target.colors to our input colors iff we're updating
	 * the whole map. Otherwise, we fallback to the old slower loop with support for offsets.
	 * @param target The MapState to update using the new colors
	 * @author tsunko
	 */
	@Inject(method = "setColorsTo(Lnet/minecraft/item/map/MapState;)V", at = @At(value = "HEAD"), cancellable = true)
	public void setColorsTo(MapState target, CallbackInfo info) {
		if(colors.length == 128*128) {
			// cool, we can just simply set target.colors instead of doing a costly copy
			target.colors = colors;
			info.cancel();
		}
	}


}
