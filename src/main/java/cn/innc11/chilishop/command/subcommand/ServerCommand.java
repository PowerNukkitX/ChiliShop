package cn.innc11.chilishop.command.subcommand;

import cn.innc11.chilishop.ChiliShop;
import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.command.SubCommand;
import cn.innc11.chilishop.shop.Shop;
import cn.innc11.chilishop.utils.Pair;
import cn.innc11.chilishop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.innc11.chilishop.localization.LangNodes.*;
import static cn.innc11.chilishop.localization.LangNodes.im_intercept_console;

public class ServerCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			if(sender.isOp())
			{
				Pair<Boolean, Shop> vi = ChiliShop.ins.shopInteractionListener.isValidInteraction(sender.getName());

				if(vi!=null)
				{
					Shop shop = vi.value;

					shop.shopData.serverShop = !shop.shopData.serverShop;

					ChiliShop.ins.shopsConfig.getShopsConfig(shop, false).save();

					shop.updateSignText();

					sender.sendMessage(shop.shopData.serverShop ? Quick.t(im_shop_updated_server) : Quick.t(im_shop_updated_ordinary));
				} else {
					sender.sendMessage(Quick.t(im_not_selected_shop));
				}
			}
		} else {
			sender.sendMessage(Quick.t(im_intercept_console));
		}

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "server";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"se"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}
}
