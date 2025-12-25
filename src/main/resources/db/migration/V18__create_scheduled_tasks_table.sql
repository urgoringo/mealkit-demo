-- db-scheduler table
-- Based on db-scheduler's schema requirements
CREATE TABLE scheduled_tasks (
    task_name VARCHAR(255) NOT NULL,
    task_instance VARCHAR(255) NOT NULL,
    task_data BYTEA,
    execution_time timestamptz NOT NULL,
    picked BOOLEAN NOT NULL,
    picked_by VARCHAR(50),
    last_success timestamptz,
    last_failure timestamptz,
    consecutive_failures INT,
    last_heartbeat timestamptz,
    version BIGINT NOT NULL,
    PRIMARY KEY (task_name, task_instance)
);

CREATE INDEX idx_scheduled_tasks_execution_time ON scheduled_tasks(execution_time);
CREATE INDEX idx_scheduled_tasks_last_heartbeat ON scheduled_tasks(last_heartbeat);
