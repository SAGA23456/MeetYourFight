package lykrast.meetyourfight.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class CurioBaseItem extends Item implements ICurioItem {
	private boolean hasDescription;
	private Object[] args;

	public CurioBaseItem(Properties properties, boolean hasDescription, Object... args) {
		super(properties);
		this.hasDescription = hasDescription;
		this.args = args;
	}

	public CurioBaseItem(Properties properties, boolean hasDescription) {
		this(properties, hasDescription, new Object[0]);
	}

	@Override
	public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (hasDescription) tooltip.add(Component.translatable(getDescriptionId() + ".desc", args).withStyle(ChatFormatting.GRAY));
	}

}
