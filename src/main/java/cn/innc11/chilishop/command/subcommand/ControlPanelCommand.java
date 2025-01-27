package cn.innc11.chilishop.command.subcommand;

import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.command.SubCommand;
import cn.innc11.chilishop.form.PluginControlPanel;
import cn.innc11.chilishop.utils.Quick;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import static cn.innc11.chilishop.localization.LangNodes.im_intercept_console;

public class ControlPanelCommand implements SubCommand
{
	@Override
	public boolean onExecute(CommandSender sender, PluginCommand pluginCommand, String masterCommand, String subCommand, String[] subArgs)
	{
		if (sender instanceof Player)
		{
			if(sender.isOp())
			{
				((Player)sender).showFormWindow(new PluginControlPanel());
			}
		} else {
			sender.sendMessage(Quick.t(im_intercept_console));
		}

		return true;
	}

	@Override
	public String getSubCommandName()
	{
		return "controlpanel";
	}

	@Override
	public String[] getAliases()
	{
		return new String[]{"c", "cp"};
	}

	@Override
	public CommandParameter[] getSubParameters()
	{
		return null;
	}
}
