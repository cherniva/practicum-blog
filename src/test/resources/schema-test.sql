-- Create images table
CREATE TABLE IF NOT EXISTS images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image LONGBLOB NOT NULL
);

-- Create posts table
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    image_id BIGINT,
    likes INT DEFAULT 0,
    FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE SET NULL
);

-- Create tags table
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag VARCHAR(50) NOT NULL UNIQUE
);

-- Create post_tags junction table for many-to-many relationship
CREATE TABLE IF NOT EXISTS post_tags (
    post_id BIGINT,
    tag_id BIGINT,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Create comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    comment TEXT NOT NULL,
    post_id BIGINT,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Create likes table
CREATE TABLE IF NOT EXISTS likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);