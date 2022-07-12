package cn.innc11.chilishop.command.subcommand;

import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.command.SubCommand;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

public class HelpCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		pluginCommand.sendHelp(sender);

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "help";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"h"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}

}
