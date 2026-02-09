package academy.hekiyou.unsafemaps.mixin.client;

import academy.hekiyou.unsafemaps.PrecomputedMapColors;
import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Arrays;

@Mixin(MapColor.class)
public abstract class MapColorPrecomputeMixin {

	@Overwrite
	public static int getRenderColor(int i) {
		return PrecomputedMapColors.LOOKUP[i & 255];
	}

}