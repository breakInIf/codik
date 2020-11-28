package aed.airport;




import java.util.Iterator;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.priorityqueue.*;
import es.upm.aedlib.map.*;
import es.upm.aedlib.positionlist.*;


/**
 * A registry which organizes information on airplane arrivals.
 */
public class IncomingFlightsRegistry {


	private PositionList<FlightArrival> registro;
  /**
   * Constructs an class instance.
   */
  public IncomingFlightsRegistry() {
	  registro = new NodePositionList<>();
  }

  /**
   * A flight is predicted to arrive at an arrival time (in seconds).
   */
  public void arrivesAt(String flight, long time) {
	  if(findFlight(flight)==null) {
		  registro.addLast(new FlightArrival(flight,time));
	  }
	  else {
		  findFlight(flight).element().setRight(time);
	  }
	  reorderList(registro,registro.first(),registro.first());
  }

  /**
   * A flight has been diverted, i.e., will not arrive at the airport.
   */
  public void flightDiverted(String flight) {
	  if(!(findFlight(flight)==null)) registro.remove(findFlight(flight));
  }

  /**
   * Returns the arrival time of the flight.
   * @return the arrival time for the flight, or null if the flight is not predicted
   * to arrive.
   */
  public Long arrivalTime(String flight) {
	  if(findFlight(flight)==null) return null;
	  return findFlight(flight).element().arrivalTime();
  }

  /**
   * Returns a list of "soon" arriving flights, i.e., if any 
   * is predicted to arrive at the airport within nowTime+180
   * then adds the predicted earliest arriving flight to the list to return, 
   * and removes it from the registry.
   * Moreover, also adds to the returned list, in order of arrival time, 
   * any other flights arriving withinfirstArrivalTime+120; these flights are 
   * also removed from the queue of incoming flights.
   * @return a list of soon arriving flights.
   */
  public PositionList<FlightArrival> arriving(long nowTime) {
	  PositionList<FlightArrival> list = new NodePositionList<>();
	  if(!registro.isEmpty()) {
		  if(registro.first().element().arrivalTime()<=nowTime+180) {
			  list.addFirst(registro.first().element());
			  Position<FlightArrival> pos = registro.next(registro.first());
			  Iterator<FlightArrival> it = registro.iterator();
			  boolean stop  = false;
			  it.next();
			  while(it.hasNext()&&!stop) {
				  if(it.next().arrivalTime()>list.first().element().arrivalTime()+120) stop = true;
				  else {
				  list.addLast(pos.element());
				  pos = registro.next(pos);
				  }
			  }
			  Position<FlightArrival> pos1;
			  pos = findFlight(list.last().element().flight());
			  while(pos!=null) {
				  pos1 = pos;
				  pos = registro.prev(pos);
				  registro.remove(pos1);
			  }
			  
		  }
	  }
      return list;
  }
  
  private Position<FlightArrival> findFlight(String flight){
	  Position<FlightArrival> pos = registro.first();
	  Iterator<FlightArrival> it = registro.iterator();
	  boolean found = false;
	  while(it.hasNext()&&!found) {
		  if(flight.equals(it.next().flight())) found = true; 
		  else pos = registro.next(pos);
	  }
	  return pos;
  }
  private void reorderList(PositionList<FlightArrival> list, Position<FlightArrival> cursor1, Position<FlightArrival> cursor2) {
	  while(cursor1!=null) {
		  if(cursor2.element().arrivalTime()<cursor1.element().arrivalTime()) {
			  list.addBefore(cursor1, cursor2.element());
			  list.remove(cursor2);
			  break;
			  }
		  cursor1 = list.next(cursor1);
	  }
	  while(cursor2!=null) {
		  reorderList(list,list.first(),list.next(cursor2));
	  }
  }
  
}
