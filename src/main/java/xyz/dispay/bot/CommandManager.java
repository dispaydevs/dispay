/*
 * DisPay: Discord Currency API
 * Copyright (C) 2019  Brett Bender & Avery Clifton
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.dispay.bot;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.dispay.bot.entities.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CommandManager {

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);

	private final List<Command> commands = new ArrayList<>();

	public CommandManager() {
		for (Class clazz : new Reflections("xyz.dispay.bot.commands").getSubTypesOf(Command.class)) {
			try {
				//noinspection unchecked
				commands.add((Command) clazz.getConstructor().newInstance());
			} catch (Exception e) {
				LOG.error("Failed to initialize " + clazz.getName(), e);
			}
		}
		commands.sort(Comparator.comparing(Command::getName));
		LOG.info("Successfully loaded {} commands", commands.size());
	}

	public Command getCommand(String name) {
		for (Command command : commands) {
			if (command.getName().equals(name)) {
				return command;
			}
		}
		return null;
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}

}
