package net.endarium.api.utils.mojang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class HTTP {

	/**
	 * Récupérer une requête HTTP.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String get(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StringBuilder sb = new StringBuilder();
			while (br.ready())
				sb.append(br.readLine());
			return sb.toString();
		}
	}

	/**
	 * Récupérer une requête JSON.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject getJson(String url) throws IOException, JSONException {
		return new JSONObject(get(url));
	}
}