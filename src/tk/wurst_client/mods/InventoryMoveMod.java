/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package tk.wurst_client.mods;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.mods.Mod.Info;

@Info(description = "You can walk arround while guis are open",
	name = "InventoryMove")
public class InventoryMoveMod extends Mod implements UpdateListener
{
	
	/**
	 * All KeyBindings which will be pressable if a gui screen is open.
	 */
	private final KeyBinding[] movementKeybinds;
	
	{
		GameSettings gs = mc.gameSettings;
		
		movementKeybinds = new KeyBinding[]{gs.keyBindForward, gs.keyBindBack,
			gs.keyBindLeft, gs.keyBindRight,
			
			gs.keyBindJump, gs.keyBindSneak};
	}
	
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	public void onUpdate()
	{
		if(isGuiOpen())
			for(KeyBinding keybind : movementKeybinds)
				updateKeybind(keybind);
	}
	
	private boolean isGuiOpen()
	{
		return mc.currentScreen != null;
	}
	
	private void updateKeybind(KeyBinding keyBinding)
	{
		keyBinding.pressed = Keyboard.isKeyDown(keyBinding.getKeyCode());
	}
	
}
