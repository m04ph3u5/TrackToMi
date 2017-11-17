package it.polito.ToMi.pojo;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MyPartialTravel {

    private int mode;
    private DetectedPosition startPosition;
    private DetectedPosition endPosition;
    private List<DetectedPosition> positions;
    private List<Geofence> geofences;

    public void addGeofence(Geofence g){
        if(geofences == null){
            geofences = new LinkedList<>();
        }
        geofences.add(g);

        Collections.sort(geofences);
    }

    public Geofence getLastGeofence(){
        if(geofences != null && geofences.size()>0){
            return geofences.get(0);
        }
        return null;
    }

    public DetectedPosition getLastPosition(){
        if(positions != null && !positions.isEmpty()){
            return  positions.get(positions.size()-1);
        }
        return null;
    }

    public void addPosition(DetectedPosition p ){
        if(positions == null){
            positions = new LinkedList<>();
        }
        positions.add(p);
    }

    public List<Geofence> getGeofences() {
        return geofences;
    }

    public void setGeofences(List<Geofence> geofences) {
        this.geofences = geofences;
    }

    public List<DetectedPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<DetectedPosition> positions) {
        this.positions = positions;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public DetectedPosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(DetectedPosition startPosition) {
        this.startPosition = startPosition;
    }

    public DetectedPosition getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(DetectedPosition endPosition) {
        this.endPosition = endPosition;
    }
}
