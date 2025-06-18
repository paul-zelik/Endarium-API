package net.endarium.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSONUtils {

	private static Gson gson = new GsonBuilder().create();
	private static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	public static Gson getGson() {
		return gson;
	}

	public static Gson getPrettyGson() {
		return prettyGson;
	}
}