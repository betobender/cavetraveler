package com.cave.traveler.engine.setters;


public class SetterFactory {
	private static SetterFactory instance = new SetterFactory();

	private SetterFactory() {

	}

	public static SetterFactory getInstance() {
		return instance;
	}

	public Setter createFromType(String type) {
		Setter setter = null;
		if (type.equals("speedSetter"))
			setter = new SpeedSetter();
		else if (type.equals("dynamicSetter"))
			setter = new DynamicSetter();		
		else if (type.equals("playableSetter"))
			setter = new PlayableSetter();		
		else if (type.equals("acceleratedSetter"))
			setter = new AcceleratedSetter();
		else if (type.equals("accelerationSetter"))
			setter = new AccelerationSetter();		
		setter.setType(type);
		return setter;
	}
}
