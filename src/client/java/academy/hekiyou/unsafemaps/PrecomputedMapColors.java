package academy.hekiyou.unsafemaps;

import net.minecraft.block.MapColor;

import java.util.Arrays;

public class PrecomputedMapColors {

    public static final int[] LOOKUP;

    static {
        int[] arr = new int[255];
        Arrays.fill(arr, 0);

        // skip clear since it needs to be 0 and getRenderColor would actually give us black otherwise
        for (int id = 1; id < MapColor.COLORS.length; id++) {
            MapColor baseColor = MapColor.COLORS[id];
            if (baseColor == null) continue;

            for (int shade=0; shade < 4; shade++) {
                MapColor.Brightness brightness = MapColor.Brightness.validateAndGet(shade);
                arr[id * 4 + shade] = baseColor.getRenderColor(brightness);
            }
        }
        LOOKUP = arr;
    }

}
