/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import java.util.ArrayList;

import tk.wurst_client.events.listeners.ModToggleListener;
import tk.wurst_client.mods.Mod;

public class ModToggleEvent extends CancellableEvent<ModToggleListener>
{
	
	private final Mod module;
	private final boolean enable;
	
	public ModToggleEvent(Mod module, boolean enable)
	{
		this.module = module;
		this.enable = enable;
	}
	
	/**
	 * @return enable or disable the mod
	 */
	public boolean isEnabling() {
		return enable;
	}
	
	public Mod getModule()
	{
		return module;
	}
	
	@Override
	public void fire(ArrayList<ModToggleListener> listeners)
	{
		for(ModToggleListener listener : listeners) {
			listener.onModuleToggle(this);
		}
	}
	
	@Override
	public Class<ModToggleListener> getListenerType()
	{
		return ModToggleListener.class;
	}
	
}
