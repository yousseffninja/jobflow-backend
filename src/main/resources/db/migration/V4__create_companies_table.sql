CREATE TABLE companies (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           name VARCHAR(255) NOT NULL,
                           logo_url VARCHAR(500),
                           website VARCHAR(255),
                           hr_contact_name VARCHAR(255),
                           hr_contact_email VARCHAR(255),
                           created_at TIMESTAMP NOT NULL DEFAULT now(),
                           updated_at TIMESTAMP NOT NULL DEFAULT now(),
                           deleted_at TIMESTAMP
);

CREATE INDEX idx_companies_user_id ON companies(user_id);
CREATE INDEX idx_companies_user_id_deleted_at ON companies(user_id, deleted_at);