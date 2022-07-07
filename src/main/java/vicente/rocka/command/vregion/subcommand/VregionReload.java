package vicente.rocka.command.vregion.subcommand;

import org.bukkit.command.CommandSender;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;

import java.util.List;

public class VregionReload implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDes() {
        return "The command for reload the plugin";
    }

    @Override
    public String getSyn() {
        return "/villa reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Region.plugin.reloadConfig();
        Region.plugin.saveConfig();
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        return null;
    }
}
