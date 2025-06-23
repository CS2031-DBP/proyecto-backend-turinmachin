CREATE TABLE IF NOT EXISTS file_info (
  id UUID NOT NULL PRIMARY KEY,
  blur_data_url TEXT,
  key VARCHAR(255) NOT NULL UNIQUE,
  media_type VARCHAR(255) NOT NULL,
  url VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS university (
  id UUID NOT NULL PRIMARY KEY,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  name VARCHAR(255) NOT NULL UNIQUE,
  short_name VARCHAR(255) UNIQUE,
  website_url VARCHAR(255),
  picture_id UUID REFERENCES file_info (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS university_email_domains (
  university_id UUID NOT NULL REFERENCES university (id) ON DELETE CASCADE,
  email_domains VARCHAR(255) UNIQUE,
  PRIMARY KEY (university_id, email_domains)
);

CREATE TABLE IF NOT EXISTS degree (
  id UUID NOT NULL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE,
  short_name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS university_degrees (
  universities_id UUID NOT NULL REFERENCES university (id) ON DELETE CASCADE,
  degrees_id UUID NOT NULL REFERENCES degree (id) ON DELETE CASCADE,
  PRIMARY KEY (universities_id, degrees_id)
);

CREATE TABLE IF NOT EXISTS users (
  id UUID NOT NULL PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  username VARCHAR(255) NOT NULL UNIQUE,
  display_name VARCHAR(255),
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL CHECK (role in ('ADMIN', 'MODERATOR', 'USER')),
  bio TEXT,
  verification_id UUID UNIQUE,
  university_id UUID REFERENCES university (id),
  degree_id UUID REFERENCES degree (id),
  profile_picture_id UUID REFERENCES file_info (id),
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS post (
  id UUID NOT NULL PRIMARY KEY,
  author_id UUID NOT NULL REFERENCES users (id),
  university_id UUID NOT NULL REFERENCES university (id),
  degree_id UUID REFERENCES degree (id),
  content TEXT NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS post_tags (
  post_id UUID NOT NULL REFERENCES post (id) ON DELETE CASCADE,
  tags VARCHAR(255),
  PRIMARY KEY (post_id, tags)
);

CREATE TABLE IF NOT EXISTS post_files (
  post_id UUID NOT NULL REFERENCES post (id) ON DELETE CASCADE,
  files_id UUID NOT NULL REFERENCES file_info (id) UNIQUE,
  files_order INTEGER NOT NULL,
  PRIMARY KEY (post_id, files_order)
);

CREATE TABLE IF NOT EXISTS post_vote (
  post_id UUID NOT NULL REFERENCES post (id) ON DELETE CASCADE,
  author_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  value SMALLINT NOT NULL CHECK (value in (-1, 1)),
  PRIMARY KEY (post_id, author_id)
);

CREATE TABLE IF NOT EXISTS comment (
  id UUID NOT NULL PRIMARY KEY,
  post_id UUID NOT NULL REFERENCES post (id) ON DELETE CASCADE,
  author_id UUID REFERENCES users (id) ON DELETE CASCADE,
  content TEXT NOT NULL,
  parent_id UUID REFERENCES comment (id) ON DELETE CASCADE,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ
);
