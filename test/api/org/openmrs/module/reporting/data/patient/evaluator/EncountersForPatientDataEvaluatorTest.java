/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.reporting.data.patient.evaluator;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.Cohort;
import org.openmrs.Encounter;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.TimeQualifier;
import org.openmrs.module.reporting.data.patient.EvaluatedPatientData;
import org.openmrs.module.reporting.data.patient.definition.EncountersForPatientDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientDataDefinition;
import org.openmrs.module.reporting.data.patient.service.PatientDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class EncountersForPatientDataEvaluatorTest extends BaseModuleContextSensitiveTest {
	
	@Before
	public void setup() throws Exception {
		executeDataSet("org/openmrs/module/reporting/include/ReportTestDataset.xml");
	}
	
	/**
	 * @see EncountersForPatientDataEvaluator#evaluate(PatientDataDefinition,EvaluationContext)
	 * @verifies return all encounters of the specified types in order for each patient
	 */
	@Test
	@SuppressWarnings({ "rawtypes" })
	public void evaluate_shouldReturnAllEncountersOfTheSpecifiedTypesInOrderForEachPatient() throws Exception {
		
		EvaluationContext context = new EvaluationContext();
		context.setBaseCohort(new Cohort("7,21"));
		
		EncountersForPatientDataDefinition d = new EncountersForPatientDataDefinition();
		d.addType(Context.getEncounterService().getEncounterType(1));

		EvaluatedPatientData pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(2, ((List)pd.getData().get(7)).size());
		Assert.assertNull(pd.getData().get(21));
		
		d.addType(Context.getEncounterService().getEncounterType(2));
		d.addType(Context.getEncounterService().getEncounterType(6));
		
		pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(3, ((List)pd.getData().get(7)).size());
		Assert.assertEquals(2, ((List)pd.getData().get(21)).size());
		
		d.setOnOrAfter(DateUtil.getDateTime(2008, 8, 15));
		d.setOnOrBefore(DateUtil.getDateTime(2009, 8, 19));
		
		pd = Context.getService(PatientDataService.class).evaluate(d, context);
		Assert.assertEquals(2, ((List)pd.getData().get(7)).size());
		Assert.assertEquals(1, ((List)pd.getData().get(21)).size());
		
		d.setWhich(TimeQualifier.LAST);
		
		pd = Context.getService(PatientDataService.class).evaluate(d, context);	
		Encounter e = (Encounter)pd.getData().get(7);
		Assert.assertEquals(5, e.getEncounterId().intValue());
		
		d.setWhich(TimeQualifier.FIRST);
		
		pd = Context.getService(PatientDataService.class).evaluate(d, context);	
		e = (Encounter)pd.getData().get(7);
		Assert.assertEquals(4, e.getEncounterId().intValue());
	}
}