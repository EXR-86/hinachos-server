CREATE TABLE IF NOT EXISTS exr_hinachos_user_details (
    id VARCHAR(36) PRIMARY KEY,
    external_id VARCHAR(255),
    name VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    comments VARCHAR(255),
    email_id VARCHAR(255),
    created_date TIMESTAMP,
    modified_date TIMESTAMP,
    INDEX external_id_index (external_id),
    UNIQUE (external_id) -- Add a unique constraint on external_id
    );

CREATE TABLE IF NOT EXISTS exr_hinachos_channel_details (
    id VARCHAR(36) PRIMARY KEY,
    external_id VARCHAR(255),
    name VARCHAR(255),
    INDEX external_id_index (external_id),
    UNIQUE (external_id) -- Add a unique constraint on external_id
    );

CREATE TABLE IF NOT EXISTS exr_hinachos_hashtag_details (
    id VARCHAR(36) PRIMARY KEY,
    external_id VARCHAR(255),
    name VARCHAR(255),
    INDEX external_id_index (external_id),
    UNIQUE (external_id) -- Add a unique constraint on external_id
    );

CREATE TABLE IF NOT EXISTS exr_hinachos_custom_labels (
    id VARCHAR(36) PRIMARY KEY,
    domain_name VARCHAR(255),
    custom_labels_json JSON,
    UNIQUE (domain_name)
    );

CREATE TABLE IF NOT EXISTS exr_hinachos_rewards (
    id VARCHAR(36) PRIMARY KEY,
    sender_id VARCHAR(255),
    receiver_id VARCHAR(255),
    recognition_message VARCHAR(255),
    time_of_recognition TIMESTAMP,
    channel_id VARCHAR(255),
    hashtag_id VARCHAR(255),
    redeemed VARCHAR(1) DEFAULT 'N',
    rewards_count INT DEFAULT 0,
    domain_name VARCHAR(255),
    customer_name VARCHAR(255),
    INDEX sender_id_index (sender_id), -- Add index on sender_id column
    INDEX receiver_id_index (receiver_id), -- Add index on receiver_id column
    FOREIGN KEY (sender_id)  REFERENCES exr_hinachos_user_details(external_id),
    FOREIGN KEY (receiver_id)  REFERENCES exr_hinachos_user_details(external_id),
    FOREIGN KEY (channel_id)  REFERENCES exr_hinachos_channel_details(external_id),
    FOREIGN KEY (hashtag_id)  REFERENCES exr_hinachos_hashtag_details(external_id)
    );
