package it.polito.ToMi.service;


import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.ConflictException;
import it.polito.ToMi.exception.NotFoundException;
import it.polito.ToMi.pojo.PassengerProfile;
import it.polito.ToMi.pojo.SubscribeDTO;

public interface UserService {

  public void subscribe(SubscribeDTO u) throws ConflictException, BadRequestException;
  public void updatePassenger(PassengerProfile p, String userId) throws NotFoundException, BadRequestException;
}
