package com.github.petruki.mytable.model;

import java.util.Arrays;

public enum Slots {
	
	S08_07("07:00"),
	S08_00("08:00"),
	S09_00("09:00"),
	S10_00("10:00"),
	S11_00("11:00");
	
	private String slot;
	
	private Slots(String slot) {
		this.slot = slot;
	}
	
	@Override
	public String toString() {
		return slot;
	}
	
	public static Slots find(String slot) {
		return Arrays.stream(values())
				.filter(s -> s.toString().equals(slot))
				.findFirst()
				.orElse(null);
	}

}
