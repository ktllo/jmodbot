package org.leolo.jmodbot.model;

import javax.persistence.*;

@Entity
@Table(name="data_pair")
public class DataPair {
	@Id
	@Column(name="key_name")
	private String keyName;
	
	@Column(name="key_value")
	private String keyValue;
	
	/**
	 * @return the keyName
	 */
	public String getKeyName() {
		return keyName;
	}
	/**
	 * @param keyName the keyName to set
	 */
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	/**
	 * @return the keyValue
	 */
	public String getKeyValue() {
		return keyValue;
	}
	/**
	 * @param keyValue the keyValue to set
	 */
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataPair [keyName=" + keyName + ", keyValue=" + keyValue + "]";
	}
}
