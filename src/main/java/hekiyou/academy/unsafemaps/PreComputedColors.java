package hekiyou.academy.unsafemaps;

import net.minecraft.block.MapColor;

import java.util.Arrays;

public class PreComputedColors {

    /**
     * A simple lookup table with 255 elements (for all of our colors)
     * We pre-calculate each element based on MapColor.COLORS.
     */
    public static final int[] LOOKUP;
    static {
        int[] arr = new int[255];
        Arrays.fill(arr, 0);

        // skip clear since it needs to be 0 and getRenderColor would actually give us black otherwise
        MapColor[] baseColors = MapColor.COLORS;
        for(int id=1; id < baseColors.length; id++){
            MapColor base = baseColors[id];
            if(base == null) break;

            for(int shade=0; shade < 4; shade++){
                arr[id * 4 + shade] = base.getRenderColor(shade);
            }
        }

        LOOKUP = arr;
    }

}
