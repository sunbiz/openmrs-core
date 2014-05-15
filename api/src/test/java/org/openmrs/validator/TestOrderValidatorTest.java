package org.openmrs.validator;

import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.TestOrder;
import org.openmrs.api.ConceptService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

/**
 *
 */
public class TestOrderValidatorTest extends BaseContextSensitiveTest {
	
	/**
	 * @verifies fail validation if the specimen source is invalid
	 * @see TestOrderValidator#validate(Object, org.springframework.validation.Errors)
	 */
	@Test
	public void validate_shouldFailValidationIfTheSpecimenSourceIsInvalid() throws Exception {
		ConceptService conceptService = Context.getConceptService();
		Concept specimenSource = conceptService.getConcept(3);
		assertThat(specimenSource, not(isIn(Context.getOrderService().getDrugRoutes())));
		TestOrder order = new TestOrder();
		Patient patient = new Patient(8);
		order.setPatient(patient);
		OrderService orderService = Context.getOrderService();
		order.setOrderType(orderService.getOrderTypeByName("Test order"));
		order.setConcept(conceptService.getConcept(5497));
		order.setOrderer(new Provider());
		order.setCareSetting(new CareSetting());
		Encounter encounter = new Encounter();
		encounter.setPatient(patient);
		order.setEncounter(encounter);
		order.setStartDate(new Date());
		order.setSpecimenSource(specimenSource);
		
		Errors errors = new BindException(order, "order");
		new TestOrderValidator().validate(order, errors);
		Assert.assertTrue(errors.hasFieldErrors("specimenSource"));
		Assert.assertEquals("TestOrder.error.notAmongAllowedConcepts", errors.getFieldError("specimenSource").getCode());
	}
}
