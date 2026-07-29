CREATE TABLE interviews (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
                            user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                            type VARCHAR(20) NOT NULL,
                            scheduled_at TIMESTAMP NOT NULL,
                            duration_minutes INT,
                            location VARCHAR(500),
                            interviewer_name VARCHAR(255),
                            feedback TEXT,
                            outcome VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                            reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP NOT NULL DEFAULT now(),
                            updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_interviews_user_id_scheduled_at ON interviews(user_id, scheduled_at);
CREATE INDEX idx_interviews_job_id ON interviews(job_id);