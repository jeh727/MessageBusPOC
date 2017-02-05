

CREATE TABLE TopicConfigs(
    id INT AUTO_INCREMENT NOT NULL,
    patternId INT,
    topicName VARCHAR(255),
    config TEXT,
    PRIMARY KEY(id)
);
