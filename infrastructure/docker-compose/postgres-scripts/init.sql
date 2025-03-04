

-- transfer_operations 테이블
CREATE TABLE transfer_operations (
    id BIGSERIAL PRIMARY KEY,
    learning_material_id VARCHAR(50) NOT NULL,
    data_migration_info_id VARCHAR(50) NOT NULL,
    trans_type VARCHAR(20) NOT NULL,
    transfer_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    create_admin_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- transfer_chunks 테이블
CREATE TABLE transfer_chunks (
    id BIGSERIAL PRIMARY KEY,
    operation_id BIGINT NOT NULL,
    chunk_offset BIGINT NOT NULL,
    size INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (operation_id) REFERENCES transfer_operations(id) ON DELETE CASCADE
);

-- transfer_log 테이블
CREATE TABLE transfer_log (
    id BIGSERIAL PRIMARY KEY,
    transfer_operation_id BIGINT NOT NULL,
    learning_material_id VARCHAR(50) NOT NULL,
    data_migration_info_id VARCHAR(50) NOT NULL,
    trans_type VARCHAR(20) NOT NULL,
    transfer_status VARCHAR(20) NOT NULL,
    create_admin_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (transfer_operation_id) REFERENCES transfer_operations(id)
);

