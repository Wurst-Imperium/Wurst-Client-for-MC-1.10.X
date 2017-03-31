/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.features.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.events.listeners.UpdateListener;
import net.wurstclient.features.Feature;
import net.wurstclient.features.mods.Mod.Bypasses;
import net.wurstclient.features.mods.Mod.Info;

@Info(
	description = "Turns your bow into a machine gun.\n"
		+ "Tip: This works with BowAimbot.",
	name = "FastBow",
	tags = "RapidFire, BowSpam, fast bow, rapid fire, bow spam",
	help = "Mods/FastBow")
@Bypasses(ghostMode = false, latestNCP = false, olderNCP = false)
public class FastBowMod extends Mod implements UpdateListener
{
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.bowAimbotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.thePlayer.getHealth() > 0 && (mc.thePlayer.onGround
			|| Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
			&& mc.thePlayer.inventory.getCurrentItem() != null
			&& mc.thePlayer.inventory.getCurrentItem()
				.getItem() instanceof ItemBow
			&& mc.gameSettings.keyBindUseItem.pressed)
		{
			mc.playerController.processRightClick(mc.thePlayer, mc.theWorld,
				mc.thePlayer.inventory.getCurrentItem(), EnumHand.MAIN_HAND);
			mc.thePlayer.inventory.getCurrentItem().getItem().onItemRightClick(
				mc.thePlayer.inventory.getCurrentItem(), mc.theWorld,
				mc.thePlayer, EnumHand.MAIN_HAND);
			for(int i = 0; i < 20; i++)
				mc.thePlayer.connection.sendPacket(new CPacketPlayer(false));
			Minecraft.getMinecraft().getConnection()
				.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM,
					new BlockPos(0, 0, 0), EnumFacing.DOWN));
			mc.thePlayer.inventory.getCurrentItem().getItem()
				.onPlayerStoppedUsing(mc.thePlayer.inventory.getCurrentItem(),
					mc.theWorld, mc.thePlayer, 10);
		}
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
