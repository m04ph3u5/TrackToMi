package it.polito.ToMi.service;


import it.polito.ToMi.exception.BadRequestException;
import it.polito.ToMi.exception.ConflictException;
import it.polito.ToMi.pojo.SubscribeDTO;

public interface UserService {
	
	public void subscribe(SubscribeDTO u) throws ConflictException, BadRequestException;

}
