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

package xyz.dispay.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import xyz.dispay.bot.entities.Command;
import xyz.dispay.common.Constants;

public class About extends Command {

    public About() {
        this.name = "about";
        this.description = "Learn about the bot";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        User user = event.getAuthor();
        event.getChannel().sendMessage(new EmbedBuilder()
                .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                .setColor(Constants.BLURPLE)
                .setTitle("What is DisPay?")
                .setDescription("**DisPay** is a simple project originally made for " +
                        "Discord Hack Week 2019 that implements a **currency API** " +
                        "for others to use.\n\n" +
                        "A simple and easy to use **JSON Web API** provides a way for"  +
                        "many bots to share a currency to save users from having " +
                        "to worry about what bot they're earning on.\n\n" +
                        "<:dispay:596484956356870174> Click [**here**](https://dispay.xyz) to visit our website!\n" +
                        "<:github:596150583623417856> Click [**here**](https://github.com/dispaydevs/dispay) to view the source!")
                .setThumbnail("https://github.com/dispaydevs/dispay/raw/master/assets/img/logo.png")
                .setFooter("Created by apollo#9292 and avery#1235")
                .build()
        ).queue();
    }

}
