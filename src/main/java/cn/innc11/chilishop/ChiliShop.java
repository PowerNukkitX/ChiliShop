package cn.innc11.chilishop;

import cn.innc11.chilishop.command.PluginCommand;
import cn.innc11.chilishop.config.PluginConfig;
import cn.innc11.chilishop.config.ShopsConfig;
import cn.innc11.chilishop.listener.CreateShopListener;
import cn.innc11.chilishop.listener.FormResponseListener;
import cn.innc11.chilishop.listener.HologramItemListener;
import cn.innc11.chilishop.listener.ItemAndInventoryListener;
import cn.innc11.chilishop.listener.ShopInteractionListener;
import cn.innc11.chilishop.listener.ShopProtectListener;
import cn.innc11.chilishop.localization.ItemNameTranslationConfig;
import cn.innc11.chilishop.localization.LangNodes;
import cn.innc11.chilishop.localization.Localization;
import cn.innc11.chilishop.utils.Quick;
import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.ServerScheduler;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

public class ChiliShop extends PluginBase
{
	public static ChiliShop ins;
	public static ServerScheduler scheduler;
	public static Server server;
	public static Logger logger;
	public static Localization loc;

	public boolean ResidencePluginLoaded = false;
	public boolean GACPluginLoaded = false;
	public boolean LandPluginLoaded = false;

	public ShopsConfig shopsConfig;
	public ItemNameTranslationConfig itemNameTranslationConfig;
	public PluginConfig pluginConfig;
	public Localization localization;

	public CreateShopListener createShopListener;
	public ShopInteractionListener shopInteractionListener;
	public HologramItemListener hologramListener;
	public FormResponseListener formResponseListener;
	public ShopProtectListener shopProtectListener;
	public ItemAndInventoryListener itemAndInventoryListener;

	@Override
	public void onEnable() 
	{
		ins = this;
		scheduler = getServer().getScheduler();
		server = getServer();
		logger  = getLogger();

		try {
			loadConfigs();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			server.getPluginManager().disablePlugin(this);
			return;
		}

		if(getServer().getPluginManager().getPlugin("QuickShop")!=null) 
		{
			getLogger().warning(TextFormat.colorize(Quick.t(LangNodes.pm_cannot_work_with_quickshop)));
			getServer().getPluginManager().disablePlugin(this);
			
			return;
		}

		if(getServer().getPluginManager().getPlugin("QuickShopX")!=null)
		{
			getLogger().warning(TextFormat.colorize(Quick.t(LangNodes.pm_cannot_work_with_quickshopx)));
			getServer().getPluginManager().disablePlugin(this);

			return;
		}
		
		ResidencePluginLoaded = getServer().getPluginManager().getPlugin("Residence")!=null;

		GACPluginLoaded = getServer().getPluginManager().getPlugin("GAC")!=null;

		LandPluginLoaded = getServer().getPluginManager().getPlugin("Land")!=null;

		if(LandPluginLoaded)
		{
			Quick.info(LangNodes.pm_linked_with_land);
		}

		if(ResidencePluginLoaded)
		{
			Quick.info(LangNodes.pm_linked_with_residence);
		}

		if(GACPluginLoaded)
		{
			if(!pluginConfig.workWithGac)
			{
				Quick.error(LangNodes.pm_gac_warning_1);
				server.getPluginManager().disablePlugin(this);
				return;
			}else{
				Quick.warning(LangNodes.pm_gac_warning_2);
			}
		}

		createShopListener = new CreateShopListener();
		shopInteractionListener = new ShopInteractionListener();
		hologramListener = new HologramItemListener(this);
		formResponseListener = new FormResponseListener();
		shopProtectListener = new ShopProtectListener();
		itemAndInventoryListener = new ItemAndInventoryListener();

		registerListeners();

		registerCommands();
	}

	void registerListeners()
	{
		getServer().getPluginManager().registerEvents(shopInteractionListener, this);
		getServer().getPluginManager().registerEvents(createShopListener, this);
		getServer().getPluginManager().registerEvents(hologramListener, this);
		getServer().getPluginManager().registerEvents(formResponseListener, this);
		getServer().getPluginManager().registerEvents(shopProtectListener, this);
		getServer().getPluginManager().registerEvents(itemAndInventoryListener, this);
	}

	void registerCommands()
	{
		getServer().getCommandMap().register("", new PluginCommand());
	}
	
	public void loadConfigs() throws FileNotFoundException
	{
		saveResource("localization/cn.yml", false);
		saveResource("localization/en.yml", false);

		analyse();

		File itemNamesFile = new File(getDataFolder(), "item-translations.yml");
		File shopsDir = new File(getDataFolder(), "shops");

		pluginConfig = new PluginConfig(new File(getDataFolder(), "config.yml"));
		loc = localization = new Localization(new File(getDataFolder(), String.format("localization/%s.yml", pluginConfig.language)));
		shopsConfig = new ShopsConfig(shopsDir);
		itemNameTranslationConfig = new ItemNameTranslationConfig(itemNamesFile);
	}

	public void reloadConfigs() throws FileNotFoundException
	{
		pluginConfig.reload();
		loc = localization = new Localization(new File(getDataFolder(), String.format("localization/%s.yml", pluginConfig.language)));
		shopsConfig.reloadAllShops();
		itemNameTranslationConfig.reload();

		hologramListener.reload();
	}
	
	public static boolean isInteger(String str)
	{
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
	
	public static boolean isPrice(String str)
	{
		Pattern pattern = Pattern.compile("^\\d+(\\.\\d+)?$");
		return pattern.matcher(str).matches();
	}

	public static void analyse()
	{

	}

}
