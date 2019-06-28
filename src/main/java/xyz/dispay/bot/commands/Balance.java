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
import xyz.dispay.DisPay;
import xyz.dispay.bot.entities.Command;
import xyz.dispay.common.Constants;
import xyz.dispay.common.entities.Account;

public class Balance extends Command {

    public Balance() {
        this.name = "balance";
        this.description = "Checks your account's balance";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Account account = DisPay.getInstance().getAccountManager().getAccount(user.getIdLong());
        user.openPrivateChannel().queue((channel) -> {
           channel.sendMessage(new EmbedBuilder()
                    .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                    .setColor(Constants.BLURPLE)
                    .addField("Balance", String.valueOf(account.getBalance()), false)
                    .build()
           ).queue();
        });
        if (event.getChannel().getType().isGuild()) {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                    .setColor(Constants.BLURPLE)
                    .setDescription("Check your DMs")
                    .build()
            ).queue();
        }
    }

}
