package com.dadynent.sa.inventory.util;

import java.util.Map;

public class Utils {
	public static String safeGet(Map map,String prop)
	{
		if (map.containsKey(prop))
		{
			return map.get(prop).toString();
		}
		else
		{
			return "";
		}
	}
}
