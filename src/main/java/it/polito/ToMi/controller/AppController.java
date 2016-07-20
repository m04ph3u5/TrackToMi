package it.polito.ToMi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.polito.ToMi.clustering.UserCluster;
import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.ConflictException;
import it.polito.ToMi.exception.ForbiddenException;
import it.polito.ToMi.exception.NotFoundException;
import it.polito.ToMi.pojo.Bus;
import it.polito.ToMi.pojo.BusStop;
import it.polito.ToMi.pojo.Comment;
import it.polito.ToMi.pojo.DailyData;
import it.polito.ToMi.pojo.DashboardInfo;
import it.polito.ToMi.pojo.DetectedPosition;
import it.polito.ToMi.pojo.Passenger;
import it.polito.ToMi.pojo.RunDTO;
import it.polito.ToMi.pojo.RunDetail;
import it.polito.ToMi.pojo.SubscribeDTO;
import it.polito.ToMi.pojo.TransportTime;
import it.polito.ToMi.pojo.User;
import it.polito.ToMi.repository.PassengerRepository;
import it.polito.ToMi.service.AppService;
import it.polito.ToMi.service.UserService;


@RestController
public class AppController extends BaseController{
	
	@Autowired
	private PassengerRepository passRepo;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value="/v1/dashboard", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public DashboardInfo getDashboardInfo(@AuthenticationPrincipal User u) throws NotFoundException{
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new NotFoundException("Passenger not found");
		
		DashboardInfo d = new DashboardInfo();
		d.setHours(p.getServiceTime());
		d.setUsers(passRepo.count());
		return d;
	}
		
	
	@RequestMapping(value="/v1/passenger", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void updatePassenger(@RequestBody Passenger passenger, @AuthenticationPrincipal User u) throws BadRequestException, ForbiddenException, NotFoundException {
	
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new NotFoundException("Passenger not found");
		
		p.update(passenger);
		passRepo.save(p);
	}
	
	
	@RequestMapping(value="/v1/user", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public Passenger getPassenger(@AuthenticationPrincipal User u) throws NotFoundException {
		return appService.getPassenger(u.getId());
	}
	
	@RequestMapping(value="/v1/transport", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<TransportTime> getTransportTime(@AuthenticationPrincipal User u) throws NotFoundException {
		return appService.getTransportTime(u.getId());
	}
//	
//	
//	@RequestMapping(value="/v1/user", method=RequestMethod.PUT)
//	@ResponseStatus(value = HttpStatus.OK)
//	public void updatePassenger(@RequestBody Passenger passenger, @RequestHeader(required=true, value="user") @Email String userEmail) throws ForbiddenException {
//		if(!userEmail.equals(passenger.getEmail()))
//			throw new ForbiddenException("Operation not allowed");
//		
//		passRepo.updatePassenger(passenger);
//	}
	
	
	@RequestMapping(value="/v1/position", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void savePosition(@RequestBody List<DetectedPosition> positions, @AuthenticationPrincipal User u) throws BadRequestException, ForbiddenException {
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new ForbiddenException("Operation not allowed");
		if(positions!=null && positions.size()>0)
			appService.saveDetectedPosition(positions, p);
	}
	
	
	@RequestMapping(value="/v1/myPositions", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<DetectedPosition> getMyPositions(@AuthenticationPrincipal User u, @RequestParam(value = "start", required=true) long start, @RequestParam(value = "end", required=true) long end) throws NotFoundException {
		return appService.getMyPositions(u.getId(), start, end);
	}
	
	
	@RequestMapping(value="/v1/busStop", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<BusStop> getBusStops() throws NotFoundException {
			return appService.getAllBusStop();
		
	}
	
	
	@RequestMapping(value="/v1/bus", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<Bus> getBus() throws NotFoundException {
		return appService.getAllBus();
	}
	
	
	@RequestMapping(value="/v1/comment", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void postComment(@RequestBody List<Comment> comments, @AuthenticationPrincipal User u) throws NotFoundException {
		appService.saveComments(comments, u.getId());
	}
	
	
	@RequestMapping(value="/v1/comment/{id}", method=RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void postAnswerToComment(@PathVariable String id, @RequestBody List<Comment> answers, 
			 @AuthenticationPrincipal User u) throws NotFoundException, BadRequestException {
		appService.saveAnswerToComments(id, answers, u.getId());
	}
	
	
	@RequestMapping(value="/v1/comment", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<Comment> getComment(@RequestParam(value = "last", required=false) String lastId) throws NotFoundException {
		return appService.getComments(lastId);
	}
	
	
	@RequestMapping(value="/v1/dailyData", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public DailyData getDailyData(@AuthenticationPrincipal User u) throws NotFoundException {
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new NotFoundException("User not found");
		
		return appService.getDailyData(p.getId());
		
	}
	
	
	@RequestMapping(value="/v1/runs", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<RunDTO> getRuns() throws NotFoundException {
		return appService.getRuns();
	}
	
	
	@RequestMapping(value="/v1/runDetails", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<RunDetail> getRunDetails(@RequestParam(required=true, value="day") long timestamp, @AuthenticationPrincipal User u) throws NotFoundException, BadRequestException {
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new NotFoundException("User not found");
		
		if(timestamp==0)
			throw new BadRequestException("Give valide date");
		
		return appService.getRunDetails(timestamp, p.getId());
	}
	
	@RequestMapping(value="/v1/cluster", method=RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public List<UserCluster> getCluster(@AuthenticationPrincipal User u) throws NotFoundException, BadRequestException {
		Passenger p = passRepo.findByUserId(u.getId());
		if(p==null)
			throw new NotFoundException("User not found");
		
			
		return appService.getUserCluster(p.getId());
	}
	
	@RequestMapping(value="/v1/subscribe", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void subscribe(@RequestBody SubscribeDTO subscribe) throws BadRequestException, ConflictException {
		userService.subscribe(subscribe);
	}
}
