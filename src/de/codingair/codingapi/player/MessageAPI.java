package de.codingair.codingapi.player;

import de.codingair.codingapi.server.Version;
import de.codingair.codingapi.server.reflections.IReflection;
import de.codingair.codingapi.server.reflections.PacketUtils;
import de.codingair.codingapi.time.Countdown;
import de.codingair.codingapi.time.CountdownListener;
import de.codingair.codingapi.time.TimeFetcher;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class MessageAPI {
    private static HashMap<String, Countdown> countdowns = new HashMap<>();

    public static void sendActionBar(Player p, String message) {
        if(message == null) message = "";

        Object com = PacketUtils.getChatMessage(message);

        Class<?> packet = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutChat");

        Object bar;

        if(Version.getVersion().isBiggerThan(Version.v1_11)) {
            Class<?> type = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "ChatMessageType");
            IReflection.MethodAccessor a = IReflection.getMethod(type, "a", type, new Class[] {byte.class});
            IReflection.ConstructorAccessor constructor = IReflection.getConstructor(packet, PacketUtils.IChatBaseComponentClass, type);

            bar = constructor.newInstance(com, a.invoke(null, (byte) 2));
        } else {
            IReflection.ConstructorAccessor constructor = IReflection.getConstructor(packet, PacketUtils.IChatBaseComponentClass, Byte.class);
            bar = constructor.newInstance(com, (byte) 2);
        }

        PacketUtils.sendPacket(p, bar);
    }

    public static void stopSendingActionBar(Player p) {
        Countdown countdown = countdowns.get(p.getName());
        if(countdown != null) {
            countdown.end();
        }

        sendActionBar(p, null);
    }

    public static void sendActionBar(Player p, String message, Plugin plugin, int seconds) {
        Countdown countdown = countdowns.get(p.getName());
        if(countdown != null) {
            countdown.end();
        }

        if(seconds <= 0) {
            sendActionBar(p, "");
            return;
        }

        countdown = new Countdown(plugin, TimeFetcher.Time.SECONDS, seconds);
        countdown.addListener(new CountdownListener() {
            @Override
            protected void CountdownStartEvent() {
                sendActionBar(p, message);
            }

            @Override
            protected void CountdownEndEvent() {
                countdowns.remove(p.getName());
            }

            @Override
            protected void CountdownCancelEvent() {
            }

            @Override
            protected void onTick(int timeLeft) {
                sendActionBar(p, message);
            }
        });

        countdown.start();

        countdowns.put(p.getName(), countdown);
    }


    public static void sendTitle(Player p, String msg1, String msg2, int fadeIn, int stay, int fadeOut) {
        sendTitle(p, msg1, msg2, fadeIn, stay, fadeOut, false);
    }

    public static void sendTitle(Player p, String msg1, String msg2, int fadeIn, int stay, int fadeOut, boolean ignoreTimePacket) {
        sendTitle(p, msg1, msg2, fadeIn, stay, fadeOut, ignoreTimePacket, false, false);
    }

    public static void sendTitle(Player p, String msg1, String msg2, int fadeIn, int stay, int fadeOut, boolean ignoreTimePacket, boolean reset, boolean clear) {
        Class<?> packet = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutTitle");
        Class<?> enumTitle = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutTitle$EnumTitleAction");
        IReflection.ConstructorAccessor constructor = IReflection.getConstructor(packet, enumTitle, PacketUtils.ChatMessageClass, Integer.class, Integer.class, Integer.class);

        IReflection.MethodAccessor getNames = IReflection.getMethod(enumTitle, "a", String[].class, null);
        IReflection.MethodAccessor getEnum = IReflection.getMethod(enumTitle, "a", enumTitle, new Class[] {String.class});

        String[] names = (String[]) getNames.invoke(enumTitle);

        Object resetP = constructor.newInstance(getEnum.invoke(enumTitle, names[4]), PacketUtils.getChatMessage("DUMMY"), fadeIn, stay, fadeOut);
        Object clearP = constructor.newInstance(getEnum.invoke(enumTitle, names[3]), PacketUtils.getChatMessage("DUMMY"), fadeIn, stay, fadeOut);
        Object times = constructor.newInstance(getEnum.invoke(enumTitle, names[2]), PacketUtils.getChatMessage("DUMMY"), fadeIn, stay, fadeOut);
        Object subTitle = msg2 == null ? null : constructor.newInstance(getEnum.invoke(enumTitle, names[1]), PacketUtils.getChatMessage(msg2), fadeIn, stay, fadeOut);
        Object title = msg1 == null ? null : constructor.newInstance(getEnum.invoke(enumTitle, names[0]), PacketUtils.getChatMessage(msg1), fadeIn, stay, fadeOut);

        if(reset) PacketUtils.sendPacket(p, resetP);
        if(clear) PacketUtils.sendPacket(p, clearP);
        if(!ignoreTimePacket) PacketUtils.sendPacket(p, times);
        if(msg2 != null) PacketUtils.sendPacket(p, subTitle);
        if(msg1 != null) PacketUtils.sendPacket(p, title);
    }

    public static void sendTablist(Player p, String header, String footer) {
        if(header == null) header = "";
        if(footer == null) footer = "";

        Class<?> packetClass = IReflection.getClass(IReflection.ServerPacket.MINECRAFT_PACKAGE, "PacketPlayOutPlayerListHeaderFooter");
        IReflection.ConstructorAccessor constructor = IReflection.getConstructor(packetClass, PacketUtils.ChatMessageClass);
        IReflection.FieldAccessor b = IReflection.getField(packetClass, "b");

        Object tabHeader = PacketUtils.getChatMessage(header);
        Object tabFooter = PacketUtils.getChatMessage(footer);

        Object packet = constructor.newInstance(tabHeader);

        b.set(packet, tabFooter);

        PacketUtils.sendPacket(p, packet);
    }

}
