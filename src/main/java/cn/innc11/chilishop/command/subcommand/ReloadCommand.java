package cn.innc11.chilishop.command.subcommand;

import cn.innc11.chilishop.ChiliShop;
import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.command.SubCommand;
import cn.innc11.chilishop.utils.Quick;
import cn.innc11.chilishop.localization.LangNodes;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.io.FileNotFoundException;

public class ReloadCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (!(sender instanceof Player && !sender.isOp()))
		{
			try {
				ChiliShop.ins.reloadConfigs();
			}catch(FileNotFoundException e){
				e.printStackTrace();
			}

			sender.sendMessage(Quick.t(LangNodes.pm_reload_done));
		}

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "reload";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"r"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}
}
