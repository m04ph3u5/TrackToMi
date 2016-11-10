/**
 * 
 */
package it.polito.ToMi.pojo;

/**
 * @author m04ph3u5
 *
 */
public class DashboardInfo {

  private int minutes;
  private long users;
  private boolean lotteryClosed;

  public int getMinutes() {
    return minutes;
  }
  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }
  public long getUsers() {
    return users;
  }
  public void setUsers(long users) {
    this.users = users;
  }
  public boolean isLotteryClosed() {
    return lotteryClosed;
  }
  public void setLotteryClosed(boolean lotteryClosed) {
    this.lotteryClosed = lotteryClosed;
  }


}
