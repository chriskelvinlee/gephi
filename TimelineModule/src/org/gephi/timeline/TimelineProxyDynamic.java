/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.timeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.GraphController;
import org.gephi.timeline.TimelineQuartzSimple;
import org.gephi.timeline.api.TimelineProxy;
import org.gephi.timeline.api.TimelineProxyListener;
import org.gephi.timeline.api.TimelinePlayMode;
import org.gephi.timeline.api.TimelineQuartz;
import org.gephi.timeline.api.TimelineQuartzListener;
import org.openide.util.Lookup;

/**
 *
 * @author Julian Bilcke
 */
public class TimelineProxyDynamic implements TimelineProxy, TimelineQuartzListener {

    private Random random;
    private List<TimelineProxyListener> listeners;
    private Float from;
    private Float to;
    private TimelineQuartz quartz;

    // FAKE DATASET SIZE
    private List<Float> data;
    private int data_getNbOfFakeRevisions = 800;
    private Float speed;

    TimelinePlayMode playMode = TimelinePlayMode.YOUNGEST;

    public TimelineProxyDynamic() {
        
        // defaults
        from = 0.15f; // defaut position of the left hook when we start the GUI
        to = 0.85f; // default position of the right hook when we start the GUI
        speed = 0.0001f; // default timeline speed

        listeners = new ArrayList<TimelineProxyListener>();

        // WE GENERATE OUR FAKE DATASET
        random = new Random();
        data = new ArrayList<Float>();
        for (int i = 0; i < data_getNbOfFakeRevisions; i++) {
            data.add(0.15f + random.nextFloat() * 0.6f);
        }

        quartz = new TimelineQuartzSimple();
        quartz.addTimelineQuartzListener(this);
        quartz.setDelay(500);
    }

    public List<Float> getOverviewSample(int resolution) {
        // TODO put a call to the timeline engine here ?

        List<Float> tmp = new ArrayList<Float>();

        int unit = data.size() / resolution; // eg.  16 = 10000 / 600
        for (int i = 0; i < resolution; i++) {
            tmp.add(data.get(i * unit)); // eg. 600 * chunks of 16
        }
        return tmp;
    }

    public void addTimelineDataModelListener(TimelineProxyListener listener) {
        listeners.add(listener);
    }

    public List<Float> getZoomSample(int resolution) {

        if (resolution < 1) resolution = 1;
        
        // TODO put a call to the timeline engine here ?
        // get size from the timeline
        int size = data.size(); // eg 200000000

        int totalf = (int) (from * (float) size); // eg. 12000
        int totalt = (int) (to * (float) size); // eg. 12000000
        //System.out.println("totalf: " + totalf);
        //System.out.println("totalt: " + totalt);

        // get tmp from the timeline getFromRange(..,..)
        List<Float> tmp = new ArrayList<Float>();

        int unit = (totalt - totalf) / resolution; // eg.  16 = 10000 / 600
        if (unit < 1) unit = 1;
        //System.out.println("unit: " + unit);

        for (int i = 0; i < resolution; i++) {
            tmp.add(data.get(totalf + i * unit)); // eg. 600 * chunks of 16
        }
        return tmp;
    }

    public void selectInterval(Float from, Float to) {
        if (0.0f < to && to < 1.0f && 0.0f < from && from < 1.0f && from < to) {
            this.to = to;
            this.from = from;
            GraphController gc = Lookup.getDefault().lookup(GraphController.class);
            gc.getCentralDynamicGraph().setRange(from, to);
        }
    }
    public void selectTo(Float to) {
        if (0.0f < to && to < 1.0f && from < to) {
            this.to = to;
            GraphController gc = Lookup.getDefault().lookup(GraphController.class);
            gc.getCentralDynamicGraph().setRange(from, to);
        }
    }

    public void selectFrom(Float from) {
        if (0.0f < from && from < 1.0f && from < to) {
            this.from = from;
            GraphController gc = Lookup.getDefault().lookup(GraphController.class);
            gc.getCentralDynamicGraph().setRange(from, to);
        }

    }

    public Comparable getFirstComparable() {
        return data.get(0);
    }
    public Comparable getLastComparable() {
        return data.get(data.size()-1);
    }

    public Float getSelectionFrom() {
        return from;
    }

    public Comparable getSelectionFromAsComparable() {
        return data.get((int) (from * (float)data.size()));
    }
    public Float getSelectionTo() {
        return to;
    }
    public Comparable getSelectionToAsComparable() {
        return data.get((int) (to * (float)data.size()));
    }

    public void play() {
        quartz.start();
    }

    public void play(TimelinePlayMode playMode) {
        this.playMode = playMode;
        if (quartz.isRunning()) 
            quartz.start();
        
    }

    public void pause() {
        quartz.delayedStop();
    }

    public void isPlaying() {
        quartz.isRunning();
    }

    /*
     * quartzTick
     * Called whenever the quartz trigger a timecode signal
     * */
    public void quartzTick(long delay) {
        // delay is the current "resolution" of the quartz
        // this is provided ases convenience, in case you would need it for your
        // calculations
        switch(playMode) {
            case OLDEST:
                selectFrom(getSelectionFrom() + speed);
                break;
            case BOTH:
                selectInterval(getSelectionFrom() + speed,
                               getSelectionTo() + speed);
                break;
            case YOUNGEST:
                selectTo(getSelectionTo() + speed);
                break;
        }
    }


    public void setTimelinePlayMode(TimelinePlayMode playMode) {
        this.playMode = playMode;
    }
}
