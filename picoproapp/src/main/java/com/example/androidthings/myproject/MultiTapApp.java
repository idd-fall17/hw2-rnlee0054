package com.example.androidthings.myproject;

import com.google.android.things.pio.Gpio;
import java.lang.Character;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by rnl on 9/8/2017.
 */

public class MultiTapApp extends SimplePicoPro {
    private long oldMs;
    private char charPrint;
    private boolean shift; // Or "alt" key.

    private final long DELAY_TIME = 250;
    private final ArrayList<Character> TWO = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
    private final ArrayList<Character> THREE = new ArrayList<>(Arrays.asList('d', 'e', 'f'));
    private final ArrayList<Character> FOUR = new ArrayList<>(Arrays.asList('g', 'h', 'i'));
    private final ArrayList<Character> FIVE = new ArrayList<>(Arrays.asList('j', 'k', 'l'));
    private final ArrayList<Character> SIX = new ArrayList<>(Arrays.asList('m', 'n', 'o'));
    private final ArrayList<Character> SEVEN = new ArrayList<>(Arrays.asList('p', 'q', 'r', 's'));
    private final ArrayList<Character> EIGHT = new ArrayList<>(Arrays.asList('t', 'u', 'v'));
    private final ArrayList<Character> NINE = new ArrayList<>(Arrays.asList('w', 'x', 'y', 'z'));
    private final ArrayList<Character> ZERO = new ArrayList<>(Arrays.asList(' '));
    private final HashMap<Gpio, ArrayList<Character>> GPIO_TO_CHARS = new HashMap<>();
    private HashSet<Gpio> GPIOS;

    @Override
    public void setup() {
        //set two GPIOs to input
        pinMode(GPIO_128,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_128,Gpio.EDGE_BOTH);

        pinMode(GPIO_39,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_39,Gpio.EDGE_BOTH);

        pinMode(GPIO_37,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_37,Gpio.EDGE_BOTH);

        // Set up other local vars.
        charPrint = '\0';
        shift = false;

        // ----------------------------------------------------------------------------------------
        // Set this up depending on how ribbon cable is plugged into header!
        // ----------------------------------------------------------------------------------------
        GPIO_TO_CHARS.put(GPIO_128, TWO);
        GPIO_TO_CHARS.put(GPIO_37, ZERO);

        GPIOS = new HashSet<>(GPIO_TO_CHARS.keySet());
    }

    @Override
    public void loop() {
        // For multi-tap (chord)
        if (charPrint != '\0') {
            if (millis() - oldMs >= DELAY_TIME) {
                printChar();
            }
        }
    }

    @Override
    void digitalEdgeEvent(Gpio pin, boolean value) {
        setTime();
        println("digitalEdgeEvent"+pin+", "+value);
        // This is on button release for pull-up resistors.
        if (GPIOS.contains(pin) && value==HIGH) {
            resolveClick(pin);
        }
    }

    private void resolveClick(Gpio pin) {
        if (pin == GPIO_39) {
            toggleShift();
        }
        if (pin == GPIO_37) {
            if (shift) {
                deleteCharacterToScreen();
            } else {
                cycleChar(pin);
            }
        }
        if (pin == GPIO_128){
            cycleChar(pin);
        }
    }

    private void toggleShift() {
        shift = !shift;
    }

    /**
     * Sets relevant char based on current char and pin (gpio) button pressed.
     * @param pin
     */
    private void cycleChar(Gpio pin) {
        ArrayList<Character> chars = GPIO_TO_CHARS.get(pin);
        int idx = chars.indexOf(Character.toLowerCase(charPrint));

        // Character not found in current multi-tap sequence.... Print to start new sequence.
        if (idx == -1 && charPrint != '\0') {
            printChar();
        }

        // End of cycle; keep last character.
        if (idx == chars.size() - 1) {
            return;
        }

        // Note: First character of the cycle is handled too (-1 + 1 == 0).
        char toPrint = chars.get(idx + 1);
        charPrint = shift ? Character.toUpperCase(toPrint) : toPrint;
    }

    private void printChar() {
        printCharacterToScreen(charPrint);
        resetChar();
    }

    private void resetChar() {
        charPrint = '\0';
    }

    private void setTime() {
        oldMs = millis();
    }
}
