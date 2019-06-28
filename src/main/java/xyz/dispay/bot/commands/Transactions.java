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
import xyz.dispay.common.entities.Transaction;

import java.util.List;

public class Transactions extends Command {

    public Transactions() {
        this.name = "transactions";
        this.description = "Returns a list of your transactions";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Account account = DisPay.getInstance().getAccountManager().getAccount(user.getIdLong());
        StringBuilder description = new StringBuilder();
        List<Transaction> transactions = account.getTransactions();
        // TYPE: AMT: TIME: DESC:
        if (!transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                description.append("[").append(transaction.getType()).append("] $")
                        .append(transaction.getAmount()).append(" @ ")
                        .append(transaction.getTimestamp()).append(" for ")
                        .append(transaction.getDescription()).append("\n");

            }
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(new EmbedBuilder()
                        .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                        .setColor(Constants.BLURPLE)
                        .setDescription(description)
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
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor(user.getAsTag(), null, user.getEffectiveAvatarUrl())
                    .setColor(Constants.BLURPLE)
                    .setDescription("You have no transactions!")
                    .build()
            ).queue();
        }
    }
}
