package com.example.androidthings.myproject;

import android.widget.ImageView;
import com.google.android.things.pio.Gpio;
import java.lang.Character;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by rnl on 9/8/2017.
 * This code contains the application logic for a multi-tap keyboard similar to those found on
 * cellular flip-phone models. This is a simple non-predictive method which groups multiple letters
 * per key. The user clicks a number of times per key to disambiguate the character.
 */

public class MultiTapApp extends SimplePicoPro {
    private long oldMs; // Time in millis from program start to last key press.
    private char charPrint; // Current character of key's char sequence.
    private boolean shift; // Capitalize/alternate button function state.

    private final long DELAY_TIME = 350; // Key press timeout.
    // Key character groupings.
    private final ArrayList<Character> TWO = new ArrayList<>(Arrays.asList('a', 'b', 'c'));
    private final ArrayList<Character> THREE = new ArrayList<>(Arrays.asList('d', 'e', 'f'));
    private final ArrayList<Character> FOUR = new ArrayList<>(Arrays.asList('g', 'h', 'i'));
    private final ArrayList<Character> FIVE = new ArrayList<>(Arrays.asList('j', 'k', 'l'));
    private final ArrayList<Character> SIX = new ArrayList<>(Arrays.asList('m', 'n', 'o'));
    private final ArrayList<Character> SEVEN = new ArrayList<>(Arrays.asList('p', 'q', 'r', 's'));
    private final ArrayList<Character> EIGHT = new ArrayList<>(Arrays.asList('t', 'u', 'v'));
    private final ArrayList<Character> NINE = new ArrayList<>(Arrays.asList('w', 'x', 'y', 'z'));
    private final ArrayList<Character> ZERO = new ArrayList<>(Arrays.asList(' '));
    private final ArrayList<Character> POUND = new ArrayList<>(Arrays.asList('?'));
    // Association of GPIO number to a key character grouping. Set it in setup().
    private final HashMap<Gpio, ArrayList<Character>> GPIO_TO_CHARS = new HashMap<>();
    private HashSet<Gpio> GPIOS; // All GPIOs being used by the keyboard.
    private HashSet<Gpio> GPIO_ALPHA; // GPIOs that handle the standard alphabet chars (#2-9).

    @Override
    public void setup() {
        // ----------------------------------------------------------------------------------------
        // Set up GPIOs to input
        // ----------------------------------------------------------------------------------------
        pinMode(GPIO_128,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_128,Gpio.EDGE_BOTH);

        pinMode(GPIO_39,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_39,Gpio.EDGE_BOTH);

        pinMode(GPIO_37,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_37,Gpio.EDGE_BOTH);

        pinMode(GPIO_35,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_35,Gpio.EDGE_BOTH);

        pinMode(GPIO_34,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_34,Gpio.EDGE_BOTH);

        pinMode(GPIO_33,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_33,Gpio.EDGE_BOTH);

        pinMode(GPIO_32,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_32,Gpio.EDGE_BOTH);

        pinMode(GPIO_10,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_10,Gpio.EDGE_BOTH);

        pinMode(GPIO_172,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_172,Gpio.EDGE_BOTH);

        pinMode(GPIO_173,Gpio.DIRECTION_IN);
        setEdgeTrigger(GPIO_173,Gpio.EDGE_BOTH);

        // ----------------------------------------------------------------------------------------
        // Register GPIOs to character sets depending on how ribbon cable is plugged into header!
        // ----------------------------------------------------------------------------------------
        GPIO_TO_CHARS.put(GPIO_128, THREE);
        GPIO_TO_CHARS.put(GPIO_39, SIX);
        GPIO_TO_CHARS.put(GPIO_37, NINE);
        GPIO_TO_CHARS.put(GPIO_35, POUND); // SHIFT (defined in logic)
        GPIO_TO_CHARS.put(GPIO_34, TWO);
        GPIO_TO_CHARS.put(GPIO_33, FIVE);
        GPIO_TO_CHARS.put(GPIO_32, EIGHT);
        GPIO_TO_CHARS.put(GPIO_10, ZERO); // On shift: BACKSPACE (defined in logic)
        GPIO_TO_CHARS.put(GPIO_172, SEVEN);
        GPIO_TO_CHARS.put(GPIO_173, FOUR);

        GPIOS = new HashSet<>(GPIO_TO_CHARS.keySet());
        GPIO_ALPHA = new HashSet<>(GPIOS);
        GPIO_ALPHA.remove(GPIO_35);
        GPIO_ALPHA.remove(GPIO_10);

        // ----------------------------------------------------------------------------------------
        // Set up other local vars.
        // ----------------------------------------------------------------------------------------
        charPrint = '\0';
        shift = false;
    }

    @Override
    public void loop() {
        // For multi-tap (delay)
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
        if (pin == GPIO_35) {
            toggleShift();
        }
        if (pin == GPIO_10) {
            if (shift) {
                deleteCharacterToScreen();
            } else {
                cycleChar(pin);
            }
        }
        if (GPIO_ALPHA.contains(pin)){
            cycleChar(pin);
        }
    }

    private void toggleShift() {
        shift = !shift;
        setImageCaps(shift);
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
