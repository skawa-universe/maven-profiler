package hu.skawa.profiler.models;

import com.google.common.base.Stopwatch;

public class Goal {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LifecyclePhase getPhase() {
		return phase;
	}

	public String getPhaseAsString() {
		return phase.toString();
	}

	public void setPhase(LifecyclePhase phase) {
		this.phase = phase;
	}

	public Stopwatch getStopwatch() {
		return stopwatch;
	}

	public void startStopwatch() {
		stopwatch.start();
	}

	public void stopStopwatch() {
		stopwatch.stop();
	}

	private String name;

	private LifecyclePhase phase;

	private Stopwatch stopwatch = Stopwatch.createUnstarted();

	public Goal(String executionId) {
		this.name = executionId;
	}

	public Goal(String executionId, String phaseName) {
		this.name = executionId;
		this.phase = LifecyclePhase.get(phaseName);
	}
}
