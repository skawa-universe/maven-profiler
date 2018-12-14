package hu.skawa.profiler.models;

import java.util.List;

public class ExecutionPhase {
	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<Goal> goals;

	private String name;

	public ExecutionPhase(String lifeCycle) {
		this.name = lifeCycle;
	}
}
