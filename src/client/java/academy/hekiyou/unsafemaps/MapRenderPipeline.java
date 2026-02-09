package academy.hekiyou.unsafemaps;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.texture.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class MapRenderPipeline {


    public static final Identifier PIPELINE_LOCATION = Identifier.of(UnsafeMaps.MOD_ID, "pipeline/map_render");
    public static final Identifier FRAGMENT_ID = Identifier.of(UnsafeMaps.MOD_ID, "core/map_render");
    public static final Identifier LUT_TEXTURE_ID = Identifier.of(UnsafeMaps.MOD_ID, "lut_texture");

    // pulled most of this stuff out of net.minecraft.client.render.MapRenderer
    private static final RenderPipeline RENDERTYPE_MAP = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.TEXT_SNIPPET, RenderPipelines.FOG_SNIPPET)
                    .withLocation(PIPELINE_LOCATION)
                    .withVertexShader("core/rendertype_text")
                    .withFragmentShader(FRAGMENT_ID)
                    .withSampler("Sampler0")
                    .withSampler("Sampler1")
                    .withSampler("Sampler2")
                    .build()
    );

    public static final Function<Identifier, RenderLayer> MAP = Util.memoize(
            texture ->
                    RenderLayer.of(
                        "text", RenderSetup.builder(RENDERTYPE_MAP)
                                    .texture("Sampler0", texture)
                                    .texture("Sampler1", LUT_TEXTURE_ID)
                                    .useLightmap()
                                    .expectedBufferSize((128 * 128) + (256 * 4))
                                    .build()
                    )

    );

    public static void loadLutTexture() {
        MinecraftClient client = MinecraftClient.getInstance();
        NativeImage backing = new NativeImage(NativeImage.Format.RGBA, 256, 1, false);
        for (int i=0; i < PrecomputedMapColors.LOOKUP.length; i++) {
            backing.setColorArgb(i, 0, PrecomputedMapColors.LOOKUP[i]);
        }
        client.getTextureManager().registerTexture(LUT_TEXTURE_ID, new NativeImageBackedTexture(LUT_TEXTURE_ID::getPath, backing));
    }

}
