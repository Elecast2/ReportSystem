package net.minemora.reportsystem.alt;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minemora.reportsystem.util.ChatUtils;

public class AltManager {
	
	private static AltManager instance;
	
	private Map<UUID, AltPlayer> altPlayers;
	
	private AltManager() {
        this.altPlayers = new HashMap<>();
    }
	
	public AltPlayer getPlayer(Player player) {
        return this.altPlayers.get(player.getUniqueId());
    }
	
	public void setGameProfile(Player player, boolean alt) {
        try {
        	EntityHuman entityHuman = ((CraftPlayer) player).getHandle();
            Field gp = entityHuman.getClass().getSuperclass().getDeclaredField("bH");
            gp.setAccessible(true);
            if (alt) {
                gp.set(entityHuman, getPlayer(player).getNewProfile());
            } else {
                gp.set(entityHuman, getPlayer(player).getOldProfile());
            }
            gp.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
            if (altPlayers.containsKey(player.getUniqueId())) {
                this.altPlayers.remove(player.getUniqueId());
            }
        }
    }
	
	public void restoreGameProfile(Player player) {
		try {
        	EntityHuman entityHuman = ((CraftPlayer) player).getHandle();
            Field gp = entityHuman.getClass().getSuperclass().getDeclaredField("bH");
            gp.setAccessible(true);
			gp.set(entityHuman, getPlayer(player).getOldProfile());
		}
		catch (Exception e) {
            e.printStackTrace();
            if (altPlayers.containsKey(player.getUniqueId())) {
                this.altPlayers.remove(player.getUniqueId());
            }
        }
	}
	
	public void updateAltPlayers(Player player) {
		for(AltPlayer ap : altPlayers.values()) {
			Player app = Bukkit.getPlayer(ap.getUUID());
			if(app == null) {
				continue;
			}
			CraftPlayer craftPlayer = ((CraftPlayer) app);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));
			setGameProfile(app, true);
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(app.getEntityId()));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
            restoreGameProfile(app);
		}
	}
	
	public void altPlayer(Player player) {
		boolean alt = true;
		if (altPlayers.containsKey(player.getUniqueId())) {
    		alt = false;
    	}
		CraftPlayer craftPlayer = ((CraftPlayer) player);
        if(alt) {
        	String name = generateNick();
            for (Player others : Bukkit.getOnlinePlayers()) {
                if (others.getName().equals(name)) {
                    player.sendMessage(ChatUtils.format("&cEse nick se encuentra conectado.")); //TODO LANG
                    return;
                }
            }
            if (name.length() > 16) {
                name = name.substring(0,16);
            }
        	GameProfile profile = new GameProfile(player.getUniqueId(), name);
            Skin skin = getSkin();
            profile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSign()));
        	altPlayers.put(player.getUniqueId(), new AltPlayer(player, name, craftPlayer.getProfile(), profile));
	        player.setPlayerListName(name);
	        player.sendMessage(ChatUtils.format("&aTe has transformado en: &e" + name)); //TODO LANG
        }
        else {
	        player.setPlayerListName(player.getName());
	        player.sendMessage(ChatUtils.format("¡&aHas vuelto a ser tu!")); //TODO LANG
        }
        
        for (Player lp : Bukkit.getOnlinePlayers()) {
        	if (!lp.getUniqueId().equals(player.getUniqueId())) {
        		((CraftPlayer) lp).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));
        	}
        }
        setGameProfile(player, alt);
        for (Player lp : Bukkit.getOnlinePlayers()) {
        	if (!lp.getUniqueId().equals(player.getUniqueId())) {
        		((CraftPlayer) lp).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
        		((CraftPlayer) lp).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                ((CraftPlayer) lp).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(craftPlayer.getHandle()));
        	}
        }
        restoreGameProfile(player);
        
        if(!alt) {
        	altPlayers.remove(player.getUniqueId());
        }
	}
	
	private String[] nickParts1 = new String[] {"_", "The", "King", "Noob", "i", "l", "Master", "Xx_", "Max", "El", "La", "Tu", "My",
			"Super", "Un", "Mine", "Craft", "_X_", "Ultra", "Mega", "Hard", "War", "Ez", "In", "Kill", "Moon", "Bad", "x", "Let", "Kuro",
			"Queen", "Gamer", "Mini", "Max", "", "Cube", "im", "Im", "Your", "ez", "Our", "Holy", "Baby", "Sky", "Ender", "Uni", "He",
			"She", "Anti", "Life", "Live", "", "i", "l", "_", "San", "Miss", "my_", "The_", "", "im", "", "Mister"};
	private String[] nickParts2 = new String[] {"_", "King", "Noob", "Master", "Pollo", "Bad", "Legit", "Pro", "Luis", "Jesus", "Dios", 
			"Dani", "Diego", "Jose", "Mari", "Luci", "Rekt", "Pepe", "Fer", "Tony", "Rodri", "RTX", "Crazy", "Craft", "Sable", "Focus",
			"Bad", "Good", "Sara", "Mars", "Mora", "Beep", "Sheep", "Neko", "Cat", "Rush", "Ken", "Marti", "Sofi", "Edgar", "Gamer",
			"Cube", "Josue", "Alex", "Ani", "Free", "Duty", "Bunny", "Daddy", "Mike", "Dano", "Pony", "Troll", "Drag", "Ball", "Joker",
			"Goku", "Prince", "Bill", "Fake", "Seba", "Sebas", "Paty", "Carla", "Carl", "Mandy", "Billy", "Jimmy", "Light", "Leo", "Leon",
			"Alfredo", "Minion", "Killer", "Dragon", "Andres", "Gaymer", "Rubius", "Boom", "Shake", "Bored", "_IzI_", "Monster", "Emma",
			"Manu","Manuel","Lord","Man","Trueno","Storm","Paty","David","Dark","Combo","Rafa"};
	private String[] nickParts3 = new String[] {"_", "777", "King", "Noob", "7u7", "Master", "_xX", "PvP", "Pro", "YT", "_YT", "TV", "Love",
			"You", "Sony", "Fruit", "Dance", "Loco", "Crazy", "Grey", "Super", "Craft", "_X_", "UwU", "OwO", "Ez", "Fork", "Kill", "Laser",
			"Bad", "Good", "Layer", "Music", "Step", "Letal", "Capo", "Chido", "Yeah", "Rush", "Pixel", "OMG", "7w7", "", "Style",
			"Cube", "SG", "007", "San", "Chan", "Sama", "MC", "MM", "Tron", "rt", "nt", "mino", "tico", "tica", "Lol", "Lord", "Bunny",
			"19", "21", "15", "Troll", "Ruso", "Rusa", "Sad", "Happy", "h", "Leak", "ter", "Con", "Zeta", "Ball", "Gate", "Izi", "Freak",
			"Fail", "Fake", "Cute", "", "Focus", "Black", "Jack", "Dios", "ed", "sh", "k", "GT", "KFC", "22", "Killer", "_PvP", "Dream","Bro"};
	
	private String generateNick() {
		Random ran = new Random();
		return nickParts1[ran.nextInt(nickParts1.length)] + nickParts2[ran.nextInt(nickParts2.length)] + nickParts3[ran.nextInt(nickParts3.length)];
	}
	
	private Skin[] skins = new Skin[] {
			new Skin("eyJ0aW1lc3RhbXAiOjE1NTAwNzQ4NTE0ODUsInByb2ZpbGVJZCI6ImJmM2FjNDgyOGFhNTRiNWQ5YzNmNGU3NmUyZjYyMTYxIiwicHJvZmlsZU5hbWUiOiJBYnVscGgiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E3MmZiMWFjODNkYTQ2NWY3M2FjOTYzMjY5ODliNGY0OTk1Yzc2YzI5YTI5ZTdjNDM1ZDc3YjQ4YTdhZDM4NGYifX19","tC7DrgLh2LCln9tfEk3xBPY4imZkpQ69ceyEzmF28+lPd1oxmckPxjJnRK4+EVmU+JLi7Cj5GRZSVs5E3x3gb+gXxarbtFfLl+dGJvlykodNpWvG7AdvWHy6PFg7Rp+foSRK0gsDiibJofdl6tAGd1BkLgt1OkVlqhOBHDOhWjEK0W6m7mUg/J58RtbUhKPIo+srK2P/9pYK3jW0alNVffS58T7oFdtKCjCSMzqAisBFAuvJsBki4RIH7/v5daJ28/DaxKo32op31wjqNQ/9HtJgZbZ+rZGHSesKoS+MaaucynMbg6wSWsefQag2qkZW9WfbgFxYoAHAwZaEzOXqElbOGMNnKbZCD6cK93DDQjxZ5Q8gRu1LaDFslWpBJg3yN9kmEFaC78rTCK//Ces3aNKQcrLwcLul52g/Vg+c6+7N1QNx289CL7UaeBZjtNCJiDcy+VN7QZCB//N44xNW7Yf3qtny9EeuFOdiTCxSO2DcoOJH6ATZmdlEyKy1n4F/u4B5tkrgYMFxcezbIq85adqwRwdqPw1p1AOvFW/UPe7CFaBbMGmVm4W7sb5vNNZpV8eR+3OAwAqKei0ZuyVZkNCMmTVKuNBoPxdhKolicuxBzK722d6BBoJ/IJLEYwStfNKORsi+UabXPWVAl+YPXYWqi9sNNgNOwyuJD5FW4ig="),
			new Skin("eyJ0aW1lc3RhbXAiOjE1MTI1NTQ0Mjk2ODIsInByb2ZpbGVJZCI6ImIxMzdiNWZhZTQyZTQwODg4NWYyODk1YTI5YzRhOTNlIiwicHJvZmlsZU5hbWUiOiJBcG9jYWx5cHRvMTMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I0ZTAyZTYwYTZkYzc4NWI4OWMwZGUzOTc3OTQ0YWNhMzVkZWZkMzVkMmQ3ZTVhMzVlYjEzOTlkNzdmNjU2ZSJ9fX0=","XessmATP1WEZIpCaSOpiBdiJu9UeiMF7xhboxNZVSbxJbeXrDSw0STFWyPP+CiVwNuLhm7FFIQu+4Fa0xVjvcqv7yaOxwDkXMUFpZSePZqxzZsQIQAGx7bw8e//gOvD5n3BIAcnlsiXj7VvmLaejQWP+C6NmIVDo71iCmrPszFKfHLONFk9x8piqPJIVS4y5y+8Op4p1tua7LMuoLV50e6w+QvRBFDSgFi5f3ag90za8l4HdRQoOFpZvn3o6K7au3IB7KMOPrP+VT9Z/aaCN2WY8JKsYVxfxp2uFb/iurvw0HEm2zH8FkQTvJr0u62utK/NdO9htzSRSHOrHQn11zUi+WtyqDsMq5OdjkV40+I99pGjbwq7ksRGDi9tREXYnNAFTTolL1BixQQxuAusr9GluJ6Ifb1VKHYNEQg+COwJA8ZDeem3GN3E6K5Ax8Vog/Nfy03JmztmWJU+RT0eSmn0ctiWY6jnH/2Dc50dpOnRzgeN7xlZu0F9rak3OTbtfh7ziW7gRrf0Kx378P7nZ0uEkNFGD8PAG9ZYPs3o668mgGv+pD04GcdJZqFr8Sk+oP9vfOoSLV3oobFH2SzFx26TCrLvHOaFAkE51IhWwoUsYu8j3lzRysom/xs8H7NKRZrMUf3rOzn4bBaD+g9jhku99UfGig5OPq6XBaroXUKQ="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTk1NTI4NDk0MDQsInByb2ZpbGVJZCI6IjhkZWIwYTM3ZTg4MDQ0NzBiNmMxNDU1ODVkNjMxZmUxIiwicHJvZmlsZU5hbWUiOiJFc21vcmFsbCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjQ4M2EyNGY1OTJiZDM3NDBlZTgyNWZhM2FmNTI4ZDUyOGE0ZDg2OWEzYmNkOTNkYzdjYmI4NWZlNGEwYTIiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ==","ELzPD5OSaS9se2VHrfCMi9OP2St2y5tK/P1DaQ1SYQAP/tjtWB5GrKrQlpt9534JtK5/bFmxq6o0nvWTmePtlP1eh0swq0CR5ZUYqfZ591wli1MS1RZH1ThOS5/P411J+frB/rGjP0AjQuNYPx9phnHuHF9e3DeE/nJ44kq8Fhkfl8R0yck41Z4gF1Ok9G/pi5n47T/eGw/bwTUugvcuZQnladPR80tZJa83kIc6VENPNzpDdT62eZeGQNZpCGDc6Blaz9gIOFylvjC0BwuDzmjRaKbr2MFuvMXi+LRnTSXJmrDQAKCEPFNSw3xlDzgpe7bTRAYwUVhGJRYf1bmn9jXz3BA+2sTidWg2waH4XLITaTvlKoxSxJk9eLI8M97xPZy/DoSqRfLDI13AaAuW4JStPIlHPPWeCB11vjfkCuWRfH7b6rxvlgzZQwbRPlXV3gIcyZ2y8JJEkR3lKmY0eCypzJ8gvRQ4EDHIwOT8AdWtzcLzIvbyQ2qFf9sacaPhBQKWb/RZq596pVJQiPKvA9xkbdN12R2EZOT6re25GTrc85csoyXsADKD37Dm/E2jQb20p6qe3fHB6mHkIhk/Cb+E+m7jn8nEUOZwXIccz78quXUE0jgAmaWe/bUbbBr0L8asW9wmrAffFKxkvo1/lSjLrVgmEkFBp5dBYqaeiu0="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTk1NTI4NDk1MDEsInByb2ZpbGVJZCI6ImI3ODQ3YWFlZTlhODQ1YmI4ZmQwZGRhYjEyOWQ0NDg3IiwicHJvZmlsZU5hbWUiOiJaYXp1R2FtZXIiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNhMjVmMGQ5ZjY2ODMzODIxOGFkYzgwNmQ2ZGQ1MzIzYTliYjVkMDk4MWJhODU4ZjBhMjUyMzg0ODg3ZTZhNiJ9fX0=","XHIO3k0ag69kPpOLY81vCzcCDRwyFOzG9p4NvhdEK+UFLBFJg7wwdC6w5AHYPc5nEWx4eLX/NlUDf0nbzhbUPaaxro45Up+d0Gw7J+TJypvD9SkggRUTOMD15zEfBObbycXD96kmjCm8p+dXhuaiBhY7ikWA4GyDkOzuN2o/CVDw6yjK+7ZbvKuEp32s9evF7KGh8i9fufNynu5s9Z1VSxSqeCj6xPGruCdFkbFylD2jCXaxjwUvW7/uRc2erUCbjkYSY1uPxIcp83g0hTy1QZBFndbpq7gWbheZbGTKu5wFWWgzMCEUMZTVIaUpq1q/2nyGl8w7KRvebZJaNe5MNQaVbVc/qXSdU6ZIEqxhFvIXlnqvBZsyMU1G4eEqiyqhTvT2AN16QU2I7sXUCI51cSa/70NVPA4bb2qT/TVs2iENDOXGCFpF3hpRAWYTgS2AYfI/0sgwBlDFByaJurhFw6DVcoKFlJG3HYU6gL/JAZd7tA10pYVGyvSI3hOToUxo6KBQsTouIi4BXh8fTHdEOE8aJga5kQiUERNw3iJJN2W9BentjKw0tvVkZm69mn/x4iFZ5BrZpIke1uBflcyLTwyBfTWJgdfH8/O27ALzEvErU3dgrE5BczsDQPrrnH2o1hiRlhCyXVcbknhWYkxGYnygpMrT2DNACJ8ut4CPL10="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTk1NTI4NDk2MzMsInByb2ZpbGVJZCI6ImE0MjI1NjM0ZjdhNzRjYmVhNDMzNWI1YjhjMDlhNWUyIiwicHJvZmlsZU5hbWUiOiJtYXhtYXI2MjgiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IzNThhOTQwYzVhZjhmNmRmNmQzODM3OGNlZTcxMmRkMWE2OGI3YTgzOTdiMWQ0ZjhmZDdiODc3ODIwY2E1MyJ9fX0=","st7eL24XmRzquS8xABsELM4zXNiwBtii3vMlgOruFoy3lboNbLIWxlsS9lTaQVM1CtVDFtBjGvjQOmLC3sD1rCaxQIvUBdjx2c0kT3Ye+miBZp84R5pUjAE2DSBEMHBh3zuhbCYAuPkb0LW3wNGsz8s9PL/ouGvpwYKOjrVIJmHkKKh8JY4Ex5tEHUc5rzXfEMEvrNTjUKcaKbEv/8meZcB0JpOpkO/O/NhpvvS0h79ZMYkvqvmE/T/1elyJkiU9fmlr/BdK9QQyQ6I6/mM/yxZ5BQ64uvhTeTGE7uLWTaYkz6Ea2ntxSQB1aDWAD37PBCYj37yw2ENbf/DFsv7lP7CSI1Y3Xd9kFp5YwxTQKMnJUL7kiij8/p7AYu/S0XA2kZ5jYtpPRMa2GPMOL/Wy7yM/YI9L/K5yRSfWn6zagFhr8ud3eF13YP9Qybo5/jAqULi/lsUeWUDffcl2Hmy8ZRsa1xXP8O7U+OQcXGuTRSlixobZXxFMtNSm4bsy9qRhMPsu2oBwzMmUqsGRGfYxhROnODclGR75n2SDLddiWvhiIU+zl50vHye4mv0OBUTuc+CxYenNj1fNOk+5qjarxzjKXIoH3yS9EPCCt8E4IrcjTCrysGNmS/Rm09fLDiSvnKTJ4F9T2qXxw9ny2HOpSnwnT6V+7wJW2BXVcXLVLfk="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MjE5MjU0MjE0NjAsInByb2ZpbGVJZCI6IjAxNTBkNGQ5NjI4MDQ1MjdhM2EyZTY4M2M0N2U3YTMxIiwicHJvZmlsZU5hbWUiOiJQZWRyb0pNOTYiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI1MGJkNzIzZmUyM2RlY2E3NTJhMzRkMTYzNjM5ZWIzODY0NDljYmIxN2IyMTVmMWIxM2Q4ZmUzZWNjMmMifX19", "SUkxXG9gJFZfPYyrHcsqSezehWGf0Au0g1KH4TswZB5U3QYLpbmm3GiRNPOKt/yGqnGGu7685FNUXvVTn19Y+s4yLMAshwyK6TMBj3FLI6WOYZOuew4vGOzyg0+nVnk5T0ylM5y6KaBRFW9+ZpapMaU3BUj4ItN/bk+Z0J/U/AA6SH1xiLJ223OFeR3fDD1cR3PVkK+OQhp1Po+FNwNSbohRqSFsEgG9WwN63CJFX4fVwRagoQydAeOzIiHKFZLuxBSj72XgswtacwY7M+OWcR9iCuiD3r96UicxhSWvYp55pWyl417EW1uen5nQVydXE163X9Wb/P4Gu/AISKBJs05jZJ1QYqcYvuK1oacGi+dDkCBwqbwZdtidI/xo8zJkZPH/9wbjYATDMXomorAo7i4abKZJl015lZE+OlPVXUSeXt3MdAg24CgN8aag7Y/oJyKi4HuHoGBGMnU/BxoZde0l1ZiXJfXC3RAHe8SC6FxFfjc9/WcB6xV+zNKQy0S1gdQjmLSh4GmxBjm6/pd4iktvvAd+R5QJuukMcUC2bKn3+tmJjEO8kKLDCYkMiT6u+vPyM5TccvpZZTgG7FkIxZCR3C5+zaHCMqVCsjIBQAuptXRx6ctyB9mTASjv0SL7ZwgoeqV0K4izEuXaiVeHhLeWx0yV2AVtQs6Y7BRQhJI="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTk1NTI4NDkyMzgsInByb2ZpbGVJZCI6Ijg0MDZkMTMzOGYzZDQ4OTA4MTFjZDg5ZTI2MTNlOTM2IiwicHJvZmlsZU5hbWUiOiJDYXN0ZWxsYW5vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xOWU0ZjU1OGVhMjBlYzczNTU0ZjQ0MzMxMjc2ZmQ5YmVhZDM3NmI1ZjFhNzI4MzVlMDY2ZDQ5ZDE4OTE4M2M1IiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=","OiAAuPAXaNNxWKGITPxM0sJq42LiIsFdD1HYrhk4FPIkSZALNG8VANjHuRf0F1s/B5bZvK0f3g5BAgpIWCuNp61BNhthU55DKAlXIsMoBnZF3uprcmSbjdHKC9IYWHnBN3nVc/9xL8Hn68ZmMfpUNnWHeAq/SNy+YogYw+Djz1j0kHi2LXa3y75hoIuNUsmi39J4fYYUlURWe95SBv4hYbO3V7cwALL9uGfWoNITMR1UEJBp08+SpsxznSiseQBdvtDNkzu17axJXbIJpSiuvWSJPCTnEEdWvQTa0KSYU3msczAIVBcUtej2cCN+Vg8vfNo0B0k+NrXqlY0jUyvIumyItezxhpSkcUFvsTJX1efcNVUMgtkfIHwLDelFwDQKBAS95Mu7oelJWdj0vTUflwDDKTAydT2/Gluw+DRZq54y5rOcrSwPCyOKS7ygEF0Fo5fiZR4OzsJgxZpW5iUh1Yfbv5mAgECf+UtQUP80mYE9KZ6t9SYD7z8IvG5lYfR4abVM0ZCtEAUK6npoNoAXxYLbWprtpnDTDY78IxFArnLrvsCOWe99usWwfw0HH27GgwY3aVa+3EdAy18y3GDdkX8hC2qP6RubM/MThD+j24hJ7Zc+fRtf3AtZIPvQOz6sea9E5asknWeaQcy1+nO8HBCxq2yBMYMxQNbWVbtCHMI="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE0NzkzMDk3NDAzMTIsInByb2ZpbGVJZCI6ImY0N2Y0ZTMwOWRmNzRjYzI4OWJhZDkwNTdhNTdkYTE0IiwicHJvZmlsZU5hbWUiOiJfR2VkZTd1N18iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk0MmRmN2U1MGNlYTFmZDVjMTIyMTY4YWExZGQ0NWM2Y2JjMzBkNDk1NDM0MTQzYWEyMmNkZWM4MTI3ZGVhYSJ9fX0=","mZyh48bGYi6OrHzDV4TXL3L5R9BNGwwSd19y6OWf8quUgq9fiPP/rYxN8EqRXA0XlPfLGPOIAYptexNgWTrYRfYtNJ6u0UL75StLoLs1OZJeuByHcPh2CmAvXcNw+Mfr0SbQ43r8ITB7s6bNO+lENOYIct3Uy4Pfw80uHzxdxeU7q68dS0nXKOyvcqG6yAPX5vxPpg7Lqg2fzTUBpmBfB5u/9oNr2FXTY9AyJRqszrOPT9ogCmTbBKPAburMuu7mzNslHaVINUPy6gN5qU5tUduSSOYTtOIc9g+L7rtXYl07ajPWGEE17M+VPfY+Xb71suYLR/7fHMaeZPj6Coqi2oHGIKAdRLaiDFqIz2TOCaX5sPCSM1JpliFuyOLrD04rbPE0/cItR76hMe1DkZvCn+T2QAUqmUAQ5aBR4A2D4tHBD4GcpOIzVEXQaZHxUhuJC/+NkfwHlZkRWxrwd2oiJ7WZsnOv57X6fF73VLI1MMI074vqvti1gyPpEDMPZtR5EeyjQYmMMOkwUl2JnSncLk52aFDxrRe9/hYXNIMczF+08rD5kV8Ex8aJ21eTIk3iwVSyOdocmAmobnR59JNEvn2BpvKEDYt9cAmRdsu7GwsVKGkJmsrLI0pk7G8jD5dwB+Rlcn854WoIvwLzsGk8uXeyHbfL4w27MU4y0WCTW4E="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTk1NTI4NDkyODEsInByb2ZpbGVJZCI6ImQzYTJlZGQ2Zjk1MjQ5ZTg5NmM3YWQyZmY4MjIzNDcwIiwicHJvZmlsZU5hbWUiOiJfTUtfMTYiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlhOWRiYWFiNGQ3M2E0Y2E4NDIxNGM5ODViYmE2YjY5ZmQzNDM4Yjk2NThkN2QxZjNjOTAxYjI1OTQ1Njc5In19fQ==","c5tHEVBl/+IluQl7wE32WxzdxkKMGCxeik6W2xEf1/SSARZjCqRte4HIHTnb6uj4Vv07NNFmo5RG5Pg8fkxe4GhSdSVrt8Ij0ZlSr+DYmSg1ToroCmNo7hTJGNK3iCBo/fFKytV/iTfOukSEaDekBHo+a3Mb1Fn0/QscpKvfE63Shdt/eSBuiz9qtQUsOdoYRJXrvcl7XG9C/tvzacR8x0y7XNt65Zp6g/uROkmSXRdYxOY7Z4Yl2NKgMCErPPQ/280zlI/SOJWBM67amyiOEgVBQnNWvkMlWtc+NK16bGo6tW67MGMCFdFNTAPLME0PGyrgA5NMbqD/t6WywEDyIUaUdwTDQbsFTuzIMDh/fRHeDouD3lxT692F+Hcb/KhXLhKsNDMRu/S6uRGboNeS7iphXF1gt5c2HJqI++UgtEUwpMtD9ZY8K8xtzmcJpaJkhdZOlAxPDEjopSR8QdJJEfn12jNwReSHddJTSNpXBFVXCwrHML0lpFp/y+TrZsFmkuCCs2KxvPbZSEIFM1KYUIsSr7LoUscU2Cwe9N8cJsmmPM7BdOSaH9iezzAJFnFzNDu8CTKGc9i/uKlsCcSYH+jgt/7f2UJIXqk2EwYegxasyzXqlWAgwJWX8d0CDYxYw6pr3+cMMhQYmDKGfH/lQ4iCxRFQS5Rmt11f4BUyX68="),
		    new Skin("eyJ0aW1lc3RhbXAiOjE1MTg0NTQ0MDUwNTQsInByb2ZpbGVJZCI6IjRiNDQ3MWZjZjI3MjQ2ZGI4YjBjMGNjNmVhNTI0Y2ViIiwicHJvZmlsZU5hbWUiOiJsWmV1c2UiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2E3OTI5N2NjMDJkYTA2ZjU4OGQxOTI0OTMzZmE2NjU4YTAyZjg5OGJkMTk3N2U0ODRjOTE4YzFjYzA2MTYifX19","CZnTnTJmgwXVIga8wzCaKCNGZ5qDXaq893/5hKn7BBvbZ0a/19dt6mg22nBldyw0K4xK7Xh5tEDsYWQtVi3XUjbY4Pto8GMVPxbazrxdRv+3QofsgKfV1knJHsGj1GpraRmFLuYETjaAmJwgVrIi/3AGnLe7fu35amLucDA55wJWK2l/lPXdxkx0nfkhKBEBqkjHMWGMemr4X4gFAq9qlpK7VmgipUXEGEiSpA5qtdOjIimvgn/pqIwNab/fjGvZPQ3kIZFdi8wsADL73ssiitoQo/EkzcfqwPBjvkDj2eOv8oJPxP9PEpWenUIguDW1BbFXl3Ke2QFD+Y3RQEd5DF7rYXEvHZ46pKgsOnnIj8ASPy+sHTQOWhPV3Uxy3iSv2SQRnXv7GTiX2qrWY8Kf+bLdqoUPwbn/mTZJqCeUuNbygt8vNk2wpNvX038fsc5DZyZ/cq8ctaG4XC0YLTDgyAwnNrMZBtiw9+5xGTZzwKJJoik4kBgJWv1buytHWKmZa4/dsrWZBj9zzvPTrGargDjC68jWil2Iu1rUWknMTNQVzo9yAxq3iXkWAkBAeIAr0moZivp11RBe8zI2BmmYzoGY4+jaBq243GfqrR+VvnNFM9VzwTbWhUYl/wbygZUHdYlz9bD8ZdeUsFBRZ4MO57QUFcVFzVeCmzseKp/MrFo=")
	};
	
	private Skin getSkin() {
		Random ran = new Random();
		return skins[ran.nextInt(skins.length)];
	}

	public static AltManager getInstance() {
		if(instance == null) {
			instance = new AltManager();
		}
		return instance;
	}
	
	public Map<UUID, AltPlayer> getAltPlayers() {
		return altPlayers;
	}

}
