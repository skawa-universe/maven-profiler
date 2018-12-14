package hu.skawa.profiler;

import hu.skawa.profiler.models.Goal;
import hu.skawa.profiler.models.Project;
import hu.skawa.profiler.reporters.PlaintextReporter;
import hu.skawa.profiler.reporters.Reporter;

import com.google.common.collect.LinkedListMultimap;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Component(role = EventSpy.class, hint = "executionProfiler")
public class ExecutionProfiler extends AbstractEventSpy {
	@Override
	public void init(Context context) throws Exception {
		LOGGER.info("Initializing profiler...");
		outputFormat = System.getProperty("maven.profiler.outputFormat", "plain");

		context.getData().forEach((s, o) -> LOGGER.info("Context key: " + s + "; type: " + o.getClass()));

		switch (outputFormat) {
			case "plain":
				REPORTER = new PlaintextReporter(LOGGER);
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
						String projectName = projectContainer.get().getName();
						Project project = new Project(projectName);
						LOGGER.info("Entering project " + projectName);
						timings.put(projectName, project);
					}
					break;
				case ProjectSucceeded:
					if (projectContainer.isPresent()) {
						String project = projectContainer.get().getName();
						LOGGER.info("Leaving successful project " + project);
					}
					break;
				case ProjectFailed:
					if (projectContainer.isPresent()) {
						String project = projectContainer.get().getName();
						LOGGER.info("Leaving failed project " + project);
					}
					break;
				case MojoStarted:
					if (mojoContainer.isPresent()) {
						String projectName = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						LOGGER.info("Starting stopwatch for mojo " + mojo + " in project " + projectName);
						Goal goal = new Goal(mojo, lifeCycle);
						timings.get(projectName).get(0).addGoal(goal);
						goal.startStopwatch();
					}
					break;
				case MojoSucceeded:
					if (mojoContainer.isPresent()) {
						String project = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						LOGGER.info("Stopping stopwatch for successful mojo " + mojo + " in project " + project);
						timings.get(project).get(0).getGoal(mojo).ifPresent(Goal::stopStopwatch);
					}
					break;
				case MojoFailed:
					if (mojoContainer.isPresent()) {
						String project = projectContainer.get().getName();
						String lifeCycle = mojoContainer.get().getLifecyclePhase();
						String mojo = mojoContainer.get().getExecutionId();
						LOGGER.info("Stopping stopwatch for failed mojo " + mojo + " in project " + project);
						timings.get(project).get(0).getGoal(mojo).ifPresent(Goal::stopStopwatch);
					}
					break;
			}
		}
	}

	@Override
	public void close() throws Exception {
		super.close();
		REPORTER.report(timings);
	}

	private static Reporter REPORTER;

	private Logger LOGGER = LoggerFactory.getLogger("profiler");

	private String outputFormat;

	private LinkedListMultimap<String, Project> timings = LinkedListMultimap.create();
}
