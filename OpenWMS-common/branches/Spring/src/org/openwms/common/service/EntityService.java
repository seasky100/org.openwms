/*
 * OpenWMS, the open Warehouse Management System
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.openwms.common.service;

import java.io.Serializable;
import java.util.List;

/**
 * A EntityService.
 * 
 * @author <a href="heiko.scherrer@gmx.de">Heiko Scherrer</a>
 * @version $Revision: 314 $
 */
public interface EntityService<T extends Serializable, ID extends Serializable> {
    
    List<T> findAll(Class<T> clazz);

}
