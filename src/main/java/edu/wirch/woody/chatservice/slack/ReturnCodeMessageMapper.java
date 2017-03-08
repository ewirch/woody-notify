package edu.wirch.woody.chatservice.slack;

import com.google.common.collect.ImmutableList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ReturnCodeMessageMapper {
	private final List<Mapper> mappers;

	ReturnCodeMessageMapper() {
		mappers = ImmutableList.of(new ExactMapper(404, "channel_not_found", m -> "User/Channel '" + m.getChannel() + "' not found."),
				new ExactMapper(404, "No service", m -> "Service not found. Check your webhook URL."),
				new ExactMapper(404, "No team", m -> "Team not found. Check your webhook URL."),
				new ExactMapper(404, "Bad token", m -> "Bad token. Check your webhook URL."));
	}

	public String map(final int code, final String text, final SlackMessage message) {
		return mappers.stream()
				.map(m -> m.map(code, text, message))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst()
				.orElse(new BareTextMapper().map(code, text));
	}

	@RequiredArgsConstructor
	static class ExactMapper implements Mapper {
		private final int code;
		private final String text;
		private final Function<SlackMessage, String> mapFunction;

		@Override
		public Optional<String> map(final int code, final String text, final SlackMessage message) {
			if (this.code == code && this.text.equals(text)) {
				return Optional.of(mapFunction.apply(message));
			} else {
				return Optional.empty();
			}
		}

	}

	@Slf4j
	private static class BareTextMapper {
		public String map(final int code, final String text) {
			log.error("No mapping for code {} and text '{}'.", code, text);
			return text;
		}
	}

	@FunctionalInterface
	interface Mapper {
		Optional<String> map(int code, String text, final SlackMessage message);
	}
}
