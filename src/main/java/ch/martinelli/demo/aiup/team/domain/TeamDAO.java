package ch.martinelli.demo.aiup.team.domain;

import ch.martinelli.demo.aiup.db.tables.records.TeamRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.martinelli.demo.aiup.db.tables.Team.TEAM;
import static ch.martinelli.demo.aiup.db.tables.TeamMembership.TEAM_MEMBERSHIP;
import static org.jooq.Records.mapping;

@Repository
public class TeamDAO extends JooqDAO<ch.martinelli.demo.aiup.db.tables.Team, TeamRecord, Long> {

	public TeamDAO(DSLContext dslContext) {
		super(dslContext, TEAM);
	}

	public List<Team> findTeamsByUsername(String username) {
		return dslContext.select(TEAM.ID, TEAM.NAME, TEAM.DESCRIPTION, TEAM.CREATED_AT, TEAM.IS_ACTIVE)
			.from(TEAM)
			.join(TEAM_MEMBERSHIP)
			.on(TEAM.ID.eq(TEAM_MEMBERSHIP.TEAM_ID))
			.where(TEAM_MEMBERSHIP.USERNAME.eq(username))
			.orderBy(TEAM.NAME)
			.fetch(mapping(Team::new));
	}

}
