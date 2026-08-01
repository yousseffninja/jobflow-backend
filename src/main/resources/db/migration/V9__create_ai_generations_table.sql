CREATE TABLE ai_generations (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                job_id UUID REFERENCES jobs(id) ON DELETE CASCADE,
                                type VARCHAR(30) NOT NULL,
                                input_snapshot TEXT,
                                result TEXT NOT NULL,
                                created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_ai_generations_user_id_job_id ON ai_generations(user_id, job_id);