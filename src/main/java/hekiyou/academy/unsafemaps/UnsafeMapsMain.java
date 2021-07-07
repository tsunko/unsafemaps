package hekiyou.academy.unsafemaps;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class UnsafeMapsMain implements ModInitializer {

	private static final String SUMMON_COMMAND = "/summon minecraft:glow_item_frame ~%d ~%d ~ {Facing:3,Item:{Count:1,id:\"minecraft:filled_map\",tag:{map:%d}}}";
	private static final String SETBLOCK_COMMAND = "/setblock ~%d ~%d ~-1 minecraft:sandstone";

	@Override
	public void onInitialize() {
		// register client-side utility command to generate displays facing south
		ClientCommandManager.DISPATCHER.register(literal("create-display")
			.then(argument("width", integer())
				.then(argument("height", integer())
					.executes(context -> {
						ClientPlayerEntity entity = MinecraftClient.getInstance().player;
						if(entity == null)
							throw new IllegalStateException("Attempted to execute command with no player set.");


						int width = context.getArgument("width", int.class);
						int height = context.getArgument("height", int.class);

						for(int y=0; y < height; y++){
							for(int x=0; x < width; x++){
								int mapId = y * width + x;
								int inverseY = height - y - 1;

								entity.sendChatMessage(String.format(SETBLOCK_COMMAND, x, inverseY));
								entity.sendChatMessage(String.format(SUMMON_COMMAND, x, inverseY, mapId));
							}
						}

						return 0;
					})
				)
			)
		);
	}

}
