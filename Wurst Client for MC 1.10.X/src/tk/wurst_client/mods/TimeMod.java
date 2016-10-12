package tk.wurst_client.mods;

import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.navigator.settings.SliderSetting.ValueDisplay;

@Info(category = Category.RENDER,
	description = "Changes the world time in the client side.",
	name = "Time",
	help = "Mods/Time")
@Bypasses(ghostMode = true)
public class TimeMod extends Mod
{
	public long time = 12000;
	
	@Override
	public void initSettings()
	{
		settings.add(new SliderSetting("Time", time, 0, 24000, 1,
			ValueDisplay.INTEGER)
		{
			@Override
			public void update()
			{
				time = (long)getValue();
			}
		});
	}
}
