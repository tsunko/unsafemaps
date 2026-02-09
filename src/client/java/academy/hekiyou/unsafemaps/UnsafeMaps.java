package academy.hekiyou.unsafemaps;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnsafeMaps implements ClientModInitializer {

	public static final String MOD_ID = "unsafemaps";
	private final AtomicBoolean loaded = new AtomicBoolean(false);

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.BEFORE_ENTITIES.register((_) -> {
			boolean isLoaded = loaded.get();
			if (!isLoaded && loaded.compareAndSet(false, true)) {
				MapRenderPipeline.loadLutTexture();
			}
		});
	}

}