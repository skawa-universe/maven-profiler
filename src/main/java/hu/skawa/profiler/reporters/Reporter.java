package hu.skawa.profiler.reporters;

import hu.skawa.profiler.models.Project;

import com.google.common.collect.Multimap;

public interface Reporter {
	void report(Multimap<String, Project> timings);
}
