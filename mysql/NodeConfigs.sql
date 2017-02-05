

CREATE TABLE NodeConfigs(
	id INT AUTO_INCREMENT NOT NULL,
	patternId INT,
	nodeType VARCHAR(255),
	enabled BIT(1),
	inputs TEXT,
	config TEXT,
	outputs TEXT,
	PRIMARY KEY(id)
);
