package hu.skawa.profiler.models;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public class Project {
	public void addGoal(Goal goal) {
		goals.add(goal);
	}

	public Optional<Goal> getGoal(String id) {
		return goals.stream().filter(goal -> id.equalsIgnoreCase(goal.getName())).findFirst();
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private List<Goal> goals = Lists.newArrayList();

	private String name;

	public Project(String name) {
		this.name = name;
	}
}
