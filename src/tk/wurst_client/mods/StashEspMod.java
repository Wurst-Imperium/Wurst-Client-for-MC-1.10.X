package tk.wurst_client.mods;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import tk.wurst_client.events.listeners.RenderListener;
import tk.wurst_client.mods.Mod.Bypasses;
import tk.wurst_client.mods.Mod.Category;
import tk.wurst_client.mods.Mod.Info;
import tk.wurst_client.navigator.NavigatorItem;
import tk.wurst_client.navigator.settings.SliderSetting;
import tk.wurst_client.utils.RenderUtils;

@Info(
        description = "Allows you to see hidden storage through walls.",
        name = "StashESP",
        tags = "StashFinder, stash esp, stash finder",
        help = "Mods/StashESP")
@Bypasses
public class StashEspMod extends Mod implements RenderListener {

    private int maxStashes = 1000;

    @Override
    public NavigatorItem[] getSeeAlso()
    {
        return new NavigatorItem[]{wurst.mods.chestEspMod, wurst.mods.itemEspMod, wurst.mods.searchMod,
                wurst.mods.xRayMod};
    }

    @Override
    public void onEnable()
    {
        wurst.events.add(RenderListener.class, this);
    }

    @Override
    public void onRender() {
        int stashes = 0;

        for (int i = 0; i < mc.theWorld.loadedTileEntityList.size(); i++) {
            TileEntity tileEntity = mc.theWorld.loadedTileEntityList.get(i);

            if (stashes >= maxStashes)
                break;

            if (tileEntity instanceof TileEntityDispenser ||
                    tileEntity instanceof TileEntityHopper ||
                    tileEntity instanceof TileEntityFurnace) {
                stashes++;
                RenderUtils.blockEspFrame(tileEntity.getPos(), 1, 0.5, 0);
                RenderUtils.blockEsp(tileEntity.getPos(), 1, 0.5, 0);
            }
        }
    }

    @Override
    public void onDisable()
    {
        wurst.events.remove(RenderListener.class, this);
    }
}