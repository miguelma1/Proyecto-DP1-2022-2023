package org.springframework.samples.petclinic.achievements;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.player.Player;
import org.springframework.samples.petclinic.player.PlayerService;
import org.springframework.samples.petclinic.progress.ProgressService;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StatisticsController {

    private final String STATISTICS_VIEW="/statistics/statistics";
    private final String RANKING_VIEW="/statistics/ranking";

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService){
        this.statisticsService = statisticsService;
    }

    @GetMapping(path="/statistics")
	public String StatisticsList(ModelMap modelMap, @AuthenticationPrincipal UserDetails user) {
		User u = userService.getUserByUsername(user.getUsername());
        modelMap.addAttribute("user", u);
		modelMap.addAttribute("statistics", statisticsService.listStatistics(u));
		return STATISTICS_VIEW;	
	}

    @GetMapping(path="/ranking")
	public String RankingList(ModelMap modelMap) {
        Map<Player, Integer> ranking = statisticsService.listRankingUserVictory();
		modelMap.addAttribute("rankingMap", ranking);
		return RANKING_VIEW;	
	}
}
