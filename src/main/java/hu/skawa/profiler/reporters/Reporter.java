package hu.skawa.profiler.reporters;

import com.google.common.base.Stopwatch;

import java.util.Map;

public interface Reporter {
	String report(Map<String, Map<String, Map<String, Stopwatch>>> timing);
}
