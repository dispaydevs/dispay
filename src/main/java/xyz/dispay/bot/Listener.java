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

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.dispay.DisPay;
import xyz.dispay.bot.entities.Command;
import xyz.dispay.common.Constants;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class Listener implements EventListener {

	private static final Logger LOG = LoggerFactory.getLogger(Listener.class);

	private final DisPay disPay;

	public Listener(DisPay disPay) {
		this.disPay = disPay;
	}

	@Override
	public void onEvent(@Nonnull GenericEvent event) {

		if (event instanceof MessageReceivedEvent) {
			// A message was received in a direct message or server
			MessageReceivedEvent receivedEvent = (MessageReceivedEvent) event;
			if (receivedEvent.getAuthor().isBot()) { return; }
			String content = receivedEvent.getMessage().getContentRaw();
			if (content.startsWith(Constants.PREFIX)) {
				// Split the message up into command and arguments
				String[] parts = Arrays.copyOf(content.substring(Constants.PREFIX.length()).trim().split("\\s+", 2), 2);
				String name = parts[0].toLowerCase();
				String[] args;
				if (parts[1] != null) {
					args = parts[1].split(" ");
				} else {
					args = null;
				}
				// Check if a command exists with that name or alias
				Command command = disPay.getCommandManager().getCommand(name);
				if (command == null) return;
				User author = receivedEvent.getAuthor();
				LOG.info("Command: {} from {} [{}] | {}", command.getName(), author.getAsTag(), author.getId(), content);
				// Make sure we have basic permissions
				Member self = receivedEvent.isFromGuild() ? receivedEvent.getGuild().getSelfMember() : null;
				if (self != null &&  !self.hasPermission(receivedEvent.getTextChannel(), Permission.MESSAGE_WRITE)) return;
				try {
					command.execute(receivedEvent, args);
				} catch (Exception e) {
					LOG.error("Error while processing a command", e);
				}
			}
		} else if (event instanceof ReadyEvent) {
			// We aren't sharding so we don't have to worry
			// about ready being called more than once
			disPay.getAPI().start();
		}

	}

}
