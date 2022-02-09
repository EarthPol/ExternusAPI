package me.deltaorion.extapi.display.bossbar;

import org.jetbrains.annotations.NotNull;

/**
 * Renders a bossbar to the users screen. This class is responsible for performing any actions related to sending a BossBar
 * to the user.
 */
public interface BossBarRenderer {

    /**
     * Sets the message shown on the bossbar. If the bossbar is running this should update the shown bossbar.
     *
     * @param render
     */
    public void setMessage(@NotNull String render);

    /**
     * Each bossbar has a progress slider. If set to 1 then the bossbar will be fully colored in. If set to 0 then the bossbar
     * will not be colored in at all. If set to 0.5 then it will be 50% colored in
     *
     * @param progress The progress to set the bossbar to
     * @throws IllegalArgumentException If the value is less than 0 or greater than 1
     */
    public void setProgress(float progress);

    /**
     * Toggles whether the bossbar should be visible on the users screen. If set to false one should not see the bossbar at all
     * and if set to true they should see it.
     *
     * @param visible Whether the bossbar should be visible
     */
    public void setVisible(boolean visible);

    /**
     * Performs any necessary updating to the shown BossBar.
     */
    public void update();
}