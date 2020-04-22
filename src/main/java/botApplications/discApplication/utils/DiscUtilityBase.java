package botApplications.discApplication.utils;

import core.Engine;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public class DiscUtilityBase {

    Engine engine;

    public DiscUtilityBase(Engine engine) {
        this.engine = engine;
    }

    public boolean userHasGuildAdminPermission(Member member, Guild guild, TextChannel textChannel) {
        boolean hasPermission = false;
        for (int i = 0; member.getRoles().size() > i; i++) {
            for (int a = 0; member.getRoles().get(i).getPermissions().size() > a; i++) {
                if (member.getRoles().get(i).getPermissions().get(a).ADMINISTRATOR != null) {
                    hasPermission = true;
                    break;
                }
            }
        }
        if (hasPermission) {
            return true;
        } else {
            engine.getDiscEngine().getTextUtils().sendError("Du hast keine Berechtigung um diesen Command auzuführen! Dafür musst du Admin auf diesem Guild sein!", textChannel, engine.getProperties().middleTime, true);
            return false;
        }
    }
}
