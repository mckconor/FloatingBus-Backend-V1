package components;

import lombok.Getter;
import lombok.Setter;

public class User {

	@Getter @Setter
	private String email;
	
	private enum userType {
		DRIVER,
		PASSENGER
	}
	@Getter @Setter
	private userType userRole;
	
	@Getter @Setter
	private Coordinates coords;
}
