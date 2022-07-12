package cn.innc11.chilishop.command;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;


public interface SubCommand
{
	boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs);

	String getSubCommandName();

	String[] getAliases();

	CommandParameter[] getSubParameters();
}
