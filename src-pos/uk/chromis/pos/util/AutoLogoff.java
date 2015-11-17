//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//     Chromis POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Chromis POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.
package uk.chromis.pos.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.Timer;

public class AutoLogoff implements ActionListener, AWTEventListener {

    private final static long KEY_EVENTS = AWTEvent.KEY_EVENT_MASK;
    private final static long MOUSE_EVENTS = AWTEvent.MOUSE_MOTION_EVENT_MASK
            + AWTEvent.MOUSE_EVENT_MASK;
    private final static long USER_EVENTS = KEY_EVENTS + MOUSE_EVENTS;

    private Action action;
    private final long eventMask;
    private Boolean running = false;
    private Timer LogoffTimer;

    private static AutoLogoff INSTANCE = new AutoLogoff();
    public static Boolean timer = false;

    // create a basic timer instance
    private AutoLogoff() {
        LogoffTimer = new Timer(100000, action);
        this.eventMask = USER_EVENTS;
        LogoffTimer.setInitialDelay(100);
    }

    public static AutoLogoff getInstance() {
        if (INSTANCE == null) {
            synchronized (AutoLogoff.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AutoLogoff();
                }
            }
        }
        return INSTANCE;
    }

    /*
     * 
     * Routines to control the timer
     * start() manually starts the timer
     * stop() manually stops the timer
     * restart() manually restart the timer
     * isRunning() returns the state of the timer
     * setTimer(Integer period, action ) set the interval rate of the timer and action event
     * 
     * 
     */
    public void start() {
        running = true;
        LogoffTimer.setRepeats(false);
        LogoffTimer.start();
        Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
    }

    public void stop() {
        running = false;
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        LogoffTimer.stop();
    }

    // Implement ActionListener for the Timer
    @Override
    public void actionPerformed(ActionEvent e) {
        action.actionPerformed(e);
    }

    // Implement AWTEventListener, all events are dispatched via this
    @Override
    public void eventDispatched(AWTEvent e) {
        if (isTimerRunning()) {
            LogoffTimer.restart();
        }
    }

    // Implement a manually triggered restart
    public void restart() {
        LogoffTimer.restart();
    }

    // if the timer is not running restart it
    public void setRunning() {
        if (!isTimerRunning()) {
            LogoffTimer.restart();
        }
    }

    // returns the timer state
    public boolean isTimerRunning() {
        return (running);
    }

    // set the timer interval in seconds
    public void setTimer(Integer period, Action action) {
        timer = true;
        if (isTimerRunning()) {
            stop();
            LogoffTimer = new Timer(period, action);
            start();
        } else {
            LogoffTimer = new Timer(period, action);
        }
    }

    public void removeTimer(){
        timer = false;
    }
}
