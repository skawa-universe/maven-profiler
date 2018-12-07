package hu.skawa.profiler;

import hu.skawa.profiler.reporters.PlaintextReporter;
import hu.skawa.profiler.reporters.Reporter;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import javafx.scene.paint.Stop;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component(role = EventSpy.class, hint = "executionProfiler")
public class ExecutionProfiler extends AbstractEventSpy {
	@Override
	public void init(Context context) throws Exception {
		LOGGER.info("Initializing profiler...");
		outputFormat = System.getProperty("maven.profiler.outputFormat", "plain");

		switch (outputFormat) {
			case "plain":
				REPORTER = new PlaintextReporter();
				break;
			case "json":
				throw new IllegalArgumentException("not implemented yet");
			default:
				throw new IllegalArgumentException("unknown reporter format");
		}
	}

	@Override
	public void onEvent(Object event) throws Exception {
		if (event instanceof ExecutionEvent) {
			ExecutionEvent execEvent = (ExecutionEvent) event;
			Optional<MavenProject> projectContainer = Optional.ofNullable(execEvent.getProject());
			Optional<MojoExecution> mojoContainer = Optional.ofNullable(execEvent.getMojoExecution());
			switch (execEvent.getType()) {
				case ProjectStarted:
					if (projectContainer.isPresent()) {
						String project = projectContainer.get().getName();
						if (!timings.containsKey(project)) {
							LOGGER.info("Entering project " + project);
							timings.put(project, Maps.newHashMap());
						} else {
							LOGGER.info("Stopwatch already running for this project.");
						}
					}
					break;
				case ProjectSucceeded:
					if (projectContainer.isPresent()) {
						String project = projectContainer.get().getName();
						if (timings.containsKey(project)) {
							LOGGER.info("Leaving successful project " + project);
						}
					}
					break;
				case ProjectFailed:
					if (projectContainer.isPresent()) {
						String project = projectContainer.get().getName();
						if (timings.containsKey(project)) {
							LOGGER.info("Leaving failed project " + project);
						}
					}
					break;
				case MojoStarted:
					if (mojoContainer.isPresent()) {
						String project = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						if (!timings.get(project).containsKey(mojo)) {
							LOGGER.info("Starting stopwatch for mojo " + mojo + " in project " + project);
							HashMap<String, Stopwatch> mojoStopwatch = Maps.newHashMap();
							mojoStopwatch.put(mojo, Stopwatch.createStarted());
							timings.get(project).put(lifeCycle, mojoStopwatch);
						}
					}
					break;
				case MojoSucceeded:
					if (mojoContainer.isPresent()) {
						String project = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						if (timings.get(project).get(lifeCycle).containsKey(mojo)) {
							LOGGER.info("Stopping stopwatch for successful mojo " + mojo + " in project " + project);
							timings.get(project).get(lifeCycle).get(mojo).stop();
						}
					}
					break;
				case MojoFailed:
					if (mojoContainer.isPresent()) {
						String project = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						if (timings.get(project).get(lifeCycle).containsKey(mojo)) {
							LOGGER.info("Stopping stopwatch for failed mojo " + mojo + " in project " + project);
							timings.get(project).get(lifeCycle).get(mojo).stop();
						}
					}
					break;
			}
		}
	}

	@Override
	public void close() throws Exception {
		super.close();
		LOGGER.info(REPORTER.report(timings));
	}

	private static Reporter REPORTER;

	@Requirement
	private Logger LOGGER;

	private String outputFormat;

	private Map<String, Map<String, Map<String, Stopwatch>>> timings = Maps.newHashMap();
}
