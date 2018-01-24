package communications;

import components.Coordinates;
import lombok.Getter;
import lombok.Setter;

public class Request {

	@Getter @Setter
	private String locationName;
	
	@Getter @Setter
	private Coordinates locationCoords;

	@Getter @Setter
	private double latitude, longitude;
	
	@Getter @Setter
	private int amount;
	
	public Request() {}
}
