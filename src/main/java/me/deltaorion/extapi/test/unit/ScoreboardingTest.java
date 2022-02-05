package me.deltaorion.extapi.test.unit;

import me.deltaorion.extapi.common.plugin.BukkitPlugin;
import me.deltaorion.extapi.display.scoreboard.EScoreboard;
import me.deltaorion.extapi.locale.message.Message;
import me.deltaorion.extapi.test.unit.generic.McTest;
import me.deltaorion.extapi.test.unit.generic.MinecraftTest;

import static org.junit.Assert.*;

public class ScoreboardingTest implements MinecraftTest {

    private final BukkitPlugin plugin;

    public ScoreboardingTest(BukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @McTest
    public void testScoreboard() {
        EScoreboard scoreboard = new EScoreboard("gamer",plugin,10);
        assertEquals(10,scoreboard.getLineCount());
        assertEquals("",scoreboard.getTitle().toString());
        assertFalse(scoreboard.isRunning());

        scoreboard.setLine("Gamer",0);
        scoreboard.setLine("Gamer",1,"Hallo");
        scoreboard.setLine(Message.valueOf("Gamer %s"),2,3);
        scoreboard.setLine(Message.valueOf("Hello World"),3,"Gamer");
        try {
            scoreboard.setLine("Gamer",4,"Gamer");
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            scoreboard.setLine("Gamer",1,"Hallo");
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertEquals("Gamer",scoreboard.getLineAt(0).toString());
        assertEquals("Gamer",scoreboard.getLineFromName("Hallo").toString());
        assertEquals("",scoreboard.getLineAt(9).toString());
        assertEquals("Gamer",scoreboard.getLineAt(1).toString());
        assertEquals("Gamer 3",scoreboard.getLineAt(2).toString());
        assertNull(scoreboard.getLineFromName("rrerpok"));
        assertEquals("Hello World",scoreboard.getLineFromName("Gamer").toString());

        scoreboard.setLineByName("Hallo","Hallo");
        scoreboard.setLineByName(Message.valueOf("Gamer"),"Gamer");

        assertEquals("Hallo",scoreboard.getLineFromName("Hallo").toString());
        assertEquals("Gamer",scoreboard.getLineFromName("Gamer").toString());

        scoreboard.setLineByName(Message.valueOf("Gamer %s"),"Hallo");
        scoreboard.setLineByName(Message.valueOf("Gamer %s"),"Gamer");

        scoreboard.setLineArgs(3,"3");
        scoreboard.setLineArgs("Hallo",3);

        assertEquals("Gamer 3",scoreboard.getLineFromName("Hallo").toString());
        assertEquals("Gamer 3",scoreboard.getLineFromName("Gamer").toString());

        try {
            new EScoreboard("epwok",plugin,17);
            fail();
        } catch (IllegalStateException e) {
        }

        try {

        } catch (ArrayIndexOutOfBoundsException e) {
            scoreboard.setLine("ewoifkw",10);
            fail();
        }

        try {
            new EScoreboard("epwok",plugin,-5);
            fail();
        } catch (IllegalStateException e) {
        }

        try {
            new EScoreboard("wepokf",plugin,0);
            new EScoreboard("ewopk",plugin,16);
        } catch (IllegalStateException e) {
            fail();
        }
    }
}