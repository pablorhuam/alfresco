
public class AlfrescoUser {
	String userName, firstName, email;
	
	public boolean canBeNotified()
	{
		return !email.trim().isEmpty();
	}
}
