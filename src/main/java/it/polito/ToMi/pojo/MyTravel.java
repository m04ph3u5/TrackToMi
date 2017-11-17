package it.polito.ToMi.pojo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
public class MyTravel {

    @Id
    private String id;
    @Indexed
    private String passengerId;
    private Date start;
    private Date end;
    private List<MyPartialTravel> partialTravels;
    private long lastUpdate;
    private Integer lastmode;

    public MyPartialTravel getLastPartialTravel(){
        MyPartialTravel toRet = null;
        if(partialTravels != null && partialTravels.size()>0){
            toRet = partialTravels.get(0);
        }
        if (toRet != null && toRet.getEndPosition() == null){
            return toRet;
        }
        return null;
    }


    public void addPartialTravel(MyPartialTravel pt){
        if(partialTravels == null){
            partialTravels = new LinkedList<>();
        }
        partialTravels.add(pt);

        partialTravels.sort(new Comparator<MyPartialTravel>() {
            @Override
            public int compare(MyPartialTravel o1, MyPartialTravel o2) {
                return o1.getStartPosition().getTimestamp().compareTo(o2.getStartPosition().getTimestamp());
            }
        });
        Collections.reverse(partialTravels);
    }

    public List<MyPartialTravel> getPartialTravels() {
        return partialTravels;
    }

    public void setPartialTravels(List<MyPartialTravel> partialTravels) {
        this.partialTravels = partialTravels;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getLastmode() {
        return lastmode;
    }

    public void setLastmode(Integer lastmode) {
        this.lastmode = lastmode;
    }
}
