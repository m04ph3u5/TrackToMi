package it.polito.ToMi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoDataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.ConflictException;
import it.polito.ToMi.exception.NotFoundException;
import it.polito.ToMi.pojo.Passenger;
import it.polito.ToMi.pojo.PassengerProfile;
import it.polito.ToMi.pojo.Role;
import it.polito.ToMi.pojo.SubscribeDTO;
import it.polito.ToMi.pojo.User;
import it.polito.ToMi.repository.PassengerProfileRepository;
import it.polito.ToMi.repository.PassengerRepository;
import it.polito.ToMi.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService{

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PassengerRepository passengerRepo;
  
  @Autowired
  private PassengerProfileRepository profileRepo;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User u = userRepo.findByUsername(username);
    return u;
  }


  /* (non-Javadoc)
   * @see it.polito.ToMi.service.UserService#subscribe(it.polito.ToMi.pojo.SubscribeDTO)
   */
  @Override
  public void subscribe(SubscribeDTO u) throws ConflictException, BadRequestException {
    User user = new User(u);
    List<Role> role = new ArrayList<Role>();
    Role r = new Role("ROLE_USER");
    role.add(r);
    user.setRoles(role);
    try{
      userRepo.save(user);
    } catch (MongoDataIntegrityViolationException e){
      throw new ConflictException("This username is already in use");
    }  catch (DuplicateKeyException e){
      throw new ConflictException("This username is already in use");
    } 
    Passenger p = new Passenger(user);
    p.setLastTimeUpdate(p.getRegistrationDate());
    p.setLastPosition(p.getRegistrationDate());
    passengerRepo.save(p);
  }


  @Override
  public void updatePassenger(PassengerProfile profile, String userId) throws NotFoundException, BadRequestException {
    Passenger passenger = passengerRepo.findByUserId(userId);
    if(passenger==null)
      throw new NotFoundException("Passenger not found");
        
    PassengerProfile oldProfile = passenger.getProfile();
    if(oldProfile!=null){
      oldProfile.setPassengerId(passenger.getId());
      if(!oldProfile.equals(profile)){
        profileRepo.save(oldProfile);
      }
    }
    
    profile.setUpdatedAt(new Date());
    passenger.setProfile(profile);
    passengerRepo.save(passenger);
  }

}
