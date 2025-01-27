package cn.innc11.chilishop.listener;

import java.lang.reflect.Field;
import java.util.HashMap;

import cn.innc11.chilishop.ChiliShop;
import cn.innc11.chilishop.config.ShopConfig;
import cn.innc11.chilishop.localization.LangNodes;
import cn.innc11.chilishop.pluginEvent.PlayerCreateShopEvent;
import cn.innc11.chilishop.shop.Shop;
import cn.innc11.chilishop.shop.ShopData;
import cn.innc11.chilishop.utils.Pair;
import cn.innc11.chilishop.utils.Quick;
import cn.innc11.chilishop.virtualLand.VirtualAreaManage;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.permission.PermissibleBase;

public class CreateShopListener implements Listener, ShopInteractionTimer
{
	public HashMap<String, Pair<Long,Block>> creatingShopPlayers = new HashMap<>();
	
	private void createShop(Player player, Block block)
	{
		boolean allowCreateShop = false;

		// check permission
		if(VirtualAreaManage.existingAreaManagementPlugin())
		{
			VirtualAreaManage vl = VirtualAreaManage.getByLoc(block);

			if(vl!=null)
			{
				boolean hasPerm = false;
				hasPerm |= vl.hasPermission(player.getName(), VirtualAreaManage.Permissions.build);
				hasPerm |= vl.getOwner().equals(player.getName());
				hasPerm |= player.isOp() && ChiliShop.ins.pluginConfig.operatorIgnoreBuildPermission;

				if(hasPerm)
				{
					allowCreateShop = true;
				} else {
					player.sendMessage(Quick.t(LangNodes.im_no_residence_permission, "PERMISSION", "build"));
				}
				
			} else {
				allowCreateShop = true;
				
				if(ChiliShop.ins.pluginConfig.onlyCreateShopInResidenceArea)
				{
					allowCreateShop = false;

					player.sendMessage(Quick.t(LangNodes.im_create_shop_in_residence_only));
				}
			}
			
		} else {
			allowCreateShop = true;
		}


		int countLimit = -1;

		if(!ChiliShop.ins.GACPluginLoaded)
		{
			// check for quickshopx.create.<Number>
			PermissibleBase pb = null;

			try {
				Field perm = player.getClass().getDeclaredField("perm");
				perm.setAccessible(true);
				pb = (PermissibleBase) perm.get(player);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}

			for(String p : pb.getEffectivePermissions().keySet())
			{
				if(p.matches("quickshopx\\.create\\.\\d+"))
				{
					countLimit = Integer.parseInt(p.split("\\.")[2]);
					break;
				}
			}
		}

		if(countLimit!=-1)
		{
			int currCount = 0;

			for (ShopConfig sc : ChiliShop.ins.shopsConfig.getAllShops())
			for (ShopData shopData : sc.shopDataMapping.values())
			{
				if(shopData.owner.equals(player.getName()))
					currCount++;
			}

			if(currCount>=countLimit)
			{
				player.sendMessage(Quick.t(LangNodes.im_not_allow_have_more_shop, "MAX", String.valueOf(countLimit)));
				return;
			}
		}

		if(allowCreateShop)
		{
			int interval = ChiliShop.ins.pluginConfig.interactionTimeout;
			
			creatingShopPlayers.put(player.getName(), new Pair<Long,Block>(Long.valueOf(System.currentTimeMillis()+interval), block));
			player.sendMessage(Quick.t(LangNodes.im_creating_shop_enter_price, "TIMEOUT", String.format("%.1f", interval/1000f)));

		}
	}

	// for Creative-Mode
	@EventHandler
	public void onPlayerBrokeBlock(BlockBreakEvent e)
	{
		Player player = e.getPlayer();
		
		if(player.getGamemode()==1
				&& e.getBlock().getId()==Block.CHEST 
				&&player.isSneaking()
				&& (Shop.findShopByChest(e.getBlock())==null))
		{
			createShop(player, e.getBlock());
			
			e.setCancelled();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(e.getAction()==Action.LEFT_CLICK_BLOCK
				&& block.getId()==Block.CHEST 
				&& (Shop.findShopByChest(block)==null))
		{
			createShop(player, block);
			
			e.setCancelled();
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChatEvent(PlayerChatEvent e)
	{
		Player player = e.getPlayer();
		String message = e.getMessage();
		String playerName = player.getName();

		if(creatingShopPlayers.containsKey(playerName))
		{
			if(ChiliShop.ins.shopInteractionListener.interactingShopHashMap.containsKey(player.getName()))
			{
				ChiliShop.ins.shopInteractionListener.interactingShopHashMap.remove(player.getName());
			}

			if(!ChiliShop.isPrice(message))
			{
				player.sendMessage(Quick.t(LangNodes.im_not_a_number));
			}else
			if(!isValidInteraction(playerName).key)
			{
				//player.sendMessage(L.get(Lang.im_interaction_timeout));
				creatingShopPlayers.remove(playerName);
				return;
			}else {
				BlockChest creatingShopChest = (BlockChest) creatingShopPlayers.get(playerName).value;

				Shop createdShop = Shop.placeShop(creatingShopChest, Float.parseFloat(message), player);

				if (createdShop != null)
				{
					PlayerCreateShopEvent event = new PlayerCreateShopEvent(player, createdShop);
					ChiliShop.ins.getServer().getPluginManager().callEvent(event);

					if(!event.isCancelled())
					{
						ChiliShop.ins.hologramListener.addShopItemEntity(Server.getInstance().getOnlinePlayers().values(), createdShop.shopData);
					}

				}
			}
			
			creatingShopPlayers.remove(playerName);
			
			e.setCancelled();
		}
		
	}

	@Override
	public Pair<Boolean, Block> isValidInteraction(String player)
	{
		if(creatingShopPlayers.containsKey(player))
		{
			Pair<Long, Block> cs = creatingShopPlayers.get(player);
			boolean noTimeout = System.currentTimeMillis() < cs.key.longValue();
			return new Pair<>(noTimeout, cs.value);
		}
		
		return null;
	}
}
