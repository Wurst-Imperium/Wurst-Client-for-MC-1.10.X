/*
 * Copyright © 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events;

import java.util.ArrayList;

import net.minecraft.world.GameType;
import tk.wurst_client.events.listeners.GameModeChangeListener;

public class GameModeChangeEvent extends Event<GameModeChangeListener>
{

	private GameType type;
	
	public static GameModeChangeEvent INSTANCE = new GameModeChangeEvent();
	
	@Override
	public void fire(ArrayList<GameModeChangeListener> listeners)
	{
		for (int i = 0; i < listeners.size(); i++)
			listeners.get(i).onGameModeChange(this.type);
	}

	@Override
	public Class<GameModeChangeListener> getListenerType()
	{
		return GameModeChangeListener.class;
	}

	public GameType getType()
	{
		return type;
	}

	public void setType(GameType type)
	{
		this.type = type;
	}
	
}
