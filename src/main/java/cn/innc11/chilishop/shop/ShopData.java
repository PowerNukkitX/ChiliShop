package cn.innc11.chilishop.shop;

import cn.nukkit.item.Item;

public class ShopData
{
	public String owner;
	public ShopType type;
	public float price;
	public int chestX;
	public int chestY;
	public int chestZ;
	public int signX;
	public int signZ;
	public String world;
	public Item item;
	public boolean serverShop;
	public long shopRandomId;
	
	public ShopData()
	{

	}

	public Item getItem()
	{
		Item temp = item.clone();
		temp.setCount(1);
		return temp;
	}

	public Shop getShop()
	{
		return Shop.getShopByLocation(getShopLocation());
	}
	
	public String getShopLocation()
	{
		return String.format("%d:%d:%d:%s", chestX, chestY, chestZ, world);
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof ShopData)
		{
			ShopData sd = ((ShopData) obj);
			boolean equal = true;
			equal &= serverShop == sd.serverShop;
			equal &= owner.equals(sd.owner);
			equal &= type == sd.type;
			equal &= price == sd.price;
			equal &= chestX == sd.chestX;
			equal &= chestY == sd.chestY;
			equal &= chestZ == sd.chestZ;
			equal &= signX == sd.signX;
			equal &= signZ == sd.signZ;
			equal &= world.equals(sd.world);
			equal &= serverShop == sd.serverShop;
			equal &= item.equalsExact(sd.item);
			equal &= shopRandomId == sd.shopRandomId;

			return equal;
		}else{
			return false;
		}
	}

	@Override
	public String toString()
	{
		return "ShopData{" + "owner='" + owner + '\'' + ", type=" + type + ", price=" + price + ", chestX=" + chestX + ", chestY=" + chestY + ", chestZ=" + chestZ + ", signX=" + signX + ", signZ=" + signZ + ", world='" + world + '\'' + ", item=" + item + ", serverShop=" + serverShop + ", shopRandomId=" + shopRandomId + '}';
	}
}
