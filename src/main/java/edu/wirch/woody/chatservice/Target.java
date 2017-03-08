package edu.wirch.woody.chatservice;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Target {
	private final String target;

	public static Target ofUser(final String userName) {
		return new Target("@" + userName);
	}

	public static Target ofChannel(final String channel) {
		return new Target("#" + channel);
	}

	public static Target ofEscaped(final String escapedTarget) {
		return new Target(escapedTarget);
	}
}
