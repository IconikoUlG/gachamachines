package xyz.ashyboxy.mc.gachamachines;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class GachaMachinesDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(GachaModelGenerator::new);
	}

	private static class GachaModelGenerator extends FabricModelProvider {
		public GachaModelGenerator(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void generateBlockStateModels(BlockStateModelGenerator generator) {
			// ideally the gacha machine blockstate should be generated here
			generator.registerParentedItemModel(GachaItems.GACHA_MACHINE, GachaMachines.id("block/gacha_machine"));
		}

		@Override
		public void generateItemModels(ItemModelGenerator generator) {
			generator.register(GachaItems.RED_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.GREEN_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.YELLOW_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.IRON_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.GOLD_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.DIAMOND_CAPSULE, Models.GENERATED);
			generator.register(GachaItems.EMERALD_CAPSULE, Models.GENERATED);
		}
	}
}
