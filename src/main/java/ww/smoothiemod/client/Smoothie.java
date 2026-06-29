package ww.smoothiemod.client;

import net.fabricmc.api.ClientModInitializer;

public class Smoothie implements ClientModInitializer {

    public static boolean ENABLED = true;


    public static int JITTER_BUFFER_TICKS = 1;
    public static int MAX_STEPS = 6;



    @Override
    public void onInitializeClient() {
    }
}