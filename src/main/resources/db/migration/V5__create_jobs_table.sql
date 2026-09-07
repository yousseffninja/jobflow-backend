CREATE TABLE jobs (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                      company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      source_url VARCHAR(500),
                      salary_min NUMERIC(12,2),
                      salary_max NUMERIC(12,2),
                      currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                      current_status VARCHAR(20) NOT NULL DEFAULT 'WISHLIST',
                      priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
                      applied_at TIMESTAMP,
                      created_at TIMESTAMP NOT NULL DEFAULT now(),
                      updated_at TIMESTAMP NOT NULL DEFAULT now(),
                      deleted_at TIMESTAMP
);

CREATE INDEX idx_jobs_user_id_deleted_at ON jobs(user_id, deleted_at);
CREATE INDEX idx_jobs_user_id_status ON jobs(user_id, current_status);
CREATE INDEX idx_jobs_company_id ON jobs(company_id);