package hu.skawa.profiler.reporters;

import com.google.common.base.Stopwatch;

import java.util.Map;

public class PlaintextReporter implements Reporter {
	@Override
	public String report(Map<String, Map<String, Map<String, Stopwatch>>> timing) {
		StringBuilder sb = new StringBuilder("----- PROFILING INFO -----");
		sb.append("\n");
		timing.forEach((p, phases) -> {
			sb.append(p).append(":\n");
			phases.forEach((phase, mojos) -> {
				sb.append("\t").append(phase).append(":\n");
				mojos.forEach((mojo, stopwatch) -> sb.append(String.format("\t\t%1$s: %2$s\n", mojo, formatDurationForLog(stopwatch.elapsed().toMillis()))));
			});
		});
		return sb.toString();
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


}
