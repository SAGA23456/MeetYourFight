package lykrast.meetyourfight.misc;

import lykrast.meetyourfight.MeetYourFight;
import lykrast.meetyourfight.registry.ModItems;
import lykrast.meetyourfight.registry.ModSounds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mod.EventBusSubscriber(modid = MeetYourFight.MODID)
public class EventHandler {	
	@SubscribeEvent
	public static void entityDamage(final LivingHurtEvent event) {
		//Full damage prevention
		LivingEntity attacked = event.getEntityLiving();
		if (attacked instanceof Player) {
			Player pattacked = (Player)attacked;
			//Ace of Iron
			if (!event.isCanceled() && CuriosApi.getCuriosHelper().findFirstCurio(pattacked, ModItems.aceOfIron).isPresent()) {
				float luck = pattacked.getLuck();
				double chance = 1.0 / 6.0;
				if (luck >= 0) chance = (1.0 + luck) / (6.0 + 2 * luck);
				else chance = 1.0 / (6.0 - 3 * luck);
				if (pattacked.getRandom().nextDouble() <= chance) {
					event.setCanceled(true);
					pattacked.level.playSound(null, attacked.blockPosition(), ModSounds.aceOfIronProc, SoundSource.PLAYERS, 1, 1);
				}
			}
		}
		
		//No need to further modify damage if the damage is cancelled
		if (event.isCanceled()) return;
		
		//Damage increases
		Entity attacker = event.getSource().getEntity();
		if (attacker != null && attacker instanceof Player) {
			Player pattacker = (Player)attacker;
			//Slicer's Dice
			if (CuriosApi.getCuriosHelper().findFirstCurio(pattacker, ModItems.slicersDice).isPresent()) {
				float luck = pattacker.getLuck();
				double chance = 0.2;
				if (luck >= 0) chance = (1.0 + luck) / (5.0 + luck);
				else chance = 1.0 / (5.0 - 3 * luck);
				if (pattacker.getRandom().nextDouble() <= chance) {
					event.setAmount(event.getAmount() * 2);
					pattacker.level.playSound(null, attacked.blockPosition(), ModSounds.slicersDiceProc, SoundSource.PLAYERS, 1, 1);
					((ServerLevel)pattacker.level).sendParticles(ParticleTypes.CRIT, attacked.getX(), attacked.getEyeY(), attacked.getZ(), 15, 0.2, 0.2, 0.2, 0);
				}
			}
		}
		
		//Damage decreases
		if (attacked instanceof Player) {
			Player pattacked = (Player)attacked;
			//Caged Heart
			if (!event.isCanceled() && CuriosApi.getCuriosHelper().findFirstCurio(pattacked, ModItems.cagedHeart).isPresent()) {
				float treshold = pattacked.getMaxHealth() / 4.0f;
				if (event.getAmount() > treshold) {
					event.setAmount((event.getAmount() - treshold) * 0.5f + treshold);
					pattacked.level.playSound(null, attacked.blockPosition(), ModSounds.cagedHeartProc, SoundSource.PLAYERS, 1, 1);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void attachCapability(final AttachCapabilitiesEvent<ItemStack> event) {
		//Copied and adjusted from Enigmatic Legacy
		ItemStack stack = event.getObject();
		if (stack.getItem() instanceof ICurio && stack.getItem().getRegistryName().getNamespace().equals(MeetYourFight.MODID)) {
			event.addCapability(CuriosCapability.ID_ITEM, new Provider((ICurio) stack.getItem()));
		}
	}
	
	private static class Provider implements ICapabilityProvider {
		private LazyOptional<ICurio> curio;

		public Provider(ICurio curio) {
			this.curio = LazyOptional.of(() -> curio);
		}

		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return CuriosCapability.ITEM.orEmpty(cap, curio);
		}
		
	}
}
