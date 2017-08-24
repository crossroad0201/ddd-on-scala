CREATE TABLE tasks (
  task_id VARCHAR(50) NOT NULL,
  name VARCHAR(100) NOT NULL,
  state VARCHAR(20) NOT NULL,
  author_id VARCHAR(50) NOT NULL,
  assignee_id VARCHAR(50),
  PRIMARY KEY (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE task_comments (
  id INT NOT NULL AUTO_INCREMENT,
  task_id VARCHAR(50) NOT NULL,
  message VARCHAR(200) NOT NULL,
  commenter_id VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  KEY entity_id (task_id),
  CONSTRAINT entity_id FOREIGN KEY (task_id) REFERENCES tasks (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
