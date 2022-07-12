package cn.innc11.chilishop.command.subcommand;

import cn.innc11.chilishop.ChiliShop;
import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.command.SubCommand;
import cn.innc11.chilishop.shop.Shop;
import cn.innc11.chilishop.shop.ShopType;
import cn.innc11.chilishop.utils.Pair;
import cn.innc11.chilishop.utils.Quick;
import cn.innc11.chilishop.localization.LangNodes;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class SellCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			Pair<Boolean, Shop> vi = ChiliShop.ins.shopInteractionListener.isValidInteraction(sender.getName());
			if(vi!=null)
			{
				if(vi.key.booleanValue())
				{
					Shop shop = vi.value;

					if(shop.shopData.owner.equals(sender.getName()))
					{

						if(shop.shopData.type!= ShopType.SELL)
						{
							shop.shopData.type = ShopType.SELL;

							ChiliShop.ins.shopsConfig.getShopsConfig(shop, false).save();

							shop.updateSignText();

							sender.sendMessage(Quick.t(LangNodes.im_shop_type_updated, "TYPE", ShopType.SELL.toString()));
						} else {
							sender.sendMessage(Quick.t(LangNodes.im_shop_type_donot_need_update));
						}

					} else {
						sender.sendMessage(Quick.t(LangNodes.im_not_allow_modify_price_not_owner));
					}
				} else {
					//sender.sendMessage(Quick.t(im_interaction_timeout));
				}

			}else{
				sender.sendMessage(Quick.t(LangNodes.im_not_selected_shop));
			}
		} else {
			sender.sendMessage(Quick.t(LangNodes.im_intercept_console));
		}

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "sell";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"s"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}
}
