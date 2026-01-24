package ch.martinelli.demo.aiup.task.domain;

import ch.martinelli.demo.aiup.db.tables.records.TaskRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.martinelli.demo.aiup.db.tables.Task.TASK;
import static org.jooq.Records.mapping;

@Repository
public class TaskDAO extends JooqDAO<ch.martinelli.demo.aiup.db.tables.Task, TaskRecord, Long> {

	public TaskDAO(DSLContext dslContext) {
		super(dslContext, TASK);
	}

	public List<Task> findTasksByTeamId(Long teamId, int offset, int limit, List<OrderField<?>> orderFields) {
		return dslContext
			.select(TASK.ID, TASK.TEAM_ID, TASK.TITLE, TASK.DESCRIPTION, TASK.STATUS, TASK.ASSIGNED_TO, TASK.CREATED_BY,
					TASK.CREATED_AT, TASK.UPDATED_AT)
			.from(TASK)
			.where(TASK.TEAM_ID.eq(teamId))
			.orderBy(orderFields)
			.offset(offset)
			.limit(limit)
			.fetch(mapping(Task::new));
	}

}
