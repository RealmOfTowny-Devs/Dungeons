package com.carson.dungeons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class DungeonsCommand implements CommandExecutor {

    public static boolean inDungeonCreationProcess;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("dungeons.admin")) {
                //// DUNGEONS
                // if the player types in just /dungeons
                if (args.length == 0) {
                    player.sendMessage(ChatColor.BOLD + "\nWelcome to Dungeons!");
                    player.sendMessage(ChatColor.RESET + "_________");
                    player.sendMessage(ChatColor.RESET + "/dungeons createdungeon - Starts the dungeon creation process");
                }

                //// DUNGEONS CREATEDUNGEON
                // if the player types in /dungeons createdungeon
                if (args.length >= 1) {
                    if (args[0].equalsIgnoreCase("createdungeon")) {
                        // create step variables:
                        final boolean[] stepOneComplete = {false};

                        // declaration of variables made for dungeons creation process:
                        final String[] playerResponseFromFirstPrompt = new String[1];
                        inDungeonCreationProcess = true;

                        ///// STEP 1
                        // Get the Dungeon name from the user:
                        StringPrompt getDungeonName = new StringPrompt() {
                            @NotNull
                            @Override
                            public String getPromptText(@NotNull ConversationContext context) {
                                System.out.println("Dungeons creation process has begun");
                                context.getForWhom().sendRawMessage(ChatColor.RED + "You have entered the dungeon creation mode\nDo not log out or the creation process may fail!");
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Enter a name for this dungeon:");

                                return "";
                            }

                            @Nullable
                            @Override
                            public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                                assert input != null;
                                if(input.equalsIgnoreCase("stop") || input.equalsIgnoreCase("end") || input.equalsIgnoreCase("quit")) {
                                    inDungeonCreationProcess = false;

                                } else {
                                    playerResponseFromFirstPrompt[0] = input;
                                }
                                return END_OF_CONVERSATION;
                            }
                        };

                        // start conversation prompt to grab input from user:
                        ConversationFactory conversationFactoryOne = new ConversationFactory(JavaPlugin.getPlugin(Dungeons.class));
                        conversationFactoryOne.withFirstPrompt(getDungeonName);
                        conversationFactoryOne.withLocalEcho(true);
                        Conversation convoOne = conversationFactoryOne.buildConversation(player);
                        convoOne.begin();

                        // check if the user has responded to the prompt, if responds, terminate scanner
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Conversation.ConversationState convoState = convoOne.getState();
                                System.out.println(convoState);

                                if (Objects.equals(convoState.toString(), "ABANDONED")) {
                                    stepOneComplete[0] = true;
                                    System.out.println(player +" named dungeon: " + playerResponseFromFirstPrompt[0] + " successfully!");
                                    player.sendMessage(ChatColor.GREEN + "Now set dungeon spawnpoint!");
                                    cancel();


                                }
                            }
                        }.runTaskTimer(JavaPlugin.getPlugin(Dungeons.class), 1, 1);

                        //// STEP 2
                        // Get the Dungeon Spawnpoint from the user:
                        StringPrompt getSpawnPoint = new StringPrompt() {
                            @NotNull
                            @Override
                            public String getPromptText(@NotNull ConversationContext context) {

                                System.out.println("Grabbing spawnpoint from user...");
                                context.getForWhom().sendRawMessage(ChatColor.GREEN + "Enter \"set + [SPACE] + mobname\" to set a mob spawnpoint.");
                                return "";
                            }

                            @Nullable
                            @Override
                            public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {

                                assert input != null;

                                if(input.equalsIgnoreCase("stop") || input.equalsIgnoreCase("end") || input.equalsIgnoreCase("quit")) {
                                    inDungeonCreationProcess = false;

                                } else {
                                    playerResponseFromFirstPrompt[0] = input;

                                }
                                return END_OF_CONVERSATION;
                            }
                        };

                        // start conversation prompt to grab input from user:
                        ConversationFactory conversationFactoryTwo = new ConversationFactory(JavaPlugin.getPlugin(Dungeons.class));
                        conversationFactoryTwo.withFirstPrompt(getSpawnPoint);
                        conversationFactoryTwo.withLocalEcho(true);
                        Conversation convoTwo = conversationFactoryTwo.buildConversation(player);

                        // check if the user has responded to the prompt, if responds, move to step 2
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Conversation.ConversationState convoState = convoTwo.getState();
                                if (Objects.equals(convoState.toString(), "ABANDONED")) {
                                    cancel();
                                }

                                if (stepOneComplete[0]) {
                                    convoTwo.begin();
                                    cancel();
                                }


                            }
                        }.runTaskTimer(JavaPlugin.getPlugin(Dungeons.class), 1, 1);

                    }

                }

            }
        }
        return false;
    }

}
