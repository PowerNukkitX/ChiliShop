package cn.innc11.chilishop.listener;

import cn.innc11.chilishop.utils.Pair;

public interface ShopInteractionTimer 
{
	// return null: not found the player
	// return pair: <isVaild, shop>
	public Pair<Boolean, ?> isValidInteraction(String player);
	
}
