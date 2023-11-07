package pw.octane.echo.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.md_5.bungee.api.ChatColor;

public class API {

    public API() {

    }

    public String getRequest(String url_string) {
        URL url;
        try {
            url = new URL(url_string);

            HttpURLConnection con;
            try {
                con = (HttpURLConnection) url.openConnection();

                try {
                    con.setRequestMethod("GET");

                    con.connect();

                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer content = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();

                    return content.toString();

                } catch (ProtocolException e) {
                    Bukkit.getLogger().warning(e.getMessage());
                    e.printStackTrace();
                    return "";
                }
            } catch (IOException e) {
                Bukkit.getLogger().warning(e.getMessage());
                e.printStackTrace();
                return "";
            }
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    public ArrayList<String> getAlts(String key, String username, CommandSender sender) {

        String meResponse = getRequest("https://api.echo.ac/query/player?key=" + key + "&player=" + username);


        if(meResponse == ""){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if((Boolean)json_response.get("success")){
                return (JSONArray) ((JSONObject)json_response.get("result")).get("alts");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + (String)json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getPin(String key, CommandSender sender) {
        String meResponse = getRequest("https://api.echo.ac/query/pin?key=" + key );

        if(meResponse == ""){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if((Boolean)json_response.get("success")){
                return (String) ((JSONObject)json_response.get("result")).get("pin");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + (String)json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getLink(String key, CommandSender sender){
        String meResponse = getRequest("https://api.echo.ac/query/pin?key=" + key );

        if(meResponse == ""){
            sender.sendMessage(ChatColor.RED + "Couldn't reach Echo servers (API DOWN?).");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            Object response = parser.parse(meResponse);

            JSONObject json_response = (JSONObject)response;

            if((Boolean)json_response.get("success")){
                return (String) ((JSONObject)json_response.get("result")).get("link");
            } else {
                sender.sendMessage(ChatColor.RED + "Error from Echo servers: " + (String)json_response.get("message") + ".");
                return null;
            }

        } catch (ParseException e) {
            Bukkit.getLogger().warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
