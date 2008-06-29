/*
 * OpenWMS, the open Warehouse Management System
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.openwms.domain.common;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.junit.Test;
import org.openwms.domain.common.helper.AbstractPDOTestCase;
import org.openwms.domain.common.system.UnitError;
import org.openwms.domain.common.values.Barcode;
import org.openwms.domain.common.values.Barcode.BARCODE_ALIGN;

public class TransportUnitTest extends AbstractPDOTestCase {

	/**
	 * Try to persist TransportUnit without TransportUnitType.
	 * 
	 */
	@Test
	public final void testTUwithoutType() {
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		try {
			em.persist(transportUnit);
			em.flush();
			fail("Persist without TransportUnitType not allowed");
		} catch (PersistenceException pe) {
			// okay
			LOG.debug("OK:Execption while persisting TransportUnit without TransportUnitType");
		}
	}

	/**
	 * Try to instanciate TransportUnit with unknown TransportUnitType.
	 * 
	 */
	@Test
	public final void testTUwithUnknownType() {
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		TransportUnitType transportUnitType = new TransportUnitType("UNKNOWN_TUT");
		transportUnit.setTransportUnitType(transportUnitType);
		try {
			em.persist(transportUnit);
			em.flush();
			fail("Persist with unknown TransportUnitType not allowed");
		} catch (PersistenceException pe) {
			// okay
			LOG.debug("OK:Exception while persisting TransportUnit with unknown TransportUnitType");
		}
	}

	/**
	 * Try to persist TransportUnit with unknown actualLocation and unknown
	 * targetLocation.
	 * 
	 */
	@Test
	public final void testTUwithUnknownLocations() {
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		TransportUnitType transportUnitType = new TransportUnitType("WELL_KNOWN_TUT");
		LocationPK unknownLocationPk = new LocationPK("UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN");
		Location actualLocation = new Location(unknownLocationPk);
		EntityTransaction entityTransaction = em.getTransaction();

		entityTransaction.begin();
		em.persist(transportUnitType);
		entityTransaction.commit();
		transportUnit.setTransportUnitType(transportUnitType);
		transportUnit.setActualLocation(actualLocation);
		try {
			entityTransaction.begin();
			em.persist(transportUnit);
			entityTransaction.commit();
			fail("Persist with unknown actualLocation && targetLocation not allowed");
		} catch (Exception pe) {
			// okay
			entityTransaction.rollback();
			LOG.debug("OK:Execption while persisting TransportUnit with unknown actualLocation and targetLocation");
		}
	}

	/**
	 * Try to persist TransportUnit with known TransportUnitType. Also with one
	 * known Location.
	 * 
	 */
	@Test
	public void testTUwithKnownLocation() {
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		TransportUnitType transportUnitType = new TransportUnitType("KNOWN_TUT");
		LocationPK knownLocationPk = new LocationPK("KNOWN", "KNOWN", "KNOWN", "KNOWN", "KNOWN");
		Location location = new Location(knownLocationPk);
		EntityTransaction entityTransaction = em.getTransaction();

		entityTransaction.begin();
		em.persist(transportUnitType);
		em.persist(location);
		entityTransaction.commit();
		transportUnit.setTransportUnitType(transportUnitType);

		transportUnit.setActualLocation(location);
		try {
			entityTransaction.begin();
			em.merge(transportUnit);
			entityTransaction.commit();
			LOG.debug("Also without targetLocation must be okay");
			//fail("Persist with unknown targetLocation not allowed");
		} catch (PersistenceException pe) {
			// okay
			entityTransaction.rollback();
			LOG.debug("OK:Execption while persisting TransportUnit with unknown targetLocation");
		}
	}

	/**
	 * Try to persist a TransportUnit with well known actualLocation as well as
	 * targetLocation and also well known TransportUnitType.
	 * 
	 */
	@Test
	public void testTUwithKnownLocations() {
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		TransportUnitType transportUnitType = new TransportUnitType("KNOWN_TUT");
		LocationPK unknownLocationPk = new LocationPK("KNOWN", "KNOWN", "KNOWN", "KNOWN", "KNOWN");
		Location actualLocation = new Location(unknownLocationPk);
		EntityTransaction entityTransaction = em.getTransaction();

		entityTransaction.begin();
		em.persist(transportUnitType);
		em.persist(actualLocation);
		entityTransaction.commit();
		transportUnit.setTransportUnitType(transportUnitType);
		transportUnit.setActualLocation(actualLocation);
		transportUnit.setTargetLocation(actualLocation);
		try {
			entityTransaction.begin();
			em.persist(transportUnit);
			entityTransaction.commit();
		} catch (Exception pe) {
			fail("Persist with well known Location and TransportUnitType fails.");
		}
	}

	@Test
	public void testTUwithErrors() {
		Barcode.setPadder('0');
		Barcode.setLength(20);
		Barcode.setAlignment(BARCODE_ALIGN.RIGHT);
		Barcode.setPadded(true);
		TransportUnit transportUnit = new TransportUnit("TEST_TU");
		TransportUnitType transportUnitType = new TransportUnitType("KNOWN_TUT");
		LocationPK unknownLocationPk = new LocationPK("KNOWN", "KNOWN", "KNOWN", "KNOWN", "KNOWN");
		Location location = new Location(unknownLocationPk);
		EntityTransaction entityTransaction = em.getTransaction();

		entityTransaction.begin();
		em.persist(transportUnitType);
		em.persist(location);
		entityTransaction.commit();
		transportUnit.setTransportUnitType(transportUnitType);
		transportUnit.setActualLocation(location);
		transportUnit.setTargetLocation(location);

		transportUnit.addError(new UnitError());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			LOG.error("Error", e);
		}
		transportUnit.addError(new UnitError());
		try {
			entityTransaction.begin();
			em.persist(transportUnit);
			entityTransaction.commit();
		} catch (Exception pe) {
			fail("Persist with well known Location and TransportUnitType fails.");
		}
		Query query = em.createQuery("select count(ue) from UnitError ue");
		Long cnt = (Long) query.getSingleResult();
		assertEquals("Expected 2 persisted UnitErrors", 2, cnt.intValue());

		entityTransaction.begin();
		em.remove(transportUnit);
		entityTransaction.commit();

		cnt = (Long) query.getSingleResult();
		assertEquals("Expected 0 persisted UnitErrors", 0, cnt.intValue());
	}
}
