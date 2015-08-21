package de.schlund.rtstat.model;

/**
 * @author Frank Spychalski (<a href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class UserDomain {

	private String user, domain;

	public String getDomain() {
		return domain;
	}

	public String getUser() {
		return user;
	}

	public UserDomain(String user, String provider) {
		this.user = user;
		this.domain = provider;
	}

	@Override
	public String toString() {
		return user + "@" + domain;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof UserDomain))
			return false;
		final UserDomain other = (UserDomain) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}