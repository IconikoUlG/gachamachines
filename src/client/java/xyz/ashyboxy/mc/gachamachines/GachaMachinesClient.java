package xyz.ashyboxy.mc.gachamachines;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;

public class GachaMachinesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(GachaMachines.GACHA_MACHINE, RenderLayer.getTranslucent());
		HandledScreens.register(GachaMachines.GACHA_MACHINE_SCREEN_HANDLER, GachaMachineScreen::new);
	}
}
