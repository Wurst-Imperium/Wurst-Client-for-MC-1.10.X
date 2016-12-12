/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import static net.minecraft.init.Blocks.*;

import java.util.*;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.utils.RenderUtils;

@Info(
	description = "Finds player bases by searching for man-made blocks.\n"
		+ "Good for finding faction bases.",
	name = "BaseFinder",
	tags = "base finder, factions",
	help = "Mods/BaseFinder")
@Bypasses
public class BaseFinderMod extends Mod implements UpdateListener,
	RenderListener
{
	
	private Collection<Block> naturalBlocks = Sets.newHashSet(
		AIR, STONE, DIRT, GRASS, GRAVEL, SAND, CLAY, SANDSTONE,
		FLOWING_WATER, WATER, FLOWING_LAVA, LAVA, LOG, LOG2, LEAVES,
		LEAVES2, DEADBUSH, IRON_ORE, COAL_ORE, GOLD_ORE, DIAMOND_ORE,
		EMERALD_ORE, REDSTONE_ORE, LAPIS_ORE, BEDROCK, MOB_SPAWNER,
		MOSSY_COBBLESTONE, TALLGRASS, YELLOW_FLOWER, RED_FLOWER, WEB,
		BROWN_MUSHROOM, RED_MUSHROOM, SNOW_LAYER, VINE, WATERLILY,
		DOUBLE_PLANT, HARDENED_CLAY, RED_SANDSTONE, ICE, QUARTZ_ORE,
		OBSIDIAN, MONSTER_EGG, RED_MUSHROOM_BLOCK, BROWN_MUSHROOM_BLOCK
	);
	private ArrayList<BlockPos> matchingBlocks = new ArrayList<BlockPos>();
	private int range = 50;
	private int maxBlocks = 1024;
	private boolean shouldInform = true;
	
	@Override
	public void onEnable()
	{
		shouldInform = true;
		wurst.events.add(UpdateListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		for(BlockPos blockPos : matchingBlocks)
			RenderUtils.blockEspBox(blockPos, 1F, 0F, 0F);
	}
	
	@Override
	public void onUpdate()
	{
		updateMS();
		if(hasTimePassedM(3000))
		{
			matchingBlocks.clear();
			
			int playerX = (int) Math.floor(mc.thePlayer.posX);
			int playerY = (int) Math.floor(mc.thePlayer.posY);
			int playerZ = (int) Math.floor(mc.thePlayer.posZ);

			for(int y = range; y >= -range; y--)
				for(int x = range; x >= -range; x--)
					for(int z = range; z >= -range; z--)
					{
						int posX = playerX + x;
						int posY = playerY + y;
						int posZ = playerZ + z;
						BlockPos pos = new BlockPos(posX, posY, posZ);
						if(!naturalBlocks.contains(mc.theWorld.getBlockState(
							pos).getBlock()))
						{
							matchingBlocks.add(pos);
							
							if(foundTooManyBlocks())
								break;
						}
					}
			
			if(foundTooManyBlocks())
			{
				if(shouldInform) {
					wurst.chat.warning(getName() + " found §lA LOT§r of blocks.");
					wurst.chat
					.message("To prevent lag, it will only show the first "
						+ maxBlocks + " blocks.");
					shouldInform = false;
				}
			} else
				shouldInform = true;
			
			updateLastMS();
		}
	}
	
	private boolean foundTooManyBlocks() {
		return matchingBlocks.size() >= maxBlocks;
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
}
