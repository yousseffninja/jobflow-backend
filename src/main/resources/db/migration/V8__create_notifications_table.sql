CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE ,
    type VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    related_entity_type VARCHAR(255),
    related_entity_id UUID,
    read_at timestamp,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

create index idx_notifications_user_id_created_at on notifications(user_id, created_at);
create index idx_notifications_user_id_read_at on notifications(user_id, read_at);