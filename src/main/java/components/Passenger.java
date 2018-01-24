package components;

import lombok.Getter;
import lombok.Setter;

public class Passenger extends User{

	@Getter @Setter
	private double credit;	//Top up account style?
	
	@Getter @Setter
	private Coordinates requestDestination;
	
}
