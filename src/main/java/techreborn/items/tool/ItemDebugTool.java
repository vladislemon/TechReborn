/*
 * This file is part of TechReborn, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018 TechReborn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package techreborn.items.tool;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Property;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import reborncore.api.power.IEnergyInterfaceTile;
import reborncore.common.powerSystem.PowerSystem;
import techreborn.TechReborn;

import java.util.Map.Entry;

/**
 * Created by Mark on 20/03/2016.
 */
public class ItemDebugTool extends Item {

	public ItemDebugTool() {
		super(new Item.Settings().group(TechReborn.ITEMGROUP));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		Block block = blockState.getBlock();
		if (block == null) {
			return ActionResult.FAIL;
		}
		sendMessage(context, new LiteralText(getRegistryName(block)));
		for (Entry<Property<?>, Comparable<?>> entry : blockState.getEntries().entrySet()) {
			sendMessage(context, new LiteralText(getPropertyString(entry)));
		}
		BlockEntity tile = context.getWorld().getBlockEntity(context.getBlockPos());
		if (tile != null) {
			sendMessage(context, new LiteralText(getTileEntityType(tile)));
			if (tile instanceof IEnergyInterfaceTile) {
				sendMessage(context, new LiteralText(getRCPower((IEnergyInterfaceTile) tile)));
			}
		}
		return ActionResult.SUCCESS;
	}

	private void sendMessage(ItemUsageContext context, Text string) {
		if (!context.getWorld().isClient) {
			context.getPlayer().sendMessage(string);
		}
	}

	private String getPropertyString(Entry<Property<?>, Comparable<?>> entryIn) {
		Property<?> iproperty = entryIn.getKey();
		Comparable<?> comparable = entryIn.getValue();
		String s = SystemUtil.getValueAsString(iproperty, comparable);
		if (Boolean.TRUE.equals(comparable)) {
			s = Formatting.GREEN + s;
		} else if (Boolean.FALSE.equals(comparable)) {
			s = Formatting.RED + s;
		}

		return iproperty.getName() + ": " + s;
	}

	private String getRegistryName(Block block) {
		String s = "" + Formatting.GREEN;
		s += "Block Registry Name: ";
		s += Formatting.BLUE;
		s += Registry.BLOCK.getId(block);

		return s;
	}
	
	private String getTileEntityType(BlockEntity tile) {
		String s = "" + Formatting.GREEN;
		s += "Tile Entity: ";
		s += Formatting.BLUE;
		s += tile.getType().toString();

		return s;
	}
	
	private String getRCPower(IEnergyInterfaceTile tile) {
		String s = "" + Formatting.GREEN;
		s += "Power: ";
		s += Formatting.BLUE;
		s += PowerSystem.getLocaliszedPower(tile.getEnergy());
		s += "/";
		s += PowerSystem.getLocaliszedPower(tile.getMaxPower());
		
		return s;
	}
}