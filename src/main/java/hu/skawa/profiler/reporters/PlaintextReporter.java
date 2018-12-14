package hu.skawa.profiler.reporters;

import hu.skawa.profiler.models.Goal;
import hu.skawa.profiler.models.LifecyclePhase;
import hu.skawa.profiler.models.Project;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaintextReporter implements Reporter {
	@Override
	public void report(Multimap<String, Project> timing) {
		LOGGER.info("----- PROFILING INFO -----");

		timing.forEach((projectName, project) -> {
			LOGGER.info(String.format("%1$s:", projectName));
			Map<String, List<Goal>> goals = project.getGoals().stream().collect(Collectors.groupingBy(Goal::getPhaseAsString));

			List<String> phases = Lists.newArrayList(goals.keySet());
			phases.sort(Comparator.comparingInt(LifecyclePhase::getIndexForSorting));

			phases.forEach(phase -> {
				LOGGER.info("\t" + phase + ":");
				List<Goal> goalsInPhase = goals.get(phase);
				goalsInPhase.forEach(goal -> LOGGER.info("\t\t" + goal.getName() + ": " + formatDurationForLog(goal.getStopwatch().elapsed().toMillis())));
			});
		});
	}

	private String formatDurationForLog(Long millis) {
		if (millis == null) return "null";
		long rem = millis;
		StringBuilder result = new StringBuilder();
		if (rem < 0) {
			result.append('-');
			rem = -rem;
		}
		millis = rem % 1000;
		rem /= 1000;
		long secs = rem % 60;
		rem /= 60;
		long minutes = rem % 60;
		rem /= 60;
		long hours = rem % 24;
		rem /= 24;
		boolean started = false;
		if (rem > 0) {
			result.append(rem).append(" days ");
			started = true;
		}
		if (started || hours > 0) {
			result.append(hours).append(':');
			started = true;
		}
		if (started || minutes > 0) {
			(started ? pad2(result, minutes) : result.append(minutes)).append(':');
			started = true;
		}
		(started ? pad2(result, secs) : result.append(secs)).append('.');
		pad3(result, millis);
		if (!started) result.append('s');
		return result.toString();
	}

	private StringBuilder pad2(StringBuilder target, long value) {
		if (value < 0) {
			target.append('-');
			value = Math.abs(value);
		}
		if (value < 10) target.append('0');
		return target.append(value);
	}

	private StringBuilder pad3(StringBuilder target, long value) {
		if (value < 0) {
			target.append('-');
			value = Math.abs(value);
		}
		if (value < 100) target.append('0');
		if (value < 10) target.append('0');
		return target.append(value);
	}

	private Logger LOGGER;

	public PlaintextReporter(Logger logger) {
		LOGGER = logger;
	}
}
