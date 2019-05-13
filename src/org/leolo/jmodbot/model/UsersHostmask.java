package org.leolo.jmodbot.model;

public class UsersHostmask implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4937541426506864032L;
	private Users user;
	private String network;
	private String hostmask;
	/**
	 * @return the user_id
	 */
	public Users getUser() {
		return user;
	}
	/**
	 * @param user_id the user_id to set
	 */
	public void setUser(Users user) {
		this.user = user;
	}
	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}
	/**
	 * @param network the network to set
	 */
	public void setNetwork(String network) {
		this.network = network;
	}
	/**
	 * @return the hostmask
	 */
	public String getHostmask() {
		return hostmask;
	}
	/**
	 * @param hostmask the hostmask to set
	 */
	public void setHostmask(String hostmask) {
		this.hostmask = hostmask;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostmask == null) ? 0 : hostmask.hashCode());
		result = prime * result + ((network == null) ? 0 : network.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsersHostmask other = (UsersHostmask) obj;
		if (hostmask == null) {
			if (other.hostmask != null)
				return false;
		} else if (!hostmask.equals(other.hostmask))
			return false;
		if (network == null) {
			if (other.network != null)
				return false;
		} else if (!network.equals(other.network))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
}
