package cn.hyperrain.peppershop.variousland;

import cn.hyperrain.peppershop.utils.Quick;
import cn.smallaswater.land.data.LandData;
import cn.smallaswater.players.LandSetting;

public class MappedLand extends VariousLand
{
	public LandData land;

	public MappedLand(LandData land)
	{
		this.land = land;
	}

	@Override
	public boolean hasPermission(String playerName, Permissions perm)
	{
		boolean has = false;

		switch(perm)
		{
			case build:
				boolean placePerm = land.hasPermission(playerName, LandSetting.PLACE);
				boolean breakPerm = land.hasPermission(playerName, LandSetting.BREAK);
				has = placePerm && breakPerm;
				break;

//			case placing:
//				has = land.hasPermission(playerName, LandSetting.PLACE);
//				break;
//
//			case breaking:
//				has = land.hasPermission(playerName, LandSetting.BREAK);
//				break;
		}

		return has;
	}

	@Override
	public String getOwner()
	{
		return land.getMaster();
	}

	@Override
	public String getName()
	{
		return land.getLandName();
	}
}
