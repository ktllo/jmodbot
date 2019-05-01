package org.leolo.jmodbot.manager;

import org.hibernate.Session;

public interface DatabaseManager {

	Session getSession();

}