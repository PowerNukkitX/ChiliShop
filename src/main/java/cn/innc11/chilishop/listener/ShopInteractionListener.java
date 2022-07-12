package cn.innc11.chilishop.listener;

import java.util.HashMap;

import cn.innc11.chilishop.ChiliShop;
import cn.innc11.chilishop.form.ShopOwnerPanel;
import cn.innc11.chilishop.form.TradingPanel;
import cn.innc11.chilishop.localization.LangNodes;
import cn.innc11.chilishop.shop.BuyShop;
import cn.innc11.chilishop.shop.SellShop;
import cn.innc11.chilishop.shop.Shop;
import cn.innc11.chilishop.utils.Pair;
import cn.innc11.chilishop.utils.Quick;
import cn.innc11.chilishop.virtualLand.VirtualAreaManage;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockChest;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;

public class ShopInteractionListener implements Listener, ShopInteractionTimer
{
	public HashMap<String, Pair<Long, Shop>> interactingShopHashMap = new HashMap<>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) 
	{
		if(e.getBlock() instanceof BlockWallSign && e.getAction()== PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
		{
			Block block = e.getBlock();
			Player player = e.getPlayer();
			String playerName = player.getName();
			
			Shop shop = Shop.findShopBySign(block);
			
			if(shop!=null)
			{
				shop.updateSignText();
				
				ChiliShop.ins.hologramListener.addShopItemEntity(Server.getInstance().getOnlinePlayers().values(), shop.shopData);

				switch (ChiliShop.ins.pluginConfig.interactionMethod)
				{
					case Both:
					{
						Pair<Boolean, Shop> interactionInfo = isValidInteraction(playerName);
						Shop interactiveShop = interactionInfo!=null? interactionInfo.value:null;
						boolean noTimeout = interactionInfo!=null? interactionInfo.key:true;

						if(interactionInfo!=null && interactiveShop.equals(shop) && noTimeout)
						{
							if(player.getName().equals(shop.shopData.owner) || player.isOp())
								player.showFormWindow(new ShopOwnerPanel(shop, player.getName()));
							else
								player.showFormWindow(new TradingPanel(shop, player.getName()));
							interactingShopHashMap.remove(e.getPlayer().getName());
							break;
						}
						// no 'break;'
					}

					case ChatBar:
					{
						Item shopItem = shop.getItem();
						
						// show the shop detail information
						player.sendMessage(Quick.t(LangNodes.im_shop_info,
								"GOODS", ChiliShop.ins.itemNameTranslationConfig.getItemName(shopItem),
								"PRICE", String.format("%.1f", shop.shopData.price),
								"OWNER", shop.shopData.serverShop? Quick.t(LangNodes.server_nickname):shop.shopData.owner,
								"TYPE", shop.shopData.type.toString(),
								"STOCK", ChiliShop.ins.localization.signText.getStockText(shop),
                                "ENCHANTMENTS", Quick.getEnchantments(shopItem)
                        ));

						//if(!player.getName().equals(shop.shopData.owner))
						//{
						player.sendMessage(Quick.t(LangNodes.im_enter_transactions_volume));
						//}
						
						interactingShopHashMap.put(player.getName(), new Pair(System.currentTimeMillis()+ ChiliShop.ins.pluginConfig.interactionTimeout, shop));
						break;
					}
					
						
					case Interface:
						if(player.getName().equals(shop.shopData.owner))
							player.showFormWindow(new ShopOwnerPanel(shop, player.getName()));
						else
							player.showFormWindow(new TradingPanel(shop, player.getName()));

						interactingShopHashMap.put(player.getName(), new Pair(System.currentTimeMillis()+ ChiliShop.ins.pluginConfig.interactionTimeout, shop));
						
						break;
				}
				
				e.setCancelled();
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerChatEvent(PlayerChatEvent e)
	{
		Player player = e.getPlayer();
		String playerName = player.getName();
		Pair<Boolean, Shop> interactionInfo = isValidInteraction(playerName);
		Shop interactiveShop = interactionInfo!=null? interactionInfo.value:null;
		boolean noTimeout = interactionInfo!=null? interactionInfo.key:true;

		if(interactionInfo!=null)
		{
			if(noTimeout)
			{
				if (ChiliShop.isInteger(e.getMessage()))
				{
					if (interactiveShop instanceof BuyShop) {
						((BuyShop) interactiveShop).buyItem(player, Integer.parseInt(e.getMessage()));
					} else if (interactiveShop instanceof SellShop) {
						((SellShop) interactiveShop).sellItme(player, Integer.parseInt(e.getMessage()));
					}

				} else {
					player.sendMessage(Quick.t(LangNodes.im_not_a_number));
				}

				e.setCancelled();
			}

			interactingShopHashMap.remove(playerName);
		}


	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerBrokeBlockEvent(BlockBreakEvent e)
	{
		if(e.getBlock() instanceof BlockWallSign || e.getBlock() instanceof BlockChest)
		{
			Player player = e.getPlayer();

			boolean isSign = e.getBlock() instanceof BlockWallSign;
			Shop shop = isSign? Shop.findShopBySign(e.getBlock()):Shop.findShopByChest(e.getBlock());

			if(shop!=null)
			{
				boolean allow = true;
				
				if(VirtualAreaManage.existingAreaManagementPlugin())
				{
					VirtualAreaManage vl = VirtualAreaManage.getByLoc(e.getBlock());

					if(vl!=null)
					{
						boolean hasPerm = false;
						hasPerm |= vl.hasPermission(player.getName(), VirtualAreaManage.Permissions.build);
						hasPerm |= vl.getOwner().equals(player.getName());
						hasPerm |= (player.isOp() && ChiliShop.ins.pluginConfig.operatorIgnoreBuildPermission);

						if(!hasPerm)
						{
							player.sendMessage(Quick.t(LangNodes.im_no_residence_permission, "PERMISSION", "build"));
							allow = false;
						}
					}
				} else {
					if(!player.getName().equals(shop.shopData.owner) && !player.isOp())
					{
						allow = false;
						player.sendMessage(Quick.t(LangNodes.im_not_allow_remove_shop_not_owner));
					}
				}
				
				if(allow)
				{
					if(shop.destroy(player))
					{
						ChiliShop.ins.hologramListener.removeItemEntity(Server.getInstance().getOnlinePlayers().values(), shop.shopData);
						if(isSign) e.setDrops(new Item[0]);
						player.sendMessage(Quick.t(LangNodes.im_successfully_removed_shop));
					}
				} else {
					player.sendMessage(Quick.t(LangNodes.im_no_residence_permission, "PERMISSION", "build"));
					e.setCancelled();
				}
			}
			
		}
		
	}

	@Override
	public Pair<Boolean, Shop> isValidInteraction(String player)
	{
		if(interactingShopHashMap.containsKey(player))
		{
			Pair<Long, Shop> is = interactingShopHashMap.get(player);
			boolean noTimeout = System.currentTimeMillis() < is.key.longValue();
			return new Pair<>(noTimeout, is.value);
		}
		
		return null;
	}

}
