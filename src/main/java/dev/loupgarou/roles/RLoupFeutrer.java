package dev.loupgarou.roles;

import java.util.Comparator;
import java.util.Random;

import org.bukkit.event.EventHandler;

import dev.loupgarou.classes.LGCustomItems;
import dev.loupgarou.classes.LGGame;
import dev.loupgarou.classes.LGPlayer;
import dev.loupgarou.events.roles.LGDiscoverRoleEvent;
import dev.loupgarou.roles.utils.Role;
import dev.loupgarou.roles.utils.RoleType;
import dev.loupgarou.roles.utils.RoleWinType;
import dev.loupgarou.utils.VariableCache.CacheType;

public class RLoupFeutrer extends Role {
    public RLoupFeutrer(LGGame game) {
        super(game);
    }
	@Override
	public int getMaxNb() {
		return Integer.MAX_VALUE;
	}
    @Override
    public String getColor() {
        return "§c";
    }
    @Override
    public String getName() {
        return "§lLoup Feutrer";
    }
    @Override
    public String getFriendlyName() {
        return "des "+getName();
    }
    @Override
    public String getShortDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous";
    }
    @Override
    public String getDescription() {
        return "Tu gagnes avec les §c§lLoups-Garous§f. Au début de la première nuit, tu peux choisir une personne, si la voyante te regarde elle verra le rôle de la personne que tu as choisis.";
    }
    @Override
    public String getTask() {
        return "Pour qui veux-tu te faire passer ?";
    }
    @Override
    public String getBroadcastedTask() {
        return "Le "+getName()+"§9 va faire semblant d'être une personne qu'il n'est pas...";
    }
    @Override
    public RoleType getType() {
        return RoleType.LOUP_GAROU;
    }
    @Override
    public RoleWinType getWinType() {
        return RoleWinType.LOUP_GAROU;
    }
    @Override
    public int getTimeout() {
        return 15;
    }
    
    @Override
    protected void onNightTurn(LGPlayer player, Runnable callback){
        if(!player.getCache().has(CacheType.LOUP_FEUTRER)){
            player.showView();
            player.sendMessage("§6Choisissez votre exemple.");
            player.choose(new LGPlayer.LGChooseCallback() {

                @Override
                public void callback(LGPlayer choosen) {
                    if(choosen != null) {
                        player.stopChoosing();
                        player.sendMessage("§6Si la voyante te sonde, elle verra que tu as celui de §7§l"+choosen.getName()+"§6.");
                        player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton exemple");
                        player.getCache().set(CacheType.LOUP_FEUTRER, choosen);
                        getPlayers().remove(player);//Pour éviter qu'il puisse avoir plusieurs modèles
                        player.hideView();
                        callback.run();
                    }
                }
            }, player);
        }
    }

    private static Random random = new Random();
    @Override
    protected void onNightTurnTimeout(LGPlayer player) {
        if(player.getCache().has(CacheType.LOUP_FEUTRER)) return;
        player.stopChoosing();
        player.hideView();
        LGPlayer choosen = null;
        while(choosen == null || choosen == player)
            choosen = getGame().getAlive().get(random.nextInt(getGame().getAlive().size()));
        player.sendMessage("§6Si la voyante te regarde, elle verra que tu as celui de "+choosen.getName()+".");
        player.sendActionBarMessage("§7§l"+choosen.getName()+"§6 est ton exemple");
        player.getCache().set(CacheType.LOUP_FEUTRER, choosen);
        getPlayers().remove(player);
    }

    @Override
    public void join(LGPlayer player, boolean sendMessage) {
        super.join(player, sendMessage);
        player.setRole(this);
        LGCustomItems.updateItem(player);
        RLoupGarou lgRole = null;
        for(Role role : getGame().getRoles())
            if(role instanceof RLoupGarou)
                lgRole = (RLoupGarou)role;

        if(lgRole == null) {
            getGame().getRoles().add(lgRole = new RLoupGarou(getGame()));

            getGame().getRoles().sort(new Comparator<Role>() {
                @Override
                public int compare(Role role1, Role role2) {
                    return role1.getTurnOrder() - role2.getTurnOrder();
                }
            });
        }

        lgRole.join(player, false);
    }
    
    @EventHandler
    public void onDiscoverRole(LGDiscoverRoleEvent e) {
    	if(!(e.getTarget().getRole() instanceof RLoupFeutrer)) return;
    	if(!e.getSource().getCache().has(CacheType.LOUP_FEUTRER)) return;
    	if(e.getSource().getCache().has(CacheType.INLOVE)) return;
    	
    	e.setDiscoveredRole(((LGPlayer) e.getSource().getCache().get(CacheType.LOUP_FEUTRER)).getRole());
    }
}