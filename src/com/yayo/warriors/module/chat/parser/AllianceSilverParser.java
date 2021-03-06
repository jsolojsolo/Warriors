package com.yayo.warriors.module.chat.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yayo.common.lock.ChainLock;
import com.yayo.common.lock.LockUtils;
import com.yayo.warriors.module.alliance.entity.Alliance;
import com.yayo.warriors.module.alliance.entity.PlayerAlliance;
import com.yayo.warriors.module.alliance.manager.AllianceManager;
import com.yayo.warriors.module.chat.parser.context.AbstractGMCommandParser;
import com.yayo.warriors.module.chat.type.GmType;
import com.yayo.warriors.module.user.entity.PlayerBattle;
import com.yayo.warriors.module.user.model.UserDomain;

/**
 * 增加帮派资金
 * @author liuyuhua
 */
@Component
public class AllianceSilverParser extends AbstractGMCommandParser {

	@Autowired
	private AllianceManager allianceManager;
	
	
	public boolean execute(UserDomain userDomain, String[] elements) {
		long silver = Long.valueOf(elements[2].trim());
		PlayerBattle battle = userDomain.getBattle();
		PlayerAlliance playerAlliance = allianceManager.getPlayerAlliance(battle);
		if(playerAlliance == null || !playerAlliance.isExistAlliance()){
			return false;
		}
		Alliance alliance = allianceManager.getAlliance(playerAlliance.getAllianceId());
		if(alliance == null || alliance.isDrop()){
			return false;
		}
		
	    ChainLock lock = LockUtils.getLock(alliance);
	    try {
	    	lock.lock();
	    	alliance.increaseSilver(silver);
		}finally{
			lock.unlock();
		}
		return true;
	}

	
	protected String getCommand() {
		return GmType.ALLIANCE_SILVER;
	}

}
