/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.util.math.BlockPos;
import tk.wurst_client.events.PacketInputEvent;
import tk.wurst_client.events.listeners.PacketInputListener;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.utils.BlockUtils;
import tk.wurst_client.utils.RenderUtils;

@Info(category = Category.RENDER,
	description = "Finds far players during thunderstorms.",
	name = "PlayerFinder",
	tags = "player finder",
	help = "Mods/PlayerFinder")
@Bypasses
public class PlayerFinderMod extends Mod
	implements PacketInputListener, RenderListener
{
	final Color tracerColor = new Color(0.5f, 0.5f, 0.5f);
	Map blocksPos = new HashMap();
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.mods.playerEspMod,
			wurst.mods.tracersMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(PacketInputListener.class, this);
		wurst.events.add(RenderListener.class, this);
	}
	
	@Override
	public void onRender()
	{
		if(blocksPos.size() == 0)
			return;
			
		// Iterates through the hash map, needs the try catch block as sometimes
		// the this.listenToBlockPosition is called WHILE it is iterating
		try
		{
			Iterator it = blocksPos.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				BlockPos pos = (BlockPos)pair.getKey();
				
				RenderUtils.tracerLine(pos.getX(), pos.getY(), pos.getZ(),
					tracerColor);
				RenderUtils.blockEsp(pos, 0.3, 0.3, 0.3);
				
				// Removes the block position if it is older than 20 seconds
				if((long)pair.getValue() + 20000 <= System.currentTimeMillis())
				{
					it.remove();
				}
			}
		}catch(Exception e)
		{
			
		}
		
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(PacketInputListener.class, this);
		wurst.events.remove(RenderListener.class, this);
	}
	
	@Override
	public void onReceivedPacket(PacketInputEvent event)
	{
		if(mc.thePlayer == null)
			return;
		Packet packet = event.getPacket();
		if(packet instanceof SPacketBlockAction)
		{
			BlockPos pos = ((SPacketBlockAction)packet).getBlockPosition();
			this.listenToBlockPosition(pos);
		}else if(packet instanceof SPacketBlockChange)
		{
			BlockPos pos = ((SPacketBlockChange)packet).getBlockPosition();
			this.listenToBlockPosition(pos);
		}else if(packet instanceof SPacketBlockBreakAnim)
		{
			BlockPos pos = ((SPacketBlockBreakAnim)packet).getPosition();
			this.listenToBlockPosition(pos);
		}else if(packet instanceof SPacketEffect)
		{
			SPacketEffect effect = (SPacketEffect)packet;
			BlockPos pos = effect.getSoundPos();
			this.listenToBlockPosition(pos);
		}else if(packet instanceof SPacketSoundEffect)
		{
			SPacketSoundEffect sound = (SPacketSoundEffect)packet;
			BlockPos pos =
				new BlockPos(sound.getX(), sound.getY(), sound.getZ());
			this.listenToBlockPosition(pos);
		}else if(packet instanceof SPacketSpawnGlobalEntity)
		{
			SPacketSpawnGlobalEntity lightning =
				(SPacketSpawnGlobalEntity)packet;
			BlockPos pos = new BlockPos(lightning.getX() / 32D,
				lightning.getY() / 32D, lightning.getZ() / 32D);
			this.listenToBlockPosition(pos);
		}
	}
	
	// Used to add a position and time added to the hash map if the pos is far
	// enough
	public void listenToBlockPosition(BlockPos pos)
	{
		if(this.isAGoodBlockPos(pos))
		{
			this.blocksPos.put(pos, System.currentTimeMillis());
		}
	}
	
	// Checks if the given pos isn't too close to another listened pos
	// and it is not too close to the player
	// prevents a lot of false positives and "tracers spam"
	private boolean isAGoodBlockPos(BlockPos testPos)
	{
		if(BlockUtils.getHorizontalPlayerBlockDistance(testPos) < 50f)
		{
			return false;
		}
		
		try
		{
			Iterator it = blocksPos.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry pair = (Map.Entry)it.next();
				BlockPos pos = (BlockPos)pair.getKey();
				
				if(BlockUtils.getBlockDistance(pos, testPos) < 10f)
				{
					return false;
				}
			}
		}catch(Exception e)
		{
			
		}
		
		return true;
	}
}
