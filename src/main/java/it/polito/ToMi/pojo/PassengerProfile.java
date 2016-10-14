package it.polito.ToMi.pojo;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import it.polito.ToMi.pojo.movement.Bike;
import it.polito.ToMi.pojo.movement.CarSharing;
import it.polito.ToMi.pojo.movement.PrivateTransport;
import it.polito.ToMi.pojo.movement.PublicTransport;

@Document
public class PassengerProfile {
  
  @Id
  @Null
  private String id;
  @Null
  private String passengerId;
  
  @Min(0)
  private int yearOfBirth;
  @Valid
  private PublicTransport publicTransport;
  private Bike bike;
  @Valid
  private PrivateTransport privateTransport;
  @Valid
  private CarSharing carSharing;
  @Size(max=30)
  private String username;
  @Size(max=30)
  private String profession;
  @Size(min=1, max=1)
  private String gender;
  @Null
  private String photo;
  @Null
  private Date updatedAt;
  
  public int getYearOfBirth() {
    return yearOfBirth;
  }
  public void setYearOfBirth(int yearOfBirth) {
    this.yearOfBirth = yearOfBirth;
  }
  public PublicTransport getPublicTransport() {
    return publicTransport;
  }
  public void setPublicTransport(PublicTransport publicTransport) {
    this.publicTransport = publicTransport;
  }
  public Bike getBike() {
    return bike;
  }
  public void setBike(Bike bike) {
    this.bike = bike;
  }
  public PrivateTransport getPrivateTransport() {
    return privateTransport;
  }
  public void setPrivateTransport(PrivateTransport privateTransport) {
    this.privateTransport = privateTransport;
  }
  public CarSharing getCarSharing() {
    return carSharing;
  }
  public void setCarSharing(CarSharing carSharing) {
    this.carSharing = carSharing;
  }
  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getProfession() {
    return profession;
  }
  public void setProfession(String profession) {
    this.profession = profession;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender;
  }
  public String getPhoto() {
    return photo;
  }
  public void setPhoto(String photo) {
    this.photo = photo;
  }
  public Date getUpdatedAt() {
    return updatedAt;
  }
  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
  public String getPassengerId() {
    return passengerId;
  }
  public void setPassengerId(String passengerId) {
    this.passengerId = passengerId;
  }
  public String getId() {
    return id;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bike == null) ? 0 : bike.hashCode());
    result = prime * result + ((carSharing == null) ? 0 : carSharing.hashCode());
    result = prime * result + ((gender == null) ? 0 : gender.hashCode());
    result = prime * result + ((passengerId == null) ? 0 : passengerId.hashCode());
    result = prime * result + ((photo == null) ? 0 : photo.hashCode());
    result = prime * result + ((privateTransport == null) ? 0 : privateTransport.hashCode());
    result = prime * result + ((profession == null) ? 0 : profession.hashCode());
    result = prime * result + ((publicTransport == null) ? 0 : publicTransport.hashCode());
    result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    result = prime * result + yearOfBirth;
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PassengerProfile other = (PassengerProfile) obj;
    if (bike == null) {
      if (other.bike != null)
        return false;
    } else if (!bike.equals(other.bike))
      return false;
    if (carSharing == null) {
      if (other.carSharing != null)
        return false;
    } else if (!carSharing.equals(other.carSharing))
      return false;
    if (gender == null) {
      if (other.gender != null)
        return false;
    } else if (!gender.equals(other.gender))
      return false;
    if (passengerId == null) {
      if (other.passengerId != null)
        return false;
    } else if (!passengerId.equals(other.passengerId))
      return false;
    if (photo == null) {
      if (other.photo != null)
        return false;
    } else if (!photo.equals(other.photo))
      return false;
    if (privateTransport == null) {
      if (other.privateTransport != null)
        return false;
    } else if (!privateTransport.equals(other.privateTransport))
      return false;
    if (profession == null) {
      if (other.profession != null)
        return false;
    } else if (!profession.equals(other.profession))
      return false;
    if (publicTransport == null) {
      if (other.publicTransport != null)
        return false;
    } else if (!publicTransport.equals(other.publicTransport))
      return false;
    if (updatedAt == null) {
      if (other.updatedAt != null)
        return false;
    } else if (!updatedAt.equals(other.updatedAt))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    if (yearOfBirth != other.yearOfBirth)
      return false;
    return true;
  }
  
  

}
