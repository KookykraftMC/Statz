package me.staartvin.statz.listeners;

import java.util.HashMap;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import me.staartvin.statz.Statz;
import me.staartvin.statz.datamanager.PlayerStat;
import me.staartvin.statz.datamanager.player.PlayerInfo;
import me.staartvin.statz.util.StatzUtil;

public class PlayerFishListener implements Listener {

	private final Statz plugin;

	public PlayerFishListener(final Statz plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onFishCaught(final PlayerFishEvent event) {

		final PlayerStat stat = PlayerStat.ITEMS_CAUGHT;

		// Get player
		final Player player = event.getPlayer();

		Entity entity;
		ItemStack item;
		
		String materialName = null;

		if (event.getCaught() != null) {
			entity = event.getCaught();
			
			if (!(entity instanceof Item)) {
				return; // Did not catch an item
			}
			
			item = ((Item) entity).getItemStack();
			
			if (event.getState().equals(State.CAUGHT_FISH)) {
				materialName = StatzUtil.getFoodName(item);
				
				if (materialName == null) {
					materialName = item.getType().toString();
				}
				
			} else {
				materialName = item.getType().toString();
			}
		} else {
			// Did not catch anything.
			return;
		}

		// Get player info.
		final PlayerInfo info = plugin.getDataManager().getPlayerInfo(player.getUniqueId(), stat);

		// Get current value of stat.
		int currentValue = 0;

		// Check if it is valid!
		if (info.isValid()) {
			for (HashMap<String, Object> map : info.getResults()) {				
				if (map.get("caught") != null && map.get("caught").toString().equalsIgnoreCase(materialName)) {
					currentValue += Integer.parseInt(map.get("value").toString());
				}
			}
		}

		// Update value to new stat.
		plugin.getDataManager().setPlayerInfo(player.getUniqueId(), stat,
				StatzUtil.makeQuery("uuid", player.getUniqueId().toString(), "value", (currentValue + 1), "caught", materialName));
	}
}
