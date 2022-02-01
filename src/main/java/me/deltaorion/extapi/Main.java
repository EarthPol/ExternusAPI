package me.deltaorion.extapi;

import com.google.common.collect.ImmutableSet;
import me.deltaorion.extapi.animation.MinecraftFrame;
import me.deltaorion.extapi.animation.RunningAnimation;
import me.deltaorion.extapi.animation.factory.AnimationFactories;
import me.deltaorion.extapi.test.TestPlugin;
import me.deltaorion.extapi.test.TestServer;
import me.deltaorion.extapi.test.animation.LoggingAnimation;
import me.deltaorion.extapi.test.animation.TestAnimation;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class Main {

    public static Main main = new Main();

    public static void main(String[] args) {
        try {
            main.run();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void run() throws URISyntaxException {
        TestServer server = new TestServer();
        TestPlugin plugin = new TestPlugin(server);
        server.addPlugin("Test",plugin);

        System.out.println("beginning");
        LoggingAnimation animation = new LoggingAnimation(plugin);
        animation.addScreen(plugin.getPluginLogger());
        RunningAnimation animation1 = animation.start();
        /*
        for(int i=0;i<100;i++) {
            System.out.println("Running: "+i);
            simpleAnimationTest(plugin);
        }
         */
        ;

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        plugin.onDisable();
    }

    public void simpleAnimationTest(TestPlugin plugin) {
        try {
            testAnimation(new TestAnimation(plugin, AnimationFactories.SCHEDULE_ASYNC()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void testAnimation(TestAnimation animation) throws InterruptedException {
        List<String> screenA = new CopyOnWriteArrayList<>();
        List<String> screenB = new CopyOnWriteArrayList<>();
        screenB.add("aaa");
        assertEquals(animation.getScreens(), Collections.emptySet());
        assertEquals(animation.getFrames(),Collections.emptyList());
        animation.addScreen(screenA);
        assertEquals(animation.getScreens(), ImmutableSet.of(screenA));
        animation.removeScreen(screenA);
        assertEquals(animation.getScreens(),Collections.emptySet());
        animation.addScreen(screenA);
        animation.addScreen(screenB);
        assertEquals(2,animation.getScreens().size());
        animation.removeScreen(screenB);
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        animation.addFrame(new MinecraftFrame<>("Gamer",10));
        RunningAnimation runningAnimation = animation.start();
        Thread.sleep(5);
        assertTrue(runningAnimation.isAlive());
        animation.addScreen(screenB);
        Thread.sleep(10);
        assertTrue(screenA.size()>0);
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        animation.addFrame(new MinecraftFrame<>("Gamer",0));
        assertTrue(runningAnimation.isAlive());
        Thread.sleep(20);
        assertTrue(screenB.size()>1);
        assertTrue(screenA.size()>0);
        runningAnimation.cancel();
        Thread.sleep(20);
        assertFalse(runningAnimation.isAlive());
        int size = screenA.size();
        int size2 = screenB.size();
        Thread.sleep(20);
        assertEquals(size,screenA.size());
        assertEquals(size2,screenB.size());
    }


}
