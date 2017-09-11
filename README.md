# Rnl's Multi-Tap Keyboard on Android Things 

[Demo video](https://www.youtube.com/watch?v=AmGAt845jz4)

## The text entry technique: Multi-tap

This text entry technique on Android Things implements a multi-tap keyboard using 
10 switches, not unlike those found on cellular flip-phone models. 
It is a simple non-predictive method which groups multiple letters per key. 
The user disambiguates the character to type by clicking a number of times on 
the relevant key to cycle to it. 
With practice, it is easy to use the keyboard with one hand as well as have 0 
focus-of-attention (FOA), i.e. the user will not have to focus their eyes on 
the keyboard nor screen.

![](https://raw.githubusercontent.com/idd-fall17/hw2-rnlee0054/master/docs/294-084-2-img0.jpg)

The final setup using the Pico Pro Maker Kit for Android Things.


## Why I chose multi-tap

I wanted to create a text entry hardware that is practical. 
The multi-tap technique is a pervasive technique used in many hardware 
applications, one of the most common being the celluar flip-phone. 
Many people already know how to operate a 
multi-tap keyboard, so it was definitely considered practical/useful enough 
to become widely adopted. 
In addition, a standard multi-tap keyboard has ~9 essential keys; since the 
project limited the amount of switches to 10, the text entry technique would 
fit under these specifications. 

Lastly, the multi-tap keyboard can fit into a small form factor, which allows 
it to have a cute appearance! (and also enabling one-handed use). 

## Implementation of character recognition (code details)

Character input occurs on key-up due to the pull-up resistors. 
In the Pico Pro, this means the digital value latches LOW (button down) then 
HIGH (button released/key-up) again. 
The code is set up to use 10 GPIOs of the Pico Pro: 8 with internal pull-up 
resistors and 2 without. 

Each GPIO is assigned a key of the multi-tap keyboard (0, 2 through 9, and #). 
When a button release is received, `digitalEdgeEvent` resolves the appropriate 
action by looking up the pin (GPIO) name to determine the correct action (see 
"Key actions" section below for list of actions). 
If the GPIO pin is a character-input pin, it will cycle through its character 
sequence one at a time per key-up. 
After a `DELAY_TIME` (timeout) since the last key-up, the character will be 
entered. The timer is only in effect if a character sequence is being cycled 
through.

### Key actions

- Alphabet character input ('a'-'z')
- Space character input (' ')
- Backspace (removes one character)
- Shift (as in Shift on a keyboard--capitalizes alphabets. For this keyboard,
also switches space character input to backspace)

### Software signal debounce

Software signal debounce is also implemented and is defined by `DEBOUNCE_TIME`. 
Due to the nature of hardware switches, it oscillates before it reaches HIGH 
or LOW and stabilizes at the correct state. The software signal debounce 
implemented sets a delay `DEBOUNCE_TIME` which ignores any HIGH (key-up) events 
which occurs between the HIGH time and the HIGH time + delay.

## Hardware construction

![](https://raw.githubusercontent.com/idd-fall17/hw2-rnlee0054/master/docs/294-084-2-schematic.png)

The schematic of the hardware construction. Unused pins are hidden for simplictiy.

### Materials

- Pico Pro maker kit
- Ribbon wire (male-male) with 12 wires, one end cut off
- Plywood
- Acrylic
- 10 momentary tactile switches (6mm)
- 2 10 kOhm resistors
- Wire + solder
- Epoxy + hot melt glue

### Construction summary

The device is constructed using Adobe Illustrator to create an enclosure plan, 
then using a laser cutter to cut the enclosure pieces 
from plywood and acrylic sheets.
This part of the process was incremental.
Then, the switches are inserted into the back board and the connections are 
soldered to the relevant ribbon wires.
The enclosure is glued together with hot melt glue and the wooden buttons to 
extend the switches with epoxy glue.

## Personal reflection/thoughts/concerns/opinions

This digital switch assignment was enjoyable and rewarding. I did not have 
previous hardware or embedded systems experience despite my schooling in 
electrical engineering, nor have I used Adobe Illustrator, a laser cutter, 
or solder. This project allowed me to gain a lot of this experience that I 
wanted to try for a long time, and with the resources that I would not have 
had elsewhere (laser cutter!).

It was not particularly difficult, but it was very time-consuming especially 
due to my lack of experience.

# Usage of this Codebase

## Pre-requisites

- Android Things compatible board
- Android Studio 2.2+


## Build and install

On Android Studio, click on the "Run" button.

If you prefer to run on the command line, type

```bash
./gradlew installDebug
adb shell am start com.example.androidthings.myproject/.MainActivity
```

## License

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
