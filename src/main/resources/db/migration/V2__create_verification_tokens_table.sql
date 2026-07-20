CREATE TABLE verification_tokens (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                     user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                     code_hash VARCHAR(255) NOT NULL,
                                     type VARCHAR(30) NOT NULL,
                                     expires_at TIMESTAMP NOT NULL,
                                     used_at TIMESTAMP,
                                     created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_verification_tokens_user_id ON verification_tokens(user_id);