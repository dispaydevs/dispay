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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;

public class Transfer extends Command {


    public Transfer() {
        this.name = "transfer";
        this.description = "Transfer some balance to a friend!";
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        User author = event.getAuthor();
        if (args != null) {
            if (args.length == 1) {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                        .setColor(Constants.BLURPLE)
                        .setDescription("Please provide an amount to transfer!")
                        .build()
                ).queue();
            } else if (args.length == 2) {
                List<User> mentions = event.getMessage().getMentionedUsers();
                if (mentions.isEmpty()) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("You need to mention somebody!")
                            .build()
                    ).queue();
                    return;
                }
                User user = mentions.get(0);
                if (user == null || user.isBot()) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("That user is invalid!")
                            .build()
                    ).queue();
                    return;
                }

                long udays = Duration.between(user.getTimeCreated(), OffsetDateTime.now()).toDays();
                long adays = Duration.between(author.getTimeCreated(), OffsetDateTime.now()).toDays();

                if (adays < 14) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("Your account is less than 2 weeks old!")
                            .build()
                    ).queue();
                    return;
                }

                if (udays < 14) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("That person's account is less than 2 weeks old!")
                            .build()
                    ).queue();
                    return;
                }

                Account transferAccount = DisPay.getInstance().getAccountManager().getAccount(user.getIdLong());
                Account authorAccount = DisPay.getInstance().getAccountManager().getAccount(author.getIdLong());

                long amount;

                try {
                    amount = Long.valueOf(args[1]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("Invalid Command Syntax!")
                            .build()
                    ).queue();
                    return;
                }
                if (amount != 0) {
                    if (amount > authorAccount.getBalance()) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                                .setColor(Constants.BLURPLE)
                                .setDescription("That amount exceeds your balance!")
                                .build()
                        ).queue();
                        return;
                    }

                    if (amount < 0) {
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                                .setColor(Constants.BLURPLE)
                                .setDescription("The amount can not be negative!")
                                .build()
                        ).queue();
                        return;
                    }

                    String toDesc = new StringBuilder()
                            .append("Transfer to ").append(user.getIdLong())
                            .toString();
                    String fromDesc = new StringBuilder()
                            .append("Transfer from ").append(user.getIdLong())
                            .toString();

                    long timestamp = OffsetDateTime.now().toEpochSecond();
                    Transaction purchaseTrans = new Transaction(author.getIdLong(), author.getIdLong(), amount, 0L,
                            timestamp, toDesc, Transaction.TransactionType.PURCHASE);
                    Transaction depositTrans = new Transaction(author.getIdLong(), author.getIdLong(), amount,
                            author.getIdLong(), timestamp, fromDesc, Transaction.TransactionType.DEPOSIT);
                    authorAccount.getTransactions().add(purchaseTrans);
                    transferAccount.getTransactions().add(depositTrans);
                    authorAccount.setBalance(authorAccount.getBalance() - purchaseTrans.getAmount()).save();
                    transferAccount.setBalance(transferAccount.getBalance() + depositTrans.getAmount()).save();

                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("Transaction Successful!")
                            .build()
                    ).queue();


                } else {
                    event.getChannel().sendMessage(new EmbedBuilder()
                            .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                            .setColor(Constants.BLURPLE)
                            .setDescription("The amount can not be 0!")
                            .build()
                    ).queue();
                    return;
                }
            } else {
                event.getChannel().sendMessage(new EmbedBuilder()
                        .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                        .setColor(Constants.BLURPLE)
                        .setDescription("Invalid Command Syntax!")
                        .build()
                ).queue();
            }
        } else {
            event.getChannel().sendMessage(new EmbedBuilder()
                    .setAuthor(author.getAsTag(), null, author.getEffectiveAvatarUrl())
                    .setColor(Constants.BLURPLE)
                    .setDescription("Please provide both a user and amount to transfer!")
                    .build()
            ).queue();
        }
    }

}
