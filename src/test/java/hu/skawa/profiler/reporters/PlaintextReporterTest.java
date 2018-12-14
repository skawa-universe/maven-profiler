package hu.skawa.profiler.reporters;

import hu.skawa.profiler.models.Goal;
import hu.skawa.profiler.models.Project;

import com.google.common.collect.LinkedListMultimap;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

public class PlaintextReporterTest {

	@Test
	public void report() {
		PlaintextReporter ptr = new PlaintextReporter(testLogger);
		Goal testGoal0 = new Goal("testId0", "compile");
		Goal testGoal1 = new Goal("testId1", "compile");
		Goal testGoal2 = new Goal("testId2", "test");
		Goal testGoal3 = new Goal("testId3", "validate");

		Project testProject0 = new Project("testProject0");
		testProject0.addGoal(testGoal1);
		testProject0.addGoal(testGoal3);
		testProject0.addGoal(testGoal0);
		testProject0.addGoal(testGoal2);

		Project testProject1 = new Project("testProject1");
		testProject1.addGoal(testGoal1);
		testProject1.addGoal(testGoal3);
		testProject1.addGoal(testGoal0);
		testProject1.addGoal(testGoal2);

		Project testProject3 = new Project("testProject0");
		testProject3.addGoal(testGoal1);
		testProject3.addGoal(testGoal3);
		testProject3.addGoal(testGoal0);
		testProject3.addGoal(testGoal2);
		LinkedListMultimap<String, Project> testTimings = LinkedListMultimap.create();
		testTimings.put(testProject0.getName(), testProject0);
		testTimings.put(testProject1.getName(), testProject1);
		testTimings.put(testProject3.getName(), testProject3);

		ptr.report(testTimings);

//		testLogger.getLoggingEvents().forEach(loggingEvent -> debugLogger.info(loggingEvent.getMessage()));

		Assert.assertThat(testLogger.getLoggingEvents().get(0), CoreMatchers.is(LoggingEvent.info("----- PROFILING INFO -----")));
		Assert.assertThat(testLogger.getLoggingEvents().get(1), CoreMatchers.is(LoggingEvent.info("testProject0:")));
		Assert.assertThat(testLogger.getLoggingEvents().get(2), CoreMatchers.is(LoggingEvent.info("\tvalidate:")));
		Assert.assertThat(testLogger.getLoggingEvents().get(4), CoreMatchers.is(LoggingEvent.info("\tcompile:")));
		Assert.assertThat(testLogger.getLoggingEvents().get(5), CoreMatchers.is(LoggingEvent.info("\t\ttestId1: 0.000s")));
	}

	@Before
	public void setUp() throws Exception {
		testLogger = TestLoggerFactory.getTestLogger("profiler-test");
	}

	@After
	public void tearDown() throws Exception {
	}

	private TestLogger testLogger;
//	private Logger debugLogger = LoggerFactory.getLogger(PlaintextReporterTest.class);
}