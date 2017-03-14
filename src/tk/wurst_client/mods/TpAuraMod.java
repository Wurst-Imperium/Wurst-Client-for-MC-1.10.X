/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.mods;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.CheckboxSetting;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.navigator.settings.SliderSetting.ValueDisplay;
import tk.wurst_client.utils.EntityUtils;
import tk.wurst_client.utils.EntityUtils.TargetSettings;

@Mod.Info(
	description = "Automatically attacks the closest valid entity while teleporting around it.",
	name = "TP-Aura",
	tags = "TpAura, EnderAura, Ender-Aura, tp aura, ender aura",
	help = "Mods/TP-Aura")
@Bypasses(ghostMode = false,
	latestNCP = false,
	olderNCP = false,
	antiCheat = false)
public class TpAuraMod extends Mod implements UpdateListener
{
	private Random random = new Random();
	
	public CheckboxSetting useKillaura =
		new CheckboxSetting("Use Killaura settings", true)
		{
			@Override
			public void update()
			{
				if(isChecked())
				{
					KillauraMod killaura = wurst.mods.killauraMod;
					useCooldown.lock(killaura.useCooldown.isChecked());
					speed.lockToValue(killaura.speed.getValue());
					range.lockToValue(killaura.range.getValue());
					fov.lockToValue(killaura.fov.getValue());
					hitThroughWalls.lock(killaura.hitThroughWalls.isChecked());
				}else
				{
					useCooldown.unlock();
					speed.unlock();
					range.unlock();
					fov.unlock();
					hitThroughWalls.unlock();
				}
			};
		};
	public CheckboxSetting useCooldown =
		new CheckboxSetting("Use Attack Cooldown as Speed", true)
		{
			@Override
			public void update()
			{
				speed.setDisabled(isChecked());
			};
		};
	public SliderSetting speed =
		new SliderSetting("Speed", 20, 0.1, 20, 0.1, ValueDisplay.DECIMAL);
	public SliderSetting range =
		new SliderSetting("Range", 6, 1, 6, 0.05, ValueDisplay.DECIMAL);
	// TODO: Does it even make sense to have an FOV setting for this?
	public SliderSetting fov =
		new SliderSetting("FOV", 360, 30, 360, 10, ValueDisplay.DEGREES);
	public CheckboxSetting hitThroughWalls =
		new CheckboxSetting("Hit through walls", false);
	
	private TargetSettings targetSettings = new TargetSettings()
	{
		@Override
		public boolean targetBehindWalls()
		{
			return hitThroughWalls.isChecked();
		}
		
		@Override
		public float getRange()
		{
			return range.getValueF();
		}
		
		@Override
		public float getFOV()
		{
			return fov.getValueF();
		}
	};
	
	@Override
	public void initSettings()
	{
		settings.add(useKillaura);
		settings.add(useCooldown);
		settings.add(speed);
		settings.add(range);
		settings.add(fov);
		settings.add(hitThroughWalls);
	}
	
	@Override
	public NavigatorItem[] getSeeAlso()
	{
		return new NavigatorItem[]{wurst.special.targetSpf,
			wurst.mods.killauraMod, wurst.mods.killauraLegitMod,
			wurst.mods.multiAuraMod, wurst.mods.clickAuraMod,
			wurst.mods.triggerBotMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// update timer
		updateMS();
		
		// set entity
		Entity entity = EntityUtils.getClosestEntity(targetSettings);
		
		// head rotation
		EntityUtils.lookChanged = entity != null;
		if(!EntityUtils.lookChanged)
			return;
		
		// teleport
		mc.thePlayer.setPosition(entity.posX + random.nextInt(3) * 2 - 2,
			entity.posY, entity.posZ + random.nextInt(3) * 2 - 2);
		
		// check timer / cooldown
		if(useCooldown.isChecked()
			? mc.thePlayer.getCooledAttackStrength(0F) < 1F
			: !hasTimePassedS(speed.getValueF()))
			return;
		
		// AutoSword
		if(wurst.mods.autoSwordMod.isActive())
			AutoSwordMod.setSlot();
		
		// Criticals
		wurst.mods.criticalsMod.doCritical();
		
		// BlockHit
		wurst.mods.blockHitMod.doBlock();
		
		// face entity
		EntityUtils.faceEntityPacket(entity);
		
		// attack entity
		mc.playerController.attackEntity(mc.thePlayer, entity);
		mc.thePlayer.swingArm(EnumHand.MAIN_HAND);
		
		// reset timer
		updateLastMS();
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
		EntityUtils.lookChanged = false;
	}
}
