package it.polito.ToMi.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;
import org.springframework.stereotype.Service;

import it.polito.ToMi.clustering.UserCluster;
import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.NotFoundException;
import it.polito.ToMi.pojo.Answer;
import it.polito.ToMi.pojo.Bus;
import it.polito.ToMi.pojo.BusRunDetector;
import it.polito.ToMi.pojo.BusStop;
import it.polito.ToMi.pojo.Comment;
import it.polito.ToMi.pojo.DailyData;
import it.polito.ToMi.pojo.DailyInfo;
import it.polito.ToMi.pojo.DayPassengerBusRun;
import it.polito.ToMi.pojo.DetectedPosition;
import it.polito.ToMi.pojo.Geofence;
import it.polito.ToMi.pojo.InfoPosition;
import it.polito.ToMi.pojo.PartialTravel;
import it.polito.ToMi.pojo.PartialTravelComparatorOnRunId;
import it.polito.ToMi.pojo.Passenger;
import it.polito.ToMi.pojo.PositionPerApp;
import it.polito.ToMi.pojo.Run;
import it.polito.ToMi.pojo.RunDTO;
import it.polito.ToMi.pojo.RunDetail;
import it.polito.ToMi.pojo.Stop;
import it.polito.ToMi.pojo.StopInfo;
import it.polito.ToMi.pojo.TemporaryTravel;
import it.polito.ToMi.pojo.TransportTime;
import it.polito.ToMi.pojo.Travel;
import it.polito.ToMi.pojo.UserHistory;
import it.polito.ToMi.repository.BusRepository;
import it.polito.ToMi.repository.BusStopRepository;
import it.polito.ToMi.repository.CommentRepository;
import it.polito.ToMi.repository.DetectedPositionRepository;
import it.polito.ToMi.repository.GeofenceRepository;
import it.polito.ToMi.repository.PassengerRepository;
import it.polito.ToMi.repository.RunRepository;
import it.polito.ToMi.repository.TemporaryTravelRepository;
import it.polito.ToMi.repository.TravelRepository;
import it.polito.ToMi.repository.UserClusterRepository;

@Service
public class AppServiceImpl implements AppService {

  @Autowired
  private DetectedPositionRepository posRepo;

  @Autowired
  private BusStopRepository busStopRepo;

  @Autowired
  private RunRepository runRepo;

  @Autowired
  private TemporaryTravelRepository tempTravelRepo;

  @Autowired
  private TravelRepository travelRepo;

  @Autowired
  private CommentRepository commentRepo;

  @Autowired
  private BusRepository busRepo;

  @Autowired
  private PassengerRepository passRepo;

  @Autowired
  private GeofenceRepository geofenceRepo;

  @Autowired
  private UserClusterRepository userClusterRepo;

  public static final int IN_VEHICLE = 0;
  public static final int ON_BICYCLE = 1;
  public static final int ON_FOOT = 2;
  public static final int STILL = 3;
  public static final int UNKNOWN = 4;
  public static final int TILTING = 5;
  public static final int WALKING = 7;
  public static final int RUNNING = 8;
  public static final int ENTER = 9;
  public static final int EXIT = 10;
  public static final int ONCREATE = 11;
  public static final int ONDESTROY = 12;
  public static final int ARRIVED = 14;
  public static final int ON_BUS = 15;

  private final int NOPOSITION = 1000;
  private final int STILL_THREESHOLD = 100;
  private final int MAX_GEOFENCE_DISTANCE = 3000;

  // 20minutes in millis
  private final long TWENTY_MINUTES = 1200000;
  // 1minute in millis
  private final long ONE_MINUTE = 60000;
  // 3minutes in millis
  private final long THREE_MINUTES = 180000;
  // 5minutes in millis
  private final long FIVE_MINUTES = 300000;

  private final long ACCURACY_TIME_THREESHOLD = 1000;

  private SimpleDateFormat date;
  private SimpleDateFormat time;

  @PostConstruct
  public void initialize() {
    date = new SimpleDateFormat("yyyy-MM-dd");
    time = new SimpleDateFormat("HH:mm");
  }

  @Override
  public void saveDetectedPosition(List<DetectedPosition> position, Passenger passenger) {

    passenger.setLastPosition(new Date());
    passRepo.save(passenger);

    // Mi arriva una lista di posizioni rilevate dall'app. Controllo se per quel
    // passeggero ho già un viaggio pendente
    // (oggetti temporaryTravel). Nel caso non ne avessi per quel passeggero nel
    // database, ne istanzio uno.
    TemporaryTravel tempTravel = tempTravelRepo.findByPassengerIdAndDeviceId(passenger.getId(),
        position.get(0).getDeviceId());
    if (tempTravel == null) {
      tempTravel = new TemporaryTravel();
      tempTravel.setPassengerId(passenger.getId());
      tempTravel.setDeviceId(position.get(0).getDeviceId());
    }

    for (DetectedPosition p : position) {
      // Se il log che mi arriva dall'app non ha una posizione valida, lo salvo
      // all'interno del repo delle detectedPosition (log) ma non lo considero
      // per la costruzione di viaggi
      p.setUserId(passenger.getId());
      DetectedPosition last = tempTravel.getLastPosition();

      if (tempTravel.getSizeOfDetectedPosition() == 1) {
        Geofence geofence = geofenceRepo.lastGeofenceOfUser(passenger.getId());
        if (geofence != null && geofence.getDurationOfStay() == 0) {
          geofence.setExitTimestamp(p.getTimestamp().getTime());
          geofenceRepo.save(geofence);
        }
      }
      if (p.getPosition().getLat() == NOPOSITION)
        tempTravel.addMissingPoint();

      if (last == null && p.getMode() != ENTER && p.getMode() != ONDESTROY && p.getPosition().getLat() != NOPOSITION)
        tempTravel.addDetectedPos(p);
      else {
        if (last != null && (p.getMode() == ENTER || p.getMode() == ONDESTROY
            || (p.getTimestamp().getTime() - last.getTimestamp().getTime() > TWENTY_MINUTES))) {
          saveGeofence(last, p, passenger.getId());
          saveTravel(tempTravel);
          tempTravel = new TemporaryTravel();
          tempTravel.setPassengerId(passenger.getId());
          tempTravel.setDeviceId(position.get(0).getDeviceId());
          if (p.getMode() != ENTER && p.getMode() != ONDESTROY && p.getPosition().getLat() != NOPOSITION)
            tempTravel.addDetectedPos(p);
        } else {
          if (p.getPosition().getLat() != NOPOSITION)
            tempTravel.addDetectedPos(p);
        }
      }
    }

    if (tempTravel.getSizeOfDetectedPosition() > 0)
      tempTravelRepo.save(tempTravel);
    posRepo.insert(position);
  }

  private void saveGeofence(DetectedPosition last, DetectedPosition p, String userId) {
    // se la distanza tra il punto di fine di un viaggio e quello di inizio del
    // nuovo viaggio è maggiore di 3000 m allora li considero entrambi geofence
    // in caso contrario salvo come geofence soltanto il punto di fine del
    // viaggio che sto per salvare

    if (p.getPosition().getLat() != NOPOSITION && distFrom(last.getPosition().getLat(), last.getPosition().getLng(),
        p.getPosition().getLat(), p.getPosition().getLng()) > MAX_GEOFENCE_DISTANCE) {
      Geofence g2 = new Geofence();
      g2.setPoint(p.getPosition());
      g2.setEnterTimestamp(p.getTimestamp().getTime());
      g2.setExitTimestamp(p.getTimestamp().getTime());
      g2.setUserId(userId);
      geofenceRepo.save(g2);
    }
    if (last.getPosition().getLat() != NOPOSITION) {
      Geofence g1 = new Geofence();
      g1.setPoint(last.getPosition());
      g1.setEnterTimestamp(last.getTimestamp().getTime());
      g1.setExitTimestamp(p.getTimestamp().getTime());
      g1.setUserId(userId);
      geofenceRepo.save(g1);
    }
  }

  @Override
  public List<BusStop> getAllBusStop() {
    return busStopRepo.findAll();
  }

  @Override
  public List<Bus> getAllBus() {
    return busRepo.findAll();
  }

  @Override
  public void saveComments(List<Comment> comments, String userId) {

    for (Comment c : comments) {
      c.setUserId(userId);
      c.setDate(date.format(c.getTimestamp()));
      c.setTime(time.format(c.getTimestamp()));
    }
    commentRepo.save(comments);
  }

  @Override
  public void saveAnswerToComments(String id, List<Answer> answers, String userId) throws BadRequestException {
    Comment father = commentRepo.findById(id);
    if (father == null)
      throw new BadRequestException("Commento non trovato!");
    for (Answer c : answers) {
      c.setUserId(userId);
      c.setDate(date.format(c.getTimestamp()));
      c.setTime(time.format(c.getTimestamp()));
    }
    father.addAnswers(answers);
    commentRepo.save(father);
  }

  @Override
  public List<Comment> getComments(String category, Long before) throws BadRequestException {
    // Nessun parametro, ritorno i 20 commenti più recenti
    if ((category == null || category.isEmpty()) && (before == null || before == 0)) {
      return commentRepo.getLastComments();
      // category specifica, nessuna data -> ritorno i 20 commenti di quella
      // categoria più recenti
    } else if ((category != null && !category.isEmpty()) && (before == null || before == 0)) {
      return commentRepo.getLastCommentsByCategory(category);
      // data specifica, nessuna categoria -> ritorno i 20 commenti precedenti a
      // quella data in ordine decrescente
    } else if ((category == null || category.isEmpty()) && (before != null && before > 0)) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(before);
      return commentRepo.getLastCommentBeforeDate(cal.getTime());
      // data e categoria specificata -> ritorno i 20 commenti di quella
      // categoria precedenti alla data specificata in ordine decrescente
    } else if ((category != null && !category.isEmpty()) && (before != null && before > 0)) {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(before);
      return commentRepo.getLastCommentByCategoryBeforeDate(cal.getTime(), category);
    }
    throw new BadRequestException("Illegal parameters for get comments");
  }

  private void saveTravel(TemporaryTravel tempTravel) {

    List<PartialTravel> partials = new LinkedList<PartialTravel>();
    List<DetectedPosition> positions = tempTravel.getDetectedPosList();
    boolean atLeastOneBusTravel = false;
    if (positions.size() > 2) {

      PartialTravel currentPartial = null;

      for (DetectedPosition p : positions) {
        // Prima iterazione costruisco il partial travel
        // A questo livello setto al partial travel il modo associato alla prima
        // posizione (ed eventualmente il beaconId).
        if (currentPartial == null) {
          currentPartial = new PartialTravel();
          currentPartial.setStart(p.getTimestamp());

          // Aggiorno la mappa degli userMode dentro al partial
          if (p.getUserMode() != null)
            currentPartial.addMode(p.getUserMode());

          if (p.getBeaconId() != null && !p.getBeaconId().isEmpty()) {
            p.setMode(ON_BUS);
            atLeastOneBusTravel = true;
            currentPartial.setBeaconId(p.getBeaconId());
          } else {
            currentPartial.setMode(p.getMode());
            currentPartial.setBeaconId("");
          }
          InfoPosition i = new InfoPosition(p);
          currentPartial.addInfoPosition(i);
          // Tutte le iterazioni successive
        } else {

          // Aggiorno la mappa degli userMode dentro al partial
          if (p.getUserMode() != null)
            currentPartial.addMode(p.getUserMode());

          // La posizione ha un beaconId associato
          if (p.getBeaconId() != null && !p.getBeaconId().isEmpty()) {
            // Se il partial ha lo stesso beaconId, aggiungo la posizione al
            // partial
            if (currentPartial.getBeaconId().equals(p.getBeaconId())) {
              InfoPosition i = new InfoPosition(p);
              currentPartial.addInfoPosition(i);
              // In caso contrario salvo il partial precdente e new creo uno
              // nuovo.
            } else {
              currentPartial.setEnd(
                  currentPartial.getAllPositions().get(currentPartial.getAllPositions().size() - 1).getTimestamp());
              lengthOfPartial(currentPartial);
              if (currentPartial.getBeaconId() != null && !currentPartial.getBeaconId().isEmpty())
                currentPartial.setMode(ON_BUS);
              partials.add(currentPartial);
              currentPartial = new PartialTravel();
              currentPartial.setStart(p.getTimestamp());
              p.setMode(ON_BUS);
              atLeastOneBusTravel = true;
              currentPartial.setBeaconId(p.getBeaconId());

              InfoPosition i = new InfoPosition(p);
              currentPartial.addInfoPosition(i);
            }
            // La posizione non ha un beaconId associato mentre il partial sì.
          } else if ((p.getBeaconId() == null || p.getBeaconId().isEmpty()) && currentPartial.getBeaconId() != null
              && !currentPartial.getBeaconId().isEmpty()) {
            currentPartial.setEnd(
                currentPartial.getAllPositions().get(currentPartial.getAllPositions().size() - 1).getTimestamp());
            lengthOfPartial(currentPartial);
            currentPartial.setMode(ON_BUS);
            partials.add(currentPartial);
            currentPartial = new PartialTravel();
            currentPartial.setStart(p.getTimestamp());
            currentPartial.setMode(p.getMode());
            currentPartial.setBeaconId("");

            InfoPosition i = new InfoPosition(p);
            currentPartial.addInfoPosition(i);
            // Né la posizione né il partial hanno un beaconId associato -->
            // CASO PIÙ COMUNE
            // Controllo se la posizione ha associata una posizione di movimento
            // (FOOT, VEHICLE, BYCICLE).
          } else if (isMovement(p.getMode())) {

            if (!isMovement(currentPartial.getMode())) {
              currentPartial.setMode(p.getMode());
              InfoPosition i = new InfoPosition(p);
              currentPartial.addInfoPosition(i);
            } else {
              if (p.getMode() == currentPartial.getMode()) {
                InfoPosition i = new InfoPosition(p);
                currentPartial.addInfoPosition(i);
              } else {
                currentPartial.setEnd(
                    currentPartial.getAllPositions().get(currentPartial.getAllPositions().size() - 1).getTimestamp());
                lengthOfPartial(currentPartial);
                if (currentPartial.getBeaconId() != null && !currentPartial.getBeaconId().isEmpty())
                  currentPartial.setMode(ON_BUS);
                partials.add(currentPartial);
                currentPartial = new PartialTravel();
                currentPartial.setStart(p.getTimestamp());
                if (p.getBeaconId() != null && !p.getBeaconId().isEmpty()) {
                  p.setMode(ON_BUS);
                  currentPartial.setBeaconId(p.getBeaconId());
                } else {
                  currentPartial.setMode(p.getMode());
                  currentPartial.setBeaconId("");
                }
                InfoPosition i = new InfoPosition(p);
                currentPartial.addInfoPosition(i);
              }
            }
          } else {
            InfoPosition i = new InfoPosition(p);
            currentPartial.addInfoPosition(i);
            if (currentPartial.getMode() == EXIT && p.getMode() == STILL)
              currentPartial.setMode(STILL);
          }
        }

      }
      if (currentPartial.getAllPositions().size() > 0) {
        currentPartial
            .setEnd(currentPartial.getAllPositions().get(currentPartial.getAllPositions().size() - 1).getTimestamp());
        lengthOfPartial(currentPartial);
        if (currentPartial.getBeaconId() != null && !currentPartial.getBeaconId().isEmpty())
          currentPartial.setMode(ON_BUS);
        partials.add(currentPartial);
      }
    }

    for (int i = 0; i < partials.size(); i++) {
      PartialTravel p = partials.get(i);
      if (!isMovement(p.getMode())) {
        int userMode = p.getUserMode();
        if (isMovement(userMode))
          p.setMode(userMode);
      }
      if (!isValidStep(p)) {
        aggregateStep(i, partials);
      }
    }

    if (partials.size() > 0) {
      Travel travel = new Travel();
      travel.setPassengerId(tempTravel.getPassengerId());
      travel.setStart(partials.get(0).getStart());
      travel.setEnd(partials.get(partials.size() - 1).getEnd());
      travel.setOnBus(atLeastOneBusTravel);
      travel.setPartials(partials);
      Calendar cal = Calendar.getInstance();
      cal.setTime(travel.getStart());
      cal.set(Calendar.HOUR_OF_DAY, 12);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      travel.setDayTimestamp(cal.getTime().getTime());
      lengthOfTravel(travel, tempTravel.getMissingPoint());

      if (travel.isOnBus()) {
        for (PartialTravel p : travel.getPartials()) {
          if (p.getBeaconId() != null && !p.getBeaconId().isEmpty()) {
            saveRun(p, travel.getPassengerId(), travel.getDayTimestamp(), date.format(travel.getStart()));

          }
        }
      }
      travelRepo.save(travel);
    }

    if (tempTravel.getId() != null && !tempTravel.getId().isEmpty())
      tempTravelRepo.delete(tempTravel.getId());
  }

  /*
   * questo metodo cerca se c'è già una corsa con quell'idRun, idLine, day
   * salvata. Se c'è incrementa il numero di persone su quella corsa di 1 e
   * /aggiorna i valori di salite/discese delle specifiche fermate sui cui il
   * passeggero è salito/sceso.
   */

  private void saveRun(PartialTravel p, String passengerId, long timestamp, String day) {
    List<BusStop> stops = new ArrayList<BusStop>();
    Bus bus = busRepo.findByBeaconId(p.getBeaconId());
    String idLine = bus.getIdLine();
    stops = getBusStops(p, idLine);
    if (stops != null) {
      p.setIdRun(stops.get(0).getIdRun());

      for (BusStop stop : stops)
        p.addBusStopId(stop.getIdStop());

      Run run = runRepo.findByIdRunAndIdLineAndDay(p.getIdRun(), idLine, day);
      if (run == null) {
        run = new Run();
        run.setDay(day);
        run.setTimestamp(timestamp);
        run.setIdRun(p.getIdRun());
        run.setIdLine(idLine);
        if (stops.get(0).getIdProg() >= 0) {
          run.setDirection(false);
          p.setDirection(false);
        } else {
          run.setDirection(true);
          p.setDirection(true);
        }
        List<BusStop> allRunStops = busStopRepo.findByIdRun(run.getIdRun(), new Sort(Direction.ASC, "idProg"));
        for (BusStop s : allRunStops) {
          StopInfo si = new StopInfo(s);

          if (s.getIdStop().equals(stops.get(0).getIdStop()))
            si.setNumPassengerGetIn(1);
          else if (s.getIdStop().equals(stops.get(stops.size() - 1).getIdStop()))
            si.setNumPassengerGetOut(1);
          run.addStopInfo(si);
        }
        try {
          runRepo.insert(run);
        } catch (MongoDataIntegrityViolationException e) {
          runRepo.updateRun(p.getIdRun(), idLine, day, stops.get(0), stops.get(stops.size() - 1));
        }
      } else {
        runRepo.updateRun(p.getIdRun(), idLine, day, stops.get(0), stops.get(stops.size() - 1));
      }
    }
  }

  // private void saveBusTravel(PartialTravel p, String passengerId, String day)
  // {
  // BusTravel b = new BusTravel();
  // b.setBeaconId(p.getBeaconId());
  // b.setPassengerId(passengerId);
  // b.setDay(day);
  // List<BusStop> stops = getBusStops(p);
  //
  //
  // if(stops==null){
  // System.out.println("NO ONE STOP");
  // return;
  // }
  // p.setIdRun(stops.get(0).getIdRun());
  // b.setStops(stops);
  // if(stops.get(0).getIdProg()>=0){
  // b.setDirection(false);//MITO
  // }else{
  // b.setDirection(true);//TOMI
  // }
  // b.setTimestamp(p.getStart());
  // busTravelRepo.save(b);
  // }

  private List<BusStop> getBusStops(PartialTravel p, String idLine) {
    List<BusStop> stops = new ArrayList<BusStop>();

    InfoPosition firstPosition = null, lastPosition = null;

    int i = 0;
    while (i < p.getAllPositions().size()) {
      firstPosition = p.getAllPositions().get(i);
      if (firstPosition.getPosition().getLat() == NOPOSITION) {
        firstPosition = null;
        i++;
      } else
        break;
    }
    i = p.getAllPositions().size() - 1;
    while (i >= 0) {
      lastPosition = p.getAllPositions().get(i);
      if (lastPosition.getPosition().getLat() == NOPOSITION) {
        lastPosition = null;
        i--;
      } else
        break;
    }
    // a questo punto fistPosition contiene l'InfoPosition relativa al primo log
    // con una posizione
    // del partial travel (o null) e lastPosition l'InfoPosition relativa
    // all'ultimo log con una posizione
    // del partial travel (o null)

    // first conterrà la lista dei BusStop candidati ad essere l'inizio del
    // viaggio in bus
    // last conterrà la lista dei BusStop candidati ad essere la fine del
    // viaggio in bus
    List<GeoResult<BusStop>> first = null, last = null;
    if (firstPosition != null && lastPosition != null && firstPosition != lastPosition
        && (firstPosition.getPosition().getLat() != lastPosition.getPosition().getLat()
            || firstPosition.getPosition().getLng() != lastPosition.getPosition().getLng())) {

      first = busStopRepo.findNear(firstPosition, idLine).getContent();
      last = busStopRepo.findNear(lastPosition, idLine).getContent();

      // con questo metodo vado a scegliere il più probabile inizio e la più
      // probabile fine del viaggio in bus
      List<BusStop> firstAndLast = getFirstLastStop(first, last, firstPosition, lastPosition);

      if (firstAndLast.size() == 2) {
        BusStop firstStop = firstAndLast.get(0);
        BusStop lastStop = firstAndLast.get(1);
        stops.add(firstStop);
        stops.addAll(busStopRepo.findBetweenFirstAndLast(firstStop, lastStop));
        stops.add(lastStop);
        return stops;
      } else
        return null;

    } else
      return null;

    // if(first!=null && first.size()>0 && last!=null && last.size()>0){
    // if(first.get(0).getContent().getIdProg()<last.get(0).getContent().getIdProg()
    // &&
    // first.get(0).getContent().getIdProg()*last.get(0).getContent().getIdProg()>0){
    // stops.add(0, first.get(0).getContent());
    // stops.addAll(busStopRepo.findBetweenFirstAndLast(first.get(0).getContent(),
    // last.get(0).getContent()));
    // stops.add(last.get(0).getContent());
    // }else if(last.size()>1 &&
    // first.get(0).getContent().getIdProg()<last.get(1).getContent().getIdProg()
    // &&
    // first.get(0).getContent().getIdProg()*last.get(1).getContent().getIdProg()>0){
    // stops.add(0, first.get(0).getContent());
    // stops.addAll(busStopRepo.findBetweenFirstAndLast(first.get(0).getContent(),
    // last.get(1).getContent()));
    // stops.add(last.get(1).getContent());
    // }else if(first.size()>1 &&
    // first.get(1).getContent().getIdProg()<last.get(0).getContent().getIdProg()
    // &&
    // first.get(1).getContent().getIdProg()*last.get(0).getContent().getIdProg()>0){
    // stops.add(0, first.get(1).getContent());
    // stops.addAll(busStopRepo.findBetweenFirstAndLast(first.get(1).getContent(),
    // last.get(0).getContent()));
    // stops.add(last.get(0).getContent());
    // }else if(first.size()>1 && last.size()>1 &&
    // first.get(1).getContent().getIdProg()<last.get(1).getContent().getIdProg()
    // &&
    // first.get(1).getContent().getIdProg()*last.get(1).getContent().getIdProg()>0){
    // stops.add(0, first.get(1).getContent());
    // stops.addAll(busStopRepo.findBetweenFirstAndLast(first.get(1).getContent(),
    // last.get(1).getContent()));
    // stops.add(last.get(1).getContent());
    // }
    //
    // return stops;
    // }
    // else
    // return null;
  }

  private List<BusStop> getFirstLastStop(List<GeoResult<BusStop>> first, List<GeoResult<BusStop>> last,
      InfoPosition firstPosition, InfoPosition lastPosition) {

    List<BusStop> listToReturn = new ArrayList<BusStop>();
    // List <BusRunDetector> elegibleCouples = new ArrayList<BusRunDetector>();
    BusRunDetector run = null;
    for (int i = 0; i < first.size(); i++) {
      for (int j = 0; j < last.size(); j++) {
        if (first.get(i).getContent().getIdRun().equals(last.get(j).getContent().getIdRun())
            && first.get(i).getContent().getIdProg() < last.get(j).getContent().getIdProg()) {

          // BusStop f, BusStop l, double distanceF, double distanceL, long
          // logTimeF, long logTimeL
          BusRunDetector possible = new BusRunDetector(first.get(i).getContent(), last.get(j).getContent(),
              first.get(i).getDistance().getValue(), last.get(j).getDistance().getValue(), firstPosition.getHour(),
              lastPosition.getHour());
          possible.setFirst(first.get(i).getContent());
          possible.setLast(last.get(j).getContent());
          possible.evaluateGoodIndex();
          if (run == null || run.getGoodIndex() > possible.getGoodIndex())
            run = possible;
          // elegibleCouples.add(possible);

        }
      }
    }

    if (run != null) {
      listToReturn.add(run.getFirst());
      listToReturn.add(run.getLast());
    }

    return listToReturn;

  }

  // private List<BusStop> bestCouplePossible(List<BusRunDetector>
  // elegibleCouples){
  // return null;
  // }
  //

  private void aggregateStep(int i, List<PartialTravel> partials) {
    PartialTravel prev = null, next = null, toAggregate = null;

    toAggregate = partials.get(i);
    if (i > 0)
      prev = partials.get(i - 1);
    if (i < partials.size() - 1)
      next = partials.get(i + 1);

    if (prev != null && next != null && prev.getMode() == next.getMode()) {
      PartialTravel newPartial = new PartialTravel();
      newPartial.setMode(prev.getMode());
      newPartial.setStart(prev.getStart());
      newPartial.setEnd(next.getEnd());
      newPartial.setBeaconId(prev.getBeaconId());
      List<InfoPosition> positions = new ArrayList<InfoPosition>();
      positions.addAll(prev.getAllPositions());
      positions.addAll(toAggregate.getAllPositions());
      positions.addAll(next.getAllPositions());
      newPartial.setAllPositions(positions);
      lengthOfPartial(newPartial);
      partials.add(i - 1, newPartial);
      partials.remove(prev);
      partials.remove(toAggregate);
      partials.remove(next);
    } else {
      if (prev != null && next != null) {
        long prevTimeDistance = toAggregate.getStart().getTime() - prev.getEnd().getTime();
        long nextTimeDistance = next.getStart().getTime() - toAggregate.getEnd().getTime();
        if (prevTimeDistance < nextTimeDistance) {
          if (prev.getBeaconId() == null || prev.getBeaconId().isEmpty()) {
            PartialTravel newPartial = new PartialTravel();
            newPartial.setMode(prev.getMode());
            newPartial.setStart(prev.getStart());
            newPartial.setEnd(toAggregate.getEnd());
            newPartial.setBeaconId(prev.getBeaconId());
            List<InfoPosition> positions = new ArrayList<InfoPosition>();
            positions.addAll(prev.getAllPositions());
            positions.addAll(toAggregate.getAllPositions());
            newPartial.setAllPositions(positions);

            lengthOfPartial(newPartial);
            partials.add(i - 1, newPartial);
            partials.remove(prev);
            partials.remove(toAggregate);
          }
        } else {
          if (next.getBeaconId() == null || next.getBeaconId().isEmpty()) {
            PartialTravel newPartial = new PartialTravel();
            newPartial.setMode(next.getMode());
            newPartial.setStart(toAggregate.getStart());
            newPartial.setEnd(next.getEnd());
            newPartial.setBeaconId(next.getBeaconId());
            List<InfoPosition> positions = new ArrayList<InfoPosition>();
            positions.addAll(toAggregate.getAllPositions());
            positions.addAll(next.getAllPositions());
            newPartial.setAllPositions(positions);
            lengthOfPartial(newPartial);
            partials.add(i, newPartial);
            partials.remove(toAggregate);
            partials.remove(next);
          }
        }
      } else if (prev != null) {
        if (prev.getBeaconId() == null || prev.getBeaconId().isEmpty()) {
          PartialTravel newPartial = new PartialTravel();
          newPartial.setMode(prev.getMode());
          newPartial.setStart(prev.getStart());
          newPartial.setEnd(toAggregate.getEnd());
          newPartial.setBeaconId(prev.getBeaconId());
          List<InfoPosition> positions = new ArrayList<InfoPosition>();
          positions.addAll(prev.getAllPositions());
          positions.addAll(toAggregate.getAllPositions());
          newPartial.setAllPositions(positions);

          lengthOfPartial(newPartial);
          partials.add(i - 1, newPartial);
          partials.remove(prev);
          partials.remove(toAggregate);
        }
      } else if (next != null) {
        if (next.getBeaconId() == null || next.getBeaconId().isEmpty()) {
          PartialTravel newPartial = new PartialTravel();
          newPartial.setMode(next.getMode());
          newPartial.setStart(toAggregate.getStart());
          newPartial.setEnd(next.getEnd());
          newPartial.setBeaconId(next.getBeaconId());
          List<InfoPosition> positions = new ArrayList<InfoPosition>();
          positions.addAll(toAggregate.getAllPositions());
          positions.addAll(next.getAllPositions());
          newPartial.setAllPositions(positions);

          lengthOfPartial(newPartial);
          partials.add(i, newPartial);
          partials.remove(toAggregate);
          partials.remove(next);
        }
      } else {
        partials.remove(i);
      }
    }
  }

  private boolean isValidStep(PartialTravel p) {
    int mode = p.getMode();
    long duration = getDuration(p);

    switch (mode) {
    case IN_VEHICLE: {
      if (duration > FIVE_MINUTES)
        return true;
      else
        return false;
    }
    case ON_BICYCLE: {
      if (duration > THREE_MINUTES)
        return true;
      else
        return false;
    }
    case ON_FOOT: {
      if (duration > ONE_MINUTE)
        return true;
      else
        return false;
    }
    case RUNNING: {
      if (duration > ONE_MINUTE)
        return true;
      else
        return false;
    }
    case WALKING: {
      if (duration > ONE_MINUTE)
        return true;
      else
        return false;
    }
    case ON_BUS: {
      if (duration > ONE_MINUTE)
        return true;
      else
        return false;
    }
    case STILL: {
      if (p.getLengthTravel() > STILL_THREESHOLD && duration > ONE_MINUTE) {
        p.setMode(UNKNOWN);
        return true;
      } else
        return false;
    }
    }
    return false;
  }

  private long getDuration(PartialTravel p) {
    if (p != null && p.getStart() != null && p.getEnd() != null) {
      return p.getEnd().getTime() - p.getStart().getTime();
    }
    return 0;
  }

  private void lengthOfPartial(PartialTravel partial) {
    // TODO add effective time per mode
    double distance = 0d;
    long effectiveTime = 0l;
    int sameDistancePoints = 0;

    int actualMode = partial.getMode();

    if (partial != null) {
      List<InfoPosition> points = partial.getAllPositions();
      if (points.size() > 1) {
        InfoPosition before = points.get(0);
        InfoPosition actual = null;
        for (int i = 1; i < points.size(); i++) {
          actual = points.get(i);
          long intervalTime = actual.getTimestamp().getTime() - before.getTimestamp().getTime();
          if (before.getMode() == actualMode)
            effectiveTime += intervalTime;
          if (before.getPosition().getLat() == actual.getPosition().getLat()
              && before.getPosition().getLng() == actual.getPosition().getLng()
              && intervalTime > ACCURACY_TIME_THREESHOLD) {
            sameDistancePoints++;
          } else {
            distance += distFrom(before.getPosition().getLat(), before.getPosition().getLng(),
                actual.getPosition().getLat(), actual.getPosition().getLng());
          }
          before = points.get(i);
        }
        partial.setLengthTravel(distance);
        if (distance > 0)
          partial.setLengthAccuracy(100 - ((sameDistancePoints * 100) / points.size()));
        else
          partial.setLengthAccuracy(0);
      }
      partial.setEffectiveTime(effectiveTime);
    }
  }

  private void lengthOfTravel(Travel travel, int missingPoints) {
    double length = 0;
    double accuracy = 0;
    int points = 0;
    long totalDuration = 0;
    long haveAccuracyDuration = 0;
    for (PartialTravel p : travel.getPartials()) {
      length += p.getLengthTravel();
      accuracy += (p.getLengthAccuracy() * p.getLengthTravel());
      points += p.getAllPositions().size();
      long actualDuration = getDuration(p);
      totalDuration += actualDuration;
      if (p.getLengthTravel() > 0)
        haveAccuracyDuration += actualDuration;

    }
    if (length != 0) {
      accuracy = (accuracy / length) * (points / (points + missingPoints)) * (haveAccuracyDuration / totalDuration);
    } else
      accuracy = 0;
    travel.setLengthTravel(length);
    travel.setLengthAccuracy(accuracy);
  }

  private double distFrom(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371000; // meters
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double sindLat = Math.sin(dLat / 2);
    double sindLng = Math.sin(dLng / 2);
    double a = Math.pow(sindLat, 2)
        + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double dist = earthRadius * c;

    return dist;
  }

  private boolean isMovement(int mode) {
    if (mode == IN_VEHICLE || mode == ON_BICYCLE || mode == ON_FOOT || mode == WALKING || mode == RUNNING)
      return true;

    return false;
  }

  // private void printMode(DetectedPosition p){
  // switch(p.getMode()){
  // case IN_VEHICLE : System.out.println("IN_VEHICLE");
  // break;
  // case ON_BICYCLE : System.out.println("ON_BICYCLE");
  // break;
  // case ON_FOOT : System.out.println("ON_FOOT");
  // break;
  // case STILL : System.out.println("STILL");
  // break;
  // case UNKNOWN : System.out.println("UNKNOWN");
  // break;
  // case TILTING : System.out.println("TILTING");
  // break;
  // case WALKING : System.out.println("WALKING");
  // break;
  // case RUNNING : System.out.println("RUNNING");
  // break;
  // case ENTER : System.out.println("ENTER");
  // break;
  // case EXIT : System.out.println("EXIT");
  // break;
  // case ONCREATE : System.out.println("ONCREATE");
  // break;
  // case ONDESTROY : System.out.println("ONDESTROY");
  // break;
  // case ON_BUS : System.out.println("ON_BUS");
  // break;
  // }
  // }

  @Override
  public DailyData getDailyData(String passengerId) {
    DailyData dd = new DailyData();
    List<DailyInfo> tomi = runRepo.getDailyInfoTomi();
    List<DailyInfo> mito = runRepo.getDailyInfoMito();

    List<DayPassengerBusRun> lTomi = travelRepo.countToMiDailyBusTravel(passengerId);
    List<DayPassengerBusRun> lMito = travelRepo.countMiToDailyBusTravel(passengerId);

    dd.setMito(mito);
    for (DayPassengerBusRun d : lTomi) {
      if (d.getCount() > 0) {
        DailyInfo di = searchDailyInfo(tomi, d.getTimestamp());
        if (di != null)
          di.setMyRoute(true);
      }
    }
    dd.setTomi(tomi);
    for (DayPassengerBusRun d : lMito) {
      if (d.getCount() > 0) {
        DailyInfo di = searchDailyInfo(mito, d.getTimestamp());
        if (di != null)
          di.setMyRoute(true);
      }
    }
    return dd;
  }

  private DailyInfo searchDailyInfo(List<DailyInfo> list, long day) {
    int max = list.size();
    if (max == 0)
      return null;
    if (max == 1) {
      DailyInfo d = list.get(0);
      if (d.getTimestamp() == day)
        return d;
      else
        return null;
    } else {
      DailyInfo d = list.get(max / 2);
      if (d.getTimestamp() - day < 0) {
        return searchDailyInfo(list.subList(max / 2, max), day);
      } else if (d.getTimestamp() - day > 0) {
        return searchDailyInfo(list.subList(0, max / 2), day);
      } else
        return d;
    }
  }

  @Override
  public List<RunDTO> getRuns() {
    List<BusStop> stops = busStopRepo.findAllSortByIdRunAndIdLineAndIdProg();
    if (stops != null) {
      List<RunDTO> runs = new ArrayList<RunDTO>();
      int n = stops.size();
      int i = 0;
      BusStop prev = null;
      BusStop actual = null;
      RunDTO run = null;
      while (i < n) {
        actual = stops.get(i);
        if (prev == null) {
          run = new RunDTO();
          run.setIdRun(actual.getIdRun());
          run.setOrigin(actual.getName());
          run.sethOrigin(formatHour(actual.getTime()));
        } else {
          if (!actual.getIdRun().equals(prev.getIdRun())) {
            run.setDestination(prev.getName());
            run.sethDestination(formatHour(prev.getTime()));
            runs.add(run);

            run = new RunDTO();
            run.setIdRun(actual.getIdRun());
            run.setOrigin(actual.getName());
            run.sethOrigin(formatHour(actual.getTime()));
          }
        }
        prev = actual;
        i++;
      }
      if (actual != null) {
        run.setDestination(actual.getName());
        run.sethDestination(formatHour(actual.getTime()));
        runs.add(run);
      }
      Collections.sort(runs);
      return runs;
    } else
      return Collections.emptyList();
  }

  private String formatHour(long time) {
    long minutes = time / 1000 / 60;
    long hour = minutes / 60;
    long minute = minutes % 60;
    if (hour < 10 && minute < 10)
      return "0" + hour + ":0" + minute;
    else if (hour < 10 && minute >= 10)
      return "0" + hour + ":" + minute;
    else if (hour >= 10 && minute < 10)
      return hour + ":0" + minute;
    else
      return "" + hour + ":" + minute;
  }

  @Override
  public List<RunDetail> getRunDetails(long timestamp, String passengerId) {
    Date d = new Date(timestamp);
    Calendar cal = Calendar.getInstance();
    cal.setTime(d);
    cal.set(Calendar.HOUR_OF_DAY, 12);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    List<Run> runs = runRepo.findByTimestamp(cal.getTime().getTime());

    if (runs != null) {
      Date start, end;
      cal.set(Calendar.HOUR_OF_DAY, 0);
      start = cal.getTime();
      cal.set(Calendar.HOUR_OF_DAY, 23);
      cal.set(Calendar.MINUTE, 59);
      cal.set(Calendar.SECOND, 59);
      cal.set(Calendar.MILLISECOND, 999);
      end = cal.getTime();

      List<Travel> myBusTravel = travelRepo.findMyBusTravelInDay(start, end, passengerId);
      List<PartialTravel> onBusPartials = getOnBusPartials(myBusTravel);
      List<RunDetail> details = new ArrayList<RunDetail>();

      for (Run r : runs) {
        RunDetail rd = new RunDetail();
        rd.setIdRun(r.getIdRun());
        rd.setPassengers(r.getTotPassenger());
        PartialTravel pt = searchPartialTravel(onBusPartials, r.getIdRun());
        List<String> myBusStops;
        if (pt != null)
          myBusStops = pt.getBusStopsId();
        else
          myBusStops = Collections.emptyList();

        List<StopInfo> stopsInfo = r.getStops();
        for (int i = 0; i < stopsInfo.size(); i++) {
          StopInfo s = stopsInfo.get(i);
          Stop stop = new Stop();
          stop.setId(s.getBusStopId());
          stop.setName(s.getBusStopName());
          if (i == 0)
            stop.setPassengers(s.getNumPassengerGetIn());
          else
            stop.setPassengers(
                rd.getStop(i - 1).getPassengers() + s.getNumPassengerGetIn() - s.getNumPassengerGetOut());

          if (myBusStops.contains(s.getBusStopId())) {
            stop.setMyRoute(true);
          }
          rd.addStop(stop);
          rd.setTimestamp(new Date(r.getTimestamp()));
        }
        details.add(rd);
      }
      return details;
    } else
      return Collections.emptyList();
  }

  private List<PartialTravel> getOnBusPartials(List<Travel> myBusTravel) {
    if (myBusTravel == null || myBusTravel.size() == 0)
      return Collections.emptyList();
    else {
      List<PartialTravel> partials = new ArrayList<PartialTravel>();
      for (Travel t : myBusTravel) {
        for (PartialTravel p : t.getPartials()) {
          if (p.getIdRun() != null && !p.getIdRun().isEmpty())
            partials.add(p);
        }
      }
      if (partials.size() > 1)
        Collections.sort(partials, new PartialTravelComparatorOnRunId());
      return partials;
    }
  }

  private PartialTravel searchPartialTravel(List<PartialTravel> list, String idRun) {
    int max = list.size();
    if (max == 0)
      return null;
    if (max == 1) {
      PartialTravel p = list.get(0);
      if (p.getIdRun().equals(idRun))
        return p;
      else
        return null;
    } else {
      PartialTravel p = list.get(max / 2);
      if (p.getIdRun().compareTo(idRun) < 0) {
        return searchPartialTravel(list.subList(max / 2, max), idRun);
      } else if (p.getIdRun().compareTo(idRun) > 0) {
        return searchPartialTravel(list.subList(0, max / 2), idRun);
      } else
        return p;
    }
  }

  @Override
  public Passenger getPassenger(String userId) throws NotFoundException {
    Passenger p = passRepo.findByUserId(userId);
    if (p == null)
      throw new NotFoundException("User not found");
    else {
      List<Travel> busTravel = travelRepo.findMyBusTravel(p.getId());
      if (busTravel != null) {
        int runs = 0, minutes = 0;

        Calendar cal = Calendar.getInstance();
        for (Travel t : busTravel) {
          for (PartialTravel pt : t.getPartials()) {
            if (pt.getBeaconId() != null && !pt.getBeaconId().isEmpty()) {
              runs++;
              int startM = 0, endM = 0;
              cal.setTime(pt.getStart());
              startM = cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR) * 60;
              cal.setTime(pt.getEnd());
              endM = cal.get(Calendar.MINUTE) + cal.get(Calendar.HOUR) * 60;
              minutes += (endM - startM);
            }
          }
        }
        p.setRuns(runs);
        p.setMinutes(minutes);
      }
      return p;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.polito.ToMi.service.AppService#getTransportTime(java.lang.String)
   */
  @Override
  public List<TransportTime> getTransportTime(String userId) throws NotFoundException {
    Passenger p = passRepo.findByUserId(userId);
    if (p == null){
      throw new NotFoundException("User not found");
    } else {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, -6);
      cal.set(Calendar.HOUR, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
      List<Travel> travels = travelRepo.findMyTravelAfterDate(p.getId(), cal.getTime());

      // Initialize array that contains 7 object (from Monday to Sunday).
      // It stores time spent on each transport (vehicle, bicycle, foot) for
      // each day.
      List<TransportTime> transportTime = new ArrayList<TransportTime>(7);
      Map<Integer, Set<Integer>> countingDaysCar = new HashMap<Integer, Set<Integer>>();
      Map<Integer, Set<Integer>> countingDaysBicycle = new HashMap<Integer, Set<Integer>>();
      Map<Integer, Set<Integer>> countingDaysFoot = new HashMap<Integer, Set<Integer>>();

      for (int i = 0; i < 7; i++) {
        transportTime.add(new TransportTime());
        countingDaysCar.put(i, new HashSet<Integer>());
        countingDaysBicycle.put(i, new HashSet<Integer>());
        countingDaysFoot.put(i, new HashSet<Integer>());

      }
      if (travels == null)
        return transportTime;

      cal.clear();
      
      for (Travel t : travels) {
        if (t.getPartials() == null)
          continue;
        for (PartialTravel pt : t.getPartials()) {
          if (pt.getEffectiveTime() == null || pt.getEffectiveTime() == 0) {
            continue;
          }
          long time = (pt.getEnd().getTime() - pt.getStart().getTime()) / 60000l;
          if (time <= 0) {
            if (time < 0) {
              System.out.println("Negative time!!!");
            }
            continue;
          }

          cal.setTime(pt.getStart());
          // Shift in order to have Monday=0 and Sunday=6
          int i = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;

          if (pt.getMode() == IN_VEHICLE || pt.getMode() == ON_BUS) {
            transportTime.get(i).addMinuteToVehicle(time);
            countingDaysCar.get(i).add(cal.get(Calendar.DAY_OF_YEAR));
            transportTime.get(i).addMinuteToEffectiveVehicle(pt.getEffectiveTime() / 60000l);
          }
          if (pt.getMode() == ON_BICYCLE) {
            transportTime.get(i).addMinuteToBicycle(time);
            countingDaysBicycle.get(i).add(cal.get(Calendar.DAY_OF_YEAR));
            transportTime.get(i).addMinuteToEffectiveBicycle(pt.getEffectiveTime() / 60000l);
          }
          if (pt.getMode() == ON_FOOT || pt.getMode() == RUNNING || pt.getMode() == WALKING) {
            transportTime.get(i).addMinuteToFoot(time);
            countingDaysFoot.get(i).add(cal.get(Calendar.DAY_OF_YEAR));
            transportTime.get(i).addMinuteToEffectiveFoot(pt.getEffectiveTime() / 60000l);
          }
        }

      }
      for (int i = 0; i < 7; i++) {
        TransportTime t = transportTime.get(i);
        int bicycleDay = countingDaysBicycle.get(i).size();
        int carDay = countingDaysCar.get(i).size();
        int footDay = countingDaysFoot.get(i).size();

        if (footDay != 0) {
          t.setOnFoot(t.getOnFoot() / footDay);
          t.setOnFootEffective(t.getOnFootEffective() / footDay);
        }
        if (carDay != 0) {
          t.setOnVehicle(t.getOnVehicle() / carDay);
          t.setOnVehicleEffective(t.getOnVehicleEffective() / carDay);
        }
        if (bicycleDay != 0) {
          t.setOnBicycle(t.getOnBicycle() / bicycleDay);
          t.setOnFootEffective(t.getOnBicycleEffective() / bicycleDay);
        }
      }
      return transportTime;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see it.polito.ToMi.service.AppService#getUserCluster(java.lang.String)
   */
  @Override
  public List<UserCluster> getUserCluster(String userId) {
    return userClusterRepo.findByUserId(userId);
  }

  @Override
  public List<UserHistory> getUserHistory(String userId) {
    List<DetectedPosition> pos = posRepo.findByUserIdWithUserInteractionTrue(userId);
    if (pos == null || pos.isEmpty()) {
      return Collections.emptyList();
    }
    List<UserHistory> uhList = new LinkedList<UserHistory>();
    for (DetectedPosition p : pos) {
      UserHistory uh = new UserHistory();
      uh.setUserMode(p.getUserMode());
      uh.setTimestamp(p.getTimestamp().getTime());
      uhList.add(uh);
    }
    return uhList;
  }

  @Override
  public List<PositionPerApp> getAllPositions() {
    List<DetectedPosition> pos = posRepo.findAllNonEmptyPosition();
    if(pos!=null){
      List<PositionPerApp> positions = new ArrayList<PositionPerApp>();
      for(DetectedPosition p : pos){
        positions.add(new PositionPerApp(p));
      }
      return positions;
    } else {
      return Collections.emptyList();
    }
  }

}
