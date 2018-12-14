package hu.skawa.profiler.models;

public enum LifecyclePhase {
	VALIDATE(0),
	INITIALIZE(1),
	GENERATE_SOURCES(2),
	PROCESS_SOURCES(3),
	GENERATE_RESOURCES(4),
	PROCESS_RESOURCES(5),
	COMPILE(6),
	PROCESS_CLASSES(7),
	GENERATE_TEST_SOURCES(8),
	PROCESS_TEST_SOURCES(9),
	GENERATE_TEST_RESOURCES(10),
	PROCESS_TEST_RESOURCES(11),
	TEST_COMPILE(12),
	PROCESS_TEST_CLASSES(13),
	TEST(15),
	PREPARE_PACKAGE(16),
	PACKAGE(17),
	PRE_INTEGRATION_TEST(17),
	INTEGRATION_TEST(18),
	POST_INTEGRATION_TEST(19),
	VERIFY(20),
	INSTALL(21),
	DEPLOY(22);

	@Override
	public String toString() {
		return name().replaceAll("_", "-").toLowerCase();
	}

	public int getIndex() {
		return index;
	}

	public static LifecyclePhase get(String phaseName) {
		return LifecyclePhase.valueOf(phaseName.replaceAll("-", "_").toUpperCase());
	}

	public static <T> int getIndexForSorting(String phaseName) {
		return LifecyclePhase.get(phaseName).ordinal();
	}

	private int index;

	LifecyclePhase(int index) {
		this.index = index;
	}
}
