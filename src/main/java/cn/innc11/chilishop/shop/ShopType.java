package cn.innc11.chilishop.shop;

import cn.innc11.chilishop.localization.LangNodes;
import cn.innc11.chilishop.utils.Quick;

public enum ShopType 
{
	BUY,
	SELL;
	
	@Override
	public String toString() 
	{
		return Quick.t(LangNodes.valueOf(this.name().toLowerCase()));
	}
}
