package be.isach.ultracosmetics.command;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.command.subcommands.*;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.util.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command manager.
 *
 * @author iSach
 * @since 12-20-2015
 */
public class CommandManager implements CommandExecutor {
    /**
     * List of the registered commands.
     */
    private final List<SubCommand> commands = new ArrayList<>();
    private final UltraCosmetics ultraCosmetics;

    public CommandManager(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
        PluginCommand cmd = ultraCosmetics.getCommand("ultracosmetics");
        cmd.setExecutor(this);
        cmd.setTabCompleter(new UCTabCompleter(ultraCosmetics));
    }

    /**
     * Registers a command.
     *
     * @param meCommand The command to register.
     */
    public void registerCommand(SubCommand meCommand) {
        commands.add(meCommand);
    }

    public void showHelp(CommandSender commandSender, int page) {
        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "UltraCosmetics Help (/uc <page>) " + ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "(" + page + "/" + getMaxPages() + ")");
        int from = 1;
        if (page > 1)
            from = 8 * (page - 1) + 1;
        int to = 8 * page;
        for (int h = from; h <= to; h++) {
            if (h > commands.size())
                break;
            SubCommand sub = commands.get(h - 1);
            commandSender.sendMessage(ChatColor.DARK_GRAY + "|  " + ChatColor.GRAY + sub.getUsage() + ChatColor.WHITE + " " + ChatColor.ITALIC + sub.getDescription());
        }
    }

    /**
     * Gets the max amount of pages.
     *
     * @return the maximum amount of pages.
     */
    private int getMaxPages() {
        int max = 8;
        // test cases:
        // 8 commands: cmds - 1 = 7, 7 / 8 = 0, 0 + 1 = 1
        // 9 commands: cmds - 1 = 8, 8 / 8 = 1, 1 + 1 = 2
        // 0 commands: cmds - 1 = -1, -1 / 8 = 0, 0 + 1 = 1
        return ((commands.size() - 1) / max) + 1;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (ultraCosmetics.getFailReason() != null) {
            sender.sendMessage(ChatColor.RED + "Plugin is currently disabled because: " + ultraCosmetics.getFailReason());
            return true;
        }

        if (args.length == 0) {
            showHelp(sender, 1);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                showHelp(sender, 1);
                return true;
            } else if (MathUtils.isInteger(args[0])) {
                showHelp(sender, Integer.parseInt(args[0]));
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("help") && MathUtils.isInteger(args[1])) {
            showHelp(sender, Integer.parseInt(args[1]));
            return true;
        }

        for (SubCommand meCommand : commands) {
            if (meCommand.is(args[0])) {

                if (!sender.hasPermission(meCommand.getPermission())) {
                    sender.sendMessage(MessageManager.getMessage("No-Permission"));
                    return true;
                }

                if (sender instanceof Player) {
                    meCommand.onExePlayer((Player) sender, args);
                } else {
                    meCommand.onExeAnyone(sender, args);
                }
                return true;
            }
        }
        showHelp(sender, 1);
        return true;
    }

    public List<SubCommand> getCommands() {
        return commands;
    }

    public void registerCommands(UltraCosmetics ultraCosmetics) {
        registerCommand(new SubCommandGadgets(ultraCosmetics));
        registerCommand(new SubCommandSelfView(ultraCosmetics));
        registerCommand(new SubCommandMenu(ultraCosmetics));
        //registerCommand(new SubCommandPurge(ultraCosmetics));
        registerCommand(new SubCommandGive(ultraCosmetics));
        registerCommand(new SubCommandToggle(ultraCosmetics));
        registerCommand(new SubCommandClear(ultraCosmetics));
        registerCommand(new SubCommandTreasure(ultraCosmetics));
        registerCommand(new SubCommandTreasureNotification(ultraCosmetics));
        registerCommand(new SubCommandMigrate(ultraCosmetics));
        registerCommand(new SubCommandReward(ultraCosmetics));
        registerCommand(new SubCommandReload(ultraCosmetics));
        registerCommand(new SubCommandUpdate(ultraCosmetics));
    }
}
