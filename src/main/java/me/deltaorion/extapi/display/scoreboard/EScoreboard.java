package me.deltaorion.extapi.display.scoreboard;

import com.google.common.base.Preconditions;
import me.deltaorion.extapi.bukkit.BukkitApiPlayer;
import me.deltaorion.extapi.common.plugin.BukkitPlugin;
import me.deltaorion.extapi.locale.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @author RienBijl, DeltaOrion
 * @website https://github.com/RienBijl
 *
 * Some work on this class was taken from
 * https://github.com/RienBijl/Scoreboard-revision/blob/53c30cfd740eda28a80710dc9fd69f2d0955337f/src/main/java/rien/bijl/Scoreboard/r/Board/Implementations/Drivers/V1/ScoreboardDriverV1.java#L124
 */

public class EScoreboard {

    @NotNull private Message title;
    @NotNull private final Map<Integer,ScoreboardLine> scoreboardLines;
    @NotNull private final BukkitPlugin plugin;
    @NotNull private final String name;
    private final int lines;

    @Nullable private BukkitApiPlayer player;
    @Nullable private Objective objective;
    @Nullable private Scoreboard scoreboard;

    private static final int LINE_LIMIT = 16; //the amount of lines that can be displayed
    private final int CHARACTER_LIMIT; //the amount of characters per line is double this

    public EScoreboard(@NotNull String name, @NotNull BukkitPlugin plugin) {
        this(name,plugin,LINE_LIMIT);
    }

    public EScoreboard(@NotNull String name, @NotNull BukkitPlugin plugin, int lines) {
        Preconditions.checkState(lines >=0 && lines<=LINE_LIMIT,"A scoreboard can only have '"+LINE_LIMIT+"' lines");
        this.lines = lines;
        this.plugin = plugin;
        this.scoreboardLines = new HashMap<>();
        this.title = Message.valueOf("");
        CHARACTER_LIMIT = getCharacterLimit(plugin);
        this.name = name;
    }

    private int getCharacterLimit(BukkitPlugin plugin) {
        if(plugin.getEServer().getServerVersion().getMajor()>=13) {
            return 64;
        } else {
            return 16;
        }
    }

    public void setLine(@NotNull Message content, int line, Object... args) {
        setLine(content,line,null,args);
    }
    
    public void setLine(@NotNull String content, int line) {
        setLine(Message.valueOf(content),line);
    }

    public void setLine(@NotNull String content, int line, @Nullable String lineName) {
        setLine(Message.valueOf(content),line,lineName);
    }

    public void setLineByName(@NotNull String content, @NotNull String lineName) {
        setLineByName(Message.valueOf(content),lineName);
    }

    public void setLineByName(@NotNull Message content, @NotNull String lineName, Object... args) {
        ScoreboardLine line = getLineByName(lineName);
        if(line!=null)
            setLine(content,line.getLine(),lineName,args);
    }

    public void setLine(@NotNull Message content, int line, @Nullable String lineName, Object... args) {
        lineValid(line);
        if(lineName!=null) {
            ScoreboardLine l = getLineByName(lineName);
            if(l!=null) {
                if(l.getLine()!=line)
                    throw new IllegalArgumentException("Two lines cannot have the same line name. The line name '"+lineName+"' already exists!");
            }
        }
        ScoreboardLine l = new ScoreboardLine(Objects.requireNonNull(content),lineName,line);
        scoreboardLines.put(line,l);
        //if should update
        if(this.player==null) {
            content.setDefaults(args);
        } else {
            updateLine(l,args);
        }
    }

    public void setLineArgs(int line, Object... args) {
        lineValid(line);
        if(this.scoreboardLines.containsKey(line)) {
            ScoreboardLine sLine = this.scoreboardLines.get(line);
            if(this.player==null) {
                sLine.getMessage().setDefaults(line);
            } else {
                updateLine(sLine,args);
            }
        }
    }

    public void setLineArgs(@NotNull String lineName, Object... args) {
        ScoreboardLine sLine = getLineByName(lineName);
        if(sLine==null)
            return;

        if(this.player==null) {
            sLine.getMessage().setDefaults(args);
        } else {
            updateLine(sLine, args);
        }
    }

    public void setTitle(@NotNull Message title, Object... args) {
        this.title = title;
        if(this.objective==null) {
            title.setDefaults(args);
        } else {
            Objects.requireNonNull(player,"Bad Initialisation");
            this.objective.setDisplayName(title.toString(player.getLocale(),args));
        }
    }

    public void setTitle(@NotNull String title) {
        this.setTitle(Message.valueOf(title));
    }

    private void updateLine(ScoreboardLine line, Object... args) {
        Objects.requireNonNull(player);
        updateLine(line,line.getMessage().toString(player.getLocale(),args));
    }

    private void updateLine(ScoreboardLine line, String content) {
        Objects.requireNonNull(scoreboard,"This should not be called unless the scoreboard exists!");
        Team team = scoreboard.getTeam(line.getTeamName());
        String[] split = split(content);

        assert team != null;

        team.setPrefix(split[0]);
        team.setSuffix(split[1]);
    }


    private String[] split(String line) {
        if (line.length() < CHARACTER_LIMIT) {
            return new String[]{line, ""};
        }

        String prefix = line.substring(0, CHARACTER_LIMIT);
        String suffix = line.substring(CHARACTER_LIMIT);

        if (prefix.endsWith("§")) { // Check if we accidentally cut off a color
            prefix = removeLastCharacter(prefix);
            suffix = "§" + suffix;
        } else if(prefix.contains("§")) { // Are there any colors we need to continue?
            suffix = ChatColor.getLastColors(prefix) + suffix;
        } else { // Just make sure the team color doesn't mess up anything
            suffix = "§f" + suffix;
        }

        if (suffix.length() > CHARACTER_LIMIT) {
            suffix = suffix.substring(0, CHARACTER_LIMIT);
        }

        return new String[]{prefix, suffix};
    }

    private String removeLastCharacter(String str) {
        String result = null;
        if ((str != null) && (str.length() > 0)) {
            result = str.substring(0, str.length() - 1);
        }
        return result;
    }



    public void setPlayer(@NotNull Player player) {
        if(this.player!=null)
            return;

        this.player = plugin.getBukkitPlayerManager().getPlayer(player);
        this.scoreboard = Objects.requireNonNull(plugin.getServer().getScoreboardManager().getNewScoreboard());
        this.objective = Objects.requireNonNull(scoreboard.registerNewObjective(name,"dummy"));
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(title.toString(this.player.getLocale()));

        this.createTeams();
        this.setBoard();
    }

    private void setBoard() {
        Objects.requireNonNull(player,"Bad initialisation");
        player.getPlayer().setScoreboard(scoreboard);
        player.setScoreboard(this);
    }

    private void createTeams() {
        int score = this.lines;

        Objects.requireNonNull(this.scoreboard,"Bad Initialisation");
        Objects.requireNonNull(this.objective,"Bad Initialisation");

        for (int i = 0; i < this.lines; i++) {
            Team t = this.scoreboard.registerNewTeam(ScoreboardLine.getTeamName(i));
            t.addEntry(ChatColor.values()[i] + "");
            this.objective.getScore(ChatColor.values()[i] + "").setScore(score);
            score--;
        }

        for(ScoreboardLine scoreboardLine : scoreboardLines.values()) {
            updateLine(scoreboardLine);
        }
    }

    @Nullable
    private ScoreboardLine getLineByName(@NotNull String lineName) {
        Objects.requireNonNull(lineName);
        for(ScoreboardLine scoreboardLine : scoreboardLines.values()) {
            if(Objects.equals(lineName,scoreboardLine.getName())) {
                return scoreboardLine;
            }
        }
        return null;
    }

    @NotNull
    public Message getLineAt(int index) {
        lineValid(index);
        if(scoreboardLines.get(index)==null) {
            return Message.valueOf("");
        } else {
            return scoreboardLines.get(index).getMessage();
        }
    }

    @Nullable
    public Message getLineFromName(@NotNull String name) {
        Objects.requireNonNull(name);
        ScoreboardLine line = getLineByName(name);
        if(line==null) {
            return null;
        } else {
            return line.getMessage();
        }
    }

    @NotNull
    public Message getTitle() {
        return this.title;
    }

    public int getLineCount() {
        return this.lines;
    }

    private void lineValid(int line) {
        if(line >= 0 && line <= this.lines-1)
            return;

        throw new ArrayIndexOutOfBoundsException("A scoreboard line number must be >= '0' AND < '"+this.lines+"'");
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return player!=null;
    }

    @NotNull
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("name",name)
                .add("running",isRunning())
                .add("title",title).toString();
    }
}
